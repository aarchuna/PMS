package premier.premierslaautomate.ProjectService.Backlog;

import org.springframework.stereotype.Service;
import premier.premierslaautomate.DataServices.AdoDataService;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Interfaces.IAdoDataService;
import premier.premierslaautomate.Models.*;
import premier.premierslaautomate.Models.ADO.*;
import premier.premierslaautomate.Utilities.CommonUtil;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VarianceToOriginaMilestoneEstimate implements Serializable
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

    public ProcessedData varianceToOriginaMilestoneEstimate(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues) {
    System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
    message = "Processing SLA : " + sla.getSlaname();
    status = util.WriteToFile(project.getLogFile(), message);
    List<String[]> dataLines = new ArrayList<>();
    List<String[]> dataLinesDetailed = new ArrayList<>();

    // ADO Variables
    List<WorkItem> denoWorkitems = new ArrayList<>();
    String denoQuery = "";
    int denoCount1=0;

    //MEthod Specific Variables
    String sourcedateFormat = "";
    double actualValue = 0;
    String holidayList = "";
    int limit = 0;
    List<Date> lstHolidays = new ArrayList<>();
    String dateFormatFromConfig = "";
    int days =0;
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

        //Holidate List Data validation
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


        if (project.getProjectsource().equals(SourceKey.ADO.value))
        {
            if (project.getDatafileRequired().equals("Y")) {
                dataLines.add(new String[]
                        {"Key", "Type","Due Date","Revised DueDate","Variance"});
                dataLines.add(new String[]
                        {"Numerator", "", ""});
                dataLinesDetailed.add(new String[]
                        {"Key", "Type","Due Date","Revised DueDate"});
            }

            if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
            {
                message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                status = util.WriteToFile(project.getLogFile(), message);
                return null;
            }
            String sprintLastDate = sla.getInput1();
            String fromDate = sla.getFrom();
            String toDate = sla.getTo();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date monthStartDate = dateFormat.parse(fromDate);
            Date monthEndDate = dateFormat.parse(toDate);

            denoQuery = "{\n" +
                    "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";
            denoWorkitems  = iAdoDataService.getWorkitems(userName,password,project.getProjecturl(),denoQuery,"POST",true,true,false,100);
            denoCount1 =0;
            totalnumCount = 0;
            if(denoWorkitems == null )
            {
                totaldenoCount = 0;
                totalnumCount = 0 ;
            }
            if(denoWorkitems != null && denoWorkitems.size() > 0){
                for(WorkItem deno : denoWorkitems)
                {
                    Date revisedDue = new Date();
                    String revisedDue1 = deno.getFields().getRevisedDueDate();
                    SimpleDateFormat dateFormatADO = new SimpleDateFormat("MM-dd-yyyy");
                    if(deno.getFields().getRevisedDueDate() != null)
                    {
                        revisedDue = dateFormatADO.parse(revisedDue1);
                    }
                    if(deno.getFields().getRevisedDueDate() != null && deno.getFields().getRevisedDueDateInfluencedBy().equals("Premier") && !deno.getFields().getState().equals("Closed") && !deno.getFields().getState().equals("Accepted")  && !(revisedDue.after(monthStartDate) && revisedDue.before(monthEndDate)) )
                    {
                        denoWorkitems.remove(deno);
                    }
                }
                denoCount1 = denoWorkitems.size();

                for(WorkItem witem : denoWorkitems)
                {
                    if(witem.getFields().getRevisedDueDateInfluencedBy().equalsIgnoreCase("Long80") || (witem.getFields().getRevisedDueDateInfluencedBy().isEmpty() && witem.getFields().getRevisedDueDate().isEmpty()) )
                    {
                        ZonedDateTime result = ZonedDateTime.parse(witem.getFields().getDueDate(), DateTimeFormatter.ISO_DATE_TIME);
                        LocalDate DueDate = result.toLocalDate();
                        ZonedDateTime revisedResult = ZonedDateTime.parse(witem.getFields().getClosedDate(),DateTimeFormatter.ISO_DATE_TIME);
                        LocalDate ClosedDate = revisedResult.toLocalDate();

                        days = (int) ChronoUnit.DAYS.between(DueDate,ClosedDate);
                        if(days > 0) {
                            totalnumCount += days;
                            if (project.getDatafileRequired().equals("Y")) {
                                dataLines.add(new String[]
                                        {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getDueDate(), witem.getFields().getRevisedDueDate() != null? witem.getFields().getRevisedDueDate(): "Null", String.valueOf(days)});
                            }
                        }
                    }
                    if(witem.getFields().getRevisedDueDate()!=null && witem.getFields().getRevisedDueDateInfluencedBy()!= null)
                    {
                        if(witem.getFields().getRevisedDueDateInfluencedBy().equalsIgnoreCase("Premier"))
                        {
                            ZonedDateTime result = ZonedDateTime.parse(witem.getFields().getRevisedDueDate(), DateTimeFormatter.ISO_DATE_TIME);
                            LocalDate revisedDueDate = result.toLocalDate();
                            ZonedDateTime revisedResult = ZonedDateTime.parse(witem.getFields().getClosedDate(),DateTimeFormatter.ISO_DATE_TIME);
                            LocalDate ClosedDate = revisedResult.toLocalDate();

                            days = (int) ChronoUnit.DAYS.between(revisedDueDate,ClosedDate);
                            if(days>0) {
                                totalnumCount += days;
                                if (project.getDatafileRequired().equals("Y")) {
                                    dataLines.add(new String[]
                                            {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getDueDate(), witem.getFields().getRevisedDueDate() != null? witem.getFields().getRevisedDueDate(): "Null", String.valueOf(days)});
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
                        dataLinesDetailed.add(new String[]
                                {items.getId(), items.getFields().getWorkItemType(), items.getFields().getDueDate()});
                    }
                }
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

            message = twoSpace + " Total Denominator Count = " + denoCount1;
            message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
            message += newLine + twoSpace + " AdoQuery = " + denoQuery;
            message += newLine + twoSpace + " Minimum SLA = " + minsla;
            message += newLine + twoSpace + " Expected SLA = " + expectedsla;

            double denoCountActual = denoCount1; //This is to hold the old value of deno Count so that it can be send back
            double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

            if (denoCount1 == 0 && totalnumCount == 0)
            {
                denoCount1 = 0;
                totalnumCount = 0;
                actualValue =0;
                message += newLine + twoSpace + " Actual = " + actualValue;
                slaStatus = "NT";
                message += newLine + twoSpace + " Status = " + slaStatus;
            }
            else {
                actualValue = util.GetActualValueV1((double)denoCount1, (double)totalnumCount);
                message += newLine + twoSpace + " Actual = " + actualValue;
                slaStatus = util.CalculateFinalSLAValueV2(actualValue, (double)expectedsla, (double)minsla);
                message += newLine + twoSpace + " Status = " + slaStatus;
            }

            ProcessedData data = util.BuildProcessData(sla, (float)actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual), denoQuery);
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

    public SlaResult varianceToOriginaMilestoneEstimateOne(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues, String src, UserVariables userVariables) {
        sla.setDenojql("Select * From WorkItems  Where [System.WorkItemType] = 'Milestone' " +
                "And [System.TeamProject] = '" + userVariables.getTeamProject() +"'"+
                "And ([System.AreaPath] in "+ userVariables.getAreaPathBL() +" Or " +"[System.AreaPath] in " + userVariables.getAreaPathNBL()   +
                "And ([Microsoft.VSTS.CodeReview.AcceptedDate]  >='"+userVariables.getFrom()+ "'" +
                "And [Microsoft.VSTS.CodeReview.AcceptedDate]  <= '"+ userVariables.getTo() +" Or " + "[Microsoft.VSTS.Common.ClosedDate] >='" +userVariables.getFrom() + "' " +
                "And [Microsoft.VSTS.Common.ClosedDate] <= '" +userVariables.getTo()+"'))");
        System.out.println("SLA : " + project.getProjectKey() + "-->" + sla.getSlaname());
        message = "Processing SLA : " + sla.getSlaname();
        status = util.WriteToFile(project.getLogFile(), message);
        List<String[]> dataLines = new ArrayList<>();
        List<String[]> dataLinesDetailed = new ArrayList<>();

        // ADO Variables
        List<WorkItem> denoWorkitems = new ArrayList<>();
        String denoQuery = "";
        int denoCount1=0;

        //MEthod Specific Variables
        String sourcedateFormat = "";
        double actualValue = 0;
        String holidayList = "";
        int limit = 0;
        List<Date> lstHolidays = new ArrayList<>();
        String dateFormatFromConfig = "";
        int days =0;
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

            //Holidate List Data validation
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


            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type","Due Date","Revised DueDate","Variance"});
                    dataLines.add(new String[]
                            {"Numerator", "", ""});
                    dataLinesDetailed.add(new String[]
                            {"Key", "Type","Due Date","Revised DueDate"});
                }

                if (sla.getDenojql() == null || sla.getDenojql().isEmpty())
                {
                    message = twoSpace + "deno Query is not available, please check your configuration. Stopping SLA calculation";
                    status = util.WriteToFile(project.getLogFile(), message);
                    return null;
                }
                String sprintLastDate = sla.getInput1();
                String fromDate = sla.getFrom();
                String toDate = sla.getTo();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date monthStartDate = dateFormat.parse(fromDate);
                Date monthEndDate = dateFormat.parse(toDate);

                denoQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql() + "\"\n" + "}";
                denoWorkitems  = iAdoDataService.getWorkitems(userName,password,project.getProjecturl(),denoQuery,"POST",true,true,false,100);
                denoCount1 =0;
                totalnumCount = 0;
                if(denoWorkitems == null )
                {
                    totaldenoCount = 0;
                    totalnumCount = 0 ;
                }
                if(denoWorkitems != null && denoWorkitems.size() > 0){
                    for(WorkItem deno : denoWorkitems)
                    {
                        Date revisedDue = new Date();
                        String revisedDue1 = deno.getFields().getRevisedDueDate();
                        SimpleDateFormat dateFormatADO = new SimpleDateFormat("MM-dd-yyyy");
                        if(deno.getFields().getRevisedDueDate() != null)
                        {
                            revisedDue = dateFormatADO.parse(revisedDue1);
                        }
                        if(deno.getFields().getRevisedDueDate() != null && deno.getFields().getRevisedDueDateInfluencedBy().equals("Premier") && !deno.getFields().getState().equals("Closed") && !deno.getFields().getState().equals("Accepted")  && !(revisedDue.after(monthStartDate) && revisedDue.before(monthEndDate)) )
                        {
                            denoWorkitems.remove(deno);
                        }
                    }
                    denoCount1 = denoWorkitems.size();

                    for(WorkItem witem : denoWorkitems)
                    {
                        if(witem.getFields().getRevisedDueDateInfluencedBy().equalsIgnoreCase("Long80") || (witem.getFields().getRevisedDueDateInfluencedBy().isEmpty() && witem.getFields().getRevisedDueDate().isEmpty()) )
                        {
                            ZonedDateTime result = ZonedDateTime.parse(witem.getFields().getDueDate(), DateTimeFormatter.ISO_DATE_TIME);
                            LocalDate DueDate = result.toLocalDate();
                            ZonedDateTime revisedResult = ZonedDateTime.parse(witem.getFields().getClosedDate(),DateTimeFormatter.ISO_DATE_TIME);
                            LocalDate ClosedDate = revisedResult.toLocalDate();

                            days = (int) ChronoUnit.DAYS.between(DueDate,ClosedDate);
                            if(days > 0) {
                                totalnumCount += days;
                                if (project.getDatafileRequired().equals("Y")) {
                                    dataLines.add(new String[]
                                            {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getDueDate(), witem.getFields().getRevisedDueDate() != null? witem.getFields().getRevisedDueDate(): "Null", String.valueOf(days)});
                                }
                            }
                        }
                        if(witem.getFields().getRevisedDueDate()!=null && witem.getFields().getRevisedDueDateInfluencedBy()!= null)
                        {
                            if(witem.getFields().getRevisedDueDateInfluencedBy().equalsIgnoreCase("Premier"))
                            {
                                ZonedDateTime result = ZonedDateTime.parse(witem.getFields().getRevisedDueDate(), DateTimeFormatter.ISO_DATE_TIME);
                                LocalDate revisedDueDate = result.toLocalDate();
                                ZonedDateTime revisedResult = ZonedDateTime.parse(witem.getFields().getClosedDate(),DateTimeFormatter.ISO_DATE_TIME);
                                LocalDate ClosedDate = revisedResult.toLocalDate();

                                days = (int) ChronoUnit.DAYS.between(revisedDueDate,ClosedDate);
                                if(days>0) {
                                    totalnumCount += days;
                                    if (project.getDatafileRequired().equals("Y")) {
                                        dataLines.add(new String[]
                                                {witem.getId(), witem.getFields().getWorkItemType(), witem.getFields().getDueDate(), witem.getFields().getRevisedDueDate() != null? witem.getFields().getRevisedDueDate(): "Null", String.valueOf(days)});
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
                            dataLinesDetailed.add(new String[]
                                    {items.getId(), items.getFields().getWorkItemType(), items.getFields().getDueDate()});
                        }
                    }
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

                message = twoSpace + " Total Denominator Count = " + denoCount1;
                message += newLine + twoSpace + " Total Numerator Count = " + totalnumCount;
                message += newLine + twoSpace + " AdoQuery = " + denoQuery;
                message += newLine + twoSpace + " Minimum SLA = " + minsla;
                message += newLine + twoSpace + " Expected SLA = " + expectedsla;

                double denoCountActual = denoCount1; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                if (denoCount1 == 0 && totalnumCount == 0)
                {
                    denoCount1 = 0;
                    totalnumCount = 0;
                    actualValue =0;
                    slaStatus = "NT";
                    slaStatus = util.CalculateFinalSLAValueV2(actualValue, (double)expectedsla, (double)minsla);
                    slaResult.setDenominator(String.valueOf(denoCount1));
                    slaResult.setNumerator(String.valueOf(totalnumCount));
                    slaResult.setExpectedServiceLevel(String.valueOf(expectedsla));
                    slaResult.setMinimumServiceLevel(String.valueOf(minsla));
                    slaResult.setSlaName(sla.getSlaname());
                    slaResult.setActual(String.valueOf(actualValue));
                    slaResult.setStatus(slaStatus);
                    return slaResult;
                }
                else {
                    actualValue = util.GetActualValueV1((double)denoCount1, (double)totalnumCount);
                    slaStatus = util.CalculateFinalSLAValueV2(actualValue, (double)expectedsla, (double)minsla);
                    slaResult.setDenominator(String.valueOf(denoCount1));
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
