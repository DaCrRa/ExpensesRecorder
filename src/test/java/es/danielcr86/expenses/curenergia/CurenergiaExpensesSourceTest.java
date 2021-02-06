package es.danielcr86.expenses.curenergia;

import com.google.common.collect.ImmutableList;
import es.danielcr86.expenses.ExpenseRecord;
import es.danielcr86.mail.model.MailRepository;
import es.danielcr86.mail.model.Message;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurenergiaExpensesSourceTest {

    private static final String LUZ_CATEGORY = "LUZ";
    private static final YearMonth LAST_STORED_EXPENSE_DATE = YearMonth.of(2019, 3);
    private static final LocalDate FIRST_DAY_OF_NEXT_MONTH = LocalDate.of(2019, 4, 1);
    private static final Message MESSAGE_1 = new Message(LocalDate.of(2020, 5, 5), "text 1");
    private static final Message MESSAGE_2 = new Message(LocalDate.of(2020, 6, 6), "text 2");
    private static final Message MESSAGE_3 = new Message(LocalDate.of(2020, 7, 7), "text 3");
    private static final ExpenseRecord RECORD_1 = new ExpenseRecord(YearMonth.of(2020, 1), LUZ_CATEGORY, BigDecimal.valueOf(1L));
    private static final ExpenseRecord RECORD_2 = new ExpenseRecord(YearMonth.of(2020, 2), LUZ_CATEGORY, BigDecimal.valueOf(2L));
    private static final ExpenseRecord RECORD_3 = new ExpenseRecord(YearMonth.of(2020, 3), LUZ_CATEGORY, BigDecimal.valueOf(3L));
    public static final String CURENERGIA_SENDER_EMAIL = "facturaelectronica@curenergia.es";

    @Mock
    private MailRepository mailRepository;
    @Mock
    private CurenergiaExpenseExtractor curenergiaExpenseExtractor;

    private CurenergiaExpensesSource curenergiaExpensesSource;

    @BeforeEach
    public void setup() {
        curenergiaExpensesSource = new CurenergiaExpensesSource(mailRepository, curenergiaExpenseExtractor);
    }

    @Test
    public void getExpensesAfter() {
        when(mailRepository.getMessages(CURENERGIA_SENDER_EMAIL, FIRST_DAY_OF_NEXT_MONTH))
                .thenReturn(ImmutableList.of(
                        Either.right(MESSAGE_1),
                        Either.right(MESSAGE_2),
                        Either.right(MESSAGE_3)));
        when(curenergiaExpenseExtractor.extractFrom(MESSAGE_1)).thenReturn(Either.right(RECORD_1));
        when(curenergiaExpenseExtractor.extractFrom(MESSAGE_2)).thenReturn(Either.right(RECORD_2));
        when(curenergiaExpenseExtractor.extractFrom(MESSAGE_3)).thenReturn(Either.right(RECORD_3));

        assertThat(curenergiaExpensesSource.getExpensesAfter(LAST_STORED_EXPENSE_DATE))
                .containsExactlyInAnyOrder(
                        Either.right(RECORD_1),
                        Either.right(RECORD_2),
                        Either.right(RECORD_3));
    }

    @Test
    public void getCategory() {
        assertThat(curenergiaExpensesSource.getCategory()).isEqualTo(LUZ_CATEGORY);
    }

}