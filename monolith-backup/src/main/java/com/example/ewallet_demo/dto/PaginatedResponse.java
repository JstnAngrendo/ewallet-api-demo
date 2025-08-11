package com.example.ewallet_demo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private PaginationMetadata pagination;
    private List<T> data;
}
