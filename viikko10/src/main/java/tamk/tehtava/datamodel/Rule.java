package tamk.tehtava.datamodel;

import java.time.LocalDate;
import java.time.MonthDay;

public interface Rule {
    MonthDay getMonthDay(int year);
}