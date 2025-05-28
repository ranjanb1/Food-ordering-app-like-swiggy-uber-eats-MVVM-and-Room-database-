package com.example;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class App {
    private static final CsvDataManager dataManager = new CsvDataManager();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    addNewCollection();
                    break;
                case "2":
                    viewClientCollections();
                    break;
                case "3":
                    generateDailyReport();
                    break;
                case "4":
                    generateWeekendReport();
                    break;
                case "5":
                    generateMonthlyReport();
                    break;
                case "6":
                    running = false;
                    System.out.println("Exiting application.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Add New Collection");
        System.out.println("2. View Client Collections");
        System.out.println("3. Daily Collection Report");
        System.out.println("4. Weekend Collection Report");
        System.out.println("5. Monthly Collection Report");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private static LocalDate promptForDate(String promptMessage) {
        LocalDate date = null;
        boolean validDate = false;
        while (!validDate) {
            System.out.print(promptMessage + " (YYYY-MM-DD): ");
            String dateString = scanner.nextLine();
            try {
                date = LocalDate.parse(dateString, DATE_FORMATTER);
                validDate = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
        return date;
    }

    private static void generateDailyReport() {
        System.out.println("\n--- Daily Collection Report ---");
        LocalDate date = promptForDate("Enter date for daily report");
        if (date == null) return;

        try {
            List<CollectionEntry> allEntries = dataManager.loadCollectionEntries();
            double dailyTotal = ReportUtil.calculateDailyTotal(allEntries, date);

            if (dailyTotal > 0) {
                System.out.println("Total collections for " + date.format(DATE_FORMATTER) + ": " + String.format("%.2f", dailyTotal));
            } else {
                System.out.println("No collections found for " + date.format(DATE_FORMATTER));
            }
        } catch (IOException e) {
            System.err.println("Error loading collection entries: " + e.getMessage());
        }
    }

    private static void generateWeekendReport() {
        System.out.println("\n--- Weekend Collection Report ---");
        LocalDate startDate = null;
        LocalDate endDate = null;
        boolean validRange = false;

        while (!validRange) {
            startDate = promptForDate("Enter start date for weekend report");
            if (startDate == null) continue;

            endDate = promptForDate("Enter end date for weekend report");
            if (endDate == null) continue;

            if (startDate.isAfter(endDate)) {
                System.out.println("Start date cannot be after end date. Please try again.");
            } else {
                validRange = true;
            }
        }

        try {
            List<CollectionEntry> allEntries = dataManager.loadCollectionEntries();
            double weekendTotal = ReportUtil.calculateWeekendTotal(allEntries, startDate, endDate);

            if (weekendTotal > 0) {
                System.out.println("Total weekend collections between " + startDate.format(DATE_FORMATTER) +
                                   " and " + endDate.format(DATE_FORMATTER) + ": " + String.format("%.2f", weekendTotal));
            } else {
                System.out.println("No weekend collections found in the specified period.");
            }
        } catch (IOException e) {
            System.err.println("Error loading collection entries: " + e.getMessage());
        }
    }

    private static void generateMonthlyReport() {
        System.out.println("\n--- Monthly Collection Report ---");
        int year = 0;
        int month = 0;

        boolean validYear = false;
        while(!validYear) {
            try {
                System.out.print("Enter year for monthly report (YYYY): ");
                year = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (year > 0 && year < 10000) { 
                    validYear = true;
                } else {
                    System.out.println("Invalid year. Please enter a valid year (e.g., 2023).");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric year (e.g., 2023).");
                scanner.nextLine(); 
            }
        }

        boolean validMonth = false;
        while(!validMonth) {
            try {
                System.out.print("Enter month for monthly report (MM, e.g., 01 for Jan, 12 for Dec): ");
                month = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (month >= 1 && month <= 12) {
                    validMonth = true;
                } else {
                    System.out.println("Invalid month. Please enter a number between 01 and 12.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric month (e.g., 10 for October).");
                scanner.nextLine(); 
            }
        }

        YearMonth yearMonth = YearMonth.of(year, month);

        try {
            List<CollectionEntry> allEntries = dataManager.loadCollectionEntries();
            double monthlyTotal = ReportUtil.calculateMonthlyTotal(allEntries, yearMonth);
            
            String monthName = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());

            if (monthlyTotal > 0) {
                System.out.println("Total collections for " + monthName + " " + year + " (" + String.format("%02d", month) + "/" + year + "): " + String.format("%.2f", monthlyTotal));
            } else {
                System.out.println("No collections found for " + monthName + " " + year + " (" + String.format("%02d", month) + "/" + year + ")");
            }
        } catch (IOException e) {
            System.err.println("Error loading collection entries: " + e.getMessage());
        }
    }

    public static String generateReceiptText(CollectionEntry entry) { // Made public static for testing
        StringBuilder receipt = new StringBuilder();
        receipt.append("\n--- CASH RECEIPT ---\n");
        receipt.append(String.format("Entry ID: %s\n", entry.getEntryId()));
        receipt.append(String.format("Date: %s\n", entry.getCollectionDate().format(DATE_FORMATTER)));
        receipt.append(String.format("Client Name: %s\n", entry.getClientId()));
        receipt.append(String.format("Amount: $%.2f\n", entry.getAmount())); // Assuming $ as currency symbol
        receipt.append("--- Thank You ---\n");
        return receipt.toString();
    }

    private static void addNewCollection() {
        System.out.print("\nEnter Client Name: ");
        String clientName = scanner.nextLine();

        double amount = 0;
        boolean validAmount = false;
        while (!validAmount) {
            try {
                System.out.print("Enter Collection Amount: ");
                amount = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                if (amount < 0) {
                    System.out.println("Amount cannot be negative. Please enter a valid amount.");
                } else {
                    validAmount = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input for amount. Please enter a numeric value.");
                scanner.nextLine(); 
            }
        }

        LocalDate collectionDate = null;
        boolean validDate = false;
        while (!validDate) {
            System.out.print("Enter Collection Date (YYYY-MM-DD) or press Enter for today: ");
            String dateString = scanner.nextLine();
            if (dateString.isEmpty()) {
                collectionDate = LocalDate.now();
                validDate = true;
            } else {
                try {
                    collectionDate = LocalDate.parse(dateString, DATE_FORMATTER);
                    validDate = true;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD or press Enter for today.");
                }
            }
        }

        String entryId = UUID.randomUUID().toString();
        CollectionEntry newEntry = new CollectionEntry(entryId, clientName, collectionDate, amount);

        try {
            dataManager.saveCollectionEntry(newEntry);
            System.out.println("\nCollection saved: Entry ID: " + newEntry.getEntryId() +
                    ", Client: " + newEntry.getClientId() +
                    ", Date: " + newEntry.getCollectionDate().format(DATE_FORMATTER) +
                    ", Amount: " + String.format("%.2f",newEntry.getAmount()));
            
            // Generate and print receipt
            String receiptText = generateReceiptText(newEntry);
            System.out.println(receiptText);

        } catch (IOException e) {
            System.err.println("Error saving collection entry: " + e.getMessage());
        }
    }

    private static void viewClientCollections() {
        System.out.print("\nEnter Client Name to view collections: ");
        String clientNameInput = scanner.nextLine();

        try {
            List<CollectionEntry> allEntries = dataManager.loadCollectionEntries();
            List<CollectionEntry> clientEntries = new ArrayList<>();

            for (CollectionEntry entry : allEntries) {
                if (entry.getClientId().equalsIgnoreCase(clientNameInput)) {
                    clientEntries.add(entry);
                }
            }

            if (clientEntries.isEmpty()) {
                System.out.println("No collections found for client: " + clientNameInput);
            } else {
                System.out.println("\nCollections for client: " + clientNameInput);
                double totalAmount = 0;
                for (CollectionEntry entry : clientEntries) {
                    System.out.println("Date: " + entry.getCollectionDate().format(DATE_FORMATTER) +
                            ", Amount: " + String.format("%.2f", entry.getAmount()) +
                            ", Entry ID: " + entry.getEntryId());
                    totalAmount += entry.getAmount();
                }
                System.out.println("Total collected from " + clientNameInput + ": " + String.format("%.2f", totalAmount));
            }

        } catch (IOException e) {
            System.err.println("Error loading collection entries: " + e.getMessage());
        }
    }
}
