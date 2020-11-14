package es.danielcr86.expenses.record.googlesheets;

import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class MonthColumnYearColumnCategoryColumnsGoogleSheetsAdapter implements MonthColumnYearColumnCategoryColumnsStorageAdapter {
    private static final Function<Integer, Column> COLUMN_MAPPER = Column::new;
    private static final Function<Integer, Row> ROW_MAPPER = Row::new;

    private final GoogleSheetsOneDimensionSearcher searcher;

    public MonthColumnYearColumnCategoryColumnsGoogleSheetsAdapter(final GoogleSheetsOneDimensionSearcher searcher) {
        this.searcher = searcher;
    }

    public Column columnForCategory(final String category, final Integer rowIndex) throws IOException, TableLayoutException {

        final Set<Column> columns = searcher.search(
                rowIndex,
                GoogleSheetsOneDimensionSearcher.SearchDimension.ROW,
                new CellContentEqualsIgnoreCase(category),
                asColumn()
        );

        if (columns.size() > 1) {
            throw new TableLayoutException("More than one column found for category " + category + " in row " + rowIndex);
        }

        return columns.stream().findFirst()
                .orElseThrow(() -> new TableLayoutException(
                        "No column found for category " + category + " in row " + rowIndex));
    }

    public RowSet rowsForMonth(final Month month, final Integer columnIndex) throws IOException {

        final String monthDisplayName = month.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

        final Set<Row> rows = searcher.search(
                columnIndex,
                GoogleSheetsOneDimensionSearcher.SearchDimension.COLUMN,
                new CellContentEqualsIgnoreCase(monthDisplayName),
                asRow()
        );

        return new RowSet(rows);
    }

    public RowSet rowsForYear(final int year, int columnIndex) throws IOException {

        final Set<Row> rows = searcher.search(
                columnIndex,
                GoogleSheetsOneDimensionSearcher.SearchDimension.COLUMN,
                new IntegerStringEquals(year),
                asRow()
        );

        return new RowSet(rows);
    }

    @EqualsAndHashCode
    static final class CellContentEqualsIgnoreCase implements Predicate<String> {

        private final String wantedValue;

        CellContentEqualsIgnoreCase(final String wantedValue) {
            this.wantedValue = wantedValue;
        }

        @Override
        public boolean test(final String cellContent) {
            return wantedValue.equalsIgnoreCase(cellContent);
        }
    }

    @EqualsAndHashCode
    static final class IntegerStringEquals implements Predicate<String> {

        private final int wantedValue;

        IntegerStringEquals(final int wantedValue) {
            this.wantedValue = wantedValue;
        }

        @Override
        public boolean test(final String cellContent) {
            return Integer.toString(wantedValue).equalsIgnoreCase(cellContent);
        }
    }

    static Function<Integer, Row> asRow() {
        return ROW_MAPPER;
    }

    static Function<Integer, Column> asColumn() {
        return COLUMN_MAPPER;
    }
}