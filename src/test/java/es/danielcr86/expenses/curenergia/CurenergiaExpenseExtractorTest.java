package es.danielcr86.expenses.curenergia;

import es.danielcr86.expenses.ExpenseRecord;
import es.danielcr86.expenses.mail.ExtractorException;
import es.danielcr86.mail.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurenergiaExpenseExtractorTest {

    private static final int YEAR = 2020;
    private static final int MONTH = 3;
    private static final int DAY_OF_MONTH = 2;
    private static final String LUZ_CATEGORY = "LUZ";

    private CurenergiaExpenseExtractor curenergiaExpenseExtractor;

    @BeforeEach
    public void setup() {
        curenergiaExpenseExtractor = new CurenergiaExpenseExtractor();
    }

    @Test
    public void extractFrom_happyPath() throws ExtractorException {
        assertThat(
                curenergiaExpenseExtractor.extractFrom(
                        new Message(
                                LocalDate.of(YEAR, MONTH, DAY_OF_MONTH),
                                "some text. Importe: 33,00 €. some more text")))
                .isEqualTo(new ExpenseRecord(
                        YearMonth.of(YEAR, MONTH),
                        LUZ_CATEGORY,
                        BigDecimal.valueOf(33)));
    }

    @Test
    public void extractFrom_noAmountInText_throwsExpenseExtractorException() {
        assertThatThrownBy(() ->
                curenergiaExpenseExtractor.extractFrom(
                        new Message(
                                LocalDate.of(YEAR, MONTH, DAY_OF_MONTH),
                                "some text. some more text")))
                .isInstanceOf(ExtractorException.class);
    }

}