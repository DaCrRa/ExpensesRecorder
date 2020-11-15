package es.danielcr86.expenses.table;

import es.danielcr86.table.model.Cell;

public interface CellWriterFactory {
    CellWriter writerFor(final Cell expenseCell);
}
