package com.tuum.account.service.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  private Long accountId;
  private Long customerId;
  private String country;
  private List<Balance> balances;
  private LocalDateTime createdAt;
}
