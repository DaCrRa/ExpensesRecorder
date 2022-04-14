package es.danielcr86.expenses;

import io.vavr.control.Either;

import java.time.YearMonth;
import java.util.List;

public interface ExpensesSource {
    List<Either<Error, ExpenseRecord>> getExpensesAfter(final YearMonth lastStoredExpenseDate);
    String getCategory();
}
