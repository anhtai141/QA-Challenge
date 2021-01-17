package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;


@CucumberOptions(
        monochrome = true,
        glue = {
                "hook",
                "page",
                "stepdefinition",
                "runner",
                "util",
        },
        plugin = {
                "pretty",
//                "event.CucumberEventHandler" ## if having need to use event handler
        },

        features = {"src/test/resources/features"}
)
public class DefaultRunner extends AbstractTestNGCucumberTests{

        @Override
        @DataProvider(parallel = true)
        public Object[][] scenarios() {
                return super.scenarios();
        }}
