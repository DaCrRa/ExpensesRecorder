package es.danielcr86.expenses;

import lombok.Value;

import java.math.BigDecimal;
import java.time.YearMonth;

@Value
public class ExpenseRecord {
    YearMonth date;
    String category;
    BigDecimal amount;
}
