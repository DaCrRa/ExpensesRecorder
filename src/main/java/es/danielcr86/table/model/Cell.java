package es.danielcr86.table.model;

import lombok.Value;

@Value
public class Cell {
    private static final int MAX_SUPPORTED_COLUMN = 25;

    final Row row;
    final Column column;

    public String a1Notation() {
        if (column.getIndex() > MAX_SUPPORTED_COLUMN) {
            throw new UnsupportedOperationException("A1 notation not implemented for columns index > " + MAX_SUPPORTED_COLUMN);
        }

        final char columnChar = (char)('A' + column.getIndex());
        final String rowNumberString = Integer.toString(row.getIndex() + 1);

        return columnChar + rowNumberString;
    }
}
