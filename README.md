# robotframework-statusupdater
Updates status of tests to various test management systems

Current code is more of PoC, so work is still needed. 

For Zephyr (Jira) tests can be written against mock endpoint: https://getzephyr.docs.apiary.io/#

## Usage
Testmanagement system is recognized from the variables in the scope. If for some reason different suites go to different systems, those variables can be set at Suite level.

### Common
There're some common variables that control how library works when used as a listener. Note that listerner has to be specified when starting the tests (e.g. [Generic way](http://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#taking-listeners-into-use) and [Java with Maven plugin](http://robotframework.org/MavenPlugin/run-mojo.html#listener) 
| Variable name | Description |
| ------------- | ----------- |
| startSuiteListenerEnabled | Listen suite start |
| stopSuiteListenerEnabled | Listen suite end |
| startTestListenerEnabled | Listen test start |
| stopTestListenerEnabled | Listen test end |

### Testlink
With TestLink, update is done with testcase's external ID, which has to be first part of the testcase name in the Robot tests. In the case of failure failure message is written to notes of the TestLink execution.

####Required variables in Robot's scope
| Variable name | Description |
| ------------- | ----------- |
| TESTLINK_URL | TestLink's XML-RPC endpoint. With Docker TestLink this is at http://<server>/lib/api/xmlrpc/v1/xmlrpc.php |
| TESTLINK_API_KEY | API key from the user who should be visible at the automated updates |
| planName | Name of TestLink's test plan containing test case that needs to be updated |
| projectName | Name of TestLink's project containing test plan containing test case that needs to be updated |
| buildId | ID number of the build that is executed in Test Plan |

#### Statuses
Updates Testlink only at test end. 
| Robot test status | Testlink Status |
| ------------- | ----------- |
| PASS | PASSED |
| FAIL | If test has tag "BLOCKED", BLOCKED; Otherwise FAILED |