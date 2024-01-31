// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

// <ImportSnippet>
package graphtutorial;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.MessageCollectionResponse;
import com.microsoft.graph.models.User;
// </ImportSnippet>

public class App {
    // <MainSnippet>
    public static void main(String[] args) {
        System.out.println("Java Graph Tutorial");
        System.out.println();

        final Properties oAuthProperties = new Properties();
        try {
            oAuthProperties.load(App.class.getResourceAsStream("oAuth.properties"));
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
            return;
        }

        initializeGraph(oAuthProperties);

        greetUser();

        Scanner input = new Scanner(System.in);

        int choice = -1;

        while (choice != 0) {
            System.out.println("Please choose one of the following options:");
            System.out.println("0. Exit");
            System.out.println("1. Display access token");
            System.out.println("2. List my inbox");
            System.out.println("3. Send mail");
            System.out.println("4. Make a Graph call");

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
                    // List emails from user's inbox
                    listInbox();
                    break;
                case 3:
                    // Send an email message
                    sendMail();
                    break;
                case 4:
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
            Graph.initializeGraphForUserAuth(properties,
                challenge -> System.out.println(challenge.getMessage()));
        } catch (Exception e)
        {
            System.out.println("Error initializing Graph for user auth");
            System.out.println(e.getMessage());
        }
    }
    // </InitializeGraphSnippet>

    // <GreetUserSnippet>
    private static void greetUser() {
        try {
            final User user = Graph.getUser();
            // For Work/school accounts, email is in mail property
            // Personal accounts, email is in userPrincipalName
            final String email = user.getMail() == null ? user.getUserPrincipalName() : user.getMail();
            System.out.println("Hello, " + user.getDisplayName() + "!");
            System.out.println("Email: " + email);
        } catch (Exception e) {
            System.out.println("Error getting user");
            System.out.println(e.getMessage());
        }
    }
    // </GreetUserSnippet>

    // <DisplayAccessTokenSnippet>
    private static void displayAccessToken() {
        try {
            final String accessToken = Graph.getUserToken();
            System.out.println("Access token: " + accessToken);
        } catch (Exception e) {
            System.out.println("Error getting access token");
            System.out.println(e.getMessage());
        }
    }
    // </DisplayAccessTokenSnippet>

    // <ListInboxSnippet>
    private static void listInbox() {
        try {
            final MessageCollectionResponse messages = Graph.getInbox();

            // Output each message's details
            for (Message message: messages.getValue()) {
                System.out.println("Message: " + message.getSubject());
                System.out.println("  From: " + message.getFrom().getEmailAddress().getName());
                System.out.println("  Status: " + (message.getIsRead() ? "Read" : "Unread"));
                System.out.println("  Received: " + message.getReceivedDateTime()
                    // Values are returned in UTC, convert to local time zone
                    .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
            }

            final Boolean moreMessagesAvailable = messages.getOdataNextLink() != null;
            System.out.println("\nMore messages available? " + moreMessagesAvailable);
        } catch (Exception e) {
            System.out.println("Error getting inbox");
            System.out.println(e.getMessage());
        }
    }
    // </ListInboxSnippet>

    // <SendMailSnippet>
    private static void sendMail() {
        try {
            // Send mail to the signed-in user
            // Get the user for their email address
            final User user = Graph.getUser();
            final String email = user.getMail() == null ? user.getUserPrincipalName() : user.getMail();

            Graph.sendMail("Testing Microsoft Graph", "Hello world!", email);
            System.out.println("\nMail sent.");
        } catch (Exception e) {
            System.out.println("Error sending mail");
            System.out.println(e.getMessage());
        }
    }
    // </SendMailSnippet>

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
