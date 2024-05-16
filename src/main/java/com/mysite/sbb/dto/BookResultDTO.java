package com.mysite.sbb.dto;

import lombok.*;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BookResultDTO {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<BookDTO> items;
}
