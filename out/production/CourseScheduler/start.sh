#!/bin/bash

# Simple start script for CourseScheduler

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed or not in PATH."
    echo "Please see RUN_ME.md for instructions on installing Java."
    exit 1
fi

# Set classpath separator based on OS
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    PATHSEP=";"
else
    PATHSEP=":"
fi

# Run the application
echo "Starting CourseScheduler..."
java -cp "lib/derby.jar${PATHSEP}." MainFrame

exit $?