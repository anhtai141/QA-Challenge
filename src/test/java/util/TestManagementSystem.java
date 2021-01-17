package util;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.qas.qtest.api.auth.PropertiesQTestCredentials;
import org.qas.qtest.api.auth.QTestCredentials;
import org.qas.qtest.api.services.attachment.AttachmentServiceClient;
import org.qas.qtest.api.services.defect.DefectServiceClient;
import org.qas.qtest.api.services.design.TestDesignServiceClient;
import org.qas.qtest.api.services.execution.TestExecutionServiceClient;
import org.qas.qtest.api.services.execution.model.TestLog;
import org.qas.qtest.api.services.project.ProjectServiceClient;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

// Example class, suppose that qTest is utilized as Test Management System
public class TestManagementSystem {

    private static final Logger LOG = LogManager.getLogger(TestManagementSystem.class);
    SimpleDateFormat dtf = new SimpleDateFormat("dd_MMM_yyyy_HHmmssa");

    String qTestUrl;
    private QTestCredentials credentials = new PropertiesQTestCredentials
            (new File("src/test/resources/properties/qTestCredentials.properties"));
    {
        try {
            qTestUrl = System.getProperty("TEST_MANAGEMENT.QTEST_URL",
                        CommonHelper.getProperty("properties/ProjectInformation.properties",
                                "TEST_MANAGEMENT.QTEST_URL"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Long projectId;
    {
        try {
            projectId = Long.parseLong(System.getProperty("TEST_MANAGEMENT.QTEST_PROJECT_ID",
                    CommonHelper.getProperty("properties/ProjectInformation.properties",
                            "TEST_MANAGEMENT.QTEST_PROJECT_ID")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RequestSpecification qTestRequestSpecification = new RequestSpecBuilder()
            .setBaseUri(qTestUrl).setBasePath("")
            .addHeader("Authorization", credentials.getToken())
            .setContentType(ContentType.JSON)
            .build();

    public TestManagementSystem() throws IOException {
    }

    public QTestCredentials getQTestCredentials() throws IOException {
        return new PropertiesQTestCredentials(
                new File("src/test/resources/properties/qTestCredentials.properties"));

    }

    //Following qTest SDK document
    public ProjectServiceClient createProjectService(QTestCredentials credentials){
        ProjectServiceClient projectService = new ProjectServiceClient(credentials);
        projectService.setEndpoint(qTestUrl);
        return projectService;
    }

    public TestDesignServiceClient createTestDesignService(QTestCredentials credentials){
        TestDesignServiceClient testDesignService = new TestDesignServiceClient(credentials);
        testDesignService.setEndpoint(qTestUrl);
        return testDesignService;
    }

    public TestExecutionServiceClient createTestExecutionService(QTestCredentials credentials){
        TestExecutionServiceClient testExecutionService = new TestExecutionServiceClient(credentials);
        testExecutionService.setEndpoint(qTestUrl);
        return testExecutionService;
    }

    public DefectServiceClient createTestDefectService(QTestCredentials credentials){
        DefectServiceClient defectService = new DefectServiceClient(credentials);
        defectService.setEndpoint(qTestUrl);
        return defectService;
    }

    public AttachmentServiceClient createTestAttachmentService(QTestCredentials credentials){
        AttachmentServiceClient attachmentService = new AttachmentServiceClient(credentials);
        attachmentService.setEndpoint(qTestUrl);
        return attachmentService;
    }

    // Method to search test cycyle by name
    public Map<String, Object> searchTestCycleByName(String testCycleName) {
        try{
            return new HashMap();
        }
        catch (Exception ex){
            LOG.error(String.format("Failed when searching test cycle, with stacktrace: "),ex);
            return new HashMap();
        }
    }

    public TestLog submitAutomationTestLogWithAttachment(String reportFilePath, String testStatus, String testScenarioName, String startTime, String endTime) throws IOException, InterruptedException, ParseException {
        try{
            return new TestLog();
        }
        catch (Exception ex){
            LOG.error(String.format("Failed when submitting test result and test log, with stacktrace: "),ex);
            return null;
        }
    }

    private Long createTestRunByName(String testCaseName, Long testCycleId) {
        try {
            return 0L;
        } catch (Exception ex) {
            LOG.error(String.format("Failed when creating test run, with stacktrace: "),ex);
            return 0L;
        }
    }

    private Long searchTestRunIDByNameAndParentTestCycle(String testRunName, Map<String, Object> cycleInformation) {
        try {
            return 0L;
        } catch (Exception ex) {
            LOG.error(String.format("Failed when searching test run, with stacktrace: "),ex);
            return 0L;
        }
    }

    public Long searchAutomatedTestCaseIdByName(String testCaseName) {
        try {
            return 0L;
        } catch (Exception ex) {
            LOG.error(String.format("Failed when searching test case by name, with stacktrace: "),ex);
            return 0L;
        }
    }

    private Map<String, Object> createTestCycleWithName(String testCycleName) {
        try {
            return new HashMap<>();
        } catch (Exception ex) {
            LOG.error(String.format("Failed when creating test cycle, with stacktrace: "),ex);
            return new HashMap();
        }
    }
}
