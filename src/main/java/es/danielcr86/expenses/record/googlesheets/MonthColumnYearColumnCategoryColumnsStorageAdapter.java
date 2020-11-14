package es.danielcr86.expenses.record.googlesheets;

import java.io.IOException;
import java.time.Month;

public interface MonthColumnYearColumnCategoryColumnsStorageAdapter {

    Column columnForCategory(final String category, final Integer rowIndex) throws IOException, TableLayoutException;

    RowSet rowsForMonth(final Month month, final Integer columnIndex) throws IOException ;

    RowSet rowsForYear(final int year, int columnIndex) throws IOException;
}
