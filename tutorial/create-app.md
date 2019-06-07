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

Open **./graphtutorial/pom.xml**. Add the following inside the `<dependencies>` element.

```xml
<dependency>
  <groupId>com.microsoft.graph</groupId>
  <artifactId>microsoft-graph</artifactId>
  <version>1.4.0</version>
</dependency>

<dependency>
  <groupId>com.microsoft.azure</groupId>
  <artifactId>msal4j</artifactId>
  <version>0.4.0-preview</version>
</dependency>
```

The next time you build the project, Maven will download those dependencies.
