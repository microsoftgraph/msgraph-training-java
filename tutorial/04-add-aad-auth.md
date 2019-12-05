<!-- markdownlint-disable MD002 MD041 -->

In this exercise you will extend the application from the previous exercise to support authentication with Azure AD. This is required to obtain the necessary OAuth access token to call the Microsoft Graph. In this step you will integrate the [Microsoft Authentication Library (MSAL) for Java](https://github.com/AzureAD/microsoft-authentication-library-for-java) into the application.

Create a new directory named **resources** in the **./graphtutorial/src/main** directory. Then create a new directory named **com** in the **resources** directory, then a new directory named **contoso** in the **com** directory. Finally, create a new file in the **./graphtutorial/src/main/resources/com/contoso** directory named **oAuth.properties**, and add the following text in that file.

```INI
app.id=YOUR_APP_ID_HERE
app.scopes=User.Read,Calendars.Read
```

Replace `YOUR_APP_ID_HERE` with the application ID you created in the Azure portal.

> [!IMPORTANT]
> If you're using source control such as git, now would be a good time to exclude the **oAuth.properties** file from source control to avoid inadvertently leaking your app ID.

Open **App.java** and add the following `import` statements.

```java
import java.io.IOException;
import java.util.Properties;
```

Then add the following code just before the `Scanner input = new Scanner(System.in);` line to load the **oAuth.properties** file.

```java
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
```

## Implement sign-in

Create a new file in the **./graphtutorial/src/main/java/com/contoso** directory named **Authentication.java** and add the following code.

```java
package com.contoso;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.function.Consumer;

import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;

/**
 * Authentication
 */
public class Authentication {

    private static String applicationId;
    // Set authority to allow only organizational accounts
    // Device code flow only supports organizational accounts
    private final static String authority = "https://login.microsoftonline.com/common/";

    public static void initialize(String applicationId) {
        Authentication.applicationId = applicationId;
    }

    public static String getUserAccessToken(String[] scopes) {
        if (applicationId == null) {
            System.out.println("You must initialize Authentication before calling getUserAccessToken");
            return null;
        }

        Set<String> scopeSet = Set.of(scopes);

        PublicClientApplication app;
        try {
            // Build the MSAL application object with
            // app ID and authority
            app = PublicClientApplication.builder(applicationId)
                .authority(authority)
                .build();
        } catch (MalformedURLException e) {
            return null;
        }

        // Create consumer to receive the DeviceCode object
        // This method gets executed during the flow and provides
        // the URL the user logs into and the device code to enter
        Consumer<DeviceCode> deviceCodeConsumer = (DeviceCode deviceCode) -> {
            // Print the login information to the console
            System.out.println(deviceCode.message());
        };

        // Request a token, passing the requested permission scopes
        IAuthenticationResult result = app.acquireToken(
            DeviceCodeFlowParameters
                .builder(scopeSet, deviceCodeConsumer)
                .build()
        ).exceptionally(ex -> {
            System.out.println("Unable to authenticate - " + ex.getMessage());
            return null;
        }).join();

        if (result != null) {
            return result.accessToken();
        }

        return null;
    }
}
```

In **App.java**, add the following code just before the `Scanner input = new Scanner(System.in);` line to get an access token.

```java
// Get an access token
Authentication.initialize(appId);
final String accessToken = Authentication.getUserAccessToken(appScopes);
```

Then add the following line after the `// Display access token` comment.

```java
System.out.println("Access token: " + accessToken);
```

Build and run the app. The application displays a URL and device code.

```Shell
Java Graph Tutorial

To sign in, use a web browser to open the page https://microsoft.com/devicelogin and enter the code F7CG945YZ to authenticate.
```

Open a browser and browse to the URL displayed. Enter the provided code and sign in. Once completed, return to the application and choose the **1. Display access token** option to display the access token.
