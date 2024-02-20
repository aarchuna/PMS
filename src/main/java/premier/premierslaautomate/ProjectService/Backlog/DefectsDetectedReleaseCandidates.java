package premier.premierslaautomate.ProjectService.Backlog;

import org.springframework.stereotype.Service;
import premier.premierslaautomate.DataServices.AdoDataService;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IAdoDataService;
import premier.premierslaautomate.Models.ADO.SlaResult;
import premier.premierslaautomate.Models.ADO.UserVariables;
import premier.premierslaautomate.Models.ADO.WorkItem;
import premier.premierslaautomate.Models.Issue;
import premier.premierslaautomate.Models.Issuelinks;
import premier.premierslaautomate.Models.ProcessedData;
import premier.premierslaautomate.Utilities.CommonUtil;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefectsDetectedReleaseCandidates implements Serializable
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
    String twoSpace = "  ";
    String newLine = "\r\n";
    boolean status = false;
    CommonUtil util = new CommonUtil();
    List<String[]> dataLinesDetailed = new ArrayList<>();

    public ProcessedData DefectDetectedInUAT(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<WorkItem> workitems= new ArrayList<>();
        List<String[]> dataLines1 = new ArrayList<>();
        String ADQuery="";
        double actualValue = 0;

        int pageSize = 1000;
        String strPageSize = "";
        String issueDefectType = "";
        String searchItemURI="";
        String searchWorkitemLinkURI="";
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

            strPageSize = project.getPageSize();

            if (!strPageSize.isEmpty()) {
                try {
                    pageSize = Integer.parseInt(strPageSize);
                } catch (Exception exPageSizeParse) {
                    pageSize = 1000;
                }
            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {

                if(project.getDatafileRequired().equals("Y"))
                {
                    dataLines1.add(new String[]
                            {"Key", "Type", "UAT_Defect KEY", "UAT_Defect Date"});
                }
                issueDefectType=sla.getConfig1();
                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                searchItemURI = project.getItemUrl();
                searchWorkitemLinkURI = project.getLinkItemUrl();
                workitems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", false, true, false, true, issueDefectType , searchItemURI,searchWorkitemLinkURI,  1000);
                totaldenoCount = 0;
                totalnumCount = 0;
                List<WorkItem> issuelinks= new ArrayList<>();
                message = "";

               if(workitems!=null&& workitems.size()>0)
                {
                    for (WorkItem witem : workitems)
                    {
                        if (witem.getChildLinks() != null && witem.getChildLinks().size() > 0 )
                        {
                                issuelinks = witem.getChildLinks();
                                for (WorkItem witems : issuelinks) {
                                    if(witems.getFields().getWorkItemType().equals("Incident") || witems.getFields().getWorkItemType().equals("Problem") || witems.getFields().getWorkItemType().equals("User Story"))
                                    {
                                        totaldenoCount++;
                                    }
                                    if (witems.getFields().getServiceLevelType() != null && (witems.getFields().getWorkItemType() != null)) {
                                        if (witems.getFields().getServiceLevelType().equals("UAT") || (witems.getFields().getWorkItemType().equalsIgnoreCase("Defect"))) {
                                            totalnumCount++;
                                            if (project.getDatafileRequired().equals("Y")) {
                                                dataLines1.add(new String[]
                                                        {witem.getId(), witem.getFields().getWorkItemType(), witems.getId(), witems.getFields().getChangedDate()});
                                            }

                                            }

                                    }
                                }

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
                message += newLine + twoSpace + " Total Denominator Count = " + totaldenoCount;
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
                    slaStatus = util.CalculateFinalSLAValueV2(actualValue, (double)expectedsla, (double)minsla);
                    message += newLine + twoSpace + " Status = " + slaStatus;
                }
                ProcessedData data = util.BuildProcessData(sla, (float) actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),ADQuery);
                return data;

            }

            return null;
        }
        catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
            throw ex;
        }

    }

    public SlaResult DefectDetectedInUAT(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues, String src, UserVariables userVariables) {
        sla.setDenojql("Select * From WorkItems  Where [System.WorkItemType] = 'ProdRelease' " +
                "And [System.TeamProject] = '" + userVariables.getTeamProject() +"'"+
                "And ([System.AreaPath] in "+ userVariables.getAreaPathBL() +" Or " +"[System.AreaPath] in " + userVariables.getAreaPathNBL()   +"And [System.State] Ever 'In Release'"+
                "And [Microsoft.VSTS.Common.ClosedDate] >='"+userVariables.getFrom()+ "'" +
                "And [Microsoft.VSTS.Common.ClosedDate] <= '"+ userVariables.getTo() +"'Or " +"[Custom.RevisedDueDate]  >='" + userVariables.getFrom()
                );

        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<WorkItem> workitems= new ArrayList<>();
        List<String[]> dataLines1 = new ArrayList<>();
        String ADQuery="";
        double actualValue = 0;

        int pageSize = 1000;
        String strPageSize = "";
        String issueDefectType = "";
        String searchItemURI="";
        String searchWorkitemLinkURI="";
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

            strPageSize = project.getPageSize();

            if (!strPageSize.isEmpty()) {
                try {
                    pageSize = Integer.parseInt(strPageSize);
                } catch (Exception exPageSizeParse) {
                    pageSize = 1000;
                }
            }

            if (project.getProjectsource().equals(SourceKey.ADO.value)) {

                if(project.getDatafileRequired().equals("Y"))
                {
                    dataLines1.add(new String[]
                            {"Key", "Type", "UAT_Defect KEY", "UAT_Defect Date"});
                }
                issueDefectType=sla.getConfig1();
                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                ADQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";

                searchItemURI = project.getItemUrl();
                searchWorkitemLinkURI = project.getLinkItemUrl();
                workitems = iAdoDataService.getWorkitems(userName, password, project.getProjecturl(), ADQuery, "POST", false, true, false, true, issueDefectType , searchItemURI,searchWorkitemLinkURI,  1000);
                totaldenoCount = 0;
                totalnumCount = 0;
                List<WorkItem> issuelinks= new ArrayList<>();
                message = "";

                if(workitems!=null&& workitems.size()>0)
                {
                    for (WorkItem witem : workitems)
                    {
                        if (witem.getChildLinks() != null && witem.getChildLinks().size() > 0 )
                        {
                            issuelinks = witem.getChildLinks();
                            for (WorkItem witems : issuelinks) {
                                if(witems.getFields().getWorkItemType().equals("Incident") || witems.getFields().getWorkItemType().equals("Problem") || witems.getFields().getWorkItemType().equals("User Story"))
                                {
                                    totaldenoCount++;
                                }
                                if (witems.getFields().getServiceLevelType() != null && (witems.getFields().getWorkItemType() != null)) {
                                    if (witems.getFields().getServiceLevelType().equals("UAT") || (witems.getFields().getWorkItemType().equalsIgnoreCase("Defect"))) {
                                        totalnumCount++;
                                        if (project.getDatafileRequired().equals("Y")) {
                                            dataLines1.add(new String[]
                                                    {witem.getId(), witem.getFields().getWorkItemType(), witems.getId(), witems.getFields().getChangedDate()});
                                        }

                                    }

                                }
                            }

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
                message += newLine + twoSpace + " Total Denominator Count = " + totaldenoCount;
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
                    slaStatus = util.CalculateFinalSLAValueV2(actualValue, (double)expectedsla, (double)minsla);
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
        }
        catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
            throw ex;
        }

    }

}
