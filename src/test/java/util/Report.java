package util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;


public class Report {
    private static Report instance = null;
    private ThreadLocal<ExtentReports> extentReport = new InheritableThreadLocal<ExtentReports>();
    private ThreadLocal<ExtentTest> extentScenario = new InheritableThreadLocal<>();
    private ThreadLocal<ExtentTest> extentStep = new InheritableThreadLocal<>();
    private ThreadLocal<String> reportFileName = new InheritableThreadLocal<>();
    public static Report getReportInstance(){
        if(instance == null){
            instance = new Report();
        }
        return instance;
    }

    public void setExtentReport(ExtentReports report){
        this.extentReport.set(report);
    }
    public ExtentReports getExtentReport(){
        return extentReport.get();
    }

    public ExtentTest getExtentScenario(){
        return extentScenario.get();
    }

    public void setExtentScenario(ExtentTest extentScenario) {
        this.extentScenario.set(extentScenario);
    }

    public ExtentTest getExtentStep(){
        return extentStep.get();
    }

    public void setExtentStep(ExtentTest extentTest) {
        this.extentStep.set(extentTest);
    }

    public String getReportFileName(){
        return reportFileName.get();
    }

    public void setReportFileName(String fileName) {
        this.reportFileName.set(fileName);
    }
}
