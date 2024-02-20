package premier.premierslaautomate.ProjectService.NonBacklog;

import org.springframework.stereotype.Service;
import premier.premierslaautomate.DataServices.AdoDataService;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IAdoDataService;
import premier.premierslaautomate.Models.*;
import premier.premierslaautomate.Models.ADO.RevisionFields;
import premier.premierslaautomate.Models.ADO.RevisionValue;
import premier.premierslaautomate.Models.ADO.WorkItem;
import premier.premierslaautomate.Utilities.CommonUtil;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProblemResolutionTime
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

    public ProcessedData ProblemResolutionTime(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<Issue> denoIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        IssueDateVariance issueDateVariance = new IssueDateVariance();
        List<Item> ItemList = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();
        List<IssueDateVariance> elligibleIssues = new ArrayList<>();
        Date committedDate = new Date();//committeddate
        Date closeDate = new Date();
        double actualValue = 0;
        String inDevelopmentStatus = "";
        String sourcedateFormat = "";
        closeDate = null;
        String holidayList = "";
        String projectdateFormat = "";
        int limitFromConfig = 0;
        List<Date> lstHolidays = new ArrayList<>();
        String strCheckHolidays = "";
        String strCheckWeekend = "";
        int pageSize = 1000;
        String strPageSize = "";
        List<IssueDateVariance> eligibleissue = new ArrayList<>();
        String strCheckCommittedDateInField = sla.getConfig5();
        String strCheckClosedDateInField = sla.getConfig4();
        String strClosedDate = "";
        String strcommitedDate = "";
        String dateFormatFromConfig = "";

        // ADO Variables
        List<WorkItem> workitems = new ArrayList<>();
        List<RevisionValue> revisionValues = new ArrayList<>();
        String ADQuery ="";
        double variance = 0f;
        int Limitvalue = 0;
        Date closeedDate =new Date();
        Date workingDate = new Date();
        String strCommittedDate = "";
        String committedStatus = sla.getConfig1();

        try
        {
            sourcedateFormat = project.getSourceDateFormat();
            if (sourcedateFormat.equals("")) {
                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            dateFormatFromConfig = project.getDateFormat();
            if (dateFormatFromConfig.equals("")) {
                message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            //Holidate List Data validation
            holidayList = project.getHolidays();
            if (holidayList.equals("")) {
                message = twoSpace + "Holiday details not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            String[] arrHoliday = holidayList.split(",");
            lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);

            if (lstHolidays == null || lstHolidays.size() == 0) {
                message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if(sla.getLimit().equalsIgnoreCase("tier0"))
            {
                int[] limitArray = project.getProblemRT();

                if ( limitArray.length > 4 && limitArray.length != 0)
                {
                    message = twoSpace + "Only four limit can be mentioned for the tier0. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if ( limitArray.length >= 1 && limitArray.length != 0)
                {
                    Limitvalue = limitArray[0];
                }

            }
            if(sla.getLimit().equalsIgnoreCase("tier1"))
            {
                int[] limitArray = project.getProblemRT();

                if ( limitArray.length > 4 && limitArray.length != 0)
                {
                    message = twoSpace + "Only four limit can be mentioned for the tier0. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }


                if ( limitArray.length >= 2 && limitArray.length != 0)
                {
                    Limitvalue = limitArray[1];
                }

            }
            if(sla.getLimit().equalsIgnoreCase("tier2"))
            {
                int[] limitArray = project.getProblemRT();

                if ( limitArray.length > 4 && limitArray.length != 0)
                {
                    message = twoSpace + "Only Three limit can be mentioned for the tier0. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if ( limitArray.length >= 2 && limitArray.length != 0)
                {
                    Limitvalue = limitArray[2];
                }

            }
            if(sla.getLimit().equalsIgnoreCase("tier3"))
            {
                int[] limitArray = project.getProblemRT();

                if ( limitArray.length > 4 && limitArray.length != 0)
                {
                    message = twoSpace + "Only Three limit can be mentioned for the tier0. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (limitArray.length >= 3 && limitArray.length != 0)
                {
                    Limitvalue =limitArray[3];
                }
            }

            strPageSize = project.getPageSize();
            if (!strPageSize.isEmpty()) {
                try {
                    pageSize = Integer.parseInt(strPageSize);
                } catch (Exception exPageSizeParse) {
                    pageSize = 1000;
                }
            }

            strCheckCommittedDateInField = sla.getConfig3();
            strCheckClosedDateInField = sla.getConfig4();

            if (strCheckCommittedDateInField.isEmpty()) {
                strCheckCommittedDateInField = "N";
            }

            if (strCheckClosedDateInField.isEmpty()) {
                strCheckClosedDateInField = "N";
            }

            strCheckHolidays = sla.getInput2();
            if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
                strCheckHolidays = "N";
            }

            strCheckWeekend = sla.getInput3();
            if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
                strCheckWeekend = "N";
            }

            String statusforcommited = sla.getConfig1().replace("'", "");
            String statusForClose = sla.getConfig2().replace("'", "");
            String includeCommittedDate = sla.getInput4();

            if (statusforcommited.equals("")) {
                message = twoSpace + "Status for commited Check for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (statusForClose.equals("")) {
                message = twoSpace + "Closed status (Development/Testing) for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String closedStatus = "";
            String prevClosedStatus = "";

            String[] arrstatusForcommited = statusforcommited.split(",");
            if (arrstatusForcommited == null && arrstatusForcommited.length == 0) {
                message = twoSpace + "Status(s) to check FTR not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String[] arrClosedStatus = statusForClose.split(",");
            if (arrClosedStatus != null && arrClosedStatus.length > 2) {
                message = twoSpace + "Only Two status(s) can be mentioned for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (arrClosedStatus != null && arrClosedStatus.length >= 1) {
                closedStatus = arrClosedStatus[0];
            }

            if (arrClosedStatus != null && arrClosedStatus.length >= 2) {
                prevClosedStatus = arrClosedStatus[1];
            }

            if (closedStatus.isEmpty()) {
                message = twoSpace + "Closed Status for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (includeCommittedDate.equals("") || !includeCommittedDate.equals("Y"))
            {
                includeCommittedDate = "N";
            }

            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (project.getDatafileRequired().equals("Y")) {
                dataLines.add(new String[]
                        {"Key", "Type", "Status", "closed Date", "commited Date", "Variance (In Days)", "Record Status"});
            }

            if (project.getDetailedLogRequired().equals("Y")) {
                dataLinesDetailed.add(new String[]
                        {"Key", "Type", "Final Issue Status", "commitedDate", "closedDate" });
            }

            if (project.getProjecturl() == null || project.getProjecturl().isEmpty()) {
                message = twoSpace + "Data Source URL is not available, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }


            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                if (sla.getDenojql() == null || sla.getDenojql().isEmpty()) {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                ////////// Business Logic Implementation Starts  //////////

                workitems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", true, true, true, pageSize);
                totaldenoCount =0;
                totalnumCount =0;
                if (workitems != null && workitems.size() > 0) {
                    for (WorkItem witem : workitems) {
                        committedDate = null;
                        closeedDate = null;
                        workingDate = null;

                        if (witem != null) {
                            try {

                                if (witem.getRevisions() != null) {
                                    revisionValues = witem.getRevisions();
                                }
                                if (strCheckCommittedDateInField.equals("N")) {
                                    if (revisionValues != null & revisionValues.size() > 0) {
                                        IssueActivityDate issueActivityDates = util.getADOWorkItemActivityDate(witem.getId(), revisionValues, committedStatus, sourcedateFormat);
                                        if (issueActivityDates != null) {
                                            if (issueActivityDates.getRequestedDate() != null) {
                                                committedDate = issueActivityDates.getRequestedDate();
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    if (strCheckCommittedDateInField.equals("Created"))
                                    {
                                        strCommittedDate = witem.getFields().getCreatedDate();
                                    } else if (strCheckCommittedDateInField.equals("Updated")) {
                                        strCommittedDate = witem.getFields().getChangedDate();
                                    }

                                    if (!strCommittedDate.isEmpty()) {
                                        if (util.isDateValid(strCommittedDate, sourcedateFormat) == true) {
                                            committedDate = util.ConvertToDate(strCommittedDate, sourcedateFormat);
                                        }
                                    }
                                }

                                if (strCheckClosedDateInField.equals("N")) {
                                    //Check the closed date by looking to the configuration from the histroy
                                    revisionValues = witem.getRevisions();
                                    boolean firstOccuranceClosedDate = false;

                                    if (revisionValues != null & revisionValues.size() > 0) {
                                        for (RevisionValue revisionValuedate : revisionValues)
                                        {
                                            if (revisionValuedate != null) {
                                                if (!closedStatus.isEmpty()) {
                                                    if (firstOccuranceClosedDate == false) {
                                                        //Check and compare the status with the record to get a match
                                                        if (revisionValuedate.getFields().getState().equals(closedStatus)) {
                                                            firstOccuranceClosedDate = true;
                                                            strClosedDate = revisionValuedate.getFields().getStateChangeDate();
                                                            break;
                                                        }
                                                    }
                                                }

                                                if (!prevClosedStatus.isEmpty()) {
                                                    if (firstOccuranceClosedDate == false) {
                                                        //Check and compare the status with the record to get a match
                                                        if (revisionValuedate.getFields().getState().equals(prevClosedStatus)) {
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
                                        if (util.isDateValid(strClosedDate, sourcedateFormat) == true) {
                                            closeedDate = util.ConvertToDate(strClosedDate, sourcedateFormat);
                                        }
                                    }
                                }
                                else
                                {
                                    if (strCheckClosedDateInField.equals("Closed")) {
                                        strClosedDate = witem.getFields().getClosedDate();
                                    } else if (strCheckClosedDateInField.equals("Resolved")) {
                                        strClosedDate = witem.getFields().getResolvedDate();
                                    }
                                    if (!strClosedDate.isEmpty()) {
                                        closeedDate = util.ConvertToDate(strClosedDate, sourcedateFormat);
                                    }
                                }

                                issueDateVariance = new IssueDateVariance();
                                issueDateVariance.setKey(witem.getId());
                                issueDateVariance.setType(witem.getFields().getWorkItemType());
                                issueDateVariance.setIssueStatus(witem.getFields().getState());
                                issueDateVariance.setCommitedDate(committedDate);
                                issueDateVariance.setPriority(witem.getFields().getSeverity());
                                issueDateVariance.setClosedDate(closeedDate);
                                issueDateVariance.setVariance(0.0);

                                if (committedDate != null && closeedDate != null)
                                {
                                    variance = util.GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, includeCommittedDate);
                                    issueDateVariance.setVariance(variance);

                                    if(variance<=Limitvalue)
                                    {
                                        issueDateVariance.setStatus("Met");
                                    }
                                    else {
                                        issueDateVariance.setStatus("Not Met");
                                    }
                                    elligibleIssues.add(issueDateVariance);
                                }

                                if (project.getDetailedLogRequired().equals("Y"))
                                {
                                    strcommitedDate = "";
                                    strClosedDate = "";

                                    if (committedDate != null)
                                    {
                                        strcommitedDate = util.ConvertDateToString(committedDate, sourcedateFormat);
                                    }

                                    if (closeedDate != null)
                                    {
                                        strClosedDate = util.ConvertDateToString(closeedDate, sourcedateFormat);
                                    }

                                    dataLinesDetailed.add(new String[]
                                            {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getState(), strcommitedDate, strClosedDate });
                                }
                            }
                            catch (Exception exception)
                            {

                            }
                        }
                    }
                }

                if (elligibleIssues != null && elligibleIssues.size() > 0) {
                    totaldenoCount = elligibleIssues.size();
                    totalnumCount = (int) elligibleIssues.stream().filter(x -> x.getStatus().equals("Met")).count();

                    //Data Line
                    for (IssueDateVariance iv : elligibleIssues) {
                        strCommittedDate = "";
                        strClosedDate = "";
                        String strVariance = "";

                        if (iv.getCommitedDate() != null) {
                            strCommittedDate = util.ConvertDateToString(iv.getCommitedDate(), sourcedateFormat);
                        }

                        if (iv.getClosedDate() != null) {
                            strClosedDate = util.ConvertDateToString(iv.getClosedDate(), sourcedateFormat);
                        }

                        if (iv.getCommitedDate() != null && iv.getClosedDate() != null) {
                            strVariance = String.valueOf(iv.getVariance());
                        }

                        if (project.getDatafileRequired().equals("Y")) {
                            dataLines.add(new String[]
                                    {iv.getKey(), iv.getType(), iv.getIssueStatus(), strCommittedDate, strClosedDate, strVariance, iv.getStatus()});
                        }
                    }
                }

                //Preparing the data for
                if (dataLines.size() > 0) {
                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
                    try {
                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
                        if (csvStatus == true) {
                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
                        } else {
                            message += newLine + twoSpace + "Unable to create the data file";
                        }
                    } catch (Exception exCsv) {
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
                message += newLine + twoSpace + " AdoQuery = " + ADQuery;
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

                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),ADQuery);
                boolean isStatus = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
