package es.danielcr86.mail.model;

import es.danielcr86.expenses.Error;
import io.vavr.control.Either;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

public interface MailRepository {
    List<Either<Error, Message>> getMessages(@NonNull final String senderEmail, @NonNull final LocalDate fromDate);
}
