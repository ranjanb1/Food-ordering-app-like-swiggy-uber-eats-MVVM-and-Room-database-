# Cash Collection CLI Application

A simple command-line interface (CLI) application built in Java to manage daily cash collections from clients.

## Prerequisites

Before you begin, ensure you have the following installed:
*   Java Development Kit (JDK) - Version 8 or newer
*   Apache Maven

## How to Build

1.  Open a terminal or command prompt.
2.  Navigate to the root directory of the `cash-collection-app` project.
3.  Run the following Maven command to compile the project, run tests, and package it into a JAR file:
    ```bash
    mvn clean package
    ```
    This will create a JAR file in the `target/` directory (e.g., `cash-collection-app-1.0-SNAPSHOT.jar`).

## How to Run

Once the project is built, you can run the application using the following command from the project's root directory:

```bash
java -jar target/cash-collection-app-1.0-SNAPSHOT.jar
```
*Note: The exact name of the JAR file (e.g., `cash-collection-app-1.0-SNAPSHOT.jar`) might vary slightly depending on the project version defined in the `pom.xml`.*

Upon running, the application will display a menu in the console to guide you through its features.

## Features

*   Record new cash collections for clients (name, amount, date).
*   View all collections for a specific client, including their total collected amount.
*   Generate daily collection reports for a specified date.
*   Generate weekend collection reports for a specified date range (Saturdays and Sundays).
*   Generate monthly collection reports for a specified year and month.
*   Display a text-based receipt on the console after each new collection is added.
*   Data is stored in CSV format (`collections.csv`).
