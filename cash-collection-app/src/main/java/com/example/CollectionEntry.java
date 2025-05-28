package com.example;

import java.time.LocalDate;

public class CollectionEntry {
    private String entryId;
    private String clientId;
    private LocalDate collectionDate;
    private double amount;

    public CollectionEntry(String entryId, String clientId, LocalDate collectionDate, double amount) {
        this.entryId = entryId;
        this.clientId = clientId;
        this.collectionDate = collectionDate;
        this.amount = amount;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(LocalDate collectionDate) {
        this.collectionDate = collectionDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
