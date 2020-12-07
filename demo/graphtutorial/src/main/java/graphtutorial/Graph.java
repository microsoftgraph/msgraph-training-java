// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package graphtutorial;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.Attendee;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.EmailAddress;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.ItemBody;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.models.generated.AttendeeType;
import com.microsoft.graph.models.generated.BodyType;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IEventCollectionPage;
import com.microsoft.graph.requests.extensions.IEventCollectionRequestBuilder;

/**
 * Graph
 */
public class Graph {

    private static IGraphServiceClient graphClient = null;
    private static SimpleAuthProvider authProvider = null;

    private static void ensureGraphClient(String accessToken) {
        if (graphClient == null) {
            // Create the auth provider
            authProvider = new SimpleAuthProvider(accessToken);

            // Create default logger to only log errors
            DefaultLogger logger = new DefaultLogger();
            logger.setLoggingLevel(LoggerLevel.ERROR);

            // Build a Graph client
            graphClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .logger(logger)
                .buildClient();
        }
    }

    public static User getUser(String accessToken) {
        ensureGraphClient(accessToken);

        // GET /me to get authenticated user
        User me = graphClient
            .me()
            .buildRequest()
            .select("displayName,mailboxSettings")
            .get();

        return me;
    }

    // <GetEventsSnippet>
    public static List<Event> getCalendarView(String accessToken,
        ZonedDateTime viewStart, ZonedDateTime viewEnd, String timeZone) {
        ensureGraphClient(accessToken);

        List<Option> options = new LinkedList<Option>();
        options.add(new QueryOption("startDateTime", viewStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        options.add(new QueryOption("endDateTime", viewEnd.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        // Sort results by start time
        options.add(new QueryOption("$orderby", "start/dateTime"));

        // Start and end times adjusted to user's time zone
        options.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timeZone + "\""));

        // GET /me/events
        IEventCollectionPage eventPage = graphClient
            .me()
            .calendarView()
            .buildRequest(options)
            .select("subject,organizer,start,end")
            .top(25)
            .get();

        List<Event> allEvents = new LinkedList<Event>();

        // Create a separate list of options for the paging requests
        // paging request should not include the query parameters from the initial
        // request, but should include the headers.
        List<Option> pagingOptions = new LinkedList<Option>();
        pagingOptions.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timeZone + "\""));

        while (eventPage != null) {
            allEvents.addAll(eventPage.getCurrentPage());

            IEventCollectionRequestBuilder nextPage =
                eventPage.getNextPage();

            if (nextPage == null) {
                break;
            } else {
                eventPage = nextPage
                    .buildRequest(pagingOptions)
                    .get();
            }
        }

        return allEvents;
    }
    // </GetEventsSnippet>

    // <CreateEventSnippet>
    public static void createEvent(
        String accessToken,
        String timeZone,
        String subject,
        LocalDateTime start,
        LocalDateTime end,
        Set<String> attendees,
        String body)
    {
        ensureGraphClient(accessToken);

        Event newEvent = new Event();

        newEvent.subject = subject;

        newEvent.start = new DateTimeTimeZone();
        newEvent.start.dateTime = start.toString();
        newEvent.start.timeZone = timeZone;

        newEvent.end = new DateTimeTimeZone();
        newEvent.end.dateTime = end.toString();
        newEvent.end.timeZone = timeZone;

        if (attendees != null && !attendees.isEmpty()) {
            newEvent.attendees = new LinkedList<Attendee>();

            attendees.forEach((email) -> {
                Attendee attendee = new Attendee();
                // Set each attendee as required
                attendee.type = AttendeeType.REQUIRED;
                attendee.emailAddress = new EmailAddress();
                attendee.emailAddress.address = email;
                newEvent.attendees.add(attendee);
            });
        }

        if (body != null) {
            newEvent.body = new ItemBody();
            newEvent.body.content = body;
            // Treat body as plain text
            newEvent.body.contentType = BodyType.TEXT;
        }

        // POST /me/events
        graphClient
            .me()
            .events()
            .buildRequest()
            .post(newEvent);
    }
    // </CreateEventSnippet>
}