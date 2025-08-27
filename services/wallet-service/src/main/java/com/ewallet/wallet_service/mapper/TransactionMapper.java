package com.ewallet.wallet_service.mapper;


import com.ewallet.wallet_service.dto.PaginationMetadata;
import com.ewallet.wallet_service.dto.request.PaginationRequest;
import com.ewallet.wallet_service.dto.response.PaginatedResponse;
import com.ewallet.wallet_service.dto.response.TransactionResponse;
import com.ewallet.wallet_service.model.Transaction;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    public Pageable toPageable(PaginationRequest request) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(request.getSortDirections().get(0)),
                request.getSortProperties().get(0)
        );
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    public TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType().toString())
                .amount(transaction.getAmount())
                .senderUsername(transaction.getSender().getUsername())
                .receiverUsername(transaction.getReceiver().getUsername())
                .description(transaction.getDescription())
                .status(transaction.getStatus().toString())
                .timestamp(transaction.getTimestamp())
                .build();
    }

    public <T, R> PaginatedResponse<R> toPaginatedResponse(Page<T> page, Function<T, R> converter) {
        return new PaginatedResponse<>(
                new PaginationMetadata(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                ),
                page.getContent().stream()
                        .map(converter)
                        .toList()
        );
    }
}
