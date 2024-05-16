package com.mysite.sbb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.entity.ShinyApp;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.service.ShinyService;
import com.mysite.sbb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/shiny")
public class ShinyController {
    private final ShinyService shinyService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        log.info("page:{}, kw:{}", page, kw);
        Page<ShinyApp> paging = this.shinyService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "shiny_list";
    }


    @PostMapping("/upload")
    @ResponseBody
    public void shinyappCreate(@AuthenticationPrincipal UserDetails principaldetail, @RequestBody Map<String, Object> data) {
        String selectedItem = (String) data.get("selectedItem");
        String description = (String) data.get("description");

        SiteUser siteUser = this.userService.getUser(principaldetail.getUsername());

        this.shinyService.create(selectedItem, description, siteUser);
    }

    @PostMapping("/revoke")
    @ResponseBody
    public void shinyappDelete(@AuthenticationPrincipal UserDetails principaldetail, @RequestBody String item ) throws JsonProcessingException {
        System.out.println(item);
        String[] parts = item.split("=");
        item = parts[1];
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode jsonNode = mapper.readTree(item);
//        item = jsonNode.get("item").asText();
        System.out.println(item);
        ShinyApp shinyApp = this.shinyService.getShinyApp(item,principaldetail.getUsername());

        this.shinyService.delete(shinyApp);
    }



//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/vote/{id}")
//    public String shinyVote(Principal principal, @PathVariable("id") Integer id) {
//        ShinyApp shinyApp = this.shinyService.getShinyApp(id);
//        SiteUser siteUser = this.userService.getUser(principal.getName());
//        this.shinyService.vote(shinyApp, siteUser);
//        return "redirect:/shiny/list";
//    }
}



