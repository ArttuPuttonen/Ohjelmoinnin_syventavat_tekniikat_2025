package tamk.tehtava.providers.web;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import tamk.tehtava.providers.EventProvider;
import tamk.tehtava.datamodel.Category;
import tamk.tehtava.datamodel.Event;

/**
 * WebEventProvider fetches events from a remote server via HTTP(S).
 * The constructor takes a URI for the server base URL.
 * The events are fetched from the "api/v1/events" endpoint by appending a query parameter "date".
 * The date is provided as a MonthDay and converted to "MM-dd" format.
 */
public class WebEventProvider implements EventProvider {

    private final URI baseUri;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a WebEventProvider with the given server URI.
     * The URI is resolved with the endpoint "api/v1/events".
     *
     * @param serverUri the base URI of the server
     */
    public WebEventProvider(URI serverUri) {
        // Resolve the endpoint relative to the provided serverUri.
        this.baseUri = serverUri.resolve("api/v1/events");
        this.httpClient = HttpClient.newHttpClient();
        // Create and configure ObjectMapper, registering the custom EventDeserializer.
        this.objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("EventDeserializer");
        module.addDeserializer(Event.class, new EventDeserializer());
        this.objectMapper.registerModule(module);
    }

    /**
     * This method is not supported.
     *
     * @return never returns normally
     */
    @Override
    public List<Event> getEvents() {
        return getEventsOfDate(MonthDay.now());
    }

    /**
     * Fetches events for the specified MonthDay.
     * The MonthDay is converted to a string in "MM-dd" format (by removing any leading "--")
     * and passed as a query parameter "date" to the endpoint.
     *
     * @param monthDay the month and day to match
     * @return list of matching events
     */
    @Override
    public List<Event> getEventsOfDate(MonthDay monthDay) {
        // Convert MonthDay to a string (expected format is "--MM-dd")
        String dateParam = monthDay.toString();
        if (dateParam.startsWith("--")) {
            dateParam = dateParam.substring(2);  // Remove leading dashes to get "MM-dd"
        }
        // Construct the request URI with the query parameter "date"
        URI requestUri = URI.create(baseUri.toString() + "?date=" + dateParam);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(requestUri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                // Deserialize JSON response into a list of Event objects
                return objectMapper.readValue(body, new TypeReference<List<Event>>() {});
            } else {
                System.err.println("HTTP error: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
        }
        // Fallback: return an empty list if errors occur.
        return new ArrayList<>();
    }

    /**
     * Returns events that match the specified category by filtering the events
     * fetched for today's MonthDay. (This is a simple filtering implementation.)
     *
     * @param category the category to match
     * @return list of matching events
     */
    @Override
    public List<Event> getEventsOfCategory(Category category) {
        // For demonstration, we fetch events for today's MonthDay and filter by category.
        MonthDay today = MonthDay.now();
        List<Event> events = getEventsOfDate(today);
        List<Event> filtered = new ArrayList<>();
        for (Event event : events) {
            if (event.getCategory().equals(category)) {
                filtered.add(event);
            }
        }
        return filtered;
    }

    /**
     * Returns the identifier for this event provider.
     *
     * @return the provider identifier "web"
     */
    @Override
    public String getIdentifier() {
        return "web";
    }
}

