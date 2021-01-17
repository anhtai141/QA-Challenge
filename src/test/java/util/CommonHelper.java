package util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

public class CommonHelper {

    public static String getProperty(String file, String property) throws IOException {

        InputStream input = CommonHelper.class.getClassLoader().getResourceAsStream(file);
        if(input == null){
            System.out.println("Error when opening the property file!");
            return "";
        }
        Properties prop = new Properties();
        prop.load(input);
        return prop.getProperty(property);
    }

    public static void setProperties(String file) throws IOException {
        InputStream input = CommonHelper.class.getClassLoader().getResourceAsStream(file);
        if(input == null){
            System.out.println("Error when opening the property file!");
        }
        Properties prop = new Properties();
        prop.load(input);
        prop.keySet().forEach(
                item -> System.setProperty(item.toString(), prop.getProperty(item.toString()))
        );
    }

    public static String formatDateFromUnixSecond (Long second, String timezone, String patten){

        Date date = new java.util.Date(second * 1000L);
        // the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat(patten);

        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(date);
    }
}
