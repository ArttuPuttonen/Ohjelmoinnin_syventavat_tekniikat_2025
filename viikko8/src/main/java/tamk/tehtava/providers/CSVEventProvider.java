package tamk.tehtava.providers;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import tamk.tehtava.*;
import tamk.tehtava.datamodel.AnnualEvent;
import tamk.tehtava.datamodel.Category;
import tamk.tehtava.datamodel.Event;
import tamk.tehtava.datamodel.SingularEvent;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides events stored in a CSV file using the OpenCSV library.
 * The CSV file is expected to have three columns: date, description, and category.
 * The events are read and created via the EventFactory.
 */
public class CSVEventProvider implements EventProvider {
    private final List<Event> events;
    private final String identifier;

    /**
     * Constructs a CSVEventProvider.
     *
     * @param fileName   the path to the CSV file.
     * @param identifier the identifier for this event provider.
     */
    public CSVEventProvider(String fileName, String identifier) {
        this.identifier = identifier;
        this.events = new ArrayList<>();

        try {
            CSVReader reader = new CSVReaderBuilder(new FileReader(fileName)).build();
            String[] line;
            while ((line = reader.readNext()) != null) {
                // Create an Event using the EventFactory.
                // Expected CSV columns: date, description, category.
                Event event = EventFactory.makeEvent(line[0], line[1], line[2]);
                this.events.add(event);
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("CSV file '" + fileName + "' not found! " + fnfe.getLocalizedMessage());
        } catch (CsvValidationException cve) {
            System.err.println("Error in CSV file contents: " + cve.getLocalizedMessage());
        } catch (DateTimeParseException dtpe) {
            System.err.println("Error in date format: " + dtpe.getLocalizedMessage());
        } catch (IOException ioe) {
            System.err.println("Error reading CSV file: " + ioe.getLocalizedMessage());
        }
    }

    /**
     * Returns all events read from the CSV file.
     *
     * @return a list of all events.
     */
    @Override
    public List<Event> getEvents() {
        return this.events;
    }

    /**
     * Returns all events that match the specified category.
     *
     * @param category the category to match.
     * @return a list of matching events.
     */
    @Override
    public List<Event> getEventsOfCategory(Category category) {
        List<Event> result = new ArrayList<>();
        for (Event event : this.events) {
            if (event.getCategory().equals(category)) {
                result.add(event);
            }
        }
        return result;
    }

    /**
     * Returns all events that occur on the given month and day.
     *
     * @param monthDay the month and day to match.
     * @return a list of matching events.
     */
    @Override
    public List<Event> getEventsOfDate(MonthDay monthDay) {
        List<Event> result = new ArrayList<>();

        for (Event event : this.events) {
            Month eventMonth;
            int eventDay;
            if (event instanceof SingularEvent) {
                SingularEvent s = (SingularEvent) event;
                eventMonth = s.getDate().getMonth();
                eventDay = s.getDate().getDayOfMonth();
            } else if (event instanceof AnnualEvent) {
                AnnualEvent a = (AnnualEvent) event;
                eventMonth = a.getMonthDay().getMonth();
                eventDay = a.getMonthDay().getDayOfMonth();
            } else {
                throw new UnsupportedOperationException(
                        "Operation not supported for " + event.getClass().getName());
            }
            if (monthDay.getMonth() == eventMonth && monthDay.getDayOfMonth() == eventDay) {
                result.add(event);
            }
        }
        return result;
    }

    /**
     * Returns the identifier of this event provider.
     *
     * @return the provider identifier.
     */
    @Override
    public String getIdentifier() {
        return this.identifier;
    }
}
