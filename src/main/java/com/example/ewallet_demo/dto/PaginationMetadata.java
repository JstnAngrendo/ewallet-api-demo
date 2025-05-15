package com.example.ewallet_demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationMetadata {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
