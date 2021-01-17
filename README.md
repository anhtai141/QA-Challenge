QA Challenge  
Automate Test Case for a web application (AUT: https://openweathermap.org/)

Feature: Search Weather in your city

Highlight about my outputs:
- Selenium in Gradle project
- Using webdrivermanager for auto downloading and initializing web driver
- Use Rest-Assured for API Test
- TestNG
- Language: Java
- BDD Cucumber approach
- Use Extent Report for creating HTML report file
- Data driven
- Multiple browsers
- Able to run in parallel
- Browser type, # of threads, test environment (Host, API Key, ...), ... can be passed to params when running test via command line
- Use Guice to share state between steps

Automation approach:
- Search weather for city on UI, 
- Send API requests to get data
- Verify data on UI against API responses

Guideline to setup this project to execute test on PC
1. Install Java 8 (You can install the newer version as long as it compatible with IntelliJ, Gradle): 
	- Download link: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
	- Installation instruction link: https://docs.oracle.com/javase/10/install/installation-jdk-and-jre-microsoft-windows-platforms.htm#JSJIG-GUID-A7E27B90-A28D-4237-9383-A58B416071CA
2. Install IntelliJ: https://www.jetbrains.com/idea/download
3. Install Gradle 6.8: https://gradle.org/install/
4. Clone this repo to your PC
5. Start IntelliJ
6. Open this project and wait for all dependencies loaded

How to run test?
1. From terminal window, execute the command "gradle test"
2. If you want to run test cases in parallel, type the command "gradle test -Dthreads=X" where X is the number of threats
3. You can indicate the Browser type by executing the command  "gradle test -DAUTOMATION.BROWSER_TYPE=Firefox". By default, test cases are executed in Chrome
4. Reports are generated in "projectDir/htmlReport"

Notes: The scenario #2 is failed and I reported that issue in the bug list which is sent in a separated email.

Things need to improve:
1. Execution report: we need consolidate report in a Excel file, or intergrate with a report system (e.g. Report Portal). The framework has supported uploading execution results to qTest.
2. Provide CI/CD integration
3. Distribution execution
4. Restructure codes. Make it more beautiful
