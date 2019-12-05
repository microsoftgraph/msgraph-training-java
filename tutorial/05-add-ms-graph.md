<!-- markdownlint-disable MD002 MD041 -->

In this exercise you will incorporate the Microsoft Graph into the application. For this application, you will use the [Microsoft Graph SDK for Java](https://github.com/microsoftgraph/msgraph-sdk-java) to make calls to Microsoft Graph.

## Implement an authentication provider

The Microsoft Graph SDK for Java requires an implementation of the `IAuthenticationProvider` interface to instantiate its `GraphServiceClient` object. Start by creating a simple class to add the access token to outgoing requests. Create a new file in the **./graphtutorial/src/main/java/com/contoso** directory named **SimpleAuthProvider.java** and add the following code.

```java
package com.contoso;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.http.IHttpRequest;

/**
 * SimpleAuthProvider
 */
public class SimpleAuthProvider implements IAuthenticationProvider {

    private String accessToken = null;

    public SimpleAuthProvider(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void authenticateRequest(IHttpRequest request) {
        // Add the access token in the Authorization header
        request.addHeader("Authorization", "Bearer " + accessToken);
    }
}
```

## Get user details

First, add a new class to contain all of the Graph functionality. Create a new file in the **./graphtutorial/src/main/java/com/contoso** directory named **Graph.java** and add the following code.

```java
package com.contoso;

import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import java.util.LinkedList;
import java.util.List;import com.microsoft.graph.models.extensions.Event;import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
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
            .get();

        return me;
    }
}
```

Add the following code in **App.java** just before the `Scanner input = new Scanner(System.in);` line to get the user and output the user's display name.

```java
// Greet the user
User user = Graph.getUser(accessToken);
System.out.println("Welcome " + user.displayName);
System.out.println();
```

If you run the app now, after you log in the app welcomes you by name.

## Get calendar events from Outlook

Add the following `import` statements to **Graph.java**.

```java
import java.util.LinkedList;
import java.util.List;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.IEventCollectionPage;
```

Add the following function to the `Graph` class in **Graph.java** to get events from the user's calendar.

```java
public static List<Event> getEvents(String accessToken) {
    ensureGraphClient(accessToken);

    // Use QueryOption to specify the $orderby query parameter
    final List<Option> options = new LinkedList<Option>();
    // Sort results by createdDateTime, get newest first
    options.add(new QueryOption("orderby", "createdDateTime DESC"));

    // GET /me/events
    IEventCollectionPage eventPage = graphClient
        .me()
        .events()
        .buildRequest(options)
        .select("subject,organizer,start,end")
        .get();

    return eventPage.getCurrentPage();
}
```

Consider what this code is doing.

- The URL that will be called is `/me/events`.
- The `select` function limits the fields returned for each event to just those the app will actually use.
- A `QueryOption` is used to sort the results by the date and time they were created, with the most recent item being first.

## Display the results

Start by adding the following `import` statements in **App.java**.

```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
```

Then add the following function to the `App` class to format the [dateTimeTimeZone](/graph/api/resources/datetimetimezone?view=graph-rest-1.0) properties from Microsoft Graph into a user-friendly format.

```java
private static String formatDateTimeTimeZone(DateTimeTimeZone date) {
    LocalDateTime dateTime = LocalDateTime.parse(date.dateTime);

    return dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)) + " (" + date.timeZone + ")";
}
```

Next, add the following function to the `App` class to get the user's events and output them to the console.

```java
private static void listCalendarEvents(String accessToken) {
    // Get the user's events
    List<Event> events = Graph.getEvents(accessToken);

    System.out.println("Events:");

    for (Event event : events) {
        System.out.println("Subject: " + event.subject);
        System.out.println("  Organizer: " + event.organizer.emailAddress.name);
        System.out.println("  Start: " + formatDateTimeTimeZone(event.start));
        System.out.println("  End: " + formatDateTimeTimeZone(event.end));
    }

    System.out.println();
}
```

Finally, add the following just after the `// List the calendar` comment in the `main` function.

```java
listCalendarEvents(accessToken);
```

Save all of your changes and run the app. Choose the **List calendar events** option to see a list of the user's events.

```Shell
Welcome Adele Vance

Please choose one of the following options:
0. Exit
1. Display access token
2. List calendar events
2
Events:
Subject: Team meeting
  Organizer: Adele Vance
  Start: 5/22/19, 3:00 PM (UTC)
  End: 5/22/19, 4:00 PM (UTC)
Subject: Team Lunch
  Organizer: Adele Vance
  Start: 5/24/19, 6:30 PM (UTC)
  End: 5/24/19, 8:00 PM (UTC)
Subject: Flight to Redmond
  Organizer: Adele Vance
  Start: 5/26/19, 4:30 PM (UTC)
  End: 5/26/19, 7:00 PM (UTC)
Subject: Let's meet to discuss strategy
  Organizer: Patti Fernandez
  Start: 5/27/19, 10:00 PM (UTC)
  End: 5/27/19, 10:30 PM (UTC)
Subject: All-hands meeting
  Organizer: Adele Vance
  Start: 5/28/19, 3:30 PM (UTC)
  End: 5/28/19, 5:00 PM (UTC)
```
