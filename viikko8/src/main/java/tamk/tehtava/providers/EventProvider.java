package tamk.tehtava.providers;

import tamk.tehtava.datamodel.Category;
import tamk.tehtava.datamodel.Event;

import java.util.List;
import java.time.MonthDay;

public interface EventProvider {
    List<Event> getEvents();
    List<Event> getEventsOfCategory(Category category);
    List<Event> getEventsOfDate(MonthDay monthDay);
    String getIdentifier();
}