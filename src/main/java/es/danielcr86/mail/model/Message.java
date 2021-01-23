package es.danielcr86.mail.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class Message {
    LocalDate date;
    String text;
}
