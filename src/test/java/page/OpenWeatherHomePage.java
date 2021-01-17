package page;

import io.cucumber.guice.ScenarioScoped;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

@ScenarioScoped
public class OpenWeatherHomePage extends BasePage {

    @FindBy(how = How.ID, using = "q")
    public WebElement txtSearchCity;

    public void searchCity(String city){
        txtSearchCity.sendKeys(city);

        // Send key Enter to search
        txtSearchCity.sendKeys(Keys.ENTER);
    }
}
