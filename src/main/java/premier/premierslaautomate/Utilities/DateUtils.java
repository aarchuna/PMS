package premier.premierslaautomate.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public static LocalDate stringToDate(String date) throws ParseException {
        if(date == null)
            return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return localDate;
    }
}
