package com.ecommerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OffsetPaginationDetails {
    private int offset;
    private int limit;
    private Long totalElements;
    private boolean isLast;
}
