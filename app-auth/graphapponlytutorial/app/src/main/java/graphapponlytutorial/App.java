// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

// <ImportSnippet>
package graphapponlytutorial;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.UserCollectionPage;
// </ImportSnippet>

public class App {
    // <MainSnippet>
    public static void main(String[] args) {
        System.out.println("Java App-Only Graph Tutorial");
        System.out.println();

        final Properties oAuthProperties = new Properties();
        try {
            oAuthProperties.load(App.class.getResourceAsStream("oAuth.properties"));
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
            return;
        }

        initializeGraph(oAuthProperties);

        Scanner input = new Scanner(System.in);

        int choice = -1;

        while (choice != 0) {
            System.out.println("Please choose one of the following options:");
            System.out.println("0. Exit");
            System.out.println("1. Display access token");
            System.out.println("2. List users");
            System.out.println("3. Make a Graph call");

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
                    displayAccessToken();
                    break;
                case 2:
                    // List users
                    listUsers();
                    break;
                case 3:
                    // Run any Graph code
                    makeGraphCall();
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }

        input.close();
    }
    // </MainSnippet>

    // <InitializeGraphSnippet>
    private static void initializeGraph(Properties properties) {
        try {
            Graph.initializeGraphForAppOnlyAuth(properties,
                challenge -> System.out.println(challenge.getMessage()));
        } catch (Exception e)
        {
            System.out.println("Error initializing Graph for user auth");
            System.out.println(e.getMessage());
        }
    }
    // </InitializeGraphSnippet>

    // <DisplayAccessTokenSnippet>
    private static void displayAccessToken() {
        try {
            final String accessToken = Graph.getAppOnlyToken();
            System.out.println("Access token: " + accessToken);
        } catch (Exception e) {
            System.out.println("Error getting access token");
            System.out.println(e.getMessage());
        }
    }
    // </DisplayAccessTokenSnippet>

    // <ListUsersSnippet>
    private static void listUsers() {
        try {
            final UserCollectionPage users = Graph.getUsers();

            // Output each user's details
            for (User user: users.getCurrentPage()) {
                System.out.println("User: " + user.displayName);
                System.out.println("  ID: " + user.id);
                System.out.println("  Email: " + user.mail);
            }

            final Boolean moreUsersAvailable = users.getNextPage() != null;
            System.out.println("\nMore users available? " + moreUsersAvailable);
        } catch (Exception e) {
            System.out.println("Error getting users");
            System.out.println(e.getMessage());
        }
    }
    // </ListUsersSnippet>

    // <MakeGraphCallSnippet>
    private static void makeGraphCall() {
        try {
            Graph.makeGraphCall();
        } catch (Exception e) {
            System.out.println("Error making Graph call");
            System.out.println(e.getMessage());
        }
    }
    // </MakeGraphCallSnippet>
}
