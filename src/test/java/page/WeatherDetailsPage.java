package page;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.google.inject.Inject;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.Assert;
import util.APIUtilities;
import util.CommonHelper;
import util.Report;
import util.ShareState;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ScenarioScoped
public class WeatherDetailsPage extends BasePage {

    @FindBy(how = How.XPATH, using = "//h3[text()='8-day forecast']/following-sibling::ul/li")
    public List<WebElement> lstDailyForeCast;

    @FindBy(how = How.XPATH, using = "//ul[@class='day-list']")
    public WebElement tbl8DayForeCast;

    public ShareState shareState;

    @Inject
    public WeatherDetailsPage(ShareState shareState){
        this.shareState = shareState;
    }

    public void verify8DayForecastWeather(String city, String unit) throws InterruptedException {

        // Get weather info from UI
        List<Map<String, String>> eightDayForeCastOnUI = getEightDayForeCastOnUI();

        // Get Data from response
        List<Map<String, String>> eightDayForeCastFromResponse = getEightDayForeCastFromAPI(city,unit);

        Report.getReportInstance().getExtentStep().createNode("INFO -").info(MarkupHelper.
                createLabel(String.format("Actual Forecast: %s <br/>, Forecast city: %s </br>"
                        , eightDayForeCastOnUI, eightDayForeCastFromResponse), ExtentColor.PURPLE));

        Assert.assertEquals(eightDayForeCastOnUI, eightDayForeCastFromResponse);
    }

    public List<Map<String, String>> getEightDayForeCastOnUI() throws InterruptedException {
        waitForPageLoad();
        List<Map<String, String>> eightDayForeCastInfo = new ArrayList<Map<String, String>>();
        for (WebElement ele : lstDailyForeCast) {
            Map<String, String> DailyForeCastInfo = new HashMap<>();

            String date = ele.findElement(By.xpath("./span")).getText();
            String temperature = ele.findElement(By.xpath("./div/div/span")).getText();
            String description = ele.findElement(By.xpath(".//span[@class='sub']")).getText();
            DailyForeCastInfo.put("date", date);
            DailyForeCastInfo.put("temperature", temperature.trim().substring(0 , temperature.length()-2));
            DailyForeCastInfo.put("description",description);
            eightDayForeCastInfo.add(DailyForeCastInfo);
        }

        return eightDayForeCastInfo;
    }

    public List<Map<String, String>> getEightDayForeCastFromAPI(String city, String unit){

        // Send request to find the Geographical coordinate
        Response response = APIUtilities.findCityAndGetWeatherInfo(city,unit);

        HashMap<String,Object> weatherInfo = (HashMap) response.jsonPath().getList("list").get(0);

        //get min temperature from API Response
        String lat = ((HashMap) weatherInfo.get("coord")).get("lat").toString();
        String lon = ((HashMap) weatherInfo.get("coord")).get("lon").toString();

        //get all weather Info
        Response allWeatherInfo =  APIUtilities.getAllWeatherInfo(lon,lat,unit);

        String timeZone = allWeatherInfo.jsonPath().getString("timezone");

        //Prepare necessary info to compare with UI
        List<Map<String, String>> resEightDayForeCast = new ArrayList<Map<String, String>>();

        //Get 8-day forecast
        List<Map<String,Object>> eightDayForeCast = allWeatherInfo.jsonPath().getList("daily");
        for (Map oneDayForeCast: eightDayForeCast){

            Map<String,String> dailyForeCast = new HashMap();

            //Get unixSecond
            Long unixSecond = Long.valueOf(oneDayForeCast.get("dt").toString());
            String formattedDate = CommonHelper.formatDateFromUnixSecond(unixSecond,timeZone,"E, MMM dd");
            dailyForeCast.put("date",formattedDate);

            //get min temp
            String minTemp = ((HashMap) oneDayForeCast.get("temp")).get("min").toString();
            //Round min temp to whole number
            BigDecimal roundedMinTemp = new BigDecimal(minTemp).setScale(0, RoundingMode.HALF_UP);

            //get max temp
            String maxTemp = ((HashMap) oneDayForeCast.get("temp")).get("max").toString();
            //Round max temp to whole number
            BigDecimal roundedMaxTemp = new BigDecimal(maxTemp).setScale(0, RoundingMode.HALF_UP);
            dailyForeCast.put("temperature", roundedMaxTemp + " / " + roundedMinTemp);

            //Get weather description
            String weatherDescription = ((HashMap) ((List) oneDayForeCast.get("weather")).get(0)).get("description").toString();
            dailyForeCast.put("description",weatherDescription);
            resEightDayForeCast.add(dailyForeCast);
        }

        return resEightDayForeCast;
    }

    @Then("I can see 8-Day Weather Forecast with correct weather information")
    public void iCanSeeCorrect8DayWeatherForecast() throws InterruptedException {
        verify8DayForecastWeather(this.shareState.customAttributes.get("city").toString(), "metric");
    }
}
