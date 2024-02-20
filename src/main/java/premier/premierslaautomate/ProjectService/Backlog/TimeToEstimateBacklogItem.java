package premier.premierslaautomate.ProjectService.Backlog;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import premier.premierslaautomate.DataServices.AdoDataService;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IAdoDataService;
import premier.premierslaautomate.Models.ADO.*;
import premier.premierslaautomate.Models.Issue;
import premier.premierslaautomate.Models.IssueDateVariance;
import premier.premierslaautomate.Models.ProcessedData;
import premier.premierslaautomate.Utilities.CommonUtil;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Common Variables


@Service
public class TimeToEstimateBacklogItem  implements Serializable {
    //CommonVariables
    private IAdoDataService iAdoDataService = new AdoDataService();

    private SlaResult slaResult = new SlaResult();

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

    public  ProcessedData timeToEstimateBacklogItem1(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {

        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);

        //Generic for each method
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();

        String processingLog = "";

        //Method Specific Variables
        String fromdateFromConfig = "";
        String todateFromConfig = "";
        String dateFormatFromConfig = "";
        String estimationcompletedate = "";
        double originalestimation = 0;
        String currentstatus = "";
        Float storyLimit = 0f;
        Float epicLimit = 0f;
        String ADQuery = "";
        List<WorkItem> workitems = new ArrayList<>();
        String holidayList = "";

        List<Date> lstHolidays = new ArrayList<>();
        String strPageSize = "";
        String strCheckHolidays = "";
        String strCheckWeekend = "";
        String releasedateformat = "";
        String sourcedateFormat = "";
        Date dtReceivedDate = null;
        Date dtEstimationDate = null;
        Date dtInPiStateDate = null;
        Date dtFromDate;
        Date dtToDate;
        long dateDifference = 0;
        double actualValue = 0;
        int pageSize = 0;
        String eligiblity = "";
        List<RevisionValue> revisions = new ArrayList<>();
        String estimationStatus = ""; //Hold the status which is used to Receive Estimation
        String piStatus = "";

        String detailLogFilePath = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.log";
        processingLog = "Processing  SLA : " + sla.getSlaname();
        if (project.getDetailedLogRequired().equals("Y")) {
            status = util.WriteToFile(detailLogFilePath, processingLog);
        }

        try {
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

            releasedateformat = project.getReleaseDateFormat();

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

            strPageSize = project.getPageSize();
            if (!strPageSize.isEmpty()) {
                try {
                    pageSize = Integer.parseInt(strPageSize);
                } catch (Exception exPageSizeParse) {
                    pageSize = 1000;
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

            String limitValue = sla.getLimit();

            if (limitValue.isEmpty()) {
                message = twoSpace + "Limit data Not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            //Split it to get the values
            if (!limitValue.isEmpty()) {
                String[] limits = limitValue.split("#", -2);
                try {
                    if (limits.length >= 2) {
                        if (!limits[0].isEmpty()) {
                            storyLimit = Float.parseFloat(limits[0]);
                        }

                        if (!limits[1].isEmpty()) {
                            epicLimit = Float.parseFloat(limits[1]);
                        }
                    }
                } catch (Exception exLimitParsing) {
                    message = twoSpace + "Error while parsing the Limit Values. Stopping the Process. Error: " + exLimitParsing.getMessage();
                    ;
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
            }

            if (storyLimit == 0 && epicLimit == 0) {
                message = twoSpace + "Invalid values in Limit configuration. Please check your config. Stopping the processing for the SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            } else {
                message += newLine + twoSpace + "Story Level Limit : " + storyLimit.toString();
                message += newLine + twoSpace + "Epic Level Limit : " + epicLimit.toString();
            }

            double dblEpicLimit = epicLimit;
            double dblStoryLimit = storyLimit;

            fromdateFromConfig = sla.getFrom();
            todateFromConfig = sla.getTo();

            if (!fromdateFromConfig.equals("")) {
                if (util.isDateValid(fromdateFromConfig, dateFormatFromConfig) == false) {
                    message = twoSpace + "From Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
            } else {
                message = twoSpace + "From Date is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (!todateFromConfig.equals("")) {
                if (util.isDateValid(todateFromConfig, dateFormatFromConfig) == false) {
                    message = twoSpace + "To Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
            } else {
                message = twoSpace + "To Date is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            dtFromDate = util.ConvertToDate(fromdateFromConfig, dateFormatFromConfig);
            dtToDate = util.ConvertToDate(todateFromConfig, dateFormatFromConfig);

            if (dtFromDate == null || dtToDate == null) {
                message = twoSpace + "From and To date is not valid, please check your configuration. Stopping SLA calculation";
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

            String strIncludeStartDate = sla.getInput4();
            if (strIncludeStartDate.equals("") || !strIncludeStartDate.equals("Y")) {
                strIncludeStartDate = "N";
            }



            if (project.getProjectsource().equals(SourceKey.ADO.value)) {

                List<IssueDateVariance> elligibleIssues = new ArrayList<>();
                String estimationstartdate = "";

                estimationStatus = sla.getConfig3();
                piStatus = sla.getConfig4();
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type", "Status", "Limit", "Original Estimation", "Estimation Recieved Date", "Estimation Completion Date", "Variance", "eligible"});
                }

                if (project.getDetailedLogRequired().equals("Y")) {
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type", "Status", "Estimation Recieved Date", "Estimation Completion Date"});
                }

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty()) {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                workitems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", false, true, false, 100);

                totaldenoCount = 0;
                totalnumCount = 0;
                message = "";

                if (workitems != null && workitems.size() > 0) {               //Line added after change


                    for (WorkItem witem : workitems) {
                        estimationcompletedate = "";
                        originalestimation = 0;
                        estimationstartdate = "";
                        currentstatus = "";
                        boolean StatusChange = false;
                        dtReceivedDate = null;
                        dtEstimationDate = null;
                        dtInPiStateDate = null;

                        if (witem != null) {
                            //Get the Estimation Receive date, Estimation Completion date and Original Estimation from the Revision
                            if (witem.getRevisions() != null && witem.getRevisions().size() > 0) {
                                revisions = witem.getRevisions();
                                for (RevisionValue rev : revisions) {
                                    StatusChange = false;

                                    if (!currentstatus.equals(rev.getFields().getState())) {
                                        currentstatus = rev.getFields().getState();
                                        StatusChange = true;
                                    }

                                    if (StatusChange == true) {
                                        //this record is status change record
                                        if (currentstatus.equals(estimationStatus)) {
                                            estimationstartdate = rev.getFields().getStateChangeDate();
                                            if (!estimationstartdate.isEmpty()) {
                                                dtReceivedDate = util.ConvertStringToDateForZFormat(estimationstartdate); //nikhil : In analysis and estimate date
                                            }
                                        }
                                        if (currentstatus.equals(piStatus)) {
                                            estimationcompletedate = rev.getFields().getStateChangeDate();
                                            if (!estimationcompletedate.isEmpty()) {
                                                dtEstimationDate = util.ConvertStringToDateForZFormat(estimationcompletedate); //nikhil : In "in PI" state date
                                            }
                                        }
                                    }

                                }

                                if (dtReceivedDate != null) {

                                    if ((dtReceivedDate.compareTo(dtFromDate) >= 0 && dtReceivedDate.compareTo(dtToDate) <= 0) == true) {
                                        IssueDateVariance issue = new IssueDateVariance();
                                        issue.setKey(String.valueOf(witem.getId()));
                                        issue.setType(witem.getFields().getWorkItemType());
                                        issue.setIssueStatus(witem.getFields().getState());
                                        issue.setCommitedDateString(estimationstartdate);
                                        issue.setClosedDateString(estimationcompletedate);
                                        issue.setStatus("");
                                        issue.setPriority(""); //Field used to store the Original Estiate value

                                        if (witem.getFields().getWorkItemType().equals("Epic")) {
                                            //issue.setLimit(Integer.parseInt(String.valueOf(epicLimit)));
                                            issue.setLimit((int) dblEpicLimit);
                                        } else {
                                            String strStoryLimit = String.valueOf(storyLimit);
                                            //issue.setLimit(Integer.parseInt(String.valueOf(storyLimit)));
                                            issue.setLimit((int) dblStoryLimit);
                                        }

                                        dateDifference = 0;
                                        issue.setVariance(-100.00);
                                        eligiblity = "";
                                        if (dtReceivedDate != null && dtEstimationDate != null) {
                                            issue.setPriority(String.valueOf(originalestimation)); //Stores the Original Estimation for printing purpose
                                            dateDifference = util.GetDayVariance(dtReceivedDate, dtEstimationDate, lstHolidays, strCheckHolidays, strCheckWeekend, strIncludeStartDate);
                                            issue.setVariance((double) dateDifference);
                                            if (witem.getFields().getWorkItemType().equals("Epic")) {
                                                if (dateDifference <= dblEpicLimit) {
                                                    eligiblity = "Met";
                                                } else {
                                                    eligiblity = "Not Met";
                                                }
                                            } else {
                                                if (dateDifference <= dblStoryLimit) {
                                                    eligiblity = "Met";
                                                } else {
                                                    eligiblity = "Not Met";
                                                }
                                            }
                                            issue.setStatus(eligiblity);
                                        } else {
                                            issue.setStatus("Not Met");
                                        }
                                        elligibleIssues.add(issue);
                                    }
                                }

                                if (project.getDetailedLogRequired().equals("Y")) {
                                    dataLinesDetailed.add(new String[]
                                            {String.valueOf(witem.getId()), witem.getFields().getWorkItemType(), witem.getFields().getState(), estimationstartdate, estimationcompletedate});
                                }
                            }
                        }
                    }
                }

                if (elligibleIssues != null && elligibleIssues.size() > 0) {
                    totaldenoCount = elligibleIssues.size(); // Total issues baselined and planned for estimation
                    totalnumCount = (int) elligibleIssues.stream().filter(x -> x.getStatus().equals("Met")).count();

                    //Write to the data file
                    for (IssueDateVariance issue : elligibleIssues) {
                        String variancedata = "";
                        String strLimit = "";

                        if (!issue.getVariance().equals(-100.00)) {
                            variancedata = String.valueOf(issue.getVariance());
                        }
                        if (issue.getLimit() >= 0) {
                            strLimit = String.valueOf(issue.getLimit());
                        }

                        if (project.getDatafileRequired().equals("Y")) {
                            dataLines.add(new String[]
                                    {issue.getKey(), issue.getType(), issue.getIssueStatus(), strLimit, issue.getPriority(), issue.getCommitedDateString(), issue.getClosedDateString(), variancedata, issue.getStatus()});
                        }
                    }
                }

                ////////// Business Logic Implementation Ends  //////////

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

                if (dataLinesDetailed.size() > 0) {
                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
                    try {
                        boolean csvStatus = util.WriteToCSv(dataLinesDetailed, dataFileName1);
                        if (csvStatus == true) {
                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + dataFileName1;
                        } else {
                            message += newLine + twoSpace + "Unable to create the data file";
                        }
                    } catch (Exception exCsv) {
                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
                    }
                }

                message += newLine + twoSpace + " Total Denominator Count = " + totaldenoCount;
                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
                message += newLine + twoSpace + " AdoQuery = " + ADQuery;

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
                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual), ADQuery);
                status = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            return null;
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;
    }

    public  SlaResult timeToEstimateBacklogItem1(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues,String src, UserVariables userVariables) {
        sla.setDenojql("Select * From WorkItems  Where  [System.WorkItemType] = ('User Story','Spike','Feature')  " +
                "And [System.TeamProject] = '" + userVariables.getTeamProject() +"'"+
                "And ([System.AreaPath] in "+ userVariables.getAreaPathBL() +
                "And [System.State] EVER 'In Analysis and Estimate' And [System.State] Not in ('In Analysis and Estimate','New')"
        );
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);

        //Generic for each method
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();

        String processingLog = "";

        //Method Specific Variables
        String fromdateFromConfig = "";
        String todateFromConfig = "";
        String dateFormatFromConfig = "";
        String estimationcompletedate = "";
        double originalestimation = 0;
        String currentstatus = "";
        Float storyLimit = 0f;
        Float epicLimit = 0f;
        String ADQuery = "";
        List<WorkItem> workitems = new ArrayList<>();
        String holidayList = "";

        List<Date> lstHolidays = new ArrayList<>();
        String strPageSize = "";
        String strCheckHolidays = "";
        String strCheckWeekend = "";
        String releasedateformat = "";
        String sourcedateFormat = "";
        Date dtReceivedDate = null;
        Date dtEstimationDate = null;
        Date dtInPiStateDate = null;
        Date dtFromDate;
        Date dtToDate;
        long dateDifference = 0;
        double actualValue = 0;
        int pageSize = 0;
        String eligiblity = "";
        List<RevisionValue> revisions = new ArrayList<>();
        String estimationStatus = ""; //Hold the status which is used to Receive Estimation
        String piStatus = "";

        String detailLogFilePath = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.log";
        processingLog = "Processing  SLA : " + sla.getSlaname();
        if (project.getDetailedLogRequired().equals("Y")) {
            status = util.WriteToFile(detailLogFilePath, processingLog);
        }

        try {
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

            releasedateformat = project.getReleaseDateFormat();

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

            strPageSize = project.getPageSize();
            if (!strPageSize.isEmpty()) {
                try {
                    pageSize = Integer.parseInt(strPageSize);
                } catch (Exception exPageSizeParse) {
                    pageSize = 1000;
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

            String limitValue = sla.getLimit();

            if (limitValue.isEmpty()) {
                message = twoSpace + "Limit data Not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            //Split it to get the values
            if (!limitValue.isEmpty()) {
                String[] limits = limitValue.split("#", -2);
                try {
                    if (limits.length >= 2) {
                        if (!limits[0].isEmpty()) {
                            storyLimit = Float.parseFloat(limits[0]);
                        }

                        if (!limits[1].isEmpty()) {
                            epicLimit = Float.parseFloat(limits[1]);
                        }
                    }
                } catch (Exception exLimitParsing) {
                    message = twoSpace + "Error while parsing the Limit Values. Stopping the Process. Error: " + exLimitParsing.getMessage();
                    ;
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
            }

            if (storyLimit == 0 && epicLimit == 0) {
                message = twoSpace + "Invalid values in Limit configuration. Please check your config. Stopping the processing for the SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            } else {
                message += newLine + twoSpace + "Story Level Limit : " + storyLimit.toString();
                message += newLine + twoSpace + "Epic Level Limit : " + epicLimit.toString();
            }

            double dblEpicLimit = epicLimit;
            double dblStoryLimit = storyLimit;

            fromdateFromConfig = sla.getFrom();
            todateFromConfig = sla.getTo();

            if (!fromdateFromConfig.equals("")) {
                if (util.isDateValid(fromdateFromConfig, dateFormatFromConfig) == false) {
                    message = twoSpace + "From Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
            } else {
                message = twoSpace + "From Date is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (!todateFromConfig.equals("")) {
                if (util.isDateValid(todateFromConfig, dateFormatFromConfig) == false) {
                    message = twoSpace + "To Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
            } else {
                message = twoSpace + "To Date is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            dtFromDate = util.ConvertToDate(fromdateFromConfig, dateFormatFromConfig);
            dtToDate = util.ConvertToDate(todateFromConfig, dateFormatFromConfig);

            if (dtFromDate == null || dtToDate == null) {
                message = twoSpace + "From and To date is not valid, please check your configuration. Stopping SLA calculation";
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

            String strIncludeStartDate = sla.getInput4();
            if (strIncludeStartDate.equals("") || !strIncludeStartDate.equals("Y")) {
                strIncludeStartDate = "N";
            }



            if (project.getProjectsource().equals(SourceKey.ADO.value)) {

                List<IssueDateVariance> elligibleIssues = new ArrayList<>();
                String estimationstartdate = "";

                estimationStatus = sla.getConfig3();
                piStatus = sla.getConfig4();
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type", "Status", "Limit", "Original Estimation", "Estimation Recieved Date", "Estimation Completion Date", "Variance", "eligible"});
                }

                if (project.getDetailedLogRequired().equals("Y")) {
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type", "Status", "Estimation Recieved Date", "Estimation Completion Date"});
                }

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty()) {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                workitems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", false, true, false, 100);

                totaldenoCount = 0;
                totalnumCount = 0;
                message = "";

                if (workitems != null && workitems.size() > 0) {               //Line added after change


                    for (WorkItem witem : workitems) {
                        estimationcompletedate = "";
                        originalestimation = 0;
                        estimationstartdate = "";
                        currentstatus = "";
                        boolean StatusChange = false;
                        dtReceivedDate = null;
                        dtEstimationDate = null;
                        dtInPiStateDate = null;

                        if (witem != null) {
                            //Get the Estimation Receive date, Estimation Completion date and Original Estimation from the Revision
                            if (witem.getRevisions() != null && witem.getRevisions().size() > 0) {
                                revisions = witem.getRevisions();
                                for (RevisionValue rev : revisions) {
                                    StatusChange = false;

                                    if (!currentstatus.equals(rev.getFields().getState())) {
                                        currentstatus = rev.getFields().getState();
                                        StatusChange = true;
                                    }

                                    if (StatusChange == true) {
                                        //this record is status change record
                                        if (currentstatus.equals(estimationStatus)) {
                                            estimationstartdate = rev.getFields().getStateChangeDate();
                                            if (!estimationstartdate.isEmpty()) {
                                                dtReceivedDate = util.ConvertStringToDateForZFormat(estimationstartdate); //nikhil : In analysis and estimate date
                                            }
                                        }
                                        if (currentstatus.equals(piStatus)) {
                                            estimationcompletedate = rev.getFields().getStateChangeDate();
                                            if (!estimationcompletedate.isEmpty()) {
                                                dtEstimationDate = util.ConvertStringToDateForZFormat(estimationcompletedate); //nikhil : In "in PI" state date
                                            }
                                        }
                                    }

                                }

                                if (dtReceivedDate != null) {

                                    if ((dtReceivedDate.compareTo(dtFromDate) >= 0 && dtReceivedDate.compareTo(dtToDate) <= 0) == true) {
                                        IssueDateVariance issue = new IssueDateVariance();
                                        issue.setKey(String.valueOf(witem.getId()));
                                        issue.setType(witem.getFields().getWorkItemType());
                                        issue.setIssueStatus(witem.getFields().getState());
                                        issue.setCommitedDateString(estimationstartdate);
                                        issue.setClosedDateString(estimationcompletedate);
                                        issue.setStatus("");
                                        issue.setPriority(""); //Field used to store the Original Estiate value

                                        if (witem.getFields().getWorkItemType().equals("Epic")) {
                                            //issue.setLimit(Integer.parseInt(String.valueOf(epicLimit)));
                                            issue.setLimit((int) dblEpicLimit);
                                        } else {
                                            String strStoryLimit = String.valueOf(storyLimit);
                                            //issue.setLimit(Integer.parseInt(String.valueOf(storyLimit)));
                                            issue.setLimit((int) dblStoryLimit);
                                        }

                                        dateDifference = 0;
                                        issue.setVariance(-100.00);
                                        eligiblity = "";
                                        if (dtReceivedDate != null && dtEstimationDate != null) {
                                            issue.setPriority(String.valueOf(originalestimation)); //Stores the Original Estimation for printing purpose
                                            dateDifference = util.GetDayVariance(dtReceivedDate, dtEstimationDate, lstHolidays, strCheckHolidays, strCheckWeekend, strIncludeStartDate);
                                            issue.setVariance((double) dateDifference);
                                            if (witem.getFields().getWorkItemType().equals("Epic")) {
                                                if (dateDifference <= dblEpicLimit) {
                                                    eligiblity = "Met";
                                                } else {
                                                    eligiblity = "Not Met";
                                                }
                                            } else {
                                                if (dateDifference <= dblStoryLimit) {
                                                    eligiblity = "Met";
                                                } else {
                                                    eligiblity = "Not Met";
                                                }
                                            }
                                            issue.setStatus(eligiblity);
                                        } else {
                                            issue.setStatus("Not Met");
                                        }
                                        elligibleIssues.add(issue);
                                    }
                                }

                                if (project.getDetailedLogRequired().equals("Y")) {
                                    dataLinesDetailed.add(new String[]
                                            {String.valueOf(witem.getId()), witem.getFields().getWorkItemType(), witem.getFields().getState(), estimationstartdate, estimationcompletedate});
                                }
                            }
                        }
                    }
                }

                if (elligibleIssues != null && elligibleIssues.size() > 0) {
                    totaldenoCount = elligibleIssues.size(); // Total issues baselined and planned for estimation
                    totalnumCount = (int) elligibleIssues.stream().filter(x -> x.getStatus().equals("Met")).count();

                    //Write to the data file
                    for (IssueDateVariance issue : elligibleIssues) {
                        String variancedata = "";
                        String strLimit = "";

                        if (!issue.getVariance().equals(-100.00)) {
                            variancedata = String.valueOf(issue.getVariance());
                        }
                        if (issue.getLimit() >= 0) {
                            strLimit = String.valueOf(issue.getLimit());
                        }

                        if (project.getDatafileRequired().equals("Y")) {
                            dataLines.add(new String[]
                                    {issue.getKey(), issue.getType(), issue.getIssueStatus(), strLimit, issue.getPriority(), issue.getCommitedDateString(), issue.getClosedDateString(), variancedata, issue.getStatus()});
                        }
                    }
                }

                ////////// Business Logic Implementation Ends  //////////

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

                if (dataLinesDetailed.size() > 0) {
                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
                    try {
                        boolean csvStatus = util.WriteToCSv(dataLinesDetailed, dataFileName1);
                        if (csvStatus == true) {
                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + dataFileName1;
                        } else {
                            message += newLine + twoSpace + "Unable to create the data file";
                        }
                    } catch (Exception exCsv) {
                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
                    }
                }

                message += newLine + twoSpace + " Total Denominator Count = " + totaldenoCount;
                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
                message += newLine + twoSpace + " AdoQuery = " + ADQuery;

                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                if (totaldenoCount == 0 && totalnumCount == 0)
                {
                    totaldenoCount = 0;
                    totalnumCount = 0;
                    actualValue =0;
                    slaStatus = "NT";
                    slaResult.setDenominator(String.valueOf(totaldenoCount));
                    slaResult.setNumerator(String.valueOf(totalnumCount));
                    slaResult.setExpectedServiceLevel(String.valueOf(expectedsla));
                    slaResult.setMinimumServiceLevel(String.valueOf(minsla));
                    slaResult.setSlaName(sla.getSlaname());
                    slaResult.setActual(String.valueOf(actualValue));
                    slaResult.setStatus(slaStatus);
                    return slaResult;
                }
                else {
                    actualValue = util.GetActualValueV1((double)totaldenoCount, (double)totalnumCount);
                    slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double)expectedsla, (double)minsla);
                    slaResult.setDenominator(String.valueOf(totaldenoCount));
                    slaResult.setNumerator(String.valueOf(totalnumCount));
                    slaResult.setExpectedServiceLevel(String.valueOf(expectedsla));
                    slaResult.setMinimumServiceLevel(String.valueOf(minsla));
                    slaResult.setSlaName(sla.getSlaname());
                    slaResult.setActual(String.valueOf(actualValue));
                    slaResult.setStatus(slaStatus);
                    return slaResult;
                }

            }

            return null;
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;
    }
}