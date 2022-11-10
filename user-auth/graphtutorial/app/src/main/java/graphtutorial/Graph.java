// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

// <ImportSnippet>
package graphtutorial;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserSendMailParameterSet;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.requests.UserCollectionPage;

import okhttp3.Request;
// </ImportSnippet>

public class Graph {
    // <UserAuthConfigSnippet>
    private static Properties _properties;
    private static DeviceCodeCredential _deviceCodeCredential;
    private static GraphServiceClient<Request> _userClient;

    public static void initializeGraphForUserAuth(Properties properties, Consumer<DeviceCodeInfo> challenge) throws Exception {
        // Ensure properties isn't null
        if (properties == null) {
            throw new Exception("Properties cannot be null");
        }

        _properties = properties;

        final String clientId = properties.getProperty("app.clientId");
        final String authTenantId = properties.getProperty("app.authTenant");
        final List<String> graphUserScopes = Arrays
            .asList(properties.getProperty("app.graphUserScopes").split(","));

        _deviceCodeCredential = new DeviceCodeCredentialBuilder()
            .clientId(clientId)
            .tenantId(authTenantId)
            .challengeConsumer(challenge)
            .build();

        final TokenCredentialAuthProvider authProvider =
            new TokenCredentialAuthProvider(graphUserScopes, _deviceCodeCredential);

        _userClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider)
            .buildClient();
    }
    // </UserAuthConfigSnippet>

    // <GetUserTokenSnippet>
    public static String getUserToken() throws Exception {
        // Ensure credential isn't null
        if (_deviceCodeCredential == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        final String[] graphUserScopes = _properties.getProperty("app.graphUserScopes").split(",");

        final TokenRequestContext context = new TokenRequestContext();
        context.addScopes(graphUserScopes);

        final AccessToken token = _deviceCodeCredential.getToken(context).block();
        return token.getToken();
    }
    // </GetUserTokenSnippet>

    // <GetUserSnippet>
    public static User getUser() throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        return _userClient.me()
            .buildRequest()
            .select("displayName,mail,userPrincipalName")
            .get();
    }
    // </GetUserSnippet>

    // <GetInboxSnippet>
    public static MessageCollectionPage getInbox() throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        return _userClient.me()
            .mailFolders("inbox")
            .messages()
            .buildRequest()
            .select("from,isRead,receivedDateTime,subject")
            .top(25)
            .orderBy("receivedDateTime DESC")
            .get();
    }
    // </GetInboxSnippet>

    // <SendMailSnippet>
    public static void sendMail(String subject, String body, String recipient) throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        // Create a new message
        final Message message = new Message();
        message.subject = subject;
        message.body = new ItemBody();
        message.body.content = body;
        message.body.contentType = BodyType.TEXT;

        final Recipient toRecipient = new Recipient();
        toRecipient.emailAddress = new EmailAddress();
        toRecipient.emailAddress.address = recipient;
        message.toRecipients = List.of(toRecipient);

        // Send the message
        _userClient.me()
            .sendMail(UserSendMailParameterSet.newBuilder()
                .withMessage(message)
                .build())
            .buildRequest()
            .post();
    }
    // </SendMailSnippet>

    // <AppOnyAuthConfigSnippet>
    private static ClientSecretCredential _clientSecretCredential;
    private static GraphServiceClient<Request> _appClient;

    private static void ensureGraphForAppOnlyAuth() throws Exception {
        // Ensure _properties isn't null
        if (_properties == null) {
            throw new Exception("Properties cannot be null");
        }

        if (_clientSecretCredential == null) {
            final String clientId = _properties.getProperty("app.clientId");
            final String tenantId = _properties.getProperty("app.tenantId");
            final String clientSecret = _properties.getProperty("app.clientSecret");

            _clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .clientSecret(clientSecret)
                .build();
        }

        if (_appClient == null) {
            final TokenCredentialAuthProvider authProvider =
                new TokenCredentialAuthProvider(
                    List.of("https://graph.microsoft.com/.default"), _clientSecretCredential);

            _appClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();
        }
    }
    // </AppOnyAuthConfigSnippet>

    // <GetUsersSnippet>
    public static UserCollectionPage getUsers() throws Exception {
        ensureGraphForAppOnlyAuth();

        return _appClient.users()
            .buildRequest()
            .select("displayName,id,mail")
            .top(25)
            .orderBy("displayName")
            .get();
    }
    // </GetUsersSnippet>

    // <MakeGraphCallSnippet>
    public static void makeGraphCall() {
        // INSERT YOUR CODE HERE
        // Note: if using _appClient, be sure to call ensureGraphForAppOnlyAuth
        // before using it.
        // ensureGraphForAppOnlyAuth();
    }
    // </MakeGraphCallSnippet>
}
