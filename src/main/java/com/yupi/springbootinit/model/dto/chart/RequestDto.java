package com.yupi.springbootinit.model.dto.chart;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RequestDto {
    @NotBlank(message="消息不能为空")
    private String message;
}
