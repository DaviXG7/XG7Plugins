package com.xg7plugins.xg7plugins.utils.Text;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.utils.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text {

    public static long convertToMilliseconds(Plugin plugin, String timeStr) {
        long milliseconds = 0;
        Pattern pattern = Pattern.compile("(\\d+)([SMHD])");
        Matcher matcher = pattern.matcher(timeStr.toUpperCase());

        while (matcher.find()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "S":
                    milliseconds += value * 1000;
                    break;
                case "M":
                    milliseconds += value * 60000;
                    break;
                case "H":
                    milliseconds += value * 3600000;
                    break;
                case "D":
                    milliseconds += value * 86400000;
                    break;
                default:
                    Log.severe(plugin,"Invalid time unit: " + unit);
            }
        }

        return milliseconds;
    }

}
