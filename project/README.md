# Today - Command Line Event Application

This project is an extended version of the "Today" application that displays events related to a specific day (typically today) from various sources such as CSV files, an SQLite database, and a web service. 
It also supports adding new events via command-line arguments. The project is built using Java (recommended version 21) and managed with Maven.
It uses the picocli library for command-line parsing, the sqlite-jdbc driver for database connectivity, Jackson for JSON deserialization, and other techniques covered in the course Ohjelmoinnin Syventävät Tekniikat.

## Contents

- [Requirements and Setup](#requirements-and-setup)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Command Line Options and Subcommands](#command-line-options-and-subcommands)
- [Database Initialization](#database-initialization)
- [Project Structure](#project-structure)
- [Tools and Libraries](#tools-and-libraries)
- [Usage Examples](#usage-examples)
- [Additional Information](#additional-information)

## Requirements and Setup

- **JDK:** Recommended JDK 21 (if other version is used, the properties in pom.xml should be changed according to the desired version). Ensure your `JAVA_HOME` is set properly.
- **Maven:** Maven 3.6 or newer.
- **SQLite:** SQLite (and the SQLite Command Line Shell) is used to initialize the database.
- **Additional Tools:** You may use a text editor or IDE (e.g., VS Code, IntelliJ IDEA) to edit the code.

## Building the Project

Navigate to the project root (where the `pom.xml` is located) and run:

mvn clean compile

///////////////////////

If desired to build and executable JAR file, run the following Maven command:

mvn clean package

This creates JAR file in the target folder that contains all the compiled classes

Configuration of the needed Maven Shade Plugin is already included in pom.xml



## Running the Application

You can run the application using Maven's exec plugin. For example, 
to run the application you can use the following command:

mvn exec:java -Dexec.mainClass="tamk.tehtava.Today" -Dexec.args="listevents"

/////////////////////////////////////////////////////////////////
If the application is build using mvn clean package (JAR), the same basic "listevents" version of the program can be run with:

java -jar target/Today-1.0-SNAPSHOT-shaded.jar listevents

Note: The jar example is assuming that the .jar file name has not been changed from the default




## Command Line Options and Subcommands

The application uses picocli to handle command line parameters and subcommands. The main command is today and it supports subcommands such as:

listevents – Lists events for a given day (and optionally filtered by category).

addevent – Adds a new event.

listproviders – Lists the identifiers of the registered event providers.




Note: For options, both short and long forms are supported. For instance, in listevents you can use -d/--date and -c/--category.


## Populating the events.csv CSV File with Fake Data (Optional)

If you want to populate the CSV event file with demo data, you can copy the `fake10k.csv` file (located in the `src/main/resources` folder) to your `.today` directory and rename it to `events.csv`. For example:

cp src/main/resources/fake10k.csv ~/.today/events.csv

Alternatively, you can run your own script or use a text editor to append the contents of fake10k.csv to your CSV file. 

Note that by default, if the CSV file does not exist, the application creates an empty file. Automatic population of this file is not performed by the application to prevent overwriting any user modifications.



## Database Initialization

The SQLite database file `events.sqlite3` is provided as a resource in the project (located in `src/main/resources/events.sqlite3`).
When the application starts, it checks if the database file exists in the `.today` directory in your home folder (e.g., `~/.today/events.sqlite3`). 
If the file is not found, the application automatically copies it from the resources to that location.

If you prefer to initialize or update the database manually, you can follow these steps:

1. Create a directory (if not already present) for your database files (e.g., in your home directory, create a .today folder):

mkdir -p ~/.today

2. Open the SQLite shell and create the database file:

sqlite3 ~/.today/events.sqlite3

3. In the SQLite shell, create the necessary tables by reading the provided SQL scripts:

.read create_tables.sql
.read insert_categories.sql

4. To import fake events (if applicable), you can create a temporary table, import data, insert into the main event table, and then drop the temporary table:

DROP TABLE IF EXISTS temp_event;
CREATE TABLE temp_event (event_date DATE, event_description TEXT, category_id INTEGER);
.import --csv fake10k.csv temp_event
INSERT INTO event(event_date, event_description, category_id) SELECT * FROM temp_event;
DROP TABLE temp_event;

Note:
By default, the application automatically populates the database if the events.sqlite3 file is not present in the .today directory. 
The manual instructions above are provided for advanced users who wish to customize the database initialization or update its contents.

## Project Structure

- src/main/java: Contains all Java source code
    - **tamk/tehtava**: Main classes, including Today.java and EventManager.
    - **tamk/tehtava/commands**: Command-line subcommands (listevents, addevent, listproviders, etc.).
    - **tamk/tehtava/datamodel**: Data models (Event, SingularEvent, AnnualEvent, RuleBasedEvent, Category, etc.).
    - **tamk/tehtava/filters**: Filters to filter the wanted events from the providers
    - **tamk/tehtava/providers**: Providers that read events from different sources (CSVEventProvider, SQLiteEventProvider).
    - **tamk/tehtava/providers/web**: WebEventProvider and associated helper classes (like EventDeserializer).


## Tools and Libraries

- **JDK 21**
- **Maven** for build and dependency management.
- **picocli** for command line interface handling.
- **sqlite-jdbc** for SQLite database connectivity.
- **Jackson** for JSON deserialization.
- **OpenCSV** for CSV event handling
- **Java built-in HTTP Client API** for web connectivity



## Usage Examples

Below are examples of how to run the application:

- List all events for today from all providers:

mvn exec:java -Dexec.mainClass="tamk.tehtava.Today" -Dexec.args="listevents"

- List events for today in a specific category and on a specific date(e.g., apple/macos):

mvn exec:java -Dexec.mainClass="tamk.tehtava.Today" -Dexec.args="listevents -c apple/macos -d 03-24"

- List events for today in multiple categories (e.g., apple/macos and programming/java):

mvn exec:java -Dexec.mainClass="tamk.tehtava.Today" -Dexec.args="listevents -c apple/macos,programming/java"

- Add a new event using the default provider (CSV):

mvn exec:java -Dexec.mainClass="tamk.tehtava.Today" -Dexec.args="addevent --date 2038-01-19 --category test/fake --description \"Unix clock rolls over\""

- List all events for today using only the web provider: 

mvn exec:java -Dexec.mainClass="tamk.tehtava.Today" -Dexec.args="listevents --provider web"

- Add a new event using a named provider (SQLite):

mvn exec:java -Dexec.mainClass="tamk.tehtava.Today" -Dexec.args="addevent --date 2038-01-19 --category test/fake --description \"SQLite Test\" --provider sqlite"

- List all registered provider identifiers:

mvn exec:java -Dexec.mainClass="tamk.tehtava.Today" -Dexec.args="listproviders"


One more example using JAR (basically the same):

java -jar target/Today-1.0-SNAPSHOT-shaded.jar listevents -d 03-24 -c apple/macos

