package es.danielcr86.expenses.table;

import es.danielcr86.table.model.Cell;

import java.io.IOException;
import java.time.YearMonth;

public interface TableLayout {
    Cell cellFor(final YearMonth date, final String category) throws IOException, TableLayoutException;
}
