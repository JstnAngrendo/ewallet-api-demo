package com.example.ewallet_demo.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
      private int page;
      private int size;
      private List<String> sortProperties;
      private List<String> sortDirections;
}
