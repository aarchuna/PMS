//package premier.premierslaautomate.ProjectService;
//
//import org.springframework.lang.Nullable;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//import premier.premierslaautomate.DataServices.AdoDataService;
//import premier.premierslaautomate.DataServices.JiraDataService;
//import premier.premierslaautomate.ENUM.CommonKey;
//import premier.premierslaautomate.ENUM.JiraTypes;
//import premier.premierslaautomate.ENUM.SLAKey;
//import premier.premierslaautomate.ENUM.SourceKey;
//import premier.premierslaautomate.Interfaces.IAdoDataService;
//import premier.premierslaautomate.Interfaces.IJiraDataService;
//import premier.premierslaautomate.Interfaces.IProject;
//import premier.premierslaautomate.Models.*;
//import premier.premierslaautomate.Models.ADO.WorkItem;
//import premier.premierslaautomate.Models.ADO.WorkItemObject;
//import premier.premierslaautomate.Utilities.CommonUtil;
//import premier.premierslaautomate.Utilities.DateUtils;
//import premier.premierslaautomate.config.MeasureConfiguration;
//import premier.premierslaautomate.config.ProjectConfiguration;
//
//import javax.print.DocFlavor;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//public class ERPMM implements IProject {
//    private IJiraDataService iJiraDataService = new JiraDataService();
//    private IAdoDataService iAdoDataService = new AdoDataService();
//
//    //Test for Arun
//    String denourl = "";
//    String numurl = "";
//    String denoJql = "";
//    String numJql = "";
//    String hellow = "";
//    int totalnumCount = 0;
//    int totaldenoCount = 0;
//    int totalNumCountSatisfied = 0;
//    int totalNumCountNotSatisfied = 0;
//    int totalNumeratorCountSatisfied = 0;
//    int totalNumeratorCountNotSatisfied = 0;
//    double expectedsla = 0;
//    double minsla = 0;
//    Float actual = 0.0f;
//    String slaStatus = "";
//
//    String currentLogPath = "";
//    String currentOutputPath = "";
//    String currentJsonPath = "";
//    String message = "";
//    String tabKey = "\t";
//    String twoSpace = "  ";
//    String newLine = "\r\n";
//    boolean status = false;
//    CommonUtil util = new CommonUtil();
//    List<String[]> dataLines = new ArrayList<>();
//
//    public ProcessedData Process(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        if (sla == null || project == null) {
//            System.out.println("Unable to process due to Project/SLA Object is Null - ERPMM");
//            return null;
//        }
//
//        //Backlog SLA Call Start
//        if (sla.getSlatype().equals(CommonKey.BACKLOG.value)) {
//            if (sla.getSlakey().equals(SLAKey.TimeToEstimateBacklogItems.toString())) {
//                return TimeToEstimateBacklogItems(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.EstimationQuality.toString())) {
//                return EstimationQuality(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.TimelyAcceptedMilestone.toString())) {
//                return TimelyAcceptedMilestone(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.TimelyBackLogItem.toString())) {
//                return TimelyBackLogItem(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.BacklogItemQuality.toString())) {
//                return BacklogItemQuality(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.DelayInReadyForProductionRelease.toString())) {
//                return DelayInReadyForProductionRelease(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.AdhereToAgileMethodology.toString())) {
//                return AdhereToAgileMethodology(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.ProductPercentTestingCompleted.toString())) {
//                return ProductPercentTestingCompleted(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.DefectDetectedInUAT.toString())) {
//                return DefectDetectedInUAT(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.IssuesDetectedPostProductionRelease.toString())) {
//                return IssuesDetectedPostProductionRelease(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.ReopenedDefectsBeforeProductionRelease.toString())) {
//                return ReopenedDefectsBeforeProductionRelease(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.PercentageOfTestsAutomated.toString())) {
//                return PercentageOfTestsAutomated(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.AverageCycleTimeForRelease.toString())) {
//                return AverageCycleTimeForRelease(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.ProcessEfficiency.toString())) {
//                return ProcessEfficiency(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.PremierCustomerSatisficationSurvey.toString())) {
//                return PremierCustomerSatisficationSurvey(sla, userName, password, project, retrievedIssues);
//            }
//        }
//
//        //Backlog SLA Call End
//
//        //Non-Backlog SLA Call Start
//        if (sla.getSlatype().equals(CommonKey.NONBACKLOG.value)) {
//            if (sla.getSlakey().equals(SLAKey.SeverityLvl1IncidentResolution.toString())) {
//                return SeverityLvl1IncidentResolution(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.SeverityLvl2IncidentResolution.toString())) {
//                return SeverityLvl1IncidentResolution(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.SeverityLvl3IncidentResolution.toString())) {
//                return SeverityLvl1IncidentResolution(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.PercentOfIncidentOpened.toString())) {
//                return PercentOfIncidentOpened(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.MTTR.toString())) {
//                return MTTR(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.SystemUpTime.toString())) {
//                return SystemUpTime(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.VolumeOfIncidents.toString())) {
//                return VolumeOfIncidents(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.PercentageofNBServicesAutomate.toString())) {
//                return PercentageofNBServicesAutomate(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.ITCustomerSatisfication.toString())) {
//                return ITCustomerSatisfication(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.NotifyToCustomerOfOutrage.toString())) {
//                return NotifyToCustomerOfOutrage(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.ProblemRCATime.toString())) {
//                return ProblemRCATime(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.ProblemResolutionTime.toString())) {
//                return ProblemResolutionTime(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.RegulatoryUpdate.toString())) {
//                return RegulatoryUpdate(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.CriticalSecurityThreatMitigation.toString())) {
//                return CriticalSecurityThreatMitigation(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.Patches.toString())) {
//                return Patches(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.ServiceLevelDataQuality.toString())) {
//                return ServiceLevelDataQuality(sla, userName, password, project, retrievedIssues);
//            } else if (sla.getSlakey().equals(SLAKey.SecurityThreatMitigation.toString())) {
//                return SecurityThreatMitigation(sla, userName, password, project, retrievedIssues);
//            }
//        }
//        //Non-Backlog SLA Call End
//
//        return null;
//    }
//
//    //BackLog SLA Calculation -- Start
//    private ProcessedData TimeToEstimateBacklogItems(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Modified and tested with July Data - Simanchal
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//
//        //Method Specific Varriable Declaration Area - Start
//
//        //Generic for each method
//        String baseURI = "";
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<Issue> commentIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        String processingLog = "";
//
//        //Method Specific Variables
//
//        String commentSection = "";
//        String strReceivedDate = "";
//        String strEstimatedDate = "";
//        Float originalEstimation = 0f;
//
//        String receivedForEstimationFromConfig = "";
//        String estimationCompletedFromConfig = "";
//        String statusesFromConfig = "";
//        String limitFromConfig = "";
//        String fromdateFromConfig = "";
//        String todateFromConfig = "";
//        String dateFormatFromConfig = "";
//        String commentURI = "";
//
//        Float storyLimit = 0f;
//        Float epicLimit = 0f;
//        Float daystoProvideEstimation = 0f;
//        String estimatdatefromconfig = "";
//        int position = 0;
//        Float estimationhour = 0f;
//        boolean isenabledforestimation = false;
//
//        List<TimeToOfferBackLogJira> sourceDataLst = new ArrayList<>();
//        //List<Issue> eligibleForEstimation = new ArrayList<>();
//        TimeToOfferBackLogJira sourceData = new TimeToOfferBackLogJira();
//        List<Comments> comments = new ArrayList<>();
//
//        Date dtReceivedDate;
//        Date dtEstimationDate;
//        Date dtFromDate;
//        Date dtToDate;
//        long dateDifference = 0;
//        double actualValue = 0;
//        //Method Specific Varriable Declaration Area - End
//
//        String detailLogFilePath = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.log";
//        processingLog = "Processing  SLA : " + sla.getSlaname();
//        if (project.getDetailedLogRequired().equals("Y")) {
//            status = util.WriteToFile(detailLogFilePath, processingLog);
//        }
//
//        try {
//            /*Jira Implementation*/
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Region For Configuration Data Retrival and Data Validation - Start
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
//                fromdateFromConfig = sla.getFrom().replace("'", "");
//                todateFromConfig = sla.getTo().replace("'", "");
//                dateFormatFromConfig = project.getDateFormat().replace("'", "");
//                receivedForEstimationFromConfig = sla.getConfig1().replace("'", "");
//                estimationCompletedFromConfig = sla.getConfig2().replace("'", "");
//                statusesFromConfig = sla.getConfig3().replace("'", "");
//                limitFromConfig = sla.getLimit().replace("'", "");
//
//                if (dateFormatFromConfig.equals("")) {
//                    message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (!fromdateFromConfig.equals("")) {
//                    if (util.isDateValid(fromdateFromConfig, dateFormatFromConfig) == false) {
//                        message = twoSpace + "From Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
//                        status = util.WriteToFile(project.getLogFile(), message);
//                        return null;
//                    }
//                } else {
//                    message = twoSpace + "From Date is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (!todateFromConfig.equals("")) {
//                    if (util.isDateValid(todateFromConfig, dateFormatFromConfig) == false) {
//                        message = twoSpace + "To Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
//                        status = util.WriteToFile(project.getLogFile(), message);
//                        return null;
//                    }
//                } else {
//                    message = twoSpace + "To Date is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                dtFromDate = util.ConvertToDate(fromdateFromConfig, dateFormatFromConfig);
//                dtToDate = util.ConvertToDate(todateFromConfig, dateFormatFromConfig);
//
//                if (dtFromDate == null || dtToDate == null) {
//                    message = twoSpace + "From and To date is not valid, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//
//                }
//
//                if (receivedForEstimationFromConfig.equals("")) {
//                    message = twoSpace + "Received Estimation Keyword Not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (estimationCompletedFromConfig.equals("")) {
//                    message = twoSpace + "Estimation Completed Keyword Not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (statusesFromConfig.equals("")) {
//                    message = twoSpace + "Status data Not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (limitFromConfig.equals("")) {
//                    message = twoSpace + "Limit data Not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                baseURI = project.getProjecturl() + "/api/latest/search?";
//                commentURI = project.getProjecturl() + "/agile/1.0/issue";
//
//                String limitValue = sla.getLimit();
//
//                //Split it to get the values
//                if (!limitValue.isEmpty()) {
//                    String[] limits = limitValue.split("#", -2);
//                    try {
//                        if (limits.length >= 2) {
//                            if (!limits[0].isEmpty()) {
//                                storyLimit = Float.parseFloat(limits[0]);
//                            }
//
//                            if (!limits[1].isEmpty()) {
//                                epicLimit = Float.parseFloat(limits[1]);
//                            }
//                        }
//                    } catch (Exception exLimitParsing) {
//                        message = twoSpace + "Error while parsing the Limit Values. Stopping the Process. Error: " + exLimitParsing.getMessage();
//                        ;
//                        status = util.WriteToFile(project.getLogFile(), message);
//                        return null;
//                    }
//                }
//
//                if (storyLimit == 0 && epicLimit == 0) {
//                    message = twoSpace + "Invalid values in Limit configuration. Please check your config. Stopping the processing for the SLA";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                } else {
//                    message += newLine + twoSpace + "Story Level Limit : " + storyLimit.toString();
//                    message += newLine + twoSpace + "Epic Level Limit : " + epicLimit.toString();
//                }
//
//                //Region Configuration Data Retrival and Data Validation - End
//
//                //Region to Retrieve data  - Start
//
//                denoIssue = retrievedIssues;
//                //Region to Retrieve data  - End
//
//                //Region to Define data file  - Start
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"StoryId", "Type", "Received Date", "Estimated Date", "Eligible For estimation", "Estimation Hour",
//                                    "Variance (In Days)", "Within Limit"});
//                }
//                //Region to Define data file  - End
//
//                ///////// SLA Implementation Stats
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                totalNumCountSatisfied = 0;
//                totalNumCountNotSatisfied = 0;
//
//                ////////// Business Logic Implementation Starts  //////////
//
//                processingLog = processingLog + newLine + "Processing Issues";
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    for (Issue issue : denoIssue) {
//                        processingLog = processingLog + newLine + "Processing Story : " + issue.getKey();
//                        comments = new ArrayList<>();
//                        comments = issue.getFields().getComment().getComments();
//
//
//
//                        strReceivedDate = "";
//                        strEstimatedDate = "";
//
//                        //Process each comment section to find the Receive date and Estimation date
//                        for (Comments cmnt : comments) {
//                            processingLog = processingLog + newLine + twoSpace + "Processing Comments";
//                            commentSection = cmnt.getBody();
//                            processingLog = processingLog + newLine + twoSpace + "Comment body" + commentSection;
//                            processingLog = processingLog + newLine + twoSpace + "Identifying Receive keyword " + receivedForEstimationFromConfig;
//
//                            if (cmnt.getBody().startsWith(receivedForEstimationFromConfig) || cmnt.getBody().contains(receivedForEstimationFromConfig)) {
//                                //Read the comment to find the receive date
//                                processingLog = processingLog + newLine + twoSpace + "Receive keyword : " + receivedForEstimationFromConfig + "Found";
//                                strReceivedDate = util.GetReceiveEstimationDates(commentSection, receivedForEstimationFromConfig, "#", dateFormatFromConfig);
//
//                                if (!strReceivedDate.equals("")) {
//                                    if (strReceivedDate.startsWith("Error")) {
//                                        processingLog = processingLog + newLine + twoSpace + "Error while retrieving the received date, " + strReceivedDate;
//                                        strReceivedDate = "";
//                                    } else {
//                                        processingLog = processingLog + newLine + twoSpace + "Successfully retrieved the received date, " + strReceivedDate;
//                                    }
//                                } else {
//                                    processingLog = processingLog + newLine + twoSpace + "No Receive date Found, the issue will be not condidered";
//                                }
//                            } else {
//                                processingLog = processingLog + newLine + twoSpace + "Receive keyword : " + receivedForEstimationFromConfig + "not found";
//                            }
//
//                            processingLog = processingLog + newLine + twoSpace + "Identifying Estimation completed keyword " + estimationCompletedFromConfig;
//                            if (cmnt.getBody().startsWith(estimationCompletedFromConfig) || cmnt.getBody().contains(estimationCompletedFromConfig)) {
//                                //Read the comment to find the estimation date
//                                processingLog = processingLog + newLine + twoSpace + "Estimation complete keyword : " + estimationCompletedFromConfig + "Found";
//                                strEstimatedDate = util.GetReceiveEstimationDates(commentSection, estimationCompletedFromConfig, "#", dateFormatFromConfig);
//                                if (!strEstimatedDate.equals("")) {
//                                    if (strEstimatedDate.startsWith("Error")) {
//                                        processingLog = processingLog + newLine + twoSpace + "Error while retrieving the estimation date, " + strEstimatedDate;
//                                        strEstimatedDate = "";
//                                    } else {
//                                        processingLog = processingLog + newLine + twoSpace + "Successfully retrieved the estimation date, " + strEstimatedDate;
//                                    }
//                                } else {
//                                    processingLog = processingLog + newLine + twoSpace + "No Estimation date Found, the issue will be not considered";
//                                }
//                            } else {
//                                processingLog = processingLog + newLine + twoSpace + "Estimation Complete keyword : " + estimationCompletedFromConfig + "not found";
//                            }
//                        }
//
//                        if (!strReceivedDate.equals("")) {
//                            //strReceivedDate = "06/30/2022";
//                            //Check if the receive date is within the from date and to date
//                            dtReceivedDate = util.ConvertToDate(strReceivedDate, dateFormatFromConfig);
//                            //System.out.println("dtReceivedDate"+dtReceivedDate);
//                            if (dtReceivedDate != null) {
//                                //Checking whether the receive date is in between from date and to date (measurement period)
//                                if ((dtReceivedDate.compareTo(dtFromDate) >= 0 && dtReceivedDate.compareTo(dtToDate) <= 0) == true) {
//                                    //The Issue is eligible as the received date is between the measurement period
//                                    processingLog = processingLog + newLine + twoSpace + "The Issue is eligible as the received date " + strReceivedDate + " is between the measurement period " + fromdateFromConfig + " - " + todateFromConfig;
//                                    dtEstimationDate = util.ConvertToDate(strEstimatedDate, dateFormatFromConfig);
//
//                                    //Add to the Object List
//                                    isenabledforestimation = false;
//                                    estimationhour = 0f;
//                                    sourceData = new TimeToOfferBackLogJira();
//                                    sourceData.setStoryId(issue.getKey());
//                                    sourceData.setType(issue.getFields().getIssuetype().getName());
//                                    sourceData.setReceiveddate(dtReceivedDate);
//
//                                    if (dtEstimationDate == null) {
//                                        sourceData.setEstimateddate(null);
//                                        sourceData.setEligibleforestimation(false);
//                                    } else {
//                                        isenabledforestimation = true;
//                                        sourceData.setEstimateddate(dtEstimationDate);
//                                        sourceData.setEligibleforestimation(true);
//
//                                        // If the Receive and estimation date is same the then differentce is 1 day
//                                        if ((dtReceivedDate.compareTo(dtEstimationDate)) == 0) {
//                                            dateDifference = 1;
//                                        } else {
//                                            dateDifference = dtEstimationDate.getTime() - dtReceivedDate.getTime();
//                                            dateDifference = TimeUnit.DAYS.convert(dateDifference, TimeUnit.MILLISECONDS);
//                                        }
//                                        sourceData.setDaystoprovideestimation(dateDifference);
//
//                                        if (issue.getFields().getCustomfield_14000() != null && !(issue.getFields().getCustomfield_14000().equals(""))) {
//                                            estimationhour = Float.parseFloat(issue.getFields().getCustomfield_14000());
//                                        }
//                                        sourceData.setEstimationhour(estimationhour);
//
//                                        if (issue.getFields().getIssuetype().getName().equals(JiraTypes.EPIC)) {
//                                            //Check with the Epic Limit coming from the Configuration File
//                                            if (isenabledforestimation == true && dateDifference <= epicLimit) {
//                                                sourceData.setWithinlimit(true);
//                                            } else {
//                                                sourceData.setWithinlimit(false);
//
//                                            }
//                                        } else {
//                                            //Story Level
//                                            //Check with the Epic Limit coming from the Configuration File
//                                            if (isenabledforestimation == true && dateDifference <= storyLimit) {
//                                                sourceData.setWithinlimit(true);
//
//                                            } else {
//                                                sourceData.setWithinlimit(false);
//
//                                            }
//                                        }
//                                    }
//
//                                    sourceDataLst.add(sourceData);
//
//                                    //Temporarily Adding the datalines here
//                                    if (project.getDatafileRequired().equals("Y")) {
//                                        DateFormat dateFormat = new SimpleDateFormat(dateFormatFromConfig);
//                                        strReceivedDate = dateFormat.format(sourceData.getReceiveddate());
//                                        strEstimatedDate = "";
//                                        if (sourceData.getEstimateddate() != null) {
//                                            strEstimatedDate = dateFormat.format(sourceData.getEstimateddate());
//                                        }
//                                        strReceivedDate = dateFormat.format(sourceData.getReceiveddate());
//
//                                        dataLines.add(new String[]
//                                                {sourceData.getStoryId(), sourceData.getType(), strReceivedDate, strEstimatedDate, String.valueOf(sourceData.isEligibleforestimation()), "",
//                                                        String.valueOf(sourceData.getDaystoprovideestimation()), String.valueOf(sourceData.isWithinlimit())});
//                                    }
//                                } else {
//                                    processingLog = processingLog + newLine + twoSpace + "The Issue is not eligible as the received date " + strReceivedDate + " is not between the measurement period " + fromdateFromConfig + " - " + todateFromConfig;
//                                }
//                            } else {
//                                processingLog = processingLog + newLine + twoSpace + "Cannot able to parse the receive date, this issue cannot be considered";
//                            }
//
//                        }
//                    }
//                }
//
//                //Write the detailed Log file
//                if (project.getDetailedLogRequired().equals("Y")) {
//                    status = util.WriteToFile(detailLogFilePath, processingLog);
//                }
//
//                ////////// Business Logic Implementation Ends  //////////
//                totaldenoCount = (int) sourceDataLst.stream().filter(x -> x.isEligibleforestimation() == true).count();
//                totalnumCount = (int) sourceDataLst.stream().filter(x -> x.isEligibleforestimation() == true && x.isWithinlimit() == true).count();
//
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
//                ///////Data File Saving - End
//
//                ///////// SLA Implementation End
//
//                /////////////////SLA Calculation Starts - Donot Delete the Code here (Generic Code)
//
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
//                actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//
//                // ------------end-----------
//
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, expectedsla, minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                /////////////////SLA Calculation Starts
//
//                //Create the return object
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            /*Ado Implementation*/
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//    }
//
//    private ProcessedData TimelyAcceptedMilestone(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Modified and tested with July Data - Simanchal
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
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
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getNumjql();
//                    //numurl =  project.getProjecturl() + "/api/2/search?jql=" + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
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
//                if (numurl.isEmpty() || denourl.isEmpty()) {
//                    message = twoSpace + "numJQL OR demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, pageSize);
//                numIssue = iJiraDataService.getIssuesUsingJQL(userName, password, numurl, "", false, false, pageSize);
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"StoryID", "Type", "Status"});
//                }
//
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                message = "";
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"Denominator Issues", "", ""});
//                    }
//
//                    for (Issue issue : denoIssue) {
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines.add(new String[]
//                                    {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName()});
//                        }
//                    }
//                }
//
//                if (numIssue != null && numIssue.size() > 0) {
//                    totalnumCount = numIssue.size();
//
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"", "", ""});
//                        dataLines.add(new String[]
//                                {"Numerator Issues", "", ""});
//                    }
//
//                    for (Issue issue : numIssue) {
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines.add(new String[]
//                                    {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName()});
//                        }
//                    }
//                }
//
//                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                //If user configuration is true to create the datafile then save it
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//    }
//
//    private ProcessedData TimelyBackLogItem(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Modified and tested with July Data - Simanchal
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Jira Processing
//                // Setup the JQL if it is using.
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
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
//                if (numurl != "" && denourl != "") {
//                    baseURI = project.getProjecturl() + "/api/latest/search?";
//                    denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, pageSize);
//                    numIssue = iJiraDataService.getIssuesUsingJQL(userName, password, numurl, "", false, false, pageSize);
//                } else {
//                    //Either process it in a different way or stop the processing by sending a messae
//                    //Currently we are stopping the processing.
//                    message = twoSpace + "numJQL and demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"StoryID, Type"});
//                }
//
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"Denominator Issues", ""});
//                    }
//
//                    for (Issue issue : denoIssue) {
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines.add(new String[]
//                                    {issue.getKey(), issue.getFields().getIssuetype().getName()});
//                        }
//                    }
//                }
//
//                if (numIssue != null && numIssue.size() > 0) {
//                    totalnumCount = numIssue.size();
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"", ""});
//                        dataLines.add(new String[]
//                                {"Numerator Issues", ""});
//                    }
//                    for (Issue issue : numIssue) {
//                        //if the SLA required process each story to identify whether the story meets the limit
//                        //Developer Comment -- There is no Story level processing
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines.add(new String[]
//                                    {issue.getKey(), issue.getFields().getIssuetype().getName()});
//                        }
//                    }
//                }
//
//                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
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
//
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, expectedsla, minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//    }
//
//    private ProcessedData BacklogItemQuality(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<Issue> commentIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        int count = 0;
//        int i = 0;
//        Boolean defect = true;
//        String strPageSize = "";
//        int pageSize = 1000;
//        double actualValue = 0;
//
//        String keyWord1 = "";
//        String keyWord2 = "";
//        String defectIssueType = "";
//        String statusToCheckforFTR = "";
//        String closedStatus = "";
//        String prevCloseStatus = "";
//        History closedHistory = new History();
//        History ftrHistory = new History();
//
//        String ftrfromHistory = "";
//        String strClosedDate = "";
//        String strClosedStatus = "";
//        String strftrDate = "";
//        String strftrStatus = "";
//
//        try {
//            totaldenoCount = 0;
//            totalnumCount = 0;
//
//            strPageSize = project.getPageSize();
//            if (!strPageSize.isEmpty()) {
//                try {
//                    pageSize = Integer.parseInt(strPageSize);
//                } catch (Exception exPageSizeParse) {
//                    pageSize = 1000;
//                }
//            }
//
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Jira Processing
//                // Setup the JQL if it is using.
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=" + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=" + sla.getDenojql();
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
//                if (numurl != "" && denourl != "") {
//                    baseURI = project.getProjecturl() + "/api/latest/search?";
//                    String commentURI = project.getProjecturl() + "/agile/1.0/issue";
//                    //Get Deno result - Without ChangeLog
//                    denoIssue = iJiraDataService.getAllIssueOnJQL(denourl, userName, password, baseURI);
//                    //Get Mum result - With ChangeLog and comment
//                    numIssue = iJiraDataService.getAllIssuesOnJQLV1(userName, password, numurl, baseURI, commentURI, true, true);
//                } else {
//                    //Either process it in a different way or stop the processing by sending a messae
//                    //Currently we are stopping the processing.
//                    message = twoSpace + "numJQL and demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                //Get it from Config
//                keyWord1 = "#Code Rework";
//                keyWord2 = "#BA REWORK";
//                defectIssueType = "Defect";
//                statusToCheckforFTR = "";
//
//                keyWord1 = sla.getConfig1().replace("'", "");
//                keyWord2 = sla.getConfig2().replace("'", "");
//                defectIssueType = sla.getConfig3().replace("'", "");
//                statusToCheckforFTR = sla.getConfig4().replace("'", "");
//                closedStatus = sla.getConfig5().replace("'", "");
//
//
//
//                if (statusToCheckforFTR.equals("")) {
//                    message = twoSpace + "Status(s) to check FTR not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String[] arrstatusToCheckforFTR = statusToCheckforFTR.split(",");
//                if (arrstatusToCheckforFTR == null && arrstatusToCheckforFTR.length == 0) {
//                    message = twoSpace + "Status(s) to check FTR not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (closedStatus.equals("")) {
//                    message = twoSpace + "Status for the Issue from which the FTR Check done not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String[] arrClosedStatus = closedStatus.split(",");
//                if (arrClosedStatus != null && arrClosedStatus.length > 2) {
//                    message = twoSpace + "Only Two status(s) can be mentioned for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (arrClosedStatus != null && arrClosedStatus.length >= 1) {
//                    closedStatus = arrClosedStatus[0];
//                }
//
//                if (arrClosedStatus != null && arrClosedStatus.length >= 2) {
//                    prevCloseStatus = arrClosedStatus[1];
//                }
//
//                if (closedStatus.isEmpty()) {
//                    message = twoSpace + "Closed Status for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                totaldenoCount = 0;
//                totalnumCount = 0;
//
//                message = "";
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"IssueId", "Type", "Issue Status", "IS FTR", "Dev/Testing Completed for Long80", "Complete Status for Long80", "Details"});
//                }
//
//
//
//
//                //Look in NumIssues for related fields to identify whether the story satisfies the Limit or not
//
//                if (numIssue != null && numIssue.size() > 0) {
//                    for (Issue issue : numIssue) {
//                        if (issue != null) {
//                            boolean isConditionSatisfied = false; //FTR. If true Not FTR
//                            String outwardType = "";
//                            String inwardType = "";
//                            String defectKey = "";
//                            String commentSection = "";
//                            String multipleInDevelopment = "";
//
//                            ftrfromHistory = "";
//                            strClosedDate = "";
//                            strftrDate = "";
//                            strftrStatus = "";
//                            strClosedStatus = "";
//
//                            String issueType = issue.getFields().getIssuetype().getName();
//                            List<Issuelinks> issuelinks = new ArrayList<>();
//
//                            if (issue.getFields().getIssuelinks() != null && issue.getFields().getIssuelinks().size() > 0) {
//                                issuelinks = issue.getFields().getIssuelinks();
//                            }
//
//                            //Condition 1 --> from the Issue Link check if there are any defects created or not if the configuration value is there.
//                            if (defectIssueType.isEmpty()) {
//                                //1. if the Current issue is a defect, then check whether it is created for any of the issues in the processing sprints.
//                                if (issueType.equals(defectIssueType)) {
//                                    //Code to be completed
//                                    if (issuelinks != null && issuelinks.size() > 0) {
//                                        for (Issuelinks issLink : issuelinks) {
//                                            if (issLink != null) {
//                                                if (issLink.getOutwardIssue() != null) {
//                                                    //Chek if it has an issue tagged except defect. If yes, Find that issue in the numIssues list
//                                                    //If found then this means this is a defect created for this issue in the processing sprints. Not a FTR
//                                                }
//                                                if (issLink.getInwardIssue() != null) {
//                                                    //Chek if it has an issue tagged except defect. If yes, Find that issue in the numIssues list
//                                                    //If found then this means this is a defect created for this issue in the processing sprints. Not a FTR
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//
//                                //2. If Current issue is not a defect then, Check whether there is any defect create for the issue within the processing sprints
//                                if (issueType.equals(defectIssueType)) {
//                                    //Code to be added
//                                }
//                            }
//
//                            //Condition 2 --> Check in the comment Section if any defined comment given for the story
//                            if (isConditionSatisfied == false) {
//                                if (!keyWord1.isEmpty() || !keyWord2.isEmpty()) {
//                                    List<Comments> comments = new ArrayList<>();
//                                    comments = null;
//                                    if (issue.getFields().getComment() != null) {
//                                        if (issue.getFields().getComment().getComments() != null && issue.getFields().getComment().getComments().size() > 0) {
//                                            comments = issue.getFields().getComment().getComments();
//                                        }
//                                    }
//
//                                    for (Comments cmnt : comments) {
//                                        if (cmnt != null) {
//                                            if (!keyWord1.isEmpty()) {
//                                                if (cmnt.getBody() != null) {
//                                                    if (cmnt.getBody().startsWith(keyWord1) || cmnt.getBody().contains(keyWord1)) {
//                                                        commentSection = commentSection + " Code Review Comment";
//                                                        isConditionSatisfied = true;
//                                                    }
//                                                }
//                                            }
//
//                                            if (!keyWord2.isEmpty()) {
//                                                if (cmnt.getBody() != null) {
//                                                    if (cmnt.getBody().startsWith(keyWord2) || cmnt.getBody().contains(keyWord2)) {
//                                                        commentSection = commentSection + " BA Review Comment";
//                                                        isConditionSatisfied = true;
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            //Condition 3 - Check the Changelog History to get the FTR
//                            if (isConditionSatisfied == false) {
//                                List<History> historyList = new ArrayList<>();
//                                if (issue.getChangelog() != null) {
//                                    if (issue.getChangelog().getHistories() != null && issue.getChangelog().getHistories().size() > 0) {
//                                        historyList = issue.getChangelog().getHistories();
//                                    }
//                                }
//
//                                if (historyList != null && historyList.size() > 0) {
//                                    //Get the First Occurance data of the CLosed Status and get the date. Then we will find out the Status
//                                    //to be identified
//                                    closedHistory = null;
//                                    ftrfromHistory = null;
//                                    boolean firstOccuranceClosed = false;
//                                    boolean firstOccuranceFTR = false;
//                                    ftrHistory = null;
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
//                                                                    strClosedStatus = item.getToString();
//                                                                    strClosedDate = history.getCreated();
//                                                                }
//                                                            }
//                                                        }
//
//                                                        if (!prevCloseStatus.isEmpty()) {
//                                                            if (firstOccuranceClosed == false) {
//                                                                if (item.getToString().equals(prevCloseStatus.trim())) {
//                                                                    closedHistory = history;
//                                                                    firstOccuranceClosed = true;
//                                                                    strClosedStatus = item.getToString();
//                                                                    strClosedDate = history.getCreated();
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            //Find the Occurance of FTR as we got the Closed Status. We need to compare the date also
//                                            if (closedHistory != null) {
//                                                if (firstOccuranceFTR == false) {
//                                                    for (Item item : itemList) {
//                                                        if (item.getField().equals("status")) {
//                                                            for (String cStatus : arrstatusToCheckforFTR) {
//                                                                if (firstOccuranceFTR == false) {
//                                                                    if (item.getToString().equals(cStatus.trim())) {
//                                                                        ftrHistory = history;
//                                                                        firstOccuranceFTR = true;
//                                                                        strftrStatus = item.getToString();
//                                                                        strftrDate = history.getCreated();
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
//                                if (ftrHistory != null) {
//                                    isConditionSatisfied = true;
//
//                                    if (closedHistory != null) {
//                                        ftrfromHistory = "Issue Closed for Long80 : " + strClosedDate + " with status : " + strClosedStatus;
//                                    }
//                                    ftrfromHistory = ftrfromHistory + " Issue Reopened for Long80 : " + strftrDate + " with status : " + strftrStatus;
//                                }
//                            }
//
//                            //If isConditionSatisfied = true, then it is not FTR
//
//                            if (isConditionSatisfied == false) {
//                                totalnumCount++;
//                            }
//
//                            if (project.getDatafileRequired().equals("Y")) {
//                                if (isConditionSatisfied == false) {
//                                    dataLines.add(new String[]
//                                            {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName(), "Yes", strClosedDate, strClosedStatus, ""});
//                                } else {
//                                    String totalComment = "";
//                                    if (!defectKey.isEmpty()) {
//                                        totalComment = "Defects : " + defectKey;
//                                    }
//
//                                    if (!commentSection.isEmpty()) {
//                                        totalComment = totalComment + "Comment Section Data : " + commentSection;
//                                    }
//
//                                    if (!ftrfromHistory.isEmpty()) {
//                                        totalComment = totalComment + "FTR Info : " + ftrfromHistory;
//                                    }
//
//                                    dataLines.add(new String[]
//                                            {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName(), "No", strClosedDate, strClosedStatus, totalComment});
//                                }
//                            }
//
//                        }
//                    }
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
//                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
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
//                actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//        return null;
//    }
//
//    private ProcessedData ProductPercentTestingCompleted(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Modified and tested with July Data - Simanchal
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        totalNumCountSatisfied = 0;
//        totalNumCountNotSatisfied = 0;
//        List<String[]> dataLines = new ArrayList<>();
//        int regression = 0;
//        String strPageSize = "";
//        int pageSize = 1000;
//        double actualValue = 0;
//
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Jira Processing
//                // Setup the JQL if it is using.
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
//                }
//
//                expectedsla = Integer.parseInt(sla.getExpectedsla());
//                minsla = Integer.parseInt(sla.getMinimumsla());
//
//                regression = 0;
//                String strregression = sla.getInput1();
//
//                try {
//                    regression = Integer.parseInt(strregression);
//                } catch (Exception exregression) {
//                    message = twoSpace + "Regression cannot be parsed, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (expectedsla == 0 || minsla == 0) {
//                    //Stop the processing
//                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (numurl != "" && denourl != "") {
//                    denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, pageSize);
//                    numIssue = iJiraDataService.getIssuesUsingJQL(userName, password, numurl, "", false, false, pageSize);
//                } else {
//                    //Either process it in a different way or stop the processing by sending a messae
//                    //Currently we are stopping the processing.
//                    message = twoSpace + "numJQL and demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                totalnumCount = 0;
//                totaldenoCount = 0;
//                message = "";
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"StoryID", "Type"});
//                }
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size() + regression;
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"Denominator Issues", "", ""});
//                    }
//
//
//                    for (Issue issue : denoIssue) {
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines.add(new String[]
//                                    {issue.getKey(), issue.getFields().getIssuetype().getName()});
//                        }
//                    }
//                }
//                message = twoSpace + " Total Deno Count = " + totaldenoCount;
//
//                if (numIssue != null && numIssue.size() > 0) {
//                    totalnumCount = numIssue.size() + regression;
//
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"", "", ""});
//                        dataLines.add(new String[]
//                                {"Numerator Issues", "", ""});
//                    }
//
//                    for (Issue issue : numIssue) {
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines.add(new String[]
//                                    {issue.getKey(), issue.getFields().getIssuetype().getName(), ""});
//                        }
//                    }
//                }
//                message += newLine + twoSpace + " Total Num Count = " + totalnumCount;
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"", ""});
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Input1", ""});
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {String.valueOf(regression), ""});
//                }
//
//                //If user configuration is true to create the datafile then save it
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
//                //As we got all the data, calculate the SLA Status
//                actualValue = util.GetActualValueV1((double) totaldenoCount, (double) totalnumCount);
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, expectedsla, minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                //Create the return object
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//    }
//
//    private ProcessedData AdhereToAgileMethodology(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Modified and tested with July Data - Simanchal
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        List<String[]> dataLines2 = new ArrayList<>();
//        String strPageSize = "";
//        int pageSize = 1000;
//        double actualValue = 0;
//        try {
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
//                String strActualValue = sla.getInput1();
//                if (strActualValue.isEmpty()) {
//                    message = twoSpace + "Actual Value not provided, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                try {
//                    actualValue = Float.parseFloat((sla.getInput1()));
//                } catch (Exception exParseActualValue) {
//                    message = twoSpace + "Not able to parse Actual Value (Numeric value required), please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + actualValue;
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, expectedsla, minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(totalnumCount), String.valueOf(totalnumCount));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//        return null;
//    }
//
//    private ProcessedData DefectDetectedInUAT(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Modified and tested with July Data - Simanchal
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines1 = new ArrayList<>();
//
//        int count = 0;
//        int i = 0;
//        Boolean defect = true;
//
//        int pageSize = 1000;
//        String strPageSize = "";
//        String issueDefectType = "";
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
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
//                issueDefectType = sla.getConfig1();
//                if (issueDefectType.isEmpty()) {
//                    message = twoSpace + "Issue Defect type not found, using default value Defect, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    issueDefectType = "Defect";
//                }
//
//                if (numurl != "" && denourl != "") {
//                    denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, pageSize);
//                    numIssue = iJiraDataService.getIssuesUsingJQL(userName, password, numurl, "", false, false, pageSize);
//                } else {
//                    message = twoSpace + "numJQL and demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                totalnumCount = 0;
//                totaldenoCount = 0;
//                message = "";
//                List<String> storyKeys = new ArrayList<>();
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines1.add(new String[]
//                            {"ID", "Type", "Storyid ", "Eligibility"});
//                }
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines1.add(new String[]
//                                {"Denominator Issues", "", "", ""});
//                    }
//
//                    for (Issue issue : denoIssue) {
//                        storyKeys.add(issue.getKey());
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines1.add(new String[]
//                                    {issue.getKey(), issue.getFields().getIssuetype().getName(), "", ""});
//                        }
//                    }
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines1.add(new String[]
//                            {"", "", "", ""});
//                    dataLines1.add(new String[]
//                            {"Numerator Issues", "", "", ""});
//                }
//
//                if (numIssue != null && numIssue.size() > 0) {
//                    for (Issue issue : numIssue) {
//                        //Loop Throuth eac issue and findout whether this is linked any Story available in the Deno JQL
//                        boolean isConditionSatisfied = false;
//                        String outwardType = "";
//                        String inwardType = "";
//                        String storyKey = "";
//                        String tmpStoryKey = "";
//                        boolean isStoryMatched = false;
//
//                        String a = issue.getFields().getIssuetype().getName();
//                        //if (issue.getFields().getIssuetype().getName().equals("Defect"))
//                        if (issue.getFields().getIssuetype().getName().equals(issueDefectType)) {
//                            List<Issuelinks> issuelinks = new ArrayList<>();
//                            if (issue.getFields().getIssuelinks() != null) {
//                                issuelinks = issue.getFields().getIssuelinks();
//                            }
//
//                            storyKey = "";
//                            isConditionSatisfied = false;
//
//                            if (issuelinks != null && issuelinks.size() > 0) {
//                                for (Issuelinks issLink : issuelinks) {
//                                    outwardType = "";
//                                    inwardType = "";
//
//                                    if (issLink.getOutwardIssue() != null) {
//                                        outwardType = issLink.getOutwardIssue().getFields().getIssuetype().getName();
//                                    }
//                                    if (issLink.getInwardIssue() != null) {
//                                        inwardType = issLink.getInwardIssue().getFields().getIssuetype().getName();
//                                    }
//
//                                    isStoryMatched = false;
//                                    if (!outwardType.isEmpty()) {
//                                        //if (!outwardType.equals("Defect"))
//                                        if (!outwardType.equals(issueDefectType)) {
//                                            //Check whether the tagged story belong to our Deno JQL Story
//                                            for (String str : storyKeys) {
//                                                if (!str.isEmpty()) {
//                                                    if (!issLink.getOutwardIssue().getKey().isEmpty()) {
//                                                        if (str.equals(issLink.getOutwardIssue().getKey())) {
//                                                            isConditionSatisfied = true;
//                                                            isStoryMatched = true;
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            if (isStoryMatched == true) {
//                                                storyKey = storyKey + issLink.getOutwardIssue().getKey() + "#";
//                                            }
//                                        }
//                                    }
//
//                                    isStoryMatched = false;
//                                    if (!inwardType.isEmpty()) {
//                                        //if (!inwardType.equals("Defect"))
//                                        if (!inwardType.equals(issueDefectType)) {
//                                            //Check whether the tagged story belong to our Deno JQL Story
//                                            for (String str : storyKeys) {
//                                                if (!str.isEmpty()) {
//                                                    if (!issLink.getInwardIssue().getKey().isEmpty()) {
//                                                        if (str.equals(issLink.getInwardIssue().getKey())) {
//                                                            isConditionSatisfied = true;
//                                                            isStoryMatched = true;
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            //Add it if there is a match
//                                            if (isStoryMatched == true) {
//                                                storyKey = storyKey + issLink.getInwardIssue().getKey() + "#";
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            //The issue links are processed, so find whether Defect is success or not
//                            if (isConditionSatisfied == true) {
//                                totalNumeratorCountSatisfied++;
//                                totalnumCount++;
//                            } else {
//                                totalNumeratorCountNotSatisfied++;
//                            }
//
//                            //write to the Log here
//                            if (project.getDatafileRequired().equals("Y")) {
//                                if (isConditionSatisfied == true) {
//                                    dataLines1.add(new String[]
//                                            {issue.getKey(), issue.getFields().getIssuetype().getName(), storyKey, "Met"});
//                                } else {
//                                    dataLines1.add(new String[]
//                                            {issue.getKey(), issue.getFields().getIssuetype().getName(), storyKey, "Not Met"});
//
//                                }
//                            }
//                        }
//                    }
//                }
//
//                //If user configuration is true to create the datafile then save it
//                if (dataLines1.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines1, dataFileName);
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
//                message += newLine + twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
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
//                double actualValue = util.GetActualValueV1((double) totaldenoCount, (double) (totaldenoCount - totalnumCount));
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//                status = util.WriteToFile(project.getLogFile(), message);
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//            throw ex;
//        }
//
//    }
//
//    private ProcessedData IssuesDetectedPostProductionRelease(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Modified and tested with July Data - Simanchal
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines1 = new ArrayList<>();
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
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
//                if (denourl.isEmpty()) {
//                    message = twoSpace + "Deno JQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines1.add(new String[]
//                            {"StoryId", "Type", "Status"});
//                }
//
//                totaldenoCount = 0;
//                message = "";
//
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, pageSize);
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//                    for (Issue issue : denoIssue) {
//
//                        if (project.getDatafileRequired().equals("Y")) {
//                            dataLines1.add(new String[]
//                                    {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName()});
//                        }
//                    }
//                }
//
//                if (dataLines1.size() > 0) {
//                    String dataFileName = project.getOutputPath() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLines1, dataFileName);
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
//                message += newLine + twoSpace + "No Num Count considered for this SLA";
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//
//                double actualValue = totaldenoCount;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//
//                if (actualValue <= minsla) {
//                    slaStatus = "Met";
//                } else {
//                    slaStatus = "Not Met";
//                }
//
//                message += newLine + twoSpace + " Status = " + slaStatus;
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(totalnumCount), String.valueOf(totaldenoCount));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//        return null;
//    }
//
//    private ProcessedData PercentageOfTestsAutomated(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Modified and tested with July Data - Simanchal
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=" + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=" + sla.getDenojql();
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//        return null;
//    }
//
//    private ProcessedData AverageCycleTimeForRelease(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        String baseURI = "";
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<Issue> commentIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        List<String[]> dataLinesDetailed = new ArrayList<>();
//
//        //MEthod Specific Variables
//        List<AvgCycleJira> eligibleissuesLst = new ArrayList<>();
//        AvgCycleJira eligibleissue = new AvgCycleJira();
//        List<FixedVersionIssue> fixedVersionData = new ArrayList<>();
//
//        List<FixedVersion> fixedVersions = new ArrayList<>();
//        List<History> Historylist = new ArrayList<>();
//
//        Date committedDate = new Date();//committeddate
//        Date closeedDate = new Date();
//        Date workingDate = new Date();
//        String strWorkingDate = "";
//        String committedField = "";
//        String releasedField = "";
//
//        boolean isValid = false;
//        String inDevelopmentStatus = "";
//        String closeStatus = "";
//        String sourcedateFormat = "";
//        double actualValue = 0;
//        String holidayList = "";
//        String projectdateFormat = "";
//        String limitFromConfig = "";
//        int limit = 0;
//        int variance = 0;
//        List<Date> lstHolidays = new ArrayList<>();
//
//        IssueActivityDate issueActivityDate = new IssueActivityDate();
//        String fromdateFromConfig = "";
//        String todateFromConfig = "";
//        String dateFormatFromConfig = "";
//        Date dtFromDate;
//        Date dtToDate;
//        String strCheckHolidays = "";
//        String strCheckWeekend = "";
//        String strCheckCreatedDateInsteadHistory = "N";
//
//        String strKey = "";
//        String strType = "";
//        String strStatus = "";
//        int percentageVariance = -1;
//        String strPageSize = "";
//        int pageSize = 1000;
//
//        String strtotalFixedVersions = "";
//        String strreleasedFixedVersions = "";
//        List<FixedVersion> matchedFixedVersion = new ArrayList<>();
//        String strFixedVersionDateFormat = "";
//        String strFixedVersionFormat = "";
//        String strFixedVersionSplitter = "";
//        int majorRelease = -1;
//        int updateRelease = -1;
//        int hotfixRelease = -1;
//
//        double baselineAvg = 0;
//        double baselineAvgMajor = 0;
//        double newBaselineAvg = 0;
//        double newBaselineAvgMajor = 0;
//
//        double totalMajorDenoCount = 0;
//        double totalOtherDenoCount = 0;
//        double totalDaysMajor = 0;
//        double totaldays = 0;
//        double addition = 0;
//        double additionMajor = 0;
//        int increaseDay = 0;
//
//        String releaseDateFormat = "";
//        //Method Specific Varriable Declaration Area - End
//
//        try {
//            //Project related Configuration Retrieval -- Start
//            //Project related validation
//            strPageSize = project.getPageSize();
//
//            if (!strPageSize.isEmpty()) {
//                try {
//                    pageSize = Integer.parseInt(strPageSize);
//                } catch (Exception exPageSizeParse) {
//                    pageSize = 1000;
//                }
//            }
//
//            sourcedateFormat = project.getSourceDateFormat();
//            if (sourcedateFormat.equals("")) {
//                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            dateFormatFromConfig = project.getDateFormat().replace("'", "");
//            if (dateFormatFromConfig.equals("")) {
//                message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            //Holidate List Data validation
//            holidayList = project.getHolidays();
//            if (holidayList.equals("")) {
//                message = twoSpace + "Holiday details not found. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            String[] arrHoliday = holidayList.split(",");
//            lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);
//
//
//            if (lstHolidays == null || lstHolidays.size() == 0) {
//                message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            //Project related Configuration Retrieval -- End
//
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Region For Configuration Data Retrival and Data Validation - Start
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
//                }
//
//                if (denourl.equals("")) {
//                    message = twoSpace + "demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
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
//                limitFromConfig = sla.getLimit().replace("'", "");
//
//                if (limitFromConfig.equals("")) {
//                    message = twoSpace + "Percentage Variance data Not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                try {
//                    percentageVariance = Integer.parseInt(limitFromConfig); //added
//                } catch (Exception exbaseline) {
//                }
//
//                if (percentageVariance == -1) {
//                    message = twoSpace + "Not able to parse Percentage Variance value. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                inDevelopmentStatus = sla.getConfig1();
//                if (inDevelopmentStatus.equals("")) {
//                    message = twoSpace + "Status for Development / Issue Committed For SLA not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                strFixedVersionDateFormat = sla.getConfig4();
//                if (strFixedVersionDateFormat.equals("")) {
//                    message = twoSpace + "Fixed version Date Format for the SLA not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                strCheckHolidays = sla.getInput2().replace("'", "");
//                strCheckWeekend = sla.getInput3().replace("'", "");
//
//                strCheckCreatedDateInsteadHistory = sla.getConfig3().replace("'", "");
//
//                if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
//                    strCheckHolidays = "N";
//                }
//
//                if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
//                    strCheckWeekend = "N";
//                }
//
//                if (strCheckCreatedDateInsteadHistory.equals("") || !strCheckCreatedDateInsteadHistory.equals("Y")) {
//                    strCheckCreatedDateInsteadHistory = "N";
//                }
//
//                committedField = sla.getConfig5().replace("'", "");
//                if (committedField.isEmpty()) {
//                    committedField = "Created";
//                }
//
//                releasedField = sla.getConfig2().replace("'", "");
//
//                releaseDateFormat = project.getReleaseDateFormat();
//                if (releaseDateFormat.equals("") && !releasedField.equals("")) {
//                    message = twoSpace + "Release Date format is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String strIncludeCommittedDate = sla.getInput1().replace("'", "");
//                if (!strIncludeCommittedDate.equals("Y")) {
//                    strIncludeCommittedDate = "N";
//                }
//
//                strFixedVersionFormat = sla.getInput4().replace("'", "");
//                if (!strFixedVersionFormat.isEmpty()) {
//                    String[] arrFixedFormat = strFixedVersionFormat.split(",");
//                    if (arrFixedFormat != null && arrFixedFormat.length > 0) {
//                        if (arrFixedFormat.length >= 1) {
//                            if (!arrFixedFormat[0].isEmpty()) {
//                                try {
//                                    majorRelease = Integer.parseInt(arrFixedFormat[0]);
//                                } catch (Exception exmajor) {
//
//                                }
//                            }
//                        }
//
//                        if (arrFixedFormat.length >= 2) {
//                            if (!arrFixedFormat[1].isEmpty()) {
//                                try {
//                                    updateRelease = Integer.parseInt(arrFixedFormat[1]);
//                                } catch (Exception exUpdate) {
//
//                                }
//                            }
//                        }
//
//                        if (arrFixedFormat.length >= 3) {
//                            if (!arrFixedFormat[2].isEmpty()) {
//                                try {
//                                    hotfixRelease = Integer.parseInt(arrFixedFormat[2]);
//                                } catch (Exception exmajor) {
//
//                                }
//                            }
//                        }
//                    }
//                }
//
//                strFixedVersionSplitter = sla.getInput5().replace("'", "");
//
//                //Region Configuration Data Retrival and Data Validation - End
//
//                //Region to Retrieve data  - Start
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, true, pageSize);
//                //Region to Retrieve data  - End
//
//                //Region to Define data file  - Start
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
//                //Region to Define data file  - End
//
//                ///////// SLA Implementation Stats
//
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                List<Issue> fixedVersionIssues = new ArrayList<>();
//                String multipleFixedVersion = "";
//                ////////// Business Logic Implementation Starts  //////////
//
//                if (denoIssue != null & denoIssue.size() > 0) {
//                    for (Issue issue : denoIssue) {
//                        committedDate = null;
//                        closeedDate = null;
//                        issueActivityDate = null;
//                        Historylist = null;
//                        multipleFixedVersion = "";
//
//                        if (issue != null) {
//                            try {
//                                if (issue.getChangelog().getHistories() != null) {
//                                    Historylist = issue.getChangelog().getHistories();
//                                }
//
//                                if (!strCheckCreatedDateInsteadHistory.equals("Y")) {
//                                    //Check it in the Hisotry because u will get some data there
//                                    issueActivityDate = util.getIssueActivityDate(issue.getKey(), Historylist, inDevelopmentStatus, sourcedateFormat, "status");
//
//                                    if (issueActivityDate != null) {
//                                        if (issueActivityDate.getRequestedDate() != null) {
//                                            committedDate = issueActivityDate.getRequestedDate();
//                                        }
//                                    }
//                                } else {
//                                    //Take the created date as committed date as it will be in the initial status
//                                    String strCommittedDate = "";
//                                    if (committedField.toLowerCase().equals("created")) {
//                                        strCommittedDate = issue.getFields().getCreated();
//                                    } else if (committedField.toLowerCase().equals("updated")) {
//                                        strCommittedDate = issue.getFields().getUpdated();
//                                    } else if (committedField.toLowerCase().equals("resolution")) {
//                                        strCommittedDate = issue.getFields().getResolutiondate();
//                                    }
//
//                                    if (!strCommittedDate.isEmpty()) {
//                                        if (util.isDateValid(strCommittedDate, sourcedateFormat) == true) {
//                                            committedDate = util.ConvertToDate(strCommittedDate, sourcedateFormat);
//                                        }
//                                    }
//                                }
//
//                                //Find the release date from Fixed version as discussed
//                                if (issue.getFields().getFixVersions() != null) {
//                                    fixedVersions = issue.getFields().getFixVersions();
//                                }
//
//                                strtotalFixedVersions = "";
//                                for (FixedVersion fv : fixedVersions) {
//                                    strtotalFixedVersions = strtotalFixedVersions + fv.getName() + "#";
//                                }
//
//                                String strClosedDate = "";
//                                closeedDate = null;
//
//                                //Get the Closed date from Fixed version
//                                //Code to get release date using fixed Version -- Start
//                                matchedFixedVersion = new ArrayList<>();
//                                strreleasedFixedVersions = "";
//
//                                List<FixedVersion> releasedFixedVersions = fixedVersions.stream().filter(x -> x.isReleased() == true).collect(Collectors.toList());
//
//                                if (releasedFixedVersions != null && releasedFixedVersions.size() > 0) {
//                                    for (FixedVersion fixedVersion : releasedFixedVersions) {
//                                        if (fixedVersion != null) {
//                                            fixedVersionIssues = new ArrayList<>();
//                                            fixedVersionIssues = null;
//
//                                            FixedVersionIssue fIssue = new FixedVersionIssue();
//                                            fIssue = null;
//                                            if (fixedVersionData != null && fixedVersionData.size() > 0) {
//                                                List<FixedVersionIssue> fixedIssues = fixedVersionData.stream().filter(x -> x.getFixedVersion().getName().equals(fixedVersion.getName())).collect(Collectors.toList());
//                                                if (fixedIssues != null && fixedIssues.size() > 0) {
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
//                                if (matchedFixedVersion != null && matchedFixedVersion.size() > 0) {
//                                    if (matchedFixedVersion.size() > 1) {
//                                        String key = issue.getKey();
//                                        multipleFixedVersion = "";
//                                        String strFDate = "";
//                                        for (FixedVersion f : matchedFixedVersion) {
//                                            if (f.getReleaseDate() != null) {
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
//                                if (!releasedField.isEmpty()) {
//                                    if (releasedField.equals("customfield_14502")) {
//                                        strClosedDate = issue.getFields().getCustomfield_14502();
//                                    }
//
//                                    closeedDate = util.ConvertToDate(strClosedDate, releaseDateFormat);
//                                }
//
//
//
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
//                                if (project.getDetailedLogRequired().equals("Y")) {
//                                    String strCommittedDate = "";
//                                    if (committedDate != null) {
//                                        strCommittedDate = util.ConvertDateToString(committedDate, sourcedateFormat);
//                                    }
//
//                                    if (closeedDate != null) {
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
//                            } catch (Exception issueException) {
//                                String a = "";
//                            }
//                        }
//                    }
//                }
//
//                //We got our eligible issues --> Next processing
//                if (eligibleissuesLst != null && eligibleissuesLst.size() > 0) {
//                    totaldenoCount = eligibleissuesLst.size();
//
//                    totalMajorDenoCount = 0;
//                    totalOtherDenoCount = 0;
//                    totalDaysMajor = 0;
//                    totaldays = 0;
//
//                    if (!strFixedVersionSplitter.isEmpty()) {
//                        if (majorRelease >= 0) {
//                            totalDaysMajor = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0 && o.getFixedVersionType().contains("Major")).mapToDouble(o -> o.getTotalDuration()).sum();
//                            totaldays = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0 && !o.getFixedVersionType().contains("Major")).mapToDouble(o -> o.getTotalDuration()).sum();
//
//                            totalMajorDenoCount = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0 && o.getFixedVersionType().contains("Major")).count();
//                            totalOtherDenoCount = eligibleissuesLst.stream().filter(o -> !o.getFixedVersionType().contains("Major")).count();
//                        } else {
//                            totaldays = eligibleissuesLst.stream().filter(o -> o.getTotalDuration() > 0).mapToDouble(o -> o.getTotalDuration()).sum();
//                            totalOtherDenoCount = eligibleissuesLst.size();
//                        }
//                    } else {
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
//                    if (majorRelease >= 0 && !strFixedVersionSplitter.isEmpty()) {
//                        additionMajor = varianceValue * baselineAvgMajor;
//                    }
//
//
//                    newBaselineAvg = baselineAvg + addition;
//                    if (majorRelease >= 0 && !strFixedVersionSplitter.isEmpty()) {
//                        newBaselineAvgMajor = baselineAvgMajor + additionMajor;
//                    }
//
//                    String recordStatus = "";
//                    for (AvgCycleJira avgcycle : eligibleissuesLst) {
//                        if (majorRelease >= 0 && !strFixedVersionSplitter.isEmpty()) {
//                            if (avgcycle.getFixedVersionType().contains("Major")) {
//                                if (avgcycle.getTotalDuration() <= newBaselineAvgMajor) {
//                                    recordStatus = "Met";
//                                    totalnumCount++;
//                                } else {
//                                    recordStatus = "Not Met";
//                                }
//                            } else {
//                                if (avgcycle.getTotalDuration() <= newBaselineAvg) {
//                                    recordStatus = "Met";
//                                    totalnumCount++;
//                                } else {
//                                    recordStatus = "Not Met";
//                                }
//                            }
//                        } else {
//                            if (avgcycle.getTotalDuration() <= newBaselineAvg) {
//                                recordStatus = "Met";
//                                totalnumCount++;
//                            } else {
//                                recordStatus = "Not Met";
//                            }
//                        }
//
//                        if (project.getDatafileRequired().equals("Y")) {
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
//                    if (project.getDatafileRequired().equals("Y")) {
//                        dataLines.add(new String[]
//                                {"", "", "", "", "", "", "", "", "", ""});
//                        dataLines.add(new String[]
//                                {"", "", "", "", "", "", "", "", "", ""});
//                        dataLines.add(new String[]
//                                {"Variance Value", String.valueOf(varianceValue), "", "", "", "", "", "", "", ""});
//
//                        if (majorRelease >= 0 && !strFixedVersionSplitter.isEmpty()) {
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
//                ////////// Business Logic Implementation Ends  //////////
//
//                ///////Data File Saving - Start
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
//                message += newLine + twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//                ///////// SLA Implementation End
//
//                /////////////////SLA Calculation Starts - Donot Delete the Code here (Generic Code)
//
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
//
//                /////////////////SLA Calculation Starts
//
//                //Create the return object
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//    }
//
//    private ProcessedData ProcessEfficiency(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//
//        //Method Specific Varriable Declaration Area - Start
//
//        //Generic for each method
//        String baseURI = "";
//
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<Issue> commentIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        List<String[]> dataLinesDetailed = new ArrayList<>();
//
//        //MEthod Specific Variables
//        IssueActivityDate issueActivityDate = new IssueActivityDate();
//
//        List<EligibleIssueProcessEfficiency> elligibleIssues = new ArrayList<>();
//        EligibleIssueProcessEfficiency eligibleissue = new EligibleIssueProcessEfficiency();
//        List<History> Historylist = new ArrayList<>();
//
//        Date committedDate = new Date();//committeddate
//        Date closeedDate = new Date();
//        Date workingDate = new Date();
//        String strWorkingDate = "";
//
//        double totDenoCount = 0;
//        double totNumCount = 0;
//
//        boolean isValid = false;
//        String inDevelopmentStatus = "";
//        String closeStatus = "";
//        String prevCloseStatus = "";
//        String additionalClosedStatus = "";
//        String sourcedateFormat = "";
//        double actualValue = 0;
//        String holidayList = "";
//        String projectdateFormat = "";
//        String limitFromConfig = "";
//        int limit = 0;
//        int variance = 0;
//        List<Date> lstHolidays = new ArrayList<>();
//
//        String fromdateFromConfig = "";
//        String todateFromConfig = "";
//        String dateFormatFromConfig = "";
//        Date dtFromDate;
//        Date dtToDate;
//        String strCheckHolidays = "";
//        String strCheckWeekend = "";
//
//        String excludeTypes = "";
//        String strKey = "";
//        String strType = "";
//        String strStatus = "";
//        String strHours = "";
//
//        String strCheckCommittedDateInField = "N";
//        String strCheckClosedDateInField = "N";
//
//        int hours = -1;
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        //Method Specific Varriable Declaration Area - End
//
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Region For Configuration Data Retrival and Data Validation - Start
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
//                //Project related validation
//                sourcedateFormat = project.getSourceDateFormat();
//                if (sourcedateFormat.equals("")) {
//                    message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                dateFormatFromConfig = project.getDateFormat().replace("'", "");
//                if (dateFormatFromConfig.equals("")) {
//                    message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                //Holidate List Data validation
//                holidayList = project.getHolidays();
//                if (holidayList.equals("")) {
//                    message = twoSpace + "Holiday details not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//                String[] arrHoliday = holidayList.split(",");
//                lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);
//
//                //if (lstHolidays == null)
//                if (lstHolidays == null || lstHolidays.size() == 0) {
//                    message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
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
//                //Validation for From and to days
//                fromdateFromConfig = sla.getFrom().replace("'", "");
//                todateFromConfig = sla.getTo().replace("'", "");
//                limitFromConfig = sla.getLimit().replace("'", "");
//                strCheckHolidays = sla.getInput2().replace("'", "");
//                strCheckWeekend = sla.getInput3().replace("'", "");
//                strHours = sla.getInput4().replace("'", "");
//
//                if (!fromdateFromConfig.equals("")) {
//                    if (util.isDateValid(fromdateFromConfig, dateFormatFromConfig) == false) {
//                        message = twoSpace + "From Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
//                        status = util.WriteToFile(project.getLogFile(), message);
//                        return null;
//                    }
//                } else {
//                    message = twoSpace + "From Date is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (!todateFromConfig.equals("")) {
//                    if (util.isDateValid(todateFromConfig, dateFormatFromConfig) == false) {
//                        message = twoSpace + "To Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
//                        status = util.WriteToFile(project.getLogFile(), message);
//                        return null;
//                    }
//                } else {
//                    message = twoSpace + "To Date is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                dtFromDate = util.ConvertToDate(fromdateFromConfig, dateFormatFromConfig);
//                dtToDate = util.ConvertToDate(todateFromConfig, dateFormatFromConfig);
//
//                if (dtFromDate == null || dtToDate == null) {
//                    message = twoSpace + "From and To date is not valid, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (limitFromConfig.equals("")) {
//                    message = twoSpace + "Limit data Not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                List<String> lstExcludeTypes = new ArrayList<>();
//                excludeTypes = sla.getConfig1();
//                if (!excludeTypes.isEmpty()) {
//                    String[] arr = excludeTypes.split(",");
//                    for (String type : arr) {
//                        lstExcludeTypes.add(type);
//                    }
//                }
//
//                limit = -1;
//                try {
//                    limit = Integer.parseInt(limitFromConfig); //added
//                } catch (Exception exbaseline) {
//                }
//
//                if (limit == -1) {
//                    message = twoSpace + "Not able to parse limit value. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (!strHours.isEmpty()) {
//                    hours = -1;
//                    try {
//                        hours = Integer.parseInt(strHours); //added
//                    } catch (Exception exbaseline) {
//                    }
//
//                    if (hours == -1) {
//                        hours = 8;
//                    }
//                } else {
//                    hours = 8;
//                }
//
//                inDevelopmentStatus = sla.getConfig2();
//                if (inDevelopmentStatus.equals("")) {
//                    message = twoSpace + "Status for Development / Issue Committed to Sprint not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String close = sla.getConfig3();
//                if (close.equals("")) {
//                    message = twoSpace + "Status for close for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String[] arrClosedStatus = close.split(",");
//                if (arrClosedStatus != null && arrClosedStatus.length > 2) {
//                    message = twoSpace + "Only Two closed status cna be mentioned for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (arrClosedStatus != null && arrClosedStatus.length >= 1) {
//                    closeStatus = arrClosedStatus[0];
//                }
//
//                if (arrClosedStatus != null && arrClosedStatus.length >= 2) {
//                    prevCloseStatus = arrClosedStatus[1];
//                }
//
//                if (closeStatus.isEmpty()) {
//                    message = twoSpace + "Closed Status for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                strCheckCommittedDateInField = sla.getConfig4().replace("'", "");
//                strCheckClosedDateInField = sla.getConfig5().replace("'", "");
//
//                if (strCheckCommittedDateInField.isEmpty()) {
//                    strCheckCommittedDateInField = "N";
//                }
//
//                if (strCheckClosedDateInField.isEmpty()) {
//                    strCheckClosedDateInField = "N";
//                }
//
//                if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
//                    strCheckHolidays = "N";
//                }
//
//                if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
//                    strCheckWeekend = "N";
//                }
//
//                double baselineavg = -1;
//                String strbaselineavg = sla.getInput1();
//                if (strbaselineavg.equals("")) {
//                    message = twoSpace + "Baseline average not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                try {
//                    baselineavg = Double.parseDouble(strbaselineavg);
//                } catch (Exception exbaseline) {
//                }
//
//                if (baselineavg == -1) {
//                    message = twoSpace + "Not able to parse Baseline average. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String strIncludeCommittedDate = sla.getInput1().replace("'", "");
//                if (!strIncludeCommittedDate.equals("Y")) {
//                    strIncludeCommittedDate = "N";
//                }
//
//                //Region Configuration Data Retrival and Data Validation - End
//
//                //Region to Define data file  - Start
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Issue Status", "commitedDate", "closedDate",
//                                    "Variance (In Days)", "actual Hour", "Total Hours"});
//                }
//
//                if (project.getDetailedLogRequired().equals("Y")) {
//                    dataLinesDetailed.add(new String[]
//                            {"Key", "Type", "Issue Status", "commitedDate", "closedDate"});
//                }
//
//                //Region to Define data file  - End
//                ////////// Business Logic Implementation Starts  //////////
//
//                //Check if User Send Deno JQL then Use that to retrieve the data otherwise use the data retrieved for the board.
//                denourl = "";
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
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
////                                if (issue.getKey().equals("ERPMM-7913"))
////                                {
////                                    String mm = "";
////                                }
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
//                                        String strCommittedDate = "";
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
//                                    if (workingDate != null) {
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
//                                double actualHours = -1;
//                                //Elligible condition - if the created date is between measurement period and has a valid closed date
//                                if (committedDate != null && closeedDate != null) {
//                                    eligibleissue = new EligibleIssueProcessEfficiency();
//                                    eligibleissue.setKey(issue.getKey());
//                                    eligibleissue.setType(issue.getFields().getIssuetype().getName());
//                                    eligibleissue.setIssueStatus(issue.getFields().getStatus().getName());
//                                    eligibleissue.setDelivereddate(util.ConvertDateToString(closeedDate, sourcedateFormat));
//                                    eligibleissue.setCommitteddate(util.ConvertDateToString(committedDate, sourcedateFormat));
//
//                                    //Get the Actual Hour from the field customfield_14001
//                                    //validate this data. If you donot foound actual hour then reject
//                                    if (issue.getFields().getCustomfield_14000() != null && !(issue.getFields().getCustomfield_14000().equals(""))) {
//                                        try {
//                                            actualHours = Float.parseFloat(issue.getFields().getCustomfield_14001());
//                                        } catch (Exception exactualHour) {
//
//                                        }
//                                    }
//
//                                    //no actual hours found, so not elligible
//                                    if (actualHours != -1) {
//                                        //Find the total hours to deliver the issue
//                                        // Get the total work day. Calculation will be done based on the configuration condition for HolidayCheck and Weekend check
//                                        processingDays = 0;
//                                        processingDays = util.GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend, strIncludeCommittedDate);
//                                        double totalHourstoDeliver = 0;
//                                        if (processingDays > 0) {
//                                            totalHourstoDeliver = processingDays * hours; //Hours can be from the config
//                                            eligibleissue.setTotalDays(processingDays);
//                                        } else {
//                                            eligibleissue.setTotalDays(0);
//                                        }
//
//                                        eligibleissue.setActualhourspent(actualHours);
//                                        eligibleissue.setTotalhourstodeliver(totalHourstoDeliver);
//                                        elligibleIssues.add(eligibleissue);
//                                    }
//
//                                    if (project.getDatafileRequired().equals("Y")) {
//                                        dataLines.add(new String[]
//                                                {eligibleissue.getKey(), eligibleissue.getType(), eligibleissue.getIssueStatus(), eligibleissue.getCommitteddate(), eligibleissue.getDelivereddate(),
//                                                        String.valueOf(eligibleissue.getTotalDays()), String.valueOf(eligibleissue.getActualhourspent()), String.valueOf(eligibleissue.getTotalhourstodeliver())});
//
//                                    }
//                                }
//                            } catch (Exception exIssueError) {
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
//                                String strCommittedDate = util.ConvertDateToString(workingDate, sourcedateFormat);
//                                String strClosedDate = util.ConvertDateToString(testDate, sourcedateFormat);
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
//                    double totIssuesEligible = elligibleIssues.size();
//                    totDenoCount = (denoCount / totIssuesEligible);
//                    totNumCount = (numcount / totIssuesEligible);
//                }
//                ////////// Business Logic Implementation Ends  //////////
//
//                message += newLine + twoSpace + " Average of Total Time Taken to deliver = " + totDenoCount;
//                message += newLine + twoSpace + " Average of Actual time spent = " + totNumCount;
//
//                ///////Data File Saving - Start
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
//                ///////Data File Saving - End
//
//                ///////// SLA Implementation End
//
//                /////////////////SLA Calculation Starts - Donot Delete the Code here (Generic Code)
//                // If totalDenoCount = 0 OR Total Numerator Count = 0 this means
//                // there is no data for the period, so SLA should pass
//                // Make the Total Demo Count = 1 and Total Numerator Count = 1 so that
//                // We can get the actual = 100%
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
//                double newBaseLineAvg = baselineavg - limit;
//                message += newLine + twoSpace + " Baseline Average = " + String.valueOf(baselineavg);
//                message += newLine + twoSpace + " Process Variance = " + String.valueOf(limit);
//                message += newLine + twoSpace + " New Baseline Average = " + String.valueOf(newBaseLineAvg);
//                actualValue = util.GetActualValueV1(totDenoCount, totNumCount);
//                message += newLine + twoSpace + " Process Efficiency = " + String.valueOf(actualValue);
//
//                if (actualValue >= newBaseLineAvg) {
//                    slaStatus = "MET";
//                } else {
//                    slaStatus = "Not Met";
//                }
//
//                message += newLine + twoSpace + " Status = " + slaStatus;
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"BaseLine avg", "", String.valueOf(baselineavg), "", "", "", ""});
//                    dataLines.add(new String[]
//                            {"Process Variance", "", String.valueOf(limit), "", "", "", ""});
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
//                /////////////////SLA Calculation Starts
//
//                //Create the return object
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//    }
//
//    private ProcessedData PremierCustomerSatisficationSurvey(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Modified and tested with July Data - Simanchal
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        List<String[]> dataLines2 = new ArrayList<>();
//
//        try {
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//            throw ex;
//        }
//
//    }
//
//    private ProcessedData EstimationQuality(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Variable Declaration
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        List<String[]> dataLinesDetails = new ArrayList<>();
//        float originalEstimation = 0f;//Get it from the field
//        float actualEstimation = 0f;//Get it from the field
//        float variance = 0f;
//        double finalLimitValue = 0.0;
//        double minusfinalLimitValue = 0.0;
//        double actualValue = 0;
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        try {
//            strPageSize = project.getPageSize();
//
//            if (!strPageSize.isEmpty()) {
//                try {
//                    pageSize = Integer.parseInt(strPageSize);
//                } catch (Exception exPageSizeParse) {
//                    pageSize = 1000;
//                }
//            }
//
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Configuration retrieve and Validate - Start
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
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
//                if (numurl.isEmpty() && denourl.isEmpty()) {
//                    message = twoSpace + "numJQL and demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                boolean eligibletoWrite = false;
//                int totalNotestimated = 0;
//
//                float limitValue = 0.f;
//                if (sla.getLimit() != null) {
//                    limitValue = Float.parseFloat(sla.getLimit());
//                } else {
//                    limitValue = Float.parseFloat("15");
//                }
//
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, pageSize);
//
//                //Configuration retrieve and Validate - End
//
//                //Data File Definition - Start
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"StoryID", "Original Estimation", "Actual Estimation"});
//                }
//
//                if (project.getDetailedLogRequired().equals("Y")) {
//                    dataLinesDetails.add(new String[]
//                            {"StoryID", "Original Estimation", "Actual Estimation", "Variance", "Status"});
//                }
//
//                //Data File Definition - End
//
//
//                //Business Logic - Start
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                message = "";
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    totaldenoCount = denoIssue.size();
//                    for (Issue issue : denoIssue) {
//                        eligibletoWrite = false;
//                        originalEstimation = 0f;//Get it from the field
//                        actualEstimation = 0f;//Get it from the field
//                        variance = 0f;
//                        finalLimitValue = 0.0;
//                        minusfinalLimitValue = 0.0;
//                        String issueStatus = "";
//
//                        if (issue != null) {
//                            if (issue.getFields().getCustomfield_14000() != null && !(issue.getFields().getCustomfield_14000().equals(""))) {
//                                try {
//                                    originalEstimation = Float.parseFloat(issue.getFields().getCustomfield_14000());
//                                } catch (Exception exOriginalEstimation) {
//                                }
//                            }
//
//                            if (issue.getFields().getCustomfield_14001() != null && !(issue.getFields().getCustomfield_14001().equals(""))) {
//                                try {
//                                    actualEstimation = Float.parseFloat(issue.getFields().getCustomfield_14001());
//                                } catch (Exception exActualEstimaiton) {
//                                }
//                            }
//
//                            //if original estimation  =0 and actual estimation has a value then
//                            //there are some stories where there is no original estimaiton but have actual estimation
//                            //then make the original estimation = actual estimation
//                            String mm = issue.getKey();
//                            if (originalEstimation == 0 && actualEstimation > 0) {
//                                originalEstimation = actualEstimation;
//                            }
//
//                            if (originalEstimation > 0 && actualEstimation > 0) {
//                                //totaldenoCount ++;
//                                variance = actualEstimation - originalEstimation;
//                                finalLimitValue = (limitValue / 100) * originalEstimation;
//                                minusfinalLimitValue = -(finalLimitValue);
//
//                                if ((variance >= (minusfinalLimitValue)) == true) {
//                                    if ((variance <= (finalLimitValue)) == true) {
//                                        status = true;
//                                        issueStatus = "Met";
//                                        totalnumCount++;
//                                    } else {
//                                        //Not Met
//                                        status = false;
//                                        issueStatus = "Not Met";
//                                    }
//                                } else {
//                                    //Not Met
//                                    status = false;
//                                    issueStatus = "Not Met";
//                                }
//
//                                eligibletoWrite = true;
//                            }
//
//                            //Write the data Line
//                            if (project.getDatafileRequired().equals("Y")) {
//                                //This should be your deno count
//
//                                if (eligibletoWrite == true) {
//                                    dataLines.add(new String[]
//                                            {issue.getKey(), String.valueOf(originalEstimation), String.valueOf(actualEstimation), String.valueOf(variance), String.valueOf(issueStatus)});
//                                }
//                            }
//
//                            if (project.getDetailedLogRequired().equals("Y")) {
//                                dataLinesDetails.add(new String[]
//                                        {issue.getKey(), String.valueOf(originalEstimation), String.valueOf(actualEstimation)});
//                            }
//
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
//                if (dataLinesDetails.size() > 0) {
//                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
//                    try {
//                        boolean csvStatus = util.WriteToCSv(dataLinesDetails, dataFileName1);
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
//                //Business Logic - End
//
//                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                message = twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
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
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double) expectedsla, (double) minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//            throw ex;
//        }
//    }
//
//    private ProcessedData DelayInReadyForProductionRelease(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//
//        //Method Specific Varriable Declaration Area - Start
//
//        //Generic for each method
//        String baseURI = "";
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<Issue> commentIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        boolean isValid = false;
//
//        //MEthod Specific Variables
//        List<String> fixVersions = new ArrayList<>();
//        List<CR> totalCrs = new ArrayList<>();
//        List<FixedVersion> issueFixedVersions = new ArrayList<>();
//        CR cr = new CR();
//        String crsAssociated = "";
//        String fixedVersionAssociated = "";
//        boolean isDelayed = false;
//        boolean isEligible = false;
//        int totalCrAssociated = 0;
//        String Comments = "";
//        int pageSize = 1000;
//        String strPageSize = "";
//        //Method Specific Varriable Declaration Area - End
//
//        strPageSize = project.getPageSize();
//
//        if (!strPageSize.isEmpty()) {
//            try {
//                pageSize = Integer.parseInt(strPageSize);
//            } catch (Exception exPageSizeParse) {
//                pageSize = 1000;
//            }
//        }
//
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Region For Configuration Data Retrival and Data Validation - Start
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
//                }
//
//                //This is mandatory to run the program. Validate
//                if (denourl.equals("")) {
//                    message = twoSpace + "demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
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
//                String[] input1 = (sla.getInput1().split(",")); //No validation required as it there is no CR created, the team will provide any CR
//                for (String a : input1) {
//                    String[] crVal = a.split("#");
//                    cr = new CR();
//                    cr.setCrNumber(crVal[0]);
//                    List<String> crFixVersions = List.of(crVal[1].split("~"));
//                    cr.setFixedversions(crFixVersions);
//                    cr.setDelayed(false);
//                    totalCrs.add(cr);
//                }
//
//                String[] statustoCheck = (sla.getConfig1().split("#")); //This is mandatory so validate it
//                if (statustoCheck != null && statustoCheck.length == 0) {
//                    //Stop the processing
//                    message = twoSpace + "Please provide status to be checked for the Issue in config1 in the SLA configuration";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                //Region Configuration Data Retrival and Data Validation - End
//
//
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, pageSize);
//
//
//                //Region Data file Definition - Start
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Issue Key", "Type", "Status", "FixedVersions", "RelatedCR", "Eligible", "Comments"});
//                }
//                //Region Data file Definition - End
//
//                ///////// SLA Implementation Stats
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//
//                totalNumCountNotSatisfied = 0;
//                totalNumCountSatisfied = 0;
//
//                /////////// Businesslogic Implementation - Start
//
//                //Deno Count = Total no of CRS we received from the users
//                totaldenoCount = totalCrs.size();
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    for (Issue issue : denoIssue) {
//                        if (issue != null) {
//                            isValid = true;
//
//
//
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
//
//
//                totalnumCount = (int) totalCrs.stream().filter(one -> one.isDelayed() == true).count();
//                message += twoSpace + " Total Denominator Count = " + totaldenoCount;
//                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
//
//                ///////// SLA Implementation End
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
//                ///////Data File Saving - End
//
//                /////////////////SLA Calculation Starts - Donot Delete the Code here (Generic Code)
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//    }
//
//    private ProcessedData ReopenedDefectsBeforeProductionRelease(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//
//        //Method Specific Varriable Declaration Area - Start
//
//        //Generic for each method
//        String baseURI = "";
//        List<Issue> denoIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        List<String[]> dataLinesDetail = new ArrayList<>();
//
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        String developmentStatus = "";
//        String defectIssueType = "";
//        List<Issue> processingIssues = new ArrayList<>();
//
//        List<KeyValue> issueDefects = new ArrayList<>();
//        String issueURI = "";
//        String defectKey = "";
//        Issue defectIssue = new Issue();
//        List<Issue> issues = new ArrayList<>();
//
//        List<History> historyList = new ArrayList<>();
//        Issue thisIssue = new Issue();
//
//        //MEthod Specific Variables
//
//        //Method Specific Varriable Declaration Area - End
//
//        try {
//
//            strPageSize = project.getPageSize();
//
//            if (!strPageSize.isEmpty()) {
//                try {
//                    pageSize = Integer.parseInt(strPageSize);
//                } catch (Exception exPageSizeParse) {
//                    pageSize = 1000;
//                }
//            }
//
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Region For Configuration Data Retrival and Data Validation - Start
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
//                }
//
//                //This is mandatory to run the program. Validate
//                if (denourl.equals("")) {
//                    message = twoSpace + "demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
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
//                //Region Configuration Data Retrival and Data Validation - End
//
//                //Region to Retrieve data  - Start
//                if (denourl != "") {
//                    baseURI = project.getProjecturl() + "/api/latest/search?";
//
//                    String commentURI = project.getProjecturl() + "/agile/1.0/issue";
//                    denoIssue = iJiraDataService.getAllIssuesOnJQLV1(userName, password, denourl, baseURI, commentURI, true, true);
//                } else {
//                    //Either process it in a different way or stop the processing by sending a messae
//                    //Currently we are stopping the processing.
//                    message = twoSpace + "numJQL and demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                developmentStatus = sla.getConfig1().replace("'", "");
//                defectIssueType = sla.getConfig3().replace("'", "");
//
//                if (developmentStatus.isEmpty()) {
//                    message = twoSpace + "Development Status is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (defectIssueType.isEmpty()) {
//                    defectIssueType = "Defect";
//                }
//
//                //Region to Define data file  - Start
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"StoryId", "Type", "Status", "No of time reopened"});
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
//                                        {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getIssuetype().getName(), ""});
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
//                                                    if (thisIssue.getChangelog() != null) {
//                                                        if (thisIssue.getChangelog().getHistories() != null) {
//                                                            historyList = thisIssue.getChangelog().getHistories();
//                                                            IssueActivityDate issueActivityDate = util.getIssueActivityDate(thisIssue.getKey(), historyList, developmentStatus, project.getSourceDateFormat(), "status");
//                                                            if (issueActivityDate != null) {
//                                                                issueDefect.setValue(String.valueOf(issueActivityDate.getOccurance() - 1));
//                                                            }
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
//                                                        {"      " + issueDefect.getKey(), issueDefect.getType(), issueDefect.getStatus(), issueDefect.getValue()});
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
//                ///////Data File Saving - End
//
//                ///////// SLA Implementation End
//
//                /////////////////SLA Calculation Starts - Donot Delete the Code here (Generic Code)
//
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
//                //slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double)expectedsla, (double)minsla);
//                if (actualValue <= expectedsla) {
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//
//
//    }
//
//    /////////////////OLD FUnctions/////////////////
//
//    ////////Generic Function - Start /////////
//    private ProcessedData JiraProcessWithJQLCountGeneric(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        try {
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
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//        return null;
//    }
//    ////////Generic Function - End //////////
//
//    //Non-backlog SLA Calculation - Start
//    //This function covers P1, P2 and P3 incidents with different config value
//    private ProcessedData SeverityLvl1IncidentResolution(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//
//        /*
//        //Testing code for ADO
//        String ADOURLFromConfig = "https://dev.azure.com/premierinc/_apis/wit/";
//        String ADOURL = ADOURLFromConfig + "wiql?api-version=6.0";
//        String ADOQueryFromConfig = "Select * From WorkItems  Where [System.WorkItemType] = 'Incident' And [System.TeamProject] = 'Code' And [Microsoft.VSTS.Common.Severity]= '1 - Critical' And [System.CreatedDate] >= @StartOfMonth-30";
//        //String ADOQueryFromConfig = "Select * From WorkItems  Where [System.TeamProject] = 'Code'";
//        String ADQuery = "{\n" +
//                "  \"query\": \"" + ADOQueryFromConfig + "\"\n" + "}";
//
//        password = "ipafbsxh7bhiqscm3mbryaniod4qcpes6hcuh7bl2z5pberxkblq";
//        userName = "spattan3";
//        List<WorkItem> getWorkitems = iAdoDataService.getWorkitems(userName, password, ADOURL, ADQuery,"POST", true, true, 1000);
//         */
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//
//        //Method Specific Varriable Declaration Area - Start
//
//        //Generic for each method
//        String baseURI = "";
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<Issue> commentIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        List<String[]> dataLinesDetailed = new ArrayList<>();
//
//        //MEthod Specific Variables
//        IssueActivityDate issueActivityDate = new IssueActivityDate();
//
//        List<IssueDateVariance> elligibleIssues = new ArrayList<>();
//        IssueDateVariance issueDateVariance = new IssueDateVariance();
//        List<History> Historylist = new ArrayList<>();
//
//        Date committedDate = new Date();
//        Date closeedDate = new Date();
//        Date workingDate = new Date();
//        String strWorkingDate = "";
//
//        boolean isValid = false;
//        String inDevelopmentStatus = "";
//        String closeStatus = "";
//        String sourcedateFormat = "";
//        double actualValue = 0;
//        String holidayList = "";
//        String projectdateFormat = "";
//        String limitFromConfig = "";
//        int limit = 0;
//        List<Date> lstHolidays = new ArrayList<>();
//
//        String fromdateFromConfig = "";
//        String todateFromConfig = "";
//        String dateFormatFromConfig = "";
//        Date dtFromDate;
//        Date dtToDate;
//        String strCheckHolidays = "";
//        String strCheckWeekend = "";
//        String strCheckCreatedDateInsteadHistory = "N";
//        String strCommittedDate = "";
//        String strClosedDate = "";
//        Date testDate = new Date();
//        String strKey = "";
//        String strType = "";
//        String strStatus = "";
//
//
//        //Method Specific Varriable Declaration Area - End
//
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //Region For Configuration Data Retrival and Data Validation - Start
//
//                //Project related validation
//                sourcedateFormat = project.getSourceDateFormat();
//                if (sourcedateFormat.equals("")) {
//                    message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                dateFormatFromConfig = project.getDateFormat().replace("'", "");
//                if (dateFormatFromConfig.equals("")) {
//                    message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                //Holidate List Data validation
//                holidayList = project.getHolidays();
//                if (holidayList.equals("")) {
//                    message = twoSpace + "Holiday details not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//                String[] arrHoliday = holidayList.split(",");
//                lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);
//
//                if (lstHolidays == null) {
//                    message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                //SLA Related Retrieval and validation
//                if (sla.getNumjql() != null && sla.getNumjql().isEmpty() == false) {
//                    numurl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getNumjql();
//                }
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=project = " + project.getProjectKey() + " AND " + sla.getDenojql();
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
//                //Validation for From and to days
//                fromdateFromConfig = sla.getFrom().replace("'", "");
//                todateFromConfig = sla.getTo().replace("'", "");
//                limitFromConfig = sla.getLimit().replace("'", "");
//                strCheckHolidays = sla.getInput2().replace("'", "");
//                strCheckWeekend = sla.getInput3().replace("'", "");
//                strCheckCreatedDateInsteadHistory = sla.getConfig3().replace("'", "");
//
//                if (!fromdateFromConfig.equals("")) {
//                    if (util.isDateValid(fromdateFromConfig, dateFormatFromConfig) == false) {
//                        message = twoSpace + "From Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
//                        status = util.WriteToFile(project.getLogFile(), message);
//                        return null;
//                    }
//                } else {
//                    message = twoSpace + "From Date is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (!todateFromConfig.equals("")) {
//                    if (util.isDateValid(todateFromConfig, dateFormatFromConfig) == false) {
//                        message = twoSpace + "To Date is not in valid date format :" + dateFormatFromConfig + ", please check your configuration. Stopping SLA calculation";
//                        status = util.WriteToFile(project.getLogFile(), message);
//                        return null;
//                    }
//                } else {
//                    message = twoSpace + "To Date is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                dtFromDate = util.ConvertToDate(fromdateFromConfig, dateFormatFromConfig);
//                dtToDate = util.ConvertToDate(todateFromConfig, dateFormatFromConfig);
//
//                if (dtFromDate == null || dtToDate == null) {
//                    message = twoSpace + "From and To date is not valid, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (limitFromConfig.equals("")) {
//                    message = twoSpace + "Limit data Not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                limit = Integer.parseInt(limitFromConfig);
//
//                inDevelopmentStatus = sla.getConfig1();
//                if (inDevelopmentStatus.equals("")) {
//                    message = twoSpace + "Status for Development / Issue Committed to Sprint not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                closeStatus = sla.getConfig2();
//                if (closeStatus.equals("")) {
//                    message = twoSpace + "Status for close for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
//                    strCheckHolidays = "N";
//                }
//
//                if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
//                    strCheckWeekend = "N";
//                }
//
//                if (strCheckCreatedDateInsteadHistory.equals("") || !strCheckCreatedDateInsteadHistory.equals("Y")) {
//                    strCheckCreatedDateInsteadHistory = "N";
//                }
//
//                //Region Configuration Data Retrival and Data Validation - End
//
//                //Region to Retrieve data  - Start
//
//                if (denourl.isEmpty()) {
//                    message = twoSpace + "deno JQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, true, 1000);
//
//                //Region to Retrieve data  - End
//
//                //Region to Define data file  - Start
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Final Issue Status", "Priority", "commitedDate", "closedDate",
//                                    "Variance (In Days)", "Eligible"});
//                }
//
//                if (project.getDetailedLogRequired().equals("Y")) {
//                    dataLinesDetailed.add(new String[]
//                            {"Key", "Type", "Final Issue Status", "Priority", "commitedDate", "closedDate", "Eligible"});
//                }
//                //Region to Define data file  - End
//
//
//                ////////// Business Logic Implementation Starts  //////////
//
//                message = "";
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                ////////// Business Logic Implementation Starts  //////////
//
//                if (denoIssue != null && denoIssue.size() > 0) {
//                    for (Issue issue : denoIssue) {
//                        committedDate = null;
//                        closeedDate = null;
//                        issueActivityDate = null;
//                        workingDate = null;
//
//                        if (issue != null) {
//                            if (issue.getChangelog() != null) {
//                                if (issue.getChangelog().getHistories() != null) {
//                                    Historylist = issue.getChangelog().getHistories();
//                                }
//                            }
//
//                            //If Config3 value = Y, This means we need to take the createddate of the Issue as
//                            //Committed date because the project team is taking when the issue is creatted instead of when they start working on the same
//                            //We cannot browse the history and get the data because there will not be any changelog added by jira because
//                            //the story is in the initial state of the workflow.
//
//                            if (!strCheckCreatedDateInsteadHistory.equals("Y")) {
//                                //Check it in the Hisotry because u will get some data there
//                                issueActivityDate = util.getIssueActivityDate(issue.getKey(), Historylist, inDevelopmentStatus, sourcedateFormat, "status");
//
//                                if (issueActivityDate != null) {
//                                    if (issueActivityDate.getRequestedDate() != null) {
//                                        //You have a date here.
//                                        workingDate = issueActivityDate.getRequestedDate();
//                                    }
//                                }
//                            } else {
//                                //Take the created date as committed date as it will be in the initial status
//                                strCommittedDate = issue.getFields().getCreated();
//                                if (!strCommittedDate.isEmpty()) {
//                                    if (util.isDateValid(strCommittedDate, sourcedateFormat) == true) {
//                                        workingDate = util.ConvertToDate(strCommittedDate, sourcedateFormat);
//                                    }
//                                }
//                            }
//
//                            //Validate date - Should be between from and to
//                            if (workingDate != null) {
//                                //workingDate = util.ConvertDateFromOneFormatToAnother(workingDate, sourcedateFormat, dateFormatFromConfig);
//                                //Check if the start date is withing the measurement period
//                                if ((workingDate.compareTo(dtFromDate) >= 0 && workingDate.compareTo(dtToDate) <= 0) == true) {
//                                    //The issue has a work starting date which is withing the measurement period
//                                    committedDate = workingDate; //This will be in the same format as per source
//                                }
//                            }
//
//                            //If the date is between the measurement period then find the closed date
//                            if (committedDate != null) {
//                                issueActivityDate = null;
//                                issueActivityDate = util.getIssueActivityDate(issue.getKey(), Historylist, closeStatus, sourcedateFormat, "status");
//
//                                if (issueActivityDate != null) {
//                                    closeedDate = issueActivityDate.getRequestedDate();
//                                }
//                            }
//
//                            String recordStatus = "Not Met";
//                            //Elligible condition - if the created date is between measurement period and has a valid closed date
//                            if (committedDate != null && closeedDate != null) {
//                                issueDateVariance = new IssueDateVariance();
//                                issueDateVariance.setKey(issue.getKey());
//                                issueDateVariance.setIssueStatus(issue.getFields().getStatus().getName());
//                                issueDateVariance.setType(issue.getFields().getIssuetype().getName());
//                                issueDateVariance.setPriority(issue.getFields().getPriority().getName());
//                                issueDateVariance.setCommitedDate(committedDate);
//                                issueDateVariance.setClosedDate(closeedDate);
//
//                                //Get the Variance
//                                long variance = util.GetDayVariance(committedDate, closeedDate, lstHolidays, strCheckHolidays, strCheckWeekend);
//                                issueDateVariance.setVariance(Double.parseDouble(String.valueOf(variance)));
//
//                                if (variance <= limit) {
//                                    issueDateVariance.setStatus("Met");
//                                    recordStatus = "Met";
//                                } else {
//                                    issueDateVariance.setStatus("Not Met");
//                                }
//
//                                elligibleIssues.add(issueDateVariance);
//                            }
//
//                            if (project.getDetailedLogRequired().equals("Y")) {
//                                //Get the CLosed date as may be earlier it might not have procesed due to not falling in measurement peiod date range
//                                testDate = null;
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
//                                        {issue.getKey(), issue.getFields().getIssuetype().getName(), issue.getFields().getStatus().getName(), issue.getFields().getPriority().getName(), strCommittedDate, strClosedDate, recordStatus});
//                            }
//                        }
//                    }
//                }
//
//                totaldenoCount = 0;
//                totalnumCount = 0;
//
//                if (elligibleIssues != null && elligibleIssues.size() > 0) {
//                    if (project.getDatafileRequired().equals("Y")) {
//                        for (IssueDateVariance elIssue : elligibleIssues) {
//                            strCommittedDate = util.ConvertDateToString(elIssue.getCommitedDate(), sourcedateFormat);
//                            strClosedDate = util.ConvertDateToString(elIssue.getClosedDate(), sourcedateFormat);
//
//                            dataLines.add(new String[]
//                                    {elIssue.getKey(), elIssue.getType(), elIssue.getIssueStatus(), elIssue.getPriority(), strCommittedDate, strClosedDate,
//                                            String.valueOf(elIssue.getVariance()), elIssue.getStatus()});
//
//                        }
//                    }
//
//                    totaldenoCount = elligibleIssues.size();
//                    totalnumCount = (int) elligibleIssues.stream().filter(x -> x.getStatus().equals("Met")).count();
//                }
//                ////////// Business Logic Implementation Ends  //////////
//
//                //totalnumCount = totalNumCountSatisfied;
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
//                ///////Data File Saving - End
//
//                ///////// SLA Implementation End
//
//                /////////////////SLA Calculation Starts - Donot Delete the Code here (Generic Code)
//                // If totalDenoCount = 0 AND Total Numerator Count = 0 this means
//                // there is no data for the period, so SLA should pass
//                // Make the Total Demo Count = 1 and Total Numerator Count = 1 so that
//                // We can get the actual = 100%
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
//                message += newLine + twoSpace + " Minimum SLA = " + minsla;
//                message += newLine + twoSpace + " Expected SLA = " + expectedsla;
//                message += newLine + twoSpace + " Actual = " + String.valueOf(actualValue);
//                slaStatus = util.CalculateFinalSLAValueV1(actualValue, expectedsla, minsla);
//                message += newLine + twoSpace + " Status = " + slaStatus;
//                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual));
//                status = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//    }
//
//    private ProcessedData SeverityLvl2IncidentResolution(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                message = twoSpace + "Not Implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }
//
//    private ProcessedData SeverityLvl3IncidentResolution(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                message = twoSpace + "Not Implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }
//
//    private ProcessedData PercentOfIncidentOpened(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Done and tested by Simanchal
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        IssueDateVariance issueDateVariance = new IssueDateVariance();
//        List<History> Historylist = new ArrayList<>();
//        List<Item> ItemList = new ArrayList<>();
//        Date committedDate = new Date();//committeddate
//        Date reDate = new Date();
//        double totDenoCount = 0;
//        double totNumCount = 0;
//        double actualValue = 0;
//        String inDevelopmentStatus = "";
//        String sourcedateFormat = "";
//        reDate = null;
//        String holidayList = "";
//        String projectdateFormat = "";
//        int limitFromConfig = 0;
//        List<Date> lstHolidays = new ArrayList<>();
//        String fromdateFromConfig = "";
//        String todateFromConfig = "";
//        String dateFormatFromConfig = "";
//        String releasedateformat = "";
//        Date dtFromDate = null;
//        Date dtToDate = null;
//        String strCheckHolidays = "";
//        String strCheckWeekend = "";
//        totalNumCountSatisfied = 0;
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        boolean isconditionsatisfied = false;
//        String closeddate = "";
//        String reopendate = "";
//        List<IssueDateVariance> eligibleissue = new ArrayList<>();
//
//        Date closedIssueDate = new Date();
//        Date reOpenIssueDate = new Date();
//        String strClosedDate = "";
//        String strReopenDate = "";
//        String strCloseStatusOnIssue = "";
//        String strReopenStatusOnIssue = "";
//        long varianceDate = 0;
//
//        History closedHistory = new History();
//        History reopenHistory = new History();
//
//        try {
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
//                releasedateformat = project.getReleaseDateFormat();
//
//                //Holidate List Data validation
//                holidayList = project.getHolidays();
//                if (holidayList.equals("")) {
//                    message = twoSpace + "Holiday details not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//                String[] arrHoliday = holidayList.split(",");
//                lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);
//
//                //if (lstHolidays == null)
//                if (lstHolidays == null || lstHolidays.size() == 0) {
//                    message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                limitFromConfig = Integer.parseInt(sla.getLimit());
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
//                strCheckHolidays = sla.getInput2();
//                if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
//                    strCheckHolidays = "N";
//                }
//
//                strCheckWeekend = sla.getInput3();
//                if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
//                    strCheckWeekend = "N";
//                }
//
//                inDevelopmentStatus = sla.getConfig1();
//                if (inDevelopmentStatus.equals("")) {
//                    message = twoSpace + "Status for Development / Issue Committed to Sprint not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String statusforReopen = sla.getConfig1().replace("'", "");
//                String statusForClose = sla.getConfig2().replace("'", "");
//
//                if (statusforReopen.equals("")) {
//                    message = twoSpace + "Status for ReOpen Check for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (statusForClose.equals("")) {
//                    message = twoSpace + "Closed status (Development/Testing) for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String closedStatus = "";
//                String prevClosedStatus = "";
//
//                String[] arrstatusForReopen = statusforReopen.split(",");
//                if (arrstatusForReopen == null && arrstatusForReopen.length == 0) {
//                    message = twoSpace + "Status(s) to check FTR not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String[] arrClosedStatus = statusForClose.split(",");
//                if (arrClosedStatus != null && arrClosedStatus.length > 2) {
//                    message = twoSpace + "Only Two status(s) can be mentioned for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
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
//
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
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, true, pageSize);
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Status", "commited Date", "reopend Date", "Variance (In Days)", "Record Status"});
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
//                            try {
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
//                                        if (util.isDateValid(strClosedDate, sourcedateFormat) == true) {
//                                            closedIssueDate = util.ConvertToDate(strClosedDate, sourcedateFormat);
//                                        }
//                                    }
//                                }
//
//                                if (reopenHistory != null) {
//                                    strReopenDate = reopenHistory.getCreated();
//                                    if (!strReopenDate.isEmpty()) {
//                                        if (util.isDateValid(strReopenDate, sourcedateFormat) == true) {
//                                            reOpenIssueDate = util.ConvertToDate(strReopenDate, sourcedateFormat);
//                                        }
//                                    }
//                                }
//                            } catch (Exception exIssueError) {
//                                message = twoSpace + "Error : " + exIssueError.getMessage() + " for the issue :" + issue.getKey();
//                                status = util.WriteToFile(project.getLogFile(), message);
//                            }
//
//                            if (closedIssueDate != null && reOpenIssueDate != null) {
//                                varianceDate = util.GetDayVariance(closedIssueDate, reOpenIssueDate, lstHolidays, strCheckHolidays, strCheckWeekend);
//                            }
//
//                            issueDateVariance = new IssueDateVariance();
//                            issueDateVariance.setCommitedDate(closedIssueDate);
//                            issueDateVariance.setClosedDate(reOpenIssueDate);
//                            issueDateVariance.setVariance((double) varianceDate);
//                            issueDateVariance.setKey(issue.getKey());
//                            issueDateVariance.setType(issue.getFields().getIssuetype().getName());
//                            issueDateVariance.setIssueStatus(issue.getFields().getStatus().getName());
//
//                            if (varianceDate == 0) {
//                                issueDateVariance.setStatus("Not Met");
//                            } else {
//                                if (varianceDate <= limitFromConfig) {
//                                    issueDateVariance.setStatus("Met");
//                                } else {
//                                    issueDateVariance.setStatus("Not Met");
//                                }
//                            }
//
//                            eligibleissue.add(issueDateVariance);
//                        }
//                    }
//
//                    if (eligibleissue != null && eligibleissue.size() > 0) {
//                        totalnumCount = (int) eligibleissue.stream().filter(x -> x.getStatus().equals("Met")).count();
//
//                        for (IssueDateVariance issueData : eligibleissue) {
//                            strReopenDate = "";
//                            strClosedDate = "";
//
//                            if (project.getDatafileRequired().equals("Y")) {
//                                if (issueData.getCommitedDate() != null) {
//                                    strClosedDate = String.valueOf(issueData.getCommitedDate());
//                                }
//                                if (issueData.getClosedDate() != null) {
//                                    strReopenDate = String.valueOf(issueData.getClosedDate());
//                                }
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    private ProcessedData MTTR(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                message = twoSpace + "Not Implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    private ProcessedData SystemUpTime(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Done by Arun. To be tested
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        try {
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//    }
//
//    private ProcessedData VolumeOfIncidents(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        //Done by Arun but needs to be tested
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        List<String[]> dataLinesDetails = new ArrayList<>();
//        float variance = 0f;
//        double finalLimitValue = 0.0;
//        double actualValue = 0;
//        String commentURI = "";
//        double baseLine = 0;
//
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//
//                totaldenoCount = 0;
//                totalnumCount = 0;
//                message = "";
//
//                if (sla.getDenojql() != null && sla.getDenojql().isEmpty() == false) {
//                    denourl = project.getProjecturl() + "/api/2/search?jql=" + sla.getDenojql();
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
//                if (denourl.isEmpty()) {
//                    message = twoSpace + "numJQL and demoJQL is not available, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                float limitValue = 0.f;
//                if (sla.getLimit() != null) {
//                    limitValue = Float.parseFloat(sla.getLimit());
//                } else {
//                    limitValue = Float.parseFloat("15");
//                }
//
//                String strTotalNumCount = sla.getConfig1();
//
//                if (strTotalNumCount.isEmpty()) {
//                    message = twoSpace + "Num count not found, Check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                try {
//                    totalnumCount = Integer.parseInt(strTotalNumCount);  //Baseline Average from the config
//                } catch (Exception exparse) {
//                    message = twoSpace + "Cannot able to parse Num count value, Check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, false, 1000);
//                totaldenoCount = denoIssue.size();
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
//                actualValue = totalnumCount + totalnumCount * (limitValue / 100);
//                message += newLine + twoSpace + " Total New Baseline = " + totalnumCount;
//
//                if (totaldenoCount > actualValue) {
//                    slaStatus = "Not Met";
//                } else {
//                    slaStatus = "Met";
//                }
//
//                message += newLine + twoSpace + " Status = " + slaStatus;
//                ProcessedData data = util.BuildProcessData(sla, 0, slaStatus, String.valueOf(actualValue), String.valueOf(denoCountActual));
//                boolean isStatus = util.WriteToFile(project.getLogFile(), message);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//    }
//
//    private ProcessedData PercentageofNBServicesAutomate(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        IssueDateVariance issueDateVariance = new IssueDateVariance();
//        List<History> Historylist = new ArrayList<>();
//        List<Item> ItemList = new ArrayList<>();
//        Date closeDate = new Date();
//        double totDenoCount = 0;
//        double totNumCount = 0;
//        double actualValue = 0;
//        String inDevelopmentStatus = "";
//        String sourcedateFormat = "";
//        closeDate = null;
//        String holidayList = "";
//        String projectdateFormat = "";
//        int limitFromConfig = 0;
//        List<Date> lstHolidays = new ArrayList<>();
//        Date dtFromDate = null;
//        Date dtToDate = null;
//        String strCheckHolidays = "";
//        String strCheckWeekend = "";
//        int pageSize = 1000;
//        String strPageSize = "";
//        boolean isconditionsatisfied = false;
//        String closeddate = "";
//        String commitedDate = "";
//        List<IssueDateVariance> eligibleissue = new ArrayList<>();
//
//        String strCheckClosedDateInField = sla.getConfig4();
//        Date closedIssueDate = new Date();
//        Date CommitedIssuedate = new Date();
//        String strClosedDate = "";
//        String strcommitedDate = "";
//        String strCloseStatusOnIssue = "";
//        String strcommitedStatusOnIssue = "";
//        long varianceDate = 0;
//        String dateFormatFromConfig = "";
//        History closedHistory = new History();
//        History committedHistory = new History();
//        try {
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }
//
//    private ProcessedData ITCustomerSatisfication(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        String strActual = sla.getConfig1();
//        double actualValue = 0;
//
//        try {
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//
//
//    }
//
//    private ProcessedData NotifyToCustomerOfOutrage(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                message = twoSpace + "Not Implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }
//
//    private ProcessedData ProblemRCATime(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        IssueDateVariance issueDateVariance = new IssueDateVariance();
//        List<History> Historylist = new ArrayList<>();
//        List<Item> ItemList = new ArrayList<>();
//        Date committedDate = new Date();//committeddate
//        Date closeDate = new Date();
//        double totDenoCount = 0;
//        double totNumCount = 0;
//        double actualValue = 0;
//        String inDevelopmentStatus = "";
//        String sourcedateFormat = "";
//        closeDate = null;
//        String holidayList = "";
//        String projectdateFormat = "";
//        int limitFromConfig = 0;
//        List<Date> lstHolidays = new ArrayList<>();
//        String strCheckHolidays = "";
//        String strCheckWeekend = "";
//        int pageSize = 1000;
//        String strPageSize = "";
//        boolean isconditionsatisfied = false;
//        String closeddate = "";
//        String commitedDate = "";
//        List<IssueDateVariance> eligibleissue = new ArrayList<>();
//        String strCheckCommittedDateInField = sla.getConfig5();
//        String strCheckClosedDateInField = sla.getConfig4();
//        Date closedIssueDate = new Date();
//        Date CommitedIssuedate = new Date();
//        String strClosedDate = "";
//        String strcommitedDate = "";
//        String strCloseStatusOnIssue = "";
//        String strcommitedStatusOnIssue = "";
//        long varianceDate = 0;
//        String dateFormatFromConfig = "";
//        String Rcafromconfig = sla.getConfig4().replace("'", "");
//        History closedHistory = new History();
//        List<Comments> comments = new ArrayList<>();
//
//
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                sourcedateFormat = project.getSourceDateFormat();
//                if (sourcedateFormat.equals("")) {
//                    message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                dateFormatFromConfig = project.getDateFormat();
//
//                if (dateFormatFromConfig.equals("")) {
//                    message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (Rcafromconfig.equals("")) {
//                    message = twoSpace + "Rca approved Keyword Not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                //Holidate List Data validation
//                holidayList = project.getHolidays();
//                if (holidayList.equals("")) {
//                    message = twoSpace + "Holiday details not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//                String[] arrHoliday = holidayList.split(",");
//                lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);
//
//                //if (lstHolidays == null)
//                if (lstHolidays == null || lstHolidays.size() == 0) {
//                    message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                limitFromConfig = Integer.parseInt(sla.getLimit());
//                int limitforp2 = Integer.parseInt(sla.getConfig1());
//                int limitforp3 = Integer.parseInt(sla.getConfig3());
//                String commentdate = sla.getInput4();
//                if (commentdate.isEmpty()) {
//                    commentdate = "N";
//
//                }
//                strPageSize = project.getPageSize();
//                if (!strPageSize.isEmpty()) {
//                    try {
//                        pageSize = Integer.parseInt(strPageSize);
//                    } catch (Exception exPageSizeParse) {
//                        pageSize = 1000;
//                    }
//                }
//                if (strCheckCommittedDateInField.isEmpty()) {
//                    strCheckCommittedDateInField = "N";
//                }
//                strCheckClosedDateInField = "";
//                strCheckClosedDateInField = sla.getConfig5();
//                if (strCheckClosedDateInField.isEmpty()) {
//                    strCheckClosedDateInField = "N";
//                }
//
//                strCheckHolidays = sla.getInput2();
//                if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
//                    strCheckHolidays = "N";
//                }
//
//                strCheckWeekend = sla.getInput3();
//                if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
//                    strCheckWeekend = "N";
//                }
//
//                String statusforcommited = sla.getConfig1().replace("'", "");
//                String statusForClose = sla.getConfig2().replace("'", "");
//
//                if (statusforcommited.equals("")) {
//                    message = twoSpace + "Status for commited Check for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (statusForClose.equals("")) {
//                    message = twoSpace + "Closed status (Development/Testing) for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String closedStatus = "";
//                String prevClosedStatus = "";
//
//
//                String[] arrClosedStatus = statusForClose.split(",");
//                if (arrClosedStatus != null && arrClosedStatus.length > 2) {
//                    message = twoSpace + "Only Two status(s) can be mentioned for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
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
//
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
//                expectedsla = Integer.parseInt(sla.getExpectedsla());
//                minsla = Integer.parseInt(sla.getMinimumsla());
//
//                if (expectedsla == 0 || minsla == 0) {
//                    //Stop the processing
//                    message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//                String baseURI = project.getProjecturl() + "/api/latest/search?";
//                String commentURI = project.getProjecturl() + "/agile/1.0/issue";
//                denoIssue = iJiraDataService.getAllIssuesOnJQLV1(userName, password, denourl, baseURI, commentURI, true, true);
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Status", "closed Date", "commited Date", "Variance (In Days)", "Record Status", "Priority Incident"});
//                }
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
//                                committedDate = null;
//                                strClosedDate = "";
//                                strcommitedDate = "";
//                                strCloseStatusOnIssue = "";
//                                strcommitedStatusOnIssue = "";
//                                varianceDate = 0;
//                                strcommitedDate = issue.getFields().getCreated();
//                                closedHistory = null;
//                                comments = new ArrayList<>();
//                                comments = issue.getFields().getComment().getComments();
//
//
//                                List<History> historyList = new ArrayList<>();
//                                if (issue.getChangelog() != null) {
//                                    if (issue.getChangelog().getHistories() != null && issue.getChangelog().getHistories().size() > 0) {
//                                        historyList = issue.getChangelog().getHistories();
//                                    }
//                                }
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
//
//
//                                    }
//                                } else {
//                                    if (!Rcafromconfig.isEmpty()) {
//                                        for (Comments cmnt : comments) {
//                                            message = message + newLine + twoSpace + "Processing Comments";
//                                            String commentSection = cmnt.getBody();
//                                            message = message + newLine + twoSpace + "Comment body" + commentSection;
//                                            message = message + newLine + twoSpace + "Identifying Receive keyword " + Rcafromconfig;
//
//                                            if (cmnt.getBody().startsWith(Rcafromconfig) || cmnt.getBody().contains(Rcafromconfig)) {
//                                                //Read the comment to find the receive date
//                                                message = message + newLine + twoSpace + "Receive keyword : " + Rcafromconfig + "Found";
//                                                strClosedDate = util.GetReceiveEstimationDates(commentSection, Rcafromconfig, "#", dateFormatFromConfig);
//
//                                                if (!strClosedDate.equals("")) {
//                                                    if (strClosedDate.startsWith("Error")) {
//                                                        message = message + newLine + twoSpace + "Error while retrieving the received date, " + strClosedDate;
//                                                        strClosedDate = "";
//                                                    } else {
//                                                        message = message + newLine + twoSpace + "Successfully retrieved the received date, " + strClosedDate;
//                                                    }
//                                                } else {
//                                                    message = message + newLine + twoSpace + "No RCA Approve date Found, the issue will be not condidered";
//                                                }
//                                            } else {
//                                                message = message + newLine + twoSpace + " RCA Approved keyword : " + Rcafromconfig + "not found";
//                                            }
//
//
//                                        }
//                                        closedIssueDate = util.ConvertToDate(strClosedDate, dateFormatFromConfig);
//                                    }
//                                }
//
//                            } catch (Exception exIssueError) {
//                                message = twoSpace + "Error : " + exIssueError.getMessage() + " for the issue :" + issue.getKey();
//                                status = util.WriteToFile(project.getLogFile(), message);
//                            }
//
//                            CommitedIssuedate = util.ConvertToDate(strcommitedDate, sourcedateFormat);
//
//                            if (closedIssueDate != null && CommitedIssuedate != null) {
//                                varianceDate = util.GetDayVariance(CommitedIssuedate, closedIssueDate, lstHolidays, strCheckHolidays, strCheckWeekend);
//                            }
//
//                            issueDateVariance = new IssueDateVariance();
//                            issueDateVariance.setCommitedDate(CommitedIssuedate);
//                            issueDateVariance.setClosedDate(closedIssueDate);
//                            issueDateVariance.setVariance((double) varianceDate);
//                            issueDateVariance.setKey(issue.getKey());
//                            issueDateVariance.setType(issue.getFields().getIssuetype().getName());
//                            issueDateVariance.setIssueStatus(issue.getFields().getStatus().getName());
//
//                            if (varianceDate == 0) {
//                                issueDateVariance.setStatus("Not Met");
//                            }
//                            if (varianceDate <= limitFromConfig) {
//                                issueDateVariance.setStatus("Met");
//                                issueDateVariance.setPriority("P1");
//                            }
//                            if ((varianceDate <= limitforp2) && (varianceDate > limitFromConfig)) {
//                                issueDateVariance.setStatus("Met");
//                                issueDateVariance.setPriority("P2");
//                            }
//                            if ((varianceDate <= limitforp3) && (varianceDate > limitforp2)) {
//                                issueDateVariance.setStatus("Met");
//                                issueDateVariance.setPriority("P3");
//                            } else {
//                                if (varianceDate > limitforp3) {
//                                    issueDateVariance.setStatus("Not Met");
//                                }
//                            }
//
//                            eligibleissue.add(issueDateVariance);
//                        }
//                    }
//
//                }
//
//                if (eligibleissue != null && eligibleissue.size() > 0) {
//                    totalnumCount = (int) eligibleissue.stream().filter(x -> x.getStatus().equals("Met")).count();
//
//                    for (IssueDateVariance issueData : eligibleissue) {
//                        strcommitedDate = "";
//                        strClosedDate = "";
//
//                        if (project.getDatafileRequired().equals("Y")) {
//                            if (issueData.getCommitedDate() != null) {
//                                strClosedDate = String.valueOf(issueData.getCommitedDate());
//                            }
//                            if (issueData.getClosedDate() != null) {
//                                strcommitedDate = String.valueOf(issueData.getClosedDate());
//                            }
//
//                            dataLines.add(new String[]
//                                    {issueData.getKey(), issueData.getType(), issueData.getIssueStatus(),
//                                            strClosedDate,
//                                            strcommitedDate,
//                                            String.valueOf(issueData.getVariance()), issueData.getStatus(), issueData.getPriority()});
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }
//
//    private ProcessedData ProblemResolutionTime(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        IssueDateVariance issueDateVariance = new IssueDateVariance();
//        List<History> Historylist = new ArrayList<>();
//        List<Item> ItemList = new ArrayList<>();
//        Date committedDate = new Date();//committeddate
//        Date closeDate = new Date();
//        double totDenoCount = 0;
//        double totNumCount = 0;
//        double actualValue = 0;
//        String inDevelopmentStatus = "";
//        String sourcedateFormat = "";
//        closeDate = null;
//        String holidayList = "";
//        String projectdateFormat = "";
//        int limitFromConfig = 0;
//        List<Date> lstHolidays = new ArrayList<>();
//        Date dtFromDate = null;
//        Date dtToDate = null;
//        String strCheckHolidays = "";
//        String strCheckWeekend = "";
//        int pageSize = 1000;
//        String strPageSize = "";
//        boolean isconditionsatisfied = false;
//        String closeddate = "";
//        String commitedDate = "";
//        List<IssueDateVariance> eligibleissue = new ArrayList<>();
//        String strCheckCommittedDateInField = sla.getConfig5();
//        String strCheckClosedDateInField = sla.getConfig4();
//        Date closedIssueDate = new Date();
//        Date CommitedIssuedate = new Date();
//        String strClosedDate = "";
//        String strcommitedDate = "";
//        String strCloseStatusOnIssue = "";
//        String strcommitedStatusOnIssue = "";
//        long varianceDate = 0;
//        String dateFormatFromConfig = "";
//
//        History closedHistory = new History();
//        History committedHistory = new History();
//        try {
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
//                //Holidate List Data validation
//                holidayList = project.getHolidays();
//                if (holidayList.equals("")) {
//                    message = twoSpace + "Holiday details not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//                String[] arrHoliday = holidayList.split(",");
//                lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);
//
//                //if (lstHolidays == null)
//                if (lstHolidays == null || lstHolidays.size() == 0) {
//                    message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                limitFromConfig = Integer.parseInt(sla.getLimit());
//
//                strPageSize = project.getPageSize();
//                if (!strPageSize.isEmpty()) {
//                    try {
//                        pageSize = Integer.parseInt(strPageSize);
//                    } catch (Exception exPageSizeParse) {
//                        pageSize = 1000;
//                    }
//                }
//                if (strCheckCommittedDateInField.isEmpty()) {
//                    strCheckCommittedDateInField = "N";
//                }
//
//                if (strCheckClosedDateInField.isEmpty()) {
//                    strCheckClosedDateInField = "N";
//                }
//
//                strCheckHolidays = sla.getInput2();
//                if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
//                    strCheckHolidays = "N";
//                }
//
//                strCheckWeekend = sla.getInput3();
//                if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
//                    strCheckWeekend = "N";
//                }
//
//                String statusforcommited = sla.getConfig1().replace("'", "");
//                String statusForClose = sla.getConfig2().replace("'", "");
//
//                if (statusforcommited.equals("")) {
//                    message = twoSpace + "Status for commited Check for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (statusForClose.equals("")) {
//                    message = twoSpace + "Closed status (Development/Testing) for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String closedStatus = "";
//                String prevClosedStatus = "";
//
//                String[] arrstatusForcommited = statusforcommited.split(",");
//                if (arrstatusForcommited == null && arrstatusForcommited.length == 0) {
//                    message = twoSpace + "Status(s) to check FTR not found, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String[] arrClosedStatus = statusForClose.split(",");
//                if (arrClosedStatus != null && arrClosedStatus.length > 2) {
//                    message = twoSpace + "Only Two status(s) can be mentioned for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
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
//
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
//                denoIssue = iJiraDataService.getIssuesUsingJQL(userName, password, denourl, "", false, true, pageSize);
//
//                if (project.getDatafileRequired().equals("Y")) {
//                    dataLines.add(new String[]
//                            {"Key", "Type", "Status", "closed Date", "commited Date", "Variance (In Days)", "Record Status"});
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
//                                committedDate = null;
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
//                                                if (strCheckCommittedDateInField.equals("N")) {
//                                                    if (closedHistory != null) {
//                                                        if (firstOccuranceFTR == false) {
//                                                            for (Item item : itemList) {
//                                                                if (item.getField().equals("status")) {
//                                                                    for (String cStatus : arrstatusForcommited) {
//                                                                        if (firstOccuranceFTR == false) {
//                                                                            if (item.getToString().equals(cStatus.trim())) {
//                                                                                committedHistory = history;
//                                                                                firstOccuranceFTR = true;
//                                                                                strcommitedStatusOnIssue = item.getToString();
//                                                                                strcommitedDate = history.getCreated();
//                                                                            }
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                } else {
//                                                    if (strCheckCommittedDateInField.equals("Created")) {
//                                                        issue.getFields().getCreated();
//                                                    }
//                                                }
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
//
//                                        if (committedHistory != null) {
//                                            strcommitedDate = committedHistory.getCreated();
//                                            if (!strcommitedDate.isEmpty()) {
//                                                if (util.isDateValid(strcommitedDate, sourcedateFormat) == true) {
//                                                    committedDate = util.ConvertToDate(strcommitedDate, sourcedateFormat);
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//
//
//                            } catch (Exception exIssueError) {
//                                message = twoSpace + "Error : " + exIssueError.getMessage() + " for the issue :" + issue.getKey();
//                                status = util.WriteToFile(project.getLogFile(), message);
//                            }
//
//                            if (closedIssueDate != null && committedDate != null) {
//                                varianceDate = util.GetDayVariance(committedDate, closedIssueDate, lstHolidays, strCheckHolidays, strCheckWeekend);
//                            }
//
//                            issueDateVariance = new IssueDateVariance();
//                            issueDateVariance.setCommitedDate(committedDate);
//                            issueDateVariance.setClosedDate(closedIssueDate);
//                            issueDateVariance.setVariance((double) varianceDate);
//                            issueDateVariance.setKey(issue.getKey());
//                            issueDateVariance.setType(issue.getFields().getIssuetype().getName());
//                            issueDateVariance.setIssueStatus(issue.getFields().getStatus().getName());
//
//                            if (varianceDate == 0) {
//                                issueDateVariance.setStatus("Not Met");
//                            } else {
//                                if (varianceDate <= limitFromConfig) {
//                                    issueDateVariance.setStatus("Met");
//                                } else {
//                                    issueDateVariance.setStatus("Not Met");
//                                }
//                            }
//
//                            eligibleissue.add(issueDateVariance);
//                        }
//                    }
//
//                }
//                if (eligibleissue != null && eligibleissue.size() > 0) {
//                    totalnumCount = (int) eligibleissue.stream().filter(x -> x.getStatus().equals("Met")).count();
//
//                    for (IssueDateVariance issueData : eligibleissue) {
//                        strcommitedDate = "";
//                        strClosedDate = "";
//
//                        if (project.getDatafileRequired().equals("Y")) {
//                            if (issueData.getCommitedDate() != null) {
//                                strcommitedDate = String.valueOf(issueData.getCommitedDate());
//                            }
//                            if (issueData.getClosedDate() != null) {
//                                strClosedDate = String.valueOf(issueData.getClosedDate());
//                            }
//
//                            dataLines.add(new String[]
//                                    {issueData.getKey(), issueData.getType(), issueData.getIssueStatus(),
//                                            strClosedDate,
//                                            strcommitedDate,
//                                            String.valueOf(issueData.getVariance()), issueData.getStatus()});
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
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }
//
//    private ProcessedData RegulatoryUpdate(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                ProcessedData regulatory = JiraProcessWithJQLCountGeneric(sla, userName, password, project, retrievedIssues);
//                return regulatory;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }
//
//    private ProcessedData CriticalSecurityThreatMitigation(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        List<String[]> dataLinesDetails = new ArrayList<>();
//        List<Date> lstHolidays;
//
//        double variance = 0f;
//        double finalLimitValue = 0.0;
//        double actualValue = 0;
//        String holidayList = "";
//        String dateFormatFromConfig = "";
//        String strPageSize = "";
//        int pageSize = 0;
//        String closedStatus = "";
//        String prevClosedStatus = "";
//        String includeCommittedDate = "";
//        Date committedDate = null;
//        Date closedDate = null;
//        String strCommittedDate = "";
//        String strClosedDate = "";
//        String sourcedateFormat = "";
//        float limitValue = 0.f;
//        String strLimitValue = "";
//
//        IssueDateVariance issueDateVariance = new IssueDateVariance();
//        List<IssueDateVariance> eligibleissue = new ArrayList<>();
//        List<Item> itemList = new ArrayList<>();
//
//        try {
//            sourcedateFormat = project.getSourceDateFormat();
//            if (sourcedateFormat.equals("")) {
//                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            strPageSize = project.getPageSize();
//            if (!strPageSize.isEmpty()) {
//                try {
//                    pageSize = Integer.parseInt(strPageSize);
//                } catch (Exception exPageSizeParse) {
//                    pageSize = 1000;
//                }
//            }
//
//            expectedsla = Integer.parseInt(sla.getExpectedsla());
//            minsla = Integer.parseInt(sla.getMinimumsla());
//
//            if (expectedsla == 0 || minsla == 0) {
//                //Stop the processing
//                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            strLimitValue = sla.getLimit();
//            if (strLimitValue.isEmpty()) {
//                message = twoSpace + "Limit value not found, please check your configuration. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            try {
//                limitValue = Float.parseFloat(sla.getLimit());
//            } catch (Exception exParse) {
//                message = twoSpace + "Unable to parse Limit value, please check your configuration. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            dateFormatFromConfig = project.getDateFormat().replace("'", "");
//            if (dateFormatFromConfig.equals("")) {
//                message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            holidayList = project.getHolidays();
//            if (holidayList.equals("")) {
//                message = twoSpace + "Holiday details not found. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//            String[] arrHoliday = holidayList.split(",");
//            lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);
//
//            if (lstHolidays == null) {
//                message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            String committedStatus = sla.getConfig1();
//            String closeStatus = sla.getConfig2();
//            String checkCommitedInField = sla.getConfig3();
//            String checkClosedInField = sla.getConfig4();
//            String strCheckHolidays = sla.getInput2();
//            String strCheckWeekend = sla.getInput3();
//            includeCommittedDate = sla.getInput4();
//
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //User can provide either Deno JQL or they can provide both Deno and Num JQL
//                //When User only provide Deno JQL, the system will check the retrieved issues using Deno JQL to get the Committed date and Closed Date, , Find the variance and check if within the limit then add it to the num Count
//                //When user proide both Deno and NUM JQL, system will
//                //  - Take the Count from DENO JQL = Deno Count
//                //  - Use the data retrieved from NUM JQL to get the Committed date, Closed date, Find the variance and check if within the limit then add it to the num Count
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
//                if ((!StringUtils.hasText(committedStatus) || committedStatus.equals("")) && (!StringUtils.hasText(checkCommitedInField) || checkCommitedInField.equals(""))) {
//                    message = twoSpace + "Please provide committed status or Field name to check the committed date, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//
//                if (!StringUtils.hasText(closeStatus) || closeStatus.equals("") && (!StringUtils.hasText(checkClosedInField) || checkClosedInField.equals(""))) {
//                    message = twoSpace + "Please provide Closed status or Field name to check the closed date, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String[] arrClosedStatus = closeStatus.split(",");
//                if (arrClosedStatus != null && arrClosedStatus.length > 2) {
//                    message = twoSpace + "Only Two status(s) can be mentioned for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (arrClosedStatus != null && arrClosedStatus.length >= 1) {
//                    closedStatus = arrClosedStatus[0];
//                }
//
//                if (arrClosedStatus != null && arrClosedStatus.length >= 2) {
//                    prevClosedStatus = arrClosedStatus[1];
//                }
//
//                if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
//                    strCheckHolidays = "N";
//                }
//
//                if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
//                    strCheckWeekend = "N";
//                }
//
//                if (includeCommittedDate.equals("") || !includeCommittedDate.equals("Y")) {
//                    includeCommittedDate = "N";
//                }
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//
//        }
//
//        return null;
//    }
//
//    private ProcessedData Patches(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        try {
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                ProcessedData data = JiraProcessWithJQLCountGeneric(sla, userName, password, project, retrievedIssues);
//                return data;
//            }
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            return null;
//        }
//
//    }
//
//    private ProcessedData ServiceLevelDataQuality(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//
//        int pageSize = 1000;
//        String strPageSize = "";
//
//        try {
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
//
//            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            return null;
//        } catch (Exception ex) {
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//        }
//
//        return null;
//
//    }
//
//    private ProcessedData SecurityThreatMitigation(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
//        String baseURI = "";
//        message = "Processing SLA : " + sla.getSlaname();
//        status = util.WriteToFile(project.getLogFile(), message);
//        List<Issue> denoIssue = new ArrayList<>();
//        List<Issue> numIssue = new ArrayList<>();
//        List<String[]> dataLines = new ArrayList<>();
//        List<String[]> dataLinesDetails = new ArrayList<>();
//        List<Date> lstHolidays;
//
//        double variance = 0f;
//        double finalLimitValue = 0.0;
//        double actualValue = 0;
//        String holidayList = "";
//        String dateFormatFromConfig = "";
//        String strPageSize = "";
//        int pageSize = 0;
//        String closedStatus = "";
//        String prevClosedStatus = "";
//        String includeCommittedDate = "";
//        Date committedDate = null;
//        Date closedDate = null;
//        String strCommittedDate = "";
//        String strClosedDate = "";
//        String sourcedateFormat = "";
//        float limitValue = 0.f;
//        String strLimitValue = "";
//        String Critical = "";
//        String Major = "";
//        String High = "";
//        String Low = "";
//        String CriticalLevel = "";
//        String MajorLevel = "";
//        String HighLevel = "";
//        String LowLevel = "";
//
//        IssueDateVariance issueDateVariance = new IssueDateVariance();
//        List<IssueDateVariance> eligibleissue = new ArrayList<>();
//        List<Item> itemList = new ArrayList<>();
//
//        try {
//            sourcedateFormat = project.getSourceDateFormat();
//            if (sourcedateFormat.equals("")) {
//                message = twoSpace + "Source Date format is not found, please check your configuration. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            strPageSize = project.getPageSize();
//            if (!strPageSize.isEmpty()) {
//                try {
//                    pageSize = Integer.parseInt(strPageSize);
//                } catch (Exception exPageSizeParse) {
//                    pageSize = 1000;
//                }
//            }
//
//
//            expectedsla = Integer.parseInt(sla.getExpectedsla());
//            minsla = Integer.parseInt(sla.getMinimumsla());
//
//            if (expectedsla == 0 || minsla == 0) {
//                //Stop the processing
//                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            String[] strLimitValue1 = sla.getLimit().split(",");
//            if (strLimitValue1 != null && strLimitValue1.length > 4) {
//                message = twoSpace + "Only Four Limit can be mentioned. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            if (strLimitValue1 != null && strLimitValue1.length >= 1) {
//                Critical = strLimitValue1[0];
//            }
//
//            if (strLimitValue1 != null && strLimitValue1.length >= 2) {
//                Major = strLimitValue1[1];
//            }
//
//            if (strLimitValue1 != null && strLimitValue1.length >= 3) {
//                High = strLimitValue1[2];
//            }
//
//            if (strLimitValue1 != null && strLimitValue1.length >= 4) {
//                Low = strLimitValue1[3];
//            }
//
//            String[] PriorityLevel = sla.getConfig5().split(",");
//            if (PriorityLevel != null && PriorityLevel.length > 4) {
//                message = twoSpace + "Only Four PriorityLevel can be mentioned. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            if (PriorityLevel != null && PriorityLevel.length >= 1) {
//                CriticalLevel = PriorityLevel[0];
//            }
//
//            if (PriorityLevel != null && PriorityLevel.length >= 2) {
//                MajorLevel = PriorityLevel[1];
//            }
//
//            if (PriorityLevel != null && PriorityLevel.length >= 3) {
//                HighLevel = PriorityLevel[2];
//            }
//
//            if (PriorityLevel != null && PriorityLevel.length >= 4) {
//                LowLevel = PriorityLevel[3];
//            }
//
//            dateFormatFromConfig = project.getDateFormat().replace("'", "");
//            if (dateFormatFromConfig.equals("")) {
//                message = twoSpace + "Date format is not found, please check your configuration. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//            holidayList = project.getHolidays();
//            if (holidayList.equals("")) {
//                message = twoSpace + "Holiday details not found. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//            String[] arrHoliday = holidayList.split(",");
//            lstHolidays = util.ValidateHolidayList(arrHoliday, dateFormatFromConfig);
//
//            if (lstHolidays == null) {
//                message = twoSpace + "Some of the holiday date is not valid. Stopping SLA calculation";
//                status = util.WriteToFile(project.getLogFile(), message);
//                return null;
//            }
//
//
//            String committedStatus = sla.getConfig1();
//            String closeStatus = sla.getConfig2();
//            String checkCommitedInField = sla.getConfig3();
//            String checkClosedInField = sla.getConfig4();
//            String strCheckHolidays = sla.getInput2();
//            String strCheckWeekend = sla.getInput3();
//            includeCommittedDate = sla.getInput4();
//
//            if (project.getProjectsource().equals(SourceKey.JIRA.value)) {
//                //User can provide either Deno JQL or they can provide both Deno and Num JQL
//                //When User only provide Deno JQL, the system will check the retrieved issues using Deno JQL to get the Committed date and Closed Date, , Find the variance and check if within the limit then add it to the num Count
//                //When user proide both Deno and NUM JQL, system will
//                //  - Take the Count from DENO JQL = Deno Count
//                //  - Use the data retrieved from NUM JQL to get the Committed date, Closed date, Find the variance and check if within the limit then add it to the num Count
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
//                if ((!StringUtils.hasText(committedStatus) || committedStatus.equals("")) && (!StringUtils.hasText(checkCommitedInField) || checkCommitedInField.equals(""))) {
//                    message = twoSpace + "Please provide committed status or Field name to check the committed date, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//
//                if (!StringUtils.hasText(closeStatus) || closeStatus.equals("") && (!StringUtils.hasText(checkClosedInField) || checkClosedInField.equals(""))) {
//                    message = twoSpace + "Please provide Closed status or Field name to check the closed date, please check your configuration. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                String[] arrClosedStatus = closeStatus.split(",");
//                if (arrClosedStatus != null && arrClosedStatus.length > 2) {
//                    message = twoSpace + "Only Two status(s) can be mentioned for the issue not found. Stopping SLA calculation";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//
//                if (arrClosedStatus != null && arrClosedStatus.length >= 1) {
//                    closedStatus = arrClosedStatus[0];
//                }
//
//                if (arrClosedStatus != null && arrClosedStatus.length >= 2) {
//                    prevClosedStatus = arrClosedStatus[1];
//                }
//
//                if (strCheckHolidays.equals("") || !strCheckHolidays.equals("Y")) {
//                    strCheckHolidays = "N";
//                }
//
//                if (strCheckWeekend.equals("") || !strCheckWeekend.equals("Y")) {
//                    strCheckWeekend = "N";
//                }
//
//                if (includeCommittedDate.equals("") || !includeCommittedDate.equals("Y")) {
//                    includeCommittedDate = "N";
//                }
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
//
//                                if (issueDateVariance.getCommitedDate() != null && issueDateVariance.getClosedDate() != null && issue.getFields().getPriority().getName() != null) {
//                                     if (issue.getFields().getPriority().getName().equalsIgnoreCase(CriticalLevel)) {
//                                        if (issueDateVariance.getVariance() <= Integer.parseInt(Critical)) {
//                                            issueDateVariance.setStatus("Met");
//                                        } else {
//                                            issueDateVariance.setStatus("Not Met");
//                                        }
//                                    }
//
//                                    else if (issue.getFields().getPriority().getName().equalsIgnoreCase(MajorLevel)) {
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
//                                   else if (issue.getFields().getPriority().getName().equalsIgnoreCase(LowLevel)) {
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
//                if (project.getProjectsource().equals(SourceKey.ADO.value)) {
//                    message = twoSpace + "This project uses ADO, Configuration which is yet to be implemented. Stoping the processing of SLA";
//                    status = util.WriteToFile(project.getLogFile(), message);
//                    return null;
//                }
//            }
//
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
//            status = util.WriteToFile(project.getLogFile(), message);
//
//        }
//        return null;
//        //Non-backlog SLA Calculation - End
//    }
//}
