<!-- markdownlint-disable MD002 MD041 -->

In this exercise you will extend the application from the previous exercise to support authentication with Azure AD. This is required to obtain the necessary OAuth access token to call the Microsoft Graph. In this step you will integrate the [Microsoft Authentication Library (MSAL) for Java](https://github.com/AzureAD/microsoft-authentication-library-for-java) into the application.

1. Create a new directory named **graphtutorial** in the **./src/main/resources** directory.

1. Create a new file in the **./src/main/resources/graphtutorial** directory named **oAuth.properties**, and add the following text in that file.

    :::code language="ini" source="../demo/graphtutorial/src/main/resources/graphtutorial/oAuth.properties.example":::

    Replace `YOUR_APP_ID_HERE` with the application ID you created in the Azure portal.

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

1. Create a new file in the **./graphtutorial/src/main/java/graphtutorial** directory named **Authentication.java** and add the following code.

    :::code language="java" source="../demo/graphtutorial/src/main/java/graphtutorial/Authentication.java" id="AuthenticationSnippet":::

1. In **App.java**, add the following code just before the `Scanner input = new Scanner(System.in);` line to get an access token.

    ```java
    // Get an access token
    Authentication.initialize(appId);
    final String accessToken = Authentication.getUserAccessToken(appScopes);
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
