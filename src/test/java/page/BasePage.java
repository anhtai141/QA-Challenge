package page;

import io.cucumber.guice.ScenarioScoped;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.CommonHelper;
import util.Constant;
import util.DriverManagement;

@ScenarioScoped
public class BasePage {
    private String timeout;
    {
        try {
            timeout = System.getProperty("AUTOMATION.DEFAULT_MEDIUM_TIMEOUT",
                        CommonHelper.getProperty("properties/ProjectInformation.properties",
                                "AUTOMATION.DEFAULT_MEDIUM_TIMEOUT"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WebDriver driver = DriverManagement.getDriverManagerInstance().getDriver();
    public WebDriverWait wait = new WebDriverWait(driver,
            Integer.parseInt(timeout));
    public BasePage() {
        PageFactory.initElements(driver, this);
    }

    public By getElementLocator(WebElement element){
        By by = null;
        //[[ChromeDriver: chrome on XP (d85e7e220b2ec51b7faf42210816285e)] -> xpath: //input[@title='Search']]
        System.out.println("Element: " + element.toString());
        String[] pathVariables = (element.toString().split("'")[1].replaceFirst("By.", "")).split(":");
        System.out.println("Element pased: " + pathVariables[0].trim());
        System.out.println("Element pased: " + pathVariables[1].trim());
        String selector = pathVariables[0].trim();
        String value = pathVariables[1].trim();

        switch (selector) {
            case "id":
                by = By.id(value);
                break;
            case "className":
                by = By.className(value);
                break;
            case "tagName":
                by = By.tagName(value);
                break;
            case "xpath":
                by = By.xpath(value);
                break;
            case "cssSelector":
                by = By.cssSelector(value);
                break;
            case "linkText":
                by = By.linkText(value);
                break;
            case "name":
                by = By.name(value);
                break;
            case "partialLinkText":
                by = By.partialLinkText(value);
                break;
            default:
                throw new IllegalStateException("locator : " + selector + " not found!!!");
        }
        return by;
    }

    public void scrollToAndClick(WebElement element) throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        scrollIntoView(element);
        element.click();
        waitForPageLoad();
    }

    public void executeJS(Object... value) {
        JavascriptExecutor executor = (JavascriptExecutor) DriverManagement.getDriverManagerInstance().getDriver();
        if (value.length < 2) {
            executor.executeScript("return " + value[0] + ";");
        }else if(value.length == 2){
            executor.executeScript(value[0].toString(),value[1]);
        }
    }

    public void scrollIntoView(WebElement element){
        executeJS("arguments[0].scrollIntoView(true)", element);
    }

    public void waitForPageLoad() throws InterruptedException {
        this.pause(Constant.DEFAULT_TIME);
        ExpectedCondition<Boolean> pageLoadCompleted = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver _driver) {
                return ((JavascriptExecutor) _driver).executeScript("return document.readyState")
                        .toString()
                        .equalsIgnoreCase("complete");
            }
        };
        wait.until(pageLoadCompleted);
    }


    public void pause(int time) throws InterruptedException {
        Thread.sleep(time*1000);
    }
}
