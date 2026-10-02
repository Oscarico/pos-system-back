package com.osulaa.possystem.dto.common;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageResponse<T> {

    private List<T> items;

    private int page;

    private int size;

    private Long totalItems;

    private int totalPages;

    private boolean hasNext;

    private boolean hasPrevious;

}
