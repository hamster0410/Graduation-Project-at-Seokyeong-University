package com.mysite.sbb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.entity.Question;
import com.mysite.sbb.entity.R_Container;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.service.R_containerService;
import com.mysite.sbb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/rstudio")
@RequiredArgsConstructor
public class R_ContainerController {
    private final UserService userService;
    private final R_containerService rContainerService;

    @GetMapping("connect")
    public String connect(@AuthenticationPrincipal UserDetails principaldetail) {
        String username = principaldetail.getUsername();
        SiteUser siteUser = this.userService.getUser(username);
        int portNumber = rContainerService.getRcontainer(siteUser).getPortNumber();
        return "redirect:http://172.30.1.16:"+String.valueOf(portNumber);
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal UserDetails principaldetail,Model model,@RequestBody String password ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(password);
        password = jsonNode.get("password").asText();

        String username = principaldetail.getUsername();
        SiteUser siteUser = this.userService.getUser(username);

        boolean[] container_list = new boolean[100];
        boolean container_no_slot = true;
        Arrays.fill(container_list, false);
        container_list = rContainerService.using_container(container_list);
        for (int i = 0; i < container_list.length; i++) {
            if (!container_list[i]) {
                rContainerService.create_Container(siteUser, i,password);
                container_no_slot = false;
                break;
            }
        }
        if (container_no_slot) {
            return "container_all_using";
        }
        return "index";

    }
    @GetMapping("/delete")
    public String delete(@AuthenticationPrincipal UserDetails principaldetail ) {
        SiteUser siteUser = this.userService.getUser(principaldetail.getUsername());
        rContainerService.delete_Container(siteUser);
        return "index";
    }


}

