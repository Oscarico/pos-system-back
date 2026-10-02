package com.osulaa.possystem.mapper;

import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.osulaa.possystem.dto.common.PageResponse;

@Component
public class PageMapper {

        public <E, D> PageResponse<D> toPageResponse(
                        Page<E> page,
                        Function<E, D> mapper) {

                return PageResponse.<D>builder()
                                .items(
                                                page.getContent()
                                                                .stream()
                                                                .map(mapper)
                                                                .toList())
                                .page(page.getNumber())
                                .size(page.getSize())
                                .totalItems(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .hasNext(page.hasNext())
                                .hasPrevious(page.hasPrevious())
                                .build();
        }
}
