package com.demowebshop.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReport {
    private static ExtentReports extent;

    public static ExtentReports getReportInstance() {
        if (extent == null) {

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String reportPath = System.getProperty("user.dir") + "/reports/TestReport_" + timeStamp + ".html";
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("Demo Web Shop Test Automation Report");
            sparkReporter.config().setReportName("Automation Test Results");
            sparkReporter.config().setTheme(Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
        }
        return extent;
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}
