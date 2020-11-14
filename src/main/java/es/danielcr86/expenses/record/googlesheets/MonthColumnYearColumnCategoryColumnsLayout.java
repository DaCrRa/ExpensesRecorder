package es.danielcr86.expenses.record.googlesheets;

import lombok.NonNull;

import java.io.IOException;
import java.time.YearMonth;

public class MonthColumnYearColumnCategoryColumnsLayout implements TableLayout {

    private final MonthColumnYearColumnCategoryColumnsStorageAdapter storageAdapter;
    private final MonthColumnYearColumnCategoryColumnsLayoutConfig config;

    public MonthColumnYearColumnCategoryColumnsLayout(
            @NonNull final MonthColumnYearColumnCategoryColumnsGoogleSheetsAdapter storageAdapter,
            @NonNull final MonthColumnYearColumnCategoryColumnsLayoutConfig config) {
        this.storageAdapter = storageAdapter;
        this.config = config;
    }

    public Cell cellFor(@NonNull final YearMonth date,
                        @NonNull final String category) throws IOException, TableLayoutException {
        final RowSet rowsForYear = storageAdapter.rowsForYear(date.getYear(), config.getYearsColumnIndex());
        final RowSet rowsForMonth = storageAdapter.rowsForMonth(date.getMonth(), config.getMonthsColumnIndex());
        final Column columnForCategory = storageAdapter.columnForCategory(category, config.getCategoryLabelsRowIndex());

        final RowSet yearAndMonthRowsIntersection = rowsForYear.intersectionWith(rowsForMonth);

        final Row yearAndMonthRow = yearAndMonthRowsIntersection.uniqueRowOrThrow(
                () -> new TableLayoutException("Not unique row for month " + date.getMonth() + " and year " + date.getYear())
        );

        return yearAndMonthRow.intersectionWith(columnForCategory);
    }
}
