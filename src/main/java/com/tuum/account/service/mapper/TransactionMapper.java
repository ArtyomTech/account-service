package com.tuum.account.service.mapper;

import com.tuum.account.service.domain.Transaction;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper {
  void insertTransaction(Transaction transaction);

  List<Transaction> findByAccountId(Long accountId);
}
