package com.example;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class ReportUtil {

    public static double calculateDailyTotal(List<CollectionEntry> entries, LocalDate date) {
        if (entries == null || date == null) {
            return 0.0;
        }
        return entries.stream()
                .filter(entry -> entry.getCollectionDate().equals(date))
                .mapToDouble(CollectionEntry::getAmount)
                .sum();
    }

    public static double calculateWeekendTotal(List<CollectionEntry> entries, LocalDate startDate, LocalDate endDate) {
        if (entries == null || startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return 0.0;
        }
        return entries.stream()
                .filter(entry -> {
                    LocalDate entryDate = entry.getCollectionDate();
                    DayOfWeek day = entryDate.getDayOfWeek();
                    return (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) &&
                           !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate);
                })
                .mapToDouble(CollectionEntry::getAmount)
                .sum();
    }

    public static double calculateMonthlyTotal(List<CollectionEntry> entries, YearMonth yearMonth) {
        if (entries == null || yearMonth == null) {
            return 0.0;
        }
        return entries.stream()
                .filter(entry -> YearMonth.from(entry.getCollectionDate()).equals(yearMonth))
                .mapToDouble(CollectionEntry::getAmount)
                .sum();
    }
}
