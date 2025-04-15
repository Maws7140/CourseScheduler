#!/bin/bash

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}CourseScheduler Dependency Installer${NC}"
echo "This script will install the Java Development Kit required to run CourseScheduler."

# Detect OS
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$NAME
elif type lsb_release >/dev/null 2>&1; then
    OS=$(lsb_release -si)
elif [ -f /etc/lsb-release ]; then
    . /etc/lsb-release
    OS=$DISTRIB_ID
else
    OS=$(uname -s)
fi

echo "Detected OS: $OS"

# Install Java based on OS
if [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Debian"* ]]; then
    echo "Installing OpenJDK on Ubuntu/Debian..."
    sudo apt update
    sudo apt install -y openjdk-11-jdk
elif [[ "$OS" == *"Fedora"* ]]; then
    echo "Installing OpenJDK on Fedora..."
    sudo dnf install -y java-11-openjdk-devel
elif [[ "$OS" == *"CentOS"* ]] || [[ "$OS" == *"Red Hat"* ]]; then
    echo "Installing OpenJDK on CentOS/RHEL..."
    sudo yum install -y java-11-openjdk-devel
elif [[ "$OS" == *"openSUSE"* ]] || [[ "$OS" == *"SUSE"* ]]; then
    echo "Installing OpenJDK on openSUSE..."
    sudo zypper install -y java-11-openjdk-devel
elif [[ "$OS" == *"Darwin"* ]] || [[ "$OS" == *"macOS"* ]]; then
    echo "For macOS, please install Java using Homebrew:"
    echo "  brew install openjdk@11"
    echo "Then add it to your PATH:"
    echo "  echo 'export PATH=\"/usr/local/opt/openjdk@11/bin:\$PATH\"' >> ~/.zshrc"
    echo "  source ~/.zshrc"
elif [[ "$OS" == *"Windows"* ]]; then
    echo "For Windows, please download and install Java from:"
    echo "  https://adoptium.net/"
else
    echo -e "${RED}Unsupported OS. Please install Java manually.${NC}"
    exit 1
fi

# Check if Java was installed successfully
if command -v java &> /dev/null; then
    echo -e "${GREEN}Java has been installed successfully.${NC}"
    java -version
    echo "You can now run the application using ./start.sh"
else
    echo -e "${RED}Failed to install Java automatically.${NC}"
    echo "Please install Java 8 or newer manually."
    exit 1
fi

exit 0