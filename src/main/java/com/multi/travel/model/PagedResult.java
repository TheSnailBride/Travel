package com.multi.travel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class PagedResult<T> {
    private List<T> items;
    private int totalCount;
}