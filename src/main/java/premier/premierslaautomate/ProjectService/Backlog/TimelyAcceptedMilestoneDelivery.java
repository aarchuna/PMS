package premier.premierslaautomate.ProjectService.Backlog;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



@Service
public class TimelyAcceptedMilestoneDelivery implements Serializable
{
    //CommonVariables
    private IAdoDataService iAdoDataService = new AdoDataService();

    private SlaResult slaResult = new SlaResult();

    //Test for Arun
    int totalnumCount = 0;
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


    public SlaResult timelyAcceptedMilestone2(MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues,String src,UserVariables userVariables) {
        sla.setDenojql("Select * From WorkItems  Where [System.WorkItemType] = 'Milestone' " +
                "And [System.TeamProject] = '" + userVariables.getTeamProject() +"'"+
                "And ([System.AreaPath] in "+ userVariables.getAreaPathBL() +" Or " +"[System.AreaPath] in " + userVariables.getAreaPathNBL()   +
                "And ([Microsoft.VSTS.Scheduling.DueDate] >='"+userVariables.getFrom()+ "'" +
                "And [Microsoft.VSTS.Scheduling.DueDate] <= '"+ userVariables.getTo() +"'Or " +"[Custom.RevisedDueDate]  >='" + userVariables.getFrom() +"'" +
                "And [Custom.RevisedDueDate] <= '"+ userVariables.getTo()+"' Or" + "[Microsoft.VSTS.Common.ClosedDate] >='" +userVariables.getFrom() + "' " +
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
        List<Date> lstHolidays = new ArrayList<>();
        String dateFormatFromConfig = "";
        LocalDate acceptedDate = LocalDate.now();
        LocalDate closedDate = LocalDate.now();

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


            String fromDate = sla.getFrom();
            String toDate = sla.getTo();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date monthStartDate = dateFormat.parse(fromDate);
            Date monthEndDate = dateFormat.parse(toDate);



            if (project.getProjectsource().equals(SourceKey.ADO.value))
            {
                if (project.getDatafileRequired().equals("Y")) {
                    dataLines.add(new String[]
                            {"Key", "Type", "Final Issue Status","Due Date","Accepted Date","Closed Date"});
                }


                String sprintLastDate = sla.getInput1().replace("/", "-");;

                denoQuery = "{\n" +
                        "  \"query\": \"" + sla.getDenojql()  + "\"\n" + "}";
                denoWorkitems  = iAdoDataService.getWorkitems(userName,password,project.getProjecturl(),denoQuery,"POST",false,true,true,100);

                if(denoWorkitems != null && denoWorkitems.size() > 0){

                    Iterator<WorkItem> iterator = denoWorkitems.iterator();
                    while(iterator.hasNext())
                    {
                        WorkItem deno = iterator.next();
                        Date revisedDue = new Date();
                        String revisedDue1 = deno.getFields().getRevisedDueDate();
                        SimpleDateFormat dateFormatADO = new SimpleDateFormat("MM-dd-yyyy");
                        if (deno.getFields().getRevisedDueDate() != null) {
                            revisedDue = dateFormatADO.parse(revisedDue1);
                        }
                        if (deno.getFields().getRevisedDueDate() != null &&
                                deno.getFields().getRevisedDueDateInfluencedBy().equals("Premier") &&
                                !deno.getFields().getState().equals("Closed") &&
                                !deno.getFields().getState().equals("Accepted") &&
                                !(revisedDue.after(monthStartDate) && revisedDue.before(monthEndDate))) {

                            iterator.remove(); // Use iterator to safely remove the current element
                        }
                    }
                    denoCount1 = denoWorkitems.size();

                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                    LocalDate EndDate1= LocalDate.parse(sprintLastDate,dateTimeFormatter);

                    for(WorkItem witem : denoWorkitems)
                    {
                        List<RevisionValue> revisionlst = new ArrayList<>();
                        revisionlst = witem.getRevisions();
                        boolean value1 = false;
                        boolean value2 = false;

                        if (witem.getRevisions() != null && witem.getRevisions().size() > 0)
                        {
                            for(RevisionValue revision : revisionlst)
                            {
                                if(revision.getFields().getState().equals("Accepted"))
                                {
                                    ZonedDateTime accepted = ZonedDateTime.parse(revision.getFields().getStateChangeDate(), DateTimeFormatter.ISO_DATE_TIME);
                                    acceptedDate = accepted.toLocalDate();
                                    value1 =true;
                                }
                                if(revision.getFields().getState().equals("Closed"))
                                {
                                    ZonedDateTime closed = ZonedDateTime.parse(revision.getFields().getStateChangeDate(),DateTimeFormatter.ISO_DATE_TIME);
                                    closedDate = closed.toLocalDate();
                                    value2 =true;
                                }
                            }
                        }
                        if(value2 == true || value1 == true) {
                            if (witem.getFields().getRevisedDueDate() != null) {
                                if (witem.getFields().getRevisedDueDateInfluencedBy() != null) {
                                    if (witem.getFields().getRevisedDueDateInfluencedBy().equalsIgnoreCase("Premier")) {
                                        ZonedDateTime result = ZonedDateTime.parse(witem.getFields().getRevisedDueDate(), DateTimeFormatter.ISO_DATE_TIME);
                                        LocalDate revisedDueDate = result.toLocalDate();
                                        if (acceptedDate.isBefore(revisedDueDate) || closedDate.isBefore(revisedDueDate) || acceptedDate.isEqual(revisedDueDate) || closedDate.isEqual(revisedDueDate)) {
                                            totalnumCount++;
                                        }
                                    }
                                    else if (witem.getFields().getDueDate() != null) {
                                        ZonedDateTime result = ZonedDateTime.parse(witem.getFields().getDueDate(), DateTimeFormatter.ISO_DATE_TIME);
                                        LocalDate dueDate = result.toLocalDate();
                                        if (acceptedDate.isBefore(dueDate) || closedDate.isBefore(dueDate)  || acceptedDate.isEqual(dueDate) || closedDate.isEqual(dueDate)) {
                                            totalnumCount++;
                                        }

                                    }

                                }

                            }
                            else {
                                if (witem.getFields().getDueDate() != null) {
                                    ZonedDateTime result = ZonedDateTime.parse(witem.getFields().getDueDate(), DateTimeFormatter.ISO_DATE_TIME);
                                    LocalDate dueDate = result.toLocalDate();
                                    if (acceptedDate.isBefore(dueDate) || closedDate.isBefore(dueDate)  || acceptedDate.isEqual(dueDate) || closedDate.isEqual(dueDate)) {
                                        totalnumCount++;
                                    }

                                }
                            }
                        }

                    }

                }

                if(denoWorkitems != null && denoWorkitems.size() > 0)
                {
                    dataLines.add(new String[]
                            {"Denominator", "", ""});
                    for(WorkItem items : denoWorkitems){
                        if(items != null){
                            if (project.getDatafileRequired().equals("Y")) {
                                dataLines.add(new String[]
                                        {items.getId(), items.getFields().getWorkItemType(), items.getFields().getState(), items.getFields().getDueDate(), items.getFields().getAcceptedDate() != null? items.getFields().getAcceptedDate() : "null" ,  items.getFields().getClosedDate() != null? items.getFields().getClosedDate() : "null"});
                            }
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


                double denoCountActual = denoCount1; //This is to hold the old value of deno Count so that it can be send back
                double numCountActual = totalnumCount;  //This is to hold the old value of num Count so that it can be send back

                if (denoCount1 == 0 && totalnumCount == 0)
                {
                    denoCount1 = 0;
                    totalnumCount = 0;
                    actualValue =0;
                    slaStatus = "NT";
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
                    slaStatus = util.CalculateFinalSLAValueV1(actualValue, (double)expectedsla, (double)minsla);
                    slaResult.setDenominator(String.valueOf(denoCount1));
                    slaResult.setNumerator(String.valueOf(totalnumCount));
                    slaResult.setExpectedServiceLevel(String.valueOf(expectedsla));
                    slaResult.setMinimumServiceLevel(String.valueOf(minsla));
                    slaResult.setSlaName(sla.getSlaname());
                    slaResult.setActual(String.valueOf(actualValue));
                    slaResult.setStatus(slaStatus);
                    return slaResult;
                }

                /*ProcessedData data = util.BuildProcessData(sla, (float)actualValue, slaStatus, String.valueOf(numCountActual), String.valueOf(denoCountActual), denoQuery);
                boolean isStatus = util.WriteToFile(project.getLogFile(), message);*//*
                return data;*/
            }

            return null;

        } catch (Exception ex) {
            message = twoSpace + "Error : " + ex.getMessage() + newLine + twoSpace + "Stopping the processing of SLA.";
            status = util.WriteToFile(project.getLogFile(), message);
        }
        return null;
    }
}
