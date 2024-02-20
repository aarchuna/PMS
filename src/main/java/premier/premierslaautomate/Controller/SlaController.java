package premier.premierslaautomate.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import premier.premierslaautomate.ENUM.SourceKey;
import premier.premierslaautomate.Models.ADO.SlaResult;
import premier.premierslaautomate.Models.ADO.UserVariables;
import premier.premierslaautomate.Models.ADO.WorkItem;
import premier.premierslaautomate.Models.Issue;
import premier.premierslaautomate.Models.ProcessedData;
import premier.premierslaautomate.ProjectService.Backlog.*;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;
import premier.premierslaautomate.config.Response;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/sla")
public class SlaController
{
    @Autowired
    TimelyAcceptedMilestoneDelivery acceptedMilestoneDelivery;

   @Autowired
    NumberofMilestoneDateDelays numberofMilestoneDateDelays;

   @Autowired
    VarianceToOriginaMilestoneEstimate varianceToOriginaMilestoneEstimate;

   @Autowired
    BacklogItemQuality backlogItemQuality;

   @Autowired
    EstimationQuality estimationQuality;

   @Autowired
    DefectsDetectedReleaseCandidates defectsDetectedReleaseCandidates;

   @Autowired
   PercentageOfTestsAutomated percentageOfTestsAutomated;

   @Autowired
    ProductPercentTestingCompleted productPercentTestingCompleted;

   @Autowired
    TimelyBackLogItemDelivery timelyBackLogItemDelivery;

   @Autowired
   TimeToEstimateBacklogItem timeToEstimateBacklogItem;

    @Autowired
    private ProjectConfiguration projectConfiguration;

    @PostMapping("/timelyAcceptedMilestone")
    public Object TimelyAcceptedMilestone(@RequestBody UserVariables userVariables) throws Exception
    {
        MeasureConfiguration measureConfiguration= new MeasureConfiguration();
        measureConfiguration.setSlaname("Timely Accepted Milestone Delivery");
        measureConfiguration.setFrom(userVariables.getFrom());
        measureConfiguration.setSlakey("TimelyAcceptedMilestone");
        measureConfiguration.setTo(userVariables.getTo());
        measureConfiguration.setSlatype("Both");
        measureConfiguration.setInput1(userVariables.getTo());
        measureConfiguration.setExpectedsla("98");
        measureConfiguration.setMinimumsla("95");

        //Response response = new Response();
        //response.setBody( acceptedMilestoneDelivery.timelyAcceptedMilestone2(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables));
        SlaResult slaResult1 =  acceptedMilestoneDelivery.timelyAcceptedMilestone2(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables);
        //slaResult1.setSlaName("ABC");
        return  slaResult1;
    }

    @PostMapping("/numberofMilestoneDateDelays")
    public Object NumberofMilestoneDateDelays(@RequestBody UserVariables userVariables) throws Exception
    {
        MeasureConfiguration measureConfiguration= new MeasureConfiguration();
        measureConfiguration.setSlaname("Number of Milestone Revision Dates Delays");
        measureConfiguration.setFrom(userVariables.getFrom());
        measureConfiguration.setSlakey("NumberofMilestoneDateDelays");
        measureConfiguration.setTo(userVariables.getTo());
        measureConfiguration.setSlatype("Both");
        measureConfiguration.setInput1(userVariables.getTo());
        measureConfiguration.setExpectedsla("5");
        measureConfiguration.setMinimumsla("10");

        SlaResult slaResult1 =  numberofMilestoneDateDelays.numberofMilestoneDateDelays1(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables);
        return  slaResult1;
    }

    @PostMapping("/VarianceToOriginaMilestoneEstimate")
    public Object VarianceToOriginaMilestoneEstimate(@RequestBody UserVariables userVariables) throws Exception
    {
        MeasureConfiguration measureConfiguration= new MeasureConfiguration();
        measureConfiguration.setSlaname("Variance to Original Milestone Estimate");
        measureConfiguration.setFrom(userVariables.getFrom());
        measureConfiguration.setSlakey("VarianceToOriginaMilestoneEstimate");
        measureConfiguration.setTo(userVariables.getTo());
        measureConfiguration.setSlatype("Both");
        measureConfiguration.setInput1(userVariables.getTo());
        measureConfiguration.setExpectedsla("1");
        measureConfiguration.setMinimumsla("2");

        SlaResult slaResult1 =  varianceToOriginaMilestoneEstimate.varianceToOriginaMilestoneEstimateOne(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables);
        return  slaResult1;
    }

    @PostMapping("/BacklogItemQuality")
    public Object BacklogItemQuality(@RequestBody UserVariables userVariables) throws Exception
    {
        MeasureConfiguration measureConfiguration= new MeasureConfiguration();
        measureConfiguration.setSlaname("BacklogItemQuality Delivery");
        measureConfiguration.setFrom(userVariables.getFrom());
        measureConfiguration.setSlakey("BacklogItemQuality");
        measureConfiguration.setTo(userVariables.getTo());
        measureConfiguration.setSlatype("BackLog");
        measureConfiguration.setInput1(userVariables.getTo());
        measureConfiguration.setExpectedsla("95");
        measureConfiguration.setMinimumsla("85");
        measureConfiguration.setConfig5("BA Review");

        SlaResult slaResult1 =  backlogItemQuality.backlogItemQuality(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables);
        return  slaResult1;
    }

    @PostMapping("/EstimationQuality")
    public Object EstimationQuality(@RequestBody UserVariables userVariables) throws Exception
    {
        MeasureConfiguration measureConfiguration= new MeasureConfiguration();
        measureConfiguration.setSlaname("EstimationQuality");
        measureConfiguration.setFrom(userVariables.getFrom());
        measureConfiguration.setSlakey("EstimationQuality");
        measureConfiguration.setTo(userVariables.getTo());
        measureConfiguration.setSlatype("BackLog");
        measureConfiguration.setExpectedsla("115");
        measureConfiguration.setMinimumsla("85");
        measureConfiguration.setLimit("15");

        SlaResult slaResult1 =  estimationQuality.estimationQuality(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables);
        return  slaResult1;
    }

    @PostMapping("/DefectsDetectedReleaseCandidates")
    public Object DefectsDetectedReleaseCandidates(@RequestBody UserVariables userVariables) throws Exception
    {
        MeasureConfiguration measureConfiguration= new MeasureConfiguration();
        measureConfiguration.setSlaname("EstimationQuality");
        measureConfiguration.setFrom(userVariables.getFrom());
        measureConfiguration.setSlakey("EstimationQuality");
        measureConfiguration.setTo(userVariables.getTo());
        measureConfiguration.setSlatype("Both");
        measureConfiguration.setConfig1("Defect");
        measureConfiguration.setExpectedsla("115");
        measureConfiguration.setMinimumsla("85");


        SlaResult slaResult1 =  defectsDetectedReleaseCandidates.DefectDetectedInUAT(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables);
        return  slaResult1;
    }

    @PostMapping("/PercentageOfTestsAutomated")
    public Object PercentageOfTestsAutomated(@RequestBody UserVariables userVariables) throws Exception
    {
        MeasureConfiguration measureConfiguration= new MeasureConfiguration();
        measureConfiguration.setSlaname("PercentageOfTestsAutomated");
        measureConfiguration.setFrom(userVariables.getFrom());
        measureConfiguration.setSlakey("PercentageOfTestsAutomated");
        measureConfiguration.setTo(userVariables.getTo());
        measureConfiguration.setSlatype("BackLog");
        measureConfiguration.setInput1("Y");
        measureConfiguration.setInput2("Y");
        measureConfiguration.setInput3("Y");
        measureConfiguration.setInput4("Y");
        measureConfiguration.setExpectedsla("85");
        measureConfiguration.setMinimumsla("70");
        measureConfiguration.setTestPlanId(userVariables.getTestPlanId());
        measureConfiguration.setTestPlanId(userVariables.getTestSuitId());

        SlaResult slaResult1 =  percentageOfTestsAutomated.PercentageOfTestsAutomated(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables);
        return  slaResult1;
    }

    @PostMapping("ProductPercentTestingCompleted")
    public Object ProductPercentTestingCompleted(@RequestBody UserVariables userVariables) throws Exception
    {
        MeasureConfiguration measureConfiguration= new MeasureConfiguration();
        measureConfiguration.setSlaname("Product - Percent Testing Completed");
        measureConfiguration.setFrom(userVariables.getFrom());
        measureConfiguration.setSlakey("ProductPercentTestingCompleted");
        measureConfiguration.setTo(userVariables.getTo());
        measureConfiguration.setSlatype("BackLog");
        measureConfiguration.setInput1(userVariables.getTo());
        measureConfiguration.setExpectedsla("98");
        measureConfiguration.setMinimumsla("90");

        SlaResult slaResult1 =  backlogItemQuality.backlogItemQuality(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables);
        return  slaResult1;
    }

    @PostMapping("TimelyBackLogItemDelivery")
    public Object TimelyBackLogItemDelivery(@RequestBody UserVariables userVariables) throws Exception
    {
        MeasureConfiguration measureConfiguration= new MeasureConfiguration();
        measureConfiguration.setSlaname("Timely Backlog Item Delivery");
        measureConfiguration.setFrom(userVariables.getFrom());
        measureConfiguration.setSlakey("TimelyBackLogItem");
        measureConfiguration.setTo(userVariables.getTo());
        measureConfiguration.setSlatype("BackLog");
        measureConfiguration.setInput1(userVariables.getTo());
        measureConfiguration.setExpectedsla("90");
        measureConfiguration.setMinimumsla("80");

        SlaResult slaResult1 =  timelyBackLogItemDelivery.timelyBackLogItem(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables);
        return  slaResult1;
    }

    @PostMapping("TimelyBackLogItemDelivery")
    public Object TimeToEstimateBacklogItems(@RequestBody UserVariables userVariables) throws Exception
    {
        MeasureConfiguration measureConfiguration= new MeasureConfiguration();
        measureConfiguration.setSlaname("Time To Estimate BacklogItem");
        measureConfiguration.setFrom(userVariables.getFrom());
        measureConfiguration.setSlakey("TimeToEstimateBacklogItems");
        measureConfiguration.setTo(userVariables.getTo());
        measureConfiguration.setSlatype("BackLog");
        measureConfiguration.setInput1(userVariables.getTo());
        measureConfiguration.setExpectedsla("95");
        measureConfiguration.setMinimumsla("90");
        measureConfiguration.setLimit("5#10");
        measureConfiguration.setConfig3("In analysis and estimate");
        measureConfiguration.setConfig4("In PI assigned with iteration");
        measureConfiguration.setInput2("Y");
        measureConfiguration.setInput3("Y");
        measureConfiguration.setInput4("Y");
        SlaResult slaResult1 =  timeToEstimateBacklogItem.timeToEstimateBacklogItem1(measureConfiguration,userVariables.getUserName(),userVariables.getPassword(),projectConfiguration,null,"manual", userVariables);
        return  slaResult1;
    }





}
