package com.ewallet.wallet_service.dto.response;

import com.ewallet.wallet_service.dto.PaginationMetadata;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private PaginationMetadata pagination;
    private List<T> data;
}
