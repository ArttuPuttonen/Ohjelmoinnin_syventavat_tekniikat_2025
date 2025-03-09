package main.java.tamk.tehtava;

import java.time.MonthDay;

public class DateCategoryFilter extends EventFilter {
    private Category filterCategory;
    private MonthDay filterDate;


    public DateCategoryFilter(Category filterCategory, MonthDay filterDate) {
        this.filterCategory = filterCategory;
        this.filterDate = filterDate;
    }

    @Override
    public boolean accepts(Event event) {
        // True if the event's category is the same as the filter category and the event's date is the same as the filter date
        return event.getMonthDay().equals(filterDate) && event.getCategory().equals(filterCategory);
    }
}
