package es.danielcr86.googlesheets.expenses;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import es.danielcr86.expenses.table.CellWriter;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

@EqualsAndHashCode
public class GoogleSheetsCellWriter implements CellWriter {

    private static final String USER_ENTERED_INPUT_OPTION = "USER_ENTERED";

    private final Sheets service;
    private final String spreadsheetId;
    private final String range;

    public GoogleSheetsCellWriter(final Sheets service,
                                  final String spreadsheetId,
                                  final String range) {
        this.service = service;
        this.spreadsheetId = spreadsheetId;
        this.range = range;
    }

    @Override
    public void write(final BigDecimal amount) throws IOException {

        final ValueRange body = new ValueRange()
                .setValues(Arrays.asList(Arrays.asList(amount)));

        service.spreadsheets()
                .values()
                .update(spreadsheetId, range, body)
                .setValueInputOption(USER_ENTERED_INPUT_OPTION)
                .execute();
    }
}
