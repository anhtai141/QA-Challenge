package hook;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.gherkin.model.Feature;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.google.inject.Inject;
import io.cucumber.core.backend.TestCaseState;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.Status;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.TestCase;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;
import util.CommonHelper;
import util.DriverManagement;
import util.Report;
import util.ShareState;
import util.TestManagementSystem;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CucumberHooks {
    private static final Logger LOG = LogManager.getLogger(CucumberHooks.class);
    private String timeout;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MMM_yyyy_HHmmssa");

    {
        try {
            timeout = System.getProperty("AUTOMATION.DEFAULT_MEDIUM_TIMEOUT",
                    CommonHelper.getProperty("properties/ProjectInformation.properties",
                            "AUTOMATION.DEFAULT_MEDIUM_TIMEOUT"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ShareState shareState;

    @Inject
    public CucumberHooks(ShareState shareState) throws IOException {
        this.shareState = shareState;
    }

    @Before()
    public void beforeEveryScenario(Scenario scenario) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IOException, ParseException {
        LOG.info(String.format("###############Before scenario %s###############", scenario.getName()));

        Report.getReportInstance().setExtentReport(new ExtentReports());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MMM_yyyy_HHmmssa");
        LocalDateTime now = LocalDateTime.now();
        String htmlFilePath = String.format("%s/htmlReport/%s_%s",
                System.getProperty("user.dir"),  scenario.getName(), dtf.format(now));
        // initialize the HtmlReporter
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(htmlFilePath);

        // attach only HtmlReporter
        Report.getReportInstance().getExtentReport().attachReporter(htmlReporter);

        Report.getReportInstance().setExtentScenario(Report.getReportInstance().
                getExtentReport().createTest(Feature.class, scenario.getName()));
        LOG.info(String.format("Create HTML report file for %s with file path %s",
                scenario.getName(), htmlFilePath));
        this.shareState.customAttributes.put("htmlFilePath", htmlFilePath);
        LocalDateTime startTime = LocalDateTime.now();
        String scenarioStartTime = startTime.format(dtf);
        this.shareState.customAttributes.put("scenarioStartTime", scenarioStartTime);
        this.shareState.customAttributes.put("stepIndex", 0);
        if(scenario.getSourceTagNames().contains("@UI")){
            beforeUIScenario();
        }
        if(scenario.getSourceTagNames().contains("@API")){
            beforeAPIScenario();
        }
    }

    public void beforeUIScenario() throws IOException {

        String url = System.getProperty("SUT.URL",
                CommonHelper.getProperty("properties/ProjectInformation.properties", "SUT.URL"));

        // Open browser
        DriverManagement.getDriverManagerInstance().initWebDriver();
        // Navigate to Login page
        DriverManagement.getDriverManagerInstance().getDriver().get(url);
        // Maximize the window
        DriverManagement.getDriverManagerInstance().getDriver().manage().window().maximize();
        // Set default time out
        DriverManagement.getDriverManagerInstance().getDriver().manage().
                timeouts().implicitlyWait(Integer.parseInt(timeout), TimeUnit.SECONDS);
    }

    public void beforeAPIScenario() throws IOException {
        String apiScheme = System.getProperty("SUT.API_SCHEME",
                CommonHelper.getProperty("properties/ProjectInformation.properties", "SUT.API_SCHEME"));
        String apiHost = System.getProperty("SUT.API_HOST",
                CommonHelper.getProperty("properties/ProjectInformation.properties", "SUT.API_HOST"));
        RestAssured.baseURI = apiScheme + "://" + apiHost;
    }

    @After("@UI")
    public void afterUIScenario(Scenario scenario) throws IOException {
        // Close browser
        DriverManagement.getDriverManagerInstance().terminateWebDriver();
    }

    // Suppose that qTest is utilized as Test Management System
    @After
    public void afterEveryScenario(Scenario scenario) throws IOException, ParseException, InterruptedException {
        String qTestStatus;
        if(scenario.getStatus() == Status.FAILED){
            qTestStatus = "fail";
        }else if ( scenario.getStatus() == Status.PASSED ) {
            qTestStatus = "pass";
        } else {
            qTestStatus = "skip";
        }
        Report.getReportInstance().getExtentReport().flush();
        LOG.info(String.format("###############After scenario %s###############", scenario.getName()));
        LocalDateTime endTime = LocalDateTime.now();
        String scenarioEndTime = endTime.format(dtf);
        String isQtestUploadNeeded = System.getProperty("TEST_MANAGEMENT.QTEST_UPLOAD_NEEDED",
                CommonHelper.getProperty("properties/ProjectInformation.properties",
                        "TEST_MANAGEMENT.QTEST_UPLOAD_NEEDED"));
        if(isQtestUploadNeeded.toLowerCase().equals("true")){
            TestManagementSystem integration = new TestManagementSystem();
            integration.submitAutomationTestLogWithAttachment(
                    this.shareState.customAttributes.get("htmlFilePath").toString() + "/index.html",
                    qTestStatus, scenario.getName(),
                    this.shareState.customAttributes.get("scenarioStartTime").toString(),
                    scenarioEndTime);
        }
    }

    @BeforeStep
    public void beforeStep(Scenario scenario) throws NoSuchFieldException, IllegalAccessException {
                String currentStepText;
        String currentStepGherkinKeyword;
        Field delegate = scenario.getClass().getDeclaredField("delegate");
        delegate.setAccessible(true);
        TestCaseState state = (TestCaseState)delegate.get(scenario);
        Field f2 = state.getClass().getDeclaredField("testCase");
        f2.setAccessible(true);
        TestCase testCase = (TestCase)f2.get(state);
        List<PickleStepTestStep> stepDefs = testCase.getTestSteps().stream().
                filter((x) -> x instanceof PickleStepTestStep).
                map((x) -> (PickleStepTestStep)x).collect(Collectors.toList());
        PickleStepTestStep currentStepDef = stepDefs.get((Integer) this.shareState.customAttributes.get("stepIndex"));
        currentStepText = currentStepDef.getStep().getText();
        currentStepGherkinKeyword = currentStepDef.getStep().getKeyword();
        this.shareState.customAttributes.put("stepIndex",
                (Integer)this.shareState.customAttributes.get("stepIndex") + 1);
        ExtentTest step = Report.getReportInstance().getExtentScenario()
                .createNode(currentStepGherkinKeyword + " " + currentStepText);
        Report.getReportInstance().setExtentStep(step);
    }

    @AfterStep
    public void afterStep(Scenario scenario){
        if(scenario.getStatus() == Status.FAILED){
            Report.getReportInstance().getExtentStep().
                    fail(MarkupHelper.createLabel(scenario.getName() + " is FAILED", ExtentColor.RED));
        }else if ( scenario.getStatus() == Status.PASSED ) {
            Report.getReportInstance().getExtentStep().
                    pass(MarkupHelper.createLabel(scenario.getName() + " is PASSED", ExtentColor.GREEN));
        } else {
            Report.getReportInstance().getExtentStep().
                    skip(MarkupHelper.createLabel(scenario.getName()+ " is SKIPPED", ExtentColor.YELLOW));
        }
    }
}
