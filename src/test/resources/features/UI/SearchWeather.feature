Feature: Search Weather in city

  @UI @API
  Scenario Outline: TC-01 View Summary Weather Information
    Given I am on the OpenWeather Home page
    When I search "<city>" city from navigation bar
    Then I can see the city with correct summary weather information

    Examples:
      | city       |
      | Ha Noi, VN |
      | My Tho, VN |

  @UI @API
  Scenario Outline: TC-02 View 8-Day Weather Forecast
    Given I am on the OpenWeather Home page
    When I search "<city>" city from navigation bar
    And I click on the city hyperlink
    Then I can see 8-Day Weather Forecast with correct weather information

    Examples:
      | city       |
      | My Tho, VN |

