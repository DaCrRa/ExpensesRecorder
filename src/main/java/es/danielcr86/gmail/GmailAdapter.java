package es.danielcr86.gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import es.danielcr86.mail.model.MailRepository;
import es.danielcr86.mail.model.Message;
import lombok.NonNull;
import lombok.SneakyThrows;

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
    @SneakyThrows
    public List<Message> getMessages(@NonNull final String senderEmail,
                                     @NonNull final LocalDate fromDate) {

        final String formattedFromDate = fromDate.format(DateTimeFormatter.ISO_DATE);

        final ListMessagesResponse messages =
                service.users()
                        .messages()
                        .list(ME_USER)
                        .setQ("from:" + senderEmail + " after:" + formattedFromDate)
                        .execute();

        return messages.getMessages().stream()
                .map(this::detailedMessageFrom)
                .map(converter::convert)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private com.google.api.services.gmail.model.Message detailedMessageFrom(com.google.api.services.gmail.model.Message msg) {
        return service.users()
                .messages()
                .get(ME_USER, msg.getId())
                .execute();
    }
}
