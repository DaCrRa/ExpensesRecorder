package es.danielcr86.gmail;

import es.danielcr86.mail.model.Message;

import java.time.LocalDate;

public class MessageConverter {

    private static final long MILLIS_PER_DAY = 86400000;

    public Message convert(final com.google.api.services.gmail.model.Message message) {
        return new Message(
                LocalDate.ofEpochDay(message.getInternalDate() / MILLIS_PER_DAY),
                message.getSnippet());
    }
}
