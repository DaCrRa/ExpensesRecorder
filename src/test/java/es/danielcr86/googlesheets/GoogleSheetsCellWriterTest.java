package es.danielcr86.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import es.danielcr86.googlesheets.expenses.GoogleSheetsCellWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleSheetsCellWriterTest {

    private static final String SPREADSHEET_ID = "spreadsheet id";
    private static final String RANGE = "range";
    private static final String USER_ENTERED_INPUT_OPTION = "USER_ENTERED";

    @Mock
    private Sheets service;

    @Mock
    private Sheets.Spreadsheets spreadsheets;

    @Mock
    private Sheets.Spreadsheets.Values values;

    @Mock
    private Sheets.Spreadsheets.Values.Update update;

    @BeforeEach
    public void setup() {
        when(service.spreadsheets()).thenReturn(spreadsheets);
        when(spreadsheets.values()).thenReturn(values);
    }

    @Test
    public void saveExpense_updatesCellThroughGoogleSDK() throws IOException {
        final BigDecimal amount = new BigDecimal(1234L);
        final ValueRange body = new ValueRange()
                .setValues(Arrays.asList(Arrays.asList(amount)));

        when(values.update(SPREADSHEET_ID, RANGE, body)).thenReturn(update);
        when(update.setValueInputOption(USER_ENTERED_INPUT_OPTION)).thenReturn(update);

        final GoogleSheetsCellWriter googleSheetsCellWriter = new GoogleSheetsCellWriter(service, SPREADSHEET_ID, RANGE);
        googleSheetsCellWriter.write(amount);

        verify(update).execute();
    }
}