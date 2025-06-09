package com.example.ewallet_demo.mapper;

import com.example.ewallet_demo.dto.AdminTransactionResponse;
import com.example.ewallet_demo.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminTransactionMapper {

    private final TransactionMapper baseMapper;

//    public AdminTransactionResponse toAdminResponse(Transaction transaction) {
//        AdminTransactionResponse response = new AdminTransactionResponse();
//        baseMapper.toResponse(transaction);
//
//        response.setIpAddress(transaction.getIpAddress());
//        response.setDeviceInfo(transaction.getDeviceInfo());
//        return response;
//    }
}
