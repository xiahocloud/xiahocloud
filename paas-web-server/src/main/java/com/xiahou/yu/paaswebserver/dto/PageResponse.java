package com.xiahou.yu.paaswebserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int pageNum;   // 从 1 开始
    private int pageSize;  // 每页大小
    private long total;    // 总记录数
    private List<T> items; // 当前页数据
}