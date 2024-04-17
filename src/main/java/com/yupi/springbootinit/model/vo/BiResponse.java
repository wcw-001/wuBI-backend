package com.yupi.springbootinit.model.vo;

import lombok.Data;

/**
 * Bi的返回结果
 */
@Data
public class BiResponse {
    String genChart;
    String genResult;
    Long chartId;
}
