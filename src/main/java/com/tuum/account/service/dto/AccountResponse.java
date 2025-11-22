package com.tuum.account.service.dto;

import java.util.List;

public record AccountResponse(Long accountId, Long customerId, List<BalanceDto> balances) {}
