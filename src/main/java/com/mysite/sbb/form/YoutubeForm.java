package com.mysite.sbb.form;

import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YoutubeForm {
    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "유튜브 id는 필수항목입니다")
    private String youtube_id;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;
}
