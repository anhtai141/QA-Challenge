package stepdefinition.ui;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.google.inject.Inject;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
import page.OpenWeatherHomePage;
import page.WeatherDetailsPage;
import page.WeatherInYourCityPage;
import util.Report;
import util.ShareState;

import java.io.IOException;
public class SearchWeather {

    @Inject
    OpenWeatherHomePage openWeatherHomePage;

    @Inject
    WeatherInYourCityPage weatherInYourCityPage;

    @Inject
    WeatherDetailsPage weatherDetailsPage;

    private ShareState shareState;

    @Inject
    public SearchWeather(ShareState shareState) throws IOException {
        this.shareState = shareState;
    }

    @Given("I am on the OpenWeather Home page")
    public void iAmOnTheOpenWeatherHomePage() {
        Report.getReportInstance().getExtentStep().createNode("INFO - ").info(MarkupHelper.
                createLabel("OpenWeather Home page", ExtentColor.PURPLE));
    }

    @When("I search {string} city from navigation bar")
    public void iSearchCityFromNavigationBar(String city) {
        this.shareState.customAttributes.put("city", city);
        openWeatherHomePage.searchCity(city);
    }

    @Then("I can see the city with correct summary weather information")
    public void iCanSeeTheCityWithCorrectWeatherInformation() throws IOException {
        weatherInYourCityPage.verifySummaryWeatherInfo(this.shareState.customAttributes.get("city").toString());
    }

    @When("I click on the city hyperlink")
    public void iClickOnTheCityHyperlink() throws InterruptedException {
        weatherInYourCityPage.clickCityLink();
        weatherDetailsPage.wait.until(ExpectedConditions.visibilityOf(weatherDetailsPage.tbl8DayForeCast));
    }
}
