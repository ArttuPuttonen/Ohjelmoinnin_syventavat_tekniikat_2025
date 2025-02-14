
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.MonthDay;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CSVEventProvider implements EventProvider {
    private List<Event> events;

    public CSVEventProvider(String fileName) {
        this.events = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(Paths.get(fileName))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord record : records) {
                Event event = createEventFromRecord(record);
                events.add(event);
            }
            System.out.printf("Luettiin %d tapahtumaa CSV-tiedostosta%n", events.size());
        } catch (IOException ioe) {
            System.err.println("Tiedostoa '" + fileName + "' ei löytynyt");
        }
}

    private Event createEventFromRecord(CSVRecord record) {
        LocalDate date = LocalDate.parse(record.get(0));
        String description = record.get(1);
        String categoryString = record.get(2);
    
        Category category = new Category(categoryString, null);
    
        return new Event(date, description, category);
}

    @Override
    public List<Event> getEvents() {
        return this.events;
    }

    @Override
    public List<Event> getEventsOfCategory(Category category) {
        List<Event> result = new ArrayList<Event>();
        for (Event event : this.events) {
            if (event.getCategory().equals(category)) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public List<Event> getEventsOfDate(MonthDay monthDay) {
        List<Event> result = new ArrayList<Event>();

        for (Event event : this.events) {
            final Month eventMonth = event.getDate().getMonth();
            final int eventDay = event.getDate().getDayOfMonth();
            if (monthDay.getMonth() == eventMonth && monthDay.getDayOfMonth() == eventDay) {
                result.add(event);
            }
        }

        return result;
    }

    private Event makeEvent(String row) {
        String[] parts = row.split(",");
        LocalDate date = LocalDate.parse(parts[0]);
        String description = parts[1];
        String categoryString = parts[2];
        String[] categoryParts = categoryString.split("/");
        String primary = categoryParts[0];
        String secondary = null;
        if (categoryParts.length == 2) {
            secondary = categoryParts[1];
        }
        Category category = new Category(primary, secondary);
        return new Event(date, description, category);
    }
}