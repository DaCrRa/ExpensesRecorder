package es.danielcr86.table.model;

import com.google.common.collect.ImmutableSet;
import es.danielcr86.table.model.Row;
import es.danielcr86.table.model.RowSet;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

class RowSetTest {

    private final Exception A_THROWABLE = new Exception("some message");

    @Test
    public void intersectionWithRowSet_emptyRowSetWithAnyRowSet_ReturnsEmptyRowSet() {
        assertThat(
                new RowSet(Collections.emptySet())
                        .intersectionWith(new RowSet(ImmutableSet.of(new Row(0), new Row(1), new Row(2)))))
                .isEqualTo(new RowSet(Collections.emptySet()));
    }

    @Test
    public void intersectionWithRowSet_anyRowSetWithEmptyRowSet_ReturnsEmptyRowSet() {
        assertThat(
                new RowSet(ImmutableSet.of(new Row(0), new Row(1), new Row(2)))
                        .intersectionWith(new RowSet(Collections.emptySet())))
                .isEqualTo(new RowSet(Collections.emptySet()));
    }

    @Test
    public void intersectionWithRowSet_anyRowSetWithItself_ReturnsTheRowSet() {
        assertThat(
                new RowSet(ImmutableSet.of(new Row(0), new Row(1), new Row(2)))
                        .intersectionWith(new RowSet(ImmutableSet.of(new Row(0), new Row(1), new Row(2)))))
                .isEqualTo(new RowSet(ImmutableSet.of(new Row(0), new Row(1), new Row(2))));
    }

    @Test
    public void intersectionWithRowSet_disjointRowSets_ReturnsEmptyRowSet() {
        assertThat(
                new RowSet(ImmutableSet.of(new Row(0), new Row(1), new Row(2)))
                        .intersectionWith(new RowSet(ImmutableSet.of(new Row(3), new Row(4), new Row(5)))))
                .isEqualTo(new RowSet(Collections.emptySet()));
    }

    @Test
    public void intersectionWithRowSet_smallRowSetWithBigRowSetTotallyContained_ReturnsOverlappingRowSet() {
        assertThat(
                new RowSet(ImmutableSet.of(new Row(0), new Row(2)))
                        .intersectionWith(new RowSet(ImmutableSet.of(new Row(0), new Row(1), new Row(2)))))
                .isEqualTo(new RowSet(ImmutableSet.of(new Row(0), new Row(2))));
    }

    @Test
    public void intersectionWithRowSet_bigRowSetWithSmallRowSetTotallyContained_ReturnsOverlappingRowSet() {
        assertThat(
                new RowSet(ImmutableSet.of(new Row(0), new Row(1), new Row(2)))
                        .intersectionWith(new RowSet(ImmutableSet.of(new Row(0), new Row(2)))))
                .isEqualTo(new RowSet(ImmutableSet.of(new Row(0), new Row(2))));
    }

    @Test
    public void intersectionWithRowSet_smallRowSetWithBigRowSetPartiallyContained_ReturnsOverlappingRowSet() {
        assertThat(
                new RowSet(ImmutableSet.of(new Row(0), new Row(1)))
                        .intersectionWith(new RowSet(ImmutableSet.of(new Row(1), new Row(2), new Row(3)))))
                .isEqualTo(new RowSet(ImmutableSet.of(new Row(1))));
    }

    @Test
    public void intersectionWithRowSet_bigRowSetWithSmallRowSetPartiallyContained_ReturnsOverlappingRowSet() {
        assertThat(
                new RowSet(ImmutableSet.of(new Row(1), new Row(2), new Row(3)))
                        .intersectionWith(new RowSet(ImmutableSet.of(new Row(0), new Row(1)))))
                .isEqualTo(new RowSet(ImmutableSet.of(new Row(1))));
    }

    @Test
    public void uniqueRowOrThrow_rowSetHasAUniqueRow_returnsTheRow() throws Exception {
        assertThat(
                new RowSet(ImmutableSet.of(new Row(1))).uniqueRowOrThrow(() ->  A_THROWABLE))
                .isEqualTo(new Row(1));
    }

    @Test
    public void uniqueRowOrThrow_emptyRowSet_throws() {
        assertThatThrownBy(
                () -> new RowSet(ImmutableSet.of()).uniqueRowOrThrow(() ->  A_THROWABLE))
                .isSameAs(A_THROWABLE);
    }

    @Test
    public void uniqueRowOrThrow_rowSetHasTwoRows_throws() {
        assertThatThrownBy(
                () -> new RowSet(ImmutableSet.of(new Row(1), new Row(2))).uniqueRowOrThrow(() ->  A_THROWABLE))
                .isSameAs(A_THROWABLE);
    }
}