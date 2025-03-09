package main.java.tamk.tehtava;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Today {
    public static void main(String[] args) {
        // Gets the singleton manager. Later calls to getInstance
        // will return the same reference.
        EventManager manager = EventManager.getInstance();

        // Add a CSV event provider that reads from the given file.
        // Replace with a valid path to the events.csv file on your own computer!
        String homeDir = System.getProperty("user.home");
        String fileName = homeDir + "/.today/events.csv";
        manager.addEventProvider(new CSVEventProvider(fileName));
        fileName = homeDir + "/.today/singular-events.csv";
        manager.addEventProvider(new CSVEventProvider(fileName));

        MonthDay today = MonthDay.now();
        List<Event> allEvents = manager.getEventsOfDate(today);
        List<AnnualEvent> annualEvents = new ArrayList<>();
        List<SingularEvent> singularEvents = new ArrayList<>();
        for (Event event : allEvents) {
            if (event instanceof AnnualEvent) {
                annualEvents.add((AnnualEvent) event);
            } else if (event instanceof SingularEvent) {
                singularEvents.add((SingularEvent) event);
            }
        }

        System.out.println("Today:");
        Collections.sort(annualEvents, new AnnualEventComparator());

        for (AnnualEvent a : annualEvents) {
            System.out.printf(
                    "- %s (%s) %n",
                    a.getDescription(),
                    a.getCategory());
        }
        //System.out.printf("%d events%n", annualEvents.size());

        System.out.println("\nToday in history:");
        Collections.sort(singularEvents, new SingularEventComparator());
        Collections.reverse(singularEvents);

        for (SingularEvent s : singularEvents) {
            int year = s.getDate().getYear();
            if (year < 2015) {
                continue;
            }

            System.out.printf(
                    "%d: %s (%s)%n",
                    year,
                    s.getDescription(),
                    s.getCategory());
        }
        //System.out.printf("%d events%n", singularEvents.size());

        // --------------------------------------------------------------------------


        // Testing getFilteredEvents with different filters
        System.out.println("\n--- Testing getFilteredEvents ---");

        // 1. Test: DateFilter - filter events by date (12.24)
        MonthDay testDate = MonthDay.of(3, 24);
        EventFilter dateFilter = new DateFilter(testDate);
        List<Event> eventsByDate = manager.getFilteredEvents(dateFilter);
        System.out.println("---Events filtered only by date--- (" + testDate + "):");
        for (Event event : eventsByDate) {
            System.out.println(event);
        }
        
        // 2. Testing CategoryFilter - filter events, with category "apple/macos"
        Category testCategory = new Category("apple", "macos");
        EventFilter categoryFilter = new CategoryFilter(testCategory);
        List<Event> eventsByCategory = manager.getFilteredEvents(categoryFilter);
        System.out.println("\n---Events filtered only by category--- (" + testCategory + "):");
        for (Event event : eventsByCategory) {
            System.out.println(event);
        }
        
        // 3. Testing DateCategoryFilter - filter events, with date (12.24) and category "apple/macos"
        EventFilter dateCategoryFilter = new DateCategoryFilter(testCategory, testDate);
        List<Event> eventsByDateCategory = manager.getFilteredEvents(dateCategoryFilter);
        System.out.println("\n---Events filtered by date and category--- (" + testDate + ", " + testCategory + "):");
        for (Event event : eventsByDateCategory) {
            System.out.println(event);
        }
    }
}
