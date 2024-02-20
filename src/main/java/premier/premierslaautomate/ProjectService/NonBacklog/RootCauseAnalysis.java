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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class RootCauseAnalysis implements Serializable
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

    public ProcessedData ProblemRCATime(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<Issue> denoIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();
        IssueDateVariance issueDateVariance = new IssueDateVariance();
        Date committedDate = new Date();//committeddate
        Date closeDate = new Date();
        double actualValue = 0;
        String sourcedateFormat = "";
        String holidayList = "";
        List<Date> lstHolidays = new ArrayList<>();
        String strCheckHolidays = "";
        String strCheckWeekend = "";
        int pageSize = 1000;
        String strPageSize = "";
        Date closedIssueDate = new Date();
        String strClosedDate = "";
        String strcommitedDate = "";
        String ADQuery ="";
        String dateFormatFromConfig = "";

        Date closeedDate =new Date();
        Date workingDate = new Date();

        String strCheckCommittedDateInField = "";
        String strCheckClosedDateInField = "";

        // ADO Variables
        List<WorkItem> workitems = new ArrayList<>();
        List<RevisionValue> revisionValues = new ArrayList<>();
        List<IssueDateVariance> elligibleIssues = new ArrayList<>();
        double variance = 0f;
        String strCommittedDate = "";

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

            //if (lstHolidays == null)
            if (lstHolidays == null || lstHolidays.size() == 0) {
                message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            int P1Limit =0;
            int P2Limit =0;
            int P3Limit =0;

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

            String[] arrClosedStatus = statusForClose.split(",");
            if (arrClosedStatus != null && arrClosedStatus.length > 2 && strCheckClosedDateInField.equals("N")) {
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

            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            String baseURI = project.getProjecturl() + "/api/latest/search?";
            
            if (project.getProjectsource().equals(SourceKey.ADO.value)) {

                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type", "Severity", "Final Issue Status", "commitedDate", "closedDate",
                                    "Variance (In Days)", "Eligible"});
                }

                if (project.getDetailedLogRequired().equals("Y"))
                {
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type", "Severity", "Final Issue Status", "commitedDate", "closedDate" });
                }

                sourcedateFormat = project.getSourceDateFormat();
                if (sourcedateFormat.equals("")) {
                    message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                expectedsla = Integer.parseInt(sla.getExpectedsla());
                minsla = Integer.parseInt(sla.getMinimumsla());

                if (expectedsla == 0 || minsla == 0) {
                    //Stop the processing
                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                strCheckHolidays = sla.getInput2();
                if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
                    strCheckHolidays = "N";
                }

                strCheckWeekend = sla.getInput3();
                if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
                    strCheckWeekend = "N";
                }

                String includeCommittedDate1 = sla.getInput4();
                if (includeCommittedDate1.equals("") || !includeCommittedDate1.equals("Y")) {
                    strCheckWeekend = "N";
                }

                ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";
                String searchWorkitemLinkURI = project.getLinkItemUrl();
                String searchItemURI = project.getItemUrl();

                ////////// Business Logic Implementation Starts  //////////
                String adoClosedStatus = "";
                String adoPrevClosedStatus = "";
                String committedStatus = sla.getConfig1();
                //String closedStatus = sla.getConfig2();
                String strCheckCommittedField = sla.getConfig3();
                String strCheckClosedField  = sla.getConfig4();
                String includeCommittedDate = sla.getInput4();
                totaldenoCount=0;
                totalnumCount=0;

                String[] closedStatusArr = closedStatus.split(",");
                if (closedStatusArr != null && closedStatusArr.length > 0)
                {
                    if (closedStatusArr.length >=1)
                    {
                        adoClosedStatus = closedStatusArr[0];
                    }
                    if (closedStatusArr.length >=2)
                    {
                        adoPrevClosedStatus = closedStatusArr[1];
                    }
                }

                workitems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", false, true, true, true, "'User Story'", searchItemURI, searchWorkitemLinkURI, 100);    //totaldenoCount= workitems.size();

                if(workitems !=null && workitems.size()>0)
                {
                    for(WorkItem witem : workitems)
                    {
                        committedDate = null;
                        closeedDate = null;
                        workingDate = null;

                        if (witem != null)
                        {
                            try
                            {
                                    if (strCheckCommittedField.equals("N")) {

                                                    String defaultTimezone = TimeZone.getDefault().getID();
                                                    IssueActivityDate issueActivityDates = util.getADOWorkItemActivityDate(witem.getId(), revisionValues, committedStatus, sourcedateFormat);
                                                    if (issueActivityDates != null) {
                                                        if (issueActivityDates.getRequestedDate() != null) {
                                                            committedDate = issueActivityDates.getRequestedDate(); //created date wll be here
                                                            strcommitedDate = issueActivityDates.getRequestedDateString();
                                                        }
                                                    }
                                            } else {
                                                if (strCheckCommittedField.equals("Created")) {
                                                    strCommittedDate = witem.getFields().getCreatedDate();
                                                } else if (strCheckCommittedField.equals("Updated")) {
                                                    strCommittedDate = witem.getFields().getChangedDate();
                                                }
                                                if (!strCommittedDate.isEmpty()) {
                                                    committedDate = util.ConvertStringToDateForZFormat(strCommittedDate);
                                                }
                                            }

                                            if (strCheckClosedField.equals("N")) {
                                                //Check the closed date by looking to the configuration from the histroy
                                                revisionValues = witem.getRevisions();
                                                boolean firstOccuranceClosedDate = false;

                                                if (revisionValues != null & revisionValues.size() > 0) {
                                                    for (RevisionValue revisionValuedate : revisionValues) {
                                                        if (revisionValuedate != null) {
                                                            if (!adoClosedStatus.isEmpty()) {
                                                                if (firstOccuranceClosedDate == false) {
                                                                    //Check and compare the status with the record to get a match
                                                                    if (revisionValuedate.getFields().getState().equals(adoClosedStatus)) {
                                                                        firstOccuranceClosedDate = true;
                                                                        strClosedDate = revisionValuedate.getFields().getStateChangeDate();
                                                                        break;
                                                                    }
                                                                }
                                                            }

                                                            if (!adoPrevClosedStatus.isEmpty()) {
                                                                if (firstOccuranceClosedDate == false) {
                                                                    //Check and compare the status with the record to get a match
                                                                    if (revisionValuedate.getFields().getState().equals(adoPrevClosedStatus)) {
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
                                            } else {
                                                if (strCheckClosedField.equals("Closed")) {
                                                    strClosedDate = witem.getFields().getClosedDate();
                                                } else if (strCheckClosedField.equals("Resolved")) {
                                                    strClosedDate = witem.getFields().getResolvedDate();
                                                }
                                                if (!strClosedDate.isEmpty()) {
                                                    closeedDate = util.ConvertStringToDateForZFormat(strClosedDate);
                                                }
                                            }

                                            if (committedDate != null && closeedDate != null) {
                                                issueDateVariance = new IssueDateVariance();
                                                issueDateVariance.setKey(witem.getId());
                                                issueDateVariance.setType(witem.getFields().getWorkItemType());
                                                issueDateVariance.setIssueStatus(witem.getFields().getState());
                                                issueDateVariance.setCommitedDate(committedDate);
                                                issueDateVariance.setClosedDate(closeedDate);
                                                issueDateVariance.setPriority(witem.getFields().getSeverity());
                                                issueDateVariance.setVariance(0.0);
                                                issueDateVariance.setCommitedDateString(strcommitedDate);
                                                issueDateVariance.setClosedDateString(strClosedDate);

                                                variance = util.GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, includeCommittedDate1);
                                                issueDateVariance.setVariance(variance);

                                                slaStatus = "";
                                                if (witem.getFields().getSeverity().equalsIgnoreCase("1 - Critical")) {
                                                    if (variance <= P1Limit) {
                                                        slaStatus = "Met";
                                                    } else {
                                                        slaStatus = "Not Met";
                                                    }
                                                } else if (witem.getFields().getSeverity().equalsIgnoreCase("2 - High")) {
                                                    if (variance <= P2Limit) {
                                                        slaStatus = "Met";
                                                    } else {
                                                        slaStatus = "Not Met";
                                                    }
                                                } else if (witem.getFields().getSeverity().equalsIgnoreCase("3 - Medium")) {
                                                    if (variance <= P3Limit) {
                                                        slaStatus = "Met";
                                                    } else {
                                                        slaStatus = "Not Met";
                                                    }
                                                }

                                                issueDateVariance.setStatus(slaStatus);
                                                elligibleIssues.add(issueDateVariance);
                                            }

                                            //Write to data file
                                            if (project.getDetailedLogRequired().equals("Y")) {
                                                dataLinesDetailed.add(new String[]
                                                        {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getSeverity(), witem.getFields().getState(), strcommitedDate, strClosedDate});
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
                        strCommittedDate = "";
                        strClosedDate = "";
                        String strVariance = "";
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

                        if (iv.getCommitedDate() != null)
                        {
                            strCommittedDate = iv.getCommitedDateString();
                        }

                        if (iv.getClosedDate() != null)
                        {
                            strClosedDate = iv.getClosedDateString();
                        }

                        if (iv.getCommitedDate() != null && iv.getClosedDate() != null)
                        {
                            strVariance = String.valueOf(iv.getVariance());
                        }

                        if (project.getDatafileRequired().equals("Y"))
                        {
                            dataLines.add(new String[]
                                    {iv.getKey(), type, status, priority, strCommittedDate, strClosedDate,
                                            strVariance, iv.getStatus()});
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
        }
        catch (Exception ex)
        {
            return null;
        }
    }

}
