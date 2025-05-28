package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvDataManagerTest {

    @Test
    void testSaveAndLoadSingleEntry(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("collections_single.csv");
        CsvDataManager dataManager = new CsvDataManager();

        CollectionEntry entry = new CollectionEntry("E001", "C001", LocalDate.of(2023, 1, 15), 100.50);
        dataManager.saveCollectionEntry(entry, csvFile);

        List<CollectionEntry> loadedEntries = dataManager.loadCollectionEntries(csvFile);
        assertNotNull(loadedEntries);
        assertEquals(1, loadedEntries.size());
        CollectionEntry loadedEntry = loadedEntries.get(0);
        assertEquals("E001", loadedEntry.getEntryId());
        assertEquals("C001", loadedEntry.getClientId());
        assertEquals(LocalDate.of(2023, 1, 15), loadedEntry.getCollectionDate());
        assertEquals(100.50, loadedEntry.getAmount());
    }

    @Test
    void testSaveAndLoadMultipleEntries(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("collections_multiple.csv");
        CsvDataManager dataManager = new CsvDataManager();

        CollectionEntry entry1 = new CollectionEntry("E001", "C001", LocalDate.of(2023, 1, 10), 50.0);
        CollectionEntry entry2 = new CollectionEntry("E002", "C002", LocalDate.of(2023, 1, 11), 75.25);
        CollectionEntry entry3 = new CollectionEntry("E003", "C001", LocalDate.of(2023, 1, 12), 25.0);

        dataManager.saveCollectionEntry(entry1, csvFile);
        dataManager.saveCollectionEntry(entry2, csvFile);
        dataManager.saveCollectionEntry(entry3, csvFile);

        List<CollectionEntry> loadedEntries = dataManager.loadCollectionEntries(csvFile);
        assertNotNull(loadedEntries);
        assertEquals(3, loadedEntries.size());

        CollectionEntry loaded1 = loadedEntries.stream().filter(e -> e.getEntryId().equals("E001")).findFirst().orElse(null);
        assertNotNull(loaded1);
        assertEquals("C001", loaded1.getClientId());
        assertEquals(50.0, loaded1.getAmount());

        CollectionEntry loaded2 = loadedEntries.stream().filter(e -> e.getEntryId().equals("E002")).findFirst().orElse(null);
        assertNotNull(loaded2);
        assertEquals("C002", loaded2.getClientId());
        assertEquals(75.25, loaded2.getAmount());

        CollectionEntry loaded3 = loadedEntries.stream().filter(e -> e.getEntryId().equals("E003")).findFirst().orElse(null);
        assertNotNull(loaded3);
        assertEquals("C001", loaded3.getClientId());
        assertEquals(25.0, loaded3.getAmount());
    }

    @Test
    void testLoadFromNonExistentFile(@TempDir Path tempDir) throws IOException {
        Path nonExistentFile = tempDir.resolve("non_existent.csv");
        CsvDataManager dataManager = new CsvDataManager();
        List<CollectionEntry> entries = dataManager.loadCollectionEntries(nonExistentFile);
        assertTrue(entries.isEmpty());
    }

    @Test
    void testLoadFromEmptyFile(@TempDir Path tempDir) throws IOException {
        Path emptyFile = tempDir.resolve("empty.csv");
        Files.createFile(emptyFile);
        CsvDataManager dataManager = new CsvDataManager();
        List<CollectionEntry> entries = dataManager.loadCollectionEntries(emptyFile);
        assertTrue(entries.isEmpty());
    }

    @Test
    void testLoadFromFileWithOnlyHeader(@TempDir Path tempDir) throws IOException {
        Path headerOnlyFile = tempDir.resolve("header_only.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(headerOnlyFile)) {
            writer.write("entryId,clientId,collectionDate,amount");
            writer.newLine();
        }
        CsvDataManager dataManager = new CsvDataManager();
        List<CollectionEntry> entries = dataManager.loadCollectionEntries(headerOnlyFile);
        assertTrue(entries.isEmpty());
    }

    @Test
    void testLoadWithMalformedDate(@TempDir Path tempDir) throws IOException {
        Path malformedFile = tempDir.resolve("malformed_date.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(malformedFile)) {
            writer.write("entryId,clientId,collectionDate,amount\n");
            writer.write("E001,C001,2023-01-15,100.00\n"); // Valid
            writer.write("E002,C002,INVALID-DATE,200.00\n"); // Malformed date
            writer.write("E003,C003,2023-01-17,300.00\n"); // Valid
        }
        CsvDataManager dataManager = new CsvDataManager();
        List<CollectionEntry> entries = dataManager.loadCollectionEntries(malformedFile);
        assertEquals(2, entries.size());
        assertTrue(entries.stream().anyMatch(e -> e.getEntryId().equals("E001")));
        assertTrue(entries.stream().anyMatch(e -> e.getEntryId().equals("E003")));
        assertFalse(entries.stream().anyMatch(e -> e.getEntryId().equals("E002")));
    }

    @Test
    void testLoadWithMalformedAmount(@TempDir Path tempDir) throws IOException {
        Path malformedFile = tempDir.resolve("malformed_amount.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(malformedFile)) {
            writer.write("entryId,clientId,collectionDate,amount\n");
            writer.write("E001,C001,2023-01-15,100.00\n"); // Valid
            writer.write("E002,C002,2023-01-16,NOT_AN_AMOUNT\n"); // Malformed amount
            writer.write("E003,C003,2023-01-17,300.00\n"); // Valid
        }
        CsvDataManager dataManager = new CsvDataManager();
        List<CollectionEntry> entries = dataManager.loadCollectionEntries(malformedFile);
        assertEquals(2, entries.size());
        assertTrue(entries.stream().anyMatch(e -> e.getEntryId().equals("E001")));
        assertTrue(entries.stream().anyMatch(e -> e.getEntryId().equals("E003")));
        assertFalse(entries.stream().anyMatch(e -> e.getEntryId().equals("E002")));
    }
}
