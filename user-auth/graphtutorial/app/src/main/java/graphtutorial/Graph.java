// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

// <ImportSnippet>
package graphtutorial;

import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.MessageCollectionResponse;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody;
// </ImportSnippet>

public class Graph {
    // <UserAuthConfigSnippet>
    private static Properties _properties;
    private static DeviceCodeCredential _deviceCodeCredential;
    private static GraphServiceClient _userClient;

    public static void initializeGraphForUserAuth(Properties properties, Consumer<DeviceCodeInfo> challenge) throws Exception {
        // Ensure properties isn't null
        if (properties == null) {
            throw new Exception("Properties cannot be null");
        }

        _properties = properties;

        final String clientId = properties.getProperty("app.clientId");
        final String tenantId = properties.getProperty("app.tenantId");
        final String[] graphUserScopes = properties.getProperty("app.graphUserScopes").split(",");

        _deviceCodeCredential = new DeviceCodeCredentialBuilder()
            .clientId(clientId)
            .tenantId(tenantId)
            .challengeConsumer(challenge)
            .build();

        _userClient = new GraphServiceClient(_deviceCodeCredential, graphUserScopes);
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

        final AccessToken token = _deviceCodeCredential.getTokenSync(context);
        return token.getToken();
    }
    // </GetUserTokenSnippet>

    // <GetUserSnippet>
    public static User getUser() throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        return _userClient.me().get(requestConfig -> {
            requestConfig.queryParameters.select = new String[] {"displayName", "mail", "userPrincipalName"};
        });
    }
    // </GetUserSnippet>

    // <GetInboxSnippet>
    public static MessageCollectionResponse getInbox() throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        return _userClient.me()
            .mailFolders()
            .byMailFolderId("inbox")
            .messages()
            .get(requestConfig -> {
                requestConfig.queryParameters.select = new String[] { "from", "isRead", "receivedDateTime", "subject" };
                requestConfig.queryParameters.top = 25;
                requestConfig.queryParameters.orderby = new String[] { "receivedDateTime DESC" };
            });
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
        message.setSubject(subject);
        final ItemBody itemBody = new ItemBody();
        itemBody.setContent(body);
        itemBody.setContentType(BodyType.Text);
        message.setBody(itemBody);

        final EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress(recipient);
        final Recipient toRecipient = new Recipient();
        toRecipient.setEmailAddress(emailAddress);
        message.setToRecipients(List.of(toRecipient));

        final SendMailPostRequestBody postRequest = new SendMailPostRequestBody();
        postRequest.setMessage(message);

        // Send the message
        _userClient.me()
            .sendMail()
            .post(postRequest);
    }
    // </SendMailSnippet>

    // <MakeGraphCallSnippet>
    public static void makeGraphCall() {
        // INSERT YOUR CODE HERE
    }
    // </MakeGraphCallSnippet>
}
