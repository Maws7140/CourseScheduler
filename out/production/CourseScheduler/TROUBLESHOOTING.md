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

3. Ensure Derby JAR is in the lib directory:
   ```
   ls -la lib/derby.jar
   ```
   (On Windows: `dir lib\derby.jar`)

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