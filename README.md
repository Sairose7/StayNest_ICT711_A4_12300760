# StayNest – ICT711 Assessment 4

## Requirements
- JDK 17 or newer
- Windows: Command Prompt / PowerShell
- Linux/macOS: Terminal

## Windows – easiest method
1. Extract this ZIP completely.
2. Open the extracted `stay` folder.
3. Double-click `run.bat`.
4. Choose **GUI** or **TBI** from the startup window.

## Run GUI directly
Open Command Prompt in the project folder and run:
```text
javac -d out src\*.java
java -cp out StayNestApp gui
```

## Run TBI directly
```text
javac -d out src\*.java
java -cp out StayNestApp tbi
```

The TBI menu is displayed as one vertical numbered option per line for readability.

## Run from an IDE
Open the `stay` folder as a Java project. Use JDK 17+. Run `src/StayNestApp.java`.
For TBI, add the program argument `tbi`. For GUI, add `gui`.

## JUnit 5 tests
Maven is included through `pom.xml`. If Maven is installed:
```text
mvn test
```
Tests are in `test/StayNestSystemTest.java`.

## Data
User data is stored in `data/users.csv`. Keep the `data` folder beside `src` when running from the project root.
