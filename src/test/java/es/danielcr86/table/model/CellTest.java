package es.danielcr86.table.model;

import es.danielcr86.table.model.Cell;
import es.danielcr86.table.model.Column;
import es.danielcr86.table.model.Row;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CellTest {

    @Test
    public void a1Notation() {
        assertThat(new Cell(new Row(0), new Column(0)).a1Notation())
            .isEqualTo("A1");

        assertThat(new Cell(new Row(1), new Column(1)).a1Notation())
                .isEqualTo("B2");

        assertThat(new Cell(new Row(2), new Column(2)).a1Notation())
                .isEqualTo("C3");

        assertThat(new Cell(new Row(2), new Column(25)).a1Notation())
                .isEqualTo("Z3");

        assertThat(new Cell(new Row(9), new Column(25)).a1Notation())
                .isEqualTo("Z10");
    }

    @Test
    public void a1Notation_unsupportedColumnInex() {
        assertThatThrownBy(() -> new Cell(new Row(1), new Column(26)).a1Notation())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}