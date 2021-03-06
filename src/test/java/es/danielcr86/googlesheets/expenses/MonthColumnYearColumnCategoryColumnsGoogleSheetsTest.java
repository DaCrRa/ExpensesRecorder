package es.danielcr86.googlesheets.expenses;

import com.google.common.collect.ImmutableSet;
import es.danielcr86.googlesheets.GoogleSheetsOneDimensionSearcher;
import es.danielcr86.expenses.table.TableLayoutException;
import es.danielcr86.table.model.Column;
import es.danielcr86.table.model.Row;
import es.danielcr86.table.model.RowSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonthColumnYearColumnCategoryColumnsGoogleSheetsTest {

    private static final int ROW_INDEX = 123;
    private static final int COLUMN_INDEX = 321;
    private static final String CATEGORY = "category";
    private static final Month MONTH = Month.MARCH;
    private static final Locale LOCALE = new Locale("es", "ES");
    private static final int YEAR = 2020;

    @Mock
    private GoogleSheetsOneDimensionSearcher searcher;

    private MonthColumnYearColumnCategoryColumnsGoogleSheets monthColumnYearColumnCategoryColumnsGoogleSheetsAdapter;

    @BeforeEach
    public void setup() {
        monthColumnYearColumnCategoryColumnsGoogleSheetsAdapter =
                new MonthColumnYearColumnCategoryColumnsGoogleSheets(searcher);
    }

    @Test
    public void cellContentEqualsIgnoreCase() {
        final String category = "category";
        final String equalsIgnoreCaseCategory = "CaTEgoRY";
        final String differentCategory = "other category";

        final MonthColumnYearColumnCategoryColumnsGoogleSheets.CellContentEqualsIgnoreCase cellContentEqualsIgnoreCase =
                new MonthColumnYearColumnCategoryColumnsGoogleSheets.CellContentEqualsIgnoreCase(category);

        assertTrue(cellContentEqualsIgnoreCase.test(category));
        assertTrue(cellContentEqualsIgnoreCase.test(equalsIgnoreCaseCategory));
        assertFalse(cellContentEqualsIgnoreCase.test(differentCategory));
    }

    @Test
    public void integerStringEquals() {
        final int wantedValue = 123;
        final String stringThatMatches = "123";
        final String stringDoesNotMatch = "123.000";


        final MonthColumnYearColumnCategoryColumnsGoogleSheets.IntegerStringEquals integerStringEquals =
                new MonthColumnYearColumnCategoryColumnsGoogleSheets.IntegerStringEquals(wantedValue);

        assertTrue(integerStringEquals.test(stringThatMatches));
        assertFalse(integerStringEquals.test(stringDoesNotMatch));
    }

    @Test
    public void columnForCategory_oneColumnFound_returnsColumn() throws IOException, TableLayoutException {
        when(searcher.search(ROW_INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.ROW,
                new MonthColumnYearColumnCategoryColumnsGoogleSheets.CellContentEqualsIgnoreCase(CATEGORY),
                MonthColumnYearColumnCategoryColumnsGoogleSheets.asColumn())
        ).thenReturn(ImmutableSet.of(new Column(COLUMN_INDEX), new Column(COLUMN_INDEX)));

        assertThat(monthColumnYearColumnCategoryColumnsGoogleSheetsAdapter.columnForCategory(CATEGORY, ROW_INDEX))
                .isEqualTo(new Column(COLUMN_INDEX));
    }

    @Test
    public void columnForCategory_moreThanOneColumnFound_throwsException() throws IOException {
        when(searcher.search(ROW_INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.ROW,
                new MonthColumnYearColumnCategoryColumnsGoogleSheets.CellContentEqualsIgnoreCase(CATEGORY),
                MonthColumnYearColumnCategoryColumnsGoogleSheets.asColumn())
        ).thenReturn(ImmutableSet.of(new Column(COLUMN_INDEX), new Column(COLUMN_INDEX + 1)));

        assertThatThrownBy(() -> monthColumnYearColumnCategoryColumnsGoogleSheetsAdapter.columnForCategory(CATEGORY, ROW_INDEX))
                .isInstanceOf(TableLayoutException.class)
                .hasMessageStartingWith("More than one column");
    }

    @Test
    public void columnForCategory_noColumnFound_throwsException() throws IOException {
        when(searcher.search(ROW_INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.ROW,
                new MonthColumnYearColumnCategoryColumnsGoogleSheets.CellContentEqualsIgnoreCase(CATEGORY),
                MonthColumnYearColumnCategoryColumnsGoogleSheets.asColumn())
        ).thenReturn(ImmutableSet.of());

        assertThatThrownBy(() -> monthColumnYearColumnCategoryColumnsGoogleSheetsAdapter.columnForCategory(CATEGORY, ROW_INDEX))
                .isInstanceOf(TableLayoutException.class)
                .hasMessageStartingWith("No column found");
    }

    @Test
    public void rowsForMonth_noRowsFound_returnsEmptyRowSet() throws IOException {
        when(searcher.search(COLUMN_INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.COLUMN,
                new MonthColumnYearColumnCategoryColumnsGoogleSheets.CellContentEqualsIgnoreCase(MONTH.getDisplayName(TextStyle.FULL, LOCALE)),
                MonthColumnYearColumnCategoryColumnsGoogleSheets.asRow())
        ).thenReturn(ImmutableSet.of());

        assertThat(monthColumnYearColumnCategoryColumnsGoogleSheetsAdapter.rowsForMonth(MONTH, COLUMN_INDEX))
                .isEqualTo(new RowSet(ImmutableSet.of()));
    }

    @Test
    public void rowsForMonth_rowsFound_returnsRowSetWithRows() throws IOException {
        when(searcher.search(COLUMN_INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.COLUMN,
                new MonthColumnYearColumnCategoryColumnsGoogleSheets.CellContentEqualsIgnoreCase(MONTH.getDisplayName(TextStyle.FULL, LOCALE)),
                MonthColumnYearColumnCategoryColumnsGoogleSheets.asRow())
        ).thenReturn(ImmutableSet.of(new Row(ROW_INDEX), new Row(ROW_INDEX + 1)));

        assertThat(monthColumnYearColumnCategoryColumnsGoogleSheetsAdapter.rowsForMonth(MONTH, COLUMN_INDEX))
                .isEqualTo(new RowSet(ImmutableSet.of(new Row(ROW_INDEX), new Row(ROW_INDEX + 1))));
    }

    @Test
    public void rowsForYear_noRowsFound_returnsEmptyRowSet() throws IOException {
        when(searcher.search(COLUMN_INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.COLUMN,
                new MonthColumnYearColumnCategoryColumnsGoogleSheets.IntegerStringEquals(YEAR),
                MonthColumnYearColumnCategoryColumnsGoogleSheets.asRow())
        ).thenReturn(ImmutableSet.of());

        assertThat(monthColumnYearColumnCategoryColumnsGoogleSheetsAdapter.rowsForYear(YEAR, COLUMN_INDEX))
                .isEqualTo(new RowSet(ImmutableSet.of()));
    }

    @Test
    public void rowsForYear_rowsFound_returnsRowSetWithRows() throws IOException {
        when(searcher.search(COLUMN_INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.COLUMN,
                new MonthColumnYearColumnCategoryColumnsGoogleSheets.IntegerStringEquals(YEAR),
                MonthColumnYearColumnCategoryColumnsGoogleSheets.asRow())
        ).thenReturn(ImmutableSet.of(new Row(ROW_INDEX), new Row(ROW_INDEX + 1)));

        assertThat(monthColumnYearColumnCategoryColumnsGoogleSheetsAdapter.rowsForYear(YEAR, COLUMN_INDEX))
                .isEqualTo(new RowSet(ImmutableSet.of(new Row(ROW_INDEX), new Row(ROW_INDEX + 1))));
    }
}