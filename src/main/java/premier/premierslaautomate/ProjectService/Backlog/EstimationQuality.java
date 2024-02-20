package premier.premierslaautomate.ProjectService.Backlog;

import org.springframework.stereotype.Service;
import premier.premierslaautomate.DataServices.AdoDataService;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IAdoDataService;
import premier.premierslaautomate.Models.ADO.SlaResult;
import premier.premierslaautomate.Models.ADO.UserVariables;
import premier.premierslaautomate.Models.ADO.WorkItem;
import premier.premierslaautomate.Models.Issue;
import premier.premierslaautomate.Models.ProcessedData;
import premier.premierslaautomate.Utilities.CommonUtil;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class EstimationQuality implements Serializable
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

    public ProcessedData estimationQuality(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        //Variable Declaration
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<WorkItem> workitems=new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetails = new ArrayList<>();
        float originalEstimation = 0f;//Get it from the field
        float actualEstimation = 0f;//Get it from the field
        float variance = 0f;
        float orginalEstimationSUM = 0f;
        float actualEstimationSUM = 0f;
        double actualValue = 0;
        int pageSize = 1000;
        String strPageSize = "";
        boolean eligibletoWrite = false;
        int totalNotestimated = 0;
        float limitValue = 0.f;
        String ADQuery="";

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

//            if (sla.getLimit() != null) {
//                limitValue = Float.parseFloat(sla.getLimit());
//            }
//
//            else {
//                limitValue = Float.parseFloat("15");
//            }

            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }


            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"StoryID", "Original Estimation", "Actual Estimation", "Variance", "Status"});
                }

                if (project.getDetailedLogRequired().equals("Y")) {
                    dataLinesDetails.add(new String[]
                            {"StoryID", "Original Estimation", "Actual Estimation", "Variance", "Status"});
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

                totaldenoCount = 0;
                totalnumCount = 0;
                message = "";
                if(workitems!=null&& workitems.size()>0)
                {


                    for (WorkItem witem : workitems)
                    {
                        eligibletoWrite = false;
                        originalEstimation = 0f;//Get it from the field
                        actualEstimation = 0f;//Get it from the field
                        variance = 0f;
                        String issueStatus = "";

                        if (witem != null)
                        {
                            if (witem.getFields().getOriginalEffort() == 0 )
                            {
                                originalEstimation =0;
                            }

                            if (witem.getFields().getOriginalEffort() != 0 )
                            {
                                try {
                                    originalEstimation = (float) witem.getFields().getOriginalEffort();
                                    orginalEstimationSUM += originalEstimation;
                                }
                                catch (Exception exOriginalEstimation)
                                {

                                }
                            }

                            if (witem.getFields().getActualEffortinHours() != 0 )
                            {
                                actualEstimation =0;
                            }
                            if (witem.getFields().getActualEffortinHours() != 0 ) {
                                try {
                                    actualEstimation = (float) witem.getFields().getActualEffortinHours();
                                    actualEstimationSUM += actualEstimation;
                                }
                                catch (Exception exActualEstimaiton)
                                {

                                }
                            }

                            //if original estimation  =0 and actual estimation has a value then
                            //there are some stories where there is no original estimaiton but have actual estimation
                            //then make the original estimation = actual estimation


                            //Write the data Line
                            if (project.getDatafileRequired().equals("Y")) {
                                //This should be your deno count

                                dataLines.add(new String[]
                                        {witem.getId(), String.valueOf(originalEstimation), String.valueOf(actualEstimation), String.valueOf(variance), String.valueOf(issueStatus)});

                            }

                            if (project.getDetailedLogRequired().equals("Y")) {
                                dataLinesDetails.add(new String[]
                                        {witem.getId(), String.valueOf(originalEstimation), String.valueOf(actualEstimation),String.valueOf(variance), String.valueOf(issueStatus)});
                            }

                        }


                    }

                    if(orginalEstimationSUM != 0 && actualEstimationSUM !=0)
                    {
                        totalnumCount = (int) actualEstimationSUM;
                        totaldenoCount = (int) orginalEstimationSUM;
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

                if (dataLinesDetails.size() > 0) {
                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
                    try {
                        boolean csvStatus = util.WriteToCSv(dataLinesDetails, dataFileName1);
                        if (csvStatus == true) {
                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + dataFileName1;
                        } else {
                            message += newLine + twoSpace + "Unable to create the data file";
                        }
                    } catch (Exception exCsv) {
                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
                    }
                }

                //Business Logic - End

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
                    slaStatus = util.EstimationQualitySlaStatus(actualValue, (double)expectedsla, (double)minsla);
                    message += newLine + twoSpace + " Status = " + slaStatus;
                }

                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),ADQuery);
                status = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
            throw ex;
        }
    }

    public SlaResult estimationQuality(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues, String src, UserVariables userVariables) {
        sla.setDenojql("Select * From WorkItems  Where [System.WorkItemType] = ('User Story','Spike','Feature') " +
                "And [System.TeamProject] = '" + userVariables.getTeamProject() +"'"+
                "And [System.AreaPath] in "+ userVariables.getAreaPathBL() +"And [System.State] ever 'Accepted'"+" And [System.IterationPath] in " + userVariables.getIterationPathFormat() );

        //Variable Declaration
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<WorkItem> workitems=new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetails = new ArrayList<>();
        float originalEstimation = 0f;//Get it from the field
        float actualEstimation = 0f;//Get it from the field
        float variance = 0f;
        float orginalEstimationSUM = 0f;
        float actualEstimationSUM = 0f;
        double actualValue = 0;
        int pageSize = 1000;
        String strPageSize = "";
        boolean eligibletoWrite = false;
        int totalNotestimated = 0;
        float limitValue = 0.f;
        String ADQuery="";

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

//            if (sla.getLimit() != null) {
//                limitValue = Float.parseFloat(sla.getLimit());
//            }
//
//            else {
//                limitValue = Float.parseFloat("15");
//            }

            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla == 0) {
                //Stop the processing
                message = twoSpace + "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }


            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"StoryID", "Original Estimation", "Actual Estimation", "Variance", "Status"});
                }

                if (project.getDetailedLogRequired().equals("Y")) {
                    dataLinesDetails.add(new String[]
                            {"StoryID", "Original Estimation", "Actual Estimation", "Variance", "Status"});
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

                totaldenoCount = 0;
                totalnumCount = 0;
                message = "";
                if(workitems!=null&& workitems.size()>0)
                {


                    for (WorkItem witem : workitems)
                    {
                        eligibletoWrite = false;
                        originalEstimation = 0f;//Get it from the field
                        actualEstimation = 0f;//Get it from the field
                        variance = 0f;
                        String issueStatus = "";

                        if (witem != null)
                        {
                            if (witem.getFields().getOriginalEffort() == 0 )
                            {
                                originalEstimation =0;
                            }

                            if (witem.getFields().getOriginalEffort() != 0 )
                            {
                                try {
                                    originalEstimation = (float) witem.getFields().getOriginalEffort();
                                    orginalEstimationSUM += originalEstimation;
                                }
                                catch (Exception exOriginalEstimation)
                                {

                                }
                            }

                            if (witem.getFields().getActualEffortinHours() != 0 )
                            {
                                actualEstimation =0;
                            }
                            if (witem.getFields().getActualEffortinHours() != 0 ) {
                                try {
                                    actualEstimation = (float) witem.getFields().getActualEffortinHours();
                                    actualEstimationSUM += actualEstimation;
                                }
                                catch (Exception exActualEstimaiton)
                                {

                                }
                            }

                            //if original estimation  =0 and actual estimation has a value then
                            //there are some stories where there is no original estimaiton but have actual estimation
                            //then make the original estimation = actual estimation


                            //Write the data Line
                            if (project.getDatafileRequired().equals("Y")) {
                                //This should be your deno count

                                dataLines.add(new String[]
                                        {witem.getId(), String.valueOf(originalEstimation), String.valueOf(actualEstimation), String.valueOf(variance), String.valueOf(issueStatus)});

                            }

                            if (project.getDetailedLogRequired().equals("Y")) {
                                dataLinesDetails.add(new String[]
                                        {witem.getId(), String.valueOf(originalEstimation), String.valueOf(actualEstimation),String.valueOf(variance), String.valueOf(issueStatus)});
                            }

                        }


                    }

                    if(orginalEstimationSUM != 0 && actualEstimationSUM !=0)
                    {
                        totalnumCount = (int) actualEstimationSUM;
                        totaldenoCount = (int) orginalEstimationSUM;
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

                if (dataLinesDetails.size() > 0) {
                    String dataFileName1 = project.getOutputPath() + "\\\\Detail\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_Detailed_data.csv";
                    try {
                        boolean csvStatus = util.WriteToCSv(dataLinesDetails, dataFileName1);
                        if (csvStatus == true) {
                            message += newLine + twoSpace + " Detailed Data file Created successfully - " + dataFileName1;
                        } else {
                            message += newLine + twoSpace + "Unable to create the data file";
                        }
                    } catch (Exception exCsv) {
                        message += newLine + twoSpace + "Unable to create the data file, Error: " + exCsv.getMessage();
                    }
                }

                //Business Logic - End

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
                    slaStatus = util.EstimationQualitySlaStatus(actualValue, (double)expectedsla, (double)minsla);
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
            ex.printStackTrace();
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
            throw ex;
        }
    }


}
