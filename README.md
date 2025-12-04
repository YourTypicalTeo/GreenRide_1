#-- GreenRide 🌿--
## Features
* **Role-Based Access:** Separate flows for Drivers, Passengers, and Administrators.
* **Distributed Architecture:** Consumes an external **NOC Service** (simulated) for SMS notifications.
* **Dual Security:**
    * **Web UI:** Stateful session-based authentication (Cookies).
    * **REST API:** Stateless JWT authentication with strict security (Issuer/Audience checks).
* **Traffic Control:** Rate limiting (50 requests/min) using **Bucket4j**.
* **Automation:** Scheduled background tasks send SMS reminders 1 hour before a ride departs.
* **Concurrency Safety:** Atomic database updates prevent overbooking.

## Tech Stack
* **Java 21** & **Spring Boot 3.5**
* **Database:** H2 (File-based)
* **Security:** Spring Security 6, JJWT 0.12
* **Frontend:** Thymeleaf, Bootstrap 5
* **Documentation:** OpenAPI (Swagger UI)

## Prerequisites
* Java 21 or higher installed.
* (Optional) The separate **HUA NOC Service** running on port `8081` for SMS functionality.

## How to Run

1.  **Start the Application:**
    ```bash
    # Windows
    mvnw.cmd spring-boot:run

    # Mac/Linux
    ./mvnw spring-boot:run
    ```

2.  **Access the App:**
    * **Web UI:** [http://localhost:8080](http://localhost:8080)
    * **Swagger API Docs:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Default Accounts
* **Admin User:** (Created automatically if you register a user and manually assign role `2` in H2 console)
* **Test User:** Register a new account via the UI to get started.
