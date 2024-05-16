package com.mysite.sbb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.dto.BookDTO;
import com.mysite.sbb.dto.BookResultDTO;
import com.mysite.sbb.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BookController {
    @GetMapping("/book")
    public String books() {
        return "book";
    }

    @PostMapping("/book")
    public String book(@RequestParam("text") String text, Model model) {

        BookService bs = new BookService();
        BookResultDTO resultDTO = null;
        resultDTO = bs.bookservice(text);
        List<BookDTO> books =resultDTO.getItems();	// books를 list.html에 출력 -> model 선언
        model.addAttribute("books", books);
        return "book";
    }
}
