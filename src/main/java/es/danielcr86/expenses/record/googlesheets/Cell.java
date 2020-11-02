package es.danielcr86.expenses.record.googlesheets;

import lombok.Value;

@Value
public class Cell {
    final Row row;
    final Column column;
}
