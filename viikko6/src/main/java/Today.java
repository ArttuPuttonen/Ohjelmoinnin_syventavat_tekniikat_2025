package viikko6.src.main.java;

import java.sql.Array;
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
        final String fileName = System.getProperty("user.home") + "/.today/events.csv";
        manager.addEventProvider(new CSVEventProvider(fileName));
        manager.addEventProvider(new CSVEventProvider(fileName));
        manager.addEventProvider(new CSVEventProvider(fileName));
        // It is possible to add multiple providers,
        // even the same ones -- EventManager does not check it yet!

        List<Event> events = manager.getAllEvents();

        int providerCount = manager.getEventProviderCount();
        int eventCount = events.size();
        System.out.printf("Manager has %d event provider(s),%n", providerCount);
        System.out.printf("with a total of %d events.%n", eventCount);

        List<Event> result = new ArrayList<>();
        final MonthDay monthDay = MonthDay.of(2, 11);
        for (Event event : events) {
            final Month eventMonth = event.getDate().getMonth();
            final int eventDay = event.getDate().getDayOfMonth();
            if (monthDay.getMonth() == eventMonth && monthDay.getDayOfMonth() == eventDay) {
                result.add(event);
            }
        }

        for (Event event : result) {
            System.out.println(event);
        }
    }
}