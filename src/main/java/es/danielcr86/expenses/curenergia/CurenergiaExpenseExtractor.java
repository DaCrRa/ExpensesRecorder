package es.danielcr86.expenses.curenergia;

import es.danielcr86.expenses.Error;
import es.danielcr86.expenses.ExpenseRecord;
import es.danielcr86.mail.model.Message;
import io.vavr.control.Either;
import lombok.SneakyThrows;
import lombok.Value;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurenergiaExpenseExtractor {

    public static final Locale ES_ES_LOCALE = new Locale("es", "ES");
    public static final PatternConfig PATTERN_CONFIG =  new PatternConfig(
            "Importe: ([1-9]*[0-9],[0-9][0-9]) €",
            1,
            ES_ES_LOCALE);

    public static final Pattern PATTERN = Pattern.compile(PATTERN_CONFIG.getPatternValue());
    public static final String LUZ_CATEGORY = "LUZ";

    public Either<Error, ExpenseRecord> extractFrom(final Message message) {
        final Matcher matcher = PATTERN.matcher(message.getText());
        if (!matcher.find()) {
            // TODO: add error information to error instance.
            // ("Message text does not contain an expense amount." +
            //                    "Message: " + message + " " +
            //                    "Category: " + LUZ_CATEGORY);
            return Either.left(new Error());
        }

        final String amount = matcher.group(PATTERN_CONFIG.getGroup());

        return Either.right(
                new ExpenseRecord(
                        YearMonth.of(message.getDate().getYear(), message.getDate().getMonth()),
                        LUZ_CATEGORY,
                        asBigDecimal(amount)));
    }

    @SneakyThrows
    private BigDecimal asBigDecimal(final String extractedAmount) {
        final Number parse = NumberFormat.getNumberInstance(ES_ES_LOCALE).parse(extractedAmount);
        return new BigDecimal(parse.toString());
    }

    @Value
    private static class PatternConfig {
        String patternValue;
        int group;
        Locale locale;
    }
}
