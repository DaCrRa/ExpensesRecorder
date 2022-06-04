package es.danielcr86.expenses;

import com.google.common.collect.ImmutableList;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpensesAggregatorTest {

    private static final String CATEGORY = "category";
    private static final String CATEGORY_2 = "category2";
    public static final YearMonth DATE = YearMonth.of(2022, 5);

    private static final ExpenseRecord RECORD_1 = new ExpenseRecord(YearMonth.of(2020, 1), CATEGORY, BigDecimal.valueOf(1L));
    private static final ExpenseRecord RECORD_2 = new ExpenseRecord(YearMonth.of(2020, 2), CATEGORY, BigDecimal.valueOf(2L));
    private static final ExpenseRecord RECORD_3 = new ExpenseRecord(YearMonth.of(2020, 3), CATEGORY, BigDecimal.valueOf(3L));

    @Mock
    private ExpensesSource source1;

    @Mock
    private ExpensesSource source2;

    @Mock
    private ExpensesStorage storage;

    private ExpensesAggregator expensesAggregator;

    @BeforeEach
    public void setup() {
        expensesAggregator = new ExpensesAggregator(ImmutableList.of(source1, source2), storage);
    }

    @Test
    public void aggregate_sunshine() {
        when(source1.getCategory()).thenReturn(CATEGORY);
        when(source2.getCategory()).thenReturn(CATEGORY);

        when(storage.getLastStoredRecordDate(CATEGORY)).thenReturn(Either.right(DATE));
        when(source1.getExpensesAfter(DATE)).thenReturn(ImmutableList.of(
                Either.right(RECORD_1)));
        when(source2.getExpensesAfter(DATE)).thenReturn(ImmutableList.of(
                Either.right(RECORD_2),
                Either.right(RECORD_3)));

        when(storage.store(RECORD_1)).thenReturn(Either.right(RECORD_1));
        when(storage.store(RECORD_2)).thenReturn(Either.right(RECORD_2));
        when(storage.store(RECORD_3)).thenReturn(Either.right(RECORD_3));

        assertThat(expensesAggregator.aggregate())
                .containsExactly(
                        Either.right(RECORD_1),
                        Either.right(RECORD_2),
                        Either.right(RECORD_3));
    }

    @Test
    public void aggregate_cannotGetLastStoredRecordDate() {
        when(source1.getCategory()).thenReturn(CATEGORY);
        when(source2.getCategory()).thenReturn(CATEGORY_2);

        when(storage.getLastStoredRecordDate(CATEGORY)).thenReturn(Either.left(new Error()));
        when(storage.getLastStoredRecordDate(CATEGORY_2)).thenReturn(Either.right(DATE));
        when(source2.getExpensesAfter(DATE)).thenReturn(ImmutableList.of(
                Either.right(RECORD_2),
                Either.right(RECORD_3)));

        when(storage.store(RECORD_2)).thenReturn(Either.right(RECORD_2));
        when(storage.store(RECORD_3)).thenReturn(Either.right(RECORD_3));

        assertThat(expensesAggregator.aggregate())
                .containsExactly(
                        Either.left(new Error()),
                        Either.right(RECORD_2),
                        Either.right(RECORD_3));
    }

    @Test
    public void aggregate_errorStoringOneRecord() {
        when(source1.getCategory()).thenReturn(CATEGORY);
        when(source2.getCategory()).thenReturn(CATEGORY);

        when(storage.getLastStoredRecordDate(CATEGORY)).thenReturn(Either.right(DATE));
        when(source1.getExpensesAfter(DATE)).thenReturn(ImmutableList.of(
                Either.right(RECORD_1)));
        when(source2.getExpensesAfter(DATE)).thenReturn(ImmutableList.of(
                Either.right(RECORD_2),
                Either.right(RECORD_3)));

        when(storage.store(RECORD_1)).thenReturn(Either.right(RECORD_1));
        when(storage.store(RECORD_2)).thenReturn(Either.left(new Error()));
        when(storage.store(RECORD_3)).thenReturn(Either.right(RECORD_3));

        assertThat(expensesAggregator.aggregate())
                .containsExactly(
                        Either.right(RECORD_1),
                        Either.left(new Error()),
                        Either.right(RECORD_3));
    }
}