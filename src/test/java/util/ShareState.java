package util;

import io.cucumber.guice.ScenarioScoped;

import java.util.HashMap;

@ScenarioScoped
public class ShareState {
    public HashMap<String, Object> customAttributes = new HashMap<>();
}
