package com.zephyr.springboottemplate.common;

import com.zephyr.springboottemplate.constant.SortConstant;
import lombok.Data;

@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int currentNum = 1;

    /**
     * 每页显示记录数
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = SortConstant.SORT_ORDER_ASC;
}
