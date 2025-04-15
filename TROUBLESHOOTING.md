# Troubleshooting Guide for Course Scheduler

This document provides solutions for common issues you might encounter when running the Course Scheduler application.

## Java Installation Issues

### Problem: "java: command not found" or "java is not recognized as an internal or external command"

**Solution**: Java is not installed or not in your PATH.

1. Install Java:
   - Windows: Download and install from [Adoptium](https://adoptium.net/)
   - macOS: `brew install openjdk@11`
   - Ubuntu/Debian: `sudo apt install default-jdk`
   - Fedora: `sudo dnf install java-11-openjdk-devel`

2. Verify installation:
   ```
   java -version
   ```

## Database Issues

### Problem: "Database initialization failed: Failed to start database '...path...' with class loader ..."

This is a critical error indicating the embedded Derby database engine itself could not start when the application first tried to connect. It often happens during application startup.

**Solutions**:

1.  **CHECK `derby.log` (MOST IMPORTANT!)**: This file is located in the application's root directory (e.g., `c:\Users\Lilly\OneDrive\Desktop\CourseScheduler`). It contains detailed logs directly from the Derby engine. **Look for the full error message and stack trace** corresponding to the time the application failed to start. Common causes found here include:
    *   `ERROR XJ040: Failed to start database '...', see the next exception for details.` followed by another error like:
        *   `ERROR XSLAO: Database '...' appears to be already booted.` (Another instance running or lock file present).
        *   `java.io.FileNotFoundException` or `java.nio.file.AccessDeniedException` (Permissions issue).
        *   Errors related to log files (`log.ctrl`) or data files (`.dat`) potentially indicating corruption.
2.  **Check for Existing Locks / Other Instances**:
    *   Ensure only **one** instance of the Course Scheduler application is running. Check your system's Task Manager (or equivalent) for any extra `java.exe` or `javaw.exe` processes that might be holding the database open.
    *   If you are sure no other instance is running, navigate to the database directory (`CourseSchedulerDBWaleBogunjoko944905508`) and **delete the `db.lck` file** if it exists. **Only do this when the application is fully closed.**
3.  **Verify Directory Permissions**:
    *   Ensure the application (and the user running it) has **full read and write permissions** for the main application directory (`c:\Users\Lilly\OneDrive\Desktop\CourseScheduler`) AND the database subdirectory (`CourseSchedulerDBWaleBogunjoko944905508`).
    *   If using OneDrive, Box, Dropbox, or similar sync services, **try pausing synchronization** temporarily to see if file locking by the sync client is interfering. It's often better to run the application from a directory *not* actively synced.
4.  **Check Disk Space**: Ensure there is sufficient disk space on the drive where the database resides.
5.  **Database Corruption (Less Common)**: If `derby.log` indicates file corruption and other steps fail, the database might be corrupt. You may need to delete the entire `CourseSchedulerDBWaleBogunjoko944905508` directory (losing all data) and let the application recreate it on the next run.

### Problem: "Error initializing database" or "Failed to establish a connection"

**Solutions**:

1. Delete the database directory and try again:
   ```
   rm -rf CourseSchedulerDBWaleBogunjoko944905508
   ```
   (On Windows: `rmdir /S /Q CourseSchedulerDBWaleBogunjoko944905508`)

2. Check if another process is using Derby:
   ```
   ps aux | grep derby
   ```
   (On Windows: `tasklist | findstr java`)
   If another Java process related to Derby is running, stop it.

3. Ensure Derby JAR is in the lib directory:
   ```
   ls -la lib/derby.jar
   ```
   (On Windows: `dir lib\derby.jar`)

4. Check file permissions for the database directory and the `derby.log` file. The application needs read/write access.

### Problem: "Database Error: No current connection"

This usually means the application lost its connection to the database or failed to establish one initially, or Derby encountered an internal issue.

**Solutions**:

1.  **Check `derby.log` (Most Important!)**: Look for a file named `derby.log` in the application's root directory (where you run the `java` command, e.g., `c:\Users\Lilly\OneDrive\Desktop\CourseScheduler`). **This file is crucial.** It contains detailed logs from the Derby database engine itself. Look for errors like `ERROR XJ001` or other exceptions around the time the "No current connection" error occurred in the application. These logs often pinpoint the exact reason the connection was lost or invalidated (e.g., database shutdown, lock issues, corruption).
2.  **Check Application Console Output**: Review the console output where you launched the application. Look for earlier `SQLException` messages, especially during startup or connection attempts logged by `DBConnection`.
3.  **Restart the Application**: Sometimes, transient issues can cause connection drops. Close and restart the application.
4.  **Verify Database Directory**: Ensure the database directory (`CourseSchedulerDBWaleBogunjoko944905508`) exists and has not been accidentally deleted or moved while the application was running. Check its permissions.
5.  **Check for Multiple Instances**: Ensure only one instance of the Course Scheduler application is running. Multiple instances trying to access the same embedded database directory will cause conflicts.

### Problem: "Database Error: ResultSet not open. Operation 'next' not permitted. Verify that autocommit is off."

This error means the application tried to read the next row from a database query result (`ResultSet`), but the result set was already closed or invalid. It often occurs during data loading operations (like loading students or courses).

**Solutions**:

1.  **Verify Autocommit**: The error message strongly suggests checking autocommit. The application code (`DBConnection.java`) should ideally set `autocommit` to `false` on the database connection. If you've modified the connection handling, ensure this is being done.
2.  **Check Resource Closing**: Ensure that the `Connection`, `PreparedStatement`, and `ResultSet` are being managed correctly. Using try-with-resources is the best practice. If resources are closed manually, ensure they are not closed *before* the `ResultSet` is fully processed (i.e., before the `while(rs.next())` loop finishes).
3.  **Check for Previous Errors**: Look closely at the console output *before* this error occurred. An earlier `SQLException` during the execution of the query (`pstmt.executeQuery()`) could invalidate the `ResultSet`, leading to this error when `rs.next()` is called later.
4.  **Check `derby.log`**: As always, check the `derby.log` file in the application's root directory for any underlying Derby engine errors that might have caused the `ResultSet` to become invalid.

### Problem: "Database Error: Cannot close a connection while a transaction is still active."

This error typically occurs when the application tries to close a database connection (`Connection.close()`) that has `autocommit` set to `false`, but an active transaction has not been explicitly committed (`commit()`) or rolled back (`rollback()`). Even read operations (`SELECT`) start a transaction when autocommit is off. This often happens when using try-with-resources on a `Connection` object.

**Solutions**:

1.  **Explicit Commit/Rollback**: Ensure that every method performing database operations (including read-only `SELECT` statements) explicitly calls `connection.commit()` upon successful completion or `connection.rollback()` within a `catch` block if an error occurs, *before* the connection is closed or returned to a pool.
2.  **Review Try-With-Resources**: If using try-with-resources for the `Connection` object itself (which might be incorrect if using a singleton connection manager like `DBConnection`), ensure `commit()` or `rollback()` is called before the try block ends. It's generally better to manage the singleton connection lifecycle separately (e.g., open at start, close at end) and only use try-with-resources for `PreparedStatement` and `ResultSet`.
3.  **Check `DBConnection.closeConnection()`**: Ensure the `closeConnection` method in `DBConnection` attempts to rollback any pending transaction before actually closing the connection, especially if `autocommit` was false.

## Configuration File Issues

### Problem: "Could not read configuration file"

**Solutions**:

1. Create a new configuration file:
   ```
   echo "U2FtIFdhbGUg" > scheduler_config.txt
   echo "Q291cnNlIFNjaGVkdWxlciBGYWxsIDIwMjQ=" >> scheduler_config.txt
   ```

2. Ensure file permissions allow reading:
   ```
   chmod 644 scheduler_config.txt
   ```

## User Interface Issues

### Problem: UI appears blank or components are missing

**Solution**: Ensure you have a supported desktop environment:

1. If running remotely, make sure X11 forwarding is enabled:
   ```
   ssh -X user@host
   ```

2. Test with a simpler UI application:
   ```
   java -cp lib/derby.jar:. -Djava.awt.headless=false javax.swing.JFrame
   ```

## Memory Issues

### Problem: "OutOfMemoryError" or application crashes

**Solution**: Increase Java heap size:

```
java -Xmx512m -cp lib/derby.jar:. MainFrame
```

## Fixing Corrupt Database

If the database becomes corrupt, follow these steps:

1. Stop the application
2. Delete the database directory:
   ```
   rm -rf CourseSchedulerDBWaleBogunjoko944905508
   ```
3. Restart the application - it will create a new database

## Starting Over Completely

If you want to reset everything and start fresh:

1. Delete the database directory:
   ```
   rm -rf CourseSchedulerDBWaleBogunjoko944905508
   ```
2. Delete the configuration file:
   ```
   rm scheduler_config.txt
   ```
3. Restart the application

## Still Having Issues?

If none of these solutions work:

1. Try running with verbose output:
   ```
   java -verbose -cp lib/derby.jar:. MainFrame
   ```

2. Check Java version compatibility:
   ```
   java -version
   ```
   (The application requires Java 8 or newer)

3. Contact technical support with the error details