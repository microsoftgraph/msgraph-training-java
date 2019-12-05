<!-- markdownlint-disable MD002 MD041 -->

Open your command-line interface (CLI) in a directory where you want to create the project. Run the following command to create a new Maven project.

```Shell
mvn archetype:generate -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeGroupId=org.apache.maven.archetypes -DgroupId=com.contoso -DartifactId=graphtutorial -Dversion=1.0-SNAPSHOT
```

> [!IMPORTANT]
> You can enter different values for the group ID (`DgroupId` parameter) and artifact ID (`DartifactId` parameter) than the values specified above. The sample code in this tutorial assumes that the group ID `com.contoso` was used. If you use a different value, be sure to replace `com.contoso` in any sample code with your group ID.

When prompted, confirm the configuration, then wait for the project to be created. Once the project is created, verify that it works by running the following commands to package and run the app in your CLI.

```Shell
mvn package
java -cp target/graphtutorial-1.0-SNAPSHOT.jar com.contoso.App
```

If it works, the app should output `Hello World!`. Before moving on, add some additional dependencies that you will use later.

- [Microsoft Authentication Library (MSAL) for Java](https://github.com/AzureAD/microsoft-authentication-library-for-java) to authenticate the user and acquire access tokens.
- [Microsoft Graph SDK for Java](https://github.com/microsoftgraph/msgraph-sdk-java) to make calls to the Microsoft Graph.
- [SLF4J NOP Binding](https://mvnrepository.com/artifact/org.slf4j/slf4j-nop) to suppress logging from MSAL.

Open **./graphtutorial/pom.xml**. Add the following inside the `<dependencies>` element.

```xml
<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-nop</artifactId>
  <version>1.8.0-beta4</version>
</dependency>

<dependency>
  <groupId>com.microsoft.graph</groupId>
  <artifactId>microsoft-graph</artifactId>
  <version>1.6.0</version>
</dependency>

<dependency>
  <groupId>com.microsoft.azure</groupId>
  <artifactId>msal4j</artifactId>
  <version>1.1.0</version>
</dependency>
```

The next time you build the project, Maven will download those dependencies.

## Design the app

Open the **./graphtutorial/src/main/java/com/contoso/App.java** file and replace its contents with the following.

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
