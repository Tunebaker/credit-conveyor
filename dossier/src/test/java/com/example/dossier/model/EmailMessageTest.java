package com.example.dossier.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailMessageTest {

    @Test
    void getAddress() {
        EmailMessage emailMessage = new EmailMessage("address", Theme.CREDIT_ISSUED, 35L);
        assertEquals("address", emailMessage.getAddress());
        assertEquals(Theme.CREDIT_ISSUED, emailMessage.getTheme());
        assertEquals(35L, emailMessage.getApplicationId());
    }

    @Test
    void testEquals() {
        EmailMessage emailMessage = new EmailMessage("address", Theme.CREDIT_ISSUED, 35L);
        EmailMessage emailMessage2 = new EmailMessage("address", Theme.CREDIT_ISSUED, 35L);
        assertEquals(emailMessage, emailMessage2);
        assertEquals(emailMessage.hashCode(), emailMessage2.hashCode());
    }

    @Test
    void testToString() {
        EmailMessage emailMessage = new EmailMessage("address", Theme.CREDIT_ISSUED, 35L);
        String string = "EmailMessage(address=address, theme=CREDIT_ISSUED, applicationId=35)";
        assertEquals(string, emailMessage.toString());
    }
}