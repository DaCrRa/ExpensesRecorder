package es.danielcr86.expenses.record.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesByDataFilterRequest;
import com.google.api.services.sheets.v4.model.BatchGetValuesByDataFilterResponse;
import com.google.api.services.sheets.v4.model.DataFilter;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.MatchedValueRange;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleSheetsOneDimensionSearcherTest {

    private static final String VALUE_RENDER_OPTION = "UNFORMATTED_VALUE";
    private static final String SPREADSHEET_ID = "spreadsheet_id";
    private static final Integer INDEX = 321;
    private static final String WANTED_CONTENT_VALUE = "yes";
    public static final String NOT_WANTED_CONTENT = "no";

    @Mock
    private Sheets service;

    @Mock
    private Sheets.Spreadsheets spreadsheets;

    @Mock
    private Sheets.Spreadsheets.Values values;

    @Mock
    private Sheets.Spreadsheets.Values.BatchGetByDataFilter request;

    private GoogleSheetsOneDimensionSearcher googleSheetsOneDimensionSearcher;

    @BeforeEach
    public void setup() {
        when(service.spreadsheets()).thenReturn(spreadsheets);
        when(spreadsheets.values()).thenReturn(values);

        googleSheetsOneDimensionSearcher = new GoogleSheetsOneDimensionSearcher(service, SPREADSHEET_ID);
    }

    @Test
    public void searchInRow_noCellsMatchPredicate_returnsEmptySet() throws IOException {
        when(values.batchGetByDataFilter(
                SPREADSHEET_ID,
                requestBody(dataFilter(rowGridRange(INDEX)), "COLUMNS"))
        ).thenReturn(request);

        when(request.execute()).thenReturn(response(Arrays.asList(
                Collections.singletonList(NOT_WANTED_CONTENT),
                Collections.singletonList(NOT_WANTED_CONTENT),
                Collections.singletonList(NOT_WANTED_CONTENT)
        )));

        final Set<Integer> matchedColumnIndexes = googleSheetsOneDimensionSearcher.search(
                INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.ROW,
                s -> s.equals(WANTED_CONTENT_VALUE),
                Function.identity()
        );

        assertThat(matchedColumnIndexes).isEmpty();
    }

    @Test
    public void searchInRow_allCellsMatchPredicate_returnsSetWithIndexes() throws IOException {
        when(values.batchGetByDataFilter(
                SPREADSHEET_ID,
                requestBody(dataFilter(rowGridRange(INDEX)), "COLUMNS"))
        ).thenReturn(request);

        when(request.execute()).thenReturn(response(Arrays.asList(
                Collections.singletonList(WANTED_CONTENT_VALUE),
                Collections.singletonList(WANTED_CONTENT_VALUE),
                Collections.singletonList(WANTED_CONTENT_VALUE)
        )));

        final Set<Integer> matchedColumnIndexes = googleSheetsOneDimensionSearcher.search(
                INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.ROW,
                s -> s.equals(WANTED_CONTENT_VALUE),
                Function.identity()
        );

        assertThat(matchedColumnIndexes).containsExactly(0, 1, 2);
    }

    @Test
    public void searchInRow_someCellsMatchPredicate_returnsSetWithIndexes() throws IOException {
        when(values.batchGetByDataFilter(
                SPREADSHEET_ID,
                requestBody(dataFilter(rowGridRange(INDEX)), "COLUMNS"))
        ).thenReturn(request);

        when(request.execute()).thenReturn(response(Arrays.asList(
                Collections.singletonList(WANTED_CONTENT_VALUE),
                Collections.singletonList(NOT_WANTED_CONTENT),
                Collections.singletonList(WANTED_CONTENT_VALUE)
        )));

        final Set<Integer> matchedColumnIndexes = googleSheetsOneDimensionSearcher.search(
                INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.ROW,
                s -> s.equals(WANTED_CONTENT_VALUE),
                Function.identity()
        );

        assertThat(matchedColumnIndexes).containsExactly(0, 2);
    }

    @Test
    public void searchInRow_someCellsMatchPredicateAndEmptyCells_returnsSetWithIndexes() throws IOException {
        when(values.batchGetByDataFilter(
                SPREADSHEET_ID,
                requestBody(dataFilter(rowGridRange(INDEX)), "COLUMNS"))
        ).thenReturn(request);

        when(request.execute()).thenReturn(response(Arrays.asList(
                Collections.emptyList(),
                Collections.singletonList(WANTED_CONTENT_VALUE),
                Collections.singletonList(NOT_WANTED_CONTENT),
                Collections.emptyList(),
                Collections.singletonList(NOT_WANTED_CONTENT),
                Collections.singletonList(WANTED_CONTENT_VALUE)
        )));

        final Set<Integer> matchedColumnIndexes = googleSheetsOneDimensionSearcher.search(
                INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.ROW,
                s -> s.equals(WANTED_CONTENT_VALUE),
                Function.identity()
        );

        assertThat(matchedColumnIndexes).containsExactly(1, 5);
    }

    @Test
    public void searchInColumn_noCellsMatchPredicate_returnsEmptySet() throws IOException {
        when(values.batchGetByDataFilter(
                SPREADSHEET_ID,
                requestBody(dataFilter(columnGridRange(INDEX)), "ROWS"))
        ).thenReturn(request);

        when(request.execute()).thenReturn(response(Arrays.asList(
                Collections.singletonList(NOT_WANTED_CONTENT),
                Collections.singletonList(NOT_WANTED_CONTENT),
                Collections.singletonList(NOT_WANTED_CONTENT)
        )));

        final Set<Integer> matchedRowIndexes = googleSheetsOneDimensionSearcher.search(
                INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.COLUMN,
                s -> s.equals(WANTED_CONTENT_VALUE),
                Function.identity()
        );

        assertThat(matchedRowIndexes).isEmpty();
    }

    @Test
    public void searchInColumn_allCellsMatchPredicate_returnsSetWithIndexes() throws IOException {
        when(values.batchGetByDataFilter(
                SPREADSHEET_ID,
                requestBody(dataFilter(columnGridRange(INDEX)), "ROWS"))
        ).thenReturn(request);

        when(request.execute()).thenReturn(response(Arrays.asList(
                Collections.singletonList(WANTED_CONTENT_VALUE),
                Collections.singletonList(WANTED_CONTENT_VALUE),
                Collections.singletonList(WANTED_CONTENT_VALUE)
        )));

        final Set<Integer> matchedRowIndexes = googleSheetsOneDimensionSearcher.search(
                INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.COLUMN,
                s -> s.equals(WANTED_CONTENT_VALUE),
                Function.identity()
        );

        assertThat(matchedRowIndexes).containsExactly(0, 1, 2);
    }

    @Test
    public void searchInColumn_someCellsMatchPredicate_returnsSetWithIndexes() throws IOException {
        when(values.batchGetByDataFilter(
                SPREADSHEET_ID,
                requestBody(dataFilter(columnGridRange(INDEX)), "ROWS"))
        ).thenReturn(request);

        when(request.execute()).thenReturn(response(Arrays.asList(
                Collections.singletonList(WANTED_CONTENT_VALUE),
                Collections.singletonList(NOT_WANTED_CONTENT),
                Collections.singletonList(WANTED_CONTENT_VALUE)
        )));

        final Set<Integer> matchedRowIndexes = googleSheetsOneDimensionSearcher.search(
                INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.COLUMN,
                s -> s.equals(WANTED_CONTENT_VALUE),
                Function.identity()
        );

        assertThat(matchedRowIndexes).containsExactly(0, 2);
    }

    @Test
    public void searchInColumns_someCellsMatchPredicateAndEmptyCells_returnsSetWithIndexes() throws IOException {
        when(values.batchGetByDataFilter(
                SPREADSHEET_ID,
                requestBody(dataFilter(columnGridRange(INDEX)), "ROWS"))
        ).thenReturn(request);

        when(request.execute()).thenReturn(response(Arrays.asList(
                Collections.emptyList(),
                Collections.singletonList(WANTED_CONTENT_VALUE),
                Collections.singletonList(NOT_WANTED_CONTENT),
                Collections.emptyList(),
                Collections.singletonList(NOT_WANTED_CONTENT),
                Collections.singletonList(WANTED_CONTENT_VALUE)
        )));

        final Set<Integer> matchedRowIndexes = googleSheetsOneDimensionSearcher.search(
                INDEX,
                GoogleSheetsOneDimensionSearcher.SearchDimension.COLUMN,
                s -> s.equals(WANTED_CONTENT_VALUE),
                Function.identity()
        );

        assertThat(matchedRowIndexes).containsExactly(1, 5);
    }

    private GridRange rowGridRange(final Integer index) {
        final GridRange gridRange = new GridRange();
        gridRange.setStartRowIndex(index);
        gridRange.setEndRowIndex(index + 1);
        return gridRange;
    }

    private GridRange columnGridRange(final Integer index) {
        final GridRange gridRange = new GridRange();
        gridRange.setStartColumnIndex(index);
        gridRange.setEndColumnIndex(index + 1);
        return gridRange;
    }

    private DataFilter dataFilter(final GridRange gridRange) {
        final DataFilter dataFilter = new DataFilter();
        dataFilter.setGridRange(gridRange);
        return dataFilter;
    }

    private BatchGetValuesByDataFilterRequest requestBody(final DataFilter dataFilter,
                                                          final String majorDimension) {
        final BatchGetValuesByDataFilterRequest request =
                new BatchGetValuesByDataFilterRequest();
        request.setDataFilters(Collections.singletonList(dataFilter));
        request.setValueRenderOption(VALUE_RENDER_OPTION);
        request.setMajorDimension(majorDimension);
        return request;
    }

    private BatchGetValuesByDataFilterResponse response(List<List<Object>> values) {
        final ValueRange valueRange = new ValueRange();
        valueRange.setValues(values);

        final MatchedValueRange matchedValueRange = new MatchedValueRange();
        matchedValueRange.setValueRange(valueRange);

        final BatchGetValuesByDataFilterResponse batchGetValuesByDataFilterResponse =
                new BatchGetValuesByDataFilterResponse();
        batchGetValuesByDataFilterResponse.setValueRanges(Collections.singletonList(matchedValueRange));
        return batchGetValuesByDataFilterResponse;
    }
}
