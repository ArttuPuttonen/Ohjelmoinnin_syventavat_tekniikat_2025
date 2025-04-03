package tamk.tehtava.commands;

import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import tamk.tehtava.EventManager;
import tamk.tehtava.datamodel.AnnualEvent;
import tamk.tehtava.datamodel.Category;
import tamk.tehtava.datamodel.Event;
import tamk.tehtava.datamodel.SingularEvent;
import tamk.tehtava.datamodel.AnnualEventComparator;
import tamk.tehtava.datamodel.SingularEventComparator;
import tamk.tehtava.filters.DateCategoryFilter;
import tamk.tehtava.filters.DateFilter;
import tamk.tehtava.filters.EventFilter;
import tamk.tehtava.providers.EventProvider;

@Command(name = "listevents", description = "Lists events for a given date with optional category and provider filtering.")
public class ListEvents implements Runnable {

    @Option(names = {"-c", "--category"}, description = "Category of events to list")
    String categoryOptionString;

    @Option(names = {"-d", "--date"}, description = "Date of events to list in the format MM-dd (default is today)")
    String dateOptionString;
    
    @Option(names = {"-p", "--provider"}, description = "Event provider identifier. (Default: standard)")
    String providerIdentifier = "standard";

    @Override
    public void run() {
        // Parse the category option, if provided.
        Category category = null;
        if (this.categoryOptionString != null) {
            try {
                category = Category.parse(this.categoryOptionString);
            } catch (IllegalArgumentException iae) {
                System.err.println("Invalid category string: '" + this.categoryOptionString + "'");
                return;
            }
        }
        
        // Parse the date option; if not provided, default to today's MonthDay.
        MonthDay monthDay;
        if (this.dateOptionString != null) {
            try {
                // Prepend "--" to conform to the expected MonthDay format.
                monthDay = MonthDay.parse("--" + this.dateOptionString);
            } catch (DateTimeParseException dtpe) {
                System.err.println("Invalid date string: '" + this.dateOptionString + "'");
                return;
            }
        } else {
            monthDay = MonthDay.now();
        }
        
        // Create a base filter based on the date (and category if provided).
        EventFilter filter;
        if (category != null) {
            filter = new DateCategoryFilter(monthDay, category);
        } else {
            filter = new DateFilter(monthDay);
        }
        
        EventManager manager = EventManager.getInstance();
        List<Event> filteredEvents = new ArrayList<>();
        
        // If a provider identifier other than the default ("standard") is given, filter providers.
        if (providerIdentifier != null && !providerIdentifier.equalsIgnoreCase("standard")) {
            // Retrieve providers from the EventManager.
            List<EventProvider> matchingProviders = manager.getProviders().stream()
                    .filter(p -> p.getIdentifier().equalsIgnoreCase(providerIdentifier))
                    .collect(Collectors.toList());
            if (matchingProviders.isEmpty()) {
                System.err.println("No event provider found with identifier: " + providerIdentifier);
                return;
            }
            // Retrieve events only from the matching providers.
            for (EventProvider provider : matchingProviders) {
                filteredEvents.addAll(provider.getEventsOfDate(monthDay));
            }
        } else {
            // Otherwise, get filtered events from all providers.
            filteredEvents = manager.getFilteredEvents(filter);
        }
        
        // Separate events into annual and singular lists.
        List<AnnualEvent> annualEvents = new ArrayList<>();
        List<SingularEvent> singularEvents = new ArrayList<>();
        for (Event event : filteredEvents) {
            if (event instanceof AnnualEvent) {
                annualEvents.add((AnnualEvent) event);
            } else if (event instanceof SingularEvent) {
                singularEvents.add((SingularEvent) event);
            }
        }
        
        // Display annual events.
        if (!annualEvents.isEmpty()) {
            System.out.println("Observed today:");
            Collections.sort(annualEvents, new AnnualEventComparator());
            for (AnnualEvent a : annualEvents) {
                System.out.printf("- %s (%s)%n", a.getDescription(), a.getCategory());
            }
        }
        
        // Display singular events.
        if (!singularEvents.isEmpty()) {
            System.out.println("\nToday in history:");
            Collections.sort(singularEvents, new SingularEventComparator());
            Collections.reverse(singularEvents);
            for (SingularEvent s : singularEvents) {
                int year = s.getDate().getYear();
                System.out.printf("%d: %s (%s)%n", year, s.getDescription(), s.getCategory());
            }
        }
    }
}


