// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package graphtutorial;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.User;

/**
 * Graph Tutorial
 *
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Java Graph Tutorial");
        System.out.println();

        // <LoadSettingsSnippet>
        // Load OAuth settings
        final Properties oAuthProperties = new Properties();
        try {
            oAuthProperties.load(App.class.getResourceAsStream("oAuth.properties"));
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
            return;
        }

        final String appId = oAuthProperties.getProperty("app.id");
        final String[] appScopes = oAuthProperties.getProperty("app.scopes").split(",");
        // </LoadSettingsSnippet>

        // Get an access token
        Authentication.initialize(appId);
        final String accessToken = Authentication.getUserAccessToken(appScopes);

        // Greet the user
        User user = Graph.getUser(accessToken);
        System.out.println("Welcome " + user.displayName);
        System.out.println("Time zone: " + user.mailboxSettings.timeZone);
        System.out.println();

        Scanner input = new Scanner(System.in);

        int choice = -1;

        while (choice != 0) {
            System.out.println("Please choose one of the following options:");
            System.out.println("0. Exit");
            System.out.println("1. Display access token");
            System.out.println("2. View this week's calendar");
            System.out.println("3. Add an event");

            try {
                choice = input.nextInt();
            } catch (InputMismatchException ex) {
                // Skip over non-integer input
            }

            input.nextLine();

            // Process user choice
            switch(choice) {
                case 0:
                    // Exit the program
                    System.out.println("Goodbye...");
                    break;
                case 1:
                    // Display access token
                    System.out.println("Access token: " + accessToken);
                    break;
                case 2:
                    // List the calendar
                    listCalendarEvents(accessToken, user.mailboxSettings.timeZone);
                    break;
                case 3:
                    // Create a new event
                    createEvent(accessToken, user.mailboxSettings.timeZone, input);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }

        input.close();
    }

    // <FormatDateSnippet>
    private static String formatDateTimeTimeZone(DateTimeTimeZone date) {
        LocalDateTime dateTime = LocalDateTime.parse(date.dateTime);

        return dateTime.format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)) +
            " (" + date.timeZone + ")";
    }
    // </FormatDateSnippet>

    // <ListEventsSnippet>
    private static void listCalendarEvents(String accessToken, String timeZone) {
        ZoneId tzId = GraphToIana.getZoneIdFromWindows("Pacific Standard Time");

        // Get midnight of the first day of the week (assumed Sunday)
        // in the user's timezone, then convert to UTC
        ZonedDateTime startOfWeek = ZonedDateTime.now(tzId)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            .truncatedTo(ChronoUnit.DAYS)
            .withZoneSameInstant(ZoneId.of("UTC"));

        // Add 7 days to get the end of the week
        ZonedDateTime endOfWeek = startOfWeek.plusDays(7);

        // Get the user's events
        List<Event> events = Graph.getCalendarView(accessToken,
            startOfWeek, endOfWeek, timeZone);

        System.out.println("Events:");

        for (Event event : events) {
            System.out.println("Subject: " + event.subject);
            System.out.println("  Organizer: " + event.organizer.emailAddress.name);
            System.out.println("  Start: " + formatDateTimeTimeZone(event.start));
            System.out.println("  End: " + formatDateTimeTimeZone(event.end));
        }

        System.out.println();
    }
    // </ListEventsSnippet>

    // <CreateEventSnippet>
    private static void createEvent(String accessToken, String timeZone, Scanner input) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");

        // Prompt for subject
        String subject = "";
        while (subject.isBlank()) {
            System.out.print("Subject (required): ");
            subject = input.nextLine();
        }

        // Prompt for start date/time
        LocalDateTime start = null;
        while (start == null) {
            System.out.print("Start (mm/dd/yyyy hh:mm AM/PM): ");
            String date = input.nextLine();

            try {
                start = LocalDateTime.parse(date, inputFormat);
            } catch (DateTimeParseException dtp) {
                System.out.println("Invalid input, try again.");
            }
        }

        // Prompt for end date/time
        LocalDateTime end = null;
        while (end == null) {
            System.out.print("End (mm/dd/yyyy hh:mm AM/PM): ");
            String date = input.nextLine();

            try {
                end = LocalDateTime.parse(date, inputFormat);
            } catch (DateTimeParseException dtp) {
                System.out.println("Invalid input, try again.");
            }

            if (end.isBefore(start)) {
                System.out.println("End time must be after start time.");
                end = null;
            }
        }

        // Prompt for attendees
        HashSet<String> attendees = new HashSet<String>();
        System.out.print("Would you like to add attendees? (y/n): ");
        if (input.nextLine().trim().toLowerCase().startsWith("y")) {
            String attendee = "";
            do {
                System.out.print("Enter an email address (leave blank to finalize the list): ");
                attendee = input.nextLine();

                if (!attendee.isBlank()) {
                    attendees.add(attendee);
                }
            } while (!attendee.isBlank());
        }

        // Prompt for body
        String body = null;
        System.out.print("Would you like to add a body? (y/n): ");
        if (input.nextLine().trim().toLowerCase().startsWith("y")) {
            System.out.print("Enter a body: ");
            body = input.nextLine();
        }

        // Confirm input
        System.out.println();
        System.out.println("New event:");
        System.out.println("Subject: " + subject);
        System.out.println("Start: " + start.format(inputFormat));
        System.out.println("End: " + end.format(inputFormat));
        System.out.println("Attendees: " + (attendees.size() > 0 ? attendees.toString() : "NONE"));
        System.out.println("Body: " + (body == null ? "NONE" : body));

        System.out.print("Is this correct? (y/n): ");
        if (input.nextLine().trim().toLowerCase().startsWith("y")) {
            Graph.createEvent(accessToken, timeZone, subject, start, end, attendees, body);
            System.out.println("Event created.");
        } else {
            System.out.println("Canceling.");
        }

        System.out.println();
    }
    // </CreateEventSnippet>
}