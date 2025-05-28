package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AppTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Test
    void testGenerateReceiptText_ValidEntry() {
        CollectionEntry entry = new CollectionEntry(
                "ID12345",
                "Test Client Inc.",
                LocalDate.of(2023, 10, 28),
                150.75
        );

        String receipt = App.generateReceiptText(entry);

        assertNotNull(receipt);
        assertTrue(receipt.contains("--- CASH RECEIPT ---"), "Receipt title missing.");
        assertTrue(receipt.contains("Entry ID: ID12345"), "Entry ID missing or incorrect.");
        assertTrue(receipt.contains("Date: 2023-10-28"), "Date missing or incorrect.");
        assertTrue(receipt.contains("Client Name: Test Client Inc."), "Client Name missing or incorrect.");
        assertTrue(receipt.contains("Amount: $150.75"), "Amount missing, incorrect, or badly formatted.");
        assertTrue(receipt.contains("--- Thank You ---"), "Receipt footer missing.");

        // Check formatting more precisely
        String expectedDateFormatted = "Date: " + LocalDate.of(2023, 10, 28).format(DATE_FORMATTER);
        String expectedAmountFormatted = String.format("Amount: $%.2f", 150.75);
        
        assertTrue(receipt.contains(expectedDateFormatted), "Receipt should contain formatted date: " + expectedDateFormatted);
        assertTrue(receipt.contains(expectedAmountFormatted), "Receipt should contain formatted amount: " + expectedAmountFormatted);
    }

    @Test
    void testGenerateReceiptText_ZeroAmount() {
        CollectionEntry entry = new CollectionEntry(
                "ID67890",
                "Another Client LLC",
                LocalDate.of(2024, 1, 1),
                0.00
        );

        String receipt = App.generateReceiptText(entry);
        assertNotNull(receipt);
        assertTrue(receipt.contains("Amount: $0.00"), "Amount for $0.00 not formatted correctly.");
    }

    @Test
    void testGenerateReceiptText_AmountNeedsTwoDecimalPlaces() {
         CollectionEntry entry = new CollectionEntry(
                "ID11223",
                "Formatting Test Co",
                LocalDate.of(2024, 2, 29),
                99.9  // Should be formatted as 99.90
        );
        String receipt = App.generateReceiptText(entry);
        assertNotNull(receipt);
        assertTrue(receipt.contains("Amount: $99.90"), "Amount 99.9 not formatted to 99.90 correctly.");
    }

    @Test
    void testGenerateReceiptText_LargeAmount() {
         CollectionEntry entry = new CollectionEntry(
                "ID44556",
                "Big Spender Corp",
                LocalDate.of(2024, 3, 15),
                1234567.89
        );
        String receipt = App.generateReceiptText(entry);
        assertNotNull(receipt);
        assertTrue(receipt.contains("Amount: $1234567.89"), "Large amount not formatted correctly.");
    }
}
