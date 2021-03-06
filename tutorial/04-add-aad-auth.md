<!-- markdownlint-disable MD002 MD041 -->

In this exercise you will extend the application from the previous exercise to support authentication with Azure AD. This is required to obtain the necessary OAuth access token to call the Microsoft Graph. In this step you will integrate the [Microsoft Authentication Library (MSAL) for Java](https://github.com/AzureAD/microsoft-authentication-library-for-java) into the application.

1. Create a new directory named **graphtutorial** in the **./src/main/resources** directory.

1. Create a new file in the **./src/main/resources/graphtutorial** directory named **oAuth.properties**, and add the following text in that file. Replace `YOUR_APP_ID_HERE` with the application ID you created in the Azure portal.

    :::code language="ini" source="../demo/graphtutorial/src/main/resources/graphtutorial/oAuth.properties.example":::

    The value of `app.scopes` contains the permission scopes the application requires.

    - **User.Read** allows the app to access the user's profile.
    - **MailboxSettings.Read** allows the app to access settings from the user's mailbox, including the user's configured time zone.
    - **Calendars.ReadWrite** allows the app to list the user's calendar and add new events to the calendar.

    > [!IMPORTANT]
    > If you're using source control such as git, now would be a good time to exclude the **oAuth.properties** file from source control to avoid inadvertently leaking your app ID.

1. Open **App.java** and add the following `import` statements.

    ```java
    import java.io.IOException;
    import java.util.Properties;
    ```

1. Add the following code just before the `Scanner input = new Scanner(System.in);` line to load the **oAuth.properties** file.

    :::code language="java" source="../demo/graphtutorial/src/main/java/graphtutorial/App.java" id="LoadSettingsSnippet":::

## Implement sign-in

1. Create a new file in the **./graphtutorial/src/main/java/graphtutorial** directory named **Graph.java** and add the following code.

    ```java
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
    }
    ```

1. In **App.java**, add the following code just before the `Scanner input = new Scanner(System.in);` line to get an access token.

    ```java
    // Initialize Graph with auth settings
    Graph.initializeGraphAuth(appId, appScopes);
    final String accessToken = Graph.getUserAccessToken();
    ```

1. Add the following line after the `// Display access token` comment.

    ```java
    System.out.println("Access token: " + accessToken);
    ```

1. Run the app. The application displays a URL and device code.

    ```Shell
    Java Graph Tutorial

    To sign in, use a web browser to open the page https://microsoft.com/devicelogin and enter the code F7CG945YZ to authenticate.
    ```

1. Open a browser and browse to the URL displayed. Enter the provided code and sign in. Once completed, return to the application and choose the **1. Display access token** option to display the access token.

> [!TIP]
> Access tokens for Microsoft work or school accounts can be parsed for troubleshooting purposes at [https://jwt.ms](https://jwt.ms). Access tokens for personal Microsoft accounts use a proprietary format and cannot be parsed.
