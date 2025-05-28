package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvDataManager {

    private static final String DEFAULT_CSV_FILE_NAME = "collections.csv";
    private static final String CSV_HEADER = "entryId,clientId,collectionDate,amount";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // Existing method, now delegates
    public void saveCollectionEntry(CollectionEntry entry) throws IOException {
        saveCollectionEntry(entry, Paths.get(DEFAULT_CSV_FILE_NAME));
    }

    // New testable method
    public void saveCollectionEntry(CollectionEntry entry, Path path) throws IOException {
        boolean fileExists = Files.exists(path);
        boolean writeHeader = !fileExists || (fileExists && Files.size(path) == 0);

        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (writeHeader) {
                writer.write(CSV_HEADER);
                writer.newLine();
            }
            writer.write(String.join(",",
                    entry.getEntryId(),
                    entry.getClientId(),
                    entry.getCollectionDate().format(DATE_FORMATTER),
                    String.valueOf(entry.getAmount())));
            writer.newLine();
        }
    }

    // Existing method, now delegates
    public List<CollectionEntry> loadCollectionEntries() throws IOException {
        return loadCollectionEntries(Paths.get(DEFAULT_CSV_FILE_NAME));
    }

    // New testable method
    public List<CollectionEntry> loadCollectionEntries(Path path) throws IOException {
        List<CollectionEntry> entries = new ArrayList<>();

        if (!Files.exists(path)) {
            // If the file doesn't exist at all, return empty list.
            // Differentiate from an empty file or file with only header.
            return entries;
        }
        
        // If file exists but is empty (this check also implicitly handles if it only has a BOM)
        if (Files.size(path) == 0) {
             return entries;
        }


        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); 

            // Check if the file is empty after potentially reading a BOM, or if header is missing/incorrect
            if (line == null || line.isEmpty()) {
                // This case handles files that are effectively empty or become empty after reading the first line.
                return entries;
            }
            
            if (!line.equals(CSV_HEADER)) {
                System.err.println("CSV file does not have the correct header. Expected: '" + CSV_HEADER + "', Found: '" + line + "'");
                return entries; // Or throw an exception based on stricter error handling
            }

            // If we're here, header was present and correct. Proceed to read data lines.
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 4) {
                    try {
                        String entryId = values[0];
                        String clientId = values[1];
                        LocalDate collectionDate = LocalDate.parse(values[2], DATE_FORMATTER);
                        double amount = Double.parseDouble(values[3]);
                        entries.add(new CollectionEntry(entryId, clientId, collectionDate, amount));
                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing date in CSV line: " + line + ". Error: " + e.getMessage());
                        // Skip this row or handle as per specific error handling policy
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing amount in CSV line: " + line + ". Error: " + e.getMessage());
                        // Skip this row or handle
                    }
                } else {
                    System.err.println("Skipping malformed CSV line: " + line);
                }
            }
        }
        return entries;
    }
}
