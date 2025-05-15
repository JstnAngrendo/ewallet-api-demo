package com.example.ewallet_demo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
public class PaginationRequest {
    @Schema(description = "Page number (0-based)", example = "0", defaultValue = "0")
    private int page = 0;
    @Schema(description = "Number of items per page", example = "10", defaultValue = "10")
    private int size = 10;
    @Schema(description = "Properties to sort by", example = "[\"timestamp\"]")
    private List<String> sortProperties = List.of("timestamp");
    @Schema(description = "Sort directions corresponding to sortProperties", example = "[\"desc\"]")
    private List<String> sortDirections = List.of("desc");
}
