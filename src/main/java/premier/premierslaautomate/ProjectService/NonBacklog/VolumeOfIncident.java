package premier.premierslaautomate.ProjectService.NonBacklog;

import org.springframework.stereotype.Service;
import premier.premierslaautomate.DataServices.AdoDataService;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IAdoDataService;
import premier.premierslaautomate.Models.ADO.WorkItem;
import premier.premierslaautomate.Models.Issue;
import premier.premierslaautomate.Models.ProcessedData;
import premier.premierslaautomate.Utilities.CommonUtil;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class VolumeOfIncident implements Serializable
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

    public ProcessedData VolumeOfIncidents(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues)
    {
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);

        List<Issue> denoIssue= new ArrayList<>();
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesBaseline = new ArrayList<>();

        float variance = 0f;
        double finalLimitValue = 0.0;
        double actualValue = 0;
        String commentURI = "";
        double baseLine=0;

        // ADO Objects, variables start
        List<WorkItem> workitems = new ArrayList<>();
        String ADQuery ="";
        double baselineAvg = 0;
        double newBaselineAvg = 0;
        int totalIncidents = 0;
        int totalMonths = 0;

        String strbaseLineAvg =  "";
        String strTotalMonths = "";
        double prevBaselineAvg = 0.0;
        double newBaselineAvgPlusVariance = 0.0;
        String resolvedReason = "";

        try
        {
            totaldenoCount = 0;
            totalnumCount = 0;
            message = "";

            strbaseLineAvg =  sla.getConfig1();
            strTotalMonths = sla.getInput1();

            try
            {
                totalMonths = Integer.parseInt(strTotalMonths);
            }
            catch (Exception eparse)
            {
                totalMonths = 3;
            }

            //This is for limit variance
            float limitValue = 0.f;
            String strLimit = sla.getLimit();
            if (!strLimit.isEmpty())
            {
                try
                {
                    limitValue = Float.parseFloat(sla.getLimit());
                }
                catch (Exception exparse)
                {
                    limitValue = 10;
                }
            }
            else
            {
                limitValue = 10;
            }

            expectedsla = Integer.parseInt(sla.getExpectedsla());
            minsla = Integer.parseInt(sla.getMinimumsla());

            if (expectedsla == 0 || minsla ==0)
            {
                //Stop the processing
                message = twoSpace +  "expectedSLA OR Minimum cannot be zero, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String baselineavgQuery = sla.getInput2();
            if (baselineavgQuery.isEmpty())
            {
                message = twoSpace +  "Base Query not provided to calculate baseline average, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            //Baseline Average file name
            String baselineFileNamePath = project.getAutomationData() + "\\\\" + project.getProjectKey() + "_" + sla.getSlakey() + "_BaselineAvg.csv";
            String baselineFileName = project.getProjectKey() + "_" + sla.getSlakey() + "_BaselineAvg.csv";
            LocalDate baselineFirstDate =    YearMonth.now().minusMonths(3).atDay(1);
            LocalDate baseLineLastDate =    YearMonth.now().minusMonths(1).atEndOfMonth();



            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (sla.getDenojql().isEmpty())
                {
                    message = twoSpace +  "Deno Query not found, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                ADQuery = "{\n" +
                        "  \"query\": \"" + baselineavgQuery + " And  [Microsoft.VSTS.Common.ClosedDate] >= '" + baselineFirstDate.toString() +"' And  [Microsoft.VSTS.Common.ClosedDate] <= '" + baseLineLastDate.toString() + "'" + "\"\n" + "}";

                if(strbaseLineAvg != null && !strbaseLineAvg.isEmpty())
                {
                    prevBaselineAvg = Double.parseDouble(strbaseLineAvg);
                }
                else
                {
                    if(baselineFileNamePath !=null)
                    {
                        try
                        {
                            String line= "";
                            BufferedReader reader = new BufferedReader(new FileReader(baselineFileNamePath));
                            while ((line= reader.readLine()) != null) {
                                List<String> baseLines= List.of(line.split(","));
                                if (baseLines != null && baseLines.size() > 0)
                                {
                                    strbaseLineAvg = baseLines.get(0);
                                    if (!strbaseLineAvg.isEmpty())
                                    {
                                        try
                                        {
                                            prevBaselineAvg = Double.parseDouble(strbaseLineAvg);
                                        }
                                        catch (Exception exparse)
                                        {

                                        }
                                    }
                                }
                            }
                        }
                        catch(Exception e)
                        {
                        }
                    }
                }

                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type", "Status", "Severity"});
                }

                String searchWorkitemLinkURI = project.getLinkItemUrl();
                String searchItemURI = project.getItemUrl();

                //Get the previous rolling month data to calculate the new baseline
                workitems  = iAdoDataService.getWorkitems(userName,password,project.getProjecturl(),ADQuery,"POST",false,false,100);

                //Calculate the new baseline
                if (workitems !=null && workitems.size()>0)
                {
                    totalIncidents = workitems.size();
                    newBaselineAvg = Math.round((double)totalIncidents / (double)totalMonths);

                    if (newBaselineAvg < prevBaselineAvg)
                    {
                        message += newLine + twoSpace + " New Base line Identified : = " + newBaselineAvg;
                    }
                    else
                    {
                        newBaselineAvg = prevBaselineAvg;
                        message += newLine + twoSpace + " No Baseline change : = " + newBaselineAvg;
                    }
                }

                double varianceValue = newBaselineAvg * limitValue/100;
                newBaselineAvgPlusVariance = newBaselineAvg + varianceValue; //This is the Minimum SLA

                //Execute the Deno Query to get the current volume
                ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                workitems = null;
                workitems   = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", false, true, true, true,"'User Story'", searchItemURI, searchWorkitemLinkURI, 1);

                //The total data returned is the actual value
                if (workitems != null && workitems.size() > 0)
                {
                    //Also write this workitems to the data file
                    for (WorkItem wi:workitems)
                    {
                        resolvedReason = wi.getFields().getResolvedReason();
                        if(resolvedReason != null )
                        {
                            if(resolvedReason.equalsIgnoreCase("Code Change")) {
                                actualValue++;
                                dataLines.add(new String[]
                                        {wi.getId(), wi.getFields().getWorkItemType(), wi.getFields().getState(), wi.getFields().getSeverity()});
                            }
                        }

                    }
                }

                if (project.getDatafileRequired().equals("Y"))
                {
                    dataLines.add(new String[]
                            {"", "", "", ""});
                    dataLines.add(new String[]
                            {"Baseline Average for Next Month Calculation : " + String.valueOf(newBaselineAvg), "", "", ""});
                }

                minsla = newBaselineAvgPlusVariance;
                message += newLine + twoSpace + " Baseline avg with variance = " + newBaselineAvgPlusVariance;

                if(actualValue <= minsla)
                {
                    slaStatus = "Met";
                }
                else
                {
                    slaStatus = "Not Met";
                }

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

                //Save the file
                boolean isdeleted = util.DeleteFile(baselineFileNamePath);
                if (isdeleted = true)
                {
                    dataLinesBaseline.add(new String[]
                            {String.valueOf(newBaselineAvg)});

                    try
                    {
                        boolean csvStatus = util.WriteToCSv(dataLinesBaseline, baselineFileNamePath);
                        if (csvStatus == true)
                        {
                            message += newLine + twoSpace + " Baseline Average file Created successfully - " + baselineFileNamePath;
                        }
                        else
                        {
                            message += newLine + twoSpace + "Unable to create the Baseline Average file";
                        }
                    }
                    catch (Exception exCsv)
                    {
                        message += newLine + twoSpace + "Unable to create the Baseline Average file, Error: " + exCsv.getMessage();
                    }
                }


                message += newLine + twoSpace + " AdoQuery = " + ADQuery;
                message += newLine + twoSpace + " Actual Value = " + String.valueOf(actualValue);
                message += newLine + twoSpace + " Status = " + slaStatus;

                //Change the SLA min limit with new baseline
                sla.setMinimumsla(String.valueOf(newBaselineAvg));
                ProcessedData data = util.BuildProcessData(sla,(float)actualValue,slaStatus,"", "",ADQuery);
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

