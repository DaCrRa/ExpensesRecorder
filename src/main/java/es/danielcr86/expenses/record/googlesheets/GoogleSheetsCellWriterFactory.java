package es.danielcr86.expenses.record.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import lombok.NonNull;

public class GoogleSheetsCellWriterFactory implements CellWriterFactory {

    private final Sheets service;
    private final String spreadsheetId;

    public GoogleSheetsCellWriterFactory(@NonNull final Sheets service,
                                         @NonNull final String spreadsheetId) {
        this.service = service;
        this.spreadsheetId = spreadsheetId;
    }

    @Override
    public CellWriter writerFor(final Cell expenseCell) {
        return new GoogleSheetsCellWriter(service, spreadsheetId, expenseCell.a1Notation());
    }
}
