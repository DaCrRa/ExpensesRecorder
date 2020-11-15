package es.danielcr86.expenses.record.googlesheets;

public interface CellWriterFactory {
    CellWriter writerFor(final Cell expenseCell);
}
