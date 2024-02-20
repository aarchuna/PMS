package premier.premierslaautomate.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SlaUtils {

    public static List<Long> splitArray(String values){
        return Arrays.asList(values.split(",")).stream().map(Long::parseLong).collect(Collectors.toList());
    }

    public static List<Integer> ConvertToIntegerList(String values){
        return Arrays.asList(values.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    public static Date stringToDate(String date, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(date);
    }
}
