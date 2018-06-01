# robotframework-statusupdater

## Introduction

Updates status of tests to various test management systems

Current code is more of PoC, so work is still needed. Testlink implementation tested with [Bitnami's Container](https://github.com/bitnami/bitnami-docker-testlink). 

## Usage
Test management system is recognized from the variables in the scope. If for some reason different suites go to different systems, those variables can be set at Suite level.

If you are using the robotframework-maven-plugin you can
use this library by adding the following dependency to 
your pom.xml:

    <dependency>
        <groupId>com.github.hi-fi</groupId>
        <artifactId>robotframework-statusupdater</artifactId>
        <version>0.0.2</version>
        <scope>test</scope>
    </dependency>

* More information about this library can be found in the
  [Keyword Documentation](http://search.maven.org/remotecontent?filepath=com/github/hi-fi/robotframework-statusupdater/0.0.2/robotframework-statusupdater-0.0.2.html).
* For keyword completion in RIDE you can download this
  [Library Specs](http://search.maven.org/remotecontent?filepath=com/github/hi-fi/robotframework-statusupdater/0.0.2/robotframework-statusupdater-0.0.2.xml)
  and place it in your PYTHONPATH.

### Common
There're some common variables that control how library works when used as a listener. Note that listerner has to be specified when starting the tests (e.g. [Generic way](http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#taking-listeners-into-use) and [Java with Maven plugin](http://robotframework.org/MavenPlugin/run-mojo.html#listener)
 
| Variable name | Description |
| ------------- | ----------- |
| startSuiteListenerEnabled | Listen suite start |
| stopSuiteListenerEnabled | Listen suite end |
| startTestListenerEnabled | Listen test start |
| stopTestListenerEnabled | Listen test end |

### HP Quality Center / ALM (version 12)
With Quality Center/ALM, update is done to testcase name's first part (until first whitespace) in the Robot tests.

#### Required variables

| Variable name | Description |
| ------------- | ----------- |
| QC_URL | Part of the QC's URL before /qcbin... |
| QC_USER | User who has rights to specified domain and project and rights to execute tests |
| QC\_PW | Password for QC\_USER |
| QC_DOMAIN | Domain to use in QC |
| QC_PROJECT | Project to use in QC |

#### Statuses

| Robot test status | QC Status | Comment |
| ------------- | ----------- | ----------- |
| - | Not Completed | Default status when test execution is created to QC. Practically means that test is started in Robot |
| PASS | Passed | |
| FAIL | If test has tag "BLOCKED" (case insensitive), Blocked; Otherwise Failed | |

### Jira with Xray

#### Required variables

| Variable name | Description |
| ------------- | ----------- |
| JIRAXRAY_URL | JIRA's hostname |
| JIRAXRAY\_CONTEXT | Path to Jira. Jira is accessed normally with JIRAXRAY\_URL/JIRAXRAY\_CONTEXT -address. |
| JIRAXRAY\_USER | User that has rights to create test executions to specified project. |
| JIRAXRAY\_PW | Password for JIRAXRAY\_USER |

#### Statuses

| Robot test status | QC Status | Comment |
| ------------- | ----------- | ----------- |
| - | Not Completed | Default status when test execution is created to QC. Practically means that test is started in Robot |
| PASS | Passed | |
| FAIL | If test has tag "BLOCKED" (case insensitive), Blocked; Otherwise Failed | |

### Jira with Zephyr

#### Required variables

| Variable name | Description |
| ------------- | ----------- |
| JIRAZEPHYR_URL | JIRA's hostname |
| JIRAZEPHYR\_CONTEXT | Path to Jira. Jira is accessed normally with JIRAZEPHYR\_URL/JIRAZEPHYR\_CONTEXT -address. |
| JIRAZEPHYR\_USER | User that has rights to create test executions to specified project. |
| JIRAZEPHYR\_PW | Password for JIRAZEPHYR\_USER |

#### Statuses

| Robot test status | QC Status | Comment |
| ------------- | ----------- | ----------- |
| - | Not Completed | Default status when test execution is created to QC. Practically means that test is started in Robot |
| PASS | Passed | |
| FAIL | If test has tag "BLOCKED" (case insensitive), Blocked; Otherwise Failed | |

### Testlink
With TestLink, update is done with testcase's external ID, which has to be first part of the testcase name in the Robot tests. In the case of failure failure message is written to notes of the TestLink execution.

#### Required variables

| Variable name | Description |
| ------------- | ----------- |
| TESTLINK_URL | TestLink's XML-RPC endpoint. With Docker TestLink this is at http://<server>/lib/api/xmlrpc/v1/xmlrpc.php |
| TESTLINK\_API\_KEY | API key from the user who should be visible at the automated updates |
| planName | Name of TestLink's test plan containing test case that needs to be updated |
| projectName | Name of TestLink's project containing test plan containing test case that needs to be updated |
| buildId | ID number of the build that is executed in Test Plan |

#### Statuses
Updates Testlink only at test end. 

| Robot test status | Testlink Status |
| ------------- | ----------- |
| PASS | PASSED |
| FAIL | If test has tag "BLOCKED" (case insensitive), BLOCKED; Otherwise FAILED |