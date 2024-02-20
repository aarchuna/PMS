package premier.premierslaautomate.ProjectService.Backlog;

import org.springframework.stereotype.Service;
import premier.premierslaautomate.DataServices.AdoDataService;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IAdoDataService;
import premier.premierslaautomate.Models.ADO.RevisionValue;
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
public class BacklogItemQuality implements Serializable
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


    public SlaResult backlogItemQuality(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues, String src, UserVariables userVariables) {
        sla.setDenojql("Select * From WorkItems  Where [System.WorkItemType] = ('User Story','Spike','Feature') " +
                "And [System.TeamProject] = '" + userVariables.getTeamProject() +"'"+
                "And [System.AreaPath] in "+ userVariables.getAreaPathBL() +" And [System.IterationPath] in " + userVariables.getIterationPathFormat() );
        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<WorkItem> workitem= new ArrayList<>();
        String strPageSize = "";
        int pageSize = 1000;
        double actualValue = 0;
        String closedStatus = "";

        try
        {
            totaldenoCount = 0;
            totalnumCount = 0;

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

            closedStatus = sla.getConfig5().replace("'", "");

            if (closedStatus.equals("")) {
                message = twoSpace + "Status for the Issue from which the FTR Check done not found, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            String[] arrClosedStatus = closedStatus.split(",");
            if (arrClosedStatus != null && arrClosedStatus.length > 2) {
                message = twoSpace + "Only Two status(s) can be mentioned for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (arrClosedStatus != null && arrClosedStatus.length >= 1) {
                closedStatus = arrClosedStatus[0];
            }

            if (closedStatus.isEmpty()) {
                message = twoSpace + "Closed Status for the issue not found. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }

            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {

                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"IssueId", "Type", "Issue Status", "Resolved Count", "Is Spill Over"});
                }
                if (project.getDetailedLogRequired().equals("Y"))
                {
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type"});
                }
                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                String ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";
                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "Denominator Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                }

                String searchItemURI =project.getLinkItemUrl();
                String searchWorkitemLinkURI =project.getItemUrl();
                String issueDefectType=sla.getConfig5();
                workitem = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", true, true, true, true, issueDefectType , searchWorkitemLinkURI, searchItemURI, 1000);

                if (workitem != null && workitem.size() > 0) {

                    for (WorkItem witem : workitem) {
                        totaldenoCount = workitem.size();
                        if (project.getDetailedLogRequired().equals("Y"))
                        {
                            dataLinesDetailed.add(new String[]
                                    {witem.getId(), witem.getFields().getWorkItemType() });
                        }
                        if (witem != null) {
                            boolean isConditionSatisfied = false; //FTR. If true Not FTR

                            //to get deno count

                            List<RevisionValue> revisionlst = new ArrayList<>();
                            int resolvedCount = 0;

                            if (witem.getRevisions() != null && witem.getRevisions().size() > 0) {


                                revisionlst = witem.getRevisions();

                                for (RevisionValue revision : revisionlst) {
                                    if (revision != null){
                                        if (revision.getFields().getState().equals(closedStatus) )
                                        {
                                            resolvedCount++;
                                        }
                                    }
                                }
                                if (resolvedCount == 1) {
                                    totalnumCount++;
                                    if (project.getDatafileRequired().equals("Y")) {
                                        dataLines.add(new String[]
                                                {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getState(), String.valueOf(resolvedCount)});
                                    }


                                }
                            }

                            resolvedCount = 0;
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

                if(dataLinesDetailed.size() > 0)
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

                double denoCountActual = totaldenoCount; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                message = twoSpace + " AdoQuery = " + ADQuery;

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
