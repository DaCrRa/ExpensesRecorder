package es.danielcr86.expenses.table;

import es.danielcr86.expenses.table.CellWriter;
import es.danielcr86.expenses.table.CellWriterFactory;
import es.danielcr86.expenses.table.TableLayout;
import es.danielcr86.expenses.table.TableLayoutException;
import es.danielcr86.expenses.table.TableUpdater;
import es.danielcr86.table.model.Cell;
import es.danielcr86.table.model.Column;
import es.danielcr86.table.model.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableUpdaterTest {

    public static final YearMonth DATE = YearMonth.of(2020, Month.MARCH);
    public static final String CATEGORY = "category";
    public static final Cell CELL = new Cell(new Row(11), new Column(22));
    public static final BigDecimal AMOUNT = BigDecimal.valueOf(123L);

    @Mock
    private CellWriterFactory factory;
    @Mock
    private CellWriter cellWriter;
    @Mock
    private TableLayout layout;

    private TableUpdater tableUpdater;

    @BeforeEach
    public void setup() {
        tableUpdater = new TableUpdater(layout, factory);
    }

    @Test
    public void newExpense() throws IOException, TableLayoutException {
        when(layout.cellFor(DATE, CATEGORY)).thenReturn(CELL);
        when(factory.writerFor(CELL)).thenReturn(cellWriter);

        tableUpdater.newExpense(DATE, CATEGORY, AMOUNT);

        verify(cellWriter).write(AMOUNT);
    }

}