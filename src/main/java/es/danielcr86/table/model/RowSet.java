package es.danielcr86.table.model;

import lombok.NonNull;
import lombok.Value;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Value
public class RowSet {
    private final Set<Row> rows;

    public RowSet intersectionWith(@NonNull final RowSet otherRows) {
        return new RowSet(
                rows.stream()
                        .filter(otherRows.rows::contains)
                        .collect(Collectors.toSet())
        );
    }

    public <T extends Throwable> Row uniqueRowOrThrow(Supplier<T> e) throws T {
        if (rows.size() > 1) {
            throw e.get();
        }

        return rows.stream().findFirst().orElseThrow(e);
    }
}
