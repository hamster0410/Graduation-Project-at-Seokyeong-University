package com.mysite.sbb.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.AddressConfigure;
import com.mysite.sbb.JschImplement;
import com.mysite.sbb.entity.Question;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.service.R_containerService;
import com.mysite.sbb.service.ShinyService;
import com.mysite.sbb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.jta.UserTransactionAdapter;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


@RequestMapping("/mypage")
@Controller
@RequiredArgsConstructor
public class MypageController {
    private final ShinyService shinyService;
    private final UserService userService;
    private final AddressConfigure addressConfigure;
    private final R_containerService rContainerService;

    @GetMapping("/info")
    public String mypage(@AuthenticationPrincipal UserDetails principaldetail, Model model) {
        String username = principaldetail.getUsername();
        SiteUser siteUser = userService.getUser(username);
        model.addAttribute("userid", username);
        model.addAttribute("hasContainer",siteUser.isHasContainer());
        return "myinfo";
    }

    @GetMapping("/delete_user")
    public String delete_user(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal UserDetails principaldetail) {
        String username = principaldetail.getUsername();
        SiteUser siteUser = this.userService.getUser(username);
        if (siteUser.isHasContainer()) {
            this.rContainerService.delete_Container(siteUser);
        }
        this.userService.delete(siteUser);
        List<String> commandList = new ArrayList<>();

        //샤이니서버 계정 삭제
        commandList.add("echo \"123456\" | sudo -S umount /home/"+username+"/R/x86_64-pc-linux-gnu-library/4.3");
        commandList.add("echo \"123456\" | sudo -S rm -rf  /home/"+username);
        commandList.add("echo \"123456\" | sudo -S rm -rf  /srv/shiny-server/sample-apps/"+username);
        commandList.add("echo \"123456\" | sudo -S userdel -rf "+username);


        //ShinyServer 디렉토리 생성 코드
        for (String s : commandList) {
            JschImplement js = new JschImplement(addressConfigure);
            js.JschExecMethod(s);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

    @GetMapping("/studio_shiny")
    public String appupload(@AuthenticationPrincipal UserDetails principaldetail, Model model) {

        String username = principaldetail.getUsername();
        JschImplement js = new JschImplement(addressConfigure);
        Vector<String> directory_list = js.JSchSftpMethod("/home/" + username);
        System.out.println("확인할 디렉토리 리스트 /home/"+username);
        System.out.println(directory_list);
        //샤이니 폴더가 있는지 확인
        boolean hasShinyDirectory = false;

        for (String directoryName : directory_list) {
            if (directoryName.equals("shiny")) {
                hasShinyDirectory = true;
                break;
            }
        }
        if (!hasShinyDirectory) {
            model.addAttribute("hasShinyDirectory",hasShinyDirectory);
            return "index";
        }

        directory_list = js.JSchSftpMethod("/home/" + username + "/shiny");
        Vector<String> shiny_list = js.JSchSftpMethod("/srv/shiny-server/sample-apps/" + username);

        model.addAttribute("studio_list", directory_list);
        model.addAttribute("shiny_list", shiny_list);

        return "mystudio_shiny";
    }

    @PostMapping("/studio_shiny/upload")
    @ResponseBody
    public Map<String, Vector<String>> upload_file (@AuthenticationPrincipal UserDetails principaldetail, @RequestBody String item) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(item);
        item = jsonNode.get("item").asText();
        
        JschImplement js = new JschImplement(addressConfigure);
        List<String> commandList = new ArrayList<>();


        //RSTUDIO 계정생성 코드
        commandList.add("echo \"123456\" | sudo -S mkdir /srv/shiny-server/sample-apps/"+principaldetail.getUsername()+"/" + item);
        commandList.add("echo \"123456\" | sudo -S mv /home/"+principaldetail.getUsername()+"/shiny/" + item + " /srv/shiny-server/sample-apps/"+principaldetail.getUsername()+"/"+item+"/app.R");

        //ShinyServer 디렉토리 생성 코드
        for (String s : commandList) {
            js.JschExecMethod(s);
        }
        Vector<String> directory_list = js.JSchSftpMethod("/home/" + principaldetail.getUsername());
        directory_list = js.JSchSftpMethod("/home/"+principaldetail.getUsername()+"/shiny");
        Vector<String> shiny_list = js.JSchSftpMethod("/srv/shiny-server/sample-apps/"+principaldetail.getUsername());

        Map<String, Vector<String>> result = new HashMap<>();
        result.put("directory_list", directory_list);
        result.put("shiny_list", shiny_list);

        return ResponseEntity.ok(result).getBody();
    }

    @PostMapping("/studio_shiny/revoke")
    @ResponseBody
    public Map<String, Vector<String>> revoke_file (@AuthenticationPrincipal UserDetails principaldetail, @RequestBody String item) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(item);
        item = jsonNode.get("item").asText();
        System.out.println("revoke "+item);
        JschImplement js = new JschImplement(addressConfigure);
        List<String> commandList = new ArrayList<>();


        //파일 회수 코드
        commandList.add("echo \"123456\" | sudo -S mv /srv/shiny-server/sample-apps/"+principaldetail.getUsername()+"/" + item + "/app.R" +
                " /home/"+principaldetail.getUsername() +"/shiny/" + item);
        commandList.add("echo \"123456\" | sudo -S rm -rf /srv/shiny-server/sample-apps/"+principaldetail.getUsername()+"/" + item + "/");

        //ShinyServer 디렉토리 생성 코드
        for (String s : commandList) {
            js.JschExecMethod(s);
        }
        Vector<String> directory_list = js.JSchSftpMethod("/home/" + principaldetail.getUsername());
        directory_list = js.JSchSftpMethod("/home/"+principaldetail.getUsername()+"/shiny");
        Vector<String> shiny_list = js.JSchSftpMethod("/srv/shiny-server/sample-apps/"+principaldetail.getUsername());

        Map<String, Vector<String>> result = new HashMap<>();
        result.put("directory_list", directory_list);
        result.put("shiny_list", shiny_list);

        return ResponseEntity.ok(result).getBody();
    }

    @PostMapping("/studio_shiny/delete")
    @ResponseBody
    public Map<String, Vector<String>> delete_file (@AuthenticationPrincipal UserDetails principaldetail, @RequestBody String item) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(item);
        item = jsonNode.get("item").asText();
        System.out.println("delete "+item);
        JschImplement js = new JschImplement(addressConfigure);
        List<String> commandList = new ArrayList<>();


        //파일 삭제 코드
        commandList.add("echo \"123456\" | sudo -S rm -rf /srv/shiny-server/sample-apps/"+principaldetail.getUsername()+"/" + item + "/");

        for (String s : commandList) {
            js.JschExecMethod(s);
        }
        Vector<String> directory_list = js.JSchSftpMethod("/home/" + principaldetail.getUsername());
        directory_list = js.JSchSftpMethod("/home/"+principaldetail.getUsername()+"/shiny");
        Vector<String> shiny_list = js.JSchSftpMethod("/srv/shiny-server/sample-apps/"+principaldetail.getUsername());

        Map<String, Vector<String>> result = new HashMap<>();
        result.put("directory_list", directory_list);
        result.put("shiny_list", shiny_list);

        return ResponseEntity.ok(result).getBody();
    }

}
