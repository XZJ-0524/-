package com.hmdp.dto;

import lombok.Data;

import java.util.List;

//redis分页查询的返回结果
@Data
public class ScrollResult {
    private List<?> list;
    private Long minTime;
    private Integer offset;
}
