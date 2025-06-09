package com.example.ewallet_demo.service;


import com.example.ewallet_demo.dto.AdminTransactionResponse;
import com.example.ewallet_demo.dto.PaginatedResponse;
import com.example.ewallet_demo.dto.PaginationMetadata;
import com.example.ewallet_demo.dto.PaginationRequest;
import com.example.ewallet_demo.mapper.TransactionMapper;
import com.example.ewallet_demo.mapper.UserMapper;
import com.example.ewallet_demo.model.Transaction;
import com.example.ewallet_demo.repository.TransactionRepository;
import com.example.ewallet_demo.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final UserMapper userMapper;
    private final TransactionMapper transactionMapper;

    public PaginatedResponse<AdminTransactionResponse> getAllTransactions(PaginationRequest paginationRequest) {
        Sort sort = createSort(paginationRequest.getSortProperties(), paginationRequest.getSortDirections());

        Pageable pageable = PageRequest.of(
                paginationRequest.getPage(),
                paginationRequest.getSize(),
                sort
        );

        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

        List<AdminTransactionResponse> content = transactionPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        PaginationMetadata metadata = new PaginationMetadata(
                transactionPage.getNumber(),
                transactionPage.getSize(),
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages()
        );

        return new PaginatedResponse<>(metadata, content);
    }

    private Sort createSort(List<String> properties, List<String> directions) {
        if (properties == null || properties.isEmpty()) {
            return Sort.unsorted();
        }

        Sort.Direction direction = directions != null && !directions.isEmpty()
                ? Sort.Direction.fromString(directions.get(0))
                : Sort.Direction.DESC;

        return Sort.by(direction, properties.get(0));
    }

    private AdminTransactionResponse mapToResponse(Transaction transaction) {
        return AdminTransactionResponse.builder()
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
}
