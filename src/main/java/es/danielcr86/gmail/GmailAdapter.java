package es.danielcr86.gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.common.collect.ImmutableList;
import es.danielcr86.expenses.Error;
import es.danielcr86.mail.model.MailRepository;
import es.danielcr86.mail.model.Message;
import io.vavr.control.Either;
import lombok.NonNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class GmailAdapter implements MailRepository {

    private static final String ME_USER = "me";

    private final Gmail service;
    private final MessageConverter converter;

    public GmailAdapter(Gmail service, MessageConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @Override
    public List<Either<Error, Message>> getMessages(@NonNull final String senderEmail,
                                                    @NonNull final LocalDate fromDate) {

        final String formattedFromDate = fromDate.format(DateTimeFormatter.ISO_DATE);

        final Either<Error, ListMessagesResponse> messageIds = getMessageIds(senderEmail, formattedFromDate);

        final List<Either<Error, com.google.api.services.gmail.model.Message>> detailedMessages = getDetailedMessages(messageIds);

        return detailedMessages.stream()
                .map(errorOrGmailMsg -> errorOrGmailMsg.map(converter::convert))
                .collect(Collectors.toList());
    }

    private Either<Error, ListMessagesResponse> getMessageIds(final String senderEmail, final String formattedFromDate) {
        try {
            return Either.right(
                    service.users()
                            .messages()
                            .list(ME_USER)
                            .setQ("from:" + senderEmail + " after:" + formattedFromDate)
                            .execute());
        } catch (final IOException e) {
            return Either.left(new Error());
        }
    }

    private List<Either<Error, com.google.api.services.gmail.model.Message>> getDetailedMessages(
            final Either<Error, ListMessagesResponse> errorOrMessageIds) {

        if (errorOrMessageIds.isLeft()) {
            return ImmutableList.of(Either.left(errorOrMessageIds.getLeft()));
        }

        return errorOrMessageIds.get().getMessages().stream()
                .map(this::detailedMessageFrom)
                .collect(Collectors.toList());
    }

    private Either<Error, com.google.api.services.gmail.model.Message> detailedMessageFrom(
            com.google.api.services.gmail.model.Message msg) {
        try {
            return Either.right(service.users()
                    .messages()
                    .get(ME_USER, msg.getId())
                    .execute());
        } catch (final IOException e) {
            return Either.left(new Error());
        }
    }
}
