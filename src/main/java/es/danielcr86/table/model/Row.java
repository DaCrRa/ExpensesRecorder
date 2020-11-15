package es.danielcr86.table.model;

import lombok.Value;

@Value
public class Row {
    private final int index;

    public Cell intersectionWith(final Column column) {
        return new Cell(this, column);
    }
}
