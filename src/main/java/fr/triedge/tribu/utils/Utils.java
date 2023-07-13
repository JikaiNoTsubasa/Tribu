package fr.triedge.tribu.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Utils {
    public static long localDateTimeToMilli(LocalDateTime time){
        return ZonedDateTime.of(time, Vars.TIMEZONE).toInstant().toEpochMilli();
    }

    public static LocalDateTime milliToLocalDateTime(long milli){
        return Instant.ofEpochMilli(milli).atZone(Vars.TIMEZONE).toLocalDateTime();
    }

    public static boolean isValid(String value){
        return value != null && !value.equals("");
    }

    public static boolean isValid(Integer value){
        return value!=null && value > 0;
    }

    public static boolean isValid(String... values){
        for (String s : values){
            if (!isValid(s))
                return false;
        }
        return true;
    }
}
