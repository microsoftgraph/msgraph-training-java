<!-- markdownlint-disable MD002 MD041 -->

In this section you will add the ability to create events on the user's calendar.

1. Open **./graphtutorial/src/main/java/graphtutorial/Graph.java** and add the following function to the **Graph** class.

    :::code language="java" source="../demo/graphtutorial/src/main/java/graphtutorial/Graph.java" id="CreateEventSnippet":::

1. Open **./graphtutorial/src/main/java/graphtutorial/App.java** and add the following function to the **App** class.

    :::code language="java" source="../demo/graphtutorial/src/main/java/graphtutorial/App.java" id="CreateEventSnippet":::

    This function prompts the user for subject, attendees, start, end, and body, then uses those values to call `Graph.createEvent`.

1. Add the following just after the `// Create a new event` comment in the `Main` function.

    ```java
    createEvent(user.mailboxSettings.timeZone, input);
    ```

1. Save all of your changes and run the app. Choose the **Add an event** option. Respond to the prompts to create a new event on the user's calendar.
