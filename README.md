# Course Scheduler

A comprehensive Java desktop application for scheduling courses, managing students, and tracking class enrollments.

## 🌐 Live Demo

Visit the web showcase: **[https://samw7140.github.io/CourseScheduler/](https://samw7140.github.io/CourseScheduler/)**

The web showcase provides a complete overview of the application's features, screenshots, and setup instructions - perfect for demonstrating capabilities to potential employers.

## Prerequisites

- Java 8 or newer (JDK)
- Apache Derby (included in `/lib` directory)

## Getting Started

There are multiple ways to run the application:

### 1. Using the start script (Linux/macOS/Windows with Git Bash)

Simply run the start script, which will check for Java, compile the code if needed, and start the application:

```bash
./start.sh
```

### 2. Using Apache Ant (if installed)

If you have Apache Ant installed, you can use the build file:

```bash
ant run
```

This will compile the code, create a JAR file in the `dist` directory, and run the application.

### 3. Manual compilation and execution

```bash
# Create bin directory if it doesn't exist
mkdir -p bin

# Compile the code
javac -d bin -cp "lib/derby.jar:." src/*.java

# Run the application
java -cp "bin:lib/derby.jar:." MainFrame
```

Note: On Windows, use `;` instead of `:` in the classpath.

## Features

- Add and manage semesters
- Add courses with descriptions
- Create classes for courses with seat limits
- Add students to the system
- Schedule students for classes (with automatic waitlisting)
- View schedules for individual students
- View all classes in a semester
- View students enrolled in a specific class
- Drop students from classes (with automatic promotion from waitlist)
- Drop entire classes
- Drop students from the system

## Project Structure

- `src/` - Java source files
- `lib/` - Required libraries (Derby)
- `bin/` - Compiled class files (created during build)
- `dist/` - Distribution files (created by Ant)
- `scheduler_config.txt` - Configuration file for author information

## Database

The application uses an embedded Derby database which is automatically created in the directory:
`CourseSchedulerDBWaleBogunjoko944905508`

The database tables will be automatically created on first run if they don't exist.

## Troubleshooting

If you encounter any issues:

1. Ensure Java 8+ is installed and in your PATH
2. Check that Derby JAR is in the lib directory
3. For database issues, delete the database directory and restart (this will reset all data)

## Web Showcase

This project includes a professional web showcase deployed on GitHub Pages at:
**[https://samw7140.github.io/CourseScheduler/](https://samw7140.github.io/CourseScheduler/)**

The showcase features:
- Complete application overview and feature descriptions
- Mock interface screenshots
- Detailed installation instructions
- Technology stack documentation
- Professional presentation suitable for job fair demonstrations