package es.danielcr86.gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import es.danielcr86.mail.model.Message;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GmailAdapterTest {

    private static final String EMAIL = "email";
    private static final LocalDate DATE = LocalDate.of(2020, 2, 28);
    private static final String FORMATTED_DATE = "2020-02-28";

    private static final Message MESSAGE_1 = new Message(
            LocalDate.of(2020, 3, 10), "message text 1");

    private static final Message MESSAGE_2 = new Message(
            LocalDate.of(2020, 3, 20), "message text 2");

    private static final com.google.api.services.gmail.model.Message GMAIL_MESSAGE_1 =
            gmailMessage("1", 1L, "gmail text 1");
    private static final com.google.api.services.gmail.model.Message GMAIL_MESSAGE_2 =
            gmailMessage("2", 2L, "gmail text 2");
    public static final String ME_USER = "me";

    @Mock
    private Gmail service;
    @Mock
    private Gmail.Users users;
    @Mock
    private Gmail.Users.Messages messages;
    @Mock
    private Gmail.Users.Messages.List list;
    @Mock
    private Gmail.Users.Messages.Get get1;
    @Mock
    private Gmail.Users.Messages.Get get2;

    @Mock
    private MessageConverter converter;

    private GmailAdapter gmailAdapter;

    @BeforeEach
    public void setup() {
        gmailAdapter = new GmailAdapter(service, converter);
    }

    @Test
    public void getMessages() throws IOException {
        when(service.users()).thenReturn(users);
        when(users.messages()).thenReturn(messages);
        when(messages.list(ME_USER)).thenReturn(list);
        when(list.setQ("from:" + EMAIL + " after:" + FORMATTED_DATE)).thenReturn(list);
        when(list.execute()).thenReturn(listMessagesResponse(GMAIL_MESSAGE_1, GMAIL_MESSAGE_2));
        when(messages.get(ME_USER, GMAIL_MESSAGE_1.getId())).thenReturn(get1);
        when(get1.execute()).thenReturn(GMAIL_MESSAGE_1);
        when(messages.get(ME_USER, GMAIL_MESSAGE_2.getId())).thenReturn(get2);
        when(get2.execute()).thenReturn(GMAIL_MESSAGE_2);
        when(converter.convert(GMAIL_MESSAGE_1)).thenReturn(MESSAGE_1);
        when(converter.convert(GMAIL_MESSAGE_2)).thenReturn(MESSAGE_2);

        assertThat(gmailAdapter.getMessages(EMAIL, DATE))
            .containsExactlyInAnyOrder(
                    Either.right(MESSAGE_1),
                    Either.right(MESSAGE_2));
    }

    private ListMessagesResponse listMessagesResponse(final com.google.api.services.gmail.model.Message... gmailMessages) {
        final ListMessagesResponse listMessagesResponse = new ListMessagesResponse();
        listMessagesResponse.setMessages(
                Arrays.stream(gmailMessages).collect(Collectors.toList()));
        return listMessagesResponse;
    }

    private static com.google.api.services.gmail.model.Message gmailMessage(final String id,
                                                                            final long internalDate,
                                                                            final String snippet) {
        final com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
        message.setId(id);
        message.setInternalDate(internalDate);
        message.setSnippet(snippet);
        return message;
    }
}