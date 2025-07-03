# JavaFX Application

## Overview

This is a JavaFX application built using Gradle. It serves as a template for creating modular Java applications with a graphical user interface (GUI). The application utilizes JavaFX for the UI components and is packaged using the `jpackage` tool for easy distribution.

## Features

- Modular architecture using Java Platform Module System (JPMS).
- Built with JavaFX for a rich user interface.
- Packaged as a standalone application using `jpackage`.

## Prerequisites

- JDK 21 or later
- Gradle 7.0 or later

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/yourusername/j2p-ui.git
cd j2p-ui
```

### Build the Project

To build the project and create the application package, run the following commands:
**WiX toolkit is required for Windows packaging.** It can be downloaded from [WiX Toolset](https://wixtoolset.org/).

```bash
./gradlew clean jlink
./gradlew jpackage
```

### Running the Application

After building the project, you can find the generated application package in the `build/jpackage` directory. 
- For macOS, a `dmg` file will be created,
- For Windows, an `exe` file will be created, 
- and for Linux, a `deb` package will be generated.

To run the application directly from the command line, use:

```bash
./build/jpackage/j2papp.app/Contents/MacOS/j2papp
```

## Usage

Once the application is installed, you can launch it from your Applications folder. The application provides a user-friendly interface for interacting with its features.
This is a simple UI built for [java2python](https://github.com/Arjungowda1/j2pbridge)
