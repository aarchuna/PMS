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
import java.util.Date;
import java.util.List;

@Service
public class TimelyBackLogItemDelivery implements Serializable
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

    public ProcessedData timelyBackLogItem(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {

        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<String[]> dataLines = new ArrayList<>();
        int pageSize = 1000;
        // ADO Variables
        List<WorkItem> denoWorkitems = new ArrayList<>();
        String denoQuery = "";

        try {


            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type", "Final Issue Status"});
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type", "Final Issue Status","Iteration Path"});
                    dataLines.add(new String[]
                            {"Numerator", "", ""});
                }

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                String Sprint1="";
                String Sprint2="";
                String ReadyState ="";
                String AcceptedState ="";
                String IterationPath1 = "";
                String IterationPath2 = "";
                String IterationChangeState="";

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
                String input1 = sla.getInput1().replace("(","");
                input1 = input1.replace(")","");
                input1 = input1.replace("'","");
                String[] sprinttocal=input1.split(",");

                if (sprinttocal != null && sprinttocal.length == 2) {
                    Sprint1 = sprinttocal[0];
                    Sprint2 = sprinttocal[1];
                }else if ( sprinttocal != null && sprinttocal.length == 1) {
                    Sprint1 = sprinttocal[0];
                }

                boolean isspillover=false;
                double actualValue = 0;
                denoQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";


                ////////// Business Logic Implementation Starts  //////////

                denoWorkitems  = iAdoDataService.getWorkitems(userName,password,project.getProjecturl(),denoQuery,"POST",true,true,100);
                totalnumCount=0;
                totaldenoCount = 0;
                if(denoWorkitems != null && denoWorkitems.size() > 0){
                    totaldenoCount= denoWorkitems.size();
                    for(WorkItem witem:denoWorkitems)
                    {  if(witem!=null) {

                        List<RevisionValue> Revision = witem.getRevisions();
//
                        boolean flag1 = true;
                        boolean flag2 =true;
                        boolean flag3 = true;
                        boolean flag4 = true;
                        Date d1 =new Date();
                        Date d2 =new Date();
                        for(RevisionValue revi: Revision) {
                            if (revi.getFields().getState() != null) {
                                if (revi.getFields().getState().equalsIgnoreCase("Ready") && flag1) {
                                    ReadyState = revi.getFields().getStateChangeDate();
                                    IterationPath1 = revi.getFields().getIterationPath();
                                    d1 = util.ConvertToDate(ReadyState, "yyyy-MM-dd");
                                    flag1 = false;
                                }

                                if(revi.getFields().getState().equalsIgnoreCase("Resolved") && flag2)
                                {
                                    AcceptedState =  revi.getFields().getStateChangeDate();
                                    IterationPath2 = revi.getFields().getIterationPath();
                                    d2 = util.ConvertToDate(AcceptedState, "yyyy-MM-dd");
                                    flag2 =false;

                                }
                            }
                            if ( revi.getFields().getIterationPath() != null && !flag1 && !flag2) {
//
                                if(IterationPath1.equals(IterationPath2) )
                                {
                                    totalnumCount++;
                                    flag3= false;
                                    dataLines.add(new String[]
                                            {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getState()});
                                    break;
                                }
                                if(!IterationPath2.equals(IterationPath1))
                                {
                                    if(d2.after(d1) && d1.before(d2))
                                    {
                                        isspillover = true;
                                    }
                                    if (isspillover == false) {
                                        totalnumCount++;
                                        dataLines.add(new String[]
                                                {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getState()});
                                        flag4= false;
                                        break;
                                    }

                                }

                            }
                        }

                    }
                    }
                }
                if(denoWorkitems != null && denoWorkitems.size() > 0)
                {
                    dataLinesDetailed.add(new String[]
                            {"Denominator", "", ""});
                    for(WorkItem items : denoWorkitems){
                        if(items != null){
                                                           if (project.getDatafileRequired().equals("Y")) {
                                    dataLinesDetailed.add(new String[]
                                            {items.getId(), items.getFields().getWorkItemType(), items.getFields().getState()});
                                }


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
                message = twoSpace + " ADO Query = " + denoQuery;
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
                    slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double)expectedsla, (double)minsla);
                    message += newLine + twoSpace + " Status = " + slaStatus;
                }

                ProcessedData data = util.BuildProcessData(sla, (float)actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual),denoQuery);
                boolean isStatus = util.WriteToFile(project.getLogFile(), message);
                return data;
            }

            return null;
        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }

        return null;
    }

    public SlaResult timelyBackLogItem(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues,String src, UserVariables userVariables) {
        sla.setDenojql("Select * From WorkItems  Where  [System.WorkItemType] = ('User Story','Spike','Feature')  " +
                "And [System.TeamProject] = '" + userVariables.getTeamProject() +"'"+
                "And ([System.AreaPath] in "+ userVariables.getAreaPathBL() +
                "[System.State] Ever 'Ready'"+
                " And [System.IterationPath] in " +userVariables.getIterationPathFormat()
               );
        String baseURI = "";
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<String[]> dataLines = new ArrayList<>();
        int pageSize = 1000;
        // ADO Variables
        List<WorkItem> denoWorkitems = new ArrayList<>();
        String denoQuery = "";

        try {


            if (project.getProjectsource().equals(SourceKey.ADO.value)) {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type", "Final Issue Status"});
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type", "Final Issue Status","Iteration Path"});
                    dataLines.add(new String[]
                            {"Numerator", "", ""});
                }

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }

                String Sprint1="";
                String Sprint2="";
                String ReadyState ="";
                String AcceptedState ="";
                String IterationPath1 = "";
                String IterationPath2 = "";
                String IterationChangeState="";

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
                String input1 = sla.getInput1().replace("(","");
                input1 = input1.replace(")","");
                input1 = input1.replace("'","");
                String[] sprinttocal=input1.split(",");

                if (sprinttocal != null && sprinttocal.length == 2) {
                    Sprint1 = sprinttocal[0];
                    Sprint2 = sprinttocal[1];
                }else if ( sprinttocal != null && sprinttocal.length == 1) {
                    Sprint1 = sprinttocal[0];
                }

                boolean isspillover=false;
                double actualValue = 0;
                denoQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";


                ////////// Business Logic Implementation Starts  //////////

                denoWorkitems  = iAdoDataService.getWorkitems(userName,password,project.getProjecturl(),denoQuery,"POST",true,true,100);
                totalnumCount=0;
                totaldenoCount = 0;
                if(denoWorkitems != null && denoWorkitems.size() > 0){
                    totaldenoCount= denoWorkitems.size();
                    for(WorkItem witem:denoWorkitems)
                    {  if(witem!=null) {

                        List<RevisionValue> Revision = witem.getRevisions();
//
                        boolean flag1 = true;
                        boolean flag2 =true;
                        boolean flag3 = true;
                        boolean flag4 = true;
                        Date d1 =new Date();
                        Date d2 =new Date();
                        for(RevisionValue revi: Revision) {
                            if (revi.getFields().getState() != null) {
                                if (revi.getFields().getState().equalsIgnoreCase("Ready") && flag1) {
                                    ReadyState = revi.getFields().getStateChangeDate();
                                    IterationPath1 = revi.getFields().getIterationPath();
                                    d1 = util.ConvertToDate(ReadyState, "yyyy-MM-dd");
                                    flag1 = false;
                                }

                                if(revi.getFields().getState().equalsIgnoreCase("Resolved") && flag2)
                                {
                                    AcceptedState =  revi.getFields().getStateChangeDate();
                                    IterationPath2 = revi.getFields().getIterationPath();
                                    d2 = util.ConvertToDate(AcceptedState, "yyyy-MM-dd");
                                    flag2 =false;

                                }
                            }
                            if ( revi.getFields().getIterationPath() != null && !flag1 && !flag2) {
//
                                if(IterationPath1.equals(IterationPath2) )
                                {
                                    totalnumCount++;
                                    flag3= false;
                                    dataLines.add(new String[]
                                            {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getState()});
                                    break;
                                }
                                if(!IterationPath2.equals(IterationPath1))
                                {
                                    if(d2.after(d1) && d1.before(d2))
                                    {
                                        isspillover = true;
                                    }
                                    if (isspillover == false) {
                                        totalnumCount++;
                                        dataLines.add(new String[]
                                                {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getState()});
                                        flag4= false;
                                        break;
                                    }

                                }

                            }
                        }

                    }
                    }
                }
                if(denoWorkitems != null && denoWorkitems.size() > 0)
                {
                    dataLinesDetailed.add(new String[]
                            {"Denominator", "", ""});
                    for(WorkItem items : denoWorkitems){
                        if(items != null){
                            if (project.getDatafileRequired().equals("Y")) {
                                dataLinesDetailed.add(new String[]
                                        {items.getId(), items.getFields().getWorkItemType(), items.getFields().getState()});
                            }


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
                message = twoSpace + " ADO Query = " + denoQuery;
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
