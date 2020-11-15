package es.danielcr86.table.model;

import es.danielcr86.table.model.Cell;
import es.danielcr86.table.model.Column;
import es.danielcr86.table.model.Row;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RowTest {

    public static final int COLUMN_INDEX = 123;
    public static final int ROW_INDEX = 456;

    @Test
    public void intersectionWithColumn_returnsCell() {
        final Row row = new Row(ROW_INDEX);

        final Cell intersection = row.intersectionWith(new Column(COLUMN_INDEX));

        assertThat(intersection).isEqualTo(new Cell(new Row(ROW_INDEX), new Column(COLUMN_INDEX)));
    }

}