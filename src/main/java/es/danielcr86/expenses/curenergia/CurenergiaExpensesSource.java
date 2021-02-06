package es.danielcr86.expenses.curenergia;

import es.danielcr86.expenses.Error;
import es.danielcr86.expenses.ExpenseRecord;
import es.danielcr86.expenses.ExpensesSource;
import es.danielcr86.mail.model.MailRepository;
import es.danielcr86.mail.model.Message;
import io.vavr.control.Either;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class CurenergiaExpensesSource implements ExpensesSource {

    private static final String CURENERGIA_SENDER_EMAIL = "facturaelectronica@curenergia.es";
    private static final String LUZ_CATEGORY = "LUZ";

    private final MailRepository mailRepository;
    private final CurenergiaExpenseExtractor curenergiaExpenseExtractor;

    public CurenergiaExpensesSource(@NonNull final MailRepository mailRepository,
                                    @NonNull final CurenergiaExpenseExtractor curenergiaExpenseExtractor) {
        this.mailRepository = mailRepository;
        this.curenergiaExpenseExtractor = curenergiaExpenseExtractor;
    }

    @Override
    public List<Either<Error, ExpenseRecord>> getExpensesAfter(@NonNull final YearMonth lastStoredExpenseDate) {
        final YearMonth nextMonth = lastStoredExpenseDate.plusMonths(1);
        return mailRepository.getMessages(CURENERGIA_SENDER_EMAIL,
                LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), 1))
                .stream()
                .map(this::extractExpense)
                .collect(Collectors.toList());
    }

    @Override
    public String getCategory() {
        return LUZ_CATEGORY;
    }

    private Either<Error, ExpenseRecord> extractExpense(final Either<Error, Message> errorOrMessage) {
        return errorOrMessage.flatMap(curenergiaExpenseExtractor::extractFrom);
    }
}
