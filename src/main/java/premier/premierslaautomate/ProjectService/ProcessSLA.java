package premier.premierslaautomate.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import premier.premierslaautomate.DataServices.AdoDataService;
import premier.premierslaautomate.DataServices.JiraDataService;
import premier.premierslaautomate.ENUM.CommonKey;
import premier.premierslaautomate.ENUM.JiraTypes;
import premier.premierslaautomate.ENUM.SLAKey;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IAdoDataService;
import premier.premierslaautomate.Interfaces.IJiraDataService;
import premier.premierslaautomate.Interfaces.IProject;
import premier.premierslaautomate.Models.*;
import premier.premierslaautomate.Models.ADO.*;
import premier.premierslaautomate.Models.Comments;
import premier.premierslaautomate.Models.Jira.ClosedSprints;
import premier.premierslaautomate.ProjectService.Backlog.*;
import premier.premierslaautomate.ProjectService.NonBacklog.*;
import premier.premierslaautomate.Utilities.CommonUtil;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProcessSLA implements IProject
{
    private IJiraDataService iJiraDataService = new JiraDataService();
    private IAdoDataService iAdoDataService = new AdoDataService();

    //Test for Arun
    String denourl = "";
    String numurl = "";
    String denoJql = "";
    String numJql = "";
    String hellow = "";
    int totalnumCount = 0;
    int totaldenoCount = 0;
    int totalNumCountSatisfied = 0;
    int totalNumCountNotSatisfied = 0;
    int totalNumeratorCountSatisfied = 0;
    int totalNumeratorCountNotSatisfied = 0;
    double expectedsla = 0;
    double minsla = 0;
    Float actual = 0.0f;
    String slaStatus = "";

    String currentLogPath = "";
    String currentOutputPath = "";
    String currentJsonPath = "";
    String message = "";
    String tabKey = "\t";
    String twoSpace = "  ";
    String newLine = "\r\n";
    boolean status = false;
    CommonUtil util = new CommonUtil();
    List<String[]> dataLines = new ArrayList<>();
    List<String[]> dataLinesDetailed = new ArrayList<>();



    public ProcessedData Process(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        if (sla == null || project == null) {
            System.out.println("Unable to process due to Project/SLA Object is Null - ERPMM");
            return null;
        }

        //Backlog SLA Call Start
        if (sla.getSlatype().equals(CommonKey.BACKLOG.value) || sla.getSlatype().equals(CommonKey.BOTH.value) ) {
            if (sla.getSlakey().equals(SLAKey.TimeToEstimateBacklogItems.toString())) {
                return new TimeToEstimateBacklogItem().timeToEstimateBacklogItem1(sla, userName, password, project, retrievedIssues );
            } else if (sla.getSlakey().equals(SLAKey.EstimationQuality.toString())) {
                return new EstimationQuality().estimationQuality (sla, userName, password, project, retrievedIssues);
            }  else if (sla.getSlakey().equals(SLAKey.TimelyAcceptedMilestone.toString())) {
               // return new TimelyAcceptedMilestoneDelivery().timelyAcceptedMilestone2(sla, userName, password, project, retrievedIssues,"automated",null);
            }  else if (sla.getSlakey().equals(SLAKey.TimelyBackLogItem.toString())) {
                return new TimelyBackLogItemDelivery().timelyBackLogItem(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.BacklogItemQuality.toString())) {
                return new BacklogItemQuality().backlogItemQuality(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.DelayInReadyForProductionRelease.toString())) {
                return DelayInReadyForProductionRelease(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.AdhereToAgileMethodology.toString())) {
                return AdhereToAgileMethodology(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.ProductPercentTestingCompleted.toString())) {
            return new ProductPercentTestingCompleted().ProductPercentTestingCompleted(sla, userName, password, project, retrievedIssues);

            } else if (sla.getSlakey().equals(SLAKey.DefectDetectedInUAT.toString())) {
                return new DefectsDetectedReleaseCandidates().DefectDetectedInUAT(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.IssuesDetectedPostProductionRelease.toString())) {
                return IssuesDetectedPostProductionRelease(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.ReopenedDefectsBeforeProductionRelease.toString())) {
                return ReopenedDefectsBeforeProductionRelease(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.PercentageOfTestsAutomated.toString())) {
            return new PercentageOfTestsAutomated().PercentageOfTestsAutomated (sla, userName, password, project, retrievedIssues);

            } else if (sla.getSlakey().equals(SLAKey.AverageCycleTimeForRelease.toString())) {
                return AverageCycleTimeForRelease(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.ProcessEfficiency.toString())) {
                return ProcessEfficiency(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.PremierCustomerSatisficationSurvey.toString())) {
                return PremierCustomerSatisficationSurvey(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.NumberofMilestoneDateDelays.toString())) {
                //return new NumberofMilestoneDateDelays().numberofMilestoneDateDelays(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.VarianceToOriginaMilestoneEstimate.toString())) {
                return new VarianceToOriginaMilestoneEstimate().varianceToOriginaMilestoneEstimate(sla, userName, password, project, retrievedIssues);
            }
        }

        //Backlog SLA Call End

        //Non-Backlog SLA Call Start
        if (sla.getSlatype().equals(CommonKey.NONBACKLOG.value)) {
            if (sla.getSlakey().equals(SLAKey.SeverityLvl1IncidentResolution.toString())) {
                return new SeverityIncidentResolution().SeverityLvl1IncidentResolution(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.SeverityLvl2IncidentResolution.toString())) {
                return new SeverityIncidentResolution().SeverityLvl1IncidentResolution(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.SeverityLvl3IncidentResolution.toString())) {
                return new SeverityIncidentResolution().SeverityLvl1IncidentResolution(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.PercentOfIncidentOpened.toString())) {
                return PercentOfIncidentOpened(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.MTTR.toString())) {
                return new MeanTimeToRepair().MTTR(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.SystemUpTime.toString())) {
                return SystemUpTime(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.VolumeOfIncidents.toString())) {
                return new VolumeOfIncident().VolumeOfIncidents(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.PercentageofNBServicesAutomate.toString())) {
                return PercentageofNBServicesAutomate(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.ITCustomerSatisfication.toString())) {
                return ITCustomerSatisfication(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.NotifyToCustomerOfOutrage.toString())) {
                return NotifyToCustomerOfOutrage(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.ProblemRCATime.toString())) {
                return new RootCauseAnalysis().ProblemRCATime(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.ProblemResolutionTime.toString())) {
                return new ProblemResolutionTime().ProblemResolutionTime(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.RegulatoryUpdate.toString())) {
                return RegulatoryUpdate(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.CriticalSecurityThreatMitigation.toString())) {
                return CriticalSecurityThreatMitigation(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.Patches.toString())) {
                return Patches(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.ServiceLevelDataQuality.toString())) {
                return ServiceLevelDataQuality(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.SecurityThreatMitigation.toString())) {
                return SecurityThreatMitigation(sla, userName, password, project, retrievedIssues);
            } else if (sla.getSlakey().equals(SLAKey.UserStoryApprovalReport.toString())) {
                return UserStoryApprovalReport(sla, userName, password, project, retrievedIssues);
            }
        }
        //Non-Backlog SLA Call End

        return null;
    }














    private ProcessedData AdhereToAgileMethodology(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        //Modified and tested with July Data - Simanchal
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        List<String[]> dataLines2 = new ArrayList<>();
        String strPageSize = "";
        int pageSize = 1000;
        double actualValue = 0;
        try {
            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
                expectedsla = Integer.parseInt(sla.getExpectedsla());
                minsla = Integer.parseInt(sla.getMinimumsla());

                if (expectedsla == 0 || minsla == 0) {
                    //Stop the processing
                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                String strActualValue = sla.getInput1();
                if (strActualValue.isEmpty()) {
                    message = twoSpace + "Actual Value not provided, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                try {
                    actualValue = Float.parseFloat((sla.getInput1()));
                } catch (Exception exParseActualValue) {
                    message = twoSpace + "Not able to parse Actual Value (Numeric value required), please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
                message += newLine + twoSpace + " Actual = " + actualValue;
                slaStatus = util.CalculateFinalSLAValueV1(actualValue, expectedsla, minsla);
                message += newLine + twoSpace + " Status = " + slaStatus;
                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(totalnumCount), String.valueOf(totalnumCount),baseURI);
                status = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            return null;
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }
        return null;
    }


    private ProcessedData IssuesDetectedPostProductionRelease(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> numIssue = new ArrayList<>();
        List<String[]> dataLines1 = new ArrayList<>();
        int pageSize = 1000;
        String statusType = "";
        String strPageSize = "";
        String holidayList = "";
        String dateFormatFromConfig = "";
        List<Date> lstHolidays = new ArrayList<>();
        String strCheckHolidays="";
        String strCheckWeekend="";
        List<String[]> dataLinesDetailed = new ArrayList<>();

        String limit = sla.getLimit();
        try {
            if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false)
            {
                denourl = project.getProjecturl() + "/api/2/search?jql=project = " + sla.getDenojql();
            }

            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (denourl.isEmpty()) {
                message = twoSpace + "Deno JQL is not available, please check your configuration. Stopping SLA calculation";
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
            strCheckHolidays = sla.getInput3();
            if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
                strCheckHolidays = "N";
            }

            strCheckWeekend = sla.getInput4();
            if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
                strCheckWeekend = "N";
            }
            String strIncludeCommittedDate = sla.getInput1().replace("'", "");
            if (!strIncludeCommittedDate.equals("Y")) {
                strIncludeCommittedDate = "N";
            }
            dateFormatFromConfig = project.getDateFormat().replace("'", "");
            if (dateFormatFromConfig.equals("")) {
                message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            holidayList = project.getHolidays();
            if (holidayList.equals("")) {
                message = twoSpace + "Holiday details not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            String[] arrHoliday = holidayList.split(",");
            lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);

            if (lstHolidays == null) {
                message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {

                if (project.getDatafileRequired().equals("Y")) {
                    dataLines1.add(new String[]
                            {"StoryId", "Type", "Status"});
                }

                totaldenoCount = 0;
                message = "";

                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, pageSize);

                if (denoIssue != null && denoIssue.size() > 0) {
                    totaldenoCount = denoIssue.size();
                    for (Issue issue : denoIssue) {

                        if (project.getDatafileRequired().equals("Y")) {
                            dataLines1.add(new String[]
                                    {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName()});
                        }
                    }
                }

                if (dataLines1.size() > 0) {
                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
                    try {
                        boolean csvStatus = util.WriteToCSv(dataLines1, dataFileName);
                        if (csvStatus == true) {
                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
                        } else {
                            message += newLine + twoSpace + "Unable to create the data file";
                        }
                    } catch (Exception exCsv) {
                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
                    }
                }

                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
                message += newLine + twoSpace + "No Num Count considered for this SLA";
                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;

                double actualValue = totaldenoCount;
                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);

                if (actualValue <= minsla) {
                    slaStatus = "Met";
                } else {
                    slaStatus = "Not Met";
                }

                message += newLine + twoSpace + " Status = " + slaStatus;
                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(totalnumCount), String.valueOf(totaldenoCount),denourl);
                status = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {



                if (project.getDatafileRequired().equals("Y"))
                {
                    //story
                    dataLines.add(new String[]
                            {"Key", "Type", "Final Issue Status", "Released Date",
                                    "Total number of Bugs within 30 days"});                }

                if (project.getDetailedLogRequired().equals("Y"))
                {
                    //story
                    //related bugs (id, creation, within
                    dataLinesDetailed.add(new String[]
                            {"Fixed Version ID", "Key", "Type", "Final Issue Status", "total number of bugs created within 30 days", "total number of bugs created After 30 days" });
                }

                List<WorkItem> workitemsCreatedDates = new ArrayList<>();
                List<WorkItem> workitemsClosedDates = new ArrayList<>();
                String issueType = sla.getConfig1();

                List<WorkItem> finalWorkitems = null;
                List<WorkItem> Workitems = null;
                String itemQuery = sla.getInput2();
                String ADDenoQuery = "";
                String searchWorkitemLinkURI = project.getLinkItemUrl();
                String searchItemURI = project.getItemUrl();
                String strdefectCreatedDate = "";
                String strFVCloseddate = "";
                Date FVClosedDate = null;
                Date defectCreatedDate = null;
                int actualCount = 0;
                int count = 0;
                long variance = 0;
                double totalDenoCount = 0;
                int finalCount = 0;
                ProcessedData data = null;
                List<IssueDateVariance> eligibleissue = new ArrayList<>();
                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
                    ADDenoQuery = "{\n" +
                            "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";
                }
                Workitems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(),ADDenoQuery, "POST", false, true, true, true, "'Defect'", searchWorkitemLinkURI, searchItemURI, 1000);

                if(Workitems!=null&&Workitems.size()>0)
                {
                    for(WorkItem witem:Workitems)
                    {
                        if(witem!=null)
                        {
                            List<WorkItem> childlinks= witem.getChildLinks();
                            int releaseCount =0;
                            if(witem.getFields().getClosedDate()!=null) {
                                String strFVClosedDate = witem.getFields().getClosedDate();
                                FVClosedDate = util.ConvertStringToDateForZFormat(strFVClosedDate);
                                releaseCount++;
                            }


                            int aCount=0;

                            if(childlinks != null) {
                                for (WorkItem childitem : childlinks) {
                                    if ((childitem.getFields().getWorkItemType().equals("Defect")) && (childitem.getFields().getServiceLevelType().equals("PROD"))) {
                                        strdefectCreatedDate = childitem.getFields().getCreatedDate();
                                        defectCreatedDate = util.ConvertStringToDateForZFormat(strdefectCreatedDate);
                                        variance = util.GetDayVariance(defectCreatedDate, FVClosedDate, lstHolidays, strCheckHolidays, strCheckWeekend, strIncludeCommittedDate);
                                        if (variance <= Integer.parseInt(limit)) {
                                            if (releaseCount > 0) {
                                                actualCount++;
                                            }
                                        } else {
                                            count++;

                                        }
                                        if (project.getDetailedLogRequired().equals("Y")) {
                                            dataLinesDetailed.add(new String[]
                                                    {"", childitem.getId(), childitem.getFields().getWorkItemType(), childitem.getFields().getState(), String.valueOf(aCount), String.valueOf(count)});
                                            // {"Key", "Type", "Final Issue Status", "total number of bugs created within 30 days", "total number of bugs created After 30 days" });
                                        }
                                        if (project.getDatafileRequired().equals("Y")) {
                                            dataLines.add(new String[]
                                                    {childitem.getId(), childitem.getFields().getWorkItemType(), childitem.getFields().getState(), witem.getFields().getClosedDate(), String.valueOf(aCount)});
                                        }
                                    }

                                }
                            }

                        }
                    }
                }

                ///////Data File Saving - Start
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

                ///////Data File Saving - End
                if (actualCount <= minsla){
                    //met
                    slaStatus = "Met";
                }
                else{
                    //not met
                    slaStatus = "Not Met";
                }
                message += newLine + twoSpace + " AdoQuery = " + ADDenoQuery;
                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
                message += newLine + twoSpace + " Actual = " + actualCount;
                message += newLine + twoSpace + " Status = " + slaStatus;
                boolean isStatus = util.WriteToFile(project.getLogFile(), message);
                data = util.BuildProcessData(sla, (float) actualCount, slaStatus, "", "" ,ADDenoQuery);
                if (data != null) {
                    return data;
                }
                return null;
            }
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + " 1. Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }
        return null;
    }


    private ProcessedData AverageCycleTimeForRelease(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues)
    {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        String baseURI = "";
        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> numIssue = new ArrayList<>();
        List<Issue> commentIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();

        //MEthod Specific Variables
        List<AvgCycleJira> eligibleissuesLst = new ArrayList<>();
        AvgCycleJira eligibleissue = new AvgCycleJira();
        List<FixedVersionIssue> fixedVersionData = new ArrayList<>();

        List<FixedVersion> fixedVersions = new ArrayList<>();
        List<History> Historylist = new ArrayList<>();

        Date committedDate = new Date();//committeddate
        Date closeedDate = new Date();
        Date workingDate = new Date();
        String strWorkingDate = "";
        String committedField = "";
        String releasedField = "";

        boolean isValid = false;
        String inDevelopmentStatus = "";
        String closeStatus = "";
        String sourcedateFormat = "";
        double actualValue = 0;
        String holidayList = "";
        String projectdateFormat = "";
        String limitFromConfig = "";
        int limit = 0;
        int variance = 0;
        List<Date> lstHolidays = new ArrayList<>();

        IssueActivityDate issueActivityDate = new IssueActivityDate();
        String fromdateFromConfig = "";
        String todateFromConfig = "";
        String dateFormatFromConfig = "";
        Date dtFromDate = null;
        Date dtToDate = null;
        String strCheckHolidays = "";
        String strCheckWeekend = "";
        String strCheckCreatedDateInsteadHistory = "N";

        String strKey = "";
        String strType = "";
        String strStatus = "";
        int percentageVariance = -1;
        String strPageSize = "";
        int pageSize = 1000;

        String strtotalFixedVersions = "";
        String strreleasedFixedVersions = "";
        List<FixedVersion> matchedFixedVersion = new ArrayList<>();
        String strFixedVersionDateFormat = "";
        String strFixedVersionFormat = "";
        String strFixedVersionSplitter = "";
        int majorRelease = -1;
        int updateRelease = -1;
        int hotfixRelease = -1;

        double baselineAvg = 0;
        double baselineAvgMajor = 0;
        double newBaselineAvg = 0;
        double newBaselineAvgMajor = 0;

        double totalMajorDenoCount = 0;
        double totalOtherDenoCount = 0;
        double totalDaysMajor = 0;
        double totaldays = 0;
        double addition = 0;
        double additionMajor = 0;
        int increaseDay = 0;

        String releaseDateFormat = "";

        String strCommittedDate = "";
        String strClosedDate = "";

        try
        {
            strPageSize = project.getPageSize();

            if (!strPageSize.isEmpty()) {
                try {
                    pageSize = Integer.parseInt(strPageSize);
                } catch (Exception exPageSizeParse) {
                    pageSize = 1000;
                }
            }

            sourcedateFormat = project.getSourceDateFormat();
            if (sourcedateFormat.equals("")) {
                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            dateFormatFromConfig = project.getDateFormat().replace("'", "");
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

            releaseDateFormat = project.getReleaseDateFormat();
            if (releaseDateFormat.equals("") && !releasedField.equals("")) {
                message = twoSpace + "Release Date format is not found, please check your configuration. Stopping SLA calculation";
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

            fromdateFromConfig = sla.getFrom().replace("'", "");
            todateFromConfig = sla.getTo().replace("'", "");
            limitFromConfig = sla.getLimit().replace("'", "");

            //This is only required for ADO
            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (!fromdateFromConfig.equals(""))
                {
                    if (util.isDateValid(fromdateFromConfig, dateFormatFromConfig) == false)
                    {
                        message = twoSpace + "From Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
                        status = util.WriteToFile(project.getLogFile(), message);
                        return null;
                    }
                }
                else
                {
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
            }

            if (limitFromConfig.equals(""))
            {
                message = twoSpace + "Percentage Variance data Not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            try
            {
                percentageVariance = Integer.parseInt(limitFromConfig); //added
            }
            catch (Exception exbaseline)
            {
            }

            if (percentageVariance == -1)
            {
                message = twoSpace + "Not able to parse Percentage Variance value. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            inDevelopmentStatus = sla.getConfig1();
            if (inDevelopmentStatus.equals(""))
            {
                message = twoSpace + "Status for Development / Issue Committed For SLA not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String ClosedStatus = sla.getConfig2().replace("'", "");
            releasedField = sla.getConfig2().replace("'", "");

            //strCheckCreatedDateInsteadHistory = sla.getConfig3().replace("'", "");
            String strCheckCommittedField = sla.getConfig3().replace("'", "");

            if (strCheckCommittedField.equals("") || !strCheckCommittedField.equals("Y")) {
                strCheckCommittedField = "N";
            }

            String strCheckClosedField = sla.getConfig4().replace("'", "");
            if (strCheckClosedField.equals(""))
            {
                strCheckClosedField = "N";
            }

            //This is not required - Commented
//            committedField = sla.getConfig5().replace("'", "");
//            if (committedField.isEmpty()) {
//                committedField = "Created";
//            }

            strFixedVersionDateFormat = sla.getConfig5(); //Date format value is only required in Jira. For ADO it is used to get the Link Types from the fixed version. So Implements is different in ADO and Jira

            String strIncludeCommittedDate = sla.getInput1().replace("'", "");
            if (!strIncludeCommittedDate.equals("Y"))
            {
                strIncludeCommittedDate = "N";
            }

            strCheckHolidays = sla.getInput2().replace("'", "");
            strCheckWeekend = sla.getInput3().replace("'", "");

            if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
                strCheckHolidays = "N";
            }

            if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
                strCheckWeekend = "N";
            }

            strFixedVersionFormat = sla.getInput4().replace("'", "");
            if (!strFixedVersionFormat.isEmpty())
            {
                String[] arrFixedFormat = strFixedVersionFormat.split(",");
                if (arrFixedFormat != null && arrFixedFormat.length > 0) {
                    if (arrFixedFormat.length >= 1) {
                        if (!arrFixedFormat[0].isEmpty()) {
                            try {
                                majorRelease = Integer.parseInt(arrFixedFormat[0]);
                            } catch (Exception exmajor) {

                            }
                        }
                    }

                    if (arrFixedFormat.length >= 2) {
                        if (!arrFixedFormat[1].isEmpty()) {
                            try {
                                updateRelease = Integer.parseInt(arrFixedFormat[1]);
                            } catch (Exception exUpdate) {

                            }
                        }
                    }

                    if (arrFixedFormat.length >= 3) {
                        if (!arrFixedFormat[2].isEmpty()) {
                            try {
                                hotfixRelease = Integer.parseInt(arrFixedFormat[2]);
                            } catch (Exception exmajor) {

                            }
                        }
                    }
                }
            }

            strFixedVersionSplitter = sla.getInput5().replace("'", ""); //Splitter is used only in Jira (different implementation) but in ADO this is used check whether Major Baseline version is calculated separately

//            if (project.getProjectsource().equals(SourceKey.JIRA.value))
//            {
//                if (strFixedVersionDateFormat.equals(""))
//                {
//                    message = twoSpace + "Fixed version Date Format for the SLA not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql= " + sla.getDenojql();
//                }
//
//                if (denourl.equals("")) {
//                    message = twoSpace + "demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, true, pageSize);
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Issue Key", "Type", "Issue Status", "Committed Date", "Release Date", "TotalDuration", "Status", "All Fixed Versions", "Released Fixed Versions", "Multiple Fixed Version", "Fixed Version Type"});
//                }
//
//                if (project.getDetailedLogRequired().equals("Y")) {
//                    dataLinesDetailed.add(new String[]
//                            {"Key", "Type", "Final Issue Status", "Fixed Version(s)/Release Date", "Released Fixed Versions/Date", "Multiple Fixed Version", "CreatedDate", "CommitedDate", "ReleasedDate", "Eligible"});
//                }
//
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                List<Issue> fixedVersionIssues = new ArrayList<>();
//                String multipleFixedVersion = "";
//
//                if (denoIssue != null & denoIssue.size() > 0)
//                {
//                    for (Issue issue : denoIssue)
//                    {
//                        committedDate = null;
//                        closeedDate = null;
//                        issueActivityDate = null;
//                        Historylist = null;
//                        multipleFixedVersion = "";
//
//                        if (issue != null)
//                        {
//                            try
//                            {
//                                if (issue.getChangelog().getHistories() != null)
//                                {
//                                    Historylist = issue.getChangelog().getHistories();
//                                }
//
//                                if (strCheckCommittedField.equals("N"))
//                                {
//                                    issueActivityDate = util.getIssueActivityDate(issue.getKey(), Historylist, inDevelopmentStatus, sourcedateFormat, "status");
//
//                                    if (issueActivityDate != null) {
//                                        if (issueActivityDate.getRequestedDate() != null) {
//                                            committedDate = issueActivityDate.getRequestedDate();
//                                        }
//                                    }
//                                }
//                                else
//                                {
//                                    //Take the created date as committed date as it will be in the initial status
//                                    strCommittedDate = "";
//                                    if (strCheckCommittedField.toLowerCase().equals("created")) {
//                                        strCommittedDate = issue.getFields().getCreated();
//                                    } else if (strCheckCommittedField.toLowerCase().equals("updated")) {
//                                        strCommittedDate = issue.getFields().getUpdated();
//                                    } else if (strCheckCommittedField.toLowerCase().equals("resolution")) {
//                                        strCommittedDate = issue.getFields().getResolutiondate();
//                                    }
//
//                                    if (!strCommittedDate.isEmpty())
//                                    {
//                                        if (util.isDateValid(strCommittedDate, sourcedateFormat) == true) {
//                                            committedDate = util.ConvertToDate(strCommittedDate, sourcedateFormat);
//                                        }
//                                    }
//                                }
//
//                                //Get the Closed Date i.e. when the Issue Released from the fixed Version  ---- Start
//                                //We should get all the fixed version associated which is released and map for which fixed version the
//                                //the issue is mapped and take its release date.
//                                if (issue.getFields().getFixVersions() != null) {
//                                    fixedVersions = issue.getFields().getFixVersions();
//                                }
//
//                                strtotalFixedVersions = "";
//                                for (FixedVersion fv : fixedVersions)
//                                {
//                                    strtotalFixedVersions = strtotalFixedVersions + fv.getName() + "#";
//                                }
//
//                                strClosedDate = "";
//                                closeedDate = null;
//
//                                //Get the Closed date from Fixed version
//                                //Code to get release date using fixed Version -- Start
//                                //No Checking of where to get the closed. Take the field directly
//                                matchedFixedVersion = new ArrayList<>();
//                                strreleasedFixedVersions = "";
//
//                                List<FixedVersion> releasedFixedVersions = fixedVersions.stream().filter(x -> x.isReleased() == true).collect(Collectors.toList());
//
//                                if (releasedFixedVersions != null && releasedFixedVersions.size() > 0)
//                                {
//                                    for (FixedVersion fixedVersion : releasedFixedVersions)
//                                    {
//                                        if (fixedVersion != null) {
//                                            fixedVersionIssues = new ArrayList<>();
//                                            fixedVersionIssues = null;
//
//                                            FixedVersionIssue fIssue = new FixedVersionIssue();
//                                            fIssue = null;
//                                            if (fixedVersionData != null && fixedVersionData.size() > 0) {
//                                                List<FixedVersionIssue> fixedIssues = fixedVersionData.stream().filter(x -> x.getFixedVersion().getName().equals(fixedVersion.getName())).collect(Collectors.toList());
//                                                if (fixedIssues != null && fixedIssues.size() > 0)
//                                                {
//                                                    fIssue = fixedIssues.get(0);
//                                                }
//                                            }
//
//                                            if (fIssue == null) {
//                                                String URL = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND fixVersion in ('" + fixedVersion.getName() + "')";
//                                                fixedVersionIssues = iJiraDataService.getIssuesUsingJQL(userName, password, URL, "", false, false, pageSize);
//                                                if (fixedVersionIssues != null && fixedVersionIssues.size() > 0) {
//                                                    FixedVersionIssue fxIssue = new FixedVersionIssue();
//                                                    fxIssue.setFixedVersion(fixedVersion);
//                                                    fxIssue.setIssueList(fixedVersionIssues);
//                                                    fixedVersionData.add(fxIssue);
//                                                }
//                                            } else {
//                                                fixedVersionIssues = fIssue.getIssueList();
//                                            }
//
//                                            if (fixedVersionIssues != null && fixedVersionIssues.size() > 0) {
//                                                if (fixedVersionIssues.stream().filter(x -> x.getKey().equals(issue.getKey())).count() > 0) {
//                                                    String strFixedVersionReleaseDate = "";
//                                                    if (fixedVersion.getReleaseDate() != null) {
//                                                        strFixedVersionReleaseDate = fixedVersion.getReleaseDate();
//                                                    }
//
//                                                    strreleasedFixedVersions = strreleasedFixedVersions + fixedVersion.getName() + "~" + strFixedVersionReleaseDate + "#";
//                                                    matchedFixedVersion.add(fixedVersion);
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//
//                                closeedDate = null;
//                                if (matchedFixedVersion != null && matchedFixedVersion.size() > 0)
//                                {
//                                    if (matchedFixedVersion.size() > 1)
//                                    {
//                                        String key = issue.getKey();
//                                        multipleFixedVersion = "";
//                                        String strFDate = "";
//                                        for (FixedVersion f : matchedFixedVersion)
//                                        {
//                                            if (f.getReleaseDate() != null)
//                                            {
//                                                strFDate = f.getReleaseDate();
//                                            }
//                                            multipleFixedVersion = multipleFixedVersion + f.getName() + "~" + strFDate + "#";
//                                        }
//                                    }
//
//                                    //Take the first Record
//                                    FixedVersion fv = matchedFixedVersion.get(0);
//                                    if (fv != null) {
//                                        if (fv.getReleaseDate() != null) {
//                                            closeedDate = util.ConvertToDate(fv.getReleaseDate(), strFixedVersionDateFormat);
//                                        }
//                                    }
//                                }
//
//                                //Take the Closed date from Released field (Custom Field)
//                                if (!strCheckClosedField.isEmpty())
//                                {
//                                    if (strCheckClosedField.equals("customfield_14502"))
//                                    {
//                                        strClosedDate = issue.getFields().getCustomfield_14502();
//                                    }
//
//                                    closeedDate = util.ConvertToDate(strClosedDate, releaseDateFormat);
//                                }
//                                //Get the Closed Date i.e. when the Issue Released from the fixed Version  ---- Start
//
//                                if (committedDate != null && closeedDate != null) {
//                                    eligibleissue = new AvgCycleJira();
//                                    eligibleissue.setKey(issue.getKey());
//                                    eligibleissue.setType(issue.getFields().getIssuetype().getName());
//                                    eligibleissue.setIssueStatus(issue.getFields().getStatus().getName());
//                                    eligibleissue.setReleaseDate(util.ConvertDateToString(closeedDate, sourcedateFormat));
//                                    eligibleissue.setCommitedDate(util.ConvertDateToString(committedDate, sourcedateFormat));
//                                    eligibleissue.setAssociatedFixedVersions(strtotalFixedVersions);
//                                    eligibleissue.setMatchedFixedVersions(strreleasedFixedVersions);
//                                    eligibleissue.setMultipleFixedVersions(multipleFixedVersion);
//
//                                    //Identify the Type of fixed version if provided by the configuration
//                                    String fixedVersionType = "";
//                                    if (!strFixedVersionSplitter.isEmpty()) {
//                                        if (!eligibleissue.getMatchedFixedVersions().isEmpty()) {
//                                            String mFixedVersion = eligibleissue.getMatchedFixedVersions();
//                                            String[] arrchkFixedVersion = mFixedVersion.split("#");
//                                            if (arrchkFixedVersion != null && arrchkFixedVersion.length > 0) {
//                                                for (String strFixedVersiondata : arrchkFixedVersion) {
//                                                    if (!strFixedVersiondata.isEmpty()) {
//                                                        String[] arrFinalFV = strFixedVersiondata.split("~");
//                                                        if (arrFinalFV != null && arrFinalFV.length > 0) {
//                                                            String finalFv = "";
//                                                            if (arrFinalFV.length > 1) {
//                                                                finalFv = arrFinalFV[0];
//                                                            }
//
//                                                            if (!finalFv.isEmpty()) {
//                                                                Character chSplit = strFixedVersionSplitter.charAt(0);
//                                                                //String [] strMajorRelease = finalFv.split(strFixedVersionSplitter);
//                                                                long count = finalFv.chars().filter(ch -> ch == chSplit).count();
//                                                                if (count >= 0) {
//                                                                    if (majorRelease >= 0) {
//                                                                        if (count == majorRelease) {
//                                                                            fixedVersionType = fixedVersionType + "Major" + ",";
//                                                                        }
//                                                                    }
//
//                                                                    if (updateRelease > 0) {
//                                                                        if (count == updateRelease) {
//                                                                            fixedVersionType = fixedVersionType + "Update" + ",";
//                                                                        }
//                                                                    }
//
//                                                                    if (hotfixRelease > 0) {
//                                                                        if (count == hotfixRelease) {
//                                                                            fixedVersionType = fixedVersionType + "Hotfix" + ",";
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                    if (!fixedVersionType.isEmpty()) {
//                                        fixedVersionType = fixedVersionType.substring(0, fixedVersionType.lastIndexOf(","));
//                                    }
//
//                                    eligibleissue.setFixedVersionType(fixedVersionType);
//                                    double processingDays = 0;
//                                    processingDays = util.GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, strIncludeCommittedDate);
//
//                                    eligibleissue.setTotalDuration((double) processingDays);
//                                    eligibleissuesLst.add(eligibleissue);
//                                }
//
//                                //Write details data file
//                                if (project.getDetailedLogRequired().equals("Y"))
//                                {
//                                     strCommittedDate = "";
//                                    if (committedDate != null)
//                                    {
//                                        strCommittedDate = util.ConvertDateToString(committedDate, sourcedateFormat);
//                                    }
//
//                                    if (closeedDate != null)
//                                    {
//                                        strClosedDate = util.ConvertDateToString(closeedDate, sourcedateFormat);
//                                    }
//
//                                    dataLinesDetailed.add(new String[]
//                                            {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getIssuetype().getName(),
//                                                    strtotalFixedVersions.replace(",", "@"),
//                                                    strreleasedFixedVersions.replace(",", "@"),
//                                                    multipleFixedVersion.replace(",", "@"),
//                                                    strCommittedDate, strCommittedDate, strClosedDate, ""});
//
//                                }
//                            }
//                            catch (Exception issueException)
//                            {
//                                String a = "";
//                            }
//                        }
//                    }
//                }
//
//                //We got our eligible issues --> Next processing
//                if (eligibleissuesLst != null && eligibleissuesLst.size() > 0)
//                {
//                    totaldenoCount = eligibleissuesLst.size();
//
//                    totalMajorDenoCount = 0;
//                    totalOtherDenoCount = 0;
//                    totalDaysMajor = 0;
//                    totaldays = 0;
//
//                    if (!strFixedVersionSplitter.isEmpty())
//                    {
//                        if (majorRelease >= 0)
//                        {
//                            totalDaysMajor = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0 && o.getFixedVersionType().contains("Major")).mapToDouble(o -> o.getTotalDuration()).sum();
//                            totaldays = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0 && !o.getFixedVersionType().contains("Major")).mapToDouble(o -> o.getTotalDuration()).sum();
//
//                            totalMajorDenoCount = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0 && o.getFixedVersionType().contains("Major")).count();
//                            totalOtherDenoCount = eligibleissuesLst.stream().filter(o -> !o.getFixedVersionType().contains("Major")).count();
//                        }
//                        else
//                        {
//                            totaldays = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0).mapToDouble(o -> o.getTotalDuration()).sum();
//                            totalOtherDenoCount = eligibleissuesLst.size();
//                        }
//                    }
//                    else
//                    {
//                        totaldays = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0).mapToDouble(o -> o.getTotalDuration()).sum();
//                        totalOtherDenoCount = eligibleissuesLst.size();
//                    }
//
//                    if (majorRelease > 0 && !strFixedVersionSplitter.isEmpty()) {
//                        baselineAvgMajor = (totalDaysMajor / (double) totalMajorDenoCount);
//                    }
//
//                    baselineAvg = (totaldays / (double) totalOtherDenoCount);
//                    totaldenoCount = (int) (totalMajorDenoCount + totalOtherDenoCount);
//                    double varianceValue = ((double) percentageVariance / (double) 100);
//
//                    addition = varianceValue * baselineAvg;
//                    if (majorRelease >= 0 && !strFixedVersionSplitter.isEmpty())
//                    {
//                        additionMajor = varianceValue * baselineAvgMajor;
//                    }
//
//
//                    newBaselineAvg = baselineAvg + addition;
//                    if (majorRelease >= 0 && !strFixedVersionSplitter.isEmpty())
//                    {
//                        newBaselineAvgMajor = baselineAvgMajor + additionMajor;
//                    }
//
//                    String recordStatus = "";
//                    for (AvgCycleJira avgcycle : eligibleissuesLst)
//                    {
//                        if (majorRelease >= 0 && !strFixedVersionSplitter.isEmpty())
//                        {
//                            if (avgcycle.getFixedVersionType().contains("Major"))
//                            {
//                                if (avgcycle.getTotalDuration() <= newBaselineAvgMajor)
//                                {
//                                    recordStatus = "Met";
//                                    totalnumCount++;
//                                }
//                                else
//                                {
//                                    recordStatus = "Not Met";
//                                }
//                            }
//                            else
//                            {
//                                if (avgcycle.getTotalDuration() <= newBaselineAvg)
//                                {
//                                    recordStatus = "Met";
//                                    totalnumCount++;
//                                }
//                                else
//                                {
//                                    recordStatus = "Not Met";
//                                }
//                            }
//                        }
//                        else
//                        {
//                            if (avgcycle.getTotalDuration() <= newBaselineAvg)
//                            {
//                                recordStatus = "Met";
//                                totalnumCount++;
//                            }
//                            else
//                            {
//                                recordStatus = "Not Met";
//                            }
//                        }
//
//                        if (project.getDatafileRequired().equals("Y"))
//                        {
//                            dataLines.add(new String[]
//                                    {avgcycle.getKey(), avgcycle.getType(), avgcycle.getIssueStatus(),
//                                            avgcycle.getCommitedDate(),
//                                            avgcycle.getReleaseDate(), String.valueOf(avgcycle.getTotalDuration()), recordStatus,
//                                            avgcycle.getAssociatedFixedVersions().replace(",", "@"),
//                                            avgcycle.getMatchedFixedVersions().replace(",", "@"),
//                                            avgcycle.getMultipleFixedVersions().replace(",", "@"), avgcycle.getFixedVersionType()
//                                    });
//                        }
//                    }
//
//                    if (project.getDatafileRequired().equals("Y"))
//                    {
//                        dataLines.add(new String[]
//                                {"", "", "", "", "", "", "", "", "", ""});
//                        dataLines.add(new String[]
//                                {"", "", "", "", "", "", "", "", "", ""});
//                        dataLines.add(new String[]
//                                {"Variance Value", String.valueOf(varianceValue), "", "", "", "", "", "", "", ""});
//
//                        if (majorRelease >= 0 && !strFixedVersionSplitter.isEmpty())
//                        {
//                            dataLines.add(new String[]
//                                    {"Major Release Total Days.", String.valueOf(totalDaysMajor), "", "", "", "", "", "", "", ""});
//                            dataLines.add(new String[]
//                                    {"Major Release Total Issues.", String.valueOf(totalMajorDenoCount), "", "", "", "", "", "", "", ""});
//                            dataLines.add(new String[]
//                                    {"Major Release Base Line Avg.", String.valueOf(baselineAvgMajor), "", "", "", "", "", "", "", ""});
//                            dataLines.add(new String[]
//                                    {"Add Variance Value", String.valueOf(additionMajor), "", "", "", "", "", "", "", ""});
//                            dataLines.add(new String[]
//                                    {"Major Baseline Average with (" + String.valueOf(percentageVariance) + "%) Variance", String.valueOf(newBaselineAvgMajor), "", "", "", "", "", "", "", ""});
//                        }
//
//                        dataLines.add(new String[]
//                                {"", "", "", "", "", "", "", "", "", ""});
//                        dataLines.add(new String[]
//                                {"General Release Total Days.", String.valueOf(totaldays), "", "", "", "", "", "", "", ""});
//                        dataLines.add(new String[]
//                                {"General Release Total Issues.", String.valueOf(totalOtherDenoCount), "", "", "", "", "", "", "", ""});
//                        dataLines.add(new String[]
//                                {"General Base Line Average.", String.valueOf(baselineAvg), "", "", "", "", "", "", "", ""});
//                        dataLines.add(new String[]
//                                {"Add Variance Value", String.valueOf(addition), "", "", "", "", "", "", "", ""});
//                        dataLines.add(new String[]
//                                {"General Baseline Average with (" + String.valueOf(percentageVariance) + "%) Variance", String.valueOf(newBaselineAvg), "", "", "", "", "", "", "", ""});
//                    }
//                }
//
//                if (dataLinesDetailed.size() > 0)
//                {
//                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
//                    try
//                    {
//                        boolean csvStatus = util.WriteToCSv(dataLinesDetailed, dataFileName1);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + dataFileName1;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                message += newLine + twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
//                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back
//
//                if (totaldenoCount == 0 && totalnumCount == 0) {
//                    totaldenoCount = 1;
//                    totalnumCount = 1;
//                } else if (totaldenoCount == 0) {
//                    totaldenoCount = 1;
//                }
//
//                actualValue = 0;
//                actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//                message += newLine + twoSpace + " Baselined Cycle time = " + String.valueOf(baselineAvg);
//                message += newLine + twoSpace + " Variance(" + String.valueOf(percentageVariance) + "%) = " + String.valueOf(newBaselineAvg);
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, expectedsla, minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"", "", "", "", "", "", "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"Denominator", String.valueOf(totaldenoCount), "", "", "", "", "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"Numerator", String.valueOf(totalnumCount), "", "", "", "", "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"Actual", String.valueOf(actualValue), "", "", "", "", "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"Min SLA", String.valueOf(minsla), "", "", "", "", "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"Expected SLA", String.valueOf(expectedsla), "", "", "", "", "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"SLA Status", String.valueOf(slaStatus), "", "", "", "", "", "", "", ""});
//                }
//
//                if (dataLines.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Issue Key", "Type", "Issue Status", "Committed Date", "Release Date", "TotalDuration", "Fixed Versions", "Fixed Version Type", "Eligible"});
                }

                if (project.getDetailedLogRequired().equals("Y")) {
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type", "Final Issue Status", "Fixed Version", "Fixed Version Type", "CreatedDate", "CommitedDate", "ReleasedDate"});
                }

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty()) {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                String itemQuery = sla.getNumjql();
                String itemLinkURL = project.getLinkItemUrl();
                String itemUrl = project.getItemUrl();
                String linkitemTypes = sla.getConfig5(); //For ADO to pull what type of link item from the fixed version. Comman delimited
                String strIsmajorReleaseCalculationRequired = sla.getInput5().replace("'", ""); //ADO to check whether to calculate major baseline average separately

                if (itemQuery.isEmpty())
                {
                    message = twoSpace + "Item Query is not available in numJQL, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (itemLinkURL.isEmpty())
                {
                    message = twoSpace + "Link Item URL is not available in numJQL, please check your project configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (itemUrl.isEmpty())
                {
                    message = twoSpace + "Item URL is not available in numJQL, please check your project configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (linkitemTypes.isEmpty())
                {
                    message = twoSpace + "Link Item Type is not available in numJQL, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (strIsmajorReleaseCalculationRequired.equals("") || !strIsmajorReleaseCalculationRequired.equals("Y"))
                {
                    strIsmajorReleaseCalculationRequired = "N";
                }

                String[] arrLinkItemTypes = linkitemTypes.split(",");
                linkitemTypes = "";
                if (arrLinkItemTypes != null && arrLinkItemTypes.length > 0)
                {
                    for (int i = 0; i < arrLinkItemTypes.length; i++)
                    {
                        linkitemTypes = linkitemTypes + "'" + arrLinkItemTypes[i] + "',";
                    }
                }

                linkitemTypes = linkitemTypes.substring(0, linkitemTypes.lastIndexOf(","));

                List<WorkItem> workitems = null;
                List<RevisionValue> revisionValues = new ArrayList<>();
                RevisionFields revisionFields = new RevisionFields();
                RevisionValue revisionValue = new RevisionValue();

                String ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                workitems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", false, true, true, false, "", "", "", 100);

                message = "";
                totaldenoCount = 0;
                totalnumCount = 0;
                List<WorkItem> fixedVersionAdo = new ArrayList<>();

                if(workitems !=null && workitems.size() >0)
                {
                    fixedVersionAdo = new ArrayList<>();
                    for(WorkItem witem : workitems)
                    {
                        String id = witem.getId();
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
                                    if (revisionValues != null & revisionValues.size() > 0)
                                    {
                                        IssueActivityDate issueActivityDates = util.getADOWorkItemActivityDate(witem.getId(), revisionValues, inDevelopmentStatus, sourcedateFormat);
                                        if (issueActivityDates != null)
                                        {
                                            if (issueActivityDates.getRequestedDate() != null)
                                            {
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
                                }

                                if (!strCommittedDate.isEmpty())
                                {
                                    //Check whether the date is within the measurement period
                                    workingDate = util.ConvertStringToDateForZFormat(strCommittedDate);
                                    if (workingDate != null)
                                    {
                                        if ((workingDate.compareTo(dtFromDate) >= 0 && workingDate.compareTo(dtToDate) <= 0) == true)
                                        {
                                            committedDate = workingDate;
                                        }
                                    }
                                }

                                String fixedVersionTitle = "";
                                String fixedVersionType = "";

                                strClosedDate = "";
                                closeedDate = null;
                                WorkItem fvAdo = null;
                                List<WorkItem> fixedVersionAdoData = null;
                                //Workitem should have fixed version attached to it in the fixed version field
                                if (witem.getFields().getFixedVersions() != null)
                                {
                                    if (!witem.getFields().getFixedVersions().isEmpty())
                                    {
                                        fixedVersionTitle = witem.getFields().getFixedVersions();
                                        //Get the Released Date
                                        if (strCheckClosedField.equals("N"))
                                        {
                                            //Get the Release Date form the Fixed Version
                                            if (fixedVersionAdo != null && fixedVersionAdo.size() > 0)
                                            {
                                                String finalFixedVersionTitle = fixedVersionTitle;
                                                fixedVersionAdoData = fixedVersionAdo.stream().filter(x->x.getFields().getTitle().equals(finalFixedVersionTitle)).collect(Collectors.toList());
                                                //There should be only one record as one fixed version assigned to one ticket

                                                if (fixedVersionAdoData != null && fixedVersionAdoData.size() > 0)
                                                {
                                                    fvAdo = fixedVersionAdoData.get(0);
                                                }
                                            }

                                            if (fvAdo == null) //Get the fixed version using its title
                                            {
                                                //Get it from ADO and push it to the collection for further use
                                                String itemQueryIndividual = itemQuery.replace("WORKITEMTITLEVALUE", fixedVersionTitle);
                                                String itemADOQuery = "{\n" +
                                                        "  \"query\": \"" + itemQueryIndividual + "\"\n" + "}";

                                                List<WorkItem> workItemFixedVersion = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), itemADOQuery, "POST", false, true, true, true, linkitemTypes, itemLinkURL, itemUrl, 1000);
                                                if (workItemFixedVersion != null && workItemFixedVersion.size() > 0) //There will be only one record
                                                {
                                                    fvAdo = workItemFixedVersion.get(0);
                                                }

                                                if (fvAdo != null) //add it to the list
                                                {
                                                    if (fixedVersionAdo == null)
                                                    {
                                                        fixedVersionAdo = new ArrayList<>();
                                                    }
                                                    fixedVersionAdo.add(fvAdo);
                                                }
                                            }

                                            if (fvAdo != null)
                                            {
                                                //THis is hard Coded. Please check with aakash what is the field to get
                                                //The Fixed version Type from the record
                                                if (fvAdo.getFields().getTitle().equals("15.1")) //Needs to removed
                                                {
                                                    fixedVersionType = "Major";
                                                }
                                                else
                                                {
                                                    fixedVersionType = "Update";
                                                }

                                                //fixedVersionType = "Major"; //Get it from the fields Fixed Version Type field which is not currently available
                                                //Verify whether the WorkItem is available with the Link item or not
                                                if (fvAdo.getChildLinks() != null && fvAdo.getChildLinks().size() > 0)
                                                {
                                                    if (fvAdo.getChildLinks().stream().filter(x->x.getId().equals(witem.getId())).count() > 0)
                                                    {
                                                        //The processing WorkItem is already available in the associated Child links.
                                                        //So this is the correct Fixed version with which the Workitem is relased
                                                        //Take the Closed Date of the fixed version as released date if it is available.
                                                        //If Closed date is available this means the Fixed version is released.
                                                        if (fvAdo.getFields().getClosedDate() != null )
                                                        {
                                                            if (!fvAdo.getFields().getClosedDate().isEmpty())
                                                            {
                                                                strClosedDate = fvAdo.getFields().getClosedDate();
                                                                closeedDate = util.ConvertStringToDateForZFormat(strClosedDate);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else
                                        {
                                            //Get it from the WorkItem Field
                                        }
                                    }
                                }

                                if (committedDate != null && closeedDate != null)
                                {
                                    eligibleissue = new AvgCycleJira();
                                    eligibleissue.setKey(witem.getId());
                                    eligibleissue.setType(witem.getFields().getWorkItemType());
                                    eligibleissue.setIssueStatus(witem.getFields().getState());
                                    eligibleissue.setReleaseDate(strClosedDate);
                                    eligibleissue.setCommitedDate(strCommittedDate);
                                    eligibleissue.setAssociatedFixedVersions(fixedVersionTitle);
                                    eligibleissue.setFixedVersionType(fixedVersionType);
                                    double processingDays = 0;
                                    processingDays = util.GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, strIncludeCommittedDate);

                                    eligibleissue.setTotalDuration((double) processingDays);
                                    eligibleissuesLst.add(eligibleissue);
                                }

                                //Write the Detailed Log File
                                if (project.getDetailedLogRequired().equals("Y"))
                                {
                                    String strCreatedDate = witem.getFields().getCreatedDate();
                                    dataLinesDetailed.add(new String[]
                                            {witem.getId(),
                                                    witem.getFields().getWorkItemType(),
                                                    witem.getFields().getState(),
                                                    fixedVersionTitle,
                                                    fixedVersionType,
                                                    strCreatedDate,
                                                    strCommittedDate,
                                                    strClosedDate,
                                            });
                                }
                            }
                            catch (Exception ex)
                            {
                                String pp = "";
                            }
                        }
                    }
                }

                //We got our eligible issues --> Next processing
                if (eligibleissuesLst != null && eligibleissuesLst.size() > 0)
                {
                    totaldenoCount = eligibleissuesLst.size();

                    totalMajorDenoCount = 0;
                    totalOtherDenoCount = 0;

                    totalDaysMajor = 0;
                    totaldays = 0;

                    if (strIsmajorReleaseCalculationRequired.equals("Y"))
                    {
                        totalDaysMajor = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0 && o.getFixedVersionType().contains("Major")).mapToDouble(o -> o.getTotalDuration()).sum();
                        totaldays = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0 && !o.getFixedVersionType().contains("Major")).mapToDouble(o -> o.getTotalDuration()).sum();

                        totalMajorDenoCount = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0 && o.getFixedVersionType().contains("Major")).count();
                        totalOtherDenoCount = eligibleissuesLst.stream().filter(o -> !o.getFixedVersionType().contains("Major")).count();
                    }
                    else
                    {
                        totaldays = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0).mapToDouble(o -> o.getTotalDuration()).sum();
                        totalOtherDenoCount = eligibleissuesLst.size();
                    }

                    if (strIsmajorReleaseCalculationRequired.equals("Y")) {
                        baselineAvgMajor = (totalDaysMajor / (double) totalMajorDenoCount);
                    }

                    baselineAvg = (totaldays / (double) totalOtherDenoCount);
                    totaldenoCount = (int) (totalMajorDenoCount + totalOtherDenoCount);
                    double varianceValue = ((double) percentageVariance / (double) 100);

                    addition = varianceValue * baselineAvg;
                    if (strIsmajorReleaseCalculationRequired.equals("Y"))
                    {
                        additionMajor = varianceValue * baselineAvgMajor;
                    }

                    newBaselineAvg = baselineAvg + addition;
                    if (strIsmajorReleaseCalculationRequired.equals("Y"))
                    {
                        newBaselineAvgMajor = baselineAvgMajor + additionMajor;
                    }

                    String recordStatus = "";
                    for (AvgCycleJira avgcycle : eligibleissuesLst)
                    {
                        if (strIsmajorReleaseCalculationRequired.equals("Y"))
                        {
                            if (avgcycle.getFixedVersionType().contains("Major"))
                            {
                                if (avgcycle.getTotalDuration() <= newBaselineAvgMajor)
                                {
                                    recordStatus = "Met";
                                    totalnumCount++;
                                }
                                else
                                {
                                    recordStatus = "Not Met";
                                }
                            }
                            else
                            {
                                if (avgcycle.getTotalDuration() <= newBaselineAvg)
                                {
                                    recordStatus = "Met";
                                    totalnumCount++;
                                }
                                else
                                {
                                    recordStatus = "Not Met";
                                }
                            }
                        }
                        else
                        {
                            if (avgcycle.getTotalDuration() <= newBaselineAvg)
                            {
                                recordStatus = "Met";
                                totalnumCount++;
                            }
                            else
                            {
                                recordStatus = "Not Met";
                            }
                        }

                        if (project.getDatafileRequired().equals("Y"))
                        {
                            dataLines.add(new String[]
                                    {avgcycle.getKey(), avgcycle.getType(), avgcycle.getIssueStatus(),
                                            avgcycle.getCommitedDate(),
                                            avgcycle.getReleaseDate(),
                                            String.valueOf(avgcycle.getTotalDuration()),
                                            avgcycle.getAssociatedFixedVersions().replace(",", "@"),
                                            avgcycle.getFixedVersionType(),
                                            recordStatus
                                    });
                        }
                    }

                    if (project.getDatafileRequired().equals("Y"))
                    {
                        dataLines.add(new String[]
                                {"", "", "", "", "", "", "", "", ""});
                        dataLines.add(new String[]
                                {"", "", "", "", "", "", "", "", ""});
                        dataLines.add(new String[]
                                {"Variance Value", String.valueOf(varianceValue), "", "", "", "", "", "", ""});

                        if (strIsmajorReleaseCalculationRequired.equals("Y"))
                        {
                            dataLines.add(new String[]
                                    {"Major Release Total Days.", String.valueOf(totalDaysMajor), "", "", "", "", "", "", ""});
                            dataLines.add(new String[]
                                    {"Major Release Total Issues.", String.valueOf(totalMajorDenoCount), "", "", "", "", "", "", ""});
                            dataLines.add(new String[]
                                    {"Major Release Base Line Avg.", String.valueOf(baselineAvgMajor), "", "", "", "", "", "", ""});
                            dataLines.add(new String[]
                                    {"Add Variance Value", String.valueOf(additionMajor), "", "", "", "", "", "", ""});
                            dataLines.add(new String[]
                                    {"Major Baseline Average with (" + String.valueOf(percentageVariance) + "%) Variance", String.valueOf(newBaselineAvgMajor), "", "", "", "", "", "", ""});
                        }

                        dataLines.add(new String[]
                                {"", "", "", "", "", "", "", "", ""});
                        dataLines.add(new String[]
                                {"General Release Total Days.", String.valueOf(totaldays), "", "", "", "", "", "", ""});
                        dataLines.add(new String[]
                                {"General Release Total Issues.", String.valueOf(totalOtherDenoCount), "", "", "", "", "", "", ""});
                        dataLines.add(new String[]
                                {"General Base Line Average.", String.valueOf(baselineAvg), "", "", "", "", "", "", ""});
                        dataLines.add(new String[]
                                {"Add Variance Value", String.valueOf(addition), "", "", "", "", "", "", ""});
                        dataLines.add(new String[]
                                {"General Baseline Average with (" + String.valueOf(percentageVariance) + "%) Variance", String.valueOf(newBaselineAvg), "", "", "", "", "", "", ""});
                    }
                }

                if (dataLinesDetailed.size() > 0)
                {
                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
                    try
                    {
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

                if (totaldenoCount == 0 && totalnumCount == 0) {
                    totaldenoCount = 1;
                    totalnumCount = 1;
                } else if (totaldenoCount == 0) {
                    totaldenoCount = 1;
                }

                actualValue = 0;
                actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
                message += newLine + twoSpace + " Baselined Cycle time = " + String.valueOf(baselineAvg);
                message += newLine + twoSpace + " Variance(" + String.valueOf(percentageVariance) + "%) = " + String.valueOf(newBaselineAvg);
                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);

                slaStatus = util.CalculateFinalSLAValueV1(actualValue, expectedsla, minsla);
                message += newLine + twoSpace + " Status = " + slaStatus;

                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"", "", "", "", "", "", "", "", ""});
                    dataLines.add(new String[]
                            {"Denominator", String.valueOf(totaldenoCount), "", "", "", "", "", "", ""});
                    dataLines.add(new String[]
                            {"Numerator", String.valueOf(totalnumCount), "", "", "", "", "", "", ""});
                    dataLines.add(new String[]
                            {"Actual", String.valueOf(actualValue), "", "", "", "", "", "", ""});
                    dataLines.add(new String[]
                            {"Min SLA", String.valueOf(minsla), "", "", "", "", "", "", ""});
                    dataLines.add(new String[]
                            {"Expected SLA", String.valueOf(expectedsla), "", "", "", "", "", "", ""});
                    dataLines.add(new String[]
                            {"SLA Status", String.valueOf(slaStatus), "", "", "", "", "", "", ""});
                }

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
                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),ADQuery);
                status = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            return null;
        }
        catch (Exception ex)
        {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;
    }
    private ProcessedData ProcessEfficiency(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        //For JIRA We pull all the records from the
        //For ADO we pull all the data from the board - Current processing

        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);

        String baseURI = "";

        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> numIssue = new ArrayList<>();
        List<Issue> commentIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();

        IssueActivityDate issueActivityDate = new IssueActivityDate();
        List<EligibleIssueProcessEfficiency> elligibleIssues = new ArrayList<>();
        EligibleIssueProcessEfficiency eligibleissue = new EligibleIssueProcessEfficiency();
        List<IssueDateVariance> elligibleItems = new ArrayList<>();
        List<History> Historylist = new ArrayList<>();

        Date committedDate = new Date();//committeddate
        Date closeedDate = new Date();
        Date workingDate = new Date();
        String strWorkingDate = "";

        double totDenoCount = 0;
        double totNumCount = 0;

        boolean isValid = false;
        String inDevelopmentStatus = "";
        String closeStatus = "";
        String prevCloseStatus = "";
        String additionalClosedStatus = "";
        String sourcedateFormat = "";
        double actualValue = 0;
        String holidayList = "";
        String projectdateFormat = "";
        String limitFromConfig = "";
        int limit = 0;
        int variance = 0;
        List<Date> lstHolidays = new ArrayList<>();

        String fromdateFromConfig = "";
        String todateFromConfig = "";
        String dateFormatFromConfig = "";
        Date dtFromDate;
        Date dtToDate;
        String strCheckHolidays = "";
        String strCheckWeekend = "";

        String excludeTypes = "";
        String strKey = "";
        String strType = "";
        String strStatus = "";
        String strHours = "";

        String strCheckCommittedDateInField = "N";
        String strCheckClosedDateInField = "N";

        int hours = -1;
        int pageSize = 1000;
        String strPageSize = "";
        double limitVariance = 0.0;
        double newBaseLineAvg = 0.0;
        String strCommittedDate = "";
        String strClosedDate = "";
        IssueDateVariance issueDateVariance = null;

        double estimatedHours = 0;
        double actualHours = 0;
        double remainingHours = 0;

        String strOriginalEstimate = "";
        String strActualHourSpent = "";
        double totIssuesEligible = 0;
        try
        {
            strPageSize = project.getPageSize();
            if (!strPageSize.isEmpty()) {
                try {
                    pageSize = Integer.parseInt(strPageSize);
                } catch (Exception exPageSizeParse) {
                    pageSize = 1000;
                }
            }

            //Project related validation
            sourcedateFormat = project.getSourceDateFormat();
            if (sourcedateFormat.equals("")) {
                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            dateFormatFromConfig = project.getDateFormat().replace("'", "");
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

            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            //Validation for From and to days
            fromdateFromConfig = sla.getFrom().replace("'", "");
            todateFromConfig = sla.getTo().replace("'", "");
            limitFromConfig = sla.getLimit().replace("'", "");

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

            if (limitFromConfig.equals("")) {
                message = twoSpace + "Limit data Not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            List<String> lstExcludeTypes = new ArrayList<>();
            excludeTypes = sla.getConfig1(); //In ADO Project this will have only Y / N value
            if (!excludeTypes.isEmpty()) {
                if (excludeTypes.equals("Y") || excludeTypes.equals("N") || excludeTypes.equals("B"))
                {
                    //Donot populate the list as there is a special purpose of these values
                }
                else
                {
                    String[] arr = excludeTypes.split(",");
                    for (String type : arr) {
                        lstExcludeTypes.add(type);
                    }
                    excludeTypes = "B"; //Should check first from the field and if not found the check it from Closed-Committed
                }
            }

            limit = -1;
            try {
                limit = Integer.parseInt(limitFromConfig); //added
            } catch (Exception exbaseline) {
            }

            if (limit == -1) {
                message = twoSpace + "Not able to parse limit value. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (!strHours.isEmpty()) {
                hours = -1;
                try {
                    hours = Integer.parseInt(strHours); //added
                } catch (Exception exbaseline) {
                }

                if (hours == -1) {
                    hours = 8;
                }
            } else {
                hours = 8;
            }

            inDevelopmentStatus = sla.getConfig2();
            if (inDevelopmentStatus.equals("")) {
                message = twoSpace + "Status for Development / Issue Committed to Sprint not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String close = sla.getConfig3();
            if (close.equals("")) {
                message = twoSpace + "Status for close for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String[] arrClosedStatus = close.split(",");
            if (arrClosedStatus != null && arrClosedStatus.length > 2) {
                message = twoSpace + "Only Two closed status cna be mentioned for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (arrClosedStatus != null && arrClosedStatus.length >= 1) {
                closeStatus = arrClosedStatus[0];
            }

            if (arrClosedStatus != null && arrClosedStatus.length >= 2) {
                prevCloseStatus = arrClosedStatus[1];
            }

            if (closeStatus.isEmpty()) {
                message = twoSpace + "Closed Status for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            strCheckCommittedDateInField = sla.getConfig4().replace("'", "");
            strCheckClosedDateInField = sla.getConfig5().replace("'", "");

            if (strCheckCommittedDateInField.isEmpty()) {
                strCheckCommittedDateInField = "N";
            }

            if (strCheckClosedDateInField.isEmpty()) {
                strCheckClosedDateInField = "N";
            }

            if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
                strCheckHolidays = "N";
            }

            if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
                strCheckWeekend = "N";
            }

            double baselineavg = -1;
            String strbaselineavg = sla.getInput1();
            if (strbaselineavg.equals("")) {
                message = twoSpace + "Baseline average not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            try {
                baselineavg = Double.parseDouble(strbaselineavg);
            } catch (Exception exbaseline) {
            }

            if (baselineavg == -1) {
                message = twoSpace + "Not able to parse Baseline average. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            strCheckHolidays = sla.getInput2().replace("'", "");
            strCheckWeekend = sla.getInput3().replace("'", "");
            strHours = sla.getInput4().replace("'", "");

            String strIncludeCommittedDate = sla.getInput5().replace("'", "");
            if (!strIncludeCommittedDate.equals("Y")) {
                strIncludeCommittedDate = "N";
            }

//            if (project.getProjectsource().equals(SourceKey.JIRA.value))
//            {
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Issue Status", "commitedDate", "closedDate",
//                                    "Variance (In Days)", "Estimated Hours", "Total Hours"});
//                }
//
//                if (project.getDetailedLogRequired().equals("Y")) {
//                    dataLinesDetailed.add(new String[]
//                            {"Key", "Type", "Issue Status", "commitedDate", "closedDate"});
//                }
//
//                denourl = "";
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql= " + sla.getDenojql();
//                }
//
//                if (!denourl.isEmpty()) {
//                    retrievedIssues = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, true, pageSize);
//                }
//
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                double processingDays = 0d;
//                ////////// Business Logic Implementation Starts  //////////
//
//                if (retrievedIssues != null && retrievedIssues.size() > 0) {
//                    for (Issue issue : retrievedIssues) {
//                        if (issue != null) {
//                            try {
//                                committedDate = null;
//                                closeedDate = null;
//                                issueActivityDate = null;
//                                workingDate = null;
//                                String strkk = issue.getKey();
//                                Historylist = null;
//                                if (issue.getChangelog() != null) {
//                                    if (issue.getChangelog().getHistories() != null) {
//                                        Historylist = issue.getChangelog().getHistories();
//                                    }
//                                }
//
//                                isValid = true;
//                                //check if the issue type in Excluded types then reject it
//                                if (lstExcludeTypes != null && lstExcludeTypes.size() > 0) {
//                                    //Write a lamda to find if the issue type matches with exclude type then donot include it
//                                    //If match found make isValid = false;
//                                    if (lstExcludeTypes.contains(issue.getFields().getIssuetype().getName())) {
//                                        isValid = false;
//                                    }
//                                }
//
//                                if (isValid == true) {
//                                    if (strCheckCommittedDateInField.equals("N")) {
//                                        //Check it in the Hisotry because u will get some data there
//                                        issueActivityDate = util.getIssueActivityDate(issue.getKey(), Historylist, inDevelopmentStatus, sourcedateFormat, "status");
//
//                                        if (issueActivityDate != null) {
//                                            if (issueActivityDate.getRequestedDate() != null) {
//                                                //You have a date here.
//                                                workingDate = issueActivityDate.getRequestedDate();
//                                            }
//                                        }
//                                    } else {
//                                        //Take the created date as committed date as it will be in the initial status
//                                        strCommittedDate = "";
//                                        if (strCheckCommittedDateInField.equals("Created")) {
//                                            strCommittedDate = issue.getFields().getCreated();
//                                        } else if (strCheckCommittedDateInField.equals("Updated")) {
//                                            strCommittedDate = issue.getFields().getUpdated();
//                                        } else if (strCheckCommittedDateInField.equals("Resolution")) {
//                                            strCommittedDate = issue.getFields().getResolutiondate();
//                                        } else {
//                                            //Custom Field
//                                        }
//
//                                        if (!strCommittedDate.isEmpty()) {
//                                            if (util.isDateValid(strCommittedDate, sourcedateFormat) == true) {
//                                                workingDate = util.ConvertToDate(strCommittedDate, sourcedateFormat);
//                                            }
//                                        }
//                                    }
//
//                                    //Validate date - Should be between from and to
//                                    if (workingDate != null)
//                                    {
//                                        //workingDate = util.ConvertDateFromOneFormatToAnother(issueActivityDate.getRequestedDate(), sourcedateFormat, dateFormatFromConfig);
//                                        //Check if the start date is withing the measurement period
//                                        if ((workingDate.compareTo(dtFromDate) >= 0 && workingDate.compareTo(dtToDate) <= 0) == true) {
//                                            //The issue has a work starting date which is withing the measurement period
//                                            committedDate = workingDate; //This will be in the same format as per source
//                                        }
//                                    }
//
//                                    //Get the Closed Date
//                                    if (strCheckCommittedDateInField.equals("N")) {
//                                        issueActivityDate = null;
//                                        issueActivityDate = util.getIssueActivityDate(issue.getKey(), Historylist, closeStatus, sourcedateFormat, "status");
//
//                                        if (issueActivityDate != null) {
//                                            closeedDate = issueActivityDate.getRequestedDate();
//                                        }
//
//                                        //if Closed date is null, then check the previous closed status if provided to identify the Closed date. system should take the first previous status
//                                        if (closeedDate == null) {
//                                            if (!prevCloseStatus.isEmpty()) {
//                                                issueActivityDate = null;
//                                                issueActivityDate = util.getIssueActivityDate(issue.getKey(), Historylist, prevCloseStatus, sourcedateFormat, "status");
//
//                                                //In case of Previous Close status, if the additional status provided check it and if the data is there
//                                                //if data is there the take that otherwise take the previsous close status data
//                                                if (!additionalClosedStatus.isEmpty()) {
//                                                    IssueActivityDate issactdate = util.getIssueActivityDate(issue.getKey(), Historylist, additionalClosedStatus, sourcedateFormat, "status");
//                                                    if (issactdate != null) {
//                                                        issueActivityDate = issactdate;
//                                                    }
//                                                }
//
//                                                if (issueActivityDate != null) {
//                                                    closeedDate = issueActivityDate.getRequestedDate();
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        String strcloseDate = "";
//                                        if (strCheckCommittedDateInField.equals("Resolution")) {
//                                            strcloseDate = issue.getFields().getResolutiondate();
//                                        } else {
//                                            //Custom Field
//                                        }
//
//                                        if (!strcloseDate.isEmpty()) {
//                                            if (util.isDateValid(strcloseDate, sourcedateFormat) == true) {
//                                                closeedDate = util.ConvertToDate(strcloseDate, sourcedateFormat); //Need to check the format
//                                            }
//                                        }
//                                    }
//                                }
//
//                                actualHours = -1; //This is original Estimation
//                                if (committedDate != null && closeedDate != null)
//                                {
//                                    eligibleissue = new EligibleIssueProcessEfficiency();
//                                    eligibleissue.setKey(issue.getKey());
//                                    eligibleissue.setType(issue.getFields().getIssuetype().getName());
//                                    eligibleissue.setIssueStatus(issue.getFields().getStatus().getName());
//                                    eligibleissue.setDelivereddate(util.ConvertDateToString(closeedDate, sourcedateFormat));
//                                    eligibleissue.setCommitteddate(util.ConvertDateToString(committedDate, sourcedateFormat));
//
//                                    //Get the Original Estimation from the field customfield_14000
//                                    //validate this data. If you donot foound Original hour then reject
//                                    estimatedHours = -1;
//                                    actualHours = -1;
//
//                                    //Get Original Estimated Hour
//                                    if (issue.getFields().getCustomfield_14000() != null && !(issue.getFields().getCustomfield_14000().equals("")))
//                                    {
//                                        try
//                                        {
//                                            estimatedHours = Float.parseFloat(issue.getFields().getCustomfield_14000());
//                                        }
//                                        catch (Exception exactualHour)
//                                        {
//
//                                        }
//                                    }
//
//                                    //Get Original Actual Hour
//
//                                    if (excludeTypes.equals("Y"))
//                                    {
//                                        if (issue.getFields().getCustomfield_14001() != null && !(issue.getFields().getCustomfield_14001().equals("")))
//                                        {
//                                            try
//                                            {
//                                                actualHours = Float.parseFloat(issue.getFields().getCustomfield_14001());
//                                            }
//                                            catch (Exception exactualHour)
//                                            {
//
//                                            }
//                                        }
//                                    }
//
//                                    //no actual hours found, so not elligible
//                                    double totalHourstoDeliver = 0;
//                                    if (estimatedHours != -1)
//                                    {
//                                        //Also we can get the actual hours to deliver from customfield_14001. If not available then
//                                        //Get the actual hours to deliver by using Closed Date - Committed date. This is not implemented.
//                                        //Current implementation - Actual Hour to deliver = Closed date - Committed Date
//
//                                        //Find the total hours to deliver the issue
//                                        processingDays = 0;
//                                        //processingDays = util.GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, strIncludeCommittedDate);
////
////                                        if (processingDays > 0) {
////                                            totalHourstoDeliver = processingDays * hours; //Hours can be from the config
////                                            eligibleissue.setTotalDays(processingDays);
////                                        } else {
////                                            eligibleissue.setTotalDays(0);
////                                        }
//
//                                        if (excludeTypes.equals("N"))
//                                        {
//                                            actualHours = util.GetWorkingHourOrMinVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, sourcedateFormat, hours, "H");
//                                        }
//                                        else if (excludeTypes.equals("B")) //If actual Hour found in the field take it otherwise take it by taking the difference of Closed date - Committed date
//                                        {
//                                            actualHours = -1;
//                                            if (issue.getFields().getCustomfield_14001() != null && !(issue.getFields().getCustomfield_14001().equals("")))
//                                            {
//                                                try
//                                                {
//                                                    actualHours = Float.parseFloat(issue.getFields().getCustomfield_14001());
//                                                }
//                                                catch (Exception exactualHour)
//                                                {
//
//                                                }
//                                            }
//
//                                            if (actualHours == -1) //No Actual Hours in the field
//                                            {
//                                                actualHours = util.GetWorkingHourOrMinVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, sourcedateFormat, hours, "H");
//                                            }
//                                        }
//
//                                        eligibleissue.setActualhourspent(estimatedHours);
//                                        if (actualHours != -1) //Eligible issues should have committed date, closed date, Estimated hour and actual time to deliver hour
//                                        {
//                                            eligibleissue.setTotalhourstodeliver(actualHours);
//                                            elligibleIssues.add(eligibleissue);
//                                        }
//                                    }
//
//                                    if (project.getDatafileRequired().equals("Y")) {
//                                        dataLines.add(new String[]
//                                                {eligibleissue.getKey(), eligibleissue.getType(), eligibleissue.getIssueStatus(), eligibleissue.getCommitteddate(), eligibleissue.getDelivereddate(),
//                                                        "", String.valueOf(eligibleissue.getActualhourspent()), String.valueOf(eligibleissue.getTotalhourstodeliver())});
//
//                                    }
//                                }
//
//
////                                if ( excludeTypes.equals("Y")) //Here we donot require the committed and closed data as user want to read the values from the custom field
////                                {
////                                    eligibleissue = new EligibleIssueProcessEfficiency();
////                                    eligibleissue.setKey(issue.getKey());
////                                    eligibleissue.setType(issue.getFields().getIssuetype().getName());
////                                    eligibleissue.setIssueStatus(issue.getFields().getStatus().getName());
////
////                                    if (closeedDate != null)
////                                    {
////                                        eligibleissue.setDelivereddate(util.ConvertDateToString(closeedDate, sourcedateFormat));
////                                    }
////                                    else
////                                    {
////                                        eligibleissue.setDelivereddate("");
////                                    }
////
////                                    if (committedDate != null)
////                                    {
////                                        eligibleissue.setCommitteddate(util.ConvertDateToString(committedDate, sourcedateFormat));
////                                    }
////                                    else
////                                    {
////                                        eligibleissue.setCommitteddate("");
////                                    }
////
////                                    //Get the Original Estimation from the field customfield_14000
////                                    //validate this data. If you donot foound Original hour then reject
////                                    estimatedHours = -1;
////                                    actualHours = -1;
////
////                                    //Get Original Estimated Hour
////                                    if (issue.getFields().getCustomfield_14000() != null && !(issue.getFields().getCustomfield_14000().equals("")))
////                                    {
////                                        try
////                                        {
////                                            estimatedHours = Float.parseFloat(issue.getFields().getCustomfield_14000());
////                                        }
////                                        catch (Exception exactualHour)
////                                        {
////
////                                        }
////                                    }
////
////                                    //Get Original Actual Hour
////                                    if (excludeTypes.equals("Y"))
////                                    {
////                                        if (issue.getFields().getCustomfield_14001() != null && !(issue.getFields().getCustomfield_14001().equals("")))
////                                        {
////                                            try
////                                            {
////                                                actualHours = Float.parseFloat(issue.getFields().getCustomfield_14001());
////                                            }
////                                            catch (Exception exactualHour)
////                                            {
////
////                                            }
////                                        }
////                                    }
////
////                                    //no actual hours found, so not elligible
////                                    if (estimatedHours != -1 && actualHours != -1)
////                                    {
////                                        eligibleissue.setActualhourspent(estimatedHours);
////                                        eligibleissue.setTotalhourstodeliver(actualHours);
////                                        elligibleIssues.add(eligibleissue);
////                                    }
////
////                                    if (project.getDatafileRequired().equals("Y"))
////                                    {
////                                        dataLines.add(new String[]
////                                                {eligibleissue.getKey(), eligibleissue.getType(), eligibleissue.getIssueStatus(), eligibleissue.getCommitteddate(), eligibleissue.getDelivereddate(),
////                                                        "", String.valueOf(eligibleissue.getActualhourspent()), String.valueOf(eligibleissue.getTotalhourstodeliver())});
////
////                                    }
////                                }
////                                else
////                                {
////                                    if (committedDate != null && closeedDate != null)
////                                    {
////                                        eligibleissue = new EligibleIssueProcessEfficiency();
////                                        eligibleissue.setKey(issue.getKey());
////                                        eligibleissue.setType(issue.getFields().getIssuetype().getName());
////                                        eligibleissue.setIssueStatus(issue.getFields().getStatus().getName());
////                                        eligibleissue.setDelivereddate(util.ConvertDateToString(closeedDate, sourcedateFormat));
////                                        eligibleissue.setCommitteddate(util.ConvertDateToString(committedDate, sourcedateFormat));
////
////                                        //Get the Original Estimation from the field customfield_14000
////                                        //validate this data. If you donot foound Original hour then reject
////                                        estimatedHours = -1;
////                                        actualHours = -1;
////
////                                        //Get Original Estimated Hour
////                                        if (issue.getFields().getCustomfield_14000() != null && !(issue.getFields().getCustomfield_14000().equals("")))
////                                        {
////                                            try
////                                            {
////                                                estimatedHours = Float.parseFloat(issue.getFields().getCustomfield_14000());
////                                            }
////                                            catch (Exception exactualHour)
////                                            {
////
////                                            }
////                                        }
////
////                                        //Get Original Actual Hour
////
////                                        if (excludeTypes.equals("Y"))
////                                        {
////                                            if (issue.getFields().getCustomfield_14001() != null && !(issue.getFields().getCustomfield_14001().equals("")))
////                                            {
////                                                try
////                                                {
////                                                    actualHours = Float.parseFloat(issue.getFields().getCustomfield_14001());
////                                                }
////                                                catch (Exception exactualHour)
////                                                {
////
////                                                }
////                                            }
////                                        }
////
////                                        //no actual hours found, so not elligible
////                                        double totalHourstoDeliver = 0;
////                                        if (estimatedHours != -1)
////                                        {
////                                            //Also we can get the actual hours to deliver from customfield_14001. If not available then
////                                            //Get the actual hours to deliver by using Closed Date - Committed date. This is not implemented.
////                                            //Current implementation - Actual Hour to deliver = Closed date - Committed Date
////
////                                            //Find the total hours to deliver the issue
////                                            processingDays = 0;
////                                            //processingDays = util.GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, strIncludeCommittedDate);
//////
//////                                        if (processingDays > 0) {
//////                                            totalHourstoDeliver = processingDays * hours; //Hours can be from the config
//////                                            eligibleissue.setTotalDays(processingDays);
//////                                        } else {
//////                                            eligibleissue.setTotalDays(0);
//////                                        }
////
////                                            if (excludeTypes.equals("N"))
////                                            {
////                                                actualHours = util.GetWorkingHourOrMinVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, sourcedateFormat, hours, "H");
////                                            }
////                                            else if (excludeTypes.equals("B")) //If actual Hour found in the field take it otherwise take it by taking the difference of Closed date - Committed date
////                                            {
////                                                actualHours = -1;
////                                                if (issue.getFields().getCustomfield_14001() != null && !(issue.getFields().getCustomfield_14001().equals("")))
////                                                {
////                                                    try
////                                                    {
////                                                        actualHours = Float.parseFloat(issue.getFields().getCustomfield_14001());
////                                                    }
////                                                    catch (Exception exactualHour)
////                                                    {
////
////                                                    }
////                                                }
////
////                                                if (actualHours == -1) //No Actual Hours in the field
////                                                {
////                                                    actualHours = util.GetWorkingHourOrMinVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, sourcedateFormat, hours, "H");
////                                                }
////                                            }
////
////                                            eligibleissue.setActualhourspent(estimatedHours);
////                                            if (actualHours != -1) //Eligible issues should have committed date, closed date, Estimated hour and actual time to deliver hour
////                                            {
////                                                eligibleissue.setTotalhourstodeliver(actualHours);
////                                                elligibleIssues.add(eligibleissue);
////                                            }
////                                        }
////
////                                        if (project.getDatafileRequired().equals("Y")) {
////                                            dataLines.add(new String[]
////                                                    {eligibleissue.getKey(), eligibleissue.getType(), eligibleissue.getIssueStatus(), eligibleissue.getCommitteddate(), eligibleissue.getDelivereddate(),
////                                                            "", String.valueOf(eligibleissue.getActualhourspent()), String.valueOf(eligibleissue.getTotalhourstodeliver())});
////
////                                        }
////                                    }
////
////                                }
//                            }
//                            catch (Exception exIssueError)
//                            {
//                                String message1 = twoSpace + "Error : " + exIssueError.getMessage() + " for the issue :" + issue.getKey();
//                                status = util.WriteToFile(project.getLogFile(), message1);
//                            }
//
//                            if (project.getDetailedLogRequired().equals("Y")) {
//                                Date testDate = new Date();
//                                testDate = null;
//
//                                if (closeedDate == null) {
//                                    issueActivityDate = null;
//                                    issueActivityDate = util.getIssueActivityDate(issue.getKey(), Historylist, closeStatus, sourcedateFormat, "status");
//
//                                    if (issueActivityDate != null) {
//                                        testDate = issueActivityDate.getRequestedDate();
//                                    }
//                                } else {
//                                    testDate = closeedDate;
//                                }
//
//                                strCommittedDate = util.ConvertDateToString(workingDate, sourcedateFormat);
//                                strClosedDate = util.ConvertDateToString(testDate, sourcedateFormat);
//
//                                dataLinesDetailed.add(new String[]
//                                        {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName(), strCommittedDate, strClosedDate});
//                            }
//                        }
//                    }
//                }
//
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                double denoCount = 0d;
//                double numcount = 0d;
//
//                if (elligibleIssues != null && elligibleIssues.size() > 0) {
//                    for (EligibleIssueProcessEfficiency eligibleIssueProcessEfficiency : elligibleIssues) {
//                        numcount += eligibleIssueProcessEfficiency.getActualhourspent();
//                        denoCount += eligibleIssueProcessEfficiency.getTotalhourstodeliver();
//                    }
//
//                    totIssuesEligible = elligibleIssues.size();
//                    totDenoCount = (denoCount / totIssuesEligible);
//                    totNumCount = (numcount / totIssuesEligible);
//                }
//
//                message += newLine + twoSpace + " Average of Total Time Taken to deliver = " + totDenoCount;
//                message += newLine + twoSpace + " Average of Actual time spent = " + totNumCount;
//
//                if (dataLinesDetailed.size() > 0) {
//                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLinesDetailed, dataFileName1);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + dataFileName1;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                double denoCountActual = totDenoCount; //This is to hold the old value of deno Count so that it can be send back
//                double numCountActual = totNumCount;  //This is to hold the old value of num Count so that it can be send back
//
//                if (totDenoCount == 0 && totNumCount == 0) {
//                    totDenoCount = 1;
//                    totNumCount = 1;
//                } else if (totDenoCount == 0) {
//                    totDenoCount = 1;
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"", "", "", "", "", "", "", ""});
//
//                    dataLines.add(new String[]
//                            {"Avg Value", "", "", "", "", "", String.valueOf(totNumCount), String.valueOf(totDenoCount)});
//                }
//
//                limitVariance = baselineavg * ((double)limit/(double)100);
//
//                newBaseLineAvg = baselineavg - limitVariance;
//                message += newLine + twoSpace + " Baseline Average = " + String.valueOf(baselineavg);
//                message += newLine + twoSpace + " Process Variance = " + String.valueOf(limitVariance);
//                message += newLine + twoSpace + " New Baseline Average = " + String.valueOf(newBaseLineAvg);
//                actualValue = util.GetActualValueV1(totDenoCount, totNumCount);
//                message += newLine + twoSpace + " Process Efficiency = " + String.valueOf(actualValue);
//
//                if (actualValue >= newBaseLineAvg) {
//                    slaStatus = "MeT";
//                } else {
//                    slaStatus = "Not Met";
//                }
//
//                message += newLine + twoSpace + " Status = " + slaStatus;
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"BaseLine avg", "", String.valueOf(baselineavg), "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"Process Variance", "", String.valueOf(limitVariance), "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"New BaseLine avg", "", String.valueOf(newBaseLineAvg), "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"Process Efficiency", "", String.valueOf(actualValue), "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"SLA Status", "", slaStatus, "", "", "", ""});
//                }
//
//                if (dataLines.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

//            if (project.getProjectsource().equals(SourceKey.ADO.value))
//            {
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Issue Status", "commitedDate", "closedDate",
//                                    "Estimated Hour", "Actual Hours"});
//                }
//
//                if (project.getDetailedLogRequired().equals("Y")) {
//                    dataLinesDetailed.add(new String[]
//                            {"Key", "Type", "Issue Status", "commitedDate", "closedDate", "Estimated Hour", "Actual Hours"});
//                }
//
//                if (sla.getDenojql() == null || sla.getDenojql().isEmpty()) {
//                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                List<WorkItem> workitems = null;
//                List<RevisionValue> revisionValues = new ArrayList<>();
//                RevisionFields revisionFields = new RevisionFields();
//                RevisionValue revisionValue = new RevisionValue();
//
//                String ADQuery = "{\n" +
//                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";
//
//                workitems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", false, true, true, false, "", "", "", 100);
//
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                double processingDays = 0d;
//
//                if(workitems !=null && workitems.size()>0)
//                {
//                    for(WorkItem witem : workitems)
//                    {
//                        committedDate = null;
//                        closeedDate = null;
//                        workingDate = null;
//                        strCommittedDate = "";
//                        strClosedDate = "";
//
//                        estimatedHours =-10;
//                        actualHours = -1;
//                        remainingHours = -1;
//
//                        if (witem != null)
//                        {
//                            try
//                            {
//                                if (witem.getRevisions() != null) {
//                                    revisionValues = witem.getRevisions();
//                                }
//
//                                if (strCheckCommittedDateInField.equals("N"))
//                                {
//                                    if (revisionValues != null & revisionValues.size() > 0)
//                                    {
//                                        IssueActivityDate issueActivityDates = util.getADOWorkItemActivityDate(witem.getId(), revisionValues, inDevelopmentStatus, sourcedateFormat);
//                                        if (issueActivityDates != null)
//                                        {
//                                            if (issueActivityDates.getRequestedDate() != null)
//                                            {
//                                                strCommittedDate = issueActivityDates.getRequestedDateString();
//                                            }
//                                        }
//                                    }
//                                }
//                                else
//                                {
//                                    if (strCheckCommittedDateInField.equals("Created"))
//                                    {
//                                        strCommittedDate = witem.getFields().getCreatedDate();
//                                    }
//                                    else if (strCheckCommittedDateInField.equals("Updated"))
//                                    {
//                                        strCommittedDate= witem.getFields().getChangedDate();
//                                    }
//                                }
//
//                                if (!strCommittedDate.isEmpty())
//                                {
//                                    //Check whether the date is within the measurement period
//                                    workingDate = util.ConvertStringToDateForZFormat(strCommittedDate);
//                                    if (workingDate != null)
//                                    {
//                                        if ((workingDate.compareTo(dtFromDate) >= 0 && workingDate.compareTo(dtToDate) <= 0) == true) {
//                                            committedDate = workingDate;
//                                        }
//                                    }
//                                }
//
//                                if (strCheckClosedDateInField.equals("N")) {
//                                    revisionValues = witem.getRevisions();
//                                    boolean firstOccuranceClosedDate = false;
//
//                                    if (revisionValues != null & revisionValues.size() > 0) {
//                                        for (RevisionValue revisionValuedate : revisionValues)
//                                        {
//                                            if (revisionValuedate != null)
//                                            {
//                                                if (!closeStatus.isEmpty())
//                                                {
//                                                    if (firstOccuranceClosedDate == false) {
//                                                        //Check and compare the status with the record to get a match
//                                                        if (revisionValuedate.getFields().getState().equals(closeStatus)) {
//                                                            firstOccuranceClosedDate = true;
//                                                            strClosedDate = revisionValuedate.getFields().getStateChangeDate();
//                                                            break;
//                                                        }
//                                                    }
//                                                }
//
//                                                if (!prevCloseStatus.isEmpty())
//                                                {
//                                                    if (firstOccuranceClosedDate == false) {
//                                                        //Check and compare the status with the record to get a match
//                                                        if (revisionValuedate.getFields().getState().equals(prevCloseStatus))
//                                                        {
//                                                            firstOccuranceClosedDate = true;
//                                                            strClosedDate = revisionValuedate.getFields().getStateChangeDate();
//                                                            break;
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    if (!strClosedDate.isEmpty()) {
//                                        closeedDate = util.ConvertStringToDateForZFormat(strClosedDate);
//                                    }
//                                }
//                                else
//                                {
//                                    if (strCheckClosedDateInField.equals("Closed")) {
//                                        strClosedDate = witem.getFields().getClosedDate();
//                                    } else if (strCheckClosedDateInField.equals("Resolved")) {
//                                        strClosedDate = witem.getFields().getResolvedDate();
//                                    }
//
//                                    if (!strClosedDate.isEmpty()) {
//                                        closeedDate = util.ConvertStringToDateForZFormat (strClosedDate);
//                                    }
//                                }
//
//                                //Get the Hours
//                                estimatedHours = witem.getFields().getOriginalEffort();
//                                if (excludeTypes.equals("Y"))
//                                {
//                                    actualHours = witem.getFields().getActualEffortinHours();
//                                }
//                                remainingHours = witem.getFields().getEffortinHoursRemaining();
//
//                                variance = 0;
//                                if (committedDate != null && closeedDate != null)
//                                {
//                                    issueDateVariance = new IssueDateVariance();
//                                    issueDateVariance.setKey(witem.getId());
//                                    issueDateVariance.setType(witem.getFields().getWorkItemType());
//                                    issueDateVariance.setIssueStatus(witem.getFields().getState());
//                                    issueDateVariance.setVariance(0.0);
//                                    issueDateVariance.setCommitedDate(committedDate);
//                                    issueDateVariance.setClosedDate(closeedDate);
//                                    issueDateVariance.setCommitedDateString(strCommittedDate);
//                                    issueDateVariance.setClosedDateString(strClosedDate);
//
//                                    //Identify the Actual Hours
//                                    //Here excludeTypes used to store the Value Y Or N Or B
//                                    processingDays = 0;
//                                    if (excludeTypes.equals("N"))
//                                    {
//                                        //processingDays = util.GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, strIncludeCommittedDate);
//                                        //actualHours = processingDays * hours;
//                                        actualHours = util.GetWorkingHourOrMinVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, sourcedateFormat, hours, "H");
////                                        double a = util.GetWorkingHourOrMinVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, sourcedateFormat, hours, "H");
////                                        actualHours = actualHours/60;
//                                    }
//                                    else if (excludeTypes.equals("B")) //If actual Hour found in the field take it otherwise take it by taking the difference of Closed date - Committed date
//                                    {
//                                        actualHours = -1;
//                                        actualHours = witem.getFields().getActualEffortinHours();
//                                        if (actualHours == -1) //No Actual Hours in the field
//                                        {
////                                            processingDays = util.GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, strIncludeCommittedDate);
////                                            actualHours = processingDays * hours;
//                                            actualHours = util.GetWorkingHourOrMinVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, sourcedateFormat, hours, "H");
//                                        }
//                                    }
//
//                                    //Eligible Records --> Record should have Committed Date, Closed Date, Original estimation hour and Actual Hour
//                                    if (estimatedHours != -1 && actualHours != -1) //-1 means no data in the record
//                                    {
//                                        issueDateVariance.setEstimatedHours(estimatedHours);
//                                        issueDateVariance.setActualHours(actualHours);
//                                        elligibleItems.add(issueDateVariance);
//                                    }
//                                }
//
//                                if (project.getDetailedLogRequired().equals("Y"))
//                                {
//                                    strOriginalEstimate = "";
//                                    strActualHourSpent = "";
//                                    if (estimatedHours != -1)
//                                    {
//                                        strOriginalEstimate = String.valueOf(estimatedHours);
//                                    }
//
//                                    if (actualHours != -1)
//                                    {
//                                        strActualHourSpent = String.valueOf(actualHours);
//                                    }
//
//                                    dataLinesDetailed.add(new String[]
//                                            {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getState(), strCommittedDate, strClosedDate, strOriginalEstimate, strActualHourSpent});
//                                }
//                            }
//                            catch (Exception exception)
//                            {
//
//                            }
//                        }
//                    }
//                }
//
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                double denoCount = 0d;
//                double numcount = 0d;
//
//                //totalDaysMajor = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0 && o.getFixedVersionType().contains("Major")).mapToDouble(o -> o.getTotalDuration()).sum();
//                if (elligibleItems != null && elligibleItems.size() > 0)
//                {
//                    numcount = elligibleItems.stream().filter(o -> o.getActualHours() > 0).mapToDouble(o -> o.getActualHours()).sum();
//                    denoCount = elligibleItems.stream().filter(o -> o.getEstimatedHours() > 0).mapToDouble(o -> o.getEstimatedHours()).sum();
//
//                    totIssuesEligible = elligibleItems.size();
//                    totDenoCount = (denoCount / totIssuesEligible);
//                    totNumCount = (numcount / totIssuesEligible);
//
//                    for (IssueDateVariance issue : elligibleItems)
//                    {
//                        if (project.getDatafileRequired().equals("Y"))
//                        {
//                            strOriginalEstimate = "";
//                            strActualHourSpent = "";
//                            if (issue.getEstimatedHours() != -1)
//                            {
//                                strOriginalEstimate = String.valueOf(issue.getEstimatedHours());
//                            }
//
//                            if (issue.getActualHours() != -1)
//                            {
//                                strActualHourSpent = String.valueOf(issue.getActualHours());
//                            }
//
//
//                            dataLines.add(new String[]
//                                    {issue.getKey(), issue.getType(), issue.getIssueStatus(), issue.getCommitedDateString(), issue.getClosedDateString(),
//                                            strOriginalEstimate, strActualHourSpent});
//                        }
//                    }
//                }
//
//                message += newLine + twoSpace + " Average of Total Time Taken to deliver = " + totDenoCount;
//                message += newLine + twoSpace + " Average of Actual time spent = " + totNumCount;
//
//                if (dataLinesDetailed.size() > 0) {
//                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLinesDetailed, dataFileName1);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + dataFileName1;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                double denoCountActual = totDenoCount; //This is to hold the old value of deno Count so that it can be send back
//                double numCountActual = totNumCount;  //This is to hold the old value of num Count so that it can be send back
//
//                if (totDenoCount == 0 && totNumCount == 0) {
//                    totDenoCount = 1;
//                    totNumCount = 1;
//                } else if (totDenoCount == 0) {
//                    totDenoCount = 1;
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"", "", "", "", "", "", ""});
//
//                    dataLines.add(new String[]
//                            {"Avg Value", "", "", "", "", String.valueOf(totDenoCount), String.valueOf(totNumCount)});
//                }
//
//                limitVariance = baselineavg * ((double)limit/(double)100);
//
//                newBaseLineAvg = baselineavg - limitVariance;
//                message += newLine + twoSpace + " Baseline Average = " + String.valueOf(baselineavg);
//                message += newLine + twoSpace + " Process Variance = " + String.valueOf(limitVariance);
//                message += newLine + twoSpace + " New Baseline Average = " + String.valueOf(newBaseLineAvg);
//                actualValue = util.GetActualValueV1(totDenoCount, totNumCount);
//                message += newLine + twoSpace + " Process Efficiency = " + String.valueOf(actualValue);
//
//                if (actualValue >= newBaseLineAvg)
//                {
//                    slaStatus = "Met";
//                }
//                else
//                {
//                    slaStatus = "Not Met";
//                }
//
//                message += newLine + twoSpace + " Status = " + slaStatus;
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"BaseLine avg", "", String.valueOf(baselineavg), "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"Process Variance", "", String.valueOf(limitVariance), "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"New BaseLine avg", "", String.valueOf(newBaseLineAvg), "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"Process Efficiency", "", String.valueOf(actualValue), "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"SLA Status", "", slaStatus, "", "", "", ""});
//                }
//
//                if (dataLines.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

            return null;
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;
    }

    private ProcessedData PremierCustomerSatisficationSurvey(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        //Modified and tested with July Data - Simanchal
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        List<String[]> dataLines2 = new ArrayList<>();

        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                expectedsla = Integer.parseInt(sla.getExpectedsla());
//                minsla = Integer.parseInt(sla.getMinimumsla());
//
//                if (expectedsla == 0 || minsla == 0) {
//                    //Stop the processing
//                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//
//                String strCreditScore = "";
//                double creditScore = 0;
//
//                strCreditScore = sla.getInput1();
//                if (strCreditScore.isEmpty()) {
//                    message += newLine + twoSpace + " No Credit Score Found, Please check the configuration. Stopping SLA Processing";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                try {
//                    creditScore = Double.parseDouble(strCreditScore);
//                } catch (Exception exParsing) {
//                    message += newLine + twoSpace + " Unable to parse the provided Credit Score, Please check the configuration. Stopping SLA Processing";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                double actualValue = creditScore;
//                message += newLine + twoSpace + " Credit Score = " + String.valueOf(actualValue);
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//                status = util.WriteToFile(project.getLogFile(), message);
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, "", "");
//                return data;
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            return null;
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
            throw ex;
        }

    }



    private ProcessedData DelayInReadyForProductionRelease(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);

        String baseURI = "";
        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> numIssue = new ArrayList<>();
        List<Issue> commentIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetail = new ArrayList<>();
        boolean isValid = false;
        String holidayList = "";
        String projectdateFormat = "";

        List<Date> lstHolidays = new ArrayList<>();
        String strPageSize="";
        String strCheckHolidays="";
        String strCheckWeekend="";
        String releasedateformat = "";
        String sourcedateFormat="";
        Date dtReceivedDate = null;
        Date dtEstimationDate = null;
        Date dtFromDate;
        Date dtToDate;
        sourcedateFormat = project.getSourceDateFormat();
        if (sourcedateFormat.equals("")) {
            message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
            status = util.WriteToFile(project.getLogFile(), message);
            return null;
        }

        String dateFormatFromConfig = project.getDateFormat();
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
        lstHolidays =util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);

        //if (lstHolidays == null)
        if (lstHolidays == null || lstHolidays.size() == 0) {
            message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
            status = util.WriteToFile(project.getLogFile(), message);
            return null;
        }


        //MEthod Specific Variables
        List<String> fixVersions = new ArrayList<>();
        List<CR> totalCrs = new ArrayList<>();
        List<FixedVersion> issueFixedVersions = new ArrayList<>();
        CR cr = new CR();
        String crsAssociated = "";
        String fixedVersionAssociated = "";
        boolean isDelayed = false;
        boolean isEligible = false;
        int totalCrAssociated = 0;
        String Comments = "";
        int pageSize = 1000;

        String crFixedVersions = "";
        String[] input1 = null;
        String closedStatus = "";

        try
        {
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


//            if (project.getProjectsource().equals(SourceKey.JIRA.value))
//            {
//                crFixedVersions = sla.getInput1();
//
//                if (project.getProjectsource().equals(SourceKey.JIRA.value))
//                {
//                    if (crFixedVersions.isEmpty())
//                    {
//                        input1 = (sla.getInput1().split(","));
//                        if (input1 != null && input1.length > 0)
//                        {
//                            for (String a : input1) {
//                                String[] crVal = a.split("#");
//                                cr = new CR();
//                                cr.setCrNumber(crVal[0]);
//                                List<String> crFixVersions = List.of(crVal[1].split("~"));
//                                cr.setFixedversions(crFixVersions);
//                                cr.setDelayed(false);
//                                totalCrs.add(cr);
//                            }
//                        }
//                    }
//                }
//
//                closedStatus = sla.getConfig1();
//                if (closedStatus.isEmpty())
//                {
//                    message = twoSpace + "Closed status of the issue not found in config1, Please check your configuration";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String[] statustoCheck = (sla.getConfig1().split(",")); //This is mandatory so validate it
//
//                if (statustoCheck != null && statustoCheck.length == 0)
//                {
//                    message = twoSpace + "Invalid Closed status of the issue found in config1, Please check your configuration";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String linkitemTypes = sla.getConfig2();
//
//                //Region For Configuration Data Retrival and Data Validation - Start
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql= " + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql= " + sla.getDenojql();
//                }
//
//                //This is mandatory to run the program. Validate
//                if (denourl.equals("")) {
//                    message = twoSpace + "demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                //denoIssue = iJiraDataService.getAllIssuesOnJQLV1(userName, password, denourl, "", "", false, false);
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, pageSize);
//                //Region Data Retrival - End
//
//                //Region Data file Definition - Start
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Issue Key", "Type", "Status", "FixedVersions", "RelatedCR", "Eligible", "Comments"});
//                }
//
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//
//                totalNumCountNotSatisfied = 0;
//                totalNumCountSatisfied = 0;
//
//                totaldenoCount = totalCrs.size();
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    for (Issue issue : denoIssue) {
//                        if (issue != null) {
//                            isValid = true;
//
//                            //Take only those issues where the status of the issue is not in the value provided in the configuration
//                            //We are checking if the status of the issue either match with the given value the make
//                            //the IsValid = false. This means the record matches with the status provided. So should not be
//                            //taken in to account. If there is a better way to use Not In then we can use that.
//                            for (String issueStatus : statustoCheck) {
//                                String aa = issue.getFields().getStatus().getName();
//                                if (issue.getFields().getStatus().getName().trim().equals(issueStatus.trim())) {
//                                    isValid = false;
//                                    break;
//                                }
//                            }
//
//                            fixedVersionAssociated = "";
//                            issueFixedVersions = issue.getFields().getFixVersions();
//                            //Get the fixed versions associated with the story
//                            if (issueFixedVersions.size() > 0) {
//                                for (FixedVersion fv : issueFixedVersions) {
//                                    fixedVersionAssociated = fixedVersionAssociated + fv.getName() + ", ";
//                                }
//                            }
//
//                            crsAssociated = "";
//                            totalCrAssociated = 0;
//                            isDelayed = false;
//                            cr = new CR();
//
//                            if (isValid == true) //if the issue is valid to process i.e. the issue type doesnot equal to config status value
//                            {
//                                if (issueFixedVersions.size() > 0) {
//                                    //Check with the CR List Using Lamda
//                                    //List<FixedVersion> f = issueFixedVersions.stream().filter(two -> totalCrs.stream().anyMatch(one -> one.getFixedversions().contains(two.getName()))).collect(Collectors.toList());
//                                    for (FixedVersion fv : issueFixedVersions) {
//                                        //CR delayCR = totalCrs.stream().filter(item -> item.getFixedversions().contains(fv)).findFirst().get();
//                                        for (CR crvalue : totalCrs) {
//                                            if (crvalue.getFixedversions().contains(fv.getName())) {
//                                                crvalue.setDelayed(true);
//                                                isDelayed = true;
//                                                cr = crvalue;
//                                                totalCrAssociated++;
//                                                crsAssociated = crsAssociated + crvalue.getCrNumber() + ", ";
//                                            }
//                                        }
//                                    }
//
//                                    if (totalCrAssociated > 1) {
//                                        Comments = "This issue assigned to fixed versions associated with multiple CRS.";
//                                    }
//                                }
//                            }
//
//                            //Write to the CSV Files
//                            if (project.getDatafileRequired().equals("Y")) {
//                                dataLines.add(new String[]
//                                        {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName(), fixedVersionAssociated, crsAssociated, String.valueOf(isValid), Comments});
//                            }
//                        }
//                    }
//                }
//
//                //Write the CR Summary
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"", "", "", "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"", "", "", "", "", "", ""});
//
//                    dataLines.add(new String[]
//                            {"CR", "Delayed", "", "", "", "", ""});
//                    for (CR crvalue : totalCrs) {
//                        dataLines.add(new String[]
//                                {crvalue.getCrNumber(), String.valueOf(crvalue.isDelayed()), "", "", "", "", ""});
//                    }
//                }
//
//                totalnumCount = (int) totalCrs.stream().filter(one -> one.isDelayed() == true).count();
//                message += twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                if (dataLines.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//
//                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
//                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back
//
//                if (totaldenoCount == 0 && totalnumCount == 0) {
//                    totaldenoCount = 1;
//                    totalnumCount = 1;
//                } else if (totaldenoCount == 0) {
//                    totaldenoCount = 1;
//                }
//
//                double actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"ID", "Type","Date"});
                }

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty()) {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
                String fromdateFromConfig = sla.getFrom();
                String todateFromConfig=sla.getTo();

                if (!fromdateFromConfig.equals(""))
                {
                    if (util.isDateValid(fromdateFromConfig, dateFormatFromConfig) == false) {
                        message = twoSpace + "From Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
                        status = util.WriteToFile(project.getLogFile(), message);
                        return null;
                    }
                }
                else
                {
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
                }
                else
                {
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



                String ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                List<WorkItem> denoWorkitems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", false, false, false, false,"", "", "", 100);

                message = "";
                totaldenoCount = 0;
                totalnumCount = 0;
                if((denoWorkitems!=null)&& denoWorkitems.size()>0) {
                    totaldenoCount = denoWorkitems.size();

                }
                if(denoWorkitems!=null && denoWorkitems.size()>0) {
                    for (WorkItem witem : denoWorkitems) {
                        if (witem != null) {
                            if ((witem.getFields().getRevisedDueDate() != null) && (witem.getFields().getDateInfluenced() !=null)) {
                                if (witem.getFields().getDateInfluenced().equals("Long80"))
                                {
                                    String RevisedDueDate = witem.getFields().getRevisedDueDate();
                                    Date revisedDuedate = util.ConvertStringToDateForZFormat(RevisedDueDate);
                                    if (((revisedDuedate.compareTo(dtFromDate) >= 0 && revisedDuedate.compareTo(dtToDate) <= 0) == false)) {
                                        totalnumCount++;
                                        if (project.getDatafileRequired().equals("Y")) {
                                            dataLines.add(new String[]
                                                    {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getRevisedDueDate()});
                                        }
                                    }
                                }
                            } else {
                                if (witem.getFields().getDueDate() != null) {
                                    String DueDate = witem.getFields().getDueDate();
                                    Date duedate = util.ConvertStringToDateForZFormat(DueDate);
                                    if (((duedate.compareTo(dtFromDate) >= 0 && duedate.compareTo(dtToDate) <= 0) == false)) {
                                        totalnumCount++;
                                        if (project.getDatafileRequired().equals("Y")) {
                                            dataLines.add(new String[]
                                                    {witem.getId(), witem.getFields().getWorkItemType(),witem.getFields().getDueDate()});
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                message += twoSpace + " Total Denominator Count = " + totaldenoCount;
                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
                message += newLine + twoSpace + " AdoQuery = " + ADQuery;


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

                if (dataLinesDetail.size()> 0)
                {
                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
                    try
                    {
                        boolean csvStatus = util.WriteToCSv(dataLinesDetail, dataFileName1);
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

                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;

                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                if (totaldenoCount == 0 && totalnumCount == 0) {
                    totaldenoCount = 1;
                    totalnumCount = 1;
                } else if (totaldenoCount == 0) {
                    totaldenoCount = 1;
                }

                double actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
                slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
                message += newLine + twoSpace + " Status = " + slaStatus;

                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),ADQuery);
                status = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            return null;
        }
        catch (Exception ex)
        {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;
    }

    private ProcessedData ReopenedDefectsBeforeProductionRelease(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);

        //Method Specific Varriable Declaration Area - Start

        //Generic for each method
        String baseURI = "";
        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> allIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetail = new ArrayList<>();

        int pageSize = 1000;
        String strPageSize = "";

        String developmentStatus = "";
        String defectIssueType = "";
        String fixedVersion = "";
        List<Issue> processingIssues = new ArrayList<>();

        List<KeyValue> issueDefects = new ArrayList<>();
        String issueURI = "";
        String defectKey = "";
        Issue defectIssue = new Issue();
        List<Issue> issues = new ArrayList<>();

        List<History> historyList = new ArrayList<>();
        Issue thisIssue = new Issue();
        developmentStatus = sla.getConfig1().replace("'", "");
        defectIssueType = sla.getConfig2();
        fixedVersion = sla.getConfig3().replace("'", "");
        String searchWorkitemLinkURI = project.getLinkItemUrl();
        String searchItemURI = project.getItemUrl();
        String defectRecordType = sla.getInput1();
        String inDevStatus = "";
        String inTestStatus = "";

        //ADO specific variables
        String denoQuery = "";
        String itemQuery = sla.getInput2();
        List<WorkItem> workitemsCreatedDates = new ArrayList<>();
        List<WorkItem> eligibleIssues = new ArrayList<>();
        int numCount = 0;
        String deliveryStatus1 = "";
        String deliveryStatus2 = "";
        String storyStatus1 = "";
        String storyStatus2 = "";

        //MEthod Specific Variables

        //Method Specific Varriable Declaration Area - End

        try
        {
            strPageSize = project.getPageSize();
            if (!strPageSize.isEmpty()) {
                try {
                    pageSize = Integer.parseInt(strPageSize);
                } catch (Exception exPageSizeParse) {
                    pageSize = 1000;
                }
            }

            try {
                expectedsla = Integer.parseInt(sla.getExpectedsla());
                minsla = Integer.parseInt(sla.getMinimumsla());

                if (expectedsla == 0 || minsla == 0) {
                    //Stop the processing
                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
            } catch (Exception exsla) {
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (sla.getDenojql() == null || sla.getDenojql().isEmpty()) {
                message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String[] developStatus = sla.getConfig1().split(",");
            if (developStatus != null && developStatus.length > 2) {
                message = twoSpace + "Only Two status(s) can be mentioned for Config1. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (developStatus != null && developStatus.length >= 1) {
                inDevStatus = developStatus[0];
            }

            if (developStatus != null && developStatus.length >= 2) {
                inTestStatus = developStatus[1];
            }

            String[] acStatus = sla.getConfig4().split(",");
            if (acStatus != null && acStatus.length > 2) {
                message = twoSpace + "Only Two status(s) can be mentioned for Config4. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (acStatus != null && acStatus.length >= 1) {
                deliveryStatus1 = acStatus[0];
            }

            if (acStatus != null && acStatus.length >= 2) {
                deliveryStatus2 = acStatus[1];
            }



//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Region For Configuration Data Retrival and Data Validation - Start
//
//
//                String[] sStatus = sla.getConfig5().split(",");
//                if (sStatus != null && sStatus.length > 2) {
//                    message = twoSpace + "Only Two status(s) can be mentioned for Config4. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (sStatus != null && sStatus.length >= 1) {
//                    storyStatus1 = sStatus[0];
//                }
//
//                if (sStatus != null && sStatus.length >= 2) {
//                    storyStatus2 = sStatus[1];
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql= " + sla.getDenojql();
//                }
//
//                //This is mandatory to run the program. Validate
//                if (denourl.equals("")) {
//                    message = twoSpace + "demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                //Region Configuration Data Retrival and Data Validation - End
//
//                //Region to Retrieve data  - Start
//                if (denourl != "") {
//                    baseURI = project.getProjecturl() + "/api/latest/search?";
//
//                    String commentURI = project.getProjecturl() + "/agile/1.0/issue";
//                    allIssue = iJiraDataService.getAllIssuesOnJQLV1(userName, password, denourl, baseURI, commentURI, true, true);
//                } else {
//                    //Either process it in a different way or stop the processing by sending a messae
//                    //Currently we are stopping the processing.
//                    message = twoSpace + "numJQL and demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                defectIssueType = sla.getConfig2().replace("'", "");
//                if (defectIssueType.isEmpty()) {
//                    defectIssueType = "Defect";
//                }
//
//                //Region to Define data file  - Start
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Status", "Closed date", "Defect key", "Type", "Status", "No of time reopened"});
//                }
//
//                if (project.getDetailedLogRequired().equals("Y")) {
//                    dataLinesDetail.add(new String[]
//                            {"Key", "Type", "Defect Key", "Type", "Status"});
//                }
//
//                if (allIssue != null && allIssue.size() > 0)
//                {
//                    for (Issue is : allIssue)
//                    {
//                        if (is.getFields().getStatus().getName() != null || !is.getFields().getStatus().getName().equals(""))
//                        {
//                            if (is.getFields().getStatus().getName().equals(storyStatus1.trim()) || is.getFields().getStatus().getName().equals(storyStatus2.trim()))
//                            {
//                                denoIssue.add(is);
//                            }
//                        }
//                    }
//                }
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//
//                    for (Issue issue : denoIssue) {
//                        issueDefects = new ArrayList<>();
//
//                        if (issue != null) {
//                            if (project.getDatafileRequired().equals("Y")) {
//                                dataLines.add(new String[]
//                                        {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName(), issue.getFields().getResolutiondate(), "", "", "", ""});
//                            }
//                            if (project.getDetailedLogRequired().equals("Y"))
//                            {
//                                dataLinesDetail.add(new String[]
//                                        {issue.getKey(), issue.getFields().getIssuetype().getName(), "", "", ""});
//                            }
//
//                            if (issue.getFields().getIssuelinks() != null && !issue.getFields().getIssuelinks().isEmpty()) {
//                                for (Issuelinks issuelinks : issue.getFields().getIssuelinks()) {
//                                    //need to check wheather qa/ba defect and not from production
//                                    KeyValue defect = new KeyValue();
//                                    if (issuelinks.getOutwardIssue() != null && issuelinks.getOutwardIssue().getFields() != null && issuelinks.getOutwardIssue().getFields().getIssuetype().getName() != null) {
//                                        if (issuelinks.getOutwardIssue().getFields().getIssuetype().getName().equalsIgnoreCase(defectIssueType)) {
//                                            defect.setKey(issuelinks.getOutwardIssue().getKey());
//                                            defect.setValue("0");
//                                            issueDefects.add(defect);
//                                        }
//                                    }
//
//                                    if (issuelinks.getInwardIssue() != null && issuelinks.getInwardIssue().getFields() != null && issuelinks.getInwardIssue().getFields().getIssuetype().getName() != null) {
//                                        if (issuelinks.getInwardIssue().getFields().getIssuetype().getName().equalsIgnoreCase(defectIssueType)) {
//                                            defect = new KeyValue();
//                                            defect.setKey(issuelinks.getInwardIssue().getKey());
//                                            defect.setValue("0");
//                                            issueDefects.add(defect);
//                                        }
//                                    }
//
//                                    //get the details of the defects
//                                    if (issueDefects != null && issueDefects.size() > 0) {
//                                        for (KeyValue issueDefect : issueDefects) {
//                                            if (issueDefect != null) {
//                                                thisIssue = null;
//                                                defectKey = issueDefect.getKey();
//                                                if (processingIssues != null && processingIssues.size() > 0) {
//                                                    issues = processingIssues.stream().filter(x -> x.getKey().equals(defectIssue)).collect(Collectors.toList());
//                                                    if (issues != null && issues.size() > 0) {
//                                                        thisIssue = issues.get(0);
//                                                    }
//                                                }
//
//                                                if (thisIssue == null) {
//                                                    try {
//                                                        String uri = project.getProjecturl() + "/agile/1.0/issue/" + defectKey + "?expand=changelog";
//                                                        thisIssue = iJiraDataService.getIssueByKey(uri, userName, password);
//                                                        if (thisIssue != null) {
//                                                            processingIssues.add(thisIssue);
//                                                        }
//                                                    } catch (Exception exissue) {
//
//                                                    }
//                                                }
//
//                                                //Check the history to get if the defect is repoened or not from the history
//                                                if (thisIssue != null) {
//                                                    issueDefect.setType(thisIssue.getFields().getIssuetype().getName());
//                                                    issueDefect.setStatus(thisIssue.getFields().getStatus().getName());
//
//                                                    if (project.getDetailedLogRequired().equals("Y"))
//                                                    {
//                                                        dataLinesDetail.add(new String[]
//                                                                {"", "", issueDefect.getKey(), issueDefect.getType(), ""});
//                                                    }
//
//                                                    if (thisIssue.getChangelog() != null) {
//                                                        if (thisIssue.getChangelog().getHistories() != null) {
//                                                            historyList = thisIssue.getChangelog().getHistories();
//                                                            int reOpenCount = 0;
//                                                            int closedCount = 0;
//                                                            for(History history:historyList)
//                                                            {
//                                                                List<Item>itemList=history.getItems();
//                                                                for(Item item:itemList)
//                                                                {
//                                                                    //String strFieldValue = item.getField();
//                                                                    if(item.getField().equals("status"))
//                                                                    {
//                                                                        if (item.getToString().equals(deliveryStatus1.trim()) || item.getToString().equals(deliveryStatus2.trim()))
//                                                                        {
//                                                                            closedCount++;
//                                                                        }
//                                                                        if (closedCount > 0)
//                                                                        {
//                                                                            if (item.getToString().equals(inDevStatus.trim()) || item.getToString().equals(inTestStatus.trim()))
//                                                                            {
//                                                                                reOpenCount++;
//                                                                                closedCount = 0;
//                                                                            }
//                                                                        }
//                                                                        if (project.getDetailedLogRequired().equals("Y"))
//                                                                        {
//                                                                            dataLinesDetail.add(new String[]
//                                                                                    {" ", " ", " ", " ", item.getToString()});
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                            numCount = numCount + reOpenCount;
//                                                            issueDefect.setValue(String.valueOf(numCount));
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//
//                                        //Calculate the Re-opened defects for the Issue
//                                        for (KeyValue issueDefect : issueDefects) {
//                                            if (issueDefect != null) {
//                                                int totalReopened = 0;
//                                                try {
//                                                    totalReopened = Integer.parseInt(issueDefect.getValue());
//                                                } catch (Exception exParse) {
//
//                                                }
//
//                                                totalnumCount = totalnumCount + totalReopened;
//                                                dataLines.add(new String[]
//                                                        {"", "", "", "",  issueDefect.getKey(), issueDefect.getType(), issueDefect.getStatus(), issueDefect.getValue()});
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                message = "";
//                message += newLine + twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                ///////Data File Saving - Start
//                if (dataLines.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                if (dataLinesDetail.size()> 0)
//                {
//                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
//                    try
//                    {
//                        boolean csvStatus = util.WriteToCSv(dataLinesDetail, dataFileName1);
//                        if (csvStatus == true)
//                        {
//                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + dataFileName1;
//                        }
//                        else
//                        {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    }
//                    catch (Exception exCsv)
//                    {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//
//                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
//                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back
//
//                if (totaldenoCount == 0 && totalnumCount == 0) {
//                    totaldenoCount = 1;
//                    totalnumCount = 1;
//                } else if (totaldenoCount == 0) {
//                    totaldenoCount = 1;
//                }
//
//                double actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//                 if (actualValue <= expectedsla) {
//                    slaStatus = "Met";
//                } else {
//                    slaStatus = "Not Met";
//                }
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {

                List<WorkItem> finalWorkitems = null;
                List<WorkItem> Workitems = null;
                defectIssueType = "'" + defectIssueType.replace(", ", "', '") + "'";
                defectRecordType = "'"+sla.getInput1()+"'";


                if (project.getDatafileRequired().equals("Y"))
                {
                    dataLines.add(new String[]
                            {"Key", "Type", "Status", "Defect key", "Type", "Status", "No of time reopened"});
                }



                //Preparing num & deno queries
                denoQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                //String fixedVersionLinkItemType = "'User Story'";
                if(denoQuery != null)
                {
                    workitemsCreatedDates = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), denoQuery, "POST", false, true, true, true, defectRecordType, searchWorkitemLinkURI, searchItemURI, 1000);

                }
                if(workitemsCreatedDates!=null&&workitemsCreatedDates.size()>0) {
                    for (WorkItem workItem : workitemsCreatedDates) {
                        if (workItem != null) {
                            List<WorkItem> childLinks = workItem.getChildLinks();
                            if (childLinks != null && childLinks.size() > 0) {


                                for (WorkItem child : childLinks) {

                                    if (child.getFields().getWorkItemType().equalsIgnoreCase("Defect")) {
                                        totaldenoCount++;
                                        List<RevisionValue> revisions = child.getRevisions();
                                        if (revisions != null) {
                                            int reOpenCount = 0;
                                            int closedCount = 0;
                                            for (RevisionValue rv : revisions) {
                                                //String f = rv.getFields().getState();
                                                if (rv.getFields().getState().equals(deliveryStatus1)) {
                                                    closedCount++;
//
//                                                if (closedCount > 1)
//                                                {
//                                                    reOpenCount++;
//                                                }
                                                    if (closedCount > 1) {
                                                        totalnumCount++;
                                                        break;
                                                    }

                                                }


                                            }

                                            if (project.getDatafileRequired().equals("Y")) {
                                                dataLines.add(new String[]
                                                        {workItem.getId(), workItem.getFields().getWorkItemType(), workItem.getFields().getState(), child.getId(), child.getFields().getWorkItemType(), child.getFields().getState(), String.valueOf(reOpenCount)});
                                            }
                                        }
                                    }
                                }
                            }


                        }
                    }
                }

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

                if (dataLinesDetail.size()> 0)
                {
                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
                    try
                    {
                        boolean csvStatus = util.WriteToCSv(dataLinesDetail, dataFileName1);
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
                message += newLine + twoSpace + " AdoQuery = " + denoQuery;
                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;

                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                if (totaldenoCount == 0 && totalnumCount == 0)
                {
                    totaldenoCount = 1;
                    totalnumCount = 1;
                }
                else if (totaldenoCount == 0)
                {
                    totaldenoCount = 1;
                }

                double actualValue = util.GetActualValueV1((double)totaldenoCount, (double)totalnumCount);
                message += newLine + twoSpace + " Actual = " + actualValue;
                if (actualValue <= minsla)
                {
                    slaStatus = "Met";
                }
                else
                {
                    slaStatus = "Not Met";
                }
                message += newLine + twoSpace + " Status = " + slaStatus;

                ProcessedData data = util.BuildProcessData(sla, (float)actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),denoQuery);
                boolean isStatus = util.WriteToFile(project.getLogFile(), message);
                return data;
            }
            return null;
        }catch (Exception ex) {
            ex.printStackTrace();
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;
    }

    ////////Generic Function - Start /////////
    private ProcessedData JiraProcessWithJQLCountGeneric(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> numIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        int pageSize = 1000;
        String strPageSize = "";

        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=" + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql= " + sla.getDenojql();
//                }
//
//                expectedsla = Integer.parseInt(sla.getExpectedsla());
//                minsla = Integer.parseInt(sla.getMinimumsla());
//
//                if (expectedsla == 0 || minsla == 0) {
//                    //Stop the processing
//                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                strPageSize = project.getPageSize();
//
//                if (!strPageSize.isEmpty()) {
//                    try {
//                        pageSize = Integer.parseInt(strPageSize);
//                    } catch (Exception exPageSizeParse) {
//                        pageSize = 1000;
//                    }
//                }
//
//                if (numurl.isEmpty() || denourl.isEmpty()) {
//                    message = twoSpace + "Num JQL and Deno JQL are not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, pageSize);
//                numIssue = iJiraDataService.getIssuesUsingJQL(userName, password, numurl, "", false, false, pageSize);
//
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                message = "";
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"StoryId", "Type", "Status"});
//                    dataLines.add(new String[]
//                            {"Denominator Issue", "", ""});
//                }
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//                    for (Issue issue : denoIssue) {
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines.add(new String[]
//                                    {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName()});
//                        }
//                    }
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"", "", ""});
//
//                    dataLines.add(new String[]
//                            {"Numerator Issue", "", ""});
//                }
//
//                if (numIssue != null && numIssue.size() > 0) {
//                    totalnumCount = numIssue.size();
//                    for (Issue issue : numIssue) {
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines.add(new String[]
//                                    {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName()});
//                        }
//                    }
//                }
//
//                //Write to Data File
//                if (dataLines.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//                message += newLine + twoSpace + " AdoQuery = " + ;
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//
//                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
//                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back
//
//                if (totaldenoCount == 0 && totalnumCount == 0) {
//                    totaldenoCount = 1;
//                    totalnumCount = 1;
//                } else if (totaldenoCount == 0) {
//                    totaldenoCount = 1;
//                }
//
//                double actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//                message += newLine + twoSpace + " Actual = " + actualValue;
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),denourl);
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }
        return null;
    }
    ////////Generic Function - End //////////

    //Non-backlog SLA Calculation - Start
    //This function covers P1, P2 and P3 incidents with different config value


    private ProcessedData PercentOfIncidentOpened(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<Issue> denoIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed=new ArrayList<>();
        IssueDateVariance issueDateVariance = new IssueDateVariance();
        List<History> Historylist = new ArrayList<>();
        List<Item> ItemList = new ArrayList<>();
        Date committedDate = new Date();//committeddate
        Date reDate = new Date();
        double totDenoCount = 0;
        double totNumCount = 0;
        double actualValue = 0;
        String inDevelopmentStatus = "";
        String sourcedateFormat = "";
        reDate = null;
        String holidayList = "";
        String projectdateFormat = "";
        int limitFromConfig = 0;
        List<Date> lstHolidays = new ArrayList<>();
        String fromdateFromConfig = "";
        String todateFromConfig = "";
        String dateFormatFromConfig = "";
        String releasedateformat = "";
        Date dtFromDate = null;
        Date dtToDate = null;
        String strCheckHolidays = "";
        String strCheckWeekend = "";
        totalNumCountSatisfied = 0;
        int pageSize = 1000;
        String strPageSize = "";
        String ADOQuery;
        boolean isconditionsatisfied = false;
        String closeddate = "";
        String reopendate = "";
        List<IssueDateVariance> eligibleissue = new ArrayList<>();

        Date closedIssueDate = new Date();
        Date reOpenIssueDate = new Date();
        String strClosedDate = "";
        String strReopenDate = "";
        String strCloseStatusOnIssue = "";
        String strReopenStatusOnIssue = "";
        long varianceDate = 0;
        String ADQuery="";
        History closedHistory = new History();
        History reopenHistory = new History();
        List<WorkItem> workitems=new ArrayList<>();
        RevisionValue closedRevision=new RevisionValue();
        RevisionValue reopenRevision=new RevisionValue();

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

            limitFromConfig = Integer.parseInt(sla.getLimit());

            strPageSize = project.getPageSize();
            if (!strPageSize.isEmpty()) {
                try {
                    pageSize = Integer.parseInt(strPageSize);
                } catch (Exception exPageSizeParse) {
                    pageSize = 1000;
                }
            }

            strCheckHolidays = sla.getInput2();
            if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
                strCheckHolidays = "N";
            }

            strCheckWeekend = sla.getInput3();
            if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
                strCheckWeekend = "N";
            }

            inDevelopmentStatus = sla.getConfig1();
            if (inDevelopmentStatus.equals("")) {
                message = twoSpace + "Status for Development / Issue Committed to Sprint not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String newStatus = sla.getConfig1().replace("'", "");
            String statusForClose = sla.getConfig2().replace("'", "");

            if (newStatus.equals("")) {
                message = twoSpace + "Status for ReOpen Check for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (statusForClose.equals("")) {
                message = twoSpace + "Closed status (Development/Testing) for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String closedStatus = "";
            String acceptedStatus = "";
            Date closeDate = new Date();
            Date newDate = new Date();
            issueDateVariance = new IssueDateVariance();

            List<String> Status1 = Arrays.asList(sla.getConfig1().split(","));
            if (!Status1.isEmpty() && Status1 == null)
            {
                message = twoSpace + "Status(s) to check Re-open not found, please check your configuration. Stopping SLA calculation";
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
                acceptedStatus = arrClosedStatus[1];
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

//            if (project.getProjectsource().equals(SourceKey.JIRA.value))
//            {
//                denourl = "";
//                if (sla.getDenojql().isEmpty()) {
//                    message = twoSpace + "Deno JQL not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=" + sla.getDenojql();
//                }
//
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, true, pageSize);
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Status", "commited Date", "reopend Date", "Variance (In Days)", "Record Status"});
//                }
//                if (project.getDetailedLogRequired().equals("Y"))
//                {
//                    dataLinesDetailed.add(new String[]
//                            {"Key", "Type", "Final Issue Status", "commitedDate", "closedDate"});
//                }
//
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//                    eligibleissue = new ArrayList<>();
//                    for (Issue issue : denoIssue) {
//                        if (issue != null) {
//                            try
//                            {
//                                closedIssueDate = null;
//                                reOpenIssueDate = null;
//                                strClosedDate = "";
//                                strReopenDate = "";
//                                strCloseStatusOnIssue = "";
//                                strReopenStatusOnIssue = "";
//                                varianceDate = 0;
//
//                                closedHistory = null;
//                                reopenHistory = null;
//
//                                List<History> historyList = new ArrayList<>();
//                                if (issue.getChangelog() != null) {
//                                    if (issue.getChangelog().getHistories() != null && issue.getChangelog().getHistories().size() > 0) {
//                                        historyList = issue.getChangelog().getHistories();
//                                    }
//                                }
//
//                                if (historyList != null && historyList.size() > 0) {
//                                    boolean firstOccuranceClosed = false;
//                                    boolean firstOccuranceFTR = false;
//                                    for (History history : historyList) {
//                                        List<Item> itemList = new ArrayList<>();
//                                        if (history.getItems() != null && history.getItems().size() > 0) {
//                                            itemList = history.getItems();
//
//                                            //Check the first Occurance of closed status
//                                            if (closedHistory == null) {
//                                                for (Item item : itemList) {
//                                                    if (item.getField().equals("status")) {
//                                                        if (!closedStatus.isEmpty()) {
//                                                            if (firstOccuranceClosed == false) {
//                                                                if (item.getToString().equals(closedStatus.trim())) {
//                                                                    closedHistory = history;
//                                                                    firstOccuranceClosed = true;
//                                                                    strCloseStatusOnIssue = item.getToString();
//                                                                    strClosedDate = history.getCreated();
//                                                                }
//                                                            }
//                                                        }
//
//                                                        String prevClosedStatus = null;
//                                                        if (!prevClosedStatus.isEmpty()) {
//                                                            if (firstOccuranceClosed == false) {
//                                                                if (item.getToString().equals(prevClosedStatus.trim())) {
//                                                                    closedHistory = history;
//                                                                    firstOccuranceClosed = true;
//                                                                    strCloseStatusOnIssue = item.getToString();
//                                                                    strClosedDate = history.getCreated();
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            //Find the Occurance of Reopen Status as we got the Closed Status. We need to compare the date also
//                                            if (closedHistory != null) {
//                                                if (firstOccuranceFTR == false) {
//                                                    for (Item item : itemList) {
//                                                        if (item.getField().equals("status")) {
//                                                            for (String cStatus : arrstatusForReopen) {
//                                                                if (firstOccuranceFTR == false) {
//                                                                    if (item.getToString().equals(cStatus.trim())) {
//                                                                        reopenHistory = history;
//                                                                        firstOccuranceFTR = true;
//                                                                        strReopenStatusOnIssue = item.getToString();
//                                                                        strReopenDate = history.getCreated();
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//
//                                if (closedHistory != null) {
//                                    strClosedDate = closedHistory.getCreated();
//                                    if (!strClosedDate.isEmpty()) {
//                                        closedIssueDate = util.ConvertStringToDateForZFormat(strClosedDate);
//                                    }
//                                }
//
//                                if (reopenHistory != null) {
//                                    strReopenDate = reopenHistory.getCreated();
//                                    if (!strReopenDate.isEmpty()) {
//                                        reOpenIssueDate = util.ConvertStringToDateForZFormat(strReopenDate);
//                                    }
//                                }
//                            }
//                            catch (Exception exIssueError)
//                            {
//                                message = twoSpace + "Error : " + exIssueError.getMessage() + " for the issue :" + issue.getKey();
//                                status = util.WriteToFile(project.getLogFile(), message);
//                            }
//                            if (closedIssueDate != null && reOpenIssueDate != null)
//                            {
//                                issueDateVariance = new IssueDateVariance();
//                                issueDateVariance.setCommitedDate(closedIssueDate);
//                                issueDateVariance.setClosedDate(reOpenIssueDate);
//                                issueDateVariance.setVariance((double) varianceDate);
//                                issueDateVariance.setKey(issue.getKey());
//                                issueDateVariance.setType(issue.getFields().getIssuetype().getName());
//                                issueDateVariance.setIssueStatus(issue.getFields().getStatus().getName());
//                                issueDateVariance.setCommitedDateString(strClosedDate);
//                                issueDateVariance.setClosedDateString(strReopenDate);
//                                varianceDate = util.GetDayVariance(closedIssueDate, reOpenIssueDate, lstHolidays, strCheckHolidays, strCheckWeekend);
//                                if (varianceDate <= limitFromConfig)
//                                {
//                                    issueDateVariance.setStatus("Met");
//                                }
//                                else {
//                                    issueDateVariance.setStatus("Not Met");
//                                }
//                                eligibleissue.add(issueDateVariance);
//
//
//                            }
//                            if (project.getDetailedLogRequired().equals("Y"))
//                            {
//                                dataLinesDetailed.add(new String[]
//                                        {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName(), strClosedDate, strReopenDate});
//                            }
//
//                        }
//                    }
//
//                    if (eligibleissue != null && eligibleissue.size() > 0) {
//                        totalnumCount = (int) eligibleissue.stream().filter(x -> x.getStatus().equals("Met")).count();
//
//                        for (IssueDateVariance issueData : eligibleissue) {
//
//                            if (project.getDatafileRequired().equals("Y"))
//                            {
//
//                                dataLines.add(new String[]
//                                        {issueData.getKey(), issueData.getType(), issueData.getIssueStatus(),
//                                                strClosedDate,
//                                                strReopenDate,
//                                                String.valueOf(issueData.getVariance()), issueData.getStatus()});
//                            }
//                        }
//                    }
//                }
//
//                message += newLine + twoSpace + " Total Denominator = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                double totalDenoCountActual = totaldenoCount;
//                double totalNumCountActual = totalnumCount;
//
//                if (totaldenoCount == 0 && totalnumCount == 0) {
//                    totaldenoCount = 1;
//                    totalnumCount = 1;
//                } else if (totaldenoCount == 0) {
//                    totaldenoCount = 1;
//                }
//
//                if (dataLines.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//                if (dataLinesDetailed.size()> 0)
//                {
//                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
//                    try
//                    {
//                        boolean csvStatus = util.WriteToCSv(dataLinesDetailed, dataFileName1);
//                        if (csvStatus == true)
//                        {
//                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + dataFileName1;
//                        }
//                        else
//                        {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    }
//                    catch (Exception exCsv)
//                    {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                actualValue = util.GetActualValueV1(totaldenoCount, totalnumCount);
//                if (actualValue <= minsla) {
//                    slaStatus = "Met";
//                } else {
//                    slaStatus = "Not Met";
//                }
//
//                //slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(totalNumCountActual), String.valueOf(totalDenoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type", "Final Issue Status", "commitedDate", "closedDate",
                                    "Variance (In Days)",  "Eligible"});                }
                if (project.getDetailedLogRequired().equals("Y"))
                {
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type", "Final Issue Status", "commitedDate", "closedDate"});
                }


                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                workitems =iAdoDataService.getWorkitems(userName,password,project.getProjecturl(),ADQuery,"POST",false,true,true,100);

                if(workitems!=null && workitems.size()>0)
                {
                    totaldenoCount = workitems.size();
                    Date dtClose = new Date();
                    for (WorkItem witem : workitems)
                    {
                        if (project.getDetailedLogRequired().equals("Y"))
                        {
                            dataLinesDetailed.add(new String[]
                                    {
                                            witem.getId(),witem.getFields().getWorkItemType(), witem.getFields().getState(),
                                    });
                        }
                        if (witem.getRevisions() != null && witem.getRevisions().size() > 0)
                        {
                            List<RevisionValue> revlist= new ArrayList<>();
                            revlist=witem.getRevisions();
                            long newCount =0l;

                            String strNew="";
                            int count = 1;

                            boolean isValid = true;
                            for (RevisionValue revision : revlist)
                            {

                                if((revision.getFields().getState().equalsIgnoreCase(closedStatus)  ))
                                {
                                    strClosedDate= revision.getFields().getStateChangeDate();
                                    dtClose = util.ConvertStringToDateForZFormat(strClosedDate);
                                    isValid =false;
                                }
                                if(Status1.contains(revision.getFields().getState()) && isValid == false)
                                {
                                    newCount++;
                                    strNew= revision.getFields().getStateChangeDate();
                                    newDate=util.ConvertStringToDateForZFormat(strNew);
                                    closeDate = dtClose;
                                }

                                if (newCount >= count) {
                                    varianceDate = util.GetDayVariance(closeDate, newDate, lstHolidays, strCheckHolidays, strCheckWeekend);



                                    if (varianceDate >= 0) {
                                        issueDateVariance.setKey(witem.getId());
                                        issueDateVariance.setCommitedDate(closeDate);
                                        issueDateVariance.setClosedDate(newDate);
                                        issueDateVariance.setVariance((double) varianceDate);
                                        issueDateVariance.setIssueStatus(witem.getFields().getState());
                                        issueDateVariance.setType(witem.getFields().getWorkItemType());

                                    }

                                }


                            }

                            if(varianceDate <= 3 && varianceDate == 0)
                            {
                                issueDateVariance.setStatus("Met");
                            }
                            else {
                                issueDateVariance.setStatus("NotMet");
                            }
                            if(varianceDate <= 3 && varianceDate == 0)
                            {
                                eligibleissue.add(issueDateVariance);
                            }


                            if(issueDateVariance.getStatus().equalsIgnoreCase("Met"))
                            {
                                totalnumCount++;

                            }


                        }

                    }
                    if (eligibleissue != null && eligibleissue.size() > 0)
                    {

                        for (IssueDateVariance issueData : eligibleissue)
                        {
                            if (project.getDatafileRequired().equals("Y"))
                            {
                                dataLines.add(new String[]
                                        {issueData.getKey(), issueData.getType(), issueData.getIssueStatus(),
                                                String.valueOf(closeDate),
                                                String.valueOf(newDate),
                                                String.valueOf(issueData.getVariance()), issueData.getStatus()});
                            }
                            break;
                        }
                    }
                }




                message += newLine + twoSpace + " Total Denominator = " + totaldenoCount;
                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
                message += newLine + twoSpace + " AdoQuery = " + ADQuery;

                double totalDenoCountActual = totaldenoCount;
                double totalNumCountActual = totalnumCount;

                if (totaldenoCount == 0 && totalnumCount == 0) {
                    totaldenoCount = 1;
                    totalnumCount = 1;
                } else if (totaldenoCount == 0) {
                    totaldenoCount = 1;
                }

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
                actualValue = util.GetActualValueV1(totaldenoCount, totalnumCount);
                if (actualValue <= minsla) {
                    slaStatus = "Met";
                } else {
                    slaStatus = "Not Met";
                }

                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
                message += newLine + twoSpace + " Status = " + slaStatus;

                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(totalNumCountActual), String.valueOf(totalDenoCountActual),ADQuery);
                status = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            return null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }



    private ProcessedData SystemUpTime(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        //Done by Arun. To be tested
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> numIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();

        int pageSize = 1000;
        String strPageSize = "";

        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                expectedsla = Double.valueOf(sla.getExpectedsla());
//                minsla = Double.valueOf(sla.getMinimumsla());
//
//                if (expectedsla == 0 || minsla == 0) {
//                    //Stop the processing
//                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                //Where is the validation
//                String strTotalNumCount = "";
//                String strTotalDenoCount = "";
//
//                strTotalNumCount = sla.getConfig1();
//                strTotalDenoCount = sla.getConfig2();
//
//                if (strTotalNumCount.isEmpty()) {
//                    message = twoSpace + "Num count not found, Check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                try {
//                    totalnumCount = Integer.parseInt(strTotalNumCount);
//                } catch (Exception exparse) {
//                    message = twoSpace + "Cannot able to parse Num count value, Check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (strTotalDenoCount.isEmpty()) {
//                    message = twoSpace + "Deno count not found, Check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                try {
//                    totaldenoCount = Integer.parseInt(strTotalDenoCount);
//                } catch (Exception exparse) {
//                    message = twoSpace + "Cannot able to parse Deno count value, Check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                //If user configuration is true to create the datafile then save it
//
//                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
//                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back
//
//                if (totaldenoCount == 0 && totalnumCount == 0) {
//                    totaldenoCount = 1;
//                    totalnumCount = 1;
//                } else if (totaldenoCount == 0) {
//                    totaldenoCount = 1;
//                }
//
//                double actualValue = 0;
//                actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, expectedsla, minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            return null;
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;
    }




    private ProcessedData PercentageofNBServicesAutomate(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<Issue> denoIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        IssueDateVariance issueDateVariance = new IssueDateVariance();
        List<History> Historylist = new ArrayList<>();
        List<Item> ItemList = new ArrayList<>();
        Date closeDate = new Date();
        double totDenoCount = 0;
        double totNumCount = 0;
        double actualValue = 0;
        String inDevelopmentStatus = "";
        String sourcedateFormat = "";
        closeDate = null;
        String holidayList = "";
        String projectdateFormat = "";
        int limitFromConfig = 0;
        List<Date> lstHolidays = new ArrayList<>();
        Date dtFromDate = null;
        Date dtToDate = null;
        String strCheckHolidays = "";
        String strCheckWeekend = "";
        int pageSize = 1000;
        String strPageSize = "";
        boolean isconditionsatisfied = false;
        String closeddate = "";
        String commitedDate = "";
        List<IssueDateVariance> eligibleissue = new ArrayList<>();

        String strCheckClosedDateInField = sla.getConfig4();
        Date closedIssueDate = new Date();
        Date CommitedIssuedate = new Date();
        String strClosedDate = "";
        String strcommitedDate = "";
        String strCloseStatusOnIssue = "";
        String strcommitedStatusOnIssue = "";
        long varianceDate = 0;
        String dateFormatFromConfig = "";
        History closedHistory = new History();
        History committedHistory = new History();
        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                sourcedateFormat = project.getSourceDateFormat();
//                if (sourcedateFormat.equals("")) {
//                    message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                dateFormatFromConfig = project.getDateFormat();
//                if (dateFormatFromConfig.equals("")) {
//                    message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                strPageSize = project.getPageSize();
//                if (!strPageSize.isEmpty()) {
//                    try {
//                        pageSize = Integer.parseInt(strPageSize);
//                    } catch (Exception exPageSizeParse) {
//                        pageSize = 1000;
//                    }
//                }
//
//                if (strCheckClosedDateInField.isEmpty()) {
//                    strCheckClosedDateInField = "N";
//                }
//
//                String statusForClose = sla.getConfig1().replace("'", "");
//                if (statusForClose.equals("")) {
//                    message = twoSpace + "Closed status (Development/Testing) for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String closedStatus = "";
//                String prevClosedStatus = "";
//                String[] arrClosedStatus = statusForClose.split(",");
//
//                if (arrClosedStatus != null && arrClosedStatus.length > 2) {
//                    message = twoSpace + "Only Two status(s) can be mentioned for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//                if (arrClosedStatus != null && arrClosedStatus.length >= 1) {
//                    closedStatus = arrClosedStatus[0];
//                }
//
//                if (arrClosedStatus != null && arrClosedStatus.length >= 2) {
//                    prevClosedStatus = arrClosedStatus[1];
//                }
//
//                if (closedStatus.isEmpty()) {
//                    message = twoSpace + "Closed Status for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//                denourl = "";
//                if (sla.getDenojql().isEmpty()) {
//                    message = twoSpace + "Deno JQL not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                denourl = project.getProjecturl() + "/api/2/search?jql=" + sla.getDenojql();
//                expectedsla = Integer.parseInt(sla.getExpectedsla());
//                minsla = Integer.parseInt(sla.getMinimumsla());
//
//                if (expectedsla == 0 || minsla == 0) {
//                    //Stop the processing
//                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, true, pageSize);
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Status", "closed Date"});
//                }
//
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//                    eligibleissue = new ArrayList<>();
//                    for (Issue issue : denoIssue) {
//                        if (issue != null) {
//                            try {
//                                closedIssueDate = null;
//
//                                strClosedDate = "";
//                                strcommitedDate = "";
//                                strCloseStatusOnIssue = "";
//                                strcommitedStatusOnIssue = "";
//                                varianceDate = 0;
//
//                                closedHistory = null;
//                                committedHistory = null;
//
//                                List<History> historyList = new ArrayList<>();
//                                if (issue.getChangelog() != null) {
//                                    if (issue.getChangelog().getHistories() != null && issue.getChangelog().getHistories().size() > 0) {
//                                        historyList = issue.getChangelog().getHistories();
//                                    }
//                                }
//
//                                //Get Committed Date --> Not required here
//
//                                //Get Closed Date
//                                if (strCheckClosedDateInField.equals("N")) {
//                                    if (historyList != null && historyList.size() > 0) {
//                                        boolean firstOccuranceClosed = false;
//                                        boolean firstOccuranceFTR = false;
//                                        for (History history : historyList) {
//                                            List<Item> itemList = new ArrayList<>();
//                                            if (history.getItems() != null && history.getItems().size() > 0) {
//                                                itemList = history.getItems();
//
//                                                //Check the first Occurance of closed status
//                                                if (closedHistory == null) {
//                                                    for (Item item : itemList) {
//                                                        if (item.getField().equals("status")) {
//                                                            if (!closedStatus.isEmpty()) {
//                                                                if (firstOccuranceClosed == false) {
//                                                                    if (item.getToString().equals(closedStatus.trim())) {
//                                                                        closedHistory = history;
//                                                                        firstOccuranceClosed = true;
//                                                                        strCloseStatusOnIssue = item.getToString();
//                                                                        strClosedDate = history.getCreated();
//                                                                    }
//                                                                }
//                                                            }
//
//                                                            if (!prevClosedStatus.isEmpty()) {
//                                                                if (firstOccuranceClosed == false) {
//                                                                    if (item.getToString().equals(prevClosedStatus.trim())) {
//                                                                        closedHistory = history;
//                                                                        firstOccuranceClosed = true;
//                                                                        strCloseStatusOnIssue = item.getToString();
//                                                                        strClosedDate = history.getCreated();
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//
//                                                //Find the Occurance of commited Status as we got the Closed Status. We need to compare the date also
//
//                                            }
//                                        }
//                                        if (closedHistory != null) {
//                                            strClosedDate = closedHistory.getCreated();
//                                            if (!strClosedDate.isEmpty()) {
//                                                if (util.isDateValid(strClosedDate, sourcedateFormat) == true) {
//                                                    closedIssueDate = util.ConvertToDate(strClosedDate, sourcedateFormat);
//                                                }
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    if (strCheckClosedDateInField.equals("Resolution")) {
//                                        strClosedDate = issue.getFields().getResolutiondate();
//                                    }
//
//                                    if (!strClosedDate.isEmpty()) {
//                                        if (util.isDateValid(strClosedDate, sourcedateFormat) == true) {
//                                            closedIssueDate = util.ConvertToDate(strClosedDate, sourcedateFormat);
//                                        }
//                                    }
//                                }
//                            } catch (Exception exIssueError) {
//                                message = twoSpace + "Error : " + exIssueError.getMessage() + " for the issue :" + issue.getKey();
//                                status = util.WriteToFile(project.getLogFile(), message);
//                            }
//
//                            issueDateVariance = new IssueDateVariance();
//                            issueDateVariance.setClosedDate(closedIssueDate);
//                            issueDateVariance.setKey(issue.getKey());
//                            issueDateVariance.setType(issue.getFields().getIssuetype().getName());
//                            issueDateVariance.setIssueStatus(issue.getFields().getStatus().getName());
//
//                            eligibleissue.add(issueDateVariance);
//                        }
//                    }
//
//                }
//                if (eligibleissue != null && eligibleissue.size() > 0) {
//
//
//                    for (IssueDateVariance issueData : eligibleissue) {
//
//                        strClosedDate = "";
//
//                        if (project.getDatafileRequired().equals("Y")) {
//
//                            if (issueData.getClosedDate() != null) {
//                                strClosedDate = String.valueOf(issueData.getClosedDate());
//                            }
//                            if (issueData.getClosedDate() != null) {
//                                totalnumCount++;
//                            }
//
//                            dataLines.add(new String[]
//                                    {issueData.getKey(), issueData.getType(), issueData.getIssueStatus(),
//                                            strClosedDate});
//                        }
//                    }
//                }
//                message += newLine + twoSpace + " Total Denominator = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                double totalDenoCountActual = totaldenoCount;
//                double totalNumCountActual = totalnumCount;
//
//                if (totaldenoCount == 0 && totalnumCount == 0) {
//                    totaldenoCount = 1;
//                    totalnumCount = 1;
//                } else if (totaldenoCount == 0) {
//                    totaldenoCount = 1;
//                }
//
//                if (dataLines.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                actualValue = util.GetActualValueV1(totaldenoCount, totalnumCount);
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + actualValue;
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(totalNumCountActual), String.valueOf(totalDenoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//
//
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            return null;
        } catch (Exception ex) {
            return null;
        }

    }

    private ProcessedData ITCustomerSatisfication(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        String strActual = sla.getConfig1();
        double actualValue = 0;

        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//
//                expectedsla = Double.valueOf(sla.getExpectedsla());
//                minsla = Double.valueOf(sla.getMinimumsla());
//
//                if (expectedsla == 0 || minsla == 0) {
//                    //Stop the processing
//                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (strActual.isEmpty()) {
//                    message = twoSpace + "Actual value is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                try {
//                    actualValue = Double.parseDouble(strActual);
//                } catch (Exception exParse) {
//                    message = twoSpace + "Not able to parse Actual value, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (actualValue <= minsla) {
//                    slaStatus = "Not Met";
//                } else {
//                    slaStatus = "Met";
//                }
//
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, "", "");
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            return null;
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;


    }

    private ProcessedData NotifyToCustomerOfOutrage(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        try {
            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
                message = twoSpace + "Not Implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;

            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            return null;
        } catch (Exception ex) {
            return null;
        }

    }








    private ProcessedData RegulatoryUpdate(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();

        // ADO Objects, variables start
        List<WorkItem> workitemsCreatedDates = new ArrayList<>();
        List<WorkItem> workitemsClosedDates = new ArrayList<>();
        List<WorkItem> workitemsBetweenDates = new ArrayList<>();

        String ADDenoQuery = "";
        String ADNumQuery = "";

        String dateFormatFromConfig = "";
        String strPageSize = "";
        double actualValue = 0;
        int pageSize = 0;
        String closedStatus = "";
        String prevClosedStatus = "";
        Date committedDate = null;
        Date closedDate = null;
        Date dueDate = null;
        String strCommittedDate = "";
        String strClosedDate = "";
        String strDuedate = "";
        String sourcedateFormat = "";
        List<RevisionValue> revisionValues = new ArrayList<>();
        //MEthod Specific Variables
        IssueActivityDate issueActivityDate = new IssueActivityDate();
        List<IssueDateVariance> elligibleIssues = new ArrayList<>();
        IssueDateVariance issueDateVariance = new IssueDateVariance();

        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        try
        {

            sourcedateFormat = project.getSourceDateFormat();
            if (sourcedateFormat.equals("")) {
                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
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
            dateFormatFromConfig = project.getDateFormat().replace("'", "");
            if (dateFormatFromConfig.equals("")) {
                message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            String holidayList = project.getHolidays();
            if (holidayList.equals("")) {
                message = twoSpace + "Holiday details not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            String[] arrHoliday = holidayList.split(",");
            List<Date> lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);

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

            String strCheckHolidays = sla.getInput2();
            if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
                strCheckHolidays = "N";
            }

            String strCheckWeekend = sla.getInput3();
            if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
                strCheckWeekend = "N";
            }

            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
                ProcessedData regulatory = JiraProcessWithJQLCountGeneric(sla, userName, password, project, retrievedIssues);
                return regulatory;
            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {

                String strCheckCommittedField = sla.getConfig1();
                String strCheckClosedField = sla.getConfig2();
                String checkCommitedInField = sla.getConfig3();
                String checkClosedInField = sla.getConfig4();
                String strRegularUpdate = sla.getInput1();
                String strProjectName = sla.getInput5();
                String strFromDate = sla.getFrom();
                String strToDate = sla.getTo();

                if (!strFromDate.equals("")) {
                    if (util.isDateValid(strFromDate, dateFormatFromConfig) == false) {
                        message = twoSpace + "From Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
                        status = util.WriteToFile(project.getLogFile(), message);
                        return null;
                    }
                } else {
                    message = twoSpace + "From Date is not found, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (!strToDate.equals("")) {
                    if (util.isDateValid(strToDate, dateFormatFromConfig) == false) {
                        message = twoSpace + "To Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
                        status = util.WriteToFile(project.getLogFile(), message);
                        return null;
                    }
                } else {
                    message = twoSpace + "To Date is not found, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
                if (project.getDetailedLogRequired().equals("Y")) {
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type", "Final Issue Status", "commitedDate", "closedDate"});
                }
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type", "Final Issue Status", "commitedDate", "closedDate"});
                }
                workitemsClosedDates=new ArrayList<>();
                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
                    ADDenoQuery = "{\n" +
                            "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";
                    workitemsClosedDates = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADDenoQuery, "POST", false, true, true, pageSize);
                }
                try {
                    committedDate = null;
                    closedDate = null;
                    dueDate = null;
                    Date fromDate = new SimpleDateFormat("MM/dd/yyyy").parse(strFromDate);
                    Date toDate = new SimpleDateFormat("MM/dd/yyyy").parse(strToDate);
                    if (workitemsClosedDates != null && workitemsClosedDates.size() > 0) {
                        for (WorkItem witemClosed : workitemsClosedDates) {
                            if (witemClosed != null) {
                                if (witemClosed.getRevisions() != null) {
                                    revisionValues = witemClosed.getRevisions();
                                }
//                                if (checkCommitedInField.equals("N")) {
//                                    if (revisionValues != null & revisionValues.size() > 0) {
//                                        IssueActivityDate issueActivityDates = util.getADOWorkItemActivityDate(witemClosed.getId(), revisionValues, strCheckCommittedField, sourcedateFormat);
//                                        if (issueActivityDates != null) {
//                                            if (issueActivityDates.getRequestedDate() != null) {
//                                                committedDate = issueActivityDates.getRequestedDate();
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    if (checkCommitedInField.equals("Created")) {
//                                        strCommittedDate = witemClosed.getFields().getCreatedDate();
//                                    } else if (checkCommitedInField.equals("Updated")) {
//                                        strCommittedDate = witemClosed.getFields().getChangedDate();
//                                    }
//                                    if (!strCommittedDate.isEmpty()) {
//                                        committedDate = util.ConvertStringToDateForZFormat(strCommittedDate);
//
//                                    }
//                                }
                                if (checkClosedInField.equals("N")) {
                                    //Check the closed date by looking to the configuration from the histroy
                                    revisionValues = witemClosed.getRevisions();
                                    boolean firstOccuranceClosedDate = false;

                                    if (revisionValues != null & revisionValues.size() > 0) {
                                        for (RevisionValue revisionValuedate : revisionValues) {
                                            if (revisionValuedate != null) {
                                                if (!closedStatus.isEmpty()) {
                                                    if (firstOccuranceClosedDate == false) {
                                                        if (revisionValuedate.getFields().getState().equals(closedStatus)) {
                                                            firstOccuranceClosedDate = true;
                                                            strClosedDate = revisionValuedate.getFields().getStateChangeDate();
                                                            break;
                                                        }

                                                    }

                                                    if (!prevClosedStatus.isEmpty()) {
                                                        if (firstOccuranceClosedDate == false) {
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
                                    }
                                    if (!strClosedDate.isEmpty()) {
                                        closedDate = util.ConvertStringToDateForZFormat(strClosedDate);
                                    }
                                } else {
                                    if (checkClosedInField.equals("Closed")) {
                                        //Take the date from Workitem Closed date
                                        strClosedDate = witemClosed.getFields().getClosedDate();
                                    } else if (checkClosedInField.equals("Resolved")) {
                                        //Take the date from Workitem Resolution date
                                        strClosedDate = witemClosed.getFields().getResolvedDate();
                                    }
                                    if (!strClosedDate.isEmpty()) {
                                        if (util.isDateValid(strClosedDate, sourcedateFormat) == true) {
                                            closedDate = util.ConvertStringToDateForZFormat(strClosedDate);
                                        }
                                    }
                                }
                                strDuedate = witemClosed.getFields().getDueDate();
                                if (strDuedate != null && !strDuedate.isEmpty()) {
                                    dueDate = util.ConvertStringToDateForZFormat(strDuedate);
                                }
                                if(closedDate!=null && dueDate!=null) {
                                    if (((closedDate.compareTo(dueDate) <= 0 ) == true)) {
                                        issueDateVariance.setStatus("Met");
                                    } else {
                                        issueDateVariance.setStatus("Not Met");
                                    }
                                }
                                issueDateVariance = new IssueDateVariance();
                                issueDateVariance.setKey(witemClosed.getId());
                                issueDateVariance.setType(witemClosed.getFields().getWorkItemType());
                                issueDateVariance.setIssueStatus(witemClosed.getFields().getState());
                                if (closedDate != null) {
                                    issueDateVariance.setClosedDate(closedDate);

                                }
                                elligibleIssues.add(issueDateVariance);
                                if (project.getDetailedLogRequired().equals("Y")) {
                                    String tType = "";
                                    String tStatus = "";
                                    String tClosedDate = "";
                                    String tCreatedDate = "";

                                    if (witemClosed.getFields().getWorkItemType() != null) {
                                        tType = witemClosed.getFields().getWorkItemType();
                                    }

                                    if (witemClosed.getFields().getState() != null) {
                                        tStatus = witemClosed.getFields().getState();
                                    }
                                    if (committedDate != null) {
                                        tCreatedDate = util.ConvertDateToString(committedDate, sourcedateFormat);
                                    }

                                    if (closedDate != null) {
                                        tClosedDate = util.ConvertDateToString(closedDate, sourcedateFormat);
                                    }

                                    dataLinesDetailed.add(new String[]
                                            {witemClosed.getId(), tType, tStatus, tCreatedDate, tClosedDate});
                                }
//                                strDuedate = witemClosed.getFields().getDueDate();
//                                if (strDuedate != null && !strDuedate.isEmpty()) {
//                                    dueDate = util.ConvertStringToDateForZFormat(strDuedate);
//                                }
//                                if (closedDate != null && dueDate != null) {
//                                    if (closedDate.before(dueDate) || closedDate.equals(dueDate)) {
//                                        issueDateVariance.setStatus("Met");
//                                    } else {
//                                        issueDateVariance.setStatus("Not Met");
//                                    }
//                                    elligibleIssues.add(issueDateVariance);
//                                    if (project.getDetailedLogRequired().equals("Y")) {
//                                        String tType = "";
//                                        String tStatus = "";
//                                        String tClosedDate = "";
//                                        String tCreatedDate = "";
//
//                                        if (witemClosed.getFields().getWorkItemType() != null) {
//                                            tType = witemClosed.getFields().getWorkItemType();
//                                        }
//
//                                        if (witemClosed.getFields().getState() != null) {
//                                            tStatus = witemClosed.getFields().getState();
//                                        }
//                                        if (committedDate != null) {
//                                            tCreatedDate = util.ConvertDateToString(committedDate, sourcedateFormat);
//                                        }
//
//                                        if (closedDate != null) {
//                                            tClosedDate = util.ConvertDateToString(closedDate, sourcedateFormat);
//                                        }
//
//                                        dataLinesDetailed.add(new String[]
//                                                {witemClosed.getId(), tType, tStatus, tCreatedDate, tClosedDate});
//                                    }
//
//                                }

                            }
                        }
                    }

                    if (elligibleIssues != null && elligibleIssues.size() > 0) {
                        totaldenoCount= elligibleIssues.size();
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

                            if (project.getDatafileRequired().equals("Y")) {
                                String type = "";
                                String status = "";

                                if (iv.getType() != null) {
                                    type = iv.getType();
                                }

                                if (iv.getIssueStatus() != null) {
                                    status = iv.getIssueStatus();
                                }


                                dataLines.add(new String[]
                                        {iv.getKey(), type, status, strCommittedDate, strClosedDate, iv.getStatus()});

                            }
                        }
                    }

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

                    message = twoSpace + " Total Denominator Count = " + totaldenoCount;
                    message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
                    message += newLine + twoSpace + " AdoQuery = " + ADDenoQuery;
                    message += newLine + twoSpace + " Minimum SLA = " + minsla;
                    message += newLine + twoSpace + " Expected SLA = " + expectedsla;

                    double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                    double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                    if (totaldenoCount == 0 && totalnumCount == 0) {
                        totaldenoCount = 1;
                        totalnumCount = 1;
                    } else if (totaldenoCount == 0) {
                        totaldenoCount = 1;
                    }

                    actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
                    message += newLine + twoSpace + " Actual = " + actualValue;
                    slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
                    message += newLine + twoSpace + " Status = " + slaStatus;

                    ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),ADDenoQuery);
                    boolean isStatus = util.WriteToFile(project.getLogFile(), message);
                    return data;

                } catch (Exception exception) {
                }

            }

            return null;
        } catch (Exception ex) {
            message = twoSpace +  "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }
        return null;
    }


    private ProcessedData CriticalSecurityThreatMitigation(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> numIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();
        List<Date> lstHolidays;

        double variance = 0f;
        double finalLimitValue = 0.0;
        double actualValue = 0;
        String holidayList = "";
        String dateFormatFromConfig = "";
        String strPageSize = "";
        int pageSize = 0;
        String closedStatus = "";
        String prevClosedStatus = "";
        String includeCommittedDate = "";
        Date committedDate = null;
        Date closedDate = null;
        String strCommittedDate = "";
        String strClosedDate = "";
        String sourcedateFormat = "";
        float limitValue = 0.f;
        String strLimitValue = "";
        // ADO Variables
        List<WorkItem> denoWorkitems = new ArrayList<>();
        List<WorkItem> numWorkitems = new ArrayList<>();
        List<RevisionValue> revisionValues = new ArrayList<>();
        String denoQuery = "";
        String numQuery = "";
        IssueDateVariance issueDateVariance = new IssueDateVariance();
        List<IssueDateVariance> eligibleissue = new ArrayList<>();
        List<Item> itemList = new ArrayList<>();
        int denoCount = 0;
        int numCount = 0;

        try {
            sourcedateFormat = project.getSourceDateFormat();
            if (sourcedateFormat.equals("")) {
                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
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
            String endDate = sla.getInput1();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            strLimitValue = sla.getLimit();
            if (strLimitValue.isEmpty()) {
                message = twoSpace + "Limit value not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            try {
                limitValue = Float.parseFloat(sla.getLimit());
            } catch (Exception exParse) {
                message = twoSpace + "Unable to parse Limit value, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            dateFormatFromConfig = project.getDateFormat().replace("'", "");
            if (dateFormatFromConfig.equals("")) {
                message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            holidayList = project.getHolidays();
            if (holidayList.equals("")) {
                message = twoSpace + "Holiday details not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            String[] arrHoliday = holidayList.split(",");
            lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);

            if (lstHolidays == null) {
                message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String committedStatus = sla.getConfig1();
            String closeStatus = sla.getConfig2();
            String checkCommitedInField = sla.getConfig3();
            String checkClosedInField = sla.getConfig4();
            String strCheckHolidays = sla.getInput2();
            String strCheckWeekend = sla.getInput3();
            includeCommittedDate = sla.getInput4();
            if ((!StringUtils.hasText(committedStatus) || committedStatus.equals("")) && (!StringUtils.hasText(checkCommitedInField) || checkCommitedInField.equals(""))) {
                message = twoSpace + "Please provide committed status or Field name to check the committed date, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }


            if (!StringUtils.hasText(closeStatus) || closeStatus.equals("") && (!StringUtils.hasText(checkClosedInField) || checkClosedInField.equals(""))) {
                message = twoSpace + "Please provide Closed status or Field name to check the closed date, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String[] arrClosedStatus = closeStatus.split(",");
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

            if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
                strCheckHolidays = "N";
            }

            if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
                strCheckWeekend = "N";
            }

            if (includeCommittedDate.equals("") || !includeCommittedDate.equals("Y")) {
                includeCommittedDate = "N";
            }

            //Added by Nikhil
            if (project.getProjecturl() == null || project.getProjecturl().isEmpty()) {
                message = twoSpace + "Data Source URL is not available, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//
//                if (sla.getDenojql().isEmpty()) {
//                    message = twoSpace + "Dino Query not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=" + sla.getDenojql() + "&expand=changelog";
//                }
//
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=" + sla.getNumjql() + "&expand=changelog";
//                }
//
//
//                if (!denourl.isEmpty()) {
//                    denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, true, pageSize);
//                }
//
//                if (!numurl.isEmpty()) {
//                    numIssue = iJiraDataService.getIssuesUsingJQL(userName, password, numurl, "", false, true, pageSize);
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Status", "commited Date", "closed Date", "Variance (In Days)", "eligible"});
//                }
//
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                message = "";
//
//                //Get the Denominator Count
//                if (!numurl.isEmpty()) {
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"Denominator Issues (Planned)", "", "", "", "", "", ""});
//                    }
//                }
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//
//                    if (!numurl.isEmpty()) {
//                        //This means we have the Num Issues retrieved. So we will not process the deno issues.
//                        //So we will print the deno issues first
//                        for (Issue issue : denoIssue) {
//                            if (project.getDatafileRequired().equals("Y")) {
//                                dataLines.add(new String[]
//                                        {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName(), "", "", "", ""});
//                            }
//                        }
//                    }
//                }
//
//                if (!numurl.isEmpty()) {
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"", "", "", "", "", "", ""});
//                    }
//
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"Numerator Issues (Completed)", "", "", "", "", "", ""});
//                    }
//
//                    //Also assign Num Issues to the Deno Issue variable so that we can process the Num Issues here
//                    denoIssue = numIssue;
//                }
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    eligibleissue = new ArrayList<>();
//                    for (Issue issue : denoIssue) {
//                        committedDate = null;
//                        closedDate = null;
//                        strCommittedDate = "";
//                        strClosedDate = "";
//                        issueDateVariance = null;
//
//                        if (issue != null) {
//                            try {
//                                List<History> historyList = new ArrayList<>();
//                                if (issue.getChangelog() != null) {
//                                    if (issue.getChangelog().getHistories() != null && issue.getChangelog().getHistories().size() > 0) {
//                                        historyList = issue.getChangelog().getHistories();
//                                    }
//                                }
//
//                                //Get the Committed Date
//                                if (checkCommitedInField.equals("N")) {
//                                    if (historyList != null && historyList.size() > 0) {
//                                        IssueActivityDate issueActivityDate = util.getIssueActivityDate(issue.getKey(), historyList, committedStatus, sourcedateFormat, "status");
//
//                                        if (issueActivityDate != null) {
//                                            if (issueActivityDate.getRequestedDate() != null) {
//                                                committedDate = issueActivityDate.getRequestedDate();
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    if (checkCommitedInField.equals("Created")) {
//                                        strCommittedDate = issue.getFields().getCreated();
//                                    } else if (checkCommitedInField.equals("Updated")) {
//                                        strCommittedDate = issue.getFields().getUpdated();
//                                    }
//
//                                    if (!strCommittedDate.isEmpty()) {
//                                        committedDate = util.ConvertToDate(strCommittedDate, sourcedateFormat);
//                                    }
//                                }
//
//                                //Get the Closed Date
//                                if (checkClosedInField.equals("N")) {
//                                    boolean firstOccuranceClosedDate = false;
//                                    for (History history : historyList) {
//                                        itemList = null;
//                                        if (firstOccuranceClosedDate == true) {
//                                            break;
//                                        }
//
//                                        if (history.getItems() != null && history.getItems().size() > 0) {
//                                            itemList = history.getItems();
//                                            if (!closedStatus.isEmpty()) {
//                                                if (firstOccuranceClosedDate == false) {
//                                                    for (Item item : itemList) {
//                                                        if (item != null) {
//                                                            if (item.getField().equals("status")) {
//                                                                if (item.getToString().equals(closedStatus.trim())) {
//                                                                    firstOccuranceClosedDate = true;
//                                                                    strClosedDate = history.getCreated();
//                                                                    break;
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            if (!prevClosedStatus.isEmpty()) {
//                                                if (firstOccuranceClosedDate == false) {
//                                                    for (Item item : itemList) {
//                                                        if (item != null) {
//                                                            if (item.getField().equals("status")) {
//                                                                if (item.getToString().equals(prevClosedStatus.trim())) {
//                                                                    firstOccuranceClosedDate = true;
//                                                                    strClosedDate = history.getCreated();
//                                                                    break;
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    if (!strClosedDate.isEmpty()) {
//                                        closedDate = util.ConvertToDate(strClosedDate, sourcedateFormat);
//                                    }
//                                } else {
//                                    if (checkClosedInField.equals("Resolution")) {
//                                        strClosedDate = issue.getFields().getResolutiondate();
//                                    }
//
//                                    if (!strClosedDate.isEmpty()) {
//                                        closedDate = util.ConvertToDate(strCommittedDate, sourcedateFormat);
//                                    }
//                                }
//
//                                //Next processing
//                                issueDateVariance = new IssueDateVariance();
//                                issueDateVariance.setKey(issue.getKey());
//                                issueDateVariance.setType(issue.getFields().getIssuetype().getName());
//                                issueDateVariance.setIssueStatus(issue.getFields().getStatus().getName());
//                                issueDateVariance.setCommitedDate(committedDate);
//                                issueDateVariance.setClosedDate(closedDate);
//                                issueDateVariance.setVariance(0.0);
//
//                                if (committedDate != null && closedDate != null) {
//                                    variance = util.GetDayVariance(committedDate, closedDate, lstHolidays, strCheckHolidays, strCheckWeekend, includeCommittedDate);
//                                    issueDateVariance.setVariance(variance);
//                                }
//
//                                if (issueDateVariance.getCommitedDate() != null && issueDateVariance.getClosedDate() != null) {
//                                    if (issueDateVariance.getVariance() <= limitValue) {
//                                        issueDateVariance.setStatus("Met");
//                                    } else {
//                                        issueDateVariance.setStatus("Not Met");
//                                    }
//                                } else {
//                                    issueDateVariance.setStatus("Not Met");
//                                }
//
//                                eligibleissue.add(issueDateVariance);
//                            } catch (Exception exIssue) {
//
//                            }
//                        }
//                    }
//                }
//
//                if (eligibleissue != null && eligibleissue.size() > 0) {
//                    //totaldenoCount=eligibleissue.size();
//                    totalnumCount = (int) eligibleissue.stream().filter(x -> x.getStatus().equals("Met")).count();
//
//                    //Data Line
//                    for (IssueDateVariance iv : eligibleissue) {
//                        strCommittedDate = "";
//                        strClosedDate = "";
//                        String strVariance = "";
//
//                        if (iv.getCommitedDate() != null) {
//                            strCommittedDate = util.ConvertDateToString(iv.getCommitedDate(), sourcedateFormat);
//                        }
//
//                        if (iv.getClosedDate() != null) {
//                            strClosedDate = util.ConvertDateToString(iv.getClosedDate(), sourcedateFormat);
//                        }
//
//                        if (iv.getCommitedDate() != null && iv.getClosedDate() != null) {
//                            strVariance = String.valueOf(iv.getVariance());
//                        }
//
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines.add(new String[]
//                                    {iv.getKey(), iv.getType(), iv.getIssueStatus(), strCommittedDate, strClosedDate, strVariance, iv.getStatus()});
//                        }
//                    }
//                }
//
//                //Preparing the data for
//                if (dataLines.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
//                        if (csvStatus == true) {
//                            message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
//                        } else {
//                            message += newLine + twoSpace + "Unable to create the data file";
//                        }
//                    } catch (Exception exCsv) {
//                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                    }
//                }
//
//                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//
//                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
//                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back
//
//                if (totaldenoCount == 0 && totalnumCount == 0) {
//                    totaldenoCount = 1;
//                    totalnumCount = 1;
//                } else if (totaldenoCount == 0) {
//                    totaldenoCount = 1;
//                }
//
//                actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//                message += newLine + twoSpace + " Actual = " + actualValue;
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                boolean isStatus = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);

                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]{ "Key", "Type", "Final Issue Status", "Severity", "commitedDate", "closedDate",
                            "Variance (In Days)", "Eligible"});
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


                denoQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";


                ////////// Business Logic Implementation Starts  //////////

                denoWorkitems  = iAdoDataService.getWorkitems(userName,password,project.getProjecturl(),denoQuery,"POST",false,true, true,100);


                if(denoWorkitems !=null && denoWorkitems.size()>0)
                {
                    totaldenoCount = denoWorkitems.size();
                    for(WorkItem witem : denoWorkitems){
                        committedDate = null;
                        closedDate = null;
                        if (witem != null)
                        {
                            try
                            {
                                if (witem.getRevisions() != null) {
                                    revisionValues = witem.getRevisions();
                                }
                                if (checkCommitedInField.equals("N")) {
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
                                    if (checkCommitedInField.equals("Created")) {
                                        strCommittedDate = witem.getFields().getCreatedDate();
                                    }
                                    else if (checkCommitedInField.equals("Updated")) {
                                        strCommittedDate= witem.getFields().getChangedDate();
                                    }

                                    if (!strCommittedDate.isEmpty())
                                    {
                                        committedDate = util.ConvertStringToDateForZFormat(strCommittedDate);
                                    }
                                }

                                if (checkClosedInField.equals("N")) {
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

                                                if (!prevClosedStatus.isEmpty())
                                                {
                                                    if (firstOccuranceClosedDate == false) {
                                                        //Check and compare the status with the record to get a match
                                                        if (revisionValuedate.getFields().getState().equals(prevClosedStatus))
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
                                        closedDate = util.ConvertStringToDateForZFormat(strClosedDate);
                                    }
                                }
                                else
                                {
                                    if (checkClosedInField.equals("Closed")) {
                                        strClosedDate = witem.getFields().getClosedDate();
                                    } else if (checkClosedInField.equals("Resolved")) {
                                        strClosedDate = witem.getFields().getResolvedDate();
                                    }

                                    if (!strClosedDate.isEmpty()) {
                                        closedDate = util.ConvertStringToDateForZFormat (strClosedDate);
                                    }
                                }

                                //numCount = witem.getFields().getSecurityThreatCount();

//                                if(numCount == 0){
//                                    totalnumCount++;
//                                }
//                                else{
//                                    totalnumCount = totalnumCount + numCount;
//                                }
//                                if(numCount == 0){
//                                    numCount = 1;
//                                }

                                variance = 0;
                                if (committedDate != null && closedDate != null)
                                {
                                    issueDateVariance = new IssueDateVariance();
                                    issueDateVariance.setKey(witem.getId());
                                    issueDateVariance.setType(witem.getFields().getWorkItemType());
                                    issueDateVariance.setIssueStatus(witem.getFields().getState());
                                    issueDateVariance.setPriority(witem.getFields().getSeverity());
                                    issueDateVariance.setVariance(0.0);
                                    issueDateVariance.setCommitedDate(committedDate);
                                    issueDateVariance.setClosedDate(closedDate);
                                    issueDateVariance.setCommitedDateString(strCommittedDate);
                                    issueDateVariance.setClosedDateString(strClosedDate);
                                    issueDateVariance.setVarianceinMin((double)numCount); //Storing the num count here

//                                    variance = Integer.parseInt(issueDateVariance.getClosedDateString()) - Integer.parseInt(issueDateVariance.getCommitedDateString());

                                    variance = util.GetDayVariance(committedDate, closedDate, lstHolidays, strCheckHolidays, strCheckWeekend, includeCommittedDate);
                                    issueDateVariance.setVariance(variance);

                                    if ( variance <= limitValue)
                                    {
                                        issueDateVariance.setStatus("Met");
                                    }
                                    else
                                    {
                                        issueDateVariance.setStatus("Not Met");
                                    }

                                    eligibleissue.add(issueDateVariance);
                                }

                                //Write to the details data file
                                if (project.getDetailedLogRequired().equals("Y"))
                                {
                                    String tType = "";
                                    String tStatus = "";
                                    String tSeverity = "";
                                    String tCommittedDate = "";
                                    String tClosedDate = "";

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

                                    if (committedDate != null)
                                    {
                                        tCommittedDate = util.ConvertDateToString(committedDate, sourcedateFormat);
                                    }

                                    if (closedDate != null)
                                    {
                                        tClosedDate = util.ConvertDateToString(closedDate, sourcedateFormat);
                                    }

                                    dataLinesDetailed.add(new String[]
                                            {witem.getId(), tType, tSeverity, tStatus, tCommittedDate, tClosedDate });
                                }

                            }
                            catch (Exception exception)
                            {

                            }
                        }
                    }
                }

                if (eligibleissue != null && eligibleissue.size() > 0)
                {

                    //Data Line
                    for (IssueDateVariance iv:eligibleissue)
                    {

                        if (iv.getStatus().equals("Met"))
                        {
                            totalnumCount++;
                        }
                        strCommittedDate = "";
                        strClosedDate = "";
                        String strVariance = "";

                        if (iv.getCommitedDate() != null)
                        {
                            strCommittedDate = util.ConvertDateToString(iv.getCommitedDate(), sourcedateFormat);
                        }

                        if (iv.getClosedDate() != null)
                        {
                            strClosedDate = util.ConvertDateToString(iv.getClosedDate(), sourcedateFormat);
                        }

                        if (iv.getCommitedDate() != null && iv.getClosedDate() != null)
                        {
                            strVariance = String.valueOf(iv.getVariance());
                        }

                        if (project.getDatafileRequired().equals("Y"))
                        {
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
                                    {iv.getKey(), type, status, priority, strCommittedDate, strClosedDate,
                                            strVariance, iv.getStatus()});

                            //                            dataLines.add(new String[]
//                                    {iv.getKey(), iv.getType(), iv.getIssueStatus(), strCommittedDate, strClosedDate, strVariance, iv.getStatus() });
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
                message += newLine + twoSpace + " AdoQuery = " + denoQuery;
                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;

                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                if (totaldenoCount == 0 && totalnumCount == 0)
                {
                    totaldenoCount = 1;
                    totalnumCount = 1;
                }
                else if (totaldenoCount == 0)
                {
                    totaldenoCount = 1;
                }

                actualValue = util.GetActualValueV1((double)totaldenoCount, (double)totalnumCount);
                message += newLine + twoSpace + " Actual = " + actualValue;
                slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double)expectedsla, (double)minsla);
                message += newLine + twoSpace + " Status = " + slaStatus;

                ProcessedData data = util.BuildProcessData(sla, (float)actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),denoQuery);
                boolean isStatus = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);

        }

        return null;
    }

    private ProcessedData Patches(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);

        String baseURI = "";
        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> numIssue = new ArrayList<>();
        List<Issue> commentIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();
        String ADDenoQuery = "";
        String ADNumQuery = "";

        // ADO Variables
        List<WorkItem> denoWorkitems = new ArrayList<>();
        List<WorkItem> numWorkitems = new ArrayList<>();
        List<WorkItem> workitemsBetweenDates = new ArrayList<>();
        List<RevisionValue> revisionValues = new ArrayList<>();
        RevisionFields revisionFields = new RevisionFields();
        RevisionValue revisionValue = new RevisionValue();
        String adourl = "";
        String adowql = "";
        String denoQuery = "";
        String numQuery = "";
        double variance = 0f;
        float limitValue = 0.f;
        String strLimitValue = "";
        Date closedDate = null;
        Date dueDate = null;

        //MEthod Specific Variables
        IssueActivityDate issueActivityDate = new IssueActivityDate();
        List<IssueDateVariance> elligibleIssues = new ArrayList<>();
        IssueDateVariance issueDateVariance = new IssueDateVariance();
        List<History> Historylist = new ArrayList<>();


        String dateFormatFromConfig = "";
        String strPageSize = "";
        double actualValue = 0;
        int pageSize = 0;
        String closedStatus = "";
        String prevClosedStatus = "";
        Date committedDate = null;
        closedDate = null;
        dueDate = null;
        String strCommittedDate = "";
        String strClosedDate = "";
        String strDuedate = "";
        String sourcedateFormat = "";
        Date workingDate = new Date();
        String strWorkingDate = "";

        boolean isValid = false;
        String inDevelopmentStatus = "";
        String closeStatus = "";
        sourcedateFormat = "";
        actualValue = 0;
        String holidayList = "";
        String projectdateFormat = "";
        String limitFromConfig = "";
        int limit = 0;
        List<Date> lstHolidays = new ArrayList<>();

        String fromdateFromConfig = "";
        String todateFromConfig = "";
        dateFormatFromConfig = "";
        Date dtFromDate;
        Date dtToDate;
        String strCheckHolidays = "";
        String strCheckWeekend = "";
        String strCheckCreatedDateInsteadHistory = "N";
        strCommittedDate = "";
        strClosedDate = "";
        String nextClosedStatus = "";
        String prevtClosedStatus = "";
        Date testDate = new Date();
        String strKey = "";
        String strType = "";
        String strStatus = "";
        //Method Specific Varriable Declaration Area - End

        try {
            //Region For Configuration Data Retrival and Data Validation - Start

            //Project related validation
            sourcedateFormat = project.getSourceDateFormat();
            if (sourcedateFormat.equals("")) {
                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            dateFormatFromConfig = project.getDateFormat().replace("'", "");
            if (dateFormatFromConfig.equals("")) {
                message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            limitValue= Float.parseFloat(sla.getLimit());

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

            strCheckHolidays = sla.getInput2();
            if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
                strCheckHolidays = "N";
            }

            strCheckWeekend = sla.getInput3();
            if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
                strCheckWeekend = "N";
            }


            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (project.getProjecturl() == null || project.getProjecturl().isEmpty()) {
                message = twoSpace + "Data Source URL is not available, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
                ProcessedData data = JiraProcessWithJQLCountGeneric(sla, userName, password, project, retrievedIssues);
                return data;
            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {

                String strCheckCommittedField = sla.getConfig1();
                String strCheckClosedField = sla.getConfig2();
                String checkCommitedInField = sla.getConfig3();
                String checkClosedInField = sla.getConfig4();
                String strRegularUpdate = sla.getInput1();
                String strProjectName = sla.getInput5();
                String strFromDate = sla.getFrom();
                String strToDate = sla.getTo();


                if (!strFromDate.equals("")) {
                    if (util.isDateValid(strFromDate, dateFormatFromConfig) == false) {
                        message = twoSpace + "From Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
                        status = util.WriteToFile(project.getLogFile(), message);
                        return null;
                    }
                } else {
                    message = twoSpace + "From Date is not found, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (!strToDate.equals("")) {
                    if (util.isDateValid(strToDate, dateFormatFromConfig) == false) {
                        message = twoSpace + "To Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
                        status = util.WriteToFile(project.getLogFile(), message);
                        return null;
                    }
                } else {
                    message = twoSpace + "To Date is not found, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (strCheckCommittedField.isEmpty() && checkCommitedInField.isEmpty()) {
                    message = twoSpace + "Please provide committed status or Field name to check the committed date, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                if (strCheckClosedField.isEmpty() && checkClosedInField.isEmpty()) {
                    message = twoSpace + "Please provide Closed status or Field name to check the closed date, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                String[] arrClosedStatus = strCheckClosedField.split(",");
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
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type", "Final Issue Status", "commitedDate", "closedDate"});
                }

                if (project.getDetailedLogRequired().equals("Y")) {
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type", "Final Issue Status", "commitedDate", "closedDate"});
                }


                List<WorkItem> workitemsClosedDates=new ArrayList<>();
                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
                    ADDenoQuery = "{\n" +
                            "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";
                    workitemsClosedDates = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADDenoQuery, "POST", false, true, true, pageSize);
                }



                try {
                    committedDate = null;
                    closedDate = null;
                    dueDate = null;
                    Date fromDate = new SimpleDateFormat("MM/dd/yyyy").parse(strFromDate);
                    Date toDate = new SimpleDateFormat("MM/dd/yyyy").parse(strToDate);

                    if (workitemsClosedDates != null && workitemsClosedDates.size() > 0) {
                        for (WorkItem witemClosed : workitemsClosedDates) {
                            if (witemClosed != null) {
                                if (witemClosed.getRevisions() != null) {
                                    revisionValues = witemClosed.getRevisions();
                                }
                                if (checkCommitedInField.equals("N")) {
                                    if (revisionValues != null & revisionValues.size() > 0) {
                                        IssueActivityDate issueActivityDates = util.getADOWorkItemActivityDate(witemClosed.getId(), revisionValues, strCheckCommittedField, sourcedateFormat);
                                        if (issueActivityDates != null) {
                                            if (issueActivityDates.getRequestedDate() != null) {
                                                committedDate = issueActivityDates.getRequestedDate();
                                            }
                                        }
                                    }
                                } else {
                                    if (checkCommitedInField.equals("Created")) {
                                        strCommittedDate = witemClosed.getFields().getCreatedDate();
                                    } else if (checkCommitedInField.equals("Updated")) {
                                        strCommittedDate = witemClosed.getFields().getChangedDate();
                                    }
                                    if (!strCommittedDate.isEmpty()) {
                                        committedDate = util.ConvertStringToDateForZFormat(strCommittedDate);

                                    }
                                }
                                if (checkClosedInField.equals("N")) {
                                    //Check the closed date by looking to the configuration from the histroy
                                    revisionValues = witemClosed.getRevisions();
                                    boolean firstOccuranceClosedDate = false;

                                    if (revisionValues != null & revisionValues.size() > 0) {
                                        for (RevisionValue revisionValuedate : revisionValues) {
                                            if (revisionValuedate != null) {
                                                if (!closedStatus.isEmpty()) {
                                                    if (firstOccuranceClosedDate == false) {
                                                        if (revisionValuedate.getFields().getState().equals(closedStatus)) {
                                                            firstOccuranceClosedDate = true;
                                                            strClosedDate = revisionValuedate.getFields().getStateChangeDate();
                                                            break;
                                                        }

                                                    }

                                                    if (!prevClosedStatus.isEmpty()) {
                                                        if (firstOccuranceClosedDate == false) {
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
                                    }
                                    if (!strClosedDate.isEmpty()) {
                                        closedDate = util.ConvertStringToDateForZFormat(strClosedDate);
                                    }
                                } else {
                                    if (checkClosedInField.equals("Closed")) {
                                        //Take the date from Workitem Closed date
                                        strClosedDate = witemClosed.getFields().getDueDate();
                                    } else if (checkClosedInField.equals("Resolved")) {
                                        //Take the date from Workitem Resolution date
                                        strClosedDate = witemClosed.getFields().getResolvedDate();
                                    }
                                    if (!strClosedDate.isEmpty()) {
                                        if (util.isDateValid(strClosedDate, sourcedateFormat) == true) {
                                            closedDate = util.ConvertStringToDateForZFormat(strClosedDate);
                                        }
                                    }
                                }
                                issueDateVariance = new IssueDateVariance();
                                issueDateVariance.setKey(witemClosed.getId());
                                issueDateVariance.setType(witemClosed.getFields().getWorkItemType());
                                issueDateVariance.setVariance(0.0);
                                issueDateVariance.setIssueStatus(witemClosed.getFields().getState());
                                if (closedDate != null && committedDate != null) {
                                    issueDateVariance.setClosedDate(closedDate);
                                    issueDateVariance.setCommitedDate(committedDate);
                                }
                                variance=util.GetDayVariance(closedDate,committedDate,lstHolidays,strCheckHolidays,strCheckWeekend);
                                issueDateVariance.setVariance(variance);
                                if(variance<=limitValue)
                                {
                                    issueDateVariance.getStatus().equals("Met");
                                }
                                else
                                {
                                    issueDateVariance.getStatus().equals("Not Met");
                                }
                                elligibleIssues.add(issueDateVariance);
                                if (project.getDetailedLogRequired().equals("Y")) {
                                    String tType = "";
                                    String tStatus = "";
                                    String tClosedDate = "";
                                    String tCreatedDate = "";

                                    if (witemClosed.getFields().getWorkItemType() != null) {
                                        tType = witemClosed.getFields().getWorkItemType();
                                    }

                                    if (witemClosed.getFields().getState() != null) {
                                        tStatus = witemClosed.getFields().getState();
                                    }
                                    if (committedDate != null) {
                                        tCreatedDate = util.ConvertDateToString(committedDate, sourcedateFormat);
                                    }

                                    if (closedDate != null) {
                                        tClosedDate = util.ConvertDateToString(closedDate, sourcedateFormat);
                                    }

                                    dataLinesDetailed.add(new String[]
                                            {witemClosed.getId(), tType, tStatus, tCreatedDate, tClosedDate});
                                }
//                                strDuedate = witemClosed.getFields().getDueDate();
//                                if (strDuedate != null && !strDuedate.isEmpty()) {
//                                    dueDate = util.ConvertStringToDateForZFormat(strDuedate);
//                                }
//                                if (closedDate != null && dueDate != null) {
//                                    if (closedDate.before(dueDate) || closedDate.equals(dueDate)) {
//                                        issueDateVariance.setStatus("Met");
//                                    } else {
//                                        issueDateVariance.setStatus("Not Met");
//                                    }
//                                    elligibleIssues.add(issueDateVariance);
//                                    if (project.getDetailedLogRequired().equals("Y")) {
//                                        String tType = "";
//                                        String tStatus = "";
//                                        String tClosedDate = "";
//                                        String tCreatedDate = "";
//
//                                        if (witemClosed.getFields().getWorkItemType() != null) {
//                                            tType = witemClosed.getFields().getWorkItemType();
//                                        }
//
//                                        if (witemClosed.getFields().getState() != null) {
//                                            tStatus = witemClosed.getFields().getState();
//                                        }
//                                        if (committedDate != null) {
//                                            tCreatedDate = util.ConvertDateToString(committedDate, sourcedateFormat);
//                                        }
//
//                                        if (closedDate != null) {
//                                            tClosedDate = util.ConvertDateToString(closedDate, sourcedateFormat);
//                                        }
//
//                                        dataLinesDetailed.add(new String[]
//                                                {witemClosed.getId(), tType, tStatus, tCreatedDate, tClosedDate});
//                                    }
//
//                                }

                            }
                        }
                    }

                    if (elligibleIssues != null && elligibleIssues.size() > 0) {
                        totaldenoCount= elligibleIssues.size();
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

                            if (project.getDatafileRequired().equals("Y")) {
                                String type = "";
                                String status = "";

                                if (iv.getType() != null) {
                                    type = iv.getType();
                                }

                                if (iv.getIssueStatus() != null) {
                                    status = iv.getIssueStatus();
                                }


                                dataLines.add(new String[]
                                        {iv.getKey(), type, status, strCommittedDate, strClosedDate, iv.getStatus()});

                            }
                        }
                    }

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

                    message = twoSpace + " Total Denominator Count = " + totaldenoCount;
                    message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
                    message += newLine + twoSpace + " AdoQuery = " + ADDenoQuery;
                    message += newLine + twoSpace + " Minimum SLA = " + minsla;
                    message += newLine + twoSpace + " Expected SLA = " + expectedsla;

                    double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                    double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                    if (totaldenoCount == 0 && totalnumCount == 0) {
                        totaldenoCount = 1;
                        totalnumCount = 1;
                    } else if (totaldenoCount == 0) {
                        totaldenoCount = 1;
                    }

                    actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
                    message += newLine + twoSpace + " Actual = " + actualValue;
                    slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
                    message += newLine + twoSpace + " Status = " + slaStatus;

                    ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),ADDenoQuery);
                    boolean isStatus = util.WriteToFile(project.getLogFile(), message);
                    return data;

                } catch (Exception exception) {
                }

            }

            return null;

        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }
        return null;
    }

    private ProcessedData ServiceLevelDataQuality(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> numIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();

        int pageSize = 1000;
        String strPageSize = "";

        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                expectedsla = Double.valueOf(sla.getExpectedsla());
//                minsla = Double.valueOf(sla.getMinimumsla());
//
//                if (expectedsla == 0 && minsla == 0) {
//                    //Stop the processing
//                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//
//                //Where is the validation
//                String strTotalNumCount = "";
//                String strTotalDenoCount = "";
//
//                strTotalNumCount = sla.getInput1();
//                strTotalDenoCount = sla.getInput2();
//
//                if (strTotalNumCount.isEmpty()) {
//                    message = twoSpace + "Num count not found, Check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                try {
//                    totalnumCount = Integer.parseInt(strTotalNumCount);
//                } catch (Exception exparse) {
//                    message = twoSpace + "Cannot able to parse Num count value, Check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (strTotalDenoCount.isEmpty()) {
//                    message = twoSpace + "Deno count not found, Check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                try {
//                    totaldenoCount = Integer.parseInt(strTotalDenoCount);
//                } catch (Exception exparse) {
//                    message = twoSpace + "Cannot able to parse Deno count value, Check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                //If user configuration is true to create the datafile then save it
//
//                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
//                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back
//
//                if (totaldenoCount == 0 && totalnumCount == 0) {
//                    totaldenoCount = 1;
//                    totalnumCount = 1;
//                } else if (totaldenoCount == 0) {
//                    totaldenoCount = 1;
//                }
//
//                double actualValue = 0;
//                actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, expectedsla, minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            return null;
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;

    }
    private ProcessedData SecurityThreatMitigation(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<Issue> denoIssue = new ArrayList<>();
        List<Issue> numIssue = new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();
        List<Date> lstHolidays;

        double variance = 0f;
        double finalLimitValue = 0.0;
        double actualValue = 0;
        String holidayList = "";
        String dateFormatFromConfig = "";
        String strPageSize = "";
        int pageSize = 0;
        String closedStatus = "";
        String prevClosedStatus = "";
        String includeCommittedDate = "";
        Date committedDate = null;
        Date closedDate = null;
        String strCommittedDate = "";
        String strClosedDate = "";
        String sourcedateFormat = "";
        float limitValue = 0.f;
        String strLimitValue = "";
        String Critical = "";
        String Major = "";
        String High = "";
        String Low = "";
        String CriticalLevel = "";
        String MajorLevel = "";
        String HighLevel = "";
        String LowLevel = "";

        // ADO Variables
        List<WorkItem> denoWorkitems = new ArrayList<>();
        List<WorkItem> numWorkitems = new ArrayList<>();
        List<RevisionValue> revisionValues = new ArrayList<>();
        String denoQuery = "";
        String numQuery = "";
        IssueDateVariance issueDateVariance = new IssueDateVariance();
        List<IssueDateVariance> eligibleissue = new ArrayList<>();
        List<Item> itemList = new ArrayList<>();
        int denoCount = 0;
        int numCount = 0;

        try {
            sourcedateFormat = project.getSourceDateFormat();
            if (sourcedateFormat.equals("")) {
                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
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

            String[] strLimitValue1 = sla.getLimit().split(",");
            if (strLimitValue1 != null && strLimitValue1.length > 4) {
                message = twoSpace + "Only Four Limit can be mentioned. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (strLimitValue1 != null && strLimitValue1.length >= 1) {
                Critical = strLimitValue1[0];
            }

            if (strLimitValue1 != null && strLimitValue1.length >= 2) {
                Major = strLimitValue1[1];
            }

            if (strLimitValue1 != null && strLimitValue1.length >= 3) {
                High = strLimitValue1[2];
            }

            if (strLimitValue1 != null && strLimitValue1.length >= 4) {
                Low = strLimitValue1[3];
            }

            String[] PriorityLevel = sla.getConfig5().split(",");
            if (PriorityLevel != null && PriorityLevel.length > 4) {
                message = twoSpace + "Only Four PriorityLevel can be mentioned. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (PriorityLevel != null && PriorityLevel.length >= 1) {
                CriticalLevel = PriorityLevel[0];
            }

            if (PriorityLevel != null && PriorityLevel.length >= 2) {
                MajorLevel = PriorityLevel[1];
            }

            if (PriorityLevel != null && PriorityLevel.length >= 3) {
                HighLevel = PriorityLevel[2];
            }

            if (PriorityLevel != null && PriorityLevel.length >= 4) {
                LowLevel = PriorityLevel[3];
            }

            dateFormatFromConfig = project.getDateFormat().replace("'", "");
            if (dateFormatFromConfig.equals("")) {
                message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            holidayList = project.getHolidays();
            if (holidayList.equals("")) {
                message = twoSpace + "Holiday details not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            String[] arrHoliday = holidayList.split(",");
            lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);

            if (lstHolidays == null) {
                message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }


            String committedStatus = sla.getConfig1();
            String closeStatus = sla.getConfig2();
            String checkCommitedInField = sla.getConfig3();
            String checkClosedInField = sla.getConfig4();
            String strCheckHolidays = sla.getInput2();
            String strCheckWeekend = sla.getInput3();
            includeCommittedDate = sla.getInput4();


            if (sla.getDenojql().isEmpty()) {
                message = twoSpace + "Dino Query not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
                denourl = project.getProjecturl() + "/api/2/search?jql=" + sla.getDenojql() + "&expand=changelog";
            }

            if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
                numurl = project.getProjecturl() + "/api/2/search?jql=" + sla.getNumjql() + "&expand=changelog";
            }

            if ((!StringUtils.hasText(committedStatus) || committedStatus.equals("")) && (!StringUtils.hasText(checkCommitedInField) || checkCommitedInField.equals(""))) {
                message = twoSpace + "Please provide committed status or Field name to check the committed date, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }


            if (!StringUtils.hasText(closeStatus) || closeStatus.equals("") && (!StringUtils.hasText(checkClosedInField) || checkClosedInField.equals(""))) {
                message = twoSpace + "Please provide Closed status or Field name to check the closed date, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String[] arrClosedStatus = closeStatus.split(",");
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

            if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
                strCheckHolidays = "N";
            }

            if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
                strCheckWeekend = "N";
            }

            if (includeCommittedDate.equals("") || !includeCommittedDate.equals("Y")) {
                includeCommittedDate = "N";
            }

//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//
//                if (!denourl.isEmpty()) {
//                    denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, true, pageSize);
//                }
//
//                if (!numurl.isEmpty()) {
//                    numIssue = iJiraDataService.getIssuesUsingJQL(userName, password, numurl, "", false, true, pageSize);
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Status", "commited Date", "closed Date", "Variance (In Days)", "eligible"});
//                }
//
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                message = "";
//
//                //Get the Denominator Count
//                if (!numurl.isEmpty()) {
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"Denominator Issues (Planned)", "", "", "", "", "", ""});
//                    }
//                }
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//
//
////                        This means we have the Num Issues retrieved. So we will not process the deno issues.
////                        So we will print the deno issues first
////                        for (Issue issue : denoIssue) {
////                            if (project.getDatafileRequired().equals("Y")) {
////                                dataLines.add(new String[]
////                                        {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName(), issue.getFields().getCreated(), "", "", ""});
////                            }
////                        }
//
//                }
//
//                if (!numurl.isEmpty()) {
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"", "", "", "", "", "", ""});
//                    }
//
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"Numerator Issues (Completed)", "", "", "", "", "", ""});
//                    }
//
//                    //Also assign Num Issues to the Deno Issue variable so that we can process the Num Issues here
//                    denoIssue = numIssue;
//                }
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    eligibleissue = new ArrayList<>();
//                    for (Issue issue : denoIssue) {
//                        committedDate = null;
//                        closedDate = null;
//                        strCommittedDate = "";
//                        strClosedDate = "2022-06-09T08:53:14.000-0400";
//                        issueDateVariance = null;
//
//                        if (issue != null) {
//                            try {
//                                List<History> historyList = new ArrayList<>();
//                                if (issue.getChangelog() != null) {
//                                    if (issue.getChangelog().getHistories() != null && issue.getChangelog().getHistories().size() > 0) {
//                                        historyList = issue.getChangelog().getHistories();
//                                    }
//                                }
//
//                                //Get the Committed Date
//                                if (checkCommitedInField.equals("N")) {
//                                    if (historyList != null && historyList.size() > 0) {
//                                        IssueActivityDate issueActivityDate = util.getIssueActivityDate(issue.getKey(), historyList, committedStatus, sourcedateFormat, "status");
//
//                                        if (issueActivityDate != null) {
//                                            if (issueActivityDate.getRequestedDate() != null) {
//                                                committedDate = issueActivityDate.getRequestedDate();
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    if (checkCommitedInField.equals("Created")) {
//                                        strCommittedDate = issue.getFields().getCreated();
//                                    } else if (checkCommitedInField.equals("Updated")) {
//                                        strCommittedDate = issue.getFields().getUpdated();
//                                    }
//
//                                    if (!strCommittedDate.isEmpty()) {
//                                        committedDate = util.ConvertToDate(strCommittedDate, sourcedateFormat);
//                                    }
//                                }
//
//                                //Get the Closed Date
//                                if (checkClosedInField.equals("N")) {
//                                    boolean firstOccuranceClosedDate = false;
//                                    for (History history : historyList) {
//                                        itemList = null;
//                                        if (firstOccuranceClosedDate == true) {
//                                            break;
//                                        }
//
//                                        if (history.getItems() != null && history.getItems().size() > 0) {
//                                            itemList = history.getItems();
//                                            if (!closedStatus.isEmpty()) {
//                                                if (firstOccuranceClosedDate == false) {
//                                                    for (Item item : itemList) {
//                                                        if (item != null) {
//                                                            if (item.getField().equals("status")) {
//                                                                if (item.getToString().equals(closedStatus.trim())) {
//                                                                    firstOccuranceClosedDate = true;
//                                                                    strClosedDate = history.getCreated();
//                                                                    break;
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            if (!prevClosedStatus.isEmpty()) {
//                                                if (firstOccuranceClosedDate == false) {
//                                                    for (Item item : itemList) {
//                                                        if (item != null) {
//                                                            if (item.getField().equals("status")) {
//                                                                if (item.getToString().equals(prevClosedStatus.trim())) {
//                                                                    firstOccuranceClosedDate = true;
//                                                                    strClosedDate = history.getCreated();
//                                                                    break;
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    if (!strClosedDate.isEmpty()) {
//                                        closedDate = util.ConvertToDate(strClosedDate, sourcedateFormat);
//                                    }
//                                } else {
//                                    if (checkClosedInField.equals("Resolution")) {
//                                        strClosedDate = issue.getFields().getResolutiondate();
//                                    }
//
//                                    if (!strClosedDate.isEmpty()) {
//                                        closedDate = util.ConvertToDate(strCommittedDate, sourcedateFormat);
//                                    }
//                                }
//
//                                //Next processing
//                                issueDateVariance = new IssueDateVariance();
//                                issueDateVariance.setKey(issue.getKey());
//                                issueDateVariance.setType(issue.getFields().getIssuetype().getName());
//                                issueDateVariance.setIssueStatus(issue.getFields().getStatus().getName());
//                                issueDateVariance.setCommitedDate(committedDate);
//                                issueDateVariance.setClosedDate(closedDate);
//                                issueDateVariance.setVariance(0.0);
//
//                                if (committedDate != null && closedDate != null) {
//                                    variance = util.GetDayVariance(committedDate, closedDate, lstHolidays, strCheckHolidays, strCheckWeekend, includeCommittedDate);
//                                    issueDateVariance.setVariance(variance);
//                                }
//                                //nikhil
//                                if (issueDateVariance.getCommitedDate() != null && issueDateVariance.getClosedDate() != null && issue.getFields().getPriority().getName() != null) {
////                                    if (issue.getFields().getPriority().getName().equalsIgnoreCase(CriticalLevel))
////                                    {
////                                        if (issueDateVariance.getVariance() <= Integer.parseInt(Critical))
////                                        {
////                                            issueDateVariance.setStatus("Met");
////                                        } else {
////                                            issueDateVariance.setStatus("Not Met");
////                                        }
////                                    }
//
//                                    if (issue.getFields().getPriority().getName().equalsIgnoreCase(MajorLevel)) {
//                                        if (issueDateVariance.getVariance() <= Integer.parseInt(Major)) {
//                                            issueDateVariance.setStatus("Met");
//                                        } else {
//                                            issueDateVariance.setStatus("Not Met");
//                                        }
//                                    }
//
//                                    else if (issue.getFields().getPriority().getName().equalsIgnoreCase(HighLevel)) {
//                                        if (issueDateVariance.getVariance() <= Integer.parseInt(High)) {
//                                            issueDateVariance.setStatus("Met");
//                                        } else {
//                                            issueDateVariance.setStatus("Not Met");
//                                        }
//                                    }
//
//                                    else if (issue.getFields().getPriority().getName().equalsIgnoreCase(LowLevel)) {
//                                        if (issueDateVariance.getVariance() <= Integer.parseInt(Low)) {
//                                            issueDateVariance.setStatus("Met");
//                                        } else {
//                                            issueDateVariance.setStatus("Not Met");
//                                        }
//                                    }
//
//
//                                }
//                                else {
//                                    issueDateVariance.setStatus("Not Met");
//                                }
//
//                                eligibleissue.add(issueDateVariance);
//                            }
//                            catch (Exception exception) {
//                                exception.printStackTrace();
//                            }
//
//                        }
//                    }
//
//
//
//                    if (!CollectionUtils.isEmpty(eligibleissue)) {
//                        //totaldenoCount=eligibleissue.size();
//                        totalnumCount = (int) eligibleissue.stream().filter(x -> x.getStatus().equals("Met")).count();
//
//                        //Data Line
//                        for (IssueDateVariance iv : eligibleissue) {
//                            strCommittedDate = "";
//                            strClosedDate = "";
//                            String strVariance = "";
//
//                            if (iv.getCommitedDate() != null) {
//                                strCommittedDate = util.ConvertDateToString(iv.getCommitedDate(), sourcedateFormat);
//                            }
//
//                            if (iv.getClosedDate() != null) {
//                                strClosedDate = util.ConvertDateToString(iv.getClosedDate(), sourcedateFormat);
//                            }
//
//                            if (iv.getCommitedDate() != null && iv.getClosedDate() != null) {
//                                strVariance = String.valueOf(iv.getVariance());
//                            }
//
//                            if (project.getDatafileRequired().equals("Y")) {
//                                dataLines.add(new String[]
//                                        {iv.getKey(), iv.getType(), iv.getIssueStatus(), strCommittedDate, strClosedDate, strVariance, iv.getStatus()});
//                            }
//                        }
//
//
//                    }
//
//
//                    //Preparing the data for
//                    if (dataLines.size() > 0) {
//                        String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                        try {
//                            boolean csvStatus = util.WriteToCSv(dataLines, dataFileName);
//                            if (csvStatus == true) {
//                                message += newLine + twoSpace + " Data file Created successfully - " + dataFileName;
//                            } else {
//                                message += newLine + twoSpace + "Unable to create the data file";
//                            }
//                        } catch (Exception exCsv) {
//                            message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
//                        }
//                    }
//
//                    message = twoSpace + " Total Denominator Count = " + totaldenoCount;
//                    message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                    message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                    message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//
//                    double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
//                    double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back
//
//                    if (totaldenoCount == 0 && totalnumCount == 0) {
//                        totaldenoCount = 1;
//                        totalnumCount = 1;
//                    } else if (totaldenoCount == 0) {
//                        totaldenoCount = 1;
//                    }
//
//                    actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//                    message += newLine + twoSpace + " Actual = " + actualValue;
//                    slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
//                    message += newLine + twoSpace + " Status = " + slaStatus;
//
//                    ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                    boolean isStatus = util.WriteToFile(project.getLogFile(), message);
//                    return data;
//                }
//
//            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
                status = util.WriteToFile(project.getLogFile(), message);

                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]{ "Key", "Type", "Final Issue Status", "Severity", "commitedDate", "closedDate",
                            "Variance (In Days)", "Eligible"});
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


                denoQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                ////////// Business Logic Implementation Starts  //////////

                denoWorkitems  = iAdoDataService.getWorkitems(userName,password,project.getProjecturl(),denoQuery,"POST",false,true, true,100);



                if(denoWorkitems !=null && denoWorkitems.size()>0)
                {
                    totaldenoCount=denoWorkitems.size();
                    for(WorkItem witem : denoWorkitems){
                        committedDate = null;
                        closedDate = null;
                        if (witem != null)
                        {
                            try
                            {
                                if (witem.getRevisions() != null) {
                                    revisionValues = witem.getRevisions();
                                }

                                if (checkCommitedInField.equals("N")) {
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
                                    if (checkCommitedInField.equals("Created")) {
                                        strCommittedDate = witem.getFields().getCreatedDate();
                                    }
                                    else if (checkCommitedInField.equals("Updated")) {
                                        strCommittedDate= witem.getFields().getChangedDate();
                                    }

                                    if (!strCommittedDate.isEmpty())
                                    {
                                        committedDate = util.ConvertStringToDateForZFormat(strCommittedDate);
                                    }
                                }

                                if (checkClosedInField.equals("N")) {
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

                                                if (!prevClosedStatus.isEmpty())
                                                {
                                                    if (firstOccuranceClosedDate == false) {
                                                        //Check and compare the status with the record to get a match
                                                        if (revisionValuedate.getFields().getState().equals(prevClosedStatus))
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
                                        closedDate = util.ConvertStringToDateForZFormat(strClosedDate);
                                    }
                                }
                                else
                                {
                                    if (checkClosedInField.equals("Closed")) {
                                        strClosedDate = witem.getFields().getClosedDate();
                                    } else if (checkClosedInField.equals("Resolved")) {
                                        strClosedDate = witem.getFields().getResolvedDate();
                                    }

                                    if (!strClosedDate.isEmpty()) {
                                        closedDate = util.ConvertStringToDateForZFormat (strClosedDate);
                                    }
                                }

                                //numCount = witem.getFields().getSecurityThreatCount();
//
//                                if(numCount == 0){
//                                    numCount = 1;
//                                }

                                variance = 0;
                                if (committedDate != null && closedDate != null)
                                {
                                    issueDateVariance = new IssueDateVariance();
                                    issueDateVariance.setKey(witem.getId());
                                    issueDateVariance.setType(witem.getFields().getWorkItemType());
                                    issueDateVariance.setIssueStatus(witem.getFields().getState());
                                    issueDateVariance.setPriority(witem.getFields().getSeverity());
                                    issueDateVariance.setVariance(0.0);
                                    issueDateVariance.setCommitedDate(committedDate);
                                    issueDateVariance.setClosedDate(closedDate);
                                    issueDateVariance.setCommitedDateString(strCommittedDate);
                                    issueDateVariance.setClosedDateString(strClosedDate);
                                    issueDateVariance.setVarianceinMin((double)numCount); //String the num count here

                                    //variance = util.get//Integer.parseInt(issueDateVariance.getClosedDateString()) - Integer.parseInt(issueDateVariance.getCommitedDateString());
                                    variance = util.GetDayVariance(committedDate, closedDate, lstHolidays, strCheckHolidays, strCheckWeekend, includeCommittedDate);
                                    issueDateVariance.setVariance(variance);

                                    if (committedDate != null && closedDate != null) {

                                        if (issueDateVariance.getPriority().equals(MajorLevel)) {

                                            if (variance <= Double.valueOf(Major)) {
                                                issueDateVariance.setStatus("Met");
                                            } else {
                                                issueDateVariance.setStatus("Not Met");
                                            }

                                        }
                                        else if (issueDateVariance.getPriority().equals(HighLevel)) {

                                            if (variance <= Integer.parseInt(High)) {
                                                issueDateVariance.setStatus("Met");
                                            } else {
                                                issueDateVariance.setStatus("Not Met");
                                            }
                                        }

                                        else if (issueDateVariance.getPriority().equals(LowLevel)) {

                                            if (variance <= Integer.parseInt(Low)) {
                                                issueDateVariance.setStatus("Met");
                                            } else {
                                                issueDateVariance.setStatus("Not Met");
                                            }
                                        }
                                    }
                                    else {
                                        issueDateVariance.setStatus("Not Met");
                                    }

                                    eligibleissue.add(issueDateVariance);
                                }

                                //Write to the details data file
                                if (project.getDetailedLogRequired().equals("Y"))
                                {
                                    String tType = "";
                                    String tStatus = "";
                                    String tSeverity = "";
                                    String tCommittedDate = "";
                                    String tClosedDate = "";

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

                                    if (committedDate != null)
                                    {
                                        tCommittedDate = util.ConvertDateToString(committedDate, sourcedateFormat);
                                    }

                                    if (closedDate != null)
                                    {
                                        tClosedDate = util.ConvertDateToString(closedDate, sourcedateFormat);
                                    }

                                    dataLinesDetailed.add(new String[]
                                            {witem.getId(), tType, tSeverity, tStatus, tCommittedDate, tClosedDate });
                                }

                            }
                            catch (Exception exception)
                            {

                            }
                        }
                    }
                }

                if (eligibleissue != null && eligibleissue.size() > 0)
                {

                    //Data Line
                    for (IssueDateVariance iv:eligibleissue)
                    {

                        if (iv.getStatus().equals("Met"))
                        {
                            totalnumCount++;
                        }
                        strCommittedDate = "";
                        strClosedDate = "";
                        String strVariance = "";

                        if (iv.getCommitedDate() != null)
                        {
                            strCommittedDate = util.ConvertDateToString(iv.getCommitedDate(), sourcedateFormat);
                        }

                        if (iv.getClosedDate() != null)
                        {
                            strClosedDate = util.ConvertDateToString(iv.getClosedDate(), sourcedateFormat);
                        }

                        if (iv.getCommitedDate() != null && iv.getClosedDate() != null)
                        {
                            strVariance = String.valueOf(iv.getVariance());
                        }

                        if (project.getDatafileRequired().equals("Y"))
                        {
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
                                    {iv.getKey(), type, status, priority, strCommittedDate, strClosedDate,
                                            strVariance, iv.getStatus()});

                            //                            dataLines.add(new String[]
//                                    {iv.getKey(), iv.getType(), iv.getIssueStatus(), strCommittedDate, strClosedDate, strVariance, iv.getStatus() });
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
                message += newLine + twoSpace + " AdoQuery = " + denoQuery;

                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;

                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                if (totaldenoCount == 0 && totalnumCount == 0)
                {
                    totaldenoCount = 1;
                    totalnumCount = 1;
                }
                else if (totaldenoCount == 0)
                {
                    totaldenoCount = 1;
                }

                actualValue = util.GetActualValueV1((double)totaldenoCount, (double)totalnumCount);
                message += newLine + twoSpace + " Actual = " + actualValue;
                slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double)expectedsla, (double)minsla);
                message += newLine + twoSpace + " Status = " + slaStatus;

                ProcessedData data = util.BuildProcessData(sla, (float)actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),denoQuery);
                boolean isStatus = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            return null;

        } catch (Exception ex) {
            ex.printStackTrace();
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);

        }
        return null;
        //Non-backlog SLA Calculation - End
    }

    private ProcessedData UserStoryApprovalReport(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues)
    {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        String inputFilePath = "";
        String itemQuery = "";
        String Query = "";
        String state1 = "";
        String state2 = "";
        String baseURI = "";
        String fMode = "";
        Date dtFromDate = new Date();
        Date dtToDate = new Date();

        List<WorkItem> workItems = null;
        List<Issue> denoIssue = new ArrayList<>();

        itemQuery = sla.getConfig1();

        fMode = sla.getConfig3();

        String dateFormatFromConfig = project.getDateFormat().replace("'", "");
        String sDateFormatFromConfig = project.getSourceDateFormat();
        String fromdateFromConfig = sla.getFrom().replace("'", "");
        String todateFromConfig = sla.getTo().replace("'", "");
        if (!fromdateFromConfig.isEmpty() && !todateFromConfig.isEmpty())
        {
            dtFromDate = util.ConvertToDate(fromdateFromConfig, dateFormatFromConfig);
            dtToDate = util.ConvertToDate(todateFromConfig, dateFormatFromConfig);
        }

        if (fMode.isEmpty() || fMode.equals(""))
        {
            message = twoSpace + "Config3, not found. To select the mode of operation, we need to provide the Config3 value as (Y/N). Stopping SLA calculation";
            status = util.WriteToFile(project.getLogFile(), message);
            return null;
        }
        if (fMode.equals("Y")){
            itemQuery = sla.getConfig1();
        }

        if (fMode.equals("N")){
            itemQuery = sla.getConfig4();
        }

        String[] acStatus = sla.getConfig2().split(",");
        if (acStatus != null && acStatus.length > 2) {
            message = twoSpace + "Only Two status(s) can be mentioned for Config2. Stopping SLA calculation";
            status = util.WriteToFile(project.getLogFile(), message);
            return null;
        }

        if (acStatus != null && acStatus.length >= 1) {
            state1 = acStatus[0];
            state1 = state1.trim();
        }

        if (acStatus != null && acStatus.length >= 2) {
            state2 = acStatus[1];
            state2 = state2.trim();
        }

        if (project.getDatafileRequired().equals("Y")) {
            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
                dataLines.add(new String[]
                        {"ProjectName", "Type", "Key", "Status", "Story Points", "Estimated Hours", "Actual Efforts (Hours)", "Last Accepted Date", "Last Approved By", "Current Status"});
            }
            if (project.getProjectsource().equals(SourceKey.ADO.value)){
                dataLines.add(new String[]
                        {"ProjectName", "Type", "Key", "Status", "Estimated Hours", "Actual Efforts (Hours)", "Last Accepted Date", "Last Approved By", "Current Status"});
            }
        }

        baseURI = project.getProjecturl() + "/api/2/search?";

        try
        {
            if (project.getProjectsource().equals(SourceKey.JIRA.value))
            {
                if (fMode.equals("Y")) {
                    inputFilePath = project.getAutomationData() + "\\\\UserStories" + "\\\\JIRA_Input.csv";
                    List<Issue> finalWorkitems = new ArrayList<>();
                    if (inputFilePath != null) {
                        String line = "";
                        BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
                        while ((line = reader.readLine()) != null) {
                            List<String> inputLines = List.of(line.split(","));
                            if (inputLines != null && !inputLines.isEmpty() && inputLines.size() > 0) {
                                for (String storyId : inputLines) {
                                    Query = itemQuery.replace("ISSUEIDVALUE", storyId);
                                    Query = baseURI + Query;
                                    String commentURI = project.getProjecturl() + "/agile/1.0/issue";
                                    try {
                                        denoIssue = iJiraDataService.getAllIssuesOnJQLV1(userName, password, Query, baseURI, commentURI, true, true);
                                    } catch (Exception excep) {
                                        message = twoSpace + "Error occurred while fetching the User Story details. Skipping this User Story Id " + storyId + "\n" + excep;
                                        status = util.WriteToFile(project.getLogFile(), message);
                                    }
                                    if (denoIssue != null && denoIssue.size() > 0) {
                                        finalWorkitems.add(denoIssue.get(0));
                                        if (denoIssue != null && denoIssue.size() > 0) {
                                            for (Issue issue : denoIssue) {
                                                if (issue != null) {
                                                    String actualEfforts = "";
                                                    String estimatedEfforts = "";
                                                    String storyPoints = "";
                                                    estimatedEfforts = issue.getFields().getCustomfield_14000();
                                                    actualEfforts = issue.getFields().getCustomfield_14001();
                                                    storyPoints = issue.getFields().getCustomfield_10002();
                                                    List<History> historyList = issue.getChangelog().getHistories();
                                                    if (historyList != null && historyList.size() > 0) {
                                                        String[] approvedItem = new String[10];
                                                        String[] closedItem = new String[10];
                                                        String[] notApprovedItem = new String[10];
                                                        String currStatus = "";
                                                        int statusCount = 0;
                                                        if (storyPoints == null){
                                                            storyPoints = "";
                                                        }
                                                        if (estimatedEfforts == null){
                                                            estimatedEfforts = "";
                                                        }
                                                        if (actualEfforts == null){
                                                            actualEfforts = "";
                                                        }
                                                        for (History history : historyList) {
                                                            if (history != null) {
                                                                List<Item> itemList = history.getItems();
                                                                if (itemList != null && itemList.size() > 0) {
                                                                    for (Item item : itemList) {
                                                                        if (item != null) {
                                                                            if (item.getField().equals("status")) {
                                                                                statusCount++;
                                                                                currStatus = item.getToString();
                                                                                if (item.getToString().equals(state1)) {
                                                                                    approvedItem = new String[]{issue.getFields().getProject().getName(), issue.getFields().getIssuetype().getName(), issue.getKey(), item.getToString(), storyPoints, estimatedEfforts, actualEfforts, history.getCreated(), history.getAuthor().getDisplayName(), ""};
                                                                                }
                                                                                if (item.getToString().equals(state2)) {
                                                                                    closedItem = new String[]{issue.getFields().getProject().getName(), issue.getFields().getIssuetype().getName(), issue.getKey(), item.getToString(), storyPoints, estimatedEfforts, actualEfforts, history.getCreated(), history.getAuthor().getDisplayName(), ""};
                                                                                }
                                                                                notApprovedItem = new String[]{issue.getFields().getProject().getName(), issue.getFields().getIssuetype().getName(), issue.getKey(), "Not " + state1 + "/" + state2, storyPoints, estimatedEfforts, actualEfforts, "", "", ""};
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (statusCount == 0) {
                                                            notApprovedItem = new String[]{issue.getFields().getProject().getName(), issue.getFields().getIssuetype().getName(), issue.getKey(), "Status not Changed", storyPoints, estimatedEfforts, actualEfforts, "", "", issue.getFields().getStatus().getName()};
                                                        }
                                                        if (approvedItem[0] != null) {
                                                            if (project.getDatafileRequired().equals("Y")) {
                                                                approvedItem[9] = currStatus;
                                                                dataLines.add(approvedItem);
                                                            }
                                                        }
                                                        if (closedItem[0] != null) {
                                                            if (project.getDatafileRequired().equals("Y")) {
                                                                closedItem[9] = currStatus;
                                                                dataLines.add(closedItem);
                                                            }
                                                        }
                                                        if (approvedItem[0] == null && closedItem[0] == null) {
                                                            if (project.getDatafileRequired().equals("Y")) {
                                                                if (statusCount != 0) {
                                                                    notApprovedItem[9] = currStatus;
                                                                    dataLines.add(notApprovedItem);
                                                                } else {
                                                                    dataLines.add(notApprovedItem);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (historyList.size() == 0) {
                                                        dataLines.add(new String[]
                                                                {issue.getFields().getProject().getName(), issue.getFields().getIssuetype().getName(), issue.getKey(), "History not available", storyPoints, estimatedEfforts, estimatedEfforts, "", "", issue.getFields().getStatus().getName()});
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (fMode.equals("N")) {

                    Query = baseURI + itemQuery;
                    String commentURI = project.getProjecturl() + "/agile/1.0/issue";

                    try {
                        denoIssue = iJiraDataService.getAllIssuesOnJQLV1(userName, password, Query, baseURI, commentURI, true, true);
                    } catch (Exception excep) {
                        message = twoSpace + "Error occurred while fetching the User Story details. \n" + excep;
                        status = util.WriteToFile(project.getLogFile(), message);
                    }
                    if (denoIssue != null && denoIssue.size() > 0) {
                        if (denoIssue != null && denoIssue.size() > 0) {
                            for (Issue issue : denoIssue) {
                                if (issue != null) {
                                    String actualEfforts = "";
                                    String estimatedEfforts = "";
                                    String storyPoints = "";
                                    estimatedEfforts = issue.getFields().getCustomfield_14000();
                                    actualEfforts = issue.getFields().getCustomfield_14001();
                                    storyPoints = issue.getFields().getCustomfield_10002();
                                    List<History> historyList = issue.getChangelog().getHistories();
                                    if (historyList != null && historyList.size() > 0) {
                                        String[] approvedItem = new String[10];
                                        String[] closedItem = new String[10];
                                        String[] notApprovedItem = new String[10];
                                        String currStatus = "";
                                        int statusCount = 0;
                                        if (storyPoints == null){
                                            storyPoints = "";
                                        }
                                        if (estimatedEfforts == null){
                                            estimatedEfforts = "";
                                        }
                                        if (actualEfforts == null){
                                            actualEfforts = "";
                                        }
                                        for (History history : historyList) {
                                            if (history != null) {
                                                Date d1 = util.ConvertToDate(history.getCreated(), sDateFormatFromConfig);
                                                boolean f = dtToDate.after(d1); //dtFromDate.after(d1);
                                                boolean e = dtFromDate.before(d1); //dtToDate.before(d1);
                                                boolean b = !(dtToDate.after(d1) && dtFromDate.before(d1));
                                                if (!(dtToDate.after(d1) && dtFromDate.before(d1))) {
                                                    continue;
                                                }
                                                List<Item> itemList = history.getItems();
                                                if (itemList != null && itemList.size() > 0) {
                                                    for (Item item : itemList) {
                                                        if (item != null) {
                                                            if (item.getField().equals("status")) {
                                                                statusCount++;
                                                                currStatus = item.getToString();
                                                                if (item.getToString().equals(state1)) {
                                                                    approvedItem = new String[]{issue.getFields().getProject().getName(), issue.getFields().getIssuetype().getName(), issue.getKey(), item.getToString(), storyPoints, estimatedEfforts, actualEfforts, history.getCreated(), history.getAuthor().getDisplayName(), ""};
                                                                }
                                                                if (item.getToString().equals(state2)) {
                                                                    closedItem = new String[]{issue.getFields().getProject().getName(), issue.getFields().getIssuetype().getName(), issue.getKey(), item.getToString(), storyPoints, estimatedEfforts, actualEfforts, history.getCreated(), history.getAuthor().getDisplayName(), ""};
                                                                }
                                                                notApprovedItem = new String[]{issue.getFields().getProject().getName(), issue.getFields().getIssuetype().getName(), issue.getKey(), "Not " + state1 + "/" + state2, storyPoints, estimatedEfforts, actualEfforts, "", "", ""};
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (statusCount == 0) {
                                            notApprovedItem = new String[]{issue.getFields().getProject().getName(), issue.getFields().getIssuetype().getName(), issue.getKey(), "Status not Changed", storyPoints, estimatedEfforts, actualEfforts, "", "", issue.getFields().getStatus().getName()};
                                        }
                                        if (approvedItem[0] != null) {
                                            if (project.getDatafileRequired().equals("Y")) {
                                                approvedItem[9] = currStatus;
                                                dataLines.add(approvedItem);
                                            }
                                        }
                                        if (closedItem[0] != null) {
                                            if (project.getDatafileRequired().equals("Y")) {
                                                closedItem[9] = currStatus;
                                                dataLines.add(closedItem);
                                            }
                                        }
                                        if (approvedItem[0] == null && closedItem[0] == null) {
                                            if (project.getDatafileRequired().equals("Y")) {
                                                if (statusCount != 0) {
                                                    notApprovedItem[9] = currStatus;
                                                    dataLines.add(notApprovedItem);
                                                } else {
                                                    dataLines.add(notApprovedItem);
                                                }
                                            }
                                        }
                                    }
                                    if (historyList.size() == 0) {
                                        dataLines.add(new String[]
                                                {issue.getFields().getProject().getName(), issue.getFields().getIssuetype().getName(), issue.getKey(), "History not available", storyPoints, estimatedEfforts, estimatedEfforts, "", "", issue.getFields().getStatus().getName()});
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (fMode.equals("Y")) {
                    inputFilePath = project.getAutomationData() + "\\\\UserStories" + "\\\\ADO_Input.csv";
                    List<WorkItem> finalWorkitems = new ArrayList<>();
                    if (inputFilePath != null) {
                        String line = "";
                        BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
                        while ((line = reader.readLine()) != null) {
                            List<String> inputLines = List.of(line.split(","));
                            if (inputLines != null && !inputLines.isEmpty() && inputLines.size() > 0) {
                                for (String storyId : inputLines) {
                                    String sQuery = itemQuery.replace("WORKITEMIDVALUE", storyId);
                                    Query = "{\n" +
                                            "  \"query\": \"" + sQuery + "\"\n" + "}";
                                    try {
                                        workItems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), Query, "POST", false, true, true, false, "", "", "", 1000);
                                    } catch (Exception excep) {
                                        message = twoSpace + "Error occurred while fetching the User Story details. Skipping this User Story Id " + storyId + "\n" + excep;
                                        status = util.WriteToFile(project.getLogFile(), message);
                                    }
                                    if (workItems != null && workItems.size() > 0) {
                                        finalWorkitems.add(workItems.get(0));
                                        if (workItems != null && workItems.size() > 0) {
                                            for (WorkItem witem : workItems) {
                                                String[] approvedItem = new String[9];
                                                String[] closedItem = new String[9];
                                                String[] notApprovedItem = new String[9];
                                                String currStatus = "";
                                                List<RevisionValue> revisions = witem.getRevisions();
                                                if (revisions != null && revisions.size() > 0) {
                                                    for (RevisionValue rv : revisions) {
                                                        currStatus = rv.getFields().getState();
                                                        if (rv.getFields().getState().equals(state1)) {
                                                            approvedItem = new String[]{witem.getFields().getTeamProject(), witem.getFields().getWorkItemType(), witem.getId(), rv.getFields().getState(), String.valueOf(witem.getFields().getOriginalEffort()), String.valueOf(witem.getFields().getActualEffortinHours()), rv.getFields().getStateChangeDate(), rv.getFields().getCreatedBy().getDisplayName(), ""};
                                                        }
                                                        if (rv.getFields().getState().equals(state2)) {
                                                            closedItem = new String[]{witem.getFields().getTeamProject(), witem.getFields().getWorkItemType(), witem.getId(), rv.getFields().getState(), String.valueOf(witem.getFields().getOriginalEffort()), String.valueOf(witem.getFields().getActualEffortinHours()), rv.getFields().getStateChangeDate(), rv.getFields().getCreatedBy().getDisplayName(), ""};
                                                        }
                                                        notApprovedItem = new String[]{witem.getFields().getTeamProject(), witem.getFields().getWorkItemType(), witem.getId(), "Not " + state1 + "/" + state2, String.valueOf(witem.getFields().getOriginalEffort()), String.valueOf(witem.getFields().getActualEffortinHours()), "", "", ""};
                                                    }
                                                } else {
                                                    notApprovedItem = new String[]{witem.getFields().getTeamProject(), witem.getFields().getWorkItemType(), witem.getId(), "Revision(History) is not available ", String.valueOf(witem.getFields().getOriginalEffort()), String.valueOf(witem.getFields().getActualEffortinHours()), "", "", witem.getFields().getState()};
                                                }
                                                if (approvedItem[0] != null) {
                                                    if (project.getDatafileRequired().equals("Y")) {
                                                        approvedItem[8] = currStatus;
                                                        dataLines.add(approvedItem);
                                                    }
                                                }
                                                if (closedItem[0] != null) {
                                                    if (project.getDatafileRequired().equals("Y")) {
                                                        closedItem[8] = currStatus;
                                                        dataLines.add(closedItem);
                                                    }
                                                }
                                                if (approvedItem[0] == null && closedItem[0] == null) {
                                                    if (project.getDatafileRequired().equals("Y")) {
                                                        notApprovedItem[8] = currStatus;//new String[]{notApprovedItem + "," + currStatus};
                                                        dataLines.add(notApprovedItem);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
                if (fMode.equals("N")){
                    String sQuery = itemQuery;
                    Query = "{\n" +
                            "  \"query\": \"" + sQuery + "\"\n" + "}";
                    try
                    {
                        workItems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), Query, "POST", false, true, true, false, "","","", 1000);
                    }
                    catch(Exception excep){
                        message = twoSpace + "Error occurred while fetching the User Story details.\n" + excep;
                        status = util.WriteToFile(project.getLogFile(), message);
                    }
                    if (workItems != null && workItems.size() > 0) {
                        if (workItems != null && workItems.size() > 0) {
                            for (WorkItem witem : workItems) {
                                String[] approvedItem = new String[9];
                                String[] closedItem = new String[9];
                                String[] notApprovedItem = new String[9];
                                String currStatus = "";
                                List<RevisionValue> revisions = witem.getRevisions();
                                if (revisions != null && revisions.size() > 0) {
                                    try {
                                        for (RevisionValue rv : revisions) {
                                            Date d1 = util.ConvertToDate(rv.getFields().getStateChangeDate().substring(0, 10), "yyyy-MM-dd");
                                            boolean f = dtToDate.after(d1); //dtFromDate.after(d1);
                                            boolean e = dtFromDate.before(d1); //dtToDate.before(d1);
                                            boolean b = !(dtToDate.after(d1) && dtFromDate.before(d1));
                                            if (!(dtToDate.after(d1) && dtFromDate.before(d1))) {
                                                continue;
                                            }
                                            currStatus = rv.getFields().getState();
                                            if (rv.getFields().getState().equals(state1)) {
                                                approvedItem = new String[]{witem.getFields().getTeamProject(), witem.getFields().getWorkItemType(), witem.getId(), rv.getFields().getState(), String.valueOf(witem.getFields().getOriginalEffort()), String.valueOf(witem.getFields().getActualEffortinHours()), rv.getFields().getStateChangeDate(), rv.getFields().getCreatedBy().getDisplayName(), ""};
                                            }
                                            if (rv.getFields().getState().equals(state2)) {
                                                closedItem = new String[]{witem.getFields().getTeamProject(), witem.getFields().getWorkItemType(), witem.getId(), rv.getFields().getState(), String.valueOf(witem.getFields().getOriginalEffort()), String.valueOf(witem.getFields().getActualEffortinHours()), rv.getFields().getStateChangeDate(), rv.getFields().getCreatedBy().getDisplayName(), ""};
                                            }
                                            notApprovedItem = new String[]{witem.getFields().getTeamProject(), witem.getFields().getWorkItemType(), witem.getId(), "Not " + state1 + "/" + state2, String.valueOf(witem.getFields().getOriginalEffort()), String.valueOf(witem.getFields().getActualEffortinHours()), "", "", ""};
                                        }
                                    }catch(Exception ex)
                                    {
                                        message = twoSpace + "Exception occurred : " + witem.getId() + "  " + ex.getMessage();
                                        status = util.WriteToFile(project.getLogFile(), message);
                                    }
                                }else {
                                    notApprovedItem = new String[]{witem.getFields().getTeamProject(), witem.getFields().getWorkItemType(), witem.getId(), "Revision(History) is not available ", String.valueOf(witem.getFields().getOriginalEffort()), String.valueOf(witem.getFields().getActualEffortinHours()), "", "", witem.getFields().getState()};
                                }
                                if (approvedItem[0] != null) {
                                    if (project.getDatafileRequired().equals("Y")) {
                                        approvedItem[8] = currStatus;
                                        dataLines.add(approvedItem);
                                    }
                                }
                                if (closedItem[0] != null) {
                                    if (project.getDatafileRequired().equals("Y")) {
                                        closedItem[8] = currStatus;
                                        dataLines.add(closedItem);
                                    }
                                }
                                /*if (approvedItem[0] == null && closedItem[0] == null) {
                                    if (project.getDatafileRequired().equals("Y")) {
                                        notApprovedItem[8] = currStatus;//new String[]{notApprovedItem + "," + currStatus};
                                        dataLines.add(notApprovedItem);
                                    }
                                }*/
                            }
                        }
                    }
                }
            }

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

            message = twoSpace + " Total Denominator Count = " + totaldenoCount;
            message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
            message += newLine + twoSpace + " AdoQuery = " + Query;
            message += newLine + twoSpace + " Minimum SLA = " + minsla;
            message += newLine + twoSpace + " Expected SLA = " + expectedsla;

            double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
            double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

            if (totaldenoCount == 0 && totalnumCount == 0)
            {
                totaldenoCount = 1;
                totalnumCount = 1;
            }
            else if (totaldenoCount == 0)
            {
                totaldenoCount = 1;
            }

            double actualValue = util.GetActualValueV1((double)totaldenoCount, (double)totalnumCount);
            message += newLine + twoSpace + " Actual = " + actualValue;
            if (actualValue <= expectedsla)
            {
                slaStatus = "Met";
            }
            else
            {
                slaStatus = "Not Met";
            }
            message += newLine + twoSpace + " Status = " + slaStatus;

            ProcessedData data = util.BuildProcessData(sla, (float)actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),Query);
            boolean isStatus = util.WriteToFile(project.getLogFile(), message);
            return data;
        }
        catch(Exception e)
        {
            message = twoSpace + "Exception occurred : " + e.getMessage();
            status = util.WriteToFile(project.getLogFile(), message);
        }
        return null;
    }
}
