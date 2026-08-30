# GoRest API Automation Test Suite

A robust, maintainable, and scalable API automation testing framework for the [GoRest Public API v2](https://gorest.co.in/public/v2). Built with **Java 17**, **REST Assured**, **JUnit 5**, and **Allure Reporting**.
<img width="1468" height="817" alt="Screenshot 2569-08-30 at 23 30 42" src="https://github.com/user-attachments/assets/99f4881b-25a2-4d6e-9dcd-178f514049c5" />

---

## 📑 Table of Contents

- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Prerequisites & Installation](#-prerequisites--installation)
- [Environment Configuration](#-environment-configuration)
- [How to Run Tests](#-how-to-run-tests)
- [Architectural Decisions](#-architectural-decisions)
- [Test Scenarios Covered](#-test-scenarios-covered-23-scenarios)
- [Future Improvements](#-future-improvements)

---

## 🛠 Tech Stack

| Technology | Purpose |
|---|---|
| **Java 17+** | Core programming language |
| **REST Assured 5.4.0** | REST API testing & HTTP client |
| **JUnit 5 (Jupiter 5.10.2)** | Test runner, lifecycle management, and ordering |
| **Jackson 2.17.0** | JSON serialization & deserialization (POJO Models) |
| **JavaFaker 1.0.2** | Dynamic and randomized test data generation |
| **AssertJ 3.25.3** | Fluent assertions with descriptive error messaging |
| **SLF4J + Logback 1.5.3** | Timestamped request/response logging |
| **Allure Framework 2.25.0** | Interactive and detailed test reporting |

---

## 📁 Project Structure

```
gorest-api-tests/
├── pom.xml                                   # Maven dependencies and build configuration
├── .env                                      # Local environment credentials (excluded from git)
├── .gitignore                                # Git ignore rules
├── README.md                                 # Documentation
│
├── src/main/java/com/gorest/
│   ├── client/
│   │   └── UserApiClient.java                # Reusable Service Layer / HTTP Client
│   ├── config/
│   │   └── ConfigManager.java                # Multi-source configuration loader
│   └── models/
│       └── User.java                         # POJO model with Jackson annotations
│
└── src/test/java/com/gorest/
    ├── base/
    │   └── BaseTest.java                     # Base test setup & REST Assured filters
    ├── helpers/
    │   ├── AssertionHelper.java              # Reusable custom assertion utilities
    │   └── TestDataGenerator.java            # Dynamic faker data generator
    └── tests/
        ├── UserCrudTest.java                 # Scenarios 1–6: Happy Path CRUD & listing
        ├── UserValidationTest.java           # Scenarios 7–15: Error handling & 422 validations
        └── UserEdgeCaseTest.java             # Scenarios 16–23: 404, 401 auth & boundary limits
```

---

## ⚙️ Prerequisites & Installation

### 1. Requirements
* **JDK 17 or higher** installed (`java -version`)
* **Apache Maven 3.8+** installed (`mvn -v`) *(Optional if running via IntelliJ IDEA)*
* **Git** installed

### 2. Setup Project
Clone the repository:
```bash
git clone <your-repository-url>
cd gorest-api-tests
```

---

## 🔐 Environment Configuration

Create a `.env` file in the project root directory (this file is ignored in `.gitignore` to prevent leaking credentials):

```properties
GOREST_API_TOKEN=your_bearer_token_here
BASE_URL=https://gorest.co.in/public/v2
```

> **Note:** The `ConfigManager` automatically resolves configurations using the following precedence:
> 1. System Properties (`-DGOREST_API_TOKEN=...`)
> 2. Local `.env` file
> 3. OS Environment Variables
> 4. Default Fallbacks

---

## 🚀 How to Run Tests

### 1. Run all tests via Maven CLI
```bash
mvn clean test
```
### 2. Run a specific test class
```bash
mvn test -Dtest=UserCrudTest
mvn test -Dtest=UserValidationTest
mvn test -Dtest=UserEdgeCaseTest
```

### 3. Run inside IntelliJ IDEA
1. Open IntelliJ IDEA $\rightarrow$ **File** $\rightarrow$ **Open** $\rightarrow$ Select the project root folder.
2. Allow Maven to import and resolve dependencies.
3. Open any test file under `src/test/java/com/gorest/tests/` and click the green **▶ Play button** next to the class or method.

### 4. Generate and View Allure Report
```bash
# Generate and open report in your default browser
mvn allure:serve
```

---

## 🏗 Architectural Decisions

1. **Service Layer Pattern (`UserApiClient.java`)**:
   - Encapsulates all HTTP request specifications, headers, parameters, and endpoints in one place.
   - Test methods remain clean, declarative, and focused solely on assertions rather than low-level HTTP handling.

2. **Model Binding / DTO Pattern (`User.java`)**:
   - Uses Jackson `@JsonInclude(JsonInclude.Include.NON_NULL)` and `@JsonIgnoreProperties(ignoreUnknown = true)`.
   - Enables type-safe request payload creation and response deserialization (`response.as(User.class)`).

3. **Dynamic Test Data Generation (`TestDataGenerator.java`)**:
   - Utilizes `JavaFaker` combined with nanosecond timestamps (`test_<nano>@domain.com`) to guarantee 100% unique emails across runs, preventing false-negative `422 Email already taken` failures.

4. **Reusable Assertion Utility (`AssertionHelper.java`)**:
   - `assertStatusCode(Response, int)`: Validates status codes and logs the response body automatically upon failure.
   - `assertValidationError(Response, String, String)`: Uses Groovy GPath expressions (`find { it.field == '...' }.message`) to target specific error fields from GoRest validation array responses.
   - `assertErrorMessage(Response, String)`: Validates single error objects (e.g., `{"message": "Resource not found"}`).

5. **Behavioral Separation of Test Suites**:
   - Tests are segregated by behavioral intent (`UserCrudTest`, `UserValidationTest`, `UserEdgeCaseTest`) rather than jamming all scenarios into a single bloated class.

---

## 🧪 Test Scenarios Covered (23 Scenarios)

### 1. `UserCrudTest.java` — Happy Path & Business Flow
| # | Scenario / Display Name | Method | Expected Status | Description / Validation |
|---|---|---|---|---|
| 1 | `[Scenario 1] GET /users` | GET | `200 OK` | Verify user list is retrieved and is non-empty |
| 2 | `[Scenario 2] POST /users` | POST | `201 Created` | Create new user with random data & verify fields match |
| 3 | `[Scenario 3] GET /users (List verification)` | GET | `200 OK` | Confirm created user ID exists in user list |
| 4 | `[Scenario 4] GET /users/{id}` | GET | `200 OK` | Fetch created user by ID & verify field persistence |
| 5 | `[Scenario 5] PUT /users/{id}` | PUT | `200 OK` | Update user name/gender/status and verify response |
| 6 | `[Scenario 6] DELETE /users/{id}` | DELETE | `204 No Content` | Delete user and verify subsequent GET returns `404` |

---

### 2. `UserValidationTest.java` — Error Handling & Validation (422)
| # | Scenario / Display Name | Method | Expected Status | Description / Validation |
|---|---|---|---|---|
| 7 | `[Scenario 7] POST /users (Duplicate Email)` | POST | `422 Unprocessable` | Reject duplicate email with `"has already been taken"` |
| 8 | `[Scenario 8] POST /users (Missing Required Fields)` | POST | `422 Unprocessable` | Reject empty payload; verify field errors for `name`, `email`, `gender`, `status` |
| 9 | `[Scenario 9] PUT /users (Missing Required Fields)` | PUT | `422 Unprocessable` | Reject update with empty fields; verify all 4 validation messages |
| 10 | `[Scenario 10] POST /users (Invalid Gender)` | POST | `422 Unprocessable` | Reject invalid gender with `"can't be blank, can be male of female"` |
| 11 | `[Scenario 11] PUT /users/{id} (Invalid Gender)` | PUT | `422 Unprocessable` | Reject update with invalid gender value |
| 12 | `[Scenario 12] POST /users (Invalid Status)` | POST | `422 Unprocessable` | Reject invalid status with `"can't be blank"` |
| 13 | `[Scenario 13] PUT /users/{id} (Invalid Status)` | PUT | `422 Unprocessable` | Reject update with invalid status value |
| 14 | `[Scenario 14] POST /users (Invalid Email Format)` | POST | `422 Unprocessable` | Reject malformed email with `"is invalid"` |
| 15 | `[Scenario 15] PUT /users (Invalid Email Format)` | PUT | `422 Unprocessable` | Reject update with malformed email |

---

### 3. `UserEdgeCaseTest.java` — Edge Cases, Security & Boundaries
| # | Scenario / Display Name | Method | Expected Status | Description / Validation |
|---|---|---|---|---|
| 16 | `[Scenario 16] GET /users/{id} (Non-existent ID)` | GET | `404 Not Found` | Verify message: `{"message": "Resource not found"}` |
| 17 | `[Scenario 17] POST /users (Invalid Token)` | POST | `401 Unauthorized` | Verify authentication failure: `{"message": "Invalid token"}` |
| 18 | `[Scenario 18] DELETE /users/{id} (Non-existent ID)` | DELETE | `404 Not Found` | Verify deletion of non-existent resource returns 404 |
| 19 | `[Scenario 19] PUT /users/{id} (Non-existent ID)` | PUT | `404 Not Found` | Verify update of non-existent resource returns 404 |
| 20 | `[Scenario 20] POST /users (Name > 200 chars)` | POST | `422 Unprocessable` | Boundary check: `"is too long (maximum is 200 characters)"` |
| 21 | `[Scenario 21] POST /users (Email > 200 chars)` | POST | `422 Unprocessable` | Boundary check: length limit & format validation |
| 22 | `[Scenario 22] PUT /users (Name > 200 chars)` | PUT | `422 Unprocessable` | Boundary check on update for name length limit |
| 23 | `[Scenario 23] PUT /users (Email > 200 chars)` | PUT | `422 Unprocessable` | Boundary check on update for email length limit |
| 24 | `[Scenario 24] POST /users (Special Character)` | POST | `201 Created` | Boundary check on create user name with special character (consider should be fail in real life scenario, but it's pass) |
| 25 | `[Scenario 25] PUT /users (Special Character)` | PUT | `200 OK` | Boundary check on update user name with special character (consider should be fail in real life scenario, but it's pass) |

---

## 🎯 Future Improvement

### Short-term (1-2 weeks)
- Implement JSON schema validation for responses
- Enhance error scenario coverage (negative tests)

### Medium-term (3-4 weeks)
- Performance baseline testing



