// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package graphtutorial;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import okhttp3.Request;

import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;

import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.EventCollectionRequestBuilder;

/**
 * Graph
 */
public class Graph {

    private static GraphServiceClient<Request> graphClient = null;
    private static TokenCredentialAuthProvider authProvider = null;

    public static void initializeGraphAuth(String applicationId, List<String> scopes) {
        // Create the auth provider
        final DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
            .clientId(applicationId)
            .challengeConsumer(challenge -> System.out.println(challenge.getMessage()))
            .build();

        authProvider = new TokenCredentialAuthProvider(scopes, credential);

        // Create default logger to only log errors
        DefaultLogger logger = new DefaultLogger();
        logger.setLoggingLevel(LoggerLevel.ERROR);

        // Build a Graph client
        graphClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider)
            .logger(logger)
            .buildClient();
    }

    public static String getUserAccessToken()
    {
        try {
            URL meUrl = new URL("https://graph.microsoft.com/v1.0/me");
            return authProvider.getAuthorizationTokenAsync(meUrl).get();
        } catch(Exception ex) {
            return null;
        }
    }

    // <GetUserSnippet>
    public static User getUser() {
        if (graphClient == null) throw new NullPointerException(
            "Graph client has not been initialized. Call initializeGraphAuth before calling this method");

        // GET /me to get authenticated user
        User me = graphClient
            .me()
            .buildRequest()
            .select("displayName,mailboxSettings")
            .get();

        return me;
    }
    // </GetUserSnippet>

    // <GetEventsSnippet>
    public static List<Event> getCalendarView(
        ZonedDateTime viewStart, ZonedDateTime viewEnd, String timeZone) {
        if (graphClient == null) throw new NullPointerException(
            "Graph client has not been initialized. Call initializeGraphAuth before calling this method");

        List<Option> options = new LinkedList<Option>();
        options.add(new QueryOption("startDateTime", viewStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        options.add(new QueryOption("endDateTime", viewEnd.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        // Sort results by start time
        options.add(new QueryOption("$orderby", "start/dateTime"));

        // Start and end times adjusted to user's time zone
        options.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timeZone + "\""));

        // GET /me/events
        EventCollectionPage eventPage = graphClient
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

            EventCollectionRequestBuilder nextPage =
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
        String timeZone,
        String subject,
        LocalDateTime start,
        LocalDateTime end,
        Set<String> attendees,
        String body)
    {
        if (graphClient == null) throw new NullPointerException(
            "Graph client has not been initialized. Call initializeGraphAuth before calling this method");

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