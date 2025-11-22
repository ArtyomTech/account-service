package com.tuum.account.service.controller;

import com.tuum.account.service.dto.CreateTransactionRequest;
import com.tuum.account.service.dto.TransactionResponse;
import com.tuum.account.service.mapper.TransactionMapStructMapper;
import com.tuum.account.service.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
  private final TransactionService transactionService;
  private final TransactionMapStructMapper transactionMapStructMapper;

  @PostMapping
  public TransactionResponse createTransaction(
      @Valid @RequestBody CreateTransactionRequest request) {
    return transactionMapStructMapper.toTransactionResponse(
        transactionService.createTransaction(request));
  }

  @GetMapping
  public List<TransactionResponse> getTransactions(@RequestParam Long accountId) {
    return transactionMapStructMapper.toTransactionResponses(
        transactionService.getTransactions(accountId));
  }
}
