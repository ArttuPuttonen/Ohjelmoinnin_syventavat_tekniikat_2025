package viikko6.src.main.java;

public interface TodayRelatable {
    public enum Relation {
        BEFORE_TODAY, TODAY, AFTER_TODAY
    }

    Relation getTodayRelation();
    long getTodayDifference();
}