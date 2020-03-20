<!-- markdownlint-disable MD002 MD041 -->

In this section you'll create a basic Java console app.

1. Open your command-line interface (CLI) in a directory where you want to create the project. Run the following command to create a new Maven project.

    ```Shell
    mvn archetype:generate "-DarchetypeArtifactId=maven-archetype-quickstart" "-DarchetypeGroupId=org.apache.maven.archetypes" "-DgroupId=com.contoso" "-DartifactId=graphtutorial" "-Dversion=1.0-SNAPSHOT"
    ```

    > [!IMPORTANT]
    > You can enter different values for the group ID (`DgroupId` parameter) and artifact ID (`DartifactId` parameter) than the values specified above. The sample code in this tutorial assumes that the group ID `com.contoso` was used. If you use a different value, be sure to replace `com.contoso` in any sample code with your group ID.

    When prompted, confirm the configuration, then wait for the project to be created.

1. Open **./graphtutorial/pom.xml** and update the `maven.compiler.source` and `maven.compiler.target` values to match your version of Java.

    :::code language="xml" source="../demo/graphtutorial/pom.xml" id="JavaVersionSnippet" highlight="3-4":::

1. Once the project is created, verify that it works by changing to the `graphtutorial` directory and running the following commands to package and run the app in your CLI.

    ```Shell
    mvn package
    mvn exec:java -D exec.mainClass="com.contoso.App"
    ```

    If it works, the app should output `Hello World!`.

## Install dependencies

Before moving on, add some additional dependencies that you will use later.

- [Microsoft Authentication Library (MSAL) for Java](https://github.com/AzureAD/microsoft-authentication-library-for-java) to authenticate the user and acquire access tokens.
- [Microsoft Graph SDK for Java](https://github.com/microsoftgraph/msgraph-sdk-java) to make calls to the Microsoft Graph.
- [SLF4J NOP Binding](https://mvnrepository.com/artifact/org.slf4j/slf4j-nop) to suppress logging from MSAL.

1. Open **./graphtutorial/pom.xml**. Add the following inside the `<dependencies>` element.

    :::code language="xml" source="../demo/graphtutorial/pom.xml" id="PomDependenciesSnippet":::

The next time you build the project, Maven will download those dependencies.

## Design the app

1. Open the **./graphtutorial/src/main/java/com/contoso/App.java** file and replace its contents with the following.

    ```java
    package com.contoso;

    import java.util.InputMismatchException;
    import java.util.Scanner;

    /**
     * Graph Tutorial
     *
     */
    public class App {
        public static void main(String[] args) {
            System.out.println("Java Graph Tutorial");
            System.out.println();

            Scanner input = new Scanner(System.in);

            int choice = -1;

            while (choice != 0) {
                System.out.println("Please choose one of the following options:");
                System.out.println("0. Exit");
                System.out.println("1. Display access token");
                System.out.println("2. List calendar events");

                try {
                    choice = input.nextInt();
                } catch (InputMismatchException ex) {
                    // Skip over non-integer input
                    input.nextLine();
                }

                // Process user choice
                switch(choice) {
                    case 0:
                        // Exit the program
                        System.out.println("Goodbye...");
                        break;
                    case 1:
                        // Display access token
                        break;
                    case 2:
                        // List the calendar
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            }

            input.close();
        }
    }
    ```

    This implements a basic menu and reads the user's choice from the command line.
