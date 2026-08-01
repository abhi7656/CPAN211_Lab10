import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class Main {

    // MySQL database connection information
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/cpan211_lab10";

    private static final String DB_USER = "root";

    // Change this to your own MySQL password
    private static final String DB_PASSWORD = "12345678Abhi";

    // Change this to your real Humber student number
    private static final String STUDENT_NUMBER = "n01754524";

    private static final String TABLE_NAME =
            STUDENT_NUMBER + "_Orders";

    public static void main(String[] args) {
        runLab();
    }

    /**
     * Controls the complete program and handles all exceptions.
     */
    private static void runLab() {
        System.out.println("CPAN 211 Lab 10 - Database Connectivity");
        System.out.println("---------------------------------------");

        try {
            loadDriver();

            try (Connection connection = openConnection()) {
                System.out.println("Connected to MySQL successfully.");

                createAndPopulateTable(connection);
                Map<String, Integer> customerBills =
                        retrieveCustomerBills(connection);

                printFinalBills(customerBills);
            }

        } catch (ClassNotFoundException exception) {
            System.err.println(
                    "MySQL JDBC driver was not found: "
                            + exception.getMessage()
            );

        } catch (SQLException exception) {
            System.err.println(
                    "Database error: " + exception.getMessage()
            );

        } catch (Exception exception) {
            System.err.println(
                    "Program error: " + exception.getMessage()
            );
        }
    }

    /**
     * Loads the MySQL JDBC driver.
     */
    private static void loadDriver()
            throws ClassNotFoundException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("MySQL JDBC driver loaded.");
    }

    /**
     * Establishes and returns the database connection.
     */
    private static Connection openConnection()
            throws SQLException {

        return DriverManager.getConnection(
                DB_URL,
                DB_USER,
                DB_PASSWORD
        );
    }

    /**
     * Reads salesScripts.sql, changes the table name,
     * and executes its SQL statements.
     */
    private static void createAndPopulateTable(
            Connection connection) throws Exception {

        dropExistingTable(connection);

        String sqlScript = readSqlScript();

        // Replace the starter table name with the required table name
        sqlScript = sqlScript.replaceAll(
                "(?i)\\bSales\\b",
                TABLE_NAME
        );

        executeSqlScript(connection, sqlScript);

        System.out.println(
                "Table " + TABLE_NAME
                        + " created and populated successfully."
        );
    }

    /**
     * Removes the old table so that the program can be run again.
     */
    private static void dropExistingTable(
            Connection connection) throws SQLException {

        String dropSql =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(dropSql);
        }
    }

    /**
     * Reads salesScripts.sql from src/main/resources.
     */
    private static String readSqlScript() throws Exception {

        InputStream inputStream =
                Main.class.getClassLoader()
                        .getResourceAsStream("salesScripts.sql");

        if (inputStream == null) {
            throw new IllegalStateException(
                    "salesScripts.sql was not found "
                            + "in src/main/resources."
            );
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8))) {

            return reader.lines()
                    .filter(line ->
                            !line.trim().startsWith("--"))
                    .collect(Collectors.joining("\n"));
        }
    }

    /**
     * Separates and executes each statement from the SQL script.
     */
    private static void executeSqlScript(
            Connection connection,
            String sqlScript) throws SQLException {

        String[] sqlStatements = sqlScript.split(";");

        try (Statement statement = connection.createStatement()) {

            for (String sql : sqlStatements) {
                String cleanedSql = sql.trim();

                if (!cleanedSql.isEmpty()) {
                    statement.executeUpdate(cleanedSql);
                }
            }
        }
    }

    /**
     * Retrieves all records and calculates customer totals
     * using a Java Map instead of SQL SUM.
     */
    private static Map<String, Integer> retrieveCustomerBills(
            Connection connection) throws SQLException {

        Map<String, Integer> customerBills =
                new LinkedHashMap<>();

        String selectSql =
                "SELECT Customer, Product, Price FROM "
                        + TABLE_NAME;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet =
                     statement.executeQuery(selectSql)) {

            System.out.println();
            System.out.println("All Sales Records");
            System.out.println("---------------------------------------");
            System.out.printf(
                    "%-15s %-15s %10s%n",
                    "Customer",
                    "Product",
                    "Price"
            );

            while (resultSet.next()) {
                String customer =
                        resultSet.getString("Customer");

                String product =
                        resultSet.getString("Product");

                int price =
                        resultSet.getInt("Price");

                System.out.printf(
                        "%-15s %-15s $%9d%n",
                        customer,
                        product,
                        price
                );

                // Aggregate the bills in Java
                customerBills.merge(
                        customer,
                        price,
                        Integer::sum
                );
            }
        }

        return customerBills;
    }

    /**
     * Prints every customer only once with their final bill.
     */
    private static void printFinalBills(
            Map<String, Integer> customerBills) {

        System.out.println();
        System.out.println("Customer Final Bills");
        System.out.println("---------------------------------------");
        System.out.printf(
                "%-20s %12s%n",
                "Customer",
                "Final Bill"
        );

        for (Map.Entry<String, Integer> entry
                : customerBills.entrySet()) {

            System.out.printf(
                    "%-20s $%11d%n",
                    entry.getKey(),
                    entry.getValue()
            );
        }}}
    
