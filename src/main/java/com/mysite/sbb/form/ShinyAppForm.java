package com.mysite.sbb.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShinyAppForm {
    @Size(max = 30)
    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;
}