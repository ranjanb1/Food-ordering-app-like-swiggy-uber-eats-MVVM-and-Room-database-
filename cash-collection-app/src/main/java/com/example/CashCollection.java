package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CashCollection {
    private List<Client> clients;
    private List<CollectionEntry> entries;

    public CashCollection() {
        this.clients = new ArrayList<>();
        this.entries = new ArrayList<>();
    }

    // Client management
    public void addClient(Client client) {
        if (clients.stream().anyMatch(c -> c.getClientId().equals(client.getClientId()))) {
            throw new IllegalArgumentException("Client with ID " + client.getClientId() + " already exists.");
        }
        clients.add(client);
    }

    public Optional<Client> getClientById(String clientId) {
        return clients.stream()
                .filter(client -> client.getClientId().equals(clientId))
                .findFirst();
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients);
    }

    // Collection entry management
    public String addCollectionEntry(String clientId, LocalDate collectionDate, double amount) {
        if (clients.stream().noneMatch(c -> c.getClientId().equals(clientId))) {
            throw new IllegalArgumentException("Client with ID " + clientId + " not found.");
        }
        String entryId = UUID.randomUUID().toString();
        CollectionEntry newEntry = new CollectionEntry(entryId, clientId, collectionDate, amount);
        entries.add(newEntry);
        return entryId;
    }

    public Optional<CollectionEntry> getCollectionEntryById(String entryId) {
        return entries.stream()
                .filter(entry -> entry.getEntryId().equals(entryId))
                .findFirst();
    }

    public List<CollectionEntry> getEntriesByClient(String clientId) {
        return entries.stream()
                .filter(entry -> entry.getClientId().equals(clientId))
                .collect(Collectors.toList());
    }

    public List<CollectionEntry> getEntriesByDate(LocalDate date) {
        return entries.stream()
                .filter(entry -> entry.getCollectionDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<CollectionEntry> getAllCollectionEntries() {
        return new ArrayList<>(entries);
    }

    public boolean removeCollectionEntry(String entryId) {
        return entries.removeIf(entry -> entry.getEntryId().equals(entryId));
    }

    public double getTotalCollectionAmount() {
        return entries.stream().mapToDouble(CollectionEntry::getAmount).sum();
    }

    public double getTotalCollectionAmountByClient(String clientId) {
        return entries.stream()
                .filter(entry -> entry.getClientId().equals(clientId))
                .mapToDouble(CollectionEntry::getAmount)
                .sum();
    }
}
