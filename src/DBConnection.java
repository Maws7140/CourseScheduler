/*
 * Provides and manages the database connection.
 */
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException; // Import File for separator
// Import logging (optional, using System.out/err for simplicity here)
// import java.util.logging.Level;
// import java.util.logging.Logger;

/**
 * Handles the database connection for the Course Scheduler application.
 * Uses a singleton pattern to maintain a single connection instance.
 *
 * @author acv (Original)
 * @author Gemini (Refactored)
 */
public class DBConnection {
    // private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName()); // Optional: Use Java Util Logging
    private static Connection connection = null; // Initialize to null
    private static final Object connectionLock = new Object(); // Lock object for thread safety

    // TODO: Move these credentials to a secure configuration file or environment variables
    private static final String USER = "java";
    private static final String PASSWORD = "java";
    // Support both embedded mode and network mode
    private static final String DATABASE_URL;

    static {
        // Initialize the database URL based on environment
        // Use File.separator for platform independence
        String dbPath = System.getProperty("user.dir") + File.separator + "CourseSchedulerDBWaleBogunjoko944905508";
        DATABASE_URL = "jdbc:derby:" + dbPath + ";create=true";
        System.out.println("Database URL set to: " + DATABASE_URL); // Log the URL being used
    }

    /**
     * Gets the singleton database connection instance.
     * If the connection doesn't exist or is closed, it attempts to create a new one.
     * Sets autocommit to false on the connection.
     * This method is thread-safe.
     *
     * @return The active database connection.
     * @throws SQLException if a database access error occurs, including initialization failures.
     */
    public static Connection getConnection() throws SQLException {
        // Log entry into the method
        System.out.println("DBConnection.getConnection() called by thread: " + Thread.currentThread().getName());
        // LOGGER.log(Level.INFO, "getConnection() called by thread: {0}", Thread.currentThread().getName()); // Optional JUL

        synchronized (connectionLock) {
            boolean needsNewConnection = false;
            if (connection == null) {
                System.out.println("Connection is null. Attempting to create a new one.");
                // LOGGER.info("Connection is null. Attempting to create a new one."); // Optional JUL
                needsNewConnection = true;
            } else {
                try {
                    if (connection.isClosed()) {
                        System.out.println("Connection is closed. Attempting to create a new one.");
                        // LOGGER.info("Connection is closed. Attempting to create a new one."); // Optional JUL
                        needsNewConnection = true;
                    } else {
                        System.out.println("Existing connection is open. Verifying autocommit.");
                        // LOGGER.info("Existing connection is open. Verifying autocommit."); // Optional JUL
                        // Ensure autocommit is off even for existing connections
                        if (connection.getAutoCommit()) {
                            System.out.println("Autocommit was on, setting to false.");
                            connection.setAutoCommit(false);
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error checking/setting autocommit on existing connection: " + e.getMessage() + ". Attempting to create a new one.");
                    // LOGGER.log(Level.WARNING, "Error checking/setting autocommit on existing connection: {0}. Attempting to create a new one.", e.getMessage()); // Optional JUL
                    needsNewConnection = true;
                    connection = null; // Ensure we try to reconnect fully
                }
            }

            if (needsNewConnection) {
                try {
                    System.out.println("Establishing new database connection to: " + DATABASE_URL);
                    // LOGGER.log(Level.INFO, "Establishing new database connection to: {0}", DATABASE_URL); // Optional JUL
                    // This is where the "Failed to start database" error likely originates
                    connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
                    System.out.println("New connection established successfully. Setting autocommit to false.");
                    connection.setAutoCommit(false); // Set autocommit to false for new connections
                    // LOGGER.info("New connection established successfully. Autocommit set to false."); // Optional JUL
                } catch (SQLException e) {
                    // Log detailed error for connection/startup failure
                    System.err.println("FATAL: Failed to establish/start database connection: " + e.getMessage());
                    System.err.println("SQLState: " + e.getSQLState());
                    System.err.println("Error Code: " + e.getErrorCode());
                    // LOGGER.log(Level.SEVERE, "Failed to establish new database connection", e); // Optional JUL
                    // Log the full stack trace for connection errors
                    e.printStackTrace(System.err);
                    // Check derby.log for more details!
                    System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    System.err.println("!!! Check the derby.log file in the application directory for more detailed Derby error messages. !!!");
                    System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    connection = null; // Ensure connection remains null on failure
                    throw e; // Re-throw the exception so the caller knows it failed
                }
            }
            if (connection == null) {
                System.err.println("CRITICAL: Connection is still null after attempting creation in getConnection()!");
                throw new SQLException("Failed to obtain a valid database connection.");
            }
            return connection;
        }
    }

    /**
     * Closes the database connection if it is open.
     * Attempts to rollback any pending transaction before closing.
     * Should be called when the application exits.
     */
    public static void closeConnection() {
        synchronized (connectionLock) {
            if (connection != null) {
                try {
                    if (!connection.isClosed()) {
                        System.out.println("Closing database connection...");
                        // Attempt to rollback any uncommitted changes if autocommit was off
                        try {
                            if (!connection.getAutoCommit()) {
                                System.out.println("Rolling back transaction before closing...");
                                connection.rollback();
                            }
                        } catch (SQLException rollbackEx) {
                            System.err.println("Error during rollback before closing: " + rollbackEx.getMessage());
                        }
                        // LOGGER.info("Closing database connection..."); // Optional JUL
                        connection.close();
                        System.out.println("Database connection closed.");
                        // LOGGER.info("Database connection closed."); // Optional JUL
                    } else {
                        System.out.println("Database connection was already closed.");
                        // LOGGER.info("Database connection was already closed."); // Optional JUL
                    }
                } catch (SQLException e) {
                    System.err.println("Error closing database connection: " + e.getMessage());
                    // LOGGER.log(Level.SEVERE, "Error closing database connection", e); // Optional JUL
                    e.printStackTrace(System.err);
                } finally {
                    connection = null; // Set to null after attempting close
                }
            } else {
                System.out.println("No active connection to close.");
                // LOGGER.info("No active connection to close."); // Optional JUL
            }
        }
    }
}
