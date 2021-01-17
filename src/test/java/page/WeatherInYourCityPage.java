package page;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.google.inject.Inject;
import io.cucumber.guice.ScenarioScoped;
import io.restassured.response.Response;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.Assert;
import util.APIUtilities;
import util.Report;
import util.ShareState;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

@ScenarioScoped
public class WeatherInYourCityPage extends BasePage {

    @FindBy(how = How.XPATH, using = "//table//a[contains(@href,'city')]")
    public WebElement lblCity;

    @FindBy(how = How.XPATH, using = "//table//span/parent::p")
    public WebElement lblSummaryWeatherInfo;

    public ShareState shareState;

    @Inject
    public WeatherInYourCityPage(ShareState shareState) throws IOException {
        this.shareState = shareState;
    }

   public void verifySummaryWeatherInfo(String city) throws IOException {

        // Get city on label
        String actualCity = lblCity.getText();

        Report.getReportInstance().getExtentStep().createNode("INFO -").info(MarkupHelper.
               createLabel(String.format("Actual city: %s, expected city: %s"
                       , actualCity, city), ExtentColor.PURPLE));

        Assert.assertEquals(actualCity, city);

        // Get temperature info
        String strTemp = lblSummaryWeatherInfo.getText();

        Response response = APIUtilities.findCityAndGetWeatherInfo(city, "metric");

        HashMap<String,Object> weatherInfo = (HashMap) response.jsonPath().getList("list").get(0);

        //get min temperature from API Response
        String minTemp = ((HashMap) weatherInfo.get("main")).get("temp_min").toString();

        //Round min temp to whole number
        BigDecimal roundedMinTemp = new BigDecimal(minTemp).setScale(0, RoundingMode.HALF_UP);

        //get max temperature from API Response
        String maxTemp = ((HashMap) weatherInfo.get("main")).get("temp_max").toString();

        //Round max temp to whole number
        BigDecimal roundedMaxTemp = new BigDecimal(maxTemp).setScale(0, RoundingMode.HALF_UP);

        //get pressure from API Response
        String pressure = ((HashMap) weatherInfo.get("main")).get("pressure").toString();

        //Get wind speed from API Response
        String wind_speed = ((HashMap) weatherInfo.get("wind")).get("speed").toString();

        //Get cloud % from API Response
        String cloud = ((HashMap) weatherInfo.get("clouds")).get("all").toString();

        // Prepare expected min/max temperature
        String expectedMinMaxTemp = "temperature from " + roundedMinTemp.toString() + " to "
                + roundedMaxTemp;

        // Prepare expected wind speed
        String expectedWindSpeed = "wind " + wind_speed + " m/s";

        // Prepare expected cloud
        String expectedCloud = "clouds " + cloud + " %";

        //Prepare expected pressure

        String expectedPressure = pressure + " hpa";

        Report.getReportInstance().getExtentStep().createNode("INFO -").info(MarkupHelper.
               createLabel(String.format("Actual weather info: %s</br>, expected containing min/max_temp: %s</br>"
                       , strTemp, expectedMinMaxTemp), ExtentColor.PURPLE));
        Assert.assertTrue(strTemp.contains(expectedMinMaxTemp));

        Report.getReportInstance().getExtentStep().createNode("INFO -").info(MarkupHelper.
               createLabel(String.format("Actual weather info: %s</br>, expected containing wind speed: %s</br>"
                       , strTemp, expectedWindSpeed), ExtentColor.PURPLE));
        Assert.assertTrue(strTemp.contains(expectedWindSpeed));

        Report.getReportInstance().getExtentStep().createNode("INFO -").info(MarkupHelper.
               createLabel(String.format("Actual weather info: %s</br>, expected containing cloud: %s</br>"
                       , strTemp, expectedCloud), ExtentColor.PURPLE));
        Assert.assertTrue(strTemp.contains(expectedCloud));


        Assert.assertTrue(strTemp.contains(expectedPressure));

        Report.getReportInstance().getExtentStep().createNode("INFO -").info(MarkupHelper.
               createLabel(String.format("Actual weather info: %s</br>, expected containing pressure: %s</br>"
                       , strTemp, expectedPressure), ExtentColor.PURPLE));
    }

    public void clickCityLink() throws InterruptedException {

        lblCity.click();

    }
}
