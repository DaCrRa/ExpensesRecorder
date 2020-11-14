package es.danielcr86.expenses.record.googlesheets;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Month;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonthColumnYearColumnCategoryColumnsLayoutTest {

    private static final int MONTHS_COLUMN = 1;
    private static final int YEARS_COLUMN = 2;
    private static final int CATEGORIES_ROW = 3;
    private static final Month MONTH = Month.MARCH;
    private static final int YEAR = 2020;
    private static final String CATEGORY = "category";

    @Mock
    private MonthColumnYearColumnCategoryColumnsGoogleSheetsAdapter storageAdapter;

    private MonthColumnYearColumnCategoryColumnsLayout layout;

    @BeforeEach
    public void setup() {
        layout = new MonthColumnYearColumnCategoryColumnsLayout(
                storageAdapter,
                MonthColumnYearColumnCategoryColumnsLayoutConfig.builder()
                        .yearsColumnIndex(YEARS_COLUMN)
                        .monthsColumnIndex(MONTHS_COLUMN)
                        .categoryLabelsRowIndex(CATEGORIES_ROW)
                        .build()
        );
    }

    @Test
    public void cellFor_cellExists_returnsCell() throws IOException, TableLayoutException {
        when(storageAdapter.rowsForMonth(MONTH, MONTHS_COLUMN)).thenReturn(
                new RowSet(ImmutableSet.of(new Row(2)))
        );
        when(storageAdapter.rowsForYear(YEAR, YEARS_COLUMN)).thenReturn(
                new RowSet(ImmutableSet.of(new Row(2)))
        );
        when(storageAdapter.columnForCategory(CATEGORY, CATEGORIES_ROW)).thenReturn(
                new Column(4)
        );

        assertThat(layout.cellFor(YearMonth.of(YEAR, MONTH), CATEGORY))
                .isEqualTo(new Cell(new Row(2), new Column(4)));
    }

    @Test
    public void cellFor_emptyMonthYearIntersection_ThrowsTableLayoutException() throws IOException, TableLayoutException {
        when(storageAdapter.rowsForMonth(MONTH, MONTHS_COLUMN)).thenReturn(
                new RowSet(ImmutableSet.of(new Row(1)))
        );
        when(storageAdapter.rowsForYear(YEAR, YEARS_COLUMN)).thenReturn(
                new RowSet(ImmutableSet.of(new Row(2)))
        );
        when(storageAdapter.columnForCategory(CATEGORY, CATEGORIES_ROW)).thenReturn(
                new Column(4)
        );

        assertThatThrownBy(
                () -> layout.cellFor(YearMonth.of(YEAR, MONTH), CATEGORY))
                .isInstanceOf(TableLayoutException.class)
                .hasMessageStartingWith("Not unique row");
    }

    @Test
    public void cellFor_monthYearIntersectionNotUnique_ThrowsTableLayoutException() throws IOException, TableLayoutException {
        when(storageAdapter.rowsForMonth(MONTH, MONTHS_COLUMN)).thenReturn(
                new RowSet(ImmutableSet.of(new Row(1), new Row(2)))
        );
        when(storageAdapter.rowsForYear(YEAR, YEARS_COLUMN)).thenReturn(
                new RowSet(ImmutableSet.of(new Row(1), new Row(2)))
        );
        when(storageAdapter.columnForCategory(CATEGORY, CATEGORIES_ROW)).thenReturn(
                new Column(4)
        );

        assertThatThrownBy(
                () -> layout.cellFor(YearMonth.of(YEAR, MONTH), CATEGORY))
                .isInstanceOf(TableLayoutException.class)
                .hasMessageStartingWith("Not unique row");
    }
}