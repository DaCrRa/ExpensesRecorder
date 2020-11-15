package es.danielcr86.expenses.table.layout;

import es.danielcr86.expenses.table.TableLayoutException;
import es.danielcr86.table.model.Column;
import es.danielcr86.table.model.RowSet;

import java.io.IOException;
import java.time.Month;

public interface MonthColumnYearColumnCategoryColumnsStorage {

    Column columnForCategory(final String category, final Integer rowIndex) throws IOException, TableLayoutException;

    RowSet rowsForMonth(final Month month, final Integer columnIndex) throws IOException ;

    RowSet rowsForYear(final int year, int columnIndex) throws IOException;
}
