package viikko3;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Arrays;

public class tehtava3 {
    public static void main(String[] args) {

        // All events share the same category: "apple/macos", that 
        // should be used in the output.
        Category cat = new Category("apple", "macos");

        // Create an array of Event objects all at once, not one by one.
        Event[] events = {
            new Event(LocalDate.of(2024, 9, 16),  "macOS 15 Sequoia released", cat),
            new Event(LocalDate.of(2023, 9, 26),  "macOS 14 Sonoma released",  cat),
            new Event(LocalDate.of(2022, 10, 24), "macOS 13 Ventura released", cat),
            new Event(LocalDate.of(2021, 10, 25), "macOS 12 Monterey released",cat),
            new Event(LocalDate.of(2020, 11, 12), "macOS 11 Big Sur released", cat)
        };

        // 1. Go through the array, extract the version number,
        //    the name, and the day of the week.
        //    Then print in the required format.
        for (Event e : events) {

            // Edit the description to get the version and name
            String description = e.getDescription(); 
            String middle = description.substring(6, description.indexOf(" released"));
            String[] parts = middle.split(" ", 2);
            String version = parts[0];
            String name = parts[1];

            // Day of week in Title Case, e.g. MONDAY -> Monday
            DayOfWeek dow = e.getDate().getDayOfWeek(); 
            String weekday = dow.toString().substring(0,1) + 
                             dow.toString().substring(1).toLowerCase();

            // Print in the required format
            System.out.println("macOS " + version + " " + name 
                               + " was released on a " + weekday);
        }

        // 2. Gather all OS names into a String[] array.
        String[] names = new String[events.length];
        for (int i = 0; i < events.length; i++) {
            // "macOS 15 Sequoia released" => only "Sequoia"
            String desc = events[i].getDescription();
            String middle = desc.substring(6, desc.indexOf(" released"));

            String[] parts = middle.split(" ", 2);
            names[i] = parts[1];
        }

        // Sort names alphabetically
        Arrays.sort(names);

        // Print them as a single string in the required format
        System.out.println("In alphabetical order: " + Arrays.toString(names) + ".");
    }
}
