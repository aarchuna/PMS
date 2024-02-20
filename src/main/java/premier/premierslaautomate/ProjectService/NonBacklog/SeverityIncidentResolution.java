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
public class SeverityIncidentResolution implements Serializable
{
    //CommonVariables
    private IAdoDataService iAdoDataService = new AdoDataService();

    //Test for Arun
    int totalnumCount = 0;
    int totaldenoCount = 0;
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
    List<String[]> dataLines = new ArrayList<>();
    List<String[]> dataLinesDetailed = new ArrayList<>();

    public ProcessedData SeverityLvl1IncidentResolution(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues)
    {
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
        List<IssueDateVariance> elligibleIssues = new ArrayList<>();
        IssueDateVariance issueDateVariance = new IssueDateVariance();
        List<History> Historylist= new ArrayList<>();

        Date committedDate=new Date();
        Date closeedDate =new Date();
        Date workingDate = new Date();
        String strWorkingDate = "";
        String closeStatus = "";
        String sourcedateFormat = "";
        double actualValue = 0;
        String holidayList = "";
        int limit = 0;
        List<Date> lstHolidays = new ArrayList<>();

        String fromdateFromConfig = "";
        String todateFromConfig = "";
        String dateFormatFromConfig = "";
        Date dtFromDate;
        Date dtToDate;
        String strCheckHolidays = "";
        String strCheckWeekend = "";
        String strCheckCreatedDateInsteadHistory = "N";
        String strCommittedDate = "";
        String strClosedDate = "";
        String nextClosedStatus = "";
        String prevtClosedStatus = "";
        Date testDate = new Date();
        int severity1Limit = 0;
        int severity2Limit = 0;
        int severity3Limit = 0;

        int startingHourOftheDay = 0;
        int endingHourOftheDay = 0;

        String strStartingHourOftheDay = "";
        String strEndingHourOftheDay = "";

        //Method Specific Varriable Declaration Area - End
        try
        {
            //Region For Configuration Data Retrival and Data Validation - Start

            //Project related validation
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

            //Holidate List Data validation
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

            if(sla.getLimit().equalsIgnoreCase("tier0"))
            {
                int[] limitArray = project.getTier0();

                if ( limitArray.length > 3 && limitArray.length != 0)
                {
                    message = twoSpace + "Only Three limit can be mentioned for the tier0. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if ( limitArray.length >= 1 && limitArray.length != 0)
                {
                    severity1Limit = limitArray[0];
                }

                if ( limitArray.length >= 2 && limitArray.length != 0)
                {
                    severity2Limit = limitArray[1];
                }
                if (limitArray.length >= 3 && limitArray.length != 0)
                {
                    severity3Limit =limitArray[2];
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
                    severity1Limit = limitArray[0];
                }

                if ( limitArray.length >= 2 && limitArray.length != 0)
                {
                    severity2Limit = limitArray[1];
                }
                if (limitArray.length >= 3 && limitArray.length != 0)
                {
                    severity3Limit =limitArray[2];
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
                    severity1Limit = limitArray[0];
                }

                if ( limitArray.length >= 2 && limitArray.length != 0)
                {
                    severity2Limit = limitArray[1];
                }
                if (limitArray.length >= 3 && limitArray.length != 0)
                {
                    severity3Limit =limitArray[2];
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
                    severity1Limit = limitArray[0];
                }

                if ( limitArray.length >= 2 && limitArray.length != 0)
                {
                    severity2Limit = limitArray[1];
                }
                if (limitArray.length >= 3 && limitArray.length != 0)
                {
                    severity3Limit =limitArray[2];
                }
            }


            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());


            //Validation for From and to days
            fromdateFromConfig = sla.getFrom().replace("'", "");
            todateFromConfig = sla.getTo().replace("'", "");

            strCheckHolidays = sla.getInput2().replace("'", "");
            strCheckWeekend = sla.getInput3().replace("'", "");

            if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y"))
            {
                strCheckHolidays = "N";
            }

            if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y"))
            {
                strCheckWeekend = "N";
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

            if (sla.getSlakey().equals(SLAKey.SeverityLvl3IncidentResolution.toString()))
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
                            {"Key", "Type", "Final Issue Status", "Severity", "commitedDate", "closedDate",
                                    "Variance (In Days)", "Variance (In Hour)", "Variance (In Min)", "Eligible"});                }

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
                totaldenoCount= 0;
                totalnumCount= 0 ;
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

                                if (strCheckCommittedField.equals("N")) {
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
                                                        if (revisionValuedate.getFields().getState().equals(closedStatus)) {
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
                                    if (sla.getSlakey().equals(SLAKey.SeverityLvl1IncidentResolution.toString()) || sla.getSlakey().equals(SLAKey.SeverityLvl2IncidentResolution.toString()))
                                    {
                                        variance = util.GetWorkingHourOrMinVariance(committedDate, closeedDate, null, strCheckHolidays, strCheckWeekend, sourcedateFormat, 24, "M");
                                        issueDateVariance.setVarianceinMin(variance);
                                        variance = variance/60;
                                        issueDateVariance.setVarianceinHour(variance);
                                        variance = variance/24;
                                        issueDateVariance.setVariance(variance);
                                        if(sla.getSlakey().equals(SLAKey.SeverityLvl1IncidentResolution.toString()) ) {
                                            if (issueDateVariance.getVariance() <= severity1Limit) {
                                                issueDateVariance.setStatus("Met");
                                            } else {
                                                issueDateVariance.setStatus("Not Met");
                                            }

                                        }

                                        if(sla.getSlakey().equals(SLAKey.SeverityLvl2IncidentResolution.toString()) ) {
                                            if (issueDateVariance.getVariance() <= severity2Limit) {
                                                issueDateVariance.setStatus("Met");
                                            } else {
                                                issueDateVariance.setStatus("Not Met");
                                            }

                                        }

                                        elligibleIssues.add(issueDateVariance);
                                    }
                                    else if  (sla.getSlakey().equals(SLAKey.SeverityLvl3IncidentResolution.toString()))
                                    {
                                        variance = util.GetP3IncidnetWorkingHourOrMinutes(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, sourcedateFormat, startingHourOftheDay, endingHourOftheDay, "M" );
                                        issueDateVariance.setVarianceinMin(variance);
                                        variance = variance/60;
                                        issueDateVariance.setVarianceinHour(variance);
                                        int hourPerDay = endingHourOftheDay-startingHourOftheDay;
                                        variance = variance/hourPerDay;
                                        issueDateVariance.setVariance(variance);

                                        if (issueDateVariance.getVariance() <= severity3Limit)
                                        {
                                            issueDateVariance.setStatus("Met");
                                        }
                                        else
                                        {
                                            issueDateVariance.setStatus("Not Met");
                                        }

                                        elligibleIssues.add(issueDateVariance);
                                    }


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
                    totaldenoCount=elligibleIssues.size();
                    totalnumCount = (int)elligibleIssues.stream().filter(x->x.getStatus().equals("Met")).count();

                    //Data Line
                    for (IssueDateVariance iv:elligibleIssues)
                    {
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
                                            strVariance, varianceinHours, varianceinMins, iv.getStatus()});
                        }
                    }
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

                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
                message += newLine + twoSpace + " Ado Query = " + ADQuery;
                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;

                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                if (totaldenoCount == 0 && totalnumCount == 0)
                {
                    totaldenoCount = 0;
                    totalnumCount = 0;
                    actualValue =0;
                    message += newLine + twoSpace + " Actual = " + actualValue;
                    slaStatus = "NT";
                    message += newLine + twoSpace + " Status = " + slaStatus;
                }
                else {
                    actualValue = util.GetActualValueV1((double)totaldenoCount, (double)totalnumCount);
                    message += newLine + twoSpace + " Actual = " + actualValue;
                    slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double)expectedsla, (double)minsla);
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
