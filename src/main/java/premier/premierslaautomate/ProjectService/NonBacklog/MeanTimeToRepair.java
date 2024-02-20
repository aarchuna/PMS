package premier.premierslaautomate.ProjectService.NonBacklog;

import org.springframework.stereotype.Service;
import premier.premierslaautomate.DataServices.AdoDataService;
import premier.premierslaautomate.ENUM.SLAKey;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IAdoDataService;
import premier.premierslaautomate.Models.*;
import premier.premierslaautomate.Models.ADO.RevisionFields;
import premier.premierslaautomate.Models.ADO.RevisionValue;
import premier.premierslaautomate.Models.ADO.WorkItem;
import premier.premierslaautomate.Utilities.CommonUtil;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MeanTimeToRepair implements Serializable
{
    //CommonVariables
    private IAdoDataService iAdoDataService = new AdoDataService();

    //Test for Arun
    double expectedsla = 0;
    double minsla = 0;
    Float actual = 0.0f;
    String slaStatus = "";
    String message = "";
    String tabKey = "\t";
    String twoSpace = "  ";
    String newLine = "\r\n";
    boolean status = false;
    CommonUtil util = new CommonUtil();

    public ProcessedData MTTR(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();

        // ADO Variables
        List<WorkItem> workitems = new ArrayList<>();
        List<RevisionValue> revisionValues = new ArrayList<>();
        String ADQuery ="";
        double variance = 0f;

        //MEthod Specific Variables
        IssueActivityDate issueActivityDate = new IssueActivityDate();
        List<IssueDateVariance> elligibleIssues = new ArrayList<>();
        IssueDateVariance issueDateVariance = new IssueDateVariance();
        Date committedDate=new Date();
        Date closeedDate =new Date();
        Date workingDate = new Date();
        String closeStatus = "";
        String sourcedateFormat = "";
        double actualValue = 0;
        String holidayList = "";
        List<Date> lstHolidays = new ArrayList<>();
        String fromdateFromConfig = "";
        String todateFromConfig = "";
        String dateFormatFromConfig = "";
        String strCheckHolidays = "";
        String strCheckWeekend = "";
        String strCheckCreatedDateInsteadHistory = "N";
        String strCommittedDate = "";
        String strClosedDate = "";
        String prevtClosedStatus = "";

        int startingHourOftheDay = 0;
        int endingHourOftheDay = 0;

        String strStartingHourOftheDay = "";
        String strEndingHourOftheDay = "";

        double dblTotalDenoCount = 0;
        double dblTotalNumCount = 0;
        String comment = "";

        try
        {
            sourcedateFormat = project.getSourceDateFormat();
            if (sourcedateFormat.equals(""))
            {
                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            dateFormatFromConfig = project.getDateFormat().replace("'", "");
            if (dateFormatFromConfig.equals(""))
            {
                message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            holidayList = project.getHolidays();
            if (holidayList.equals(""))
            {
                message = twoSpace + "Holiday details not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            String[] arrHoliday = holidayList.split(",");
            lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);

            if (lstHolidays == null)
            {
                message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String committedStatus = sla.getConfig1();
            String closedStatus = sla.getConfig2();
            String strCheckCommittedField = sla.getConfig3();
            String strCheckClosedField  = sla.getConfig4();
            String includeCommittedDate = sla.getInput4();

            //ADO Related Validation , URL and Query Start
            if (committedStatus.isEmpty() && strCheckCommittedField.equals("N"))
            {
                message = twoSpace +  "Please provide committed status or Field name to check the committed date, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (closedStatus.isEmpty() && strCheckClosedField.equals("N"))
            {
                message = twoSpace +  "Please provide Closed status or Field name to check the closed date, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String [] arrClosedStatus = closedStatus.split(",");
            if (arrClosedStatus != null && arrClosedStatus.length > 2)
            {
                message = twoSpace + "Only Two status(s) can be mentioned for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (arrClosedStatus != null && arrClosedStatus.length >= 1)
            {
                closeStatus = arrClosedStatus[0];
            }

            if (arrClosedStatus != null && arrClosedStatus.length >= 2)
            {
                prevtClosedStatus = arrClosedStatus[1];
            }

            int P1Limit =0;
            int P2Limit =0;
            int P3Limit =0;

            if(sla.getLimit().equalsIgnoreCase("tier0"))
            {
                int[] limitArray = project.getTier0();

                if ( limitArray.length != 0 && limitArray.length > 3  )
                {
                    message = twoSpace + "Only Three limit can be mentioned for the tier0. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if ( limitArray.length >= 1 && limitArray.length != 0)
                {
                    P1Limit = limitArray[0];
                }

                if ( limitArray.length >= 2 && limitArray.length != 0)
                {
                    P2Limit = limitArray[1];
                }
                if (limitArray.length >= 3 && limitArray.length != 0)
                {
                    P3Limit =limitArray[2];
                }
            }
            if(sla.getLimit().equalsIgnoreCase("tier1"))
            {
                int[] limitArray = project.getTier1();

                if ( limitArray.length > 3 && limitArray.length != 0)
                {
                    message = twoSpace + "Only Three limit can be mentioned for the tier0. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if ( limitArray.length >= 1 && limitArray.length != 0)
                {
                    P1Limit = limitArray[0];
                }

                if ( limitArray.length >= 2 && limitArray.length != 0)
                {
                    P2Limit = limitArray[1];
                }
                if (limitArray.length >= 3 && limitArray.length != 0)
                {
                    P3Limit =limitArray[2];
                }
            }
            if(sla.getLimit().equalsIgnoreCase("tier2"))
            {
                int[] limitArray = project.getTier2();

                if ( limitArray.length > 3 && limitArray.length != 0)
                {
                    message = twoSpace + "Only Three limit can be mentioned for the tier0. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if ( limitArray.length >= 1 && limitArray.length != 0)
                {
                    P1Limit = limitArray[0];
                }

                if ( limitArray.length >= 2 && limitArray.length != 0)
                {
                    P2Limit = limitArray[1];
                }
                if (limitArray.length >= 3 && limitArray.length != 0)
                {
                    P3Limit =limitArray[2];
                }
            }
            if(sla.getLimit().equalsIgnoreCase("tier3"))
            {
                int[] limitArray = project.getTier3();

                if ( limitArray.length > 3 && limitArray.length != 0)
                {
                    message = twoSpace + "Only Three limit can be mentioned for the tier0. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if ( limitArray.length >= 1 && limitArray.length != 0)
                {
                    P1Limit= limitArray[0];
                }

                if ( limitArray.length >= 2 && limitArray.length != 0)
                {
                    P2Limit = limitArray[1];
                }
                if (limitArray.length >= 3 && limitArray.length != 0)
                {
                    P3Limit =limitArray[2];
                }
            }

            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            fromdateFromConfig = sla.getFrom().replace("'", "");
            todateFromConfig = sla.getTo().replace("'", "");

            strCheckHolidays = sla.getInput2().replace("'", "");
            strCheckWeekend = sla.getInput3().replace("'", "");

            String p1HolidayCheck = "";
            String p2HolidayCheck = "";
            String p3HolidayCheck = "";

            if (strCheckHolidays.isEmpty())
            {
                message = twoSpace + "Please configure the value(s) for Input2 (HolidayCheck). Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String [] arrHolidayCheck = strCheckHolidays.split(",");
            if (arrHolidayCheck != null && arrHolidayCheck.length != 3) {
                message = twoSpace + "Three status(s) must be mentioned for Input2 (HolidayCheck). Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (arrHolidayCheck != null && arrHolidayCheck.length == 3) {
                p1HolidayCheck = arrHolidayCheck[0];
                p2HolidayCheck = arrHolidayCheck[1];
                p3HolidayCheck = arrHolidayCheck[2];
            }

            String p1WeekendCheck = "";
            String p2WeekendCheck = "";
            String p3WeekendCheck = "";

            if (strCheckWeekend.isEmpty())
            {
                message = twoSpace + "Please configure the value(s) for Input3 (WeekendCheck). Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String [] arrWeekendCheck = strCheckWeekend.split(",");
            if (arrWeekendCheck != null && arrWeekendCheck.length != 3)
            {
                message = twoSpace + "Three status(s) must be mentioned for Input3 (WeekendCheck). Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (arrWeekendCheck != null && arrWeekendCheck.length == 3)
            {
                p1WeekendCheck = arrWeekendCheck[0];
                p2WeekendCheck = arrWeekendCheck[1];
                p3WeekendCheck = arrWeekendCheck[2];
            }

            if (includeCommittedDate.equals("") || !includeCommittedDate.equals("Y"))
            {
                includeCommittedDate = "N";
            }

            if (project.getProjecturl() == null || project.getProjecturl().isEmpty())
            {
                message = twoSpace + "Data Source URL is not available, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            strStartingHourOftheDay = sla.getInput1().replace("'", "");
            strEndingHourOftheDay = sla.getInput5().replace("'", "");

            if (sla.getSlakey().equals(SLAKey.MTTR.toString()))
            {
                if (strStartingHourOftheDay.isEmpty())
                {
                    message = twoSpace + "Starting Hour for the day not found, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (strEndingHourOftheDay.isEmpty())
                {
                    message = twoSpace + "Ending Hour for the day not found, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                try
                {
                    startingHourOftheDay = Integer.parseInt(strStartingHourOftheDay);
                }
                catch (Exception exParse)
                {
                    message = twoSpace + "Unable to parse Starting Hour for the day value, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                try
                {
                    endingHourOftheDay = Integer.parseInt(strEndingHourOftheDay);
                }
                catch (Exception exParse)
                {
                    message = twoSpace + "Unable to parse Ending Hour for the day value, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
            }



            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (project.getDatafileRequired().equals("Y"))
                {
                    dataLines.add(new String[]
                            {"Key", "Type", "Final Issue Status", "Priority", "commitedDate", "closedDate",
                                    "Limit", "Variance (In Days)", "Variance (In Hour)", "Variance (In Min)", "Comment"});
                }

                if (project.getDetailedLogRequired().equals("Y"))
                {
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type", "Severity", "Final Issue Status", "commitedDate", "closedDate" });
                }

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                workitems  = iAdoDataService.getWorkitems(userName,password,project.getProjecturl(),ADQuery,"POST",false,true, true,100);

                if(workitems !=null && workitems.size()>0)
                {
                    for(WorkItem witem : workitems){
                        committedDate = null;
                        closeedDate = null;
                        workingDate = null;

                        if (witem != null)
                        {
                            try
                            {
                                if (witem.getRevisions() != null) {
                                    revisionValues = witem.getRevisions();
                                }

                                if (strCheckCommittedField.equals("N"))
                                {
                                    if (revisionValues != null & revisionValues.size() > 0) {
                                        IssueActivityDate issueActivityDates = util.getADOWorkItemActivityDate(witem.getId(), revisionValues, committedStatus, sourcedateFormat);
                                        if (issueActivityDates != null)
                                        {
                                            if (issueActivityDates.getRequestedDate() != null)
                                            {
                                                committedDate = issueActivityDates.getRequestedDate();
                                                strCommittedDate = issueActivityDates.getRequestedDateString();
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    if (strCheckCommittedField.equals("Created"))
                                    {
                                        strCommittedDate = witem.getFields().getCreatedDate();
                                    }
                                    else if (strCheckCommittedField.equals("Updated"))
                                    {
                                        strCommittedDate= witem.getFields().getChangedDate();
                                    }

                                    if (!strCommittedDate.isEmpty())
                                    {
                                        committedDate = util.ConvertStringToDateForZFormat(strCommittedDate);
                                    }
                                }

                                if (strCheckClosedField.equals("N")) {
                                    //Check the closed date by looking to the configuration from the histroy
                                    revisionValues = witem.getRevisions();
                                    boolean firstOccuranceClosedDate = false;

                                    if (revisionValues != null & revisionValues.size() > 0) {
                                        for (RevisionValue revisionValuedate : revisionValues)
                                        {
                                            if (revisionValuedate != null)
                                            {
                                                if (!closedStatus.isEmpty())
                                                {
                                                    if (firstOccuranceClosedDate == false) {
                                                        //Check and compare the status with the record to get a match
                                                        if (revisionValuedate.getFields().getState().equals(closeStatus)) {
                                                            firstOccuranceClosedDate = true;
                                                            strClosedDate = revisionValuedate.getFields().getStateChangeDate();
                                                            break;
                                                        }
                                                    }
                                                }

                                                if (!prevtClosedStatus.isEmpty())
                                                {
                                                    if (firstOccuranceClosedDate == false) {
                                                        //Check and compare the status with the record to get a match
                                                        if (revisionValuedate.getFields().getState().equals(prevtClosedStatus))
                                                        {
                                                            firstOccuranceClosedDate = true;
                                                            strClosedDate = revisionValuedate.getFields().getStateChangeDate();
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!strClosedDate.isEmpty()) {
                                        closeedDate = util.ConvertStringToDateForZFormat(strClosedDate);
                                    }
                                }
                                else
                                {
                                    if (strCheckClosedField.equals("Closed")) {
                                        strClosedDate = witem.getFields().getClosedDate();
                                    } else if (strCheckClosedField.equals("Resolved")) {
                                        strClosedDate = witem.getFields().getResolvedDate();
                                    }

                                    if (!strClosedDate.isEmpty()) {
                                        closeedDate = util.ConvertStringToDateForZFormat (strClosedDate);
                                    }
                                }

                                variance = 0;
                                if (committedDate != null && closeedDate != null)
                                {
                                    issueDateVariance = new IssueDateVariance();
                                    issueDateVariance.setKey(witem.getId());
                                    issueDateVariance.setType(witem.getFields().getWorkItemType());
                                    issueDateVariance.setIssueStatus(witem.getFields().getState());
                                    issueDateVariance.setPriority(witem.getFields().getSeverity());
                                    issueDateVariance.setVariance(0.0);
                                    issueDateVariance.setCommitedDate(committedDate);
                                    issueDateVariance.setClosedDate(closeedDate);
                                    issueDateVariance.setCommitedDateString(strCommittedDate);
                                    issueDateVariance.setClosedDateString(strClosedDate);

                                    variance = 0;
                                    comment = "";
                                    if (issueDateVariance.getPriority().equals("1 - Critical"))
                                    {
                                        variance = util.GetWorkingHourOrMinVariance(committedDate, closeedDate, null, p1HolidayCheck, p1WeekendCheck, sourcedateFormat, 24, "M");
                                        if (variance <0)
                                        {
                                            variance = 0;
                                            comment = "Bad Data";
                                        }
                                        issueDateVariance.setVarianceinMin(variance);
                                        variance = variance/60;
                                        issueDateVariance.setVarianceinHour(variance);
                                        variance = variance/24;
                                        if (comment.equals("Bad Data"))
                                        {
                                            issueDateVariance.setLimit(0);
                                        }
                                        else
                                        {
                                            issueDateVariance.setLimit(P1Limit);
                                        }
                                        //issueDateVariance.setLimit(P1Limit);
                                    }
                                    else if (issueDateVariance.getPriority().equals("2 - High"))
                                    {
                                        variance = util.GetWorkingHourOrMinVariance(committedDate, closeedDate, null, p2HolidayCheck, p2WeekendCheck, sourcedateFormat, 24, "M");
                                        if (variance <0)
                                        {
                                            variance = 0;
                                            comment = "Bad Data";
                                        }
                                        issueDateVariance.setVarianceinMin(variance);
                                        variance = variance/60;
                                        issueDateVariance.setVarianceinHour(variance);
                                        variance = variance/24;
                                        if (comment.equals("Bad Data"))
                                        {
                                            issueDateVariance.setLimit(0);
                                        }
                                        else
                                        {
                                            issueDateVariance.setLimit(P2Limit);
                                        }
                                        //issueDateVariance.setLimit(P2Limit);
                                    }
                                    else if  (issueDateVariance.getPriority().equals("3 - Medium"))
                                    {
                                        variance = util.GetP3IncidnetWorkingHourOrMinutes(committedDate, closeedDate, lstHolidays, p3HolidayCheck, p3WeekendCheck, sourcedateFormat, startingHourOftheDay, endingHourOftheDay, "M" );
                                        if (variance <0)
                                        {
                                            variance = 0;
                                            comment = "Bad Data";
                                        }
                                        issueDateVariance.setVarianceinMin(variance);
                                        variance = variance/60;
                                        issueDateVariance.setVarianceinHour(variance);
                                        int hourPerDay = endingHourOftheDay-startingHourOftheDay;
                                        variance = variance/hourPerDay;
                                        if (comment.equals("Bad Data"))
                                        {
                                            issueDateVariance.setLimit(0);
                                        }
                                        else
                                        {
                                            issueDateVariance.setLimit(P3Limit);
                                        }

                                        //issueDateVariance.setLimit(P3Limit);
                                    }
                                    issueDateVariance.setComments(comment);
                                    issueDateVariance.setVariance(variance);
                                    elligibleIssues.add(issueDateVariance);
                                }

                                if (project.getDetailedLogRequired().equals("Y"))
                                {
                                    String tType = "";
                                    String tStatus = "";
                                    String tSeverity = "";

                                    if (witem.getFields().getWorkItemType() != null)
                                    {
                                        tType = witem.getFields().getWorkItemType();
                                    }

                                    if (witem.getFields().getSeverity() != null)
                                    {
                                        tSeverity = witem.getFields().getSeverity();
                                    }

                                    if (witem.getFields().getState() != null)
                                    {
                                        tStatus = witem.getFields().getState();
                                    }

                                    dataLinesDetailed.add(new String[]
                                            {witem.getId(), tType, tSeverity, tStatus, strCommittedDate, strClosedDate });
                                }
                            }
                            catch (Exception exception)
                            {

                            }
                        }
                    }
                }

                if (elligibleIssues != null && elligibleIssues.size() > 0)
                {
                    int limitSum = 0;
                    int VarianceSum = 0;

                    for (IssueDateVariance iv:elligibleIssues)
                    {
                        VarianceSum += iv.getVariance();
                        limitSum += iv.getLimit();

                        if (project.getDatafileRequired().equals("Y"))
                        {
                            String varianceinHours = "";
                            String varianceinMins = "";
                            String strVariance = "";

                            if (iv.getCommitedDate() != null && iv.getClosedDate() != null)
                            {
                                strVariance = String.valueOf(iv.getVariance());
                            }

                            if (iv.getVarianceinHour() != null)
                            {
                                varianceinHours = String.valueOf(iv.getVarianceinHour());
                            }
                            if (iv.getVarianceinMin() != null)
                            {
                                varianceinMins = String.valueOf(iv.getVarianceinMin());
                            }

                            String type = "";
                            String status = "";
                            String priority = "";

                            if (iv.getType() != null)
                            {
                                type = iv.getType();
                            }

                            if (iv.getIssueStatus() != null)
                            {
                                status = iv.getIssueStatus();
                            }

                            if (iv.getPriority() != null)
                            {
                                priority = iv.getPriority();
                            }

                            dataLines.add(new String[]
                                    {iv.getKey(), type, status, priority, iv.getCommitedDateString(), iv.getClosedDateString(),
                                            String.valueOf(iv.getLimit()), strVariance, varianceinHours, varianceinMins, iv.getComments()});
                        }
                    }

                    dblTotalNumCount = elligibleIssues.stream().filter(o -> o.getVariance() > 0).mapToDouble(o -> o.getVarianceinMin()).sum();
                    dblTotalDenoCount = elligibleIssues.stream().filter(o -> o.getLimit() > 0).mapToDouble(o -> o.getLimit()).sum();
                    dblTotalDenoCount = dblTotalDenoCount * 24*60;

                    //                    totalnumCount = VarianceSum;
//                    totaldenoCount = limitSum;
                }

                //Preparing the data for
                if (dataLines.size()> 0)
                {
                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
                    try
                    {
                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
                        if (csvStatus == true)
                        {
                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
                        }
                        else
                        {
                            message += newLine + twoSpace + "Unable to create the data file";
                        }
                    }
                    catch (Exception exCsv)
                    {
                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
                    }
                }

                if (dataLinesDetailed.size()> 0)
                {
                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
                    try
                    {
                        boolean csvStatus = util.WriteToCSv(dataLinesDetailed, dataFileName1);
                        if (csvStatus == true)
                        {
                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + dataFileName1;
                        }
                        else
                        {
                            message += newLine + twoSpace + "Unable to create the data file";
                        }
                    }
                    catch (Exception exCsv)
                    {
                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
                    }
                }

                message = twoSpace + " Total Denominator Count = " + dblTotalDenoCount;
                message += newLine + twoSpace + " Total Numerator Count = " + dblTotalNumCount;
                message += newLine + twoSpace + " AdoQuery = " + ADQuery;
                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;

                double denoCountActual = dblTotalDenoCount; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = dblTotalNumCount;  //This is to hold the old value of num Count so that it can be send back

                if (dblTotalDenoCount == 0 && dblTotalNumCount == 0)
                {
                    dblTotalDenoCount = 0;
                    dblTotalNumCount = 0;
                    actualValue =0;
                    message += newLine + twoSpace + " Actual = " + actualValue;
                    slaStatus = "NT";
                    message += newLine + twoSpace + " Status = " + slaStatus;
                }
                else {
                    actualValue = util.GetActualValueV1((double)dblTotalDenoCount, (double)dblTotalNumCount);
                    message += newLine + twoSpace + " Actual = " + actualValue;
                    slaStatus = util.CalculateFinalSLAValueV2(actualValue, (double)expectedsla, (double)minsla);
                    message += newLine + twoSpace + " Status = " + slaStatus;
                }

                ProcessedData data = util.BuildProcessData(sla, (float)actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),ADQuery);
                boolean isStatus = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            return null;
        }
        catch (Exception ex)
        {
            message = twoSpace +  "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;
    }

}
