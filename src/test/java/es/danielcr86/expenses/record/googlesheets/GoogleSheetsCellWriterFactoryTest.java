package es.danielcr86.expenses.record.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GoogleSheetsCellWriterFactoryTest {

    private static final String SPREADSHEET_ID = "spreadsheet id";
    private static final Cell CELL = new Cell(new Row(11), new Column(22));

    @Mock
    private Sheets service;

    private GoogleSheetsCellWriterFactory googleSheetsCellWriterFactory;

    @BeforeEach
    public void setup() {
        googleSheetsCellWriterFactory = new GoogleSheetsCellWriterFactory(service, SPREADSHEET_ID);
    }

    @Test
    public void cellWriterFor() {
        assertThat(googleSheetsCellWriterFactory.writerFor(CELL))
                .isEqualTo(new GoogleSheetsCellWriter(service, SPREADSHEET_ID, CELL.a1Notation()));
    }

}