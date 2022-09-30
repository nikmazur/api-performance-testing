# API Performance Testing
This project launches a simple API server and runs a series of stress tests on it. After that an HTML report with test results and performance criteria is generated (results are also displayed in the console for each test separately).

## [View Sample HTML Report](https://htmlpreview.github.io/?https://github.com/nikmazur/api-performance-testing/blob/master/reports/SampleTestReport.html)
![alt text](https://github.com/nikmazur/api-performance-testing/blob/master/reports/ReportScreen.png "Test Report Screenshot")
The report lists test performance data such as total and per second number of executions, latency and pass / fail percentages. There's also a latency graph which shows a percentile distribution of test execution times.

## Execution Parameters
Each test can be individually adjusted with parameters like number of threads, execution time, pass requirements, etc. Detailed info on these parameters is available in JUnitPerf library [documentation](https://github.com/noconnor/JUnitPerf#readme).

Other libraries used for this project is JUnit 5 as the testing framework, MockServer for creating an API server, REST Assured for managing API requests.