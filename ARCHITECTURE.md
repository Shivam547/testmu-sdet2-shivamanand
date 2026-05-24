# Architecture Document

> Technical architecture and design decisions for the TestMu SDET-2 Test Automation Framework

---

## 1. High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           TEST EXECUTION LAYER                                │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐   │
│  │   UI Tests      │  │   API Tests     │  │   Integration Tests         │   │
│  │  - LoginTest    │  │  - CrudApiTest  │  │  - ApiToUIIntegrationTest   │   │
│  │  - DashboardTest│  │  - AuthApiTest  │  │                             │   │
│  │  - FormTest     │  │  - SchemaTest   │  │                             │   │
│  │  - CrossBrowser │  │  - ErrorTest    │  │                             │   │
│  └────────┬────────┘  └────────┬────────┘  └──────────────┬──────────────┘   │
│           │                    │                          │                   │
│           └────────────────────┼──────────────────────────┘                   │
│                                ▼                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│                           FRAMEWORK CORE LAYER                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                           BaseTest.java                                  │ │
│  │  - @BeforeMethod: Driver initialization (ThreadLocal)                   │ │
│  │  - @AfterMethod: Driver cleanup                                         │ │
│  │  - Environment/Browser configuration                                    │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                    │                                          │
│           ┌────────────────────────┼────────────────────────┐                │
│           ▼                        ▼                        ▼                │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐  │
│  │   PAGE OBJECTS  │    │   API CLIENT    │    │      UTILITIES          │  │
│  │  ┌───────────┐  │    │                 │    │  ┌─────────────────┐    │  │
│  │  │ BasePage  │  │    │  - GET/POST/    │    │  │  WaitUtils      │    │  │
│  │  │ - click() │  │    │    PUT/DELETE   │    │  │  - Explicit     │    │  │
│  │  │ - type()  │  │    │  - Auth handling│    │  │  - Fluent       │    │  │
│  │  │ - wait()  │  │    │  - Response     │    │  │  - Custom       │    │  │
│  │  └─────┬─────┘  │    │    validation   │    │  └─────────────────┘    │  │
│  │        │        │    │  - Logging      │    │  ┌─────────────────┐    │  │
│  │  ┌─────┴─────┐  │    │                 │    │  │  RetryUtils     │    │  │
│  │  │ LoginPage │  │    └─────────────────┘    │  │  - Retry logic  │    │  │
│  │  │ Dashboard │  │                           │  │  - Configurable │    │  │
│  │  │ FormPage  │  │                           │  └─────────────────┘    │  │
│  │  └───────────┘  │                           │  ┌─────────────────┐    │  │
│  └─────────────────┘                           │  │ ScreenshotUtils │    │  │
│                                                │  │ - On failure    │    │  │
│                                                │  │ - Allure attach │    │  │
│                                                │  └─────────────────┘    │  │
│                                                │  ┌─────────────────┐    │  │
│                                                │  │  JsonUtils      │    │  │
│                                                │  │  - JSON/YAML    │    │  │
│                                                │  │  - Data loading │    │  │
│                                                │  └─────────────────┘    │  │
│                                                └─────────────────────────┘  │
├──────────────────────────────────────────────────────────────────────────────┤
│                          CONFIGURATION LAYER                                  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │  ConfigReader   │  │  Test Data      │  │     JSON Schemas            │  │
│  │  - Properties   │  │  - JSON files   │  │  - Response validation      │  │
│  │  - Env override │  │  - YAML files   │  │  - Contract testing         │  │
│  │  - Sys props    │  │  - CSV files    │  │                             │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘  │
├──────────────────────────────────────────────────────────────────────────────┤
│                           REPORTING LAYER                                     │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │  TestListener.java                    │  Allure Reports                 │ │
│  │  - onTestStart: Log test name         │  - Step annotations             │ │
│  │  - onTestFailure: Screenshot capture  │  - Screenshot attachments       │ │
│  │  - onTestSuccess: Log result          │  - API request/response logs    │ │
│  │  - onFinish: Summary stats            │  - Environment info             │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
├──────────────────────────────────────────────────────────────────────────────┤
│                           CI/CD LAYER                                         │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │  GitHub Actions Pipeline                                                 │ │
│  │  ┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌──────────────┐  │ │
│  │  │API Tests│ -> │ UI Chrome   │    │ UI Firefox  │ -> │Generate      │  │ │
│  │  │ (fast)  │    │ (parallel)  │    │ (parallel)  │    │Allure Report │  │ │
│  │  └─────────┘    └─────────────┘    └─────────────┘    └──────────────┘  │ │
│  │                                                              │           │ │
│  │                                                              ▼           │ │
│  │                                                     ┌──────────────┐    │ │
│  │                                                     │ Publish to   │    │ │
│  │                                                     │ GitHub Pages │    │ │
│  │                                                     └──────────────┘    │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Component Design

### 2.1 Page Object Model (POM)

```
┌─────────────────────────────────────────────────────────────────┐
│                         BasePage                                 │
├─────────────────────────────────────────────────────────────────┤
│ Fields:                                                          │
│   - WebDriver driver                                            │
│   - WaitUtils waitUtils                                         │
│   - Actions actions                                             │
│   - Logger logger                                               │
├─────────────────────────────────────────────────────────────────┤
│ Locator Strategies:                                              │
│   + byTestId(String testId)      → By                           │
│   + byAriaLabel(String label)    → By                           │
│   + byRole(String role, name)    → By                           │
├─────────────────────────────────────────────────────────────────┤
│ Actions:                                                         │
│   + click(By/WebElement)         → void                         │
│   + type(By/WebElement, text)    → void                         │
│   + getText(By/WebElement)       → String                       │
│   + isDisplayed(By/WebElement)   → boolean                      │
│   + selectByVisibleText(By, text)→ void                         │
│   + jsClick(WebElement)          → void                         │
│   + jsScrollIntoView(WebElement) → void                         │
├─────────────────────────────────────────────────────────────────┤
│ Abstract:                                                        │
│   + isLoaded()                   → boolean                      │
└─────────────────────────────────────────────────────────────────┘
                              △
                              │ extends
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
┌───────┴───────┐    ┌───────┴───────┐    ┌───────┴───────┐
│   LoginPage   │    │ DashboardPage │    │   FormPage    │
├───────────────┤    ├───────────────┤    ├───────────────┤
│ Locators:     │    │ Locators:     │    │ Locators:     │
│ - username    │    │ - welcomeMsg  │    │ - firstName   │
│ - password    │    │ - logoutBtn   │    │ - lastName    │
│ - loginBtn    │    │ - navMenu     │    │ - email       │
│ - errorMsg    │    │ - dataTable   │    │ - submitBtn   │
├───────────────┤    ├───────────────┤    ├───────────────┤
│ Actions:      │    │ Actions:      │    │ Actions:      │
│ + open()      │    │ + logout()    │    │ + fillForm()  │
│ + loginAs()   │    │ + navigateTo()│    │ + submit()    │
│ + getError()  │    │ + getWelcome()│    │ + getErrors() │
│ + isLoaded()  │    │ + isLoaded()  │    │ + isLoaded()  │
└───────────────┘    └───────────────┘    └───────────────┘
```

### 2.2 API Client Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         ApiClient                                │
├─────────────────────────────────────────────────────────────────┤
│ Configuration:                                                   │
│   - RequestSpecification (base URI, content type, filters)      │
│   - ResponseSpecification (logging, default expectations)       │
│   - Auth token management                                       │
├─────────────────────────────────────────────────────────────────┤
│ Methods:                                                         │
│   + get(endpoint)              → Response                       │
│   + get(endpoint, params)      → Response                       │
│   + post(endpoint, body)       → Response                       │
│   + put(endpoint, body)        → Response                       │
│   + patch(endpoint, body)      → Response                       │
│   + delete(endpoint)           → Response                       │
│   + withAuth(token)            → ApiClient (fluent)             │
│   + expectStatusCode(code)     → ApiClient (fluent)             │
├─────────────────────────────────────────────────────────────────┤
│ Features:                                                        │
│   - Allure REST Assured filter (automatic logging)              │
│   - Response time measurement                                   │
│   - JSON schema validation support                              │
└─────────────────────────────────────────────────────────────────┘
```

### 2.3 Thread Safety Model

```
┌─────────────────────────────────────────────────────────────────┐
│                    Parallel Execution Model                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Thread 1 (Chrome)          Thread 2 (Firefox)                 │
│   ┌─────────────────┐        ┌─────────────────┐                │
│   │ ThreadLocal     │        │ ThreadLocal     │                │
│   │ <WebDriver>     │        │ <WebDriver>     │                │
│   │ ┌─────────────┐ │        │ ┌─────────────┐ │                │
│   │ │ChromeDriver│ │        │ │FirefoxDriver│ │                │
│   │ └─────────────┘ │        │ └─────────────┘ │                │
│   └────────┬────────┘        └────────┬────────┘                │
│            │                          │                          │
│            ▼                          ▼                          │
│   ┌─────────────────┐        ┌─────────────────┐                │
│   │   LoginTest     │        │   LoginTest     │                │
│   │   (Instance 1)  │        │   (Instance 2)  │                │
│   └─────────────────┘        └─────────────────┘                │
│                                                                  │
│   Key: Each thread has its own WebDriver instance               │
│        No shared mutable state between threads                  │
│        BrowserFactory.getDriver() returns thread-specific       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. Design Patterns Used

### 3.1 Page Object Model (POM)
**Purpose**: Encapsulate UI elements and interactions
**Benefits**:
- Separation of test logic from page structure
- Single point of maintenance for locators
- Reusable page actions across tests
- Improved readability

### 3.2 Factory Pattern (BrowserFactory)
**Purpose**: Create WebDriver instances based on configuration
**Benefits**:
- Centralized driver creation logic
- Easy addition of new browsers
- Configurable browser options

### 3.3 Singleton Pattern (ConfigReader)
**Purpose**: Single source of configuration
**Benefits**:
- Consistent configuration access
- Lazy loading of properties
- Environment override support

### 3.4 Builder Pattern (Model classes with Lombok @Builder)
**Purpose**: Construct complex test data objects
**Benefits**:
- Fluent, readable object creation
- Optional field handling
- Immutable objects

### 3.5 Strategy Pattern (Selector strategies)
**Purpose**: Different approaches to locate elements
**Benefits**:
- Flexible locator selection
- Easy to add custom strategies
- Consistent API

---

## 4. Data Flow

### 4.1 Test Execution Flow

```
┌──────────┐     ┌──────────────┐     ┌─────────────┐     ┌──────────────┐
│  TestNG  │────>│  BaseTest    │────>│ BrowserFactory│───>│  WebDriver   │
│  Runner  │     │ @BeforeMethod│     │ createDriver()│    │  (Chrome/FF) │
└──────────┘     └──────────────┘     └─────────────┘     └──────────────┘
                        │
                        ▼
              ┌──────────────────┐
              │   Test Method    │
              │  LoginTest.java  │
              └────────┬─────────┘
                       │
         ┌─────────────┴─────────────┐
         │                           │
         ▼                           ▼
┌─────────────────┐         ┌─────────────────┐
│   Page Object   │         │   ApiClient     │
│   LoginPage     │         │   REST calls    │
└────────┬────────┘         └────────┬────────┘
         │                           │
         ▼                           ▼
┌─────────────────┐         ┌─────────────────┐
│  WaitUtils      │         │  Response       │
│  Assertions     │         │  Validation     │
└────────┬────────┘         └────────┬────────┘
         │                           │
         └─────────────┬─────────────┘
                       ▼
              ┌──────────────────┐
              │   Assertions     │
              │   Pass/Fail      │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │  TestListener    │
              │  - Screenshot    │
              │  - Allure attach │
              └──────────────────┘
```

### 4.2 Configuration Loading Flow

```
┌────────────────────────────────────────────────────────────────┐
│                    Configuration Priority                       │
│                    (Highest to Lowest)                          │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│   1. System Properties       mvn test -Dbrowser=firefox        │
│            │                                                    │
│            ▼                                                    │
│   2. Environment Properties  config-{env}.properties            │
│            │                                                    │
│            ▼                                                    │
│   3. Base Properties         config.properties                  │
│            │                                                    │
│            ▼                                                    │
│   4. Default Values          Hardcoded in ConfigReader          │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

---

## 5. Test Categories

| Category | Description | Location | Parallelization |
|----------|-------------|----------|-----------------|
| **UI Tests** | Browser-based functional tests | `tests/ui/` | By class |
| **API Tests** | REST API validation | `tests/api/` | By method |
| **Integration** | End-to-end UI+API flows | `tests/integration/` | Sequential |
| **Smoke** | Quick sanity checks | Tagged with `@Test(groups="smoke")` | By class |
| **Regression** | Full test coverage | All tests | By class |

---

## 6. Reporting Strategy

### 6.1 Allure Report Structure

```
Allure Report
├── Overview
│   ├── Test Results Summary (Pass/Fail/Skip)
│   ├── Test Duration
│   └── Categories (Product Defects, Test Defects)
├── Suites
│   ├── UI Tests
│   │   ├── LoginTest
│   │   ├── DashboardTest
│   │   └── FormValidationTest
│   ├── API Tests
│   │   ├── CrudApiTest
│   │   └── SchemaValidationTest
│   └── Integration Tests
├── Graphs
│   ├── Test Status Pie Chart
│   ├── Severity Distribution
│   └── Duration Trends
├── Timeline
│   └── Parallel execution visualization
└── Behaviors
    ├── Epic: Authentication
    │   └── Feature: Login
    │       └── Story: User Login
    └── Epic: API Testing
        └── Feature: CRUD Operations
```

### 6.2 Failure Artifacts

On test failure, the following are captured:
1. **Screenshot** - Full page screenshot
2. **Page Source** - HTML at time of failure (optional)
3. **Browser Console Logs** - JavaScript errors (optional)
4. **Exception Stack Trace** - Full Java exception

---

## 7. CI/CD Pipeline Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    GitHub Actions Pipeline                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Trigger: Push to main/develop OR Pull Request                 │
│                                                                  │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │                     JOB 1: API Tests                    │   │
│   │   - Fast feedback (no browser needed)                   │   │
│   │   - ~2-3 minutes                                        │   │
│   └───────────────────────────┬─────────────────────────────┘   │
│                               │                                  │
│                               ▼                                  │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │              JOB 2: UI Tests (Matrix)                   │   │
│   │   ┌─────────────────┐  ┌─────────────────┐              │   │
│   │   │ Chrome (ubuntu) │  │ Firefox (ubuntu)│              │   │
│   │   │   Headless      │  │   Headless      │              │   │
│   │   └─────────────────┘  └─────────────────┘              │   │
│   │   Running in parallel                                   │   │
│   └───────────────────────────┬─────────────────────────────┘   │
│                               │                                  │
│                               ▼                                  │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │                JOB 3: Generate Report                   │   │
│   │   - Merge all allure-results                            │   │
│   │   - Generate Allure HTML report                         │   │
│   │   - Deploy to GitHub Pages                              │   │
│   └───────────────────────────┬─────────────────────────────┘   │
│                               │                                  │
│                               ▼                                  │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │           JOB 4: Notify (on failure only)               │   │
│   │   - Slack webhook notification                          │   │
│   │   - PR comment with report link                         │   │
│   └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 8. Extensibility Points

### Adding a New Page Object
1. Create class extending `BasePage`
2. Define locators using `byTestId()` or similar
3. Implement page-specific actions
4. Override `isLoaded()` method

### Adding a New API Endpoint Test
1. Create test class
2. Instantiate `ApiClient`
3. Call appropriate HTTP methods
4. Validate response with assertions/schema

### Adding a New Browser
1. Add case to `BrowserFactory.createDriver()`
2. Configure browser-specific options
3. Add to TestNG XML for cross-browser suite

### Adding New Test Data
1. Create JSON/YAML file in `testdata/`
2. Load using `JsonUtils.readJson()` or `readYaml()`
3. Use in `@DataProvider` for parameterized tests

---

## 9. Security Considerations

| Concern | Mitigation |
|---------|------------|
| Credentials in code | External config, environment variables |
| API tokens | Never committed, injected via CI secrets |
| Test data | Sanitized, no PII in public repo |
| Screenshots | May contain sensitive data, review before sharing |

---

## 10. Performance Considerations

| Aspect | Strategy |
|--------|----------|
| Test isolation | Each test gets fresh browser instance |
| Parallel execution | TestNG parallel="classes" with thread-count |
| Wait optimization | Explicit waits over Thread.sleep |
| API-first data setup | Faster than UI-based setup |
| Selective runs | Tag-based test selection for quick feedback |

---

*Document Version: 1.0*  
*Last Updated: [Current Date]*
