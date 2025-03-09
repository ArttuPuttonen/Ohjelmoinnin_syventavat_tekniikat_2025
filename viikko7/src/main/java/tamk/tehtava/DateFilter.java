package main.java.tamk.tehtava;
import java.time.MonthDay;

public class DateFilter extends EventFilter {
    private MonthDay filterDate;
    private Integer year; // Integer instead of int, because it can be null

    //Constructor with year
    public DateFilter(MonthDay filterDate, Integer year) {
        this.filterDate = filterDate;
        this.year = year;
    }

    //Constructor without year
    public DateFilter(MonthDay filterDate) {
        this(filterDate, null);
    }

    @Override
    public boolean accepts(Event event) {
        if (event instanceof SingularEvent) {
            // If event is SingularEvent, cast it to SingularEvent
            SingularEvent singular = (SingularEvent) event;
            if (this.year != null) {
                // If year is defined in the filter, compare the year in addition to the month and day.
                return singular.getMonthDay().equals(filterDate) && singular.getYear() == year;
            } else {
                // If year is not defined in the filter, compare only the month and day.
                return singular.getMonthDay().equals(filterDate);
            }
        } else if (event instanceof AnnualEvent) {
            // If event is AnnualEvent, cast it to AnnualEvent
            AnnualEvent annual = (AnnualEvent) event;
            return annual.getMonthDay().equals(filterDate);
        } else {
            // If event is neither SingularEvent nor AnnualEvent, for example new subclass of Event are added in the future..
            return event.getMonthDay().equals(filterDate);
        }
    }

}