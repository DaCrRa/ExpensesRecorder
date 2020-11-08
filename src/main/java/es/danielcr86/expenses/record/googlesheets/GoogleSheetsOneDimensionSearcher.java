package es.danielcr86.expenses.record.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesByDataFilterRequest;
import com.google.api.services.sheets.v4.model.BatchGetValuesByDataFilterResponse;
import com.google.api.services.sheets.v4.model.DataFilter;
import com.google.api.services.sheets.v4.model.GridRange;
import lombok.Value;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GoogleSheetsOneDimensionSearcher {

    private static final String VALUE_RENDER_OPTION = "UNFORMATTED_VALUE";

    private final Sheets service;
    private final String spreadsheetId;

    public GoogleSheetsOneDimensionSearcher(final Sheets service, final String spreadsheetId) {
        this.service = service;
        this.spreadsheetId = spreadsheetId;
    }

    <T> Set<T> search(final Integer index,
                      final SearchDimension searchDimension,
                      final Predicate<String> cellContentCondition,
                      final Function<Integer, T> outputMapping) throws IOException {

        final List<DataFilter> dataFilters = Collections.singletonList(
                dataFilter(searchDimension.integerToGridRange.apply(index))
        );

        final BatchGetValuesByDataFilterRequest requestBody =
                requestBody(dataFilters, searchDimension.resultsMajorDimension);

        final Sheets.Spreadsheets.Values.BatchGetByDataFilter request =
                service.spreadsheets().values().batchGetByDataFilter(spreadsheetId, requestBody);

        final BatchGetValuesByDataFilterResponse response = request.execute();

        final List<List<Object>> values =
                response.getValueRanges().get(0).getValueRange().getValues();


        return IntStream.range(0, values.size())
                .mapToObj(i -> new GoogleSheetsOneDimensionSearcher.Indexed<>(i, values.get(i)))
                .filter(indexed -> !indexed.getElement().isEmpty())
                .map(indexed -> new GoogleSheetsOneDimensionSearcher.Indexed<>(
                        indexed.getI(), indexed.getElement().get(0).toString()))
                .filter(indexed -> cellContentCondition.test(indexed.getElement()))
                .map(GoogleSheetsOneDimensionSearcher.Indexed::getI)
                .map(outputMapping)
                .collect(Collectors.toSet());
    }

    private DataFilter dataFilter(final GridRange gridRange) {
        final DataFilter dataFilter = new DataFilter();
        dataFilter.setGridRange(gridRange);
        return dataFilter;
    }

    private BatchGetValuesByDataFilterRequest requestBody(final List<DataFilter> dataFilters,
                                                          final String resultsMajorDimension) {
        final BatchGetValuesByDataFilterRequest requestBody = new BatchGetValuesByDataFilterRequest();
        requestBody.setValueRenderOption(VALUE_RENDER_OPTION);
        requestBody.setDataFilters(dataFilters);
        requestBody.setMajorDimension(resultsMajorDimension);
        return requestBody;
    }

    @Value
    private static class Indexed<T> {
        int i;
        T element;
    }

    public enum SearchDimension {

        COLUMN(GoogleSheetsOneDimensionSearcher::gridRangeForColumn, "ROWS"),
        ROW(GoogleSheetsOneDimensionSearcher::gridRangeForRow, "COLUMNS");

        private final Function<Integer, GridRange> integerToGridRange;
        private final String resultsMajorDimension;

        SearchDimension(final Function<Integer, GridRange> integerToGridRange,
                        final String resultsMajorDimension) {
            this.integerToGridRange = integerToGridRange;
            this.resultsMajorDimension = resultsMajorDimension;
        }
    }

    private static GridRange gridRangeForColumn(final int columnIndex) {
        final GridRange gridRange = new GridRange();
        gridRange.setStartColumnIndex(columnIndex);
        gridRange.setEndColumnIndex(columnIndex + 1);
        return gridRange;
    }

    private static GridRange gridRangeForRow(final int rowIndex) {
        final GridRange gridRange = new GridRange();
        gridRange.setStartRowIndex(rowIndex);
        gridRange.setEndRowIndex(rowIndex + 1);
        return gridRange;
    }
}
