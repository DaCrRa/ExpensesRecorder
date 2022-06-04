package es.danielcr86.expenses;

import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExpensesAggregator {

    private final List<ExpensesSource> expensesSources;
    private final ExpensesStorage expensesStorage;

    public ExpensesAggregator(final List<ExpensesSource> expensesSources, final ExpensesStorage expensesStorage) {
        this.expensesSources = expensesSources;
        this.expensesStorage = expensesStorage;
    }

    public List<Either<Error, ExpenseRecord>> aggregate() {
        return expensesSources.stream()
                .map(this::getRecordsToStore)
                .flatMap(Collection::stream)
                .map(this::storeRecord)
                .collect(Collectors.toList());
    }
    private List<Either<Error, ExpenseRecord>> getRecordsToStore(final ExpensesSource expensesSource) {
        return expensesStorage.getLastStoredRecordDate(expensesSource.getCategory())
                .map(expensesSource::getExpensesAfter)
                .fold(error -> Collections.singletonList(Either.left(error)), Function.identity());
    }

    private Either<Error, ExpenseRecord> storeRecord(final Either<Error, ExpenseRecord> errorOrRecord) {
        return errorOrRecord.map(expensesStorage::store)
                .fold(error -> Either.left(error), Function.identity());
    }
}
