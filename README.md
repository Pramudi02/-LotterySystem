# LotterySystem

LotterySystem is a modular Java server-client application intended to support user registration, ticket purchase, periodic draws, and result distribution. The design separates core responsibilities into server, client, model, and protocol layers to make the project maintainable and testable.

This repository currently provides a clear skeleton with descriptive placeholders for each component so developers can pick up and implement modules in a consistent way.

## Key features (planned)

- Multi-client server capable of handling user requests (buy tickets, check results).
- Thread-safe data management for users, tickets, and draw results.
- Admin interface for managing draws and viewing statistics.
- Optional HTTP API and NIO server implementation for performance/interop.
- JSON-based protocol for client-server communication.

## Architecture & folder layout

Top-level layout (important files and intended responsibilities):

- `src/`
  - `server/`
    - `LotteryServer.java` — main server entry point; accepts connections and coordinates draws.
    - `ClientHandler.java` — per-connection request handling, authentication, and response dispatch.
    - `DataManager.java` — thread-safe storage and persistence for `User`, `Ticket`, and `LotteryResult` objects.
  - `client/`
    - `UserClientGUI.java` — user-facing client UI (Swing/JavaFX placeholder).
    - `AdminClientGUI.java` — admin interface for managing draws and viewing logs.
  - `optional/`
    - `HttpServerModule.java` — optional REST API layer for external integration.
    - `NIOServer.java` — optional non-blocking server implementation.
    - `FileLogger.java` — optional file-based logging/rotation helper.
  - `model/`
    - `User.java`, `Ticket.java`, `LotteryResult.java` — domain models.
  - `protocol/`
    - `Request.java`, `Response.java`, `MessageParser.java` — message classes and JSON helpers.

- `lib/`
  - `gson-2.10.1.jar` — placeholder for Gson library (if not using Maven dependency)
- `docs/` — Project report and user manual placeholders
- `pom.xml` — Maven build descriptor

## Getting started

### Prerequisites

- Java 11 or later (JDK recommended)
- Maven 3.6+ to build using `pom.xml`
- (Optional) An IDE such as IntelliJ IDEA or Eclipse
