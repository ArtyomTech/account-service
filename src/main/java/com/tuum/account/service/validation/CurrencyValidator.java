package com.tuum.account.service.validation;

import com.tuum.account.service.domain.Currency;
import com.tuum.account.service.dto.CurrencyDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrencyValidator implements ConstraintValidator<ValidCurrencies, List<CurrencyDto>> {

  private static final Set<String> VALID_CURRENCIES =
      Arrays.stream(Currency.values()).map(Enum::name).collect(Collectors.toSet());

  @Override
  public boolean isValid(List<CurrencyDto> currencies, ConstraintValidatorContext context) {
    if (currencies == null || currencies.isEmpty()) {
      return false;
    }

    return currencies.stream().allMatch(currency -> VALID_CURRENCIES.contains(currency.name()));
  }
}
