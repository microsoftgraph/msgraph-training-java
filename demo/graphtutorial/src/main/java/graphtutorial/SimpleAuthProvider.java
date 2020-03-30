// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

// <AuthProviderSnippet>
package graphtutorial;

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
// </AuthProviderSnippet>