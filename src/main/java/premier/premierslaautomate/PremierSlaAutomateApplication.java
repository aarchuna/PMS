package premier.premierslaautomate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IProject;
import premier.premierslaautomate.Interfaces.IJiraDataService;
import premier.premierslaautomate.Models.BacklogIssue;
import premier.premierslaautomate.Models.Issue;
import premier.premierslaautomate.Models.IssueV1;
import premier.premierslaautomate.Models.ProcessedData;
import premier.premierslaautomate.ProjectService.ProjectFactory;
import premier.premierslaautomate.Utilities.CommonUtil;
import premier.premierslaautomate.config.*;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class PremierSlaAutomateApplication
{

    public static String userName = "aarchuna";
    public static String password = "3q3kb25qf7umxhgp7ddq6payuejx2b2slycxv6vtbag2o6pdkjea";
    public static String reProcessSLAData = "N";

    public static void main(String[] args)
    {
//        Console console = System.console();
//        if (console == null) {
//            System.out.println("Couldn't get Console instance, Please try running from a CLI");
//            System.exit(0);
//        }
//        userName = console.readLine("Enter your Username: ");
//        password = new String(console.readPassword("Enter your  password: "));
//        reProcessSLAData = console.readLine("Do you want to re-process the exclusions data? (Y/N) : ");
//
//        if (userName!=null && password!=null && reProcessSLAData!=null)
//        {
//            SpringApplication.run(PremierSlaAutomateApplication.class, args);
//        }else{
//            System.out.println("Please enter your credentials");
//        }
       SpringApplication.run(PremierSlaAutomateApplication.class, args); //Intellij Application use

    }

    //Start of Autowired Section
    @Autowired
    private IJiraDataService iJiraDataService;

    @Autowired
    private ProjectConfiguration projectConfiguration;

    @Autowired
    private ProcessedData processedData;

    @Autowired
    private SLABacklog slasbacklog;

    @Autowired
    private SLANonBackLog slasnonbacklog;

    //End of Autowired Section
    @PostConstruct  //Starting point of program execution
    public void ProcessAutomation() throws ParseException
    {

        //Check if the Call is for Encryption, (Y) then take the password, Encypt it and
        //Print in the Console and quit the program

        //Call is to run SLA (N) then run the SLA



        CommonUtil util = new CommonUtil();
        boolean status = false;
        boolean isDirectoryCreated = false;
        String newLine = "\r\n";
        String message = "";
        String processingTimeStamp = "";
        String basePath = projectConfiguration.getOutputFolder();
        String currentProcessingPath = "";
        String currentProcessingLogPath = "";
        String currentProcessingOutputPath = "";
        String currentProcessingJSonPath = "";
        String currentLogFileName = "";
        String currentJSonFileName = "";
        String currentDetailsLogFileName = "";
        String changeLogURI = "";
        String commentURI = "";
        String finalOutput = "";

        List<Issue> boardIssues = new ArrayList<>();
        List<BacklogIssue> backlogIssues = new ArrayList<>();

        try
        {

            //Reading the SLA Configuration from Application.yml
            List<MeasureConfiguration> lstbacklog = slasbacklog.getBacklogconfiguration();
            List<MeasureConfiguration> lstnonBackLog = slasnonbacklog.getNonbacklogconfiguration();


            String addToFileName = projectConfiguration.getAddToFileName();
            if (addToFileName == null && addToFileName.isEmpty())
            {
                addToFileName = "yyyyMMddHHmmss";
            }

            processingTimeStamp = new SimpleDateFormat(addToFileName).format(Calendar.getInstance().getTime());
            basePath = projectConfiguration.getOutputFolder();
            currentProcessingPath = basePath + "\\\\" + processingTimeStamp;
            currentProcessingLogPath = basePath + "\\\\" + processingTimeStamp + "\\\\Log";
            currentProcessingOutputPath = basePath + "\\\\" + processingTimeStamp + "\\\\Output";
            currentProcessingJSonPath = basePath + "\\\\" + processingTimeStamp + "\\\\JSon";
            currentLogFileName = currentProcessingLogPath + "\\\\" + projectConfiguration.getProjectKey() + "_" + processingTimeStamp + ".log";
            currentJSonFileName = currentProcessingJSonPath + "\\\\" + projectConfiguration.getProjectKey() + "_" + processingTimeStamp + ".log";
            currentDetailsLogFileName = currentProcessingLogPath + "\\\\" + projectConfiguration.getProjectKey() + "_detail_" + processingTimeStamp + ".log";

            String baseDataPath = projectConfiguration.getAutomationData();
            String inputFiles = baseDataPath + "\\\\UserStories";
            String verifiedData = baseDataPath + "\\\\ExclusionsVerifiedData";

            //Create the base folder if not Exists
            File fCreateFolder = new File(basePath);
            if (!fCreateFolder.exists())
            {
                isDirectoryCreated = fCreateFolder.mkdir();
                if (isDirectoryCreated == false)
                {
                    System.out.println("Cannot able to create the base processing path : " + basePath + ", Kindly manually create the folder and process again.");
                    return;
                }
            }

            fCreateFolder = new File(baseDataPath);
            if (!fCreateFolder.exists())
            {
                isDirectoryCreated = fCreateFolder.mkdir();
                if (isDirectoryCreated == false)
                {
                    System.out.println("Cannot able to create the base processing path : " + basePath + ", Kindly manually create the folder and process again.");
                    return;
                }
            }
            //Create Current processing folders if not exists
            isDirectoryCreated = false;
            fCreateFolder = new File(currentProcessingPath);
            if (!fCreateFolder.exists())
            {
                isDirectoryCreated = fCreateFolder.mkdir();
                if (isDirectoryCreated == false)
                {
                    System.out.println("Cannot able to create the current processing path : " + currentProcessingPath + ", Kindly manually create the folder and process again.");
                    return;
                }
            }

            //Log Path
            fCreateFolder = new File(currentProcessingLogPath);
            if (!fCreateFolder.exists())
            {
                isDirectoryCreated = fCreateFolder.mkdir();
                if (isDirectoryCreated == false)
                {
                    System.out.println("Cannot able to create the Log path : " + currentProcessingLogPath + ", Kindly manually create the folder and process again.");
                    return;
                }
            }

            //Output Path
            fCreateFolder = new File(currentProcessingOutputPath);
            if (!fCreateFolder.exists())
            {
                isDirectoryCreated = fCreateFolder.mkdir();
                if (isDirectoryCreated == false)
                {
                    System.out.println("Cannot able to create the Output path : " + currentProcessingOutputPath + ", Kindly manually create the folder and process again.");
                    return;
                }
            }

            fCreateFolder = new File(verifiedData);
            if (!fCreateFolder.exists())
            {
                isDirectoryCreated = fCreateFolder.mkdir();
                if (isDirectoryCreated == false)
                {
                    System.out.println("Cannot create the Log path : " + verifiedData + ", Kindly manually create the folder and process again.");
                    return;
                }
            }

            fCreateFolder = new File(inputFiles);
            if (!fCreateFolder.exists())
            {
                isDirectoryCreated = fCreateFolder.mkdir();
                if (isDirectoryCreated == false)
                {
                    System.out.println("Cannot create Log path : " + inputFiles + ", Kindly manually create the folder and process again.");
                    return;
                }
            }

            if (reProcessSLAData.equals("Y")) {

                fCreateFolder = new File(currentProcessingPath + "\\\\FinalOutput");
                if (!fCreateFolder.exists()) {
                    isDirectoryCreated = fCreateFolder.mkdir();
                    if (isDirectoryCreated == false) {
                        System.out.println("Cannot able to create the Log path : " + currentProcessingPath + "\\FinalOutput, Kindly manually create the folder and process again.");
                        return;
                    }
                }
            }

            if (projectConfiguration.getDetailedLogRequired().equals("Y"))
            {
                fCreateFolder = new File(currentProcessingOutputPath + "\\\\Detail");
                if (!fCreateFolder.exists())
                {
                    isDirectoryCreated = fCreateFolder.mkdir();
                    if (isDirectoryCreated == false)
                    {
                        System.out.println("Cannot able to create the Output path : " + currentProcessingOutputPath + ", Kindly manually create the folder and process again.");
                        return;
                    }
                }
            }

            //JSON Path
            fCreateFolder = new File(currentProcessingJSonPath);
            if (!fCreateFolder.exists())
            {
                isDirectoryCreated = fCreateFolder.mkdir();
                if (isDirectoryCreated == false)
                {
                    System.out.println("Cannot able to create the JSON path : " + currentProcessingJSonPath + ", Kindly manually create the folder and process again.");
                    return;
                }
            }

            if (projectConfiguration.getDetailedLogRequired().equals("Y"))
            {
                fCreateFolder = new File(currentProcessingOutputPath + "\\\\Detail");
                if (!fCreateFolder.exists())
                {
                    isDirectoryCreated = fCreateFolder.mkdir();
                    if (isDirectoryCreated == false)
                    {
                        System.out.println("Cannot able to create the Output path : " + currentProcessingOutputPath + ", Kindly manually create the folder and process again.");
                        return;
                    }
                }
            }



            //Create the Log file for writing. Currently we are not using the Log Component for logging
            status = util.CreateNewFile (currentLogFileName);
            if (!status)
            {
                System.out.println("Unable to create the Log file : " + currentLogFileName + ", Terminating the program");
                return;
            }

            //Setting the Logfile and Output folder in project object
            projectConfiguration.setLogFile(currentLogFileName);
            projectConfiguration.setOutputPath(currentProcessingOutputPath);
            projectConfiguration.setJsonPath(currentProcessingJSonPath);
            projectConfiguration.setDetailedLogFile(currentDetailsLogFileName);

            //Checking for the exclusions, Re-processing the SLAs files
            //Code by Ajinkya

            if (reProcessSLAData.equals("Y"))
            {
                System.out.println("T--------------------------------------------------------------");
                System.out.println("               SLAs re-processing has been started");
                System.out.println("                           Please wait ... ");

                message = "Re-processing the SLAs for Project : " ;
                message = message + newLine + "Project Log Folder : " + currentProcessingLogPath;

                message = message + newLine + "Project Output Folder : " + currentProcessingPath + "\\FinalOutput";
                message = message + newLine + "Script Execution On : " + processingTimeStamp;
                status = util.WriteToFile(currentLogFileName, message);

                String row = "";
                String SLAname = "";
                String twoSpace = "  ";
                String projectKey = "";
                double num = 0;
                double deno = 0;
                double actual = 0;
                double minSla = 0;
                double maxSla = 0;
                List<String[]> dataLines = new ArrayList<>();
                File detailFile = null;
                File folder = new File(verifiedData);
                File[] listOfFiles = folder.listFiles();
                List<String> hLine = null;
                Map<String, String> SLAlink = new HashMap<String, String>();
                String tool = projectConfiguration.getProjectsource();
                int noOfCol = 0;
                int iLineSize = 0;
                int totalFiles = 0;
                int totalReProcessed = 0;
                int filesNotProcessed  = 0;
                boolean process = false;
                boolean mainFile = true;

                //Creating the relation by Mapping the SLA Names and SLA keys to track the file names for re-processing.
                SLAlink.put("Timely Accepted Milestone Delivery", "_TimelyAcceptedMilestone_data");
                SLAlink.put("Product - Percent Testing Completed", "_ProductPercentTestingCompleted_data");
                SLAlink.put("Backlog Item Quality Delivery", "_BacklogItemQuality_data");
                SLAlink.put("Defects Detected in Release Candidates", "_DefectDetectedInUAT_data");
                SLAlink.put("Re-opened defects before GoLive", "_ReopenedDefectsBeforeGOLIVE_data");
                SLAlink.put("Estimation Quality", "_EstimationQuality_data");
                SLAlink.put("Timely Backlog Item Delivery", "_TimelyBackLogItem_data");
                SLAlink.put("Problem Resolution Time", "_ProblemResolutionTime_data");
                SLAlink.put("Time To Offer Backlog Item", "_TimeToOfferBacklogItem_data");
                SLAlink.put("Delay in Ready for GoLive", "_DelayinReadyForGoLive_data");
                SLAlink.put("Premier Stakeholder Customer Satisfaction Survey", "_PremierCustomerSatisficationSurvey_data");
                SLAlink.put("Percentage of tests automated", "_PercentageOfTestsAutomated_data");
                SLAlink.put("Issues detected post-go-live", "_IssuesDetectedPostGOLIVE_data");
                SLAlink.put("Average Cycle Time for release", "_AverageCycleTimeForRelease");
                SLAlink.put("Process Efficienc", "_ProcessEfficiency_data");
                SLAlink.put("Severity Level 1 Incident Resolution", "_SeverityLvl1IncidentResolution_data");
                SLAlink.put("Severity Level 2 Incident Resolution", "_SeverityLvl2IncidentResolution_data");
                SLAlink.put("Severity Level 3 Incident Resolution", "_SeverityLvl3IncidentResolution_data");
                SLAlink.put("Mean Time to Repair", "_MTTR_data");
                SLAlink.put("Percent Of Incident Opened", "_PercentOfIncidentOpened_data");
                SLAlink.put("System UpTime", "_SystemUpTime_data");
                SLAlink.put("Volume Of Incidents", "_VolumeOfIncidents_data");
                SLAlink.put("Percentage of Non-backlog Services Automated", "_PercentageofNBServicesAutomate_data");
                SLAlink.put("Pathches", "_Pathches_data");
                SLAlink.put("Problem Route cause Analysis Time", "_ProblemRCATime_data");
                SLAlink.put("IT Customer Satisfication", "_ITCustomerSatisfication_data");
                SLAlink.put("Critical Security Threat Mitigation", "_CriticalSecurityThreatMitigation_data");
                SLAlink.put("Security Threat Mitigation", "_SecurityThreatMitigation_data");
                SLAlink.put("Adherence to Agile Methodology", "_AdhereToAgileMethodology_data");
                SLAlink.put("Notify To Customer Of Outrag", "_NotifyToCustomerOfOutrage_data");
                SLAlink.put("Regulatory Update", "_RegulatoryUpdate_data");
                SLAlink.put("Service Level Data Quality", "_ServiceLevelDataQuality_data");


                try {

                    //Taking the total files count and finding the main summary file to start the re-processing as per data in it
                    if (listOfFiles != null) {
                        for (File f : listOfFiles) {
                            if (f.getName().contains("_20")) {
                                if (mainFile == true)
                                {
                                    detailFile = f;
                                    projectKey = f.getAbsoluteFile().getName();
                                    projectKey = projectKey.substring(0, projectKey.indexOf("_"));
                                    mainFile = false;
                                }
                            } else if (f.getName().contains(projectKey+"_")) {
                                totalFiles++;
                            }
                        }

                        System.out.println("       processing started for project :" + projectKey);

                        if (detailFile != null) {
                            //Reading the main summary file and adding up the Remark column in the header record to re-generate new file.
                            BufferedReader br = new BufferedReader(new FileReader(detailFile));
                            if ((row = br.readLine()) != null) {
                                hLine = List.of(row.split(","));
                                dataLines.add(new String[]{hLine.get(0), hLine.get(1), hLine.get(2), hLine.get(3), hLine.get(4), hLine.get(5), hLine.get(6), hLine.get(7), "Remark"});
                            }

                            while ((row = br.readLine()) != null) {
                                //Reading out the main summary file line by line in while loop and taking the SLA names from column 1 to re-process them
                                List<String> inLine = List.of(row.split(","));
                                if (inLine != null && !inLine.isEmpty() && inLine.size() > 0) {
                                    SLAname = projectKey + SLAlink.get(inLine.get(0));

                                    if (SLAname != null) {
                                        for (File file : listOfFiles) {

                                            if (file.getAbsoluteFile().getName().equals(SLAname + ".csv")) {
                                                String line = "";
                                                int count = 0;
                                                String remark = "";
                                                BufferedReader reader = new BufferedReader(new FileReader(file));

                                                //Reading out the SLA specific files to check whether it has the EXCLUSIONS and COMMENT columns or not
                                                if ((row = reader.readLine()) != null) {
                                                    List<String> inputLines = List.of(row.split(","));
                                                    iLineSize = inputLines.size();
                                                    process = true;
                                                    if (!inputLines.get(iLineSize - 1).equalsIgnoreCase("comment") && !inputLines.get(iLineSize - 2).equalsIgnoreCase("exclusion")) {
                                                        process = false;
                                                        message = twoSpace + "Error : Exclusion and Comment headers are not found in file " + file + newLine + "Please check your input file and add proper headers. Stopping the re-processing for this";
                                                        status = util.WriteToFile(projectConfiguration.getLogFile(), message);
                                                    }

                                                }

                                                //If the EXCLUSION and COMMENT columns are exists in file then further process it to count the number of exclusions in it
                                                //And Preparing the REMARK column data to add in new summary file.
                                                if (file.getName().contains(SLAname) && process) {
                                                    while ((row = reader.readLine()) != null) {
                                                        List<String> inputLines = List.of(row.split(","));
                                                        if (inputLines != null && !inputLines.isEmpty() && inputLines.size() == iLineSize) {
                                                            if (inputLines.get((iLineSize - 2)).equalsIgnoreCase("Y")) {
                                                                count++;
                                                                remark = remark + inputLines.get(0) + ":" + inputLines.get(iLineSize - 1) + "|";
                                                            }
                                                        }
                                                    }
                                                    totalReProcessed++;
                                                }

                                                //Reading the main summary file data to subtract the EXCLUSIONS and re-calculating the SLAs stats
                                                minSla = Double.parseDouble(inLine.get(3));
                                                maxSla = Double.parseDouble(inLine.get(2));
                                                num = Double.parseDouble(inLine.get(4));
                                                deno = Double.parseDouble(inLine.get(5)) - count;

                                                //Cheking the count value is not zero, to re-calculate and add updated lines in new main summary file
                                                if (count != 0) {
                                                    if (num == 0 && deno == 0) {
                                                        dataLines.add(new String[]
                                                                {inLine.get(0), inLine.get(1), inLine.get(2), inLine.get(3), inLine.get(4), "0", "100", "Met", remark});
                                                    } else if (deno < 0) {
                                                        message = twoSpace + "there is issue in file " + file + " , denominators count is less than zero" + newLine + " Check your input file";
                                                        status = util.WriteToFile(currentLogFileName, message);
                                                    } else {
                                                        actual = (num / deno) * 100;
                                                        if (actual >= minSla && maxSla > minSla) {
                                                            dataLines.add(new String[]
                                                                    {inLine.get(0), inLine.get(1), inLine.get(2), inLine.get(3), inLine.get(4), String.valueOf(deno), String.valueOf(actual), "Met", remark});
                                                        } else if (actual <= minSla && maxSla < minSla) {
                                                            dataLines.add(new String[]
                                                                    {inLine.get(0), inLine.get(1), inLine.get(2), inLine.get(3), inLine.get(4), String.valueOf(deno), String.valueOf(actual), "Met", remark});
                                                        } else {
                                                            dataLines.add(new String[]
                                                                    {inLine.get(0), inLine.get(1), inLine.get(2), inLine.get(3), inLine.get(4), String.valueOf(deno), String.valueOf(actual), "Not Met", remark});

                                                        }
                                                    }
                                                } else {
                                                    dataLines.add(new String[]
                                                            {inLine.get(0), inLine.get(1), inLine.get(2), inLine.get(3), inLine.get(4), inLine.get(5), inLine.get(6), inLine.get(7), remark});
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            //Writing the new main summary file, with re-calculated data
                            if (dataLines.size() > 0) {
                                String dataFileName = currentProcessingPath + "\\\\finalOutput\\\\" + projectKey + "_" + processingTimeStamp + "_Final.csv";
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

                            filesNotProcessed = totalFiles - totalReProcessed;

                            //Deleting the some of unnecessary directories to make the final result directory more clean with the required result
                            System.out.println("                    Deleting Blank Directories ... ");
                            File dir = new File(currentProcessingOutputPath);
                            File js = new File(currentProcessingJSonPath);
                            File detail = new File(currentProcessingOutputPath + "\\Detail");
                            js.delete();
                            detail.delete();
                            dir.delete();

                        } else {
                            message = twoSpace + "SLA summary report file is not found, Stopping SLA re-processing";
                            System.out.println(message);
                            status = util.WriteToFile(currentLogFileName, message);
                            return;
                        }
                    }

                    System.out.println("T--------------------------------------------------------------");
                    System.out.println("                   Final Re-Processing Report for project: " + projectKey);
                    System.out.println("T--------------------------------------------------------------");
                    System.out.println("Total files found : " + totalFiles);
                    System.out.println("Total files re-processed : " + totalReProcessed);
                    System.out.println("Total files not having exclusions, hence can not be re-processed: " + filesNotProcessed);
                    System.out.println("T--------------------------------------------------------------");

                }
                catch(Exception e){
                    message = "Exception Occured While Re-processing Data: " + e.getMessage();
                    message = message + newLine + "Stopping the re-processing.";
                    message = message + newLine + "----------------------END OF LOG------------------------------------.";
                    status = util.WriteToFile(currentLogFileName, message);
                }
            }
            else {
                List<ProcessedData> processedDataList = new ArrayList<>();
                message = "Starting the SLA Automation Process for Project : " + projectConfiguration.getProjectKey();
                message = message + newLine + "Project Data Source : " + projectConfiguration.getProjectsource();
                message = message + newLine + "Project Log Folder : " + currentProcessingLogPath;

                message = message + newLine + "Project Output Folder : " + currentProcessingOutputPath;
                message = message + newLine + "Project JSon Folder : " + currentProcessingJSonPath;
                message = message + newLine + "Script Execution On : " + processingTimeStamp;
                status = util.WriteToFile(currentLogFileName, message);

                ProjectFactory factory = new ProjectFactory();
                try {
                    IProject project = factory.CreateProject(projectConfiguration.getProjectKey());
                    if (project == null) {
                        message = "exception : Cannot able to create Project Object : " + projectConfiguration.getProjectKey() + ", Stopping the process.";
                        message = message + newLine + "Stopping processing.";
                        status = util.WriteToFile(currentLogFileName, message);
                        return;
                    }

                    List<ProcessedData> finaloutput = new ArrayList<>();
                    ProcessedData data = new ProcessedData();

                    //Pull all the date for the project and then send for processing in the beginning
                    List<Issue> projectIssuesfromJira = new ArrayList<>();
                    if (projectConfiguration.getProjectsource().equals(SourceKey.JIRA.value)) {

                    }

                    int totalBacklogSLAToProcess = 0;
                    int totalnonBacklogSlaToProcess = 0;
                    int totalbacklogProcessed = 0;
                    int totalnonbacklogProcessed = 0;

                    //BackLog Processing
                    if (lstbacklog != null && lstbacklog.size() > 0) {
                        totalBacklogSLAToProcess = lstbacklog.size();
                        System.out.println("Processing BackLog SLA");
                        message = newLine + "Start Processing Backlog SLA";
                        status = util.WriteToFile(currentLogFileName, message);


                        if (projectConfiguration.getProjectsource().equals(SourceKey.JIRA.value)) {
                            boardIssues = null;
                            changeLogURI = projectConfiguration.getProjecturl() + "/api/latest/search?";
                            commentURI = projectConfiguration.getProjecturl() + "/agile/1.0/issue";
                            boardIssues = iJiraDataService.getIssuesUsingBoard(userName, password, projectConfiguration.getProjecturl(), projectConfiguration.getBacklogBoardIds(), true, 1000);
                        }

                        for (MeasureConfiguration backlogitem : lstbacklog) {
                            try {
                                data = project.Process(backlogitem, userName, password, projectConfiguration, boardIssues);
                                if (data == null) {
                                    System.out.println("There are some Error While processing SLA : " + backlogitem.getSlaname() + ", Please check the Log");
                                } else {
                                    finaloutput.add(data);
                                    totalbacklogProcessed++;
                                }
                            } catch (Exception exbackLogSLA) {
                                message = "Error While processing SLA : " + backlogitem.getSlaname();
                                message = message + newLine + "Error : " + exbackLogSLA.getMessage();
                                status = util.WriteToFile(currentLogFileName, message);
                                System.out.println("Error While processing SLA : " + backlogitem.getSlaname() + ", Please check the Log");
                            }
                        }
                    } else {
                        System.out.println("No Backlog SLA to process for the Project : " + projectConfiguration.getProjectKey());
                        message = newLine + "No Backlog SLA to process for the Project : " + projectConfiguration.getProjectKey();
                        status = util.WriteToFile(currentLogFileName, message);
                    }

                    //Non-BackLog Processing
                    if (lstnonBackLog != null && lstnonBackLog.size() > 0) {
                        totalnonBacklogSlaToProcess = lstnonBackLog.size();
                        System.out.println("Processing Non-BackLog SLA");
                        message = newLine + "Start Processing Non-BackLog SLA";
                        status = util.WriteToFile(currentLogFileName, message);

                        if (projectConfiguration.getProjectsource().equals(SourceKey.JIRA.value)) {
                            boardIssues = null;

                            String URI = projectConfiguration.getProjecturl() + "/api/2/search?jql=project=" + projectConfiguration.getProjectKey();

                        }

                        for (MeasureConfiguration nonBacklogItem : lstnonBackLog) {
                            try {
                                data = project.Process(nonBacklogItem, userName, password, projectConfiguration, boardIssues);
                                if (data == null) {
                                    System.out.println("There are some Error While processing SLA : " + nonBacklogItem.getSlaname() + ", Please check the Log");
                                } else {
                                    finaloutput.add(data);
                                    totalnonbacklogProcessed++;
                                }
                            } catch (Exception exnonbackLogSLA) {
                                message = "Error While processing SLA : " + nonBacklogItem.getSlaname();
                                message = message + newLine + "Error : " + exnonbackLogSLA.getMessage();
                                status = util.WriteToFile(currentLogFileName, message);
                                System.out.println("Error While processing SLA : " + nonBacklogItem.getSlaname() + ", Please check the Log");
                            }
                        }
                    } else {
                        System.out.println("No Non-Backlog SLA to process for the Project : " + projectConfiguration.getProjectKey());
                        message = newLine + "No Non-Backlog SLA to process for the Project : " + projectConfiguration.getProjectKey();
                        status = util.WriteToFile(currentLogFileName, message);
                    }

                    //Create final output file
                    List<String[]> dataLines = new ArrayList<>();
                    dataLines.add(new String[] {"","","Automation Data","","","","","","","Actual Value"});
                    dataLines.add(new String[]
                            {"SLA Name", "Type", "Expected Service Level", "Minimum Service Level", "Numerator", "Denominator",
                                    "Actual", "SLA Status", "", "Numerator", "Denominator","Actual", "SLA Status","Comment"});


                    if (finaloutput != null && finaloutput.size() > 0) {
                        for (ProcessedData fData : finaloutput) {
                            //EXCEL
                            if (fData.getSLAKey().equals("AdhereToAgileMethodology")) {
                                dataLines.add(new String[]
                                        {fData.getSLAName(), fData.getSLAType(), fData.getExpectedServiceLevel(),
                                                fData.getMinimumServiceLevel(), "", "",
                                                fData.getActual(), fData.getSlaStatus()});
                            } else if (fData.getSLAKey().equals("IssuesDetectedPostGOLIVE")) {
                                dataLines.add(new String[]
                                        {fData.getSLAName(), fData.getSLAType(), fData.getExpectedServiceLevel(),
                                                fData.getMinimumServiceLevel(), "", fData.getDenCount(),
                                                fData.getActual(), fData.getSlaStatus()});

                            } else {
                                dataLines.add(new String[]
                                        {fData.getSLAName(), fData.getSLAType(), fData.getExpectedServiceLevel(),
                                                fData.getMinimumServiceLevel(), fData.getNumCount(), fData.getDenCount(),
                                                fData.getActual(), fData.getSlaStatus()});
                            }


                        }
                    }

                    String currentfinalDatafilePath = currentProcessingOutputPath + "\\\\" + projectConfiguration.getProjectKey() + "_" + processingTimeStamp + ".csv";
                    if (dataLines != null && dataLines.size() > 0) {
                        boolean status1 = util.WriteToCSv(dataLines, currentfinalDatafilePath);
                    }

                    //Processing Report Printing
                    System.out.println("T--------------------------------------------------------------");
                    System.out.println("                Final Processing Report");
                    System.out.println("T--------------------------------------------------------------");
                    System.out.println("Total Backlog SLA Configured :" + totalBacklogSLAToProcess);
                    System.out.println("Total Backlog SLA Successful:" + totalbacklogProcessed);
                    System.out.println("Total Backlog SLA Failed :" + (totalBacklogSLAToProcess - totalbacklogProcessed));
                    System.out.println("Total Non-Backlog SLA Configured :" + totalnonBacklogSlaToProcess);
                    System.out.println("Total Non-Backlog SLA Successful:" + totalnonbacklogProcessed);
                    System.out.println("Total Non-Backlog SLA Failed :" + (totalnonBacklogSlaToProcess - totalnonbacklogProcessed));
                    System.out.println("T--------------------------------------------------------------");
                } catch (Exception exProjectProcessing) {
                    message = "Exception Occurs : " + exProjectProcessing.getMessage();
                    message = message + newLine + "Stopping processing.";
                    status = util.WriteToFile(currentLogFileName, message);
                }

            }
        }
        catch (Exception ex)
        {
            message = "Exception Occurs : " + ex.getMessage();
            message = message + newLine + "Stopping processing.";
            message = message + newLine + "----------------------END OF LOG------------------------------------.";
            status = util.WriteToFile(currentLogFileName, message);
            throw ex;
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

    public boolean WriteToFile(String fileName, String text)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append("New Line!");
            writer.append(text);
            writer.close();
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    public void CommentedCode()
    {
        List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[]
                { "StoryID", "Original Estimation", "Actual Estimation", "Within Limit" });
        dataLines.add(new String[]
                { "ERPMM-3454", "10", "15", "true" });
        dataLines.add(new String[]
                { "ERPMM-3455", "10", "60", "false" });
        dataLines.add(new String[]
                { "ERPMM-3456", "10", "18", "true" });
        dataLines.add(new String[]
                { "ERPMM-3457", "10", "45", "false" });

        CommonUtil commonUtil = new CommonUtil();
        String fileName = projectConfiguration.getOutputFolder() + "\\\\ERPMM_TESTSLA_25062022.csv";
        boolean a = commonUtil.WriteToCSv(dataLines, fileName);

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        String basePath = projectConfiguration.getOutputFolder();
        String CurrentProcessingPath = basePath + "\\\\" + timeStamp;
        String testFile = CurrentProcessingPath + "\\\\test.txt";
        //Create the directory if not exists
        File directory1 = new File(testFile);
        if (!directory1.exists()) {
            directory1.mkdir();
        }

    }


}

