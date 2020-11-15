package es.danielcr86.expenses.table;

import java.io.IOException;
import java.math.BigDecimal;

public interface CellWriter {
    void write(BigDecimal amount) throws IOException;
}
