package util;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;

public class APIUtilities {

    public static final String FIND_CITY_URL = "/find";
    public static final String ONE_CALL_URL = "/onecall";
    public static String API_KEY;

    static {
        try {
            API_KEY = System.getProperty("SUT.API_KEY",
                    CommonHelper.getProperty("properties/ProjectInformation.properties", "SUT.API_KEY"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Response findCityAndGetWeatherInfo(String city, String unit){
        RequestSpecification httpRequest = RestAssured.given()
                .queryParam("q",city)
                .queryParam("units", unit)
                .queryParam("appid",API_KEY);

        return (Response) httpRequest.get(FIND_CITY_URL);
    }

    public static Response getAllWeatherInfo(String lon, String lat, String unit){
        RequestSpecification httpRequest = RestAssured.given()
                .queryParam("lon",lon)
                .queryParam("lat",lat)
                .queryParam("units", unit)
                .queryParam("appid",API_KEY);

        return (Response) httpRequest.get(ONE_CALL_URL);
    }
}
