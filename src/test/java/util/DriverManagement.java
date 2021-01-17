package util;

import com.google.inject.Provides;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.util.Strings;

import java.io.IOException;
import java.net.URL;

public class DriverManagement {
    private static DriverManagement instance= null;
    private static ThreadLocal<WebDriver> webDriver = new InheritableThreadLocal<>();

    private DriverManagement(){
    }

    public static DriverManagement getDriverManagerInstance(){
        if(instance == null){
            instance = new DriverManagement();
        }
        return instance;
    }

    public void initWebDriver() throws IOException {
        String executionMode = System.getProperty("AUTOMATION.EXECUTION_MODE",
                CommonHelper.getProperty("properties/ProjectInformation.properties",
                        "AUTOMATION.EXECUTION_MODE"));
        String browserType =  System.getProperty("AUTOMATION.BROWSER_TYPE",
                CommonHelper.getProperty("properties/ProjectInformation.properties",
                        "AUTOMATION.BROWSER_TYPE"));
        if(executionMode.toLowerCase().equals("local")){
            switch (browserType.toLowerCase()){
                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    CommonHelper.setProperties("properties/Firefox.properties");
                    System.getProperties().keySet().forEach(
                            sys -> {
                                if(sys.toString().contains("Firefox.options")){
                                    firefoxOptions.addArguments(System.getProperty(sys.toString()));
                                }
                            }
                    );
                    WebDriverManager.firefoxdriver().setup();
                    webDriver.set(new FirefoxDriver(firefoxOptions));
                    break;
                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    CommonHelper.setProperties("properties/Chrome.properties");
                    System.getProperties().keySet().forEach(
                            sys -> {
                                if(sys.toString().contains("chrome.options")){
                                    chromeOptions.addArguments(System.getProperty(sys.toString()));
                                }
                            }
                    );
                    WebDriverManager.chromedriver().setup();
                    webDriver.set(new ChromeDriver(chromeOptions));
                    break;
            }
        }
        else {
            String remoteHubUrl = System.getProperty("AUTOMATION.REMOTE_HUB_URL",
                    CommonHelper.getProperty("properties/ProjectInformation.properties",
                            "AUTOMATION.REMOTE_HUB_URL"));
            DesiredCapabilities capabilities;
            if(!Strings.isNullOrEmpty(remoteHubUrl)){
                switch (browserType.toLowerCase()){
                    case "firefox":
                        capabilities = DesiredCapabilities.firefox();
                        capabilities.setJavascriptEnabled(true);
                        webDriver.set(new RemoteWebDriver(new URL(remoteHubUrl), capabilities));
                        break;
                    default:
                        capabilities = DesiredCapabilities.chrome();
                        capabilities.setJavascriptEnabled(true);
                        webDriver.set(new RemoteWebDriver(new URL(remoteHubUrl), capabilities));
                        break;
                }
            }
        }
    }

    public void terminateWebDriver(){
        webDriver.get().quit();
        webDriver.remove();
    }

    @Provides
    public WebDriver getDriver(){
        return webDriver.get();
    }
}
