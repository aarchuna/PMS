package premier.premierslaautomate.ProjectService.Backlog;

import org.springframework.stereotype.Service;
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
public class ProductPercentTestingCompleted implements Serializable
{
    //CommonVariables
    private IAdoDataService iAdoDataService = new AdoDataService();

    private SlaResult  slaResult = new SlaResult();

    //Test for Arun
    int totalnumCount = 0;
    int totaldenoCount = 0;
    int totalNumCountSatisfied = 0;
    int totalNumCountNotSatisfied = 0;
    double expectedsla = 0;
    double minsla = 0;
    String slaStatus = "";
    String message = "";
    String tabKey = "\t";
    String twoSpace = "  ";
    String newLine = "\r\n";
    boolean status = false;
    CommonUtil util = new CommonUtil();

    public ProcessedData ProductPercentTestingCompleted(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        totalNumCountSatisfied = 0;
        totalNumCountNotSatisfied = 0;
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();
        int pageSize = 1000;
        double actualValue = 0;
        //ADO Variables
        String denoQuery = "";
        String testPlan = "";
        String testSuite ="";
        List<WorkItem> denoWorkItem = new ArrayList<>();
        List<Value> testPlanIdList = new ArrayList<>();
        List<Value> testSuiteList = new ArrayList<>();

        try
        {
            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
        }
        catch (Exception exsla)
        {
            message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
            status = util.WriteToFile(project.getLogFile(), message);
            return null;
        }

        if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
        {
            message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
            status = util.WriteToFile(project.getLogFile(), message);
            return null;
        }

        try {

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {

                if (project.getDatafileRequired().equals("Y"))
                {
                    dataLines.add(new String[]
                            {"StoryID", "Title","Outcome"});
                }

                if(project.getDetailedLogRequired().equals("Y"))
                {
                    dataLinesDetailed.add(new String[]{
                            "StoryID", "Title","Outcome"
                    });
                }

                //Preparing num & deno queries
                denoQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                String searchWorkitemLinkURI = project.getLinkItemUrl();
                String searchItemURI = project.getItemUrl();

                if (denoQuery != "")
                {
                    denoWorkItem = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), denoQuery, "POST", false, true, true, true,"'User Story'", searchItemURI, searchWorkitemLinkURI, 100);
                }
                else
                {
                    message = twoSpace + "denoQuery is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                totalnumCount = 0;
                totaldenoCount = 0;
                message = "";

                if (denoWorkItem != null && denoWorkItem.size() > 0) {
                    for (WorkItem witem : denoWorkItem) {
                        if (witem != null) {
                            if (witem.getChildLinks() != null && witem.getChildLinks().size() > 0) {
                                List<WorkItem> childs = witem.getChildLinks();
                                for (WorkItem child : childs) {
                                    if (child.getFields().getWorkItemType().equals("Test Plan")) {

                                        testPlan = project.getTestPlanUrl() + '/'+ child.getId() +  '/' + "suites?api-version=6.0";
                                        TestPlan testPlan1 = iAdoDataService.getTestPlan(userName,password,testPlan);
                                        testPlanIdList = testPlan1.getValue();

                                        for(Value value : testPlanIdList)
                                        {
                                            testSuite = project.getTestPlanUrl() + '/'+ child.getId() +  '/' + "suites" +  '/' + value.getId()+  '/' + "TestPoint?api-version=6.0"  ;
                                            TestPlan testPlan2 = iAdoDataService.getTestPlan(userName,password,testSuite);
                                            testSuiteList = testPlan2.getValue();
                                            totaldenoCount += testSuiteList.size();

                                            for(Value value1 : testSuiteList)
                                            {
                                                if(project.getDatafileRequired().equals("Y"))
                                                {
                                                    dataLines.add(new String[]{
                                                            String.valueOf(value1.getId()), value1.getTestCaseReference().getName(),value1.getResults().getOutcome()
                                                    });
                                                }
                                                if(value1.getResults().getOutcome().equalsIgnoreCase("passed") || value1.getResults().getOutcome().equalsIgnoreCase("failed") )
                                                {
                                                    totalnumCount++;

                                                }
                                            }

                                        }



                                    }
                                }
                            }

                        }

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
                //Create the return object
                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),denoQuery);
                status = util.WriteToFile(project.getLogFile(), message);
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

    public SlaResult ProductPercentTestingCompleted(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues, String src, UserVariables userVariables) {
        sla.setDenojql("Select * From WorkItems  Where  [System.WorkItemType] = 'ProdRelease'  " +
                "And [System.TeamProject] = '" + userVariables.getTeamProject() +"'"+
                "And ([System.AreaPath] in "+ userVariables.getAreaPathBL() +
                "And [System.State] in ('Closed')"+
               "[Microsoft.VSTS.Common.ClosedDate] >='" +userVariables.getFrom() + "' " +
                "And [Microsoft.VSTS.Common.ClosedDate] <= '" +userVariables.getTo());

        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        totalNumCountSatisfied = 0;
        totalNumCountNotSatisfied = 0;
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();
        int pageSize = 1000;
        double actualValue = 0;
        //ADO Variables
        String denoQuery = "";
        String testPlan = "";
        String testSuite ="";
        List<WorkItem> denoWorkItem = new ArrayList<>();
        List<Value> testPlanIdList = new ArrayList<>();
        List<Value> testSuiteList = new ArrayList<>();

        try
        {
            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
        }
        catch (Exception exsla)
        {
            message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
            status = util.WriteToFile(project.getLogFile(), message);
            return null;
        }

        if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
        {
            message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
            status = util.WriteToFile(project.getLogFile(), message);
            return null;
        }

        try {

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {

                if (project.getDatafileRequired().equals("Y"))
                {
                    dataLines.add(new String[]
                            {"StoryID", "Title","Outcome"});
                }

                if(project.getDetailedLogRequired().equals("Y"))
                {
                    dataLinesDetailed.add(new String[]{
                            "StoryID", "Title","Outcome"
                    });
                }

                //Preparing num & deno queries
                denoQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                String searchWorkitemLinkURI = project.getLinkItemUrl();
                String searchItemURI = project.getItemUrl();

                if (denoQuery != "")
                {
                    denoWorkItem = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), denoQuery, "POST", false, true, true, true,"'User Story'", searchItemURI, searchWorkitemLinkURI, 100);
                }
                else
                {
                    message = twoSpace + "denoQuery is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                totalnumCount = 0;
                totaldenoCount = 0;
                message = "";

                if (denoWorkItem != null && denoWorkItem.size() > 0) {
                    for (WorkItem witem : denoWorkItem) {
                        if (witem != null) {
                            if (witem.getChildLinks() != null && witem.getChildLinks().size() > 0) {
                                List<WorkItem> childs = witem.getChildLinks();
                                for (WorkItem child : childs) {
                                    if (child.getFields().getWorkItemType().equals("Test Plan")) {

                                        testPlan = project.getTestPlanUrl() + '/'+ child.getId() +  '/' + "suites?api-version=6.0";
                                        TestPlan testPlan1 = iAdoDataService.getTestPlan(userName,password,testPlan);
                                        testPlanIdList = testPlan1.getValue();

                                        for(Value value : testPlanIdList)
                                        {
                                            testSuite = project.getTestPlanUrl() + '/'+ child.getId() +  '/' + "suites" +  '/' + value.getId()+  '/' + "TestPoint?api-version=6.0"  ;
                                            TestPlan testPlan2 = iAdoDataService.getTestPlan(userName,password,testSuite);
                                            testSuiteList = testPlan2.getValue();
                                            totaldenoCount += testSuiteList.size();

                                            for(Value value1 : testSuiteList)
                                            {
                                                if(project.getDatafileRequired().equals("Y"))
                                                {
                                                    dataLines.add(new String[]{
                                                            String.valueOf(value1.getId()), value1.getTestCaseReference().getName(),value1.getResults().getOutcome()
                                                    });
                                                }
                                                if(value1.getResults().getOutcome().equalsIgnoreCase("passed") || value1.getResults().getOutcome().equalsIgnoreCase("failed") )
                                                {
                                                    totalnumCount++;

                                                }
                                            }

                                        }



                                    }
                                }
                            }

                        }

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
                //Create the return object

            }

            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;
    }

}
