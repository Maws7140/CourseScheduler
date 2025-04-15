import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes the database tables required for the Course Scheduler application.
 * This class should be called during application startup.
 */
public class DBInitializer {

    private static final String[] TABLE_NAMES = {
        "APP.SEMESTER", "APP.COURSES", "APP.CLASSES", "APP.STUDENTS", "APP.SCHEDULE"
    };

    /**
     * Initializes the database tables if they don't exist.
     * @throws SQLException if a database error occurs
     */
    public static void initializeDatabase() throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            
            // Check if tables exist and create them if they don't
            if (!tablesExist(conn)) {
                createTables(conn);
                System.out.println("Database tables created successfully.");
            } else {
                System.out.println("Database tables already exist.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Checks if all required tables exist in the database.
     * @param conn Database connection
     * @return true if all tables exist, false otherwise
     * @throws SQLException if a database error occurs
     */
    private static boolean tablesExist(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        
        for (String tableName : TABLE_NAMES) {
            String[] parts = tableName.split("\\.");
            String schema = parts[0];
            String table = parts[1];
            
            try (ResultSet rs = metaData.getTables(null, schema, table, new String[]{"TABLE"})) {
                if (!rs.next()) {
                    return false;  // Table not found
                }
            }
        }
        return true;  // All tables exist
    }

    /**
     * Creates all required tables in the database.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     */
    private static void createTables(Connection conn) throws SQLException {
        // Create the APP schema if it doesn't exist
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE SCHEMA APP");
            stmt.close();
        } catch (SQLException e) {
            // Ignore error if schema already exists
            if (!e.getSQLState().equals("X0Y68")) {
                throw e;
            }
        }
        
        // Create SEMESTER table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE APP.SEMESTER (" +
                         "semester VARCHAR(50) PRIMARY KEY)");
        }

        // Create COURSES table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE APP.COURSES (" +
                         "coursecode VARCHAR(10) PRIMARY KEY, " +
                         "description VARCHAR(100) NOT NULL)");
        }

        // Create CLASSES table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE APP.CLASSES (" +
                         "semester VARCHAR(50) NOT NULL, " +
                         "coursecode VARCHAR(10) NOT NULL, " +
                         "seats INTEGER NOT NULL, " +
                         "PRIMARY KEY (semester, coursecode), " +
                         "FOREIGN KEY (semester) REFERENCES APP.SEMESTER(semester), " +
                         "FOREIGN KEY (coursecode) REFERENCES APP.COURSES(coursecode))");
        }

        // Create STUDENTS table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE APP.STUDENTS (" +
                         "studentid VARCHAR(20) PRIMARY KEY, " +
                         "firstname VARCHAR(50) NOT NULL, " +
                         "lastname VARCHAR(50) NOT NULL)");
        }

        // Create SCHEDULE table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE APP.SCHEDULE (" +
                         "semester VARCHAR(50) NOT NULL, " +
                         "studentid VARCHAR(20) NOT NULL, " +
                         "coursecode VARCHAR(10) NOT NULL, " +
                         "status VARCHAR(20) NOT NULL, " +
                         "timestamp TIMESTAMP NOT NULL, " +
                         "PRIMARY KEY (semester, studentid, coursecode), " +
                         "FOREIGN KEY (semester) REFERENCES APP.SEMESTER(semester), " +
                         "FOREIGN KEY (studentid) REFERENCES APP.STUDENTS(studentid), " +
                         "FOREIGN KEY (coursecode) REFERENCES APP.COURSES(coursecode))");
        }
    }
}