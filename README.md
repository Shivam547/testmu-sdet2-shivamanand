# TestMu SDET-2 Test Automation Framework

> A unified, scalable test automation framework for UI, API, and Integration testing built with Selenium WebDriver and Java.
> Live Hosted Site - https://shivam547.github.io/testmu-sdet2-shivamanand/

---

## 🎯 What We're Building

This framework solves the problem of **fragmented, unreliable test suites** by providing:

1. **Unified Testing Platform** - UI, API, and Integration tests in one repository
2. **Reliable Execution** - Built-in retry mechanisms, robust waits, and stable selectors
3. **Actionable Reporting** - Allure reports with screenshots, logs, and failure artifacts
4. **CI/CD Ready** - GitHub Actions pipeline with parallel execution and notifications

### Problem Statement
> "Our regression suite is a mess. UI and API tests live in separate repos, there's no shared framework, half the tests are flaky, and nobody trusts the report."

### Our Solution
A framework designed for **reliability** and **maintainability** that any engineer can extend without asking questions.

---

## 📋 Table of Contents

- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Running Tests](#running-tests)
- [Test Reports](#test-reports)
- [Configuration](#configuration)
- [Design Decisions](#design-decisions)
- [Future Improvements](#future-improvements)

---

## 🏗️ Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed architecture documentation.

```
┌─────────────────────────────────────────────────────────────────┐
│                    Test Automation Framework                     │
├─────────────────────────────────────────────────────────────────┤
│  TEST LAYER                                                      │
│  ├── UI Tests (Login, Dashboard, Forms, Cross-Browser)          │
│  ├── API Tests (CRUD, Auth, Errors, Schema, Performance)        │
│  └── Integration Tests (API → UI End-to-End Flows)              │
├─────────────────────────────────────────────────────────────────┤
│  FRAMEWORK LAYER                                                 │
│  ├── Pages (BasePage + Page Objects with encapsulated locators) │
│  ├── API Client (REST operations, auth handling, logging)       │
│  ├── Utilities (WaitUtils, RetryUtils, ScreenshotUtils, etc.)   │
│  └── Listeners (TestListener, RetryAnalyzer)                    │
├─────────────────────────────────────────────────────────────────┤
│  DATA & CONFIG LAYER                                             │
│  ├── Environment Configs (dev, staging, prod)                   │
│  ├── Test Data (JSON, YAML, CSV - externalized)                 │
│  └── JSON Schemas (API response validation)                     │
├─────────────────────────────────────────────────────────────────┤
│  EXECUTION & REPORTING                                           │
│  ├── TestNG (Parallel execution, XML suites, data providers)    │
│  ├── Allure Reports (Screenshots, steps, attachments)           │
│  └── GitHub Actions (CI/CD, artifacts, notifications)           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| Language | Java | 17 | Core programming language |
| UI Automation | Selenium WebDriver | 4.18.x | Browser automation |
| Driver Management | WebDriverManager | 5.7.x | Automatic driver setup |
| API Testing | REST Assured | 5.4.x | REST API testing |
| Test Runner | TestNG | 7.9.x | Test execution & parallel runs |
| Build Tool | Maven | 3.9.x | Dependency & build management |
| Reporting | Allure | 2.25.x | Rich test reports |
| Logging | Log4j2 | 2.22.x | Structured logging |
| Data Parsing | Jackson | 2.16.x | JSON/YAML processing |
| Assertions | AssertJ | 3.25.x | Fluent assertions |
| CI/CD | GitHub Actions | - | Automated pipeline |

---

## 📦 Prerequisites

- **Java JDK 17+** - [Download](https://adoptium.net/)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **Git** - [Download](https://git-scm.com/downloads)
- **Chrome/Firefox Browser** - Latest version
- **Allure CLI** (optional) - `brew install allure` (macOS)

---

## 🚀 Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/testmu-sdet2-shivamanand.git
cd testmu-sdet2-shivamanand

# 2. Install dependencies
mvn clean install -DskipTests

# 3. Run smoke tests
mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml

# 4. Generate and view report
mvn allure:serve
```

---

## 📁 Project Structure

```
testmu-sdet2-shivamanand/
├── src/
│   ├── main/java/com/testmu/
│   │   ├── config/              # Configuration readers
│   │   │   ├── ConfigReader.java
│   │   │   └── EnvironmentConfig.java
│   │   ├── pages/               # Page Object Model classes
│   │   │   ├── BasePage.java
│   │   │   ├── LoginPage.java
│   │   │   ├── DashboardPage.java
│   │   │   └── FormPage.java
│   │   ├── api/                 # API testing components
│   │   │   ├── ApiClient.java
│   │   │   ├── endpoints/
│   │   │   └── models/
│   │   ├── utils/               # Utility classes
│   │   │   ├── WaitUtils.java
│   │   │   ├── RetryUtils.java
│   │   │   ├── ScreenshotUtils.java
│   │   │   ├── JsonUtils.java
│   │   │   ├── AssertionUtils.java
│   │   │   └── BrowserFactory.java
│   │   └── listeners/           # TestNG listeners
│   │       ├── TestListener.java
│   │       └── RetryAnalyzer.java
│   └── test/
│       ├── java/com/testmu/tests/
│       │   ├── BaseTest.java    # Base test class
│       │   ├── ui/              # UI test classes
│       │   │   ├── LoginTest.java
│       │   │   ├── DashboardTest.java
│       │   │   ├── FormValidationTest.java
│       │   │   └── CrossBrowserTest.java
│       │   ├── api/             # API test classes
│       │   │   ├── CrudApiTest.java
│       │   │   ├── AuthApiTest.java
│       │   │   ├── ErrorHandlingTest.java
│       │   │   └── SchemaValidationTest.java
│       │   └── integration/     # Integration test classes
│       │       └── ApiToUIIntegrationTest.java
│       └── resources/
│           ├── testng.xml
│           ├── testng-smoke.xml
│           ├── testng-regression.xml
│           ├── testng-crossbrowser.xml
│           └── allure.properties
├── testdata/                    # External test data
│   ├── login-data.json
│   ├── users.csv
│   ├── form-data.yaml
│   └── api-payloads/
├── config/                      # Environment configs
│   ├── config.properties
│   ├── config-dev.properties
│   ├── config-staging.properties
│   └── config-prod.properties
├── schemas/                     # JSON schemas for validation
│   └── user-schema.json
├── reports/                     # Generated reports
├── screenshots/                 # Failure screenshots
├── .github/workflows/           # CI/CD pipelines
│   └── test-pipeline.yml
├── pom.xml                      # Maven configuration
├── README.md                    # This file
├── ARCHITECTURE.md              # Architecture documentation
├── test-strategy.md             # Test strategy document
└── ai-usage-log.md              # AI tool usage log
```

---

## 🧪 Running Tests

### Run All Tests
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

### Run by Test Type
```bash
# UI Tests only
mvn test -DsuiteXmlFile=src/test/resources/testng-ui.xml

# API Tests only
mvn test -DsuiteXmlFile=src/test/resources/testng-api.xml

# Integration Tests only
mvn test -DsuiteXmlFile=src/test/resources/testng-integration.xml
```

### Run Cross-Browser Tests
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-crossbrowser.xml
```

### Run with Custom Browser
```bash
mvn test -Dbrowser=firefox -DsuiteXmlFile=src/test/resources/testng.xml
```

### Run in Headless Mode
```bash
mvn test -Dheadless=true -DsuiteXmlFile=src/test/resources/testng.xml
```

### Run Against Different Environment
```bash
mvn test -Denv=staging -DsuiteXmlFile=src/test/resources/testng.xml
```

---

## 📊 Test Reports

### Generate Allure Report
```bash
# Generate and open in browser
mvn allure:serve

# Generate report only (to target/site/allure-maven-plugin)
mvn allure:report
```

### Report Features
- **Test Steps** - Detailed step-by-step execution logs
- **Screenshots** - Automatic capture on failure
- **Attachments** - Request/response logs for API tests
- **History** - Trend analysis across runs
- **Categories** - Failure categorization

---

### Sample Output

<img width="2936" height="1602" alt="image" src="https://github.com/user-attachments/assets/e4292130-a142-4e28-8296-d7ee3764a843" />


## ⚙️ Configuration

### Environment Configuration (`config/config.properties`)
```properties
# Environment
environment=staging

# Browser Settings
browser=chrome
headless=false
implicit.wait=10
explicit.wait=20

# URLs
base.url.staging=https://staging.testmu.ai
api.base.url=https://api.testmu.ai/v1

# Retry Settings
retry.count=2
```

### System Properties (Override via command line)
```bash
-Dbrowser=firefox
-Dheadless=true
-Denv=prod
-Dretry.count=3
```

---

## 🎨 Design Decisions

| Decision | Rationale |
|----------|-----------|
| **Page Object Model** | Encapsulates locators and actions, reduces maintenance |
| **data-testid Selectors** | Stable, decoupled from styling changes |
| **ThreadLocal WebDriver** | Safe parallel execution |
| **External Test Data** | Easy modification without code changes |
| **Retry Analyzer** | Handles transient failures, reduces flakiness |
| **Builder Pattern for Models** | Clean, readable test data creation |
| **REST Assured over HttpClient** | Better DSL for API testing, built-in validation |

---

## 🔮 Future Improvements

With more time, I would add:

1. **Visual Regression Testing** - Percy or Applitools integration
2. **Performance Testing** - JMeter or Gatling integration
3. **Docker Support** - Containerized test execution
4. **Database Utilities** - Direct DB validation
5. **Contract Testing** - Pact for API contracts
6. **Mobile Testing** - Appium integration
7. **Test Data Factory** - Faker-based dynamic data generation

---

## 📚 Documentation

- [Architecture Guide](ARCHITECTURE.md) - Detailed system design
- [Test Strategy](test-strategy.md) - Coverage rationale and risk analysis
- [AI Usage Log](ai-usage-log.md) - AI tool usage documentation

---

## 👤 Author

**[Shivam Anand]**

---

## 📄 License

This project is created for the TestMu AI SDET-2 Assessment.
