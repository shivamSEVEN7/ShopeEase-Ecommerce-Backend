package com.ecommerce.project.dto;

import com.ecommerce.project.model.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrdersResponse {
    private List<OrderDTO> content;
    private OffsetPaginationDetails pagination;
}
