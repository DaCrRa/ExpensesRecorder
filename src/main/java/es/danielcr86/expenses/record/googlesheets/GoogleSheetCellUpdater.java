package es.danielcr86.expenses.record.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

public class GoogleSheetCellUpdater {

    private static final String USER_ENTERED_INPUT_OPTION = "USER_ENTERED";

    private final Sheets service;
    private final String spreadsheetId;
    private final String range;

    public GoogleSheetCellUpdater(final Sheets service,
                                  final String spreadsheetId,
                                  final String range) {
        this.service = service;
        this.spreadsheetId = spreadsheetId;
        this.range = range;
    }


    public void saveExpense(final BigDecimal amount) throws IOException {

        final ValueRange body = new ValueRange()
                .setValues(Arrays.asList(Arrays.asList(amount)));

        service.spreadsheets()
                .values()
                .update(spreadsheetId, range, body)
                .setValueInputOption(USER_ENTERED_INPUT_OPTION)
                .execute();
    }
}
