package com.tuum.account.service.controller;

import com.tuum.account.service.dto.AccountResponse;
import com.tuum.account.service.dto.CreateAccountRequest;
import com.tuum.account.service.mapper.AccountMapStructMapper;
import com.tuum.account.service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
  private final AccountService accountService;
  private final AccountMapStructMapper accountMapStructMapper;

  @PostMapping
  public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
    return accountMapStructMapper.toAccountResponse(accountService.createAccount(request));
  }

  @GetMapping("/{id}")
  public AccountResponse getAccount(@PathVariable Long id) {
    return accountMapStructMapper.toAccountResponse(accountService.getAccount(id));
  }
}
