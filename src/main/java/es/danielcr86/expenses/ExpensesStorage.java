package es.danielcr86.expenses;

import io.vavr.control.Either;

import java.time.YearMonth;

public interface ExpensesStorage {
    Either<Error, ExpenseRecord> store(final ExpenseRecord record);

    Either<Error, YearMonth> getLastStoredRecordDate(final String category);
}
