# Electricity Billing DevOps Project — CI Revision & Interview Notes

## 1. CI Objective

Implement Continuous Integration using GitHub Actions to automatically:

1. Checkout source code
2. Set up Java 17
3. Start temporary MySQL 8.0
4. Run unit tests
5. Run integration tests
6. Build and verify the application

### High-level structure

```text
Developer
   |
   | git push / Pull Request
   v
GitHub Repository
   |
   v
GitHub Actions Runner
   |
   +-------------------------+
   |                         |
   v                         v
Unit Tests              Integration Tests
   |                         |
BillingService          Spring Boot + MySQL
   |                         |
   +------------+------------+
                |
                v
          Maven Verify
                |
                v
             CI PASS
```

---

## 2. Testing Structure

```text
src/test/java
       |
       +-----------------------------+
       |                             |
       v                             v
BillingServiceTest.java       ElectricityBillingApplicationIT.java
       |                             |
       v                             v
    Unit Test                  Integration Test
       |                             |
       v                             v
 Business Logic             Spring Boot Context
                                     |
                                     v
                                   MySQL
```

| Test | Type | Purpose | Database? | Maven |
|---|---|---|---|---|
| `BillingServiceTest` | Unit | Billing calculation logic | No | Surefire |
| `ElectricityBillingApplicationIT` | Integration | Spring Boot + DB startup | Yes | Failsafe |

### Why separate them?

Unit tests are fast and isolated. Integration tests verify that multiple components and dependencies work together.

Interview answer:

> I separated unit and integration tests because they have different responsibilities and dependencies. Unit tests validate business logic in isolation and do not need infrastructure, while integration tests validate interaction between the application and dependencies such as MySQL. This makes failures easier to diagnose and gives faster feedback.

---

# 3. Maven Testing Structure

### Surefire — Unit Tests

```text
mvn clean test
      |
      v
Maven Surefire
      |
      v
BillingServiceTest
      |
      v
5 tests
```

### Failsafe — Integration Tests

```text
mvn clean verify
      |
      v
Maven Failsafe
      |
      v
ElectricityBillingApplicationIT
      |
      v
Spring Boot + MySQL
```

The integration test was renamed from:

```text
ElectricityBillingApplicationTests.java
```

to:

```text
ElectricityBillingApplicationIT.java
```

This follows the conventional `IT` naming used for integration tests.

---

# 4. Issue 1 — Maven Wrapper Permission Error

## Problem

The first GitHub Actions run failed while executing:

```bash
./mvnw clean test
```

with:

```text
Permission denied
Process completed with exit code 126
```

## Root cause

GitHub Actions used a Linux-based runner. The `mvnw` file did not have the executable permission required by Linux.

It worked differently on the local Windows environment.

## Resolution

```cmd
git update-index --chmod=+x mvnw
```

Then the change was committed and pushed.

## Result

GitHub Actions could execute the Maven Wrapper successfully.

### Interview point

> File permissions can differ across operating systems. The Maven Wrapper needed the Linux executable bit, so I stored that permission in Git.

---

# 5. Issue 2 — Integration Test Failed Because MySQL Was Unavailable

After fixing the Maven Wrapper, unit tests passed but the integration test failed with:

```text
CannotGetJdbcConnectionException
java.net.ConnectException: Connection refused
```

## Why?

The integration test uses:

```java
@SpringBootTest
```

This starts the Spring Boot application context.

The application also uses:

```properties
spring.sql.init.mode=always
```

so Spring tries to connect to MySQL and initialize the database schema during startup.

The application was configured for:

```text
localhost:3306
```

but the CI runner did not have the required MySQL service.

### Failure flow

```text
GitHub Actions Runner
        |
        v
Spring Boot
        |
        v
Hikari Connection Pool
        |
        v
localhost:3306
        |
        v
MySQL unavailable
        |
        v
Connection refused
        |
        v
Integration Test FAILED
```

### Important conclusion

The integration test was not failing because Failsafe could not find it. The test was discovered and executed; the database dependency was unavailable.

---

# 6. Why We Tested Local MySQL First

We first verified the integration test locally.

Start MySQL:

```cmd
net start MySQL80
```

Run:

```cmd
mvnw.cmd failsafe:integration-test
```

Result:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### What this proved

```text
Spring Boot
    |
    v
Hikari
    |
    v
Local MySQL
    |
    v
Connection successful
    |
    v
Integration Test PASS
```

This isolated the original problem to the CI environment's missing database service.

**Local MySQL is only a development/test dependency here. It is not our production database.**

---

# 7. Solution — Temporary MySQL Service in GitHub Actions

We configured GitHub Actions with:

```yaml
services:
  mysql:
    image: mysql:8.0
    env:
      MYSQL_DATABASE: electricity_billing
      MYSQL_ROOT_PASSWORD: root
    ports:
      - 3306:3306
    options: >-
      --health-cmd="mysqladmin ping -h localhost -uroot -proot"
      --health-interval=10s
      --health-timeout=5s
      --health-retries=5
```

### Architecture

```text
GitHub Actions Runner
          |
          +----------------------+
          |                      |
          v                      v
    Spring Boot              MySQL 8.0
          |                      |
          +----------+-----------+
                     |
                     v
             Integration Test
```

The MySQL container is temporary and exists for the CI job.

---

# 8. Why the MySQL Health Check?

A container being started does not always mean the application inside it is ready.

Without a health check:

```text
Start MySQL
    |
    v
MySQL still initializing
    |
    v
Spring Boot starts
    |
    v
Connection attempt
    |
    v
Possible failure
```

With a health check:

```text
Start MySQL
    |
    v
Check health
    |
    +---- Not ready ----> Retry
    |
    +---- Ready --------> Tests
```

### Interview definition

> A health check determines whether a service is running and ready to perform its expected function.

---

# 9. CI Database Configuration

GitHub Actions supplies:

```yaml
env:
  DB_HOST: localhost
  DB_PORT: 3306
  DB_NAME: electricity_billing
  DB_USER: root
  DB_PASSWORD: root
```

Spring Boot uses:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:electricity_billing}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD}
```

### Why environment variables?

They allow the same application to run with different environment-specific configuration.

```text
Local
  |
  +--> Local MySQL

Docker Compose
  |
  +--> MySQL container

GitHub Actions
  |
  +--> Temporary MySQL service

AWS
  |
  +--> AWS infrastructure
```

The source code does not need to be changed for each environment.

---

# 10. Issue 3 — GitHub Actions YAML Syntax Error

After adding the MySQL service, GitHub reported:

```text
Invalid workflow file
You have an error in your yaml syntax on line 14
```

## Root cause

The YAML contained tab-based indentation.

YAML indentation must use spaces consistently.

## Resolution

The workflow was reformatted using spaces.

We also checked:

```cmd
git diff --check
```

Then committed and pushed the corrected workflow.

## Result

The workflow was accepted and ran successfully.

### Interview point

> YAML is indentation-sensitive. A tab can make a GitHub Actions workflow invalid, so I use spaces consistently and validate the file before pushing.

---

# 11. Final CI Flow

```text
Git Push / Pull Request
          |
          v
   GitHub Actions Runner
          |
          +--------------------+
          |                    |
          v                    v
   Start MySQL 8.0       Checkout Code
          |                    |
          +---------+----------+
                    |
                    v
              Set up Java 17
                    |
                    v
             ./mvnw clean verify
                    |
          +---------+---------+
          |                   |
          v                   v
     Unit Tests        Integration Test
          |                   |
       5 tests          Spring Boot + MySQL
          |                   |
          +---------+---------+
                    |
                    v
                   PASS
```

Final result:

```text
Unit Tests:        5 passed
Integration Test:  1 passed
GitHub Actions:    PASS
```

---

# 12. Why `mvn clean verify`?

The final workflow uses:

```bash
./mvnw clean verify
```

instead of only:

```bash
./mvnw clean test
```

The lifecycle is approximately:

```text
clean
  |
  v
Remove previous build output
  |
  v
compile
  |
  v
test
  |
  v
Unit Tests
  |
  v
integration-test
  |
  v
Integration Tests
  |
  v
verify
  |
  v
Final verification
```

Because Failsafe is configured for the integration-test/verify lifecycle, `verify` is the appropriate stage for the complete CI verification.

---

# 13. Important CI Interview Definitions

## Continuous Integration (CI)

> Continuous Integration is the practice of frequently integrating code changes into a shared repository and automatically building and testing those changes.

Project example:

```text
git push
   |
   v
GitHub Actions
   |
   v
Build + Test
```

---

## Continuous Delivery

> Continuous Delivery keeps software in a releasable state through automated build, test, and deployment-preparation processes. Deployment may require a manual approval.

---

## Continuous Deployment

> Continuous Deployment automatically deploys every change that successfully passes the required pipeline stages.

### Difference

```text
CI
 |
 +--> Build + Test

Continuous Delivery
 |
 +--> Build + Test + Release Preparation
 |
 +--> Deployment may require approval

Continuous Deployment
 |
 +--> Build + Test
 |
 +--> Automatic Deployment
```

---

## GitHub Actions

> GitHub Actions is GitHub's automation platform for building CI/CD workflows in response to repository events such as pushes and pull requests.

---

## Workflow

> A workflow is an automated process defined in a YAML file that GitHub Actions executes.

Project file:

```text
.github/workflows/ci.yml
```

---

## Job

> A job is a group of steps executed together on a runner.

Example:

```yaml
jobs:
  build-and-test:
```

---

## Step

> A step is an individual task within a GitHub Actions job.

Example:

```yaml
uses: actions/checkout@v4
```

or:

```yaml
run: ./mvnw clean verify
```

---

## Runner

> A runner is the machine/environment where a GitHub Actions job executes.

Our workflow uses:

```yaml
runs-on: ubuntu-latest
```

so the job runs on a GitHub-hosted Linux runner.

---

## Service Container

> A service container is a temporary container started alongside a CI job to provide a dependency such as MySQL, PostgreSQL, or Redis.

Our project uses:

```text
GitHub Actions Runner
       |
       +--> Test/Application process
       |
       +--> MySQL service container
```

---

## Unit Testing

> Unit testing verifies a small unit of application logic independently from external dependencies.

---

## Integration Testing

> Integration testing verifies that multiple components or systems work correctly together.

Our example:

```text
Spring Boot
    +
DataSource
    +
MySQL
    =
Integration Test
```

---

## Test Isolation

> Test isolation means a test should depend only on the components necessary for the behavior being tested and should avoid unnecessary external state.

---

## Maven

> Maven is a Java build and dependency-management tool used for dependency management, compilation, testing, packaging, and build lifecycle execution.

---

## Maven Wrapper

> Maven Wrapper allows a project to invoke Maven through project-provided wrapper scripts without requiring a globally installed Maven version.

Windows:

```text
mvnw.cmd
```

Linux/macOS:

```text
./mvnw
```

---

## Maven Surefire

> Maven Surefire is commonly used to execute unit tests during Maven's `test` phase.

---

## Maven Failsafe

> Maven Failsafe is designed for integration tests and normally executes them during the later `integration-test` and `verify` lifecycle.

---

## Build Artifact

> A build artifact is an output produced by the build process, such as a JAR file.

Project output:

```text
target/
   |
   +--> electricity-billing-*.jar
```

---

## Environment Variable

> An environment variable is a runtime configuration value supplied outside the application source code.

Examples:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USER
DB_PASSWORD
```

---

## Configuration Externalization

> Configuration externalization means keeping environment-specific configuration outside application code so the same application can run in different environments.

---

## Database Dependency

> A database dependency is an external service required by an application for database-related operations.

---

## Health Check

> A health check determines whether a service is available and ready to perform its expected function.

---

## CI Failure vs Application Failure

A useful troubleshooting distinction:

```text
CI / Environment Failure
   |
   +--> YAML syntax
   +--> permissions
   +--> missing environment variables
   +--> unavailable service
   +--> runner configuration

Application Failure
   |
   +--> Java code
   +--> business logic
   +--> Spring configuration
   +--> SQL/query problems
```

The earlier `Connection refused` error was an environment/dependency problem: MySQL was unavailable.

---

# 14. Interview Troubleshooting Story

A concise interview-ready answer:

> "I initially configured GitHub Actions to run the Maven build and tests. The first failure was a Maven Wrapper permission issue because the GitHub-hosted runner was Linux-based and `mvnw` did not have executable permission. I fixed that using `git update-index --chmod=+x mvnw`.
>
> After that, the unit tests passed, but the Spring Boot integration test failed with a database connection refused error. I checked the logs and found that `@SpringBootTest` was starting the application and Spring was trying to connect to MySQL, but the CI runner did not have our local MySQL service.
>
> I first verified the integration test locally by starting MySQL and running the Failsafe integration test. It passed, confirming that the test itself was correct.
>
> I separated unit and integration tests because unit tests should be fast and independent, while integration tests validate the application together with dependencies such as MySQL. I configured Surefire for unit tests and Failsafe for integration tests.
>
> Finally, I added a temporary MySQL 8 service container to GitHub Actions with a health check and supplied the database configuration through environment variables. The final pipeline uses `mvn clean verify`, and both unit and integration tests pass."

---

# 15. Troubleshooting Checklist

When a CI pipeline fails:

```text
1. Check workflow YAML
        |
2. Check runner
        |
3. Check executable permissions
        |
4. Check Java/tool versions
        |
5. Check dependencies/services
        |
6. Check environment variables
        |
7. Check unit-test results
        |
8. Check integration-test results
        |
9. Check database/service readiness
        |
10. Check application logs
```

---

# 16. Quick Revision

### One-line memory aid

> **Unit = isolated logic. Integration = components working together. Surefire = unit-test phase. Failsafe = integration-test lifecycle. GitHub Actions = CI automation. Service container = temporary dependency. Health check = readiness check.**

### CI architecture to remember

```text
Git Push
   |
   v
GitHub Actions
   |
   +--> Java 17
   |
   +--> MySQL Service
   |
   +--> Maven
          |
          +--> Unit Tests
          |
          +--> Integration Tests
          |
          +--> Verify
   |
   v
CI PASS
```

---

# 17. CI Stage Lessons

1. CI must provide the dependencies required by integration tests.
2. Unit and integration tests have different responsibilities.
3. Separating tests makes troubleshooting easier.
4. Surefire and Failsafe have different roles.
5. Linux CI runners can expose permission issues not visible on Windows.
6. YAML is indentation-sensitive.
7. Service readiness matters, not only service startup.
8. Environment variables make configuration portable.
9. Temporary service containers are useful for CI integration tests.
10. Troubleshooting should follow logs and root-cause analysis.

---

# 18. Project Progress

Completed:

```text
Java Application
       ↓
Spring Boot
       ↓
REST API
       ↓
JDBC + MySQL
       ↓
Maven
       ↓
Docker
       ↓
Docker Compose
       ↓
GitHub Actions CI
       ↓
Unit Tests
       ↓
Integration Tests
       ↓
Temporary MySQL in CI
       ↓
CI PASS
```

Next:

```text
CI
 |
 v
Ansible
 |
 v
Terraform
 |
 v
AWS EC2 Deployment
 |
 v
Kubernetes
 |
 v
Prometheus + Grafana
 |
 v
Final Documentation + Resume
```

---

# 19. Commands Used

Start local MySQL:

```cmd
net start MySQL80
```

Run unit tests:

```cmd
mvnw.cmd clean test
```

Run integration tests:

```cmd
mvnw.cmd failsafe:integration-test
```

Run complete verification:

```cmd
mvnw.cmd clean verify
```

Fix Maven Wrapper executable permission:

```cmd
git update-index --chmod=+x mvnw
```

Check status:

```cmd
git status
```

Check whitespace:

```cmd
git diff --check
```

Review workflow:

```cmd
git diff -- .github/workflows/ci.yml
```

Push:

```cmd
git push
```

---

# 20. Final CI Status

```text
Maven Wrapper Permission       ✅ Resolved
Unit Tests                     ✅ 5 passed
Integration Test               ✅ 1 passed
Local MySQL Verification       ✅ Passed
GitHub Actions MySQL Service   ✅ Configured
MySQL Health Check             ✅ Configured
YAML Formatting Issue          ✅ Resolved
GitHub Actions CI              ✅ Passed
```

**CI stage completed successfully.**
