package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportUtilTest {

    private final CollectionEntry entry1 = new CollectionEntry("E1", "C1", LocalDate.of(2023, 10, 15), 100.0); // Sunday
    private final CollectionEntry entry2 = new CollectionEntry("E2", "C2", LocalDate.of(2023, 10, 16), 50.0);  // Monday
    private final CollectionEntry entry3 = new CollectionEntry("E3", "C1", LocalDate.of(2023, 10, 21), 75.0);  // Saturday
    private final CollectionEntry entry4 = new CollectionEntry("E4", "C3", LocalDate.of(2023, 10, 22), 125.0); // Sunday
    private final CollectionEntry entry5 = new CollectionEntry("E5", "C2", LocalDate.of(2023, 11, 1), 200.0);  // Different month
    private final CollectionEntry entry6 = new CollectionEntry("E6", "C1", LocalDate.of(2023, 10, 15), 30.0); // Sunday, same day as E1

    private final List<CollectionEntry> entries = Arrays.asList(entry1, entry2, entry3, entry4, entry5, entry6);

    @Test
    void testCalculateDailyTotal() {
        assertEquals(130.0, ReportUtil.calculateDailyTotal(entries, LocalDate.of(2023, 10, 15)), 0.01);
        assertEquals(50.0, ReportUtil.calculateDailyTotal(entries, LocalDate.of(2023, 10, 16)), 0.01);
        assertEquals(0.0, ReportUtil.calculateDailyTotal(entries, LocalDate.of(2023, 1, 1)), 0.01); // No entries for this date
        assertEquals(0.0, ReportUtil.calculateDailyTotal(new ArrayList<>(), LocalDate.of(2023, 10, 15)), 0.01); // Empty list
        assertEquals(0.0, ReportUtil.calculateDailyTotal(null, LocalDate.of(2023, 10, 15)), 0.01); // Null list
        assertEquals(0.0, ReportUtil.calculateDailyTotal(entries, null), 0.01); // Null date
    }

    @Test
    void testCalculateWeekendTotal() {
        // Weekend of Oct 14-15: entry1 (100) + entry6 (30) = 130
        assertEquals(130.0, ReportUtil.calculateWeekendTotal(entries, LocalDate.of(2023, 10, 14), LocalDate.of(2023, 10, 15)), 0.01);
        // Weekend of Oct 21-22: entry3 (75) + entry4 (125) = 200
        assertEquals(200.0, ReportUtil.calculateWeekendTotal(entries, LocalDate.of(2023, 10, 21), LocalDate.of(2023, 10, 22)), 0.01);
        // Range covering both weekends
        assertEquals(330.0, ReportUtil.calculateWeekendTotal(entries, LocalDate.of(2023, 10, 14), LocalDate.of(2023, 10, 22)), 0.01);
        // Range with only one weekend day (Saturday Oct 21)
        assertEquals(75.0, ReportUtil.calculateWeekendTotal(entries, LocalDate.of(2023, 10, 21), LocalDate.of(2023, 10, 21)), 0.01);
        // Range with no weekend days
        assertEquals(0.0, ReportUtil.calculateWeekendTotal(entries, LocalDate.of(2023, 10, 16), LocalDate.of(2023, 10, 20)), 0.01);
        // Range including entry2 (Monday) and entry3 (Saturday) - only entry3 should count
        assertEquals(75.0, ReportUtil.calculateWeekendTotal(entries, LocalDate.of(2023, 10, 16), LocalDate.of(2023, 10, 21)), 0.01);
        // Empty list
        assertEquals(0.0, ReportUtil.calculateWeekendTotal(new ArrayList<>(), LocalDate.of(2023, 10, 14), LocalDate.of(2023, 10, 15)), 0.01);
        // Null list
        assertEquals(0.0, ReportUtil.calculateWeekendTotal(null, LocalDate.of(2023, 10, 14), LocalDate.of(2023, 10, 15)), 0.01);
        // Null dates
        assertEquals(0.0, ReportUtil.calculateWeekendTotal(entries, null, LocalDate.of(2023, 10, 15)), 0.01);
        assertEquals(0.0, ReportUtil.calculateWeekendTotal(entries, LocalDate.of(2023, 10, 14), null), 0.01);
        assertEquals(0.0, ReportUtil.calculateWeekendTotal(entries, null, null), 0.01);
        // Start date after end date
        assertEquals(0.0, ReportUtil.calculateWeekendTotal(entries, LocalDate.of(2023, 10, 15), LocalDate.of(2023, 10, 14)), 0.01);
    }

    @Test
    void testCalculateMonthlyTotal() {
        // October 2023: entry1 (100) + entry2 (50) + entry3 (75) + entry4 (125) + entry6 (30) = 380
        assertEquals(380.0, ReportUtil.calculateMonthlyTotal(entries, YearMonth.of(2023, 10)), 0.01);
        // November 2023: entry5 (200)
        assertEquals(200.0, ReportUtil.calculateMonthlyTotal(entries, YearMonth.of(2023, 11)), 0.01);
        // December 2023: No entries
        assertEquals(0.0, ReportUtil.calculateMonthlyTotal(entries, YearMonth.of(2023, 12)), 0.01);
        // Empty list
        assertEquals(0.0, ReportUtil.calculateMonthlyTotal(new ArrayList<>(), YearMonth.of(2023, 10)), 0.01);
        // Null list
        assertEquals(0.0, ReportUtil.calculateMonthlyTotal(null, YearMonth.of(2023, 10)), 0.01);
        // Null YearMonth
        assertEquals(0.0, ReportUtil.calculateMonthlyTotal(entries, null), 0.01);
    }
}
