package premier.premierslaautomate.ProjectService.Backlog;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import premier.premierslaautomate.DataServices.AdoDataService;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IAdoDataService;
import premier.premierslaautomate.Models.ADO.*;
import premier.premierslaautomate.Models.Issue;
import premier.premierslaautomate.Models.ProcessedData;
import premier.premierslaautomate.Utilities.CommonUtil;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class PercentageOfTestsAutomated implements Serializable
{
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
    List<String[]> dataLines = new ArrayList<>();
    List<String[]> dataLinesDetailed = new ArrayList<>();

    public ProcessedData PercentageOfTestsAutomated(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        int pageSize = 1000;
        String denoQuery = " ";
        String denoQuery1 = " ";
        String strPageSize = "";
        double actualValue = 0;
        //String testPlanIdList = "";
        List<Value> testPlanIdList = new ArrayList<>();
        List<Value> testPlanIdList2 = new ArrayList<>();


        try {

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

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"StoryID", "Title","Outcome"});
                }

                if(project.getDetailedLogRequired().equals("Y"))
                {
                    dataLinesDetailed.add(new String[]
                            {
                                    "StoryID", "Title","Outcome"
                            });
                }

                // testPlanIdList []  = project.getTestSuitId();
                if (testPlanIdList.equals("")) {
                    message = twoSpace + "Status for close for the issue not found. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                String automationStatus ="";

                denoQuery = project.getTestPlanUrl() + '/' + project.getTestPlanId() + '/' + "suites" + '/' + project.getTestSuiteId() + '/' + "TestPoint?api-version=6.0" ;
                TestPlan testPlan = iAdoDataService.getTestPlan(userName,password,denoQuery);
                if(!testPlan.equals(null)){
                    testPlanIdList2 = testPlan.getValue();
                }

                totalnumCount =0;
                totaldenoCount = testPlanIdList2.size();
                for(Value value : testPlanIdList2)
                {

                    try
                    {
                        denoQuery1 =  project.getTestPlanUrl() + '/' + project.getTestPlanId() + '/' + "suites" + '/' + project.getTestSuiteId()  + '/' + "TestCase" + '/' + value.getTestCaseReference().getId() + "?api-version=6.0 " ;
                        TestPlan testPlan1 = iAdoDataService.getTestPlan(userName,password,denoQuery1);
                        testPlanIdList = testPlan1.getValue();


                        for (Value value1 : testPlanIdList)
                        {
                            if(value1.getWorkItem()!=null)
                            {
                                if(!CollectionUtils.isEmpty(value1.getWorkItem().getWorkItemFields()))
                                {
                                    automationStatus = value1.getWorkItem().getWorkItemFields()
                                            .get(0).get("Microsoft.VSTS.TCM.AutomationStatus").asText();
                                    if(automationStatus.equals("Automated")){
                                        totalnumCount++;
                                    }

                                }
                            }
                        }

                        if (project.getDatafileRequired().equals("Y"))
                        {
                            dataLines.add(new String[]{
                                    String.valueOf(value.getTestCaseReference().getId()),value.getTestCaseReference().getName(),automationStatus
                            });
                        }

                    }

                    catch (Exception e) {

                    }

                }

                message = twoSpace + " Total Deno Count = " + totaldenoCount;
                message += newLine + twoSpace + " Total Num Count = " + totalnumCount;
                message += newLine + twoSpace + " AdoQuery = " + denoQuery;

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

                if(dataLinesDetailed.size()>0)
                {
                    String detailLogFilePath = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.log";
                    try
                    {
                        boolean csvStatus = util.WriteToCSv(dataLinesDetailed,detailLogFilePath);
                        if (csvStatus == true)
                        {
                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + detailLogFilePath;
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

                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                if (totaldenoCount == 0 && totalnumCount == 0) {
                    totaldenoCount = 1;
                    totalnumCount = 1;
                } else if (totaldenoCount == 0) {
                    totaldenoCount = 1;
                }

                //As we got all the data, calculate the SLA Status
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

                //Create the return object
                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),denoQuery);
                status = util.WriteToFile(project.getLogFile(), message);
                return data;
            }
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }
        return null;
    }

    public SlaResult PercentageOfTestsAutomated(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues, String src, UserVariables userVariables) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        int pageSize = 1000;
        String denoQuery = " ";
        String denoQuery1 = " ";
        String strPageSize = "";
        double actualValue = 0;
        //String testPlanIdList = "";
        List<Value> testPlanIdList = new ArrayList<>();
        List<Value> testPlanIdList2 = new ArrayList<>();


        try {

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

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"StoryID", "Title","Outcome"});
                }

                if(project.getDetailedLogRequired().equals("Y"))
                {
                    dataLinesDetailed.add(new String[]
                            {
                                    "StoryID", "Title","Outcome"
                            });
                }

                // testPlanIdList []  = project.getTestSuitId();
                if (testPlanIdList.equals("")) {
                    message = twoSpace + "Status for close for the issue not found. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                String automationStatus ="";

                denoQuery = project.getTestPlanUrl() + '/' + userVariables.getTestPlanId() + '/' + "suites" + '/' + userVariables.getTestSuitId() + '/' + "TestPoint?api-version=6.0" ;
                TestPlan testPlan = iAdoDataService.getTestPlan(userName,password,denoQuery);
                if(!testPlan.equals(null)){
                    testPlanIdList2 = testPlan.getValue();
                }

                totalnumCount =0;
                totaldenoCount = testPlanIdList2.size();
                for(Value value : testPlanIdList2)
                {

                    try
                    {
                        denoQuery1 =  project.getTestPlanUrl() + '/' + userVariables.getTestPlanId() + '/' + "suites" + '/' + userVariables.getTestSuitId()  + '/' + "TestCase" + '/' + value.getTestCaseReference().getId() + "?api-version=6.0 " ;
                        TestPlan testPlan1 = iAdoDataService.getTestPlan(userName,password,denoQuery1);
                        testPlanIdList = testPlan1.getValue();


                        for (Value value1 : testPlanIdList)
                        {
                            if(value1.getWorkItem()!=null)
                            {
                                if(!CollectionUtils.isEmpty(value1.getWorkItem().getWorkItemFields()))
                                {
                                    automationStatus = value1.getWorkItem().getWorkItemFields()
                                            .get(0).get("Microsoft.VSTS.TCM.AutomationStatus").asText();
                                    if(automationStatus.equals("Automated")){
                                        totalnumCount++;
                                    }

                                }
                            }
                        }

                        if (project.getDatafileRequired().equals("Y"))
                        {
                            dataLines.add(new String[]{
                                    String.valueOf(value.getTestCaseReference().getId()),value.getTestCaseReference().getName(),automationStatus
                            });
                        }

                    }

                    catch (Exception e) {

                    }

                }

                message = twoSpace + " Total Deno Count = " + totaldenoCount;
                message += newLine + twoSpace + " Total Num Count = " + totalnumCount;
                message += newLine + twoSpace + " AdoQuery = " + denoQuery;

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

                if(dataLinesDetailed.size()>0)
                {
                    String detailLogFilePath = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.log";
                    try
                    {
                        boolean csvStatus = util.WriteToCSv(dataLinesDetailed,detailLogFilePath);
                        if (csvStatus == true)
                        {
                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + detailLogFilePath;
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

                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                if (totaldenoCount == 0 && totalnumCount == 0) {
                    totaldenoCount = 1;
                    totalnumCount = 1;
                } else if (totaldenoCount == 0) {
                    totaldenoCount = 1;
                }

                //As we got all the data, calculate the SLA Status
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
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }
        return null;
    }
}
