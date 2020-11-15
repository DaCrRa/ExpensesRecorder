package es.danielcr86.expenses.table;

import es.danielcr86.table.model.Cell;
import lombok.NonNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;

public class TableUpdater {

    private final TableLayout layout;
    private final CellWriterFactory cellWriterFactory;

    public TableUpdater(@NonNull final TableLayout layout,
                        @NonNull final CellWriterFactory cellWriterFactory) {
        this.layout = layout;
        this.cellWriterFactory = cellWriterFactory;
    }

    public void newExpense(final YearMonth date,
                           final String category,
                           final BigDecimal amount) throws IOException, TableLayoutException {
        final Cell expenseCell = layout.cellFor(date, category);
        final CellWriter writer = cellWriterFactory.writerFor(expenseCell);
        writer.write(amount);
    }
}
