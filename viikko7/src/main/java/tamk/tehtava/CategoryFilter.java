package main.java.tamk.tehtava;

public class CategoryFilter extends EventFilter {
    private Category filterCategory;

    public CategoryFilter(Category filterCategory) {
        this.filterCategory = filterCategory;
    }

    @Override
    public boolean accepts(Event event) {
        //True if the event's category is the same as the filter category
        return event.getCategory().equals(filterCategory);
    }
    
}
