package es.danielcr86.mail.model;

import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

public interface MailRepository {
    List<Message> getMessages(@NonNull final String senderEmail, @NonNull final LocalDate fromDate);
}
