# How to Run the Course Scheduler

This document provides instructions for running the Course Scheduler application.

## Prerequisites

You must have **Java** installed on your system to run this application. The Course Scheduler requires Java 8 or newer.

## How to Check if Java is Installed

Open a terminal/command prompt and type:

```
java -version
```

If Java is installed, you'll see information about the version. If not, you'll get an error.

## Installing Java

### On Windows:
1. Download the JDK from [AdoptOpenJDK](https://adoptium.net/)
2. Run the installer and follow the instructions
3. Verify installation with `java -version`

### On macOS:
1. Install using Homebrew: `brew install openjdk@11`
2. Add to your PATH: `export PATH="/usr/local/opt/openjdk@11/bin:$PATH"`
3. Verify installation with `java -version`

### On Ubuntu/Debian:
```
sudo apt-get update
sudo apt-get install default-jdk
```

### On Fedora/RHEL/CentOS:
```
sudo dnf install java-11-openjdk-devel  # Fedora
sudo yum install java-11-openjdk-devel  # RHEL/CentOS
```

## Running the Application

### Method 1: Using the start script (Linux/macOS)
```
./start.sh
```

### Method 2: Running manually
```
# On Windows:
java -cp "lib/derby.jar;." MainFrame

# On Linux/macOS:
java -cp "lib/derby.jar:." MainFrame
```

## Troubleshooting

If you encounter any issues:

1. Make sure Java is installed and in your PATH
2. Verify that you're in the CourseScheduler directory when running commands
3. If database errors occur, you may need to delete the CourseSchedulerDBWaleBogunjoko944905508 directory and restart

## Getting Help

If you need additional help, please contact your instructor or technical support.