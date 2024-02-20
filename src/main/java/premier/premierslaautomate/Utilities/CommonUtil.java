package premier.premierslaautomate.Utilities;
import premier.premierslaautomate.Models.*;
import premier.premierslaautomate.Models.ADO.RevisionValue;
import premier.premierslaautomate.Models.ADO.TestCaseReference;
import premier.premierslaautomate.Models.ADO.WorkItemFields;
import premier.premierslaautomate.config.MeasureConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;;
public class CommonUtil {
    String newLine = "\r\n";
    public boolean WriteToCSv (List<String[]> data, String fileName)
    {
        try
        {
            if(data != null) {
                File csvFile = new File(fileName);
                try (PrintWriter pw = new PrintWriter(csvFile)) {
                    data.stream()
                            .map(this::convertToCSV)
                            .forEach(pw::println);
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public boolean CreateNewFile(String fileName)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    public boolean DeleteFile (String fileNameWithPath)
    {
        Path path = Paths.get(fileNameWithPath);
        if (path != null)
        {
            try
            {
                Files.deleteIfExists(path);
                return true;
            }
            catch (Exception ex)
            {
                return false;
            }
        }
        return false;
    }
    public boolean WriteToFile(String fileName, String text)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(newLine);
            writer.append(text);
            writer.close();
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    public ProcessedData BuildProcessData (MeasureConfiguration sla, float actual, String status)
    {
        ProcessedData data = new ProcessedData();
        data.setSLAName(sla.getSlaname());
        data.setSLAType(sla.getSlatype());
        data.setSLAMeasurementPeriod("Monthly");
        data.setExpectedServiceLevel(sla.getExpectedsla());
        data.setMinimumServiceLevel(sla.getMinimumsla());
        data.setNumCount(sla.getMinimumsla());
        data.setDenCount(sla.getMinimumsla());
        data.setActual(String.valueOf(actual));
        data.setSlaStatus(status);
        return data;
    }

    public ProcessedData BuildProcessData (MeasureConfiguration sla, float actual, String status, String NumCount, String DenoCount,String AdoQuery)
    {
        ProcessedData data = new ProcessedData();
        data.setSLAName(sla.getSlaname());
        data.setSLAKey(sla.getSlakey());
        data.setSLAType(sla.getSlatype());
        data.setExpectedServiceLevel(sla.getExpectedsla());
        data.setMinimumServiceLevel(sla.getMinimumsla());
        data.setNumCount(NumCount);
        data.setDenCount(DenoCount);
        data.setActual(String.valueOf(actual));
        data.setSlaStatus(status);
        data.setAdoQuery(AdoQuery);
        return data;
    }

    public Float GetActualValue(int demoCount, int numCount)
    {
        Float returnValue = 0f;
        try
        {
            double dValue = numCount/demoCount;
            returnValue = (float)(dValue * 100);
            return returnValue;
        }
        catch (Exception ex)
        {
            return returnValue;
        }
    }

    public double GetActualValueV1(double demoCount, double numCount)
    {
        double returnValue = 0f;
        try
        {
            double dValue = numCount/demoCount;
            returnValue = (dValue * 100);
            DecimalFormat df = new DecimalFormat("#.##");
            return returnValue;
        }
        catch (Exception ex)
        {
            return returnValue;
        }
    }

    public String CalculateFinalSLAValue(float actual, double expected, double minsla)
    {
        String returnValue = "";
        try
        {
            if (actual >= minsla )
            {
                returnValue = "Met";
            }
            else
            {
                returnValue = "Not Met";
            }
            return returnValue;
        }
        catch (Exception ex)
        {
            return returnValue;
        }
    }

    public String CalculateFinalSLAValueV1(double actual, double expected, double minsla)
    {
        String returnValue = "";
        try
        {
            if (actual >= expected )
            {
                returnValue = "Met";
            }
            else
            {
                returnValue = "Not Met";
            }
            return returnValue;
        }
        catch (Exception ex)
        {
            return returnValue;
        }
    }

    public String CalculateFinalSLAValueV2(double actual, double expected, double minsla)
    {
        String returnValue = "";
        try
        {
            if (actual <= expected )
            {
                returnValue = "Met";
            }
            else
            {
                returnValue = "Not Met";
            }
            return returnValue;
        }
        catch (Exception ex)
        {
            return returnValue;
        }
    }



    public String EstimationQualitySlaStatus(double actual, double expected, double minsla)
    {
        String returnValue = "";
        try
        {
            if (actual >= 115 )
            {
                returnValue = "Not Met";
            }
            else if (actual <= 85)
            {
                returnValue = "Not Met";
            }
            else
            {
                returnValue = "Met";
            }
            return returnValue;
        }
        catch (Exception ex)
        {
            return returnValue;
        }
    }

    public String timeZone4hoursv1(String dateTimeReceived)
    {
        String updatedDateString=null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateTimeReceived = dateTimeReceived.replaceAll("\\.\\d+", "");
        try
        {
            Date date = dateFormat.parse(dateTimeReceived);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, -4);
            Date newDate = calendar.getTime();
            updatedDateString = dateFormat.format(newDate);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return updatedDateString;
    }

    public String timeZone4hoursv2(String dateTimeReceived)
    {
        String updatedDateString=null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateTimeReceived = dateTimeReceived.replaceAll("\\.\\d+", "");
        try
        {
            Date date = dateFormat.parse(dateTimeReceived);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, -4);
            Date newDate = calendar.getTime();
            updatedDateString = dateFormat.format(newDate);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return updatedDateString;
    }


    public boolean isDateValid(String dateStr ,String dateFormat) {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public Date ConvertToDate (String strDate, String dateFormat)
    {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public Date ConvertStringToDateForZFormat (String strDate)
    {
        try
        {
            Date returnDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(strDate.replaceAll("Z$", "+0000"));
            return returnDate;
        }
        catch (Exception ex)
        {
            //Check One more time
            try
            {
                Date returnDate1 = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).parse(strDate.replaceAll("Z$", "+0000"));
                return returnDate1;
            }
            catch (Exception ex1)
            {
            }
            return null;
        }
    }

    public String ConvertStringToDate (Date date, String dateFormat)
    {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            return  sdf.format(date);
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public String ConvertDateToString (Date date, String dateFormat)
    {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            return  sdf.format(date);
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public Date ConvertDateFromOneFormatToAnother (Date date, String sourceFormat, String destinationFormat)
    {
        Date returnValue = new Date();
        returnValue = null;
        String strDate = "";

        try
        {
            strDate = this.ConvertStringToDate(date, sourceFormat);

            if (!strDate.isEmpty())
            {
                DateTimeFormatter formatterSource = DateTimeFormatter.ofPattern(sourceFormat);
                DateTimeFormatter formatterDestination = DateTimeFormatter.ofPattern(destinationFormat);

                String processValue = LocalDate.parse(strDate, formatterSource).format(formatterDestination);

                if (!processValue.isEmpty())
                {
                    returnValue = this.ConvertToDate(processValue, destinationFormat);
                }
            }
            return returnValue;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public long GetDayVariance (Date dateSource, Date dateDestination, List<Date> lstHolidays, String checkHolidays, String checkWeekend)
    {
        int noOfdaysToReduce = 0;
        try
        {
            if (dateSource.compareTo(dateDestination) > 0)
            {
                //Source date cannot be greater than destination date
                return -1;
            }

            if (checkHolidays.equals("Y"))
            {
                if (lstHolidays == null || lstHolidays.size() == 0)
                {
                    return -1;
                }
            }

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date newDateSource = formatter.parse(formatter.format(dateSource));
            Date newDateDestination = formatter.parse(formatter.format(dateDestination));
            dateSource = newDateSource;
            dateDestination = newDateDestination;

            Date date = dateSource;

            while (date.compareTo(dateDestination) <= 0)
            {
                boolean isHoliday = false;
                boolean isWeekend = false;

                //Check for holiday
                if (checkHolidays.equals("Y"))
                {
                    for(Date dt: lstHolidays)
                    {
                        if (date.compareTo(dt) == 0)
                        {
                            isHoliday = true;
                        }
                    }
                }

                //check Weekend
                if (checkWeekend.equals("Y"))
                {
                    LocalDate lDate = convertToLocalDateViaInstant(date);
                    isWeekend = isWeekend(lDate);
                }

                if (isHoliday == true || isWeekend == true)
                {
                    noOfdaysToReduce++;
                }

                //date = new Date(date.getTime() + (1000 * 60 * 60 * 24));

                date = addDays(date, 1);
            }

            long diffInMillies = Math.abs(dateDestination.getTime() - dateSource.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            return (diff - noOfdaysToReduce);
        }
        catch (Exception ex)
        {
            return -1;
        }
    }

    public long GetDayVariance (Date dateSource, Date dateDestination, List<Date> lstHolidays, String checkHolidays, String checkWeekend, String includeCommittedDate)
    {
        int noOfdaysToReduce = 0;
        int increaseDays = 0;
        boolean isHoliday = false;
        boolean isWeekend = false;

        try
        {
            if (dateSource.compareTo(dateDestination) > 0)
            {
                //Source date cannot be greater than destination date
                return -1;
            }

            if (checkHolidays.equals("Y"))
            {
                if (lstHolidays == null || lstHolidays.size() == 0)
                {
                    return -1;
                }
            }

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date newDateSource = formatter.parse(formatter.format(dateSource));
            Date newDateDestination = formatter.parse(formatter.format(dateDestination));
            dateSource = newDateSource;
            dateDestination = newDateDestination;

            Date date = dateSource;

            boolean checkCommittedDateHolidayOrWeekend = false;
            //This is to check whether the committed date is a holiday or weekend. If yes then donot add 1 day
            if (includeCommittedDate.equals("Y"))
            {
                if (checkHolidays.equals("Y"))
                {
                    for(Date dt: lstHolidays)
                    {
                        if (date.compareTo(dt) == 0)
                        {
                            checkCommittedDateHolidayOrWeekend = true;
                        }
                    }
                }

                if (checkCommittedDateHolidayOrWeekend == false)
                {
                    if (checkWeekend.equals("Y"))
                    {
                        isWeekend = false;
                        LocalDate lDate = convertToLocalDateViaInstant(date);
                        isWeekend = isWeekend(lDate);
                        if (isWeekend == true)
                        {
                            checkCommittedDateHolidayOrWeekend = true;
                        }
                    }
                }
            }
            else
            {
                //as the call is to not to include committed date
                checkCommittedDateHolidayOrWeekend = true;
            }

            if (checkCommittedDateHolidayOrWeekend == false)
            {
                increaseDays = 1;
            }

            //Here donot start from the Start date to check the holiday or weekend as
            //that day will not be calculated when do a day variance
            date = addDays(date, 1);
            while (date.compareTo(dateDestination) <= 0)
            {
                isHoliday = false;
                isWeekend = false;

                //Check for holiday
                if (checkHolidays.equals("Y"))
                {
                    for(Date dt: lstHolidays)
                    {
                        if (date.compareTo(dt) == 0)
                        {
                            isHoliday = true;
                        }
                    }
                }

                //check Weekend
                if (checkWeekend.equals("Y"))
                {
                    LocalDate lDate = convertToLocalDateViaInstant(date);
                    isWeekend = isWeekend(lDate);
                }

                if (isHoliday == true || isWeekend == true)
                {
                    noOfdaysToReduce++;
                }



                date = addDays(date, 1);
            }

           long diffInMillies = Math.abs(dateDestination.getTime() - dateSource.getTime());

            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            return ((diff - noOfdaysToReduce) + increaseDays);
        }
        catch (Exception ex)
        {
            return -1;
        }
    }

    public double GetWorkingHourOrMinVarianceOld (Date dateSource, Date dateDestination, List<Date> lstHolidays, String checkHolidays, String checkWeekend, String sourceDateFormat, int totalWorkingHoursInDay, String ReturnHourOrMinute)
    {
        double noOfHolidayWeekendHoursToReduce = 0;
        double noOfdayHoursToReduce = 0;
        double noOfMinutesToReduce = 0;

        double curDifferMin = 0;
        double curDifferHour = 0;
        long curDiffMillisecond = 0;
        double returnValue = 0;

        int dayCur = 0;
        int monthCur = 0;
        int yearCur = 0;

        int daynext = 0;
        int monthNext = 0;
        int yearNext = 0;

        int dayDest = 0;
        int monthDest = 0;
        int yearDest = 0;

        int hourFirstDay = 0;
        int minFirstday = 0;
        int hourLastDay = 0;
        int minLastday = 0;

        int sec = 0;

        double totaldifferenceHour = 0;
        double totaldifferenceMin = 0;

        //bypassing the sourceDateFormat as we have a date field
        sourceDateFormat = "yyyy-MM-dd HH:mm:ss";

        try
        {
            if (dateSource.compareTo(dateDestination) > 0)
            {
                //Source date cannot be greater than destination date
                return -1;
            }

            if (checkHolidays.equals("Y"))
            {
                if (lstHolidays == null || lstHolidays.size() == 0)
                {
                    return -1;
                }
            }

            //Get the Total Hours between these two dates
            long diffInMillies = Math.abs(dateDestination.getTime() - dateSource.getTime());
            long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            long diffHour = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            long diffMin = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
            long diffSec = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            double totalDifferenceInHour = (double) diffMin / (double) 60;

            LocalDateTime firstDate = null;
            LocalDateTime secondDate = null;

            String strFirstDate = "";
            String strSecondDate = "";

            Date newDate = new Date();
            Date newEndDate = new Date();
            String strDate = "";

            Date date = dateSource;
            while (date.compareTo(dateDestination) <= 0)
            {
                boolean isNotWorkingDay = false;
                dayCur = 0;
                monthCur = 0;
                yearCur = 0;

                daynext = 0;
                monthNext = 0;
                yearNext = 0;

                dayDest = 0;
                monthDest = 0;
                yearDest = 0;

                hourFirstDay = 0;
                minFirstday = 0;
                hourLastDay = 0;
                minLastday = 0;

                curDifferMin = 0;
                curDifferHour = 0;
                curDiffMillisecond = 0;

                totaldifferenceMin = 0;
                totaldifferenceHour = 0;

                firstDate = null;

                strFirstDate = "";
                strSecondDate = "";

                if (checkWeekend.equals("Y"))
                {
                    LocalDate lDate = convertToLocalDateViaInstant(date);
                    isNotWorkingDay = isWeekend(lDate);
                }

                if (checkHolidays.equals("Y"))
                {
                    if (isNotWorkingDay == false)
                    {
                        for(Date dt: lstHolidays)
                        {
                            if (date.compareTo(dt) == 0)
                            {
                                isNotWorkingDay = true;
                            }
                        }
                    }
                }

                //Calculate the total No of hours based on total working hours
                strFirstDate = this.ConvertDateToString(date, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayCur = firstDate.getDayOfMonth();
                monthCur = firstDate.getMonthValue();
                yearCur = firstDate.getYear();
                hourFirstDay = firstDate.getHour();
                minFirstday = firstDate.getMinute();

                strFirstDate = this.ConvertDateToString(dateSource, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                daynext = firstDate.getDayOfMonth();
                monthNext = firstDate.getMonthValue();
                yearNext = firstDate.getYear();

                strFirstDate = this.ConvertDateToString(dateDestination, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayDest = firstDate.getDayOfMonth();
                monthDest = firstDate.getMonthValue();
                yearDest = firstDate.getYear();
                hourLastDay = firstDate.getHour();
                minLastday = firstDate.getMinute();

                if (dayCur == daynext && monthCur == monthNext && yearCur == yearNext)
                {
                    //this is the First date
                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " 24:00:00";
                    newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);;
                    curDiffMillisecond = Math.abs(newDate.getTime() - date.getTime());
                    totaldifferenceMin = TimeUnit.MINUTES.convert(curDiffMillisecond, TimeUnit.MILLISECONDS);
                    totaldifferenceHour = (double)totaldifferenceMin/(double) 60;

                    if (isNotWorkingDay == true)
                    {
                        //IF it is not a working day reduce all the hours in that day
                        noOfHolidayWeekendHoursToReduce = noOfHolidayWeekendHoursToReduce + totaldifferenceHour;
                    }
                    else
                    {
                        if (totalWorkingHoursInDay < 24)
                        {
                            //Reduce the Pending Hours after the total day hours
                            if (totaldifferenceHour > totalWorkingHoursInDay)
                            {
                                noOfdayHoursToReduce = noOfdayHoursToReduce + (totaldifferenceHour - (long)totalWorkingHoursInDay);
                            }
                        }
                    }
                }
                else if (dayCur == dayDest && monthCur == monthDest && yearCur == yearDest)
                {
                    //this is the Last date
                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " 00:00:00";
                    newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);;
                    curDiffMillisecond = Math.abs(newDate.getTime() - date.getTime());
                    totaldifferenceMin = TimeUnit.MINUTES.convert(curDiffMillisecond, TimeUnit.MILLISECONDS);
                    totaldifferenceHour = (double)totaldifferenceMin/(double) 60;

                    if (isNotWorkingDay == true)
                    {
                        //IF it is not a working day reduce all the hours in that day
                        noOfHolidayWeekendHoursToReduce = noOfHolidayWeekendHoursToReduce + totaldifferenceHour;
                    }
                    else
                    {
                        if (totalWorkingHoursInDay < 24)
                        {
                            //Reduce the Pending Hours after the total day hours
                            if (totaldifferenceHour > totalWorkingHoursInDay)
                            {
                                noOfdayHoursToReduce = noOfdayHoursToReduce + (totaldifferenceHour - (long)totalWorkingHoursInDay);
                            }
                        }
                    }
                }
                else
                {
                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " 00:00:00";
                    newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);

                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " 24:00:00";
                    newEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);

                    curDiffMillisecond = newEndDate.getTime() - newDate.getTime();
                    totaldifferenceMin = TimeUnit.MINUTES.convert(curDiffMillisecond, TimeUnit.MILLISECONDS);
                    totaldifferenceHour = (double)totaldifferenceMin/(double) 60;

                    //Normal day with 24 hours
                    if (isNotWorkingDay == true)
                    {
                        //reduce 24 hours for that day
                        noOfHolidayWeekendHoursToReduce = noOfHolidayWeekendHoursToReduce + totaldifferenceHour;
                    }
                    else
                    {
                        //Need to only calculate only total no of working hours for that day
                        //i.e reduce Total hour difference - Total Working hours / day if total working hours is not 24 hours per day
                        if (totalWorkingHoursInDay < 24)
                        {
                            //Reduce the Pending Hours after the total day hours
                            if (totaldifferenceHour > totalWorkingHoursInDay)
                            {
                                noOfdayHoursToReduce = noOfdayHoursToReduce + (totaldifferenceHour - (long)totalWorkingHoursInDay);
                            }
                        }
                    }
                }

                //Add 1 day to current date and set the time if it is not the last date
                date = addDays(date, 1);
                strFirstDate = this.ConvertDateToString(date, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayCur = firstDate.getDayOfMonth();
                monthCur = firstDate.getMonthValue();
                yearCur = firstDate.getYear();

                strFirstDate = this.ConvertDateToString(dateDestination, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayDest = firstDate.getDayOfMonth();
                monthDest = firstDate.getMonthValue();
                yearDest = firstDate.getYear();

                if (dayCur == dayDest && monthCur == monthDest && yearCur == yearDest)
                {
                    //Last date. So we need to take the date and time for that
                    date = dateDestination;
                }
                else
                {
                    date.setHours(12);
                    date.setMinutes(00);
                    date.setSeconds(01);
                }
            }

            returnValue = (totalDifferenceInHour - (noOfdayHoursToReduce + noOfHolidayWeekendHoursToReduce));
            if (ReturnHourOrMinute.equals("M"))
            {
                returnValue = returnValue * 60;
            }

            return returnValue;
        }
        catch (Exception ex)
        {
            return -1;
        }
    }

    public double GetWorkingHourOrMinVariance (Date dateSource, Date dateDestination, List<Date> lstHolidays, String checkHolidays, String checkWeekend, String sourceDateFormat, int totalWorkingHoursInDay, String ReturnHourOrMinute)
    {
        double noOfHolidayWeekendHoursToReduce = 0;
        double noOfdayHoursToReduce = 0;
        double noOfMinutesToReduce = 0;

        double curDifferMin = 0;
        double curDifferHour = 0;
        long curDiffMillisecond = 0;
        double returnValue = 0;

        int dayCur = 0;
        int monthCur = 0;
        int yearCur = 0;

        int daynext = 0;
        int monthNext = 0;
        int yearNext = 0;

        int dayDest = 0;
        int monthDest = 0;
        int yearDest = 0;

        int hourFirstDay = 0;
        int minFirstday = 0;
        int hourLastDay = 0;
        int minLastday = 0;

        int sec = 0;

        double totaldifferenceHour = 0;
        double totaldifferenceMin = 0;

        //bypassing the sourceDateFormat as we have a date field
        sourceDateFormat = "yyyy-MM-dd HH:mm:ss";

        try
        {
            if (dateSource.compareTo(dateDestination) > 0)
            {
                //Source date cannot be greater than destination date
                return -1;
            }

            if (checkHolidays.equals("Y"))
            {
                if (lstHolidays == null || lstHolidays.size() == 0)
                {
                    return -1;
                }
            }

            //Get the Total Hours between these two dates
            long diffInMillies = Math.abs(dateDestination.getTime() - dateSource.getTime());
            long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            long diffHour = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            long diffMin = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
            long diffSec = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            double totalDifferenceInHour = (double) diffMin / (double) 60;

            LocalDateTime firstDate = null;
            LocalDateTime secondDate = null;

            String strFirstDate = "";
            String strSecondDate = "";

            Date newDate = new Date();
            Date newEndDate = new Date();
            String strDate = "";

            Date date = dateSource;
            while (date.compareTo(dateDestination) <= 0)
            {
                boolean isNotWorkingDay = false;
                dayCur = 0;
                monthCur = 0;
                yearCur = 0;

                daynext = 0;
                monthNext = 0;
                yearNext = 0;

                dayDest = 0;
                monthDest = 0;
                yearDest = 0;

                hourFirstDay = 0;
                minFirstday = 0;
                hourLastDay = 0;
                minLastday = 0;

                curDifferMin = 0;
                curDifferHour = 0;
                curDiffMillisecond = 0;

                totaldifferenceMin = 0;
                totaldifferenceHour = 0;

                firstDate = null;

                strFirstDate = "";
                strSecondDate = "";

                if (checkWeekend.equals("Y"))
                {
                    LocalDate lDate = convertToLocalDateViaInstant(date);
                    isNotWorkingDay = isWeekend(lDate);
                }

                if (checkHolidays.equals("Y"))
                {
                    if (isNotWorkingDay == false)
                    {
                        for(Date dt: lstHolidays)
                        {
                            if (date.compareTo(dt) == 0)
                            {
                                isNotWorkingDay = true;
                            }
                        }
                    }
                }

                //Calculate the total No of hours based on total working hours
                strFirstDate = this.ConvertDateToString(date, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayCur = firstDate.getDayOfMonth();
                monthCur = firstDate.getMonthValue();
                yearCur = firstDate.getYear();
                hourFirstDay = firstDate.getHour();
                minFirstday = firstDate.getMinute();

                strFirstDate = this.ConvertDateToString(dateSource, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                daynext = firstDate.getDayOfMonth();
                monthNext = firstDate.getMonthValue();
                yearNext = firstDate.getYear();

                strFirstDate = this.ConvertDateToString(dateDestination, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayDest = firstDate.getDayOfMonth();
                monthDest = firstDate.getMonthValue();
                yearDest = firstDate.getYear();
                hourLastDay = firstDate.getHour();
                minLastday = firstDate.getMinute();

                //this is the First date
                if (dayCur == daynext && monthCur == monthNext && yearCur == yearNext)
                {
                    if (dayCur == dayDest && monthCur == monthDest && yearCur == yearDest) // Source and destination date is same
                    {
                        strDate = yearDest + "-" + monthCur + "-" + dayCur + " " + hourLastDay + ":" + minLastday + ":00";
                        newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);;
                    }
                    else //Source date is not same as destination date so full calculation
                    {
                        strDate = yearCur + "-" + monthCur + "-" + dayCur + " 24:00:00";
                        newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);;
                    }

                    curDiffMillisecond = Math.abs(newDate.getTime() - date.getTime());
                    totaldifferenceMin = TimeUnit.MINUTES.convert(curDiffMillisecond, TimeUnit.MILLISECONDS);
                    totaldifferenceHour = (double)totaldifferenceMin/(double) 60;

                    if (isNotWorkingDay == true)
                    {
                        //IF it is not a working day reduce all the hours in that day
                        noOfHolidayWeekendHoursToReduce = noOfHolidayWeekendHoursToReduce + totaldifferenceHour;
                    }
                    else
                    {
                        if (totalWorkingHoursInDay < 24)
                        {
                            //Reduce the Pending Hours after the total day hours
                            if (totaldifferenceHour > totalWorkingHoursInDay)
                            {
                                noOfdayHoursToReduce = noOfdayHoursToReduce + (totaldifferenceHour - (long)totalWorkingHoursInDay);
                            }
                        }
                    }
                }
                else if (dayCur == dayDest && monthCur == monthDest && yearCur == yearDest)
                {
                    //this is the Last date
                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " 00:00:00";
                    newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);;
                    curDiffMillisecond = Math.abs(newDate.getTime() - date.getTime());
                    totaldifferenceMin = TimeUnit.MINUTES.convert(curDiffMillisecond, TimeUnit.MILLISECONDS);
                    totaldifferenceHour = (double)totaldifferenceMin/(double) 60;

                    if (isNotWorkingDay == true)
                    {
                        //IF it is not a working day reduce all the hours in that day
                        noOfHolidayWeekendHoursToReduce = noOfHolidayWeekendHoursToReduce + totaldifferenceHour;
                    }
                    else
                    {
                        if (totalWorkingHoursInDay < 24)
                        {
                            //Reduce the Pending Hours after the total day hours
                            if (totaldifferenceHour > totalWorkingHoursInDay)
                            {
                                noOfdayHoursToReduce = noOfdayHoursToReduce + (totaldifferenceHour - (long)totalWorkingHoursInDay);
                            }
                        }
                    }
                }
                else
                {
                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " 00:00:00";
                    newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);

                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " 24:00:00";
                    newEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);

                    curDiffMillisecond = newEndDate.getTime() - newDate.getTime();
                    totaldifferenceMin = TimeUnit.MINUTES.convert(curDiffMillisecond, TimeUnit.MILLISECONDS);
                    totaldifferenceHour = (double)totaldifferenceMin/(double) 60;

                    //Normal day with 24 hours
                    if (isNotWorkingDay == true)
                    {
                        //reduce 24 hours for that day
                        noOfHolidayWeekendHoursToReduce = noOfHolidayWeekendHoursToReduce + totaldifferenceHour;
                    }
                    else
                    {

                        if (totalWorkingHoursInDay < 24)
                        {
                            //Reduce the Pending Hours after the total day hours
                            if (totaldifferenceHour > totalWorkingHoursInDay)
                            {
                                noOfdayHoursToReduce = noOfdayHoursToReduce + (totaldifferenceHour - (long)totalWorkingHoursInDay);
                            }
                        }
                    }
                }

                //Add 1 day to current date and set the time if it is not the last date
                date = addDays(date, 1);
                strFirstDate = this.ConvertDateToString(date, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayCur = firstDate.getDayOfMonth();
                monthCur = firstDate.getMonthValue();
                yearCur = firstDate.getYear();

                strFirstDate = this.ConvertDateToString(dateDestination, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayDest = firstDate.getDayOfMonth();
                monthDest = firstDate.getMonthValue();
                yearDest = firstDate.getYear();

                if (dayCur == dayDest && monthCur == monthDest && yearCur == yearDest)
                {
                    //Last date. So we need to take the date and time for that
                    date = dateDestination;
                }
                else
                {
                    date.setHours(12);
                    date.setMinutes(00);
                    date.setSeconds(01);
                }
            }

            returnValue = (totalDifferenceInHour - (noOfdayHoursToReduce + noOfHolidayWeekendHoursToReduce));
            if (ReturnHourOrMinute.equals("M"))
            {
                returnValue = returnValue * 60;
            }

            return returnValue;
        }
        catch (Exception ex)
        {
            return -1;
        }
    }

    public double GetP3IncidnetWorkingHourOrMinutes (Date dateSource, Date dateDestination, List<Date> lstHolidays, String checkHolidays, String checkWeekend, String sourceDateFormat, int startingHour, int endingHour, String hourOrMinute)
    {
        double returnValue = 0;
        double totaldifferenceHour = 0;
        double totaldifferenceMin = 0;
        long curDiffMillisecond = 0;

        //bypassing the sourceDateFormat as we have a date field
        sourceDateFormat = "yyyy-MM-dd HH:mm:ss";

        try
        {
            if (dateSource.compareTo(dateDestination) > 0)
            {
                //Source date cannot be greater than destination date
                return -1;
            }

            if (checkHolidays.equals("Y"))
            {
                if (lstHolidays == null || lstHolidays.size() == 0)
                {
                    return -1;
                }
            }

            //Get the Total Hours between these two dates
            long diffInMillies = Math.abs(dateDestination.getTime() - dateSource.getTime());
            long diffMin = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
            double totalDifferenceInHour = (double) diffMin / (double) 60;

            Date dtWorkingDateWithStartTime = null;
            Date dtWorkingDateWithEndTime = null;
            Date dtWithStartingTime = null;
            Date dtWithEndingTime = null;
            Date calculationStartDateTime = null;
            Date calculationEndDateTime = null;

            String strFirstDate = "";
            String strSecondDate = "";
            LocalDateTime firstDate = null;

            int dayCur = 0;
            int monthCur = 0;
            int yearCur = 0;

            int daynext = 0;
            int monthNext = 0;
            int yearNext = 0;

            int dayDest = 0;
            int monthDest = 0;
            int yearDest = 0;

            int hourFirstDay = 0;
            int minFirstday = 0;
            int hourLastDay = 0;
            int minLastday = 0;

            String strDate = "";
            double totalWorkingHourInaDay = 0;
            double totalWorkingMinInaDay = 0;

            double totalWorkHours = 0;
            double totalWorkMin = 0;

            Date date = dateSource;

            while (date.compareTo(dateDestination) <= 0)
            {
                totalWorkingHourInaDay = 0;
                totalWorkingMinInaDay = 0;
                curDiffMillisecond = 0;
                strFirstDate = "";
                strSecondDate = "";
                firstDate = null;

                totalWorkingHourInaDay = 0;
                dayCur = 0;
                monthCur = 0;
                yearCur = 0;

                daynext = 0;
                monthNext = 0;
                yearNext = 0;

                dayDest = 0;
                monthDest = 0;
                yearDest = 0;

                hourFirstDay = 0;
                minFirstday = 0;
                hourLastDay = 0;
                minLastday = 0;

                dtWorkingDateWithStartTime = null;
                dtWorkingDateWithEndTime = null;
                dtWithStartingTime = null;
                dtWithEndingTime = null;
                calculationStartDateTime = null;
                calculationEndDateTime = null;

                boolean isNotWorkingDay = false;
                if (checkWeekend.equals("Y"))
                {
                    LocalDate lDate = convertToLocalDateViaInstant(date);
                    isNotWorkingDay = isWeekend(lDate);
                }

                if (checkHolidays.equals("Y"))
                {
                    if (isNotWorkingDay == false)
                    {
                        for(Date dt: lstHolidays)
                        {
                            if (date.compareTo(dt) == 0)
                            {
                                isNotWorkingDay = true;
                            }
                        }
                    }
                }

                strFirstDate = this.ConvertDateToString(date, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayCur = firstDate.getDayOfMonth();
                monthCur = firstDate.getMonthValue();
                yearCur = firstDate.getYear();
                hourFirstDay = firstDate.getHour();
                minFirstday = firstDate.getMinute();

                strFirstDate = this.ConvertDateToString(dateSource, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                daynext = firstDate.getDayOfMonth();
                monthNext = firstDate.getMonthValue();
                yearNext = firstDate.getYear();

                strFirstDate = this.ConvertDateToString(dateDestination, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayDest = firstDate.getDayOfMonth();
                monthDest = firstDate.getMonthValue();
                yearDest = firstDate.getYear();
                hourLastDay = firstDate.getHour();
                minLastday = firstDate.getMinute();

                if (dayCur == daynext && monthCur == monthNext && yearCur == yearNext)
                {
                    //First Date
                    //Check if the currentHout > starting working hour then reduce (CurrentWorkingHour-StartingWorkingHour)
                    //Calculate total workingHour
                    dtWorkingDateWithStartTime = null;
                    dtWorkingDateWithEndTime = null;
                    dtWithStartingTime = null;
                    dtWithEndingTime = null;

                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " " + startingHour + ":00:00";
                    dtWorkingDateWithStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);;
                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " " + endingHour + ":00:00";
                    dtWorkingDateWithEndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);;

                    //Here we should check if the Source date and destination date are same.
                    //if same then we should not take the end time as 12 PM instead we should take the
                    //work finish time from the desination date
                    //Get the destination time year, Month, day, hour and min
                    if (dayCur == dayDest && monthCur == monthDest && yearCur == yearDest) // Source and destination date is same
                    {
                        strDate = yearDest + "-" + monthCur + "-" + dayCur + " " + hourFirstDay + ":" + minFirstday + ":00";
                        dtWithStartingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
                        strDate = yearDest + "-" + monthCur + "-" + dayCur + " " + hourLastDay + ":" + minLastday + ":00";
                        dtWithEndingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);;
                    }
                    else //Source date is not same as destination date so full calculation
                    {
                        strDate = yearCur + "-" + monthCur + "-" + dayCur + " " + hourFirstDay + ":" + minFirstday + ":00";
                        dtWithStartingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
                        strDate = yearCur + "-" + monthCur + "-" + dayCur + " 24:00:00";
                        dtWithEndingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);;
                    }

                    //Get the Calculation Start Time
                    //If the currentDate starting time > the Standard Working start time then take the calculation start datetime as Current Starting time
                    //Else take the calculation Start date time as standard Working start time
                    if (dtWithStartingTime.compareTo(dtWorkingDateWithStartTime) == 1) //the current start time > standard woriking start time
                    {
                        calculationStartDateTime = dtWithStartingTime;
                    }
                    else
                    {
                        calculationStartDateTime = dtWorkingDateWithStartTime;
                    }

                    //Get the Calculation end Time
                    //If the standard Work End time is <= Day end time then calculatin end time = standard Work End time
                    //Else calculation end time = Day end time
                    if (dtWorkingDateWithEndTime.compareTo(dtWithEndingTime) == -1)
                    {
                        calculationEndDateTime = dtWorkingDateWithEndTime;
                    }
                    else
                    {
                        calculationEndDateTime = dtWithEndingTime;
                    }

                    curDiffMillisecond = Math.abs(calculationEndDateTime.getTime() - calculationStartDateTime.getTime());
                    totalWorkingMinInaDay = TimeUnit.MINUTES.convert(curDiffMillisecond, TimeUnit.MILLISECONDS);
                    totalWorkingHourInaDay = totalWorkingMinInaDay/60;

                    if (isNotWorkingDay == false)
                    {
                        totalWorkHours = totalWorkHours + totalWorkingHourInaDay;
                        totalWorkMin = totalWorkMin + totalWorkingMinInaDay;
                    }
                }
                else if (dayCur == dayDest && monthCur == monthDest && yearCur == yearDest)
                {
                    dtWorkingDateWithStartTime = null;
                    dtWorkingDateWithEndTime = null;
                    dtWithStartingTime = null;
                    dtWithEndingTime = null;

                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " " + startingHour + ":00:00";
                    dtWorkingDateWithStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " " + endingHour + ":00:00";
                    dtWorkingDateWithEndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);

                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " 00:00:00";
                    dtWithStartingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " " + hourLastDay + ":" + minLastday + ":00";
                    dtWithEndingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);

                    if (dtWorkingDateWithStartTime.compareTo(dtWithStartingTime) == 1)
                    {
                        calculationStartDateTime = dtWorkingDateWithStartTime;
                    }
                    else
                    {
                        calculationStartDateTime = dtWithStartingTime;
                    }

                    if (dtWorkingDateWithEndTime.compareTo(dtWithEndingTime) == -1)
                    {
                        calculationEndDateTime = dtWorkingDateWithEndTime;
                    }
                    else
                    {
                        calculationEndDateTime = dtWithEndingTime;
                    }

                    curDiffMillisecond = Math.abs(calculationEndDateTime.getTime() - calculationStartDateTime.getTime());
                    totalWorkingMinInaDay = TimeUnit.MINUTES.convert(curDiffMillisecond, TimeUnit.MILLISECONDS);
                    totalWorkingHourInaDay = totalWorkingMinInaDay/60;

                    if (isNotWorkingDay == false)
                    {
                        totalWorkHours = totalWorkHours + totalWorkingHourInaDay;
                        totalWorkMin = totalWorkMin + totalWorkingMinInaDay;
                    }
                }
                else
                {
                    //Normal day - Should take the full hours
                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " " + startingHour + ":00:00";
                    calculationStartDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);

                    strDate = yearCur + "-" + monthCur + "-" + dayCur + " " + endingHour + ":00:00";
                    calculationEndDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);

                    curDiffMillisecond = Math.abs(calculationEndDateTime.getTime() - calculationStartDateTime.getTime());
                    totalWorkingMinInaDay = TimeUnit.MINUTES.convert(curDiffMillisecond, TimeUnit.MILLISECONDS);
                    totalWorkingHourInaDay = totalWorkingMinInaDay/60;

                    if (isNotWorkingDay == false)
                    {
                        totalWorkHours = totalWorkHours + totalWorkingHourInaDay;
                        totalWorkMin = totalWorkMin + totalWorkingMinInaDay;
                    }
                }

                //Add 1 day to current date and set the time if it is not the last date
                date = addDays(date, 1);
                strFirstDate = this.ConvertDateToString(date, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayCur = firstDate.getDayOfMonth();
                monthCur = firstDate.getMonthValue();
                yearCur = firstDate.getYear();

                strFirstDate = this.ConvertDateToString(dateDestination, sourceDateFormat);
                firstDate = LocalDateTime.parse(strFirstDate, DateTimeFormatter.ofPattern(sourceDateFormat));

                dayDest = firstDate.getDayOfMonth();
                monthDest = firstDate.getMonthValue();
                yearDest = firstDate.getYear();

                if (dayCur == dayDest && monthCur == monthDest && yearCur == yearDest)
                {
                    //Last date. So we need to take the date and time for that
                    date = dateDestination;
                }
                else
                {
                    date.setHours(12);
                    date.setMinutes(00);
                    date.setSeconds(01);
                }
            }

            returnValue = totalWorkHours;
            if (hourOrMinute.equals("M"))
            {
                returnValue = totalWorkMin;
            }

            return returnValue;
        }
        catch (Exception ex)
        {
            return -1;
        }
    }

    private long GetDayVarianceOld (Date dateSource, Date dateDestination, List<Date> lstHolidays, String checkHolidays, String checkWeekend)
    {
        int noOfdaysToReduce = 0;
        try
        {
            if (dateSource.compareTo(dateDestination) > 0)
            {
                //Source date cannot be greater than destination date
                return -1;
            }

            if (checkHolidays.equals("Y"))
            {
                if (lstHolidays == null || lstHolidays.size() == 0)
                {
                    return -1;
                }
            }

            Date date = dateSource;

            while (date.compareTo(dateDestination) <= 0)
            {
                boolean isHoliday = false;
                boolean isWeekend = false;

                //Check for holiday
                if (checkHolidays.equals("Y"))
                {
                    for(Date dt: lstHolidays)
                    {
                        if (date.compareTo(dt) == 0)
                        {
                            isHoliday = true;
                        }
                    }
                }

                //check Weekend
                if (checkWeekend.equals("Y"))
                {
                    LocalDate lDate = convertToLocalDateViaInstant(date);
                    isWeekend = isWeekend(lDate);
                }

                if (isHoliday == true || isWeekend == true)
                {
                    noOfdaysToReduce++;
                }

                //date = new Date(date.getTime() + (1000 * 60 * 60 * 24));

                date = addDays(date, 1);
            }

            long diffInMillies = Math.abs(dateDestination.getTime() - dateSource.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            return (diff - noOfdaysToReduce);
        }
        catch (Exception ex)
        {
            return -1;
        }
    }

    public boolean isWeekend(final LocalDate ld)
    {
        DayOfWeek day = DayOfWeek.of(ld.get(ChronoField.DAY_OF_WEEK));
        return day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY;
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public List<Date> ValidateHolidayList (String[] arrHoliday, String projectdateFormat)
    {
        List<Date> dtList = new ArrayList<>();
        dtList = null;
        boolean isValid = true;
        try
        {
            if (arrHoliday != null && arrHoliday.length > 0)
            {
                dtList = new ArrayList<>();
                for (String strDate: arrHoliday)
                {
                    isValid = this.isDateValid(strDate, projectdateFormat);
                    if (isValid == false)
                    {
                        return null; //There are some items not valid. So completely return null data
                    }
                    else
                    {
                        dtList.add(this.ConvertToDate(strDate, projectdateFormat));
                    }
                }
            }
            return dtList;
        }
        catch (Exception ex)
        {
            return null;
        }
    }


    public String GetReceiveEstimationDates (String commentSection, String Keyword, String delimiter, String dateFormat)
    {
        try
        {
            int position = commentSection.indexOf(Keyword, 0);
            int istHashPosition = 0;
            int secondHashPosition = 0;
            String data = "";
            String dateValue = "";

            if (position >= 0)
            {
                istHashPosition = commentSection.indexOf(delimiter, position+1);
                if (istHashPosition > position)
                {
                    secondHashPosition = commentSection.indexOf(delimiter, istHashPosition+1);
                }
            }

            if (secondHashPosition > istHashPosition && secondHashPosition <= commentSection.length())
            {
                data = commentSection.substring(position, secondHashPosition+1);
            }

            if (!data.equals(""))
            {
                String [] strArr = data.split(delimiter);
                if (strArr.length>=3) //There should be 3 elements there otherwise we donot process
                {
                    //We are interested with the last element here which should be a date
                    dateValue = strArr[2].toString().trim();
                    if (this.isDateValid(dateValue, dateFormat) == false)
                    {
                        dateValue = "Error: Invalid date provided";
                    }
                }
            }

            return dateValue;
        }
        catch (Exception ex)
        {
            return "Error: " + ex.getMessage();
        }
    }

    public IssueActivityDate getADOWorkItemActivityDateOld (String issueKey, List<RevisionValue> revisionValues, String activityStatus, String sourceDateFormat)
    {
        IssueActivityDate returnValue = new IssueActivityDate();
        String strActivitydate = "";
        Date dtActivityDate = new Date();
        dtActivityDate = null;
        returnValue = null;

        List<Date> dtList = new ArrayList<>();
        try
        {
            for(RevisionValue revisionValue:revisionValues)
            {
                if(revisionValue!=null)
                {
                    if (revisionValue.getFields().getState().equals(activityStatus))
                    {
                        //String defaultTimezone = TimeZone.getDefault().getID();
                        strActivitydate = revisionValue.getFields().getCreatedDate();
                        dtActivityDate=null;
                        if (!strActivitydate.isEmpty())
                        {
                            dtActivityDate = this.ConvertStringToDateForZFormat(strActivitydate);
                        }

                        if (dtActivityDate != null)
                        {
                            dtList.add(dtActivityDate);
                        }
                    }
                }
            }

            //check if there are multiple accurance of the same activity then take the first occurance
            //by comparing the dates
            if (dtList != null && dtList.size() > 0)
            {
                returnValue = new IssueActivityDate();
                returnValue.setIssueKey(issueKey);
                returnValue.setRequestedStatus(activityStatus);

                if (dtList.size() == 1)
                {
                    returnValue.setRequestedDate(dtList.get(0));
                    returnValue.setOccurance(1);
                }
                else
                {
                    returnValue.setOccurance(dtList.size());
                    dtActivityDate = dtList.get(0);

                    for (Date dt : dtList)
                    {
                        if (dt.before(dtActivityDate))
                        {
                            dtActivityDate = dt;
                        }
                    }
                    returnValue.setRequestedDate(dtActivityDate);

                    //Take the next activity Date
                    int count = 0;
                    dtActivityDate = returnValue.getRequestedDate();
                    for (Date dt : dtList)
                    {
                        if (dt.after(dtActivityDate))
                        {
                            dtActivityDate = dt;
                            count++;
                            break;
                        }
                    }

                    returnValue.setSecondRequestedDate(dtActivityDate);
                }
            }
        }
        catch (Exception ex)
        {
            returnValue = null;
        }

        return returnValue;
    }

    public IssueActivityDate getADOWorkItemActivityDate (String issueKey, List<RevisionValue> revisionValues, String activityStatus, String sourceDateFormat)
    {
        IssueActivityDate returnValue = new IssueActivityDate();
        String strActivitydate = "";
        Date dtActivityDate = new Date();
        dtActivityDate = null;
        returnValue = null;

        List<Date> dtList = new ArrayList<>();
        List<String> dtStrList = new ArrayList<>();
        try
        {
            for(RevisionValue revisionValue:revisionValues)
            {
                if(revisionValue!=null)
                {
                    if (revisionValue.getFields().getState().equals(activityStatus))
                    {
                        //String defaultTimezone = TimeZone.getDefault().getID();
                        strActivitydate = revisionValue.getFields().getCreatedDate();
                        dtActivityDate=null;
                        if (!strActivitydate.isEmpty())
                        {
                            dtActivityDate = this.ConvertStringToDateForZFormat(strActivitydate);
                        }

                        if (dtActivityDate != null)
                        {
                            dtList.add(dtActivityDate);
                            dtStrList.add(strActivitydate);
                        }
                    }
                }
            }

            //check if there are multiple accurance of the same activity then take the first occurance
            //by comparing the dates
            Date thisDate = null;
            if (dtStrList != null && dtStrList.size() > 0)
            {
                returnValue = new IssueActivityDate();
                returnValue.setIssueKey(issueKey);
                returnValue.setRequestedStatus(activityStatus);

                if (dtList.size() == 1)
                {
                    thisDate = this.ConvertStringToDateForZFormat(dtStrList.get(0));
                    returnValue.setRequestedDate(thisDate);
                    returnValue.setRequestedDateString(dtStrList.get(0));
                    returnValue.setOccurance(1);

                }
                else
                {
                    returnValue.setOccurance(dtList.size());
                    String firstDate = dtStrList.get(0);
                    dtActivityDate = this.ConvertStringToDateForZFormat(dtStrList.get(0));

                    for (String dt : dtStrList)
                    {
                        thisDate = this.ConvertStringToDateForZFormat(dt);
                        if (thisDate.before(dtActivityDate))
                        {
                            dtActivityDate = this.ConvertStringToDateForZFormat(dt);
                            firstDate = dt;
                        }
                    }

                    returnValue.setRequestedDate(dtActivityDate);
                    returnValue.setRequestedDateString(firstDate);

                    //Take the next activity Date
                    int count = 0;
                    dtActivityDate = returnValue.getRequestedDate();
                    for (String dt : dtStrList)
                    {
                        thisDate = this.ConvertStringToDateForZFormat(dt);
                        if (thisDate.after(dtActivityDate))
                        {
                            dtActivityDate = this.ConvertStringToDateForZFormat(dt);
                            firstDate = dt;
                            count++;
                            break;
                        }
                    }

                    returnValue.setSecondRequestedDate(dtActivityDate);
                    returnValue.setRequestedDateString(firstDate);                }
            }
        }
        catch (Exception ex)
        {
            returnValue = null;
        }

        return returnValue;
    }

    public IssueActivityDate getIssueActivityDate (String issueKey, List<History> historyList, String activityStatus, String sourceDateFormat, String fieldValue)
    {
        IssueActivityDate returnValue = new IssueActivityDate();
        String strActivitydate = "";
        Date dtActivityDate = new Date();
        dtActivityDate = null;
        returnValue = null;

        List<Date> dtList = new ArrayList<>();
        List<String> dtStrList = new ArrayList<>();
        try
        {
            for(History history:historyList)
            {
                List<Item>itemList=history.getItems();
                for(Item item:itemList)
                {
                    String strFieldValue = item.getField();
                    if(item.getField().equals(fieldValue))
                    {
                        if (item.getToString().equals(activityStatus.trim()))
                        {
                            strActivitydate = history.getCreated();
                            dtActivityDate=null;
                            if (!strActivitydate.isEmpty())
                            {
                                //We will check this date validation later. Date should be validated here
                                if (this.isDateValid(strActivitydate, sourceDateFormat) == true)
                                {
                                    dtActivityDate = this.ConvertStringToDateForZFormat(strActivitydate);
                                }
                            }
                            if (dtActivityDate != null)
                            {
                                dtList.add(dtActivityDate);
                                dtStrList.add(strActivitydate);
                            }
                        }
                    }
                }
            }

            Date thisDate = null;
            if (dtStrList != null && dtStrList.size() > 0)
            {
                returnValue = new IssueActivityDate();
                returnValue.setIssueKey(issueKey);
                returnValue.setRequestedStatus(activityStatus);

                if (dtStrList.size() == 1)
                {
                    thisDate = this.ConvertStringToDateForZFormat(dtStrList.get(0));
                    returnValue.setRequestedDate(thisDate);
                    returnValue.setRequestedDateString(dtStrList.get(0));
                    returnValue.setOccurance(1);
                }
                else
                {
                    returnValue.setOccurance(dtList.size());
                    String firstDate = dtStrList.get(0);
                    dtActivityDate = this.ConvertStringToDateForZFormat(dtStrList.get(0));

                    for (String dt : dtStrList)
                    {
                        thisDate = this.ConvertStringToDateForZFormat(dt);

                        if (thisDate.before(dtActivityDate))
                        {
                            dtActivityDate = this.ConvertStringToDateForZFormat(dt);
                            firstDate = dt;
                        }
                    }

                    returnValue.setRequestedDate(dtActivityDate);
                    returnValue.setRequestedDateString(firstDate);

                    //Take the next activity Date
                    int count = 0;
                    dtActivityDate = returnValue.getRequestedDate();

                    for (String dt : dtStrList)
                    {
                        thisDate = this.ConvertStringToDateForZFormat(dt);
                        if (thisDate.after(dtActivityDate))
                        {
                            dtActivityDate = this.ConvertStringToDateForZFormat(dt);
                            firstDate = dt;
                            count++;
                            break;
                        }
                    }

                    returnValue.setSecondRequestedDate(dtActivityDate);
                    returnValue.setRequestedDateString(firstDate);
                }
            }
        }
        catch (Exception ex)
        {
            returnValue = null;
        }

        return returnValue;
    }

    public IssueActivityDate getIssueActivityDateOld (String issueKey, List<History> historyList, String activityStatus, String sourceDateFormat, String fieldValue)
    {
        IssueActivityDate returnValue = new IssueActivityDate();
        String strActivitydate = "";
        Date dtActivityDate = new Date();
        dtActivityDate = null;
        returnValue = null;

        List<Date> dtList = new ArrayList<>();
        List<String> dtStrList = new ArrayList<>();
        try
        {
            for(History history:historyList)
            {
                List<Item>itemList=history.getItems();
                for(Item item:itemList)
                {
                    String strFieldValue = item.getField();
                    if(item.getField().equals(fieldValue))
                    {
                        if (item.getToString().equals(activityStatus.trim()))
                        {
                            strActivitydate = history.getCreated();
                            dtActivityDate=null;
                            if (!strActivitydate.isEmpty())
                            {
                                //We will check this date validation later. Date should be validated here
                                if (this.isDateValid(strActivitydate, sourceDateFormat) == true)
                                {
                                    dtActivityDate = this.ConvertStringToDateForZFormat(strActivitydate);
                                }
                            }
                            if (dtActivityDate != null)
                            {
                                dtList.add(dtActivityDate);
                            }
                        }
                    }
                }
            }

            //check if there are multiple accurance of the same activity then take the first occurance
            //by comparing the dates
            if (dtList != null && dtList.size() > 0)
            {
                returnValue = new IssueActivityDate();
                returnValue.setIssueKey(issueKey);
                returnValue.setRequestedStatus(activityStatus);

                if (dtList.size() == 1)
                {
                    returnValue.setRequestedDate(dtList.get(0));
                    returnValue.setOccurance(1);
                }
                else
                {
                    returnValue.setOccurance(dtList.size());
                    dtActivityDate = dtList.get(0);

                    for (Date dt : dtList)
                    {
                        if (dt.before(dtActivityDate))
                        {
                            dtActivityDate = dt;
                        }
                    }
                    returnValue.setRequestedDate(dtActivityDate);

                    //Take the next activity Date
                    int count = 0;
                    dtActivityDate = returnValue.getRequestedDate();
                    for (Date dt : dtList)
                    {
                        if (dt.after(dtActivityDate))
                        {
                            dtActivityDate = dt;
                            count++;
                            break;
                        }
                    }

                    returnValue.setSecondRequestedDate(dtActivityDate);
                }
            }
        }
        catch (Exception ex)
        {
            returnValue = null;
        }

        return returnValue;
    }

    public AvgCycleJira PopulateAverageCycleObject (Issue issue, Date committedDate, Date closeedDate, String strtotalFixedVersions, String strreleasedFixedVersions, String multipleFixedVersion, List<Date> lstHolidays, String strCheckHolidays, String strCheckWeekend, String sourcedateFormat )
    {
        AvgCycleJira eligibleissue = new AvgCycleJira();

        try
        {
            eligibleissue.setKey(issue.getKey());
            eligibleissue.setType(issue.getFields().getIssuetype().getName());
            eligibleissue.setIssueStatus(issue.getFields().getStatus().getName());
            eligibleissue.setReleaseDate(ConvertDateToString(closeedDate, sourcedateFormat));
            eligibleissue.setCommitedDate(ConvertDateToString(committedDate, sourcedateFormat));
            eligibleissue.setAssociatedFixedVersions(strtotalFixedVersions);
            eligibleissue.setMatchedFixedVersions(strreleasedFixedVersions);
            eligibleissue.setMultipleFixedVersions(multipleFixedVersion);
            double processingDays = 0;
            processingDays = GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend);

            eligibleissue.setTotalDuration((double)processingDays);

            return eligibleissue;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public IssueActivityDate getADOWorkItemActivityDateSai (String issueKey, List<RevisionValue> revisionValues, String activityStatus, String sourceDateFormat)
    {
        IssueActivityDate returnValue = new IssueActivityDate();
        String strActivitydate = "";
        Date dtActivityDate = new Date();
        dtActivityDate = null;
        returnValue = null;

        List<Date> dtList = new ArrayList<>();
        try
        {
            for(RevisionValue revisionValue:revisionValues)
            {
                if(revisionValue!=null){
                    strActivitydate = revisionValue.getFields().getCreatedDate();
                    dtActivityDate=null;
                    if (!strActivitydate.isEmpty())
                    {
                        //We will check this date validation later. Date should be validated here
                        if (this.isDateValid(strActivitydate, sourceDateFormat) == true)
                        {
                            dtActivityDate = this.ConvertToDate(strActivitydate, sourceDateFormat);

                        }
                    }
                    if (dtActivityDate != null)
                    {
                        dtList.add(dtActivityDate);
                    }
                }
            }


            //check if there are multiple accurance of the same activity then take the first occurance
            //by comparing the dates
            if (dtList != null && dtList.size() > 0)
            {
                returnValue = new IssueActivityDate();
                returnValue.setIssueKey(issueKey);
                returnValue.setRequestedStatus(activityStatus);

                if (dtList.size() == 1)
                {
                    returnValue.setRequestedDate(dtList.get(0));
                    returnValue.setOccurance(1);
                }
                else
                {
                    returnValue.setOccurance(dtList.size());
                    dtActivityDate = dtList.get(0);

                    for (Date dt : dtList)
                    {
                        if (dt.before(dtActivityDate))
                        {
                            dtActivityDate = dt;
                        }
                    }
                    returnValue.setRequestedDate(dtActivityDate);

                    //Take the next activity Date
                    int count = 0;
                    dtActivityDate = returnValue.getRequestedDate();
                    for (Date dt : dtList)
                    {
                        if (dt.after(dtActivityDate))
                        {
                            dtActivityDate = dt;
                            count++;
                            break;
                        }
                    }

                    returnValue.setSecondRequestedDate(dtActivityDate);
                }
            }
        }
        catch (Exception ex)
        {
            returnValue = null;
        }

        return returnValue;
    }

    public WorkItemFields setupWorkItemFields (Map<String,Object> mapFields)
    {
        WorkItemFields fields = null;

        try
        {
            if (mapFields != null)
            {
                fields = new WorkItemFields();

                if (mapFields.get("System.AreaPath") != null)
                {
                    fields.setAreaPath(String.valueOf(mapFields.get("System.AreaPath")));
                }

                if (mapFields.get("System.TeamProject") != null)
                {
                    fields.setTeamProject(String.valueOf(mapFields.get("System.TeamProject")));
                }

                if (mapFields.get("System.IterationPath") != null)
                {
                    fields.setIterationPath(String.valueOf(mapFields.get("System.IterationPath")));
                }
                if (mapFields.get("Custom.RevisedDueDateInfluencedBy") != null)
                {
                    fields.setRevisedDueDateInfluencedBy(String.valueOf(mapFields.get("Custom.RevisedDueDateInfluencedBy")));
                }
                if (mapFields.get("Custom.RevisedDueDate") != null) {
                    String revisedDue = timeZone4hoursv1(String.valueOf(mapFields.get("Custom.RevisedDueDate")));
                    fields.setRevisedDueDate(revisedDue);
                }
                if (mapFields.get("System.WorkItemType") != null)
                {
                    fields.setWorkItemType(String.valueOf(mapFields.get("System.WorkItemType")));
                }
                if (mapFields.get("Custom.TotalTestCasesNotExecuted") != null)
                {
                    fields.setTestNotExecuted(String.valueOf(mapFields.get("Custom.TotalTestCasesNotExecuted")));
                }
                if (mapFields.get("Microsoft.VSTS.TCM.AutomationStatus") != null)
                {
                    fields.setAutomationStatus(String.valueOf(mapFields.get("Microsoft.VSTS.TCM.AutomationStatus")));
                }
                if (mapFields.get("Custom.TypesofTesting") != null)
                {
                    fields.setTypesofTesting(String.valueOf(mapFields.get("Custom.TypesofTesting")));
                }
                if (mapFields.get("System.State") != null)
                {
                    fields.setState(String.valueOf(mapFields.get("System.State")));
                }

                if (mapFields.get("System.Reason") != null)
                {
                    fields.setReason(String.valueOf(mapFields.get("System.Reason")));
                }

                if (mapFields.get("System.CreatedDate") != null)
                {
                    String createdDate = timeZone4hoursv2(String.valueOf(mapFields.get("System.CreatedDate")));
                    fields.setCreatedDate(createdDate);
                }

                if (mapFields.get("System.ChangedDate") != null)
                {
                    String changeDate = timeZone4hoursv2(String.valueOf(mapFields.get("System.ChangedDate")));
                    fields.setChangedDate(changeDate);
                }

                if (mapFields.get("Microsoft.VSTS.Common.ResolvedDate") != null)
                {
                    String resolveDate = timeZone4hoursv2(String.valueOf(mapFields.get("Microsoft.VSTS.Common.ResolvedDate")));
                    fields.setResolvedDate(resolveDate);
                }

                if (mapFields.get("Microsoft.VSTS.Common.ClosedDate") != null)
                {
                    String closedDate = timeZone4hoursv2(String.valueOf(mapFields.get("Microsoft.VSTS.Common.ClosedDate")));
                    fields.setClosedDate(closedDate);
                }

                if (mapFields.get("Microsoft.VSTS.Common.ResolvedReason") != null)
                {
                    fields.setResolvedReason(String.valueOf(mapFields.get("Microsoft.VSTS.Common.ResolvedReason")));
                }

                if (mapFields.get("Microsoft.VSTS.CodeReview.AcceptedDate") != null)
                {
                    String acceptedDate = timeZone4hoursv2(String.valueOf(mapFields.get("Microsoft.VSTS.CodeReview.AcceptedDate")));
                    fields.setAcceptedDate(acceptedDate);
                }

                if (mapFields.get("Microsoft.VSTS.Scheduling.DueDate") != null)
                {
                    String dueDate= timeZone4hoursv1(String.valueOf(mapFields.get("Microsoft.VSTS.Scheduling.DueDate")));
                    fields.setDueDate(dueDate);
                }

                if (mapFields.get("Microsoft.VSTS.Common.StateChangeDate") != null)
                {
                    String stateChangeDate = timeZone4hoursv2(String.valueOf(mapFields.get("Microsoft.VSTS.Common.StateChangeDate")));
                    fields.setStateChangeDate(stateChangeDate);
                }

                if (mapFields.get("System.CommentCount") != null)
                {
                    fields.setCommentCount((int)(mapFields.get("System.CommentCount")));
                }

                if (mapFields.get("System.Title") != null)
                {
                    fields.setTitle(String.valueOf(mapFields.get("System.Title")));
                }

                if (mapFields.get("System.BoardColumn") != null)
                {
                    fields.setBoardColumn(String.valueOf(mapFields.get("System.BoardColumn")));
                }

                if (mapFields.get("Microsoft.VSTS.Common.Severity") != null)
                {
                    fields.setSeverity(String.valueOf(mapFields.get("Microsoft.VSTS.Common.Severity")));
                }

                if (mapFields.get("Microsoft.VSTS.Common.Priority") != null)
                {
                    fields.setPriority(String.valueOf(mapFields.get("Microsoft.VSTS.Common.Priority")));
                }

                fields.setActualEffortinHours(0);
                if (mapFields.get("Custom.ActualEffortinHours") != null)
                {
                    try
                    {
                        fields.setActualEffortinHours(Double.parseDouble(String.valueOf(mapFields.get("Custom.ActualEffortinHours"))));
                    }
                    catch (Exception ex)
                    {
                    }
                }
                fields.setEffortinHoursRemaining(-1);
                if (mapFields.get("Custom.RemainingRequiredEffortinhours") != null)
                {
                    try
                    {
                        fields.setEffortinHoursRemaining(Double.parseDouble(String.valueOf(mapFields.get("Custom.RemainingRequiredEffortinhours"))));
                    }
                    catch (Exception ex)
                    {
                    }
                }

                fields.setOriginalEffort(0);
                if (mapFields.get("Microsoft.VSTS.Scheduling.OriginalEstimate") != null)
                {
                    try
                    {
                        fields.setOriginalEffort(Double.parseDouble(String.valueOf(mapFields.get("Microsoft.VSTS.Scheduling.OriginalEstimate"))));
                    }
                    catch (Exception ex)
                    {
                    }
                }

                if (mapFields.get("Custom.ServiceLevelType") != null)
                {
                    fields.setServiceLevelType(String.valueOf(mapFields.get("Custom.ServiceLevelType")));
                }

                if (mapFields.get("Custom.SecurityThreatCount ") != null)
                {
                    try
                    {
                        fields.setSecurityThreatCount(Integer.parseInt(String.valueOf(mapFields.get("Custom.SecurityThreatCount"))));
                    }
                    catch (Exception ex)
                    {
                    }
                }

                if (mapFields.get("Microsoft.VSTS.Scheduling.StoryPoints") != null)
                {
                    try
                    {
                        fields.setStoryPoints(Double.parseDouble(String.valueOf(mapFields.get("Microsoft.VSTS.Scheduling.StoryPoints"))));
                    }
                    catch (Exception ex)
                    {
                    }
                }

                if (mapFields.get("Custom.FixVersion") != null)
                {
                    fields.setFixedVersions(String.valueOf(mapFields.get("Custom.FixVersion")));
                }

                if (mapFields.get("System.Tags") != null)
                {
                    fields.setTags(String.valueOf(mapFields.get("System.Tags")));
                }
            }

            return fields;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public TestCaseReference setupTestCaseReferance (Map<String,Integer> mapFields)
    {
         TestCaseReference fields = null;
         try
         {
             if(mapFields != null)
             {
                 fields = new TestCaseReference();

                 if(mapFields.get("id") != null)
                 {
                     fields.setId(mapFields.get("id"));
                 }


             }

         }
         catch (Exception e)
        {
           System.out.println("Error");
        }
         return fields;
    }

    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }


}
