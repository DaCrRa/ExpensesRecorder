package es.danielcr86.expenses.record.googlesheets;

import lombok.Value;

import java.util.Set;
import java.util.stream.Collectors;

@Value
public class RowSet {
    private final Set<Row> rows;

    public RowSet intersectionWith(final RowSet otherRows) {
        return new RowSet(
                rows.stream()
                        .filter(otherRows.rows::contains)
                        .collect(Collectors.toSet())
        );
    }
}
