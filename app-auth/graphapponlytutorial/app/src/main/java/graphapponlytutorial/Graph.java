// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

// <ImportSnippet>
package graphapponlytutorial;

import java.util.List;
import java.util.Properties;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserCollectionPage;

import okhttp3.Request;
// </ImportSnippet>

public class Graph {
    // <AppOnyAuthConfigSnippet>
    private static Properties _properties;
    private static ClientSecretCredential _clientSecretCredential;
    private static GraphServiceClient<Request> _appClient;

    public static void initializeGraphForAppOnlyAuth(Properties properties) throws Exception {
        // Ensure properties isn't null
        if (properties == null) {
            throw new Exception("Properties cannot be null");
        }

        _properties = properties;

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

    // <GetAppOnlyTokenSnippet>
    public static String getAppOnlyToken() throws Exception {
        // Ensure credential isn't null
        if (_clientSecretCredential == null) {
            throw new Exception("Graph has not been initialized for app-only auth");
        }

        final String[] graphScopes = new String[] {"https://graph.microsoft.com/.default"};

        final TokenRequestContext context = new TokenRequestContext();
        context.addScopes(graphScopes);

        final AccessToken token = _clientSecretCredential.getToken(context).block();
        return token.getToken();
    }
    // </GetAppOnlyTokenSnippet>

    // <GetUsersSnippet>
    public static UserCollectionPage getUsers() throws Exception {
        // Ensure client isn't null
        if (_appClient == null) {
            throw new Exception("Graph has not been initialized for app-only auth");
        }

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
    }
    // </MakeGraphCallSnippet>
}
