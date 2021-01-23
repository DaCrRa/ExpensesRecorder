package es.danielcr86.gmail;

import es.danielcr86.mail.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MessageConverterTest {

    private static final long EPOCH_MILLISECONDS = 1606590383000L;
    private static final long MILLIS_PER_DAY = 86400000;
    private static final long EPOCH_DAYS = EPOCH_MILLISECONDS / MILLIS_PER_DAY;
    private static final String SOME_TEXT = "some text";

    private MessageConverter messageConverter;

    @BeforeEach
    public void setup() {
        messageConverter = new MessageConverter();
    }

    @Test
    public void convert() {
        final com.google.api.services.gmail.model.Message gmailMessage = new com.google.api.services.gmail.model.Message();
        gmailMessage.setInternalDate(EPOCH_MILLISECONDS);
        gmailMessage.setSnippet(SOME_TEXT);

        assertThat(messageConverter.convert(gmailMessage))
                .isEqualTo(new Message(
                        LocalDate.ofEpochDay(EPOCH_DAYS),
                        SOME_TEXT));
    }

}