package premier.premierslaautomate.DataServices;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import lombok.val;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import premier.premierslaautomate.Interfaces.IJiraDataService;
import premier.premierslaautomate.Models.*;
import premier.premierslaautomate.Models.Jira.*;
import premier.premierslaautomate.Utilities.CommonUtil;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.lang.reflect.Field;


@Service
public class JiraDataService implements IJiraDataService
{

    private SlaRestClient restClient = new SlaRestClient();
    CommonUtil util = new CommonUtil();

    @Autowired
    private SlaRestClientService restClientService;

    @Autowired
    private RestTemplate restTemplate;

    //This is the JIRA API Component Class
    @Autowired
    private JiraAPI jiraApi;

    @Override
    public List<Sprint> getAllSprint(String userName, String password)
    {
        return restClientService.exchange("https://code.premierinc.com/issues/rest/agile/1.0/board/1227/sprint", new ParameterizedTypeReference<List<Sprint>>() {});
    }

    @Override
    public SearchBoard getAllBoards(String userName, String password)
    {

        return restClientService.exchangeWihCredential("https://code.premierinc.com/issues/rest/agile/latest/board?startAt=0&maxResults=1000", new ParameterizedTypeReference<SearchBoard>() {}, userName, password);
    }

    @Override
    public List<BoardInfo> getAllBoardsNew(String userName, String password)
    {
        List<BoardInfo> boards = new ArrayList<>();
        SearchBoard tBoard = restClientService.exchangeWihCredential("https://code.premierinc.com/issues/rest/agile/latest/board?startAt=0&maxResults=1000", new ParameterizedTypeReference<SearchBoard>() {}, userName, password);
        return boards;



    }

    @Override
    public List<BoardInfo> getAllBoards1(String userName, String password)
    {
        List<BoardInfo> boards = new ArrayList<>();
        String requestURI = "https://code.premierinc.com/issues/rest/agile/latest/board?startAt=0&maxResults=50";
        SearchBoard tBoard = jiraApi.GetBoardsPagewise(userName, password, requestURI);
        if(tBoard != null){
            boards.addAll(tBoard.getValues());
            int total = tBoard.getTotal();
            for (int i=50;i<=total;i+=50){
                String url = "https://code.premierinc.com/issues/rest/agile/latest/board?startAt="+i+"&maxResults=50";
                boards.addAll(jiraApi.GetBoardsPagewise(userName, password, url).getValues());
            }
        }
        return boards;
    }

    public List<Issue> getAllIssuesBySprint(String requestUri,String userName, String password)
    {
        List<Issue> issues = new ArrayList<>();
        String url = requestUri + "?expand=changelog&startAt=0&maxResults=50";
        SearchIssueChangeLog data = jiraApi.GetIssuesBySprintWithChangeLog(userName, password, url);
        if(data != null){
            issues.addAll(data.getIssues());
            int total = data.getTotal();
            for (int i=50;i<=total;i+=50){
                String paginatedUrl = requestUri + "?expand=changelog&startAt="+i+"&maxResults=50";
                issues.addAll(jiraApi.GetIssuesBySprintWithChangeLog(userName,password,paginatedUrl).getIssues());
            }
            if (!CollectionUtils.isEmpty(issues)){
                List<Item> transistionStatus = new ArrayList<>();
                for (Issue issue : issues){
                    ChangeLog changeLog = issue.getChangelog();
                    if (changeLog != null){
                        List<History> histories = changeLog.getHistories();
                        if(!CollectionUtils.isEmpty(histories)){
                            for (History history : histories){
                                List<Item> items = history.getItems();
                                if(!CollectionUtils.isEmpty(items)){
                                    transistionStatus = items.stream()
                                            .filter(item -> item.getField() != null && item.getField().equals("status"))
                                            .collect(Collectors.toList());
                                }
                            }
                        }
                    }
                }
                System.out.println(transistionStatus);
            }
        }
        return issues;

    }

    public SlaProcessingData getTimelyBacklogDelivery(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel,String userName, String password)
    {
        SlaProcessingData slaProcessingData = new SlaProcessingData();
        try
        {
            SearchIssue denoIssues = jiraApi.Searchissues(userName, password, denoUri);
            int totalIssuesCommitted = denoIssues.getTotal();
            SearchIssue NumIssues = jiraApi.Searchissues(userName, password, numURI);
            int totalIssuesAccepted = NumIssues.getTotal();

            // There is no baseline limit for this Slas

            int actual = (totalIssuesAccepted/totalIssuesCommitted) * 100;
            String SLAStatus = "Not Met";
            if (actual >= minServiceLevel)
            {
                SLAStatus = "Met";
            }
            slaProcessingData.setNumCount(String.valueOf(totalIssuesAccepted));
            slaProcessingData.setDenCount(String.valueOf(totalIssuesCommitted));
            slaProcessingData.setActualprocessingLevel(String.valueOf(actual));
            slaProcessingData.setExpectedServiceLevel(String.valueOf(expectedServiceLevel));
            slaProcessingData.setMinimumServiceLevel(String.valueOf(minServiceLevel));
            slaProcessingData.setSlaStatus(SLAStatus);

            return slaProcessingData;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public SlaProcessingData getBacklogItemQualityDelivery(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel,String userName, String password)
    {
        SlaProcessingData slaProcessingData = new SlaProcessingData();

        try
        {
            SearchIssue denoIssues = jiraApi.Searchissues(userName, password, denoUri);
            int totalIssuesCommitted = denoIssues.getTotal();
            SearchIssue NumIssues = jiraApi.Searchissues(userName, password, numURI);
            int totalIssuesAccepted = NumIssues.getTotal();

            // There is no baseline limit for this Slas

            int actual = (totalIssuesAccepted/totalIssuesCommitted) * 100;
            String SLAStatus = "Not Met";
            if (actual >= minServiceLevel)
            {
                SLAStatus = "Met";
            }
            slaProcessingData.setNumCount(String.valueOf(totalIssuesAccepted));
            slaProcessingData.setDenCount(String.valueOf(totalIssuesCommitted));
            slaProcessingData.setActualprocessingLevel(String.valueOf(actual));
            slaProcessingData.setExpectedServiceLevel(String.valueOf(expectedServiceLevel));
            slaProcessingData.setMinimumServiceLevel(String.valueOf(minServiceLevel));
            slaProcessingData.setSlaStatus(SLAStatus);

            return slaProcessingData;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public SlaProcessingData getDelayInReadyForProductionRelease(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel,String userName, String password)
    {
        SlaProcessingData slaProcessingData = new SlaProcessingData();

        try
        {
            SearchIssue denoIssues = jiraApi.Searchissues(userName, password, denoUri);
            int totalIssuesCommitted = denoIssues.getTotal();
            SearchIssue NumIssues = jiraApi.Searchissues(userName, password, numURI);
            int totalIssuesAccepted = NumIssues.getTotal();

            // There is no baseline limit for this Slas

            int actual = (totalIssuesAccepted/totalIssuesCommitted) * 100;
            String SLAStatus = "Not Met";
            if (actual >= minServiceLevel)
            {
                SLAStatus = "Met";
            }
            slaProcessingData.setNumCount(String.valueOf(totalIssuesAccepted));
            slaProcessingData.setDenCount(String.valueOf(totalIssuesCommitted));
            slaProcessingData.setActualprocessingLevel(String.valueOf(actual));
            slaProcessingData.setExpectedServiceLevel(String.valueOf(expectedServiceLevel));
            slaProcessingData.setMinimumServiceLevel(String.valueOf(minServiceLevel));
            slaProcessingData.setSlaStatus(SLAStatus);

            return slaProcessingData;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public SlaProcessingData getDefectsDetectedInUserAcceptanceTesting(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel,String userName, String password)
    {
        SlaProcessingData slaProcessingData = new SlaProcessingData();

        try
        {
            SearchIssue denoIssues = jiraApi.Searchissues(userName, password, denoUri);
            int totalIssuesCommitted = denoIssues.getTotal();
            SearchIssue NumIssues = jiraApi.Searchissues(userName, password, numURI);
            int totalIssuesAccepted = NumIssues.getTotal();

            // There is no baseline limit for this Slas

            int actual = ((totalIssuesCommitted - totalIssuesAccepted)/totalIssuesCommitted) * 100;
            String SLAStatus = "Not Met";
            if (actual >= minServiceLevel)
            {
                SLAStatus = "Met";
            }
            slaProcessingData.setNumCount(String.valueOf(totalIssuesAccepted));
            slaProcessingData.setDenCount(String.valueOf(totalIssuesCommitted));
            slaProcessingData.setActualprocessingLevel(String.valueOf(actual));
            slaProcessingData.setExpectedServiceLevel(String.valueOf(expectedServiceLevel));
            slaProcessingData.setMinimumServiceLevel(String.valueOf(minServiceLevel));
            slaProcessingData.setSlaStatus(SLAStatus);

            return slaProcessingData;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public SlaProcessingData getIssuesDetectedPostProductionRelease( String numURI, int expectedServiceLevel, int minServiceLevel,String userName, String password)
    {
        SlaProcessingData slaProcessingData = new SlaProcessingData();

        try
        {


            SearchIssue NumIssues = jiraApi.Searchissues(userName, password, numURI);
            int totalIssuesAccepted = NumIssues.getTotal();

            // There is no baseline limit for this Slas
            int actual = totalIssuesAccepted;
            String SLAStatus = "Not Met";
            if (actual <= minServiceLevel)
            {
                SLAStatus = "Met";
            }
            slaProcessingData.setNumCount(String.valueOf(totalIssuesAccepted));

            slaProcessingData.setActualprocessingLevel(String.valueOf(actual));
            slaProcessingData.setExpectedServiceLevel(String.valueOf(expectedServiceLevel));
            slaProcessingData.setMinimumServiceLevel(String.valueOf(minServiceLevel));
            slaProcessingData.setSlaStatus(SLAStatus);

            return slaProcessingData;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public SlaProcessingData getProductPercentTestingCompleted(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel,String userName, String password)
    {
        SlaProcessingData slaProcessingData = new SlaProcessingData();

        try
        {
            SearchIssue denoIssues = jiraApi.Searchissues(userName, password, denoUri);
            int testingplanned = denoIssues.getTotal()+127;
            SearchIssue NumIssues = jiraApi.Searchissues(userName, password, numURI);
            int testingCompleted = NumIssues.getTotal()+127;

            // There is no baseline limit for this Slas

            int actual = (testingCompleted/testingplanned) * 100;
            String SLAStatus = "Not Met";
            if (actual >= minServiceLevel)
            {
                SLAStatus = "Met";
            }
            slaProcessingData.setNumCount(String.valueOf(testingCompleted));
            slaProcessingData.setDenCount(String.valueOf(testingplanned));
            slaProcessingData.setActualprocessingLevel(String.valueOf(actual));
            slaProcessingData.setExpectedServiceLevel(String.valueOf(expectedServiceLevel));
            slaProcessingData.setMinimumServiceLevel(String.valueOf(minServiceLevel));
            slaProcessingData.setSlaStatus(SLAStatus);

            return slaProcessingData;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    @Override
    public SlaProcessingData getPercentageOfTestsAutomated(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel, String userName, String password) {
        SlaProcessingData slaProcessingData = new SlaProcessingData();

        try
        {
            SearchIssue denoIssues = jiraApi.Searchissues(userName, password, denoUri);
            int totalIssuesCommitted = denoIssues.getTotal();
            SearchIssue NumIssues = jiraApi.Searchissues(userName, password, numURI);
            int totalIssuesAccepted = NumIssues.getTotal();

            // There is no baseline limit for this Slas

            int actual = ((totalIssuesAccepted/totalIssuesCommitted) * 100);
            String SLAStatus = "Not Met";
            if (actual >= minServiceLevel)
            {
                SLAStatus = "Met";
            }
            slaProcessingData.setNumCount(String.valueOf(totalIssuesAccepted));
            slaProcessingData.setDenCount(String.valueOf(totalIssuesCommitted));
            slaProcessingData.setActualprocessingLevel(String.valueOf(actual));
            slaProcessingData.setExpectedServiceLevel(String.valueOf(expectedServiceLevel));
            slaProcessingData.setMinimumServiceLevel(String.valueOf(minServiceLevel));
            slaProcessingData.setSlaStatus(SLAStatus);

            return slaProcessingData;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }


    //------------------------------------Generic Functions-------------------------------------------------------------//
    public List<Issue> getAllIssueOnJQL(String uri, String userName, String password, String baseURI)
    {
        List<Issue> issues = new ArrayList<>();
        try
        {
            SearchIssue searchIssue = restClient.exchangeWihCredential(uri, SearchIssue.class, userName, password);
           issues = searchIssue.getIssues();
           //Loop till you get all the results. Update the isses object and then

            return issues;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public List<Issue> getAllIssuesOnJQLV1(String userName, String password, String searchURI, String changeLogBaseURI, String commentbaseURI, boolean changeLogRequired, boolean commentsRequired)
    {
        List<Issue> issues = new ArrayList<>();
        List<Issue> finalissue = new ArrayList<>();
        String issueURI = "";
        try
        {
            SearchIssue searchIssue = restClient.exchangeWihCredential(searchURI, SearchIssue.class, userName, password);
            issues = searchIssue.getIssues();
            //Loop till you get all the results. Update the isses object and then
            Issue thisIssue = new Issue();
            //Get All the Change Log
            for (Issue issue: issues)
            {
                Issue issueToAdd = new Issue();
                premier.premierslaautomate.Models.Field field = issue.getFields();
                ChangeLog changeLog = null;
                Comment comment = null;

                if (changeLogRequired)
                {
                    issueURI =changeLogBaseURI + "jql=issuekey=" + issue.getKey() + "&expand=changelog";
                    searchIssue = restClient.exchangeWihCredential(issueURI,SearchIssue.class, userName, password);
                    if (searchIssue != null)
                    {
                        if (searchIssue.getIssues().size() > 0)
                        {
                            if (searchIssue.getIssues().get(0).getChangelog() != null)
                            {
                                //changeLog = searchIssue.getIssues().get(0).getChangelog();
                                issue.setChangelog(searchIssue.getIssues().get(0).getChangelog());
                            }
                        }
                    }
                }


                if (commentsRequired)
                {
                    issueURI = commentbaseURI + "/" + issue.getKey();
                    comment = null;
                    thisIssue = restClient.exchangeWihCredential(issueURI,Issue.class, userName, password);
                    if (thisIssue != null)
                    {
                        if (thisIssue.getFields() != null)
                        {
                            if (thisIssue.getFields().getComment() != null)
                            {
                                //comment = searchIssue.getIssues().get(0).getFields().getComment();
                                field.setComment(thisIssue.getFields().getComment());
                            }
                        }
                    }

                }

                issueToAdd.setKey(issue.getKey());
                issueToAdd.setSelf(issue.getSelf());
                issueToAdd.setId(issue.getId());
                issueToAdd.setExpand(issue.getExpand());
                issueToAdd.setChangelog(issue.getChangelog());
                issueToAdd.setFields(issue.getFields());
                finalissue.add(issueToAdd);
            }

            return finalissue;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public List<Issue> getAllIssuesOnJQLV2(String userName, String password, String searchURI, String commentbaseURI, boolean changeLogRequired, boolean commentsRequired, int pageSize)
    {
        List<Issue> output = new ArrayList<>();
        SearchIssue searchIssue = new SearchIssue();
        List<Issue> issuesToProcess = new ArrayList<>();
        List<List<Issue>> processedIssues = new ArrayList<>();
        String issueURI = "";
        int totalRecords = 0;
        int startAt = 0;
        int maxResults = pageSize;
        int totalRecordsRetrieved = 0;
        String finalURL = "";
        String jiraIssueURI = "";

        output = null;
        try
        {
            if (changeLogRequired == true)
            {
                jiraIssueURI = searchURI + "&expand=changelog";
            }

            try
            {
                finalURL = jiraIssueURI + "&startAt=" + String.valueOf(startAt) + "&maxResults=1";
                searchIssue = null;
                searchIssue = restClient.exchangeWihCredential(jiraIssueURI, SearchIssue.class, userName, password);

                if (searchIssue != null)
                {
                    totalRecords = searchIssue.getTotal();
                }

                if (totalRecords > 0)
                {
                    totalRecords = 200;
                    if (totalRecords  > maxResults)
                    {
                        totalRecordsRetrieved = 0;
                        while ((totalRecords -1) > totalRecordsRetrieved)
                        {
                            finalURL = jiraIssueURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(maxResults);
                            searchIssue = restClient.exchangeWihCredential(jiraIssueURI, SearchIssue.class, userName, password);

                            if (searchIssue == null)
                            {
                                break;
                            }

                            if (commentsRequired == true)
                            {
                                issuesToProcess = searchIssue.getIssues();
                                issuesToProcess = GetCommentsForIssue(userName, password, issuesToProcess, commentbaseURI, true);
                                processedIssues.add(issuesToProcess);
                            }

                            totalRecordsRetrieved = totalRecordsRetrieved + searchIssue.getIssues().size();
                            startAt = totalRecordsRetrieved + 1;
                        }
                    }
                    else
                    {
                        //Less record than page size, no looping
                        finalURL = jiraIssueURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(totalRecords);
                        searchIssue = restClient.exchangeWihCredential(jiraIssueURI, SearchIssue.class, userName, password);

                        if (searchIssue != null)
                        {
                            if (searchIssue.getIssues().size() > 0)
                            {
                                issuesToProcess = searchIssue.getIssues();
                                issuesToProcess = GetCommentsForIssue(userName, password, issuesToProcess, commentbaseURI, true);
                                processedIssues.add(issuesToProcess);
                            }
                        }
                    }
                }

            }
            catch (Exception exSearch)
            {

            }

            if (processedIssues.size() > 0)
            {
                output = new ArrayList<>();
                for (List<Issue> bissue : processedIssues)
                {
                    for (Issue issue : bissue)
                    {
                        output.add(issue);
                    }
                }
            }
            return output;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public List<Issue> getAllIssueWithChangeLogOnJQL(String uri, String userName, String password, String baseURI)
    {
        List<Issue> issues = new ArrayList<>();
        List<Issue> finalissue = new ArrayList<>();
        try
        {
            SearchIssue searchIssue = restClient.exchangeWihCredential(uri, SearchIssue.class, userName, password);
            issues = searchIssue.getIssues();
            //Loop till you get all the results. Update the isses object and then
            //Get All the Change Log
            for (Issue issue: issues)
            {
                String issueURI =baseURI + "jql=issuekey=" + issue.getKey() + "&expand=changelog";
                SearchIssue searchIssues = restClient.exchangeWihCredential(issueURI,SearchIssue.class, userName, password);
                finalissue.addAll(searchIssues.getIssues());
            }

            return finalissue;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public List<Issue> getAllIssueWithComment(String uri, String userName, String password, String baseURI) {
        List<Issue> issues = new ArrayList<>();
        List<Issue> issues1 = new ArrayList<>();
        try {
            SearchIssue searchIssue = restClient.exchangeWihCredential(uri, SearchIssue.class, userName, password);
            issues = searchIssue.getIssues();
            //Loop till you get all the results. Update the isses object and then
            //Get All the Comment
            for (Issue issue : issues) {
                String issuecmtURI = "https://code.premierinc.com/issues/rest/agile/1.0/issue/" + issue.getKey();
                Issue issue1 = restClient.exchangeWihCredential(issuecmtURI, Issue.class, userName, password);
                issues1.add( issue1);
            }
            return issues1;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public Issue getIssueByKey(String uri, String userName, String password)
    {
        return restClient.exchangeWihCredential(uri, Issue.class, userName, password);
    }

    public List<BacklogIssue> getAllIssuesFromBacklog (String userName, String password, String baseURI, String boardIds, String changeLogURI, String commentURI,boolean isChangeLogRequired, boolean isCommentsRequired, int pageSize)
    {
        List<BacklogIssue> output = new ArrayList<>();
        SearchBackLogIssue searchIssue = new SearchBackLogIssue();
        SearchBackLogIssue tSearchIssue = new SearchBackLogIssue();
        List<BacklogIssue> issuesToProcess = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<BacklogIssue>> backlogIssues = new ArrayList<>();

        String backlogBaseURI = baseURI + "/agile/latest/board/{BoardId}/backlog?";
        String backlogURI = "";
        String backlogURIFinal = "";
        String[] boards = (boardIds.split(","));
        int totalRecords = 0;
        int startAt = 0;
        int maxResults = 0;
        String strJSonData = "";
        int totalRecordsRetrieved = 0;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, false);

        output = null;
        try
        {
            for (String board : boards)
            {
                backlogURI = backlogBaseURI.replace("{BoardId}", board);

                try
                {


                    backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=1";
                    strJSonData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);

                    if (!strJSonData.isEmpty())
                    {
                        searchIssue = objectMapper.readValue(strJSonData, SearchBackLogIssue.class);

                        if (searchIssue != null)
                        {
                            totalRecords = searchIssue.getTotal();
                        }

                        //Get the Pagesize and update in Max Results
                        maxResults = pageSize;
                        if (totalRecords > 0)
                        {
                            if ((totalRecords -1) > maxResults)
                            {
                                totalRecordsRetrieved = 0;
                                //Loop and Get the data till it retrieved all the records
                                while (totalRecordsRetrieved < totalRecords)
                                {
                                    backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(maxResults);
                                    strJSonData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);

                                    if (!strJSonData.isEmpty())
                                    {
                                        searchIssue = objectMapper.readValue(strJSonData, SearchBackLogIssue.class);
                                    }
                                    else
                                    {
                                        break;
                                    }

                                    if (searchIssue != null)
                                    {
                                        if (searchIssue.getIssues().size() > 0)
                                        {
                                            issuesToProcess = searchIssue.getIssues();
                                            issuesToProcess = ProcessBackLogIssuesForCommentAndChangeLog(userName, password, issuesToProcess, commentURI, changeLogURI, isCommentsRequired, isChangeLogRequired);
                                            backlogIssues.add(issuesToProcess);
                                        }
                                    }
                                    else
                                    {
                                        break;
                                    }

                                    //Run more if any records are pending to retrieve
                                    totalRecordsRetrieved = totalRecordsRetrieved + searchIssue.getIssues().size();
                                    startAt = totalRecordsRetrieved + 1;
                                } //End of While
                            }
                            else
                            {
                                backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(totalRecords);
                                strJSonData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);

                                if (!strJSonData.isEmpty())
                                {
                                    searchIssue = objectMapper.readValue(strJSonData, SearchBackLogIssue.class);
                                }

                                if (searchIssue != null)
                                {
                                    if (searchIssue.getIssues().size() > 0)
                                    {
                                        issuesToProcess = searchIssue.getIssues();
                                        issuesToProcess = ProcessBackLogIssuesForCommentAndChangeLog(userName, password, issuesToProcess, commentURI, changeLogURI, isCommentsRequired, isChangeLogRequired);
                                        backlogIssues.add(issuesToProcess);
                                    }
                                }
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                }
            }

            if (backlogIssues.size() > 0)
            {
                output = new ArrayList<>();
                for (List<BacklogIssue> bissue : backlogIssues)
                {
                    for (BacklogIssue issue : bissue)
                    {
                        output.add(issue);
                    }
                }
            }

            return output;
        }
        catch (Exception exMain)
        {
            return null;
        }
    }

    ////////
    public List<Issue> GetIssueDifference (String userName, String password, String baseURI, String backlogBoardId, String nblBoardId, String projectKey, int pageSize)
    {
        List<Issue> output = new ArrayList<>();
        output = null;
        List<Issue> backlogIssues = new ArrayList<>();
        List<Issue> nbacklogIssues = new ArrayList<>();
        List<Issue> projectIssues = new ArrayList<>();

        try
        {
            backlogIssues = null;
            nbacklogIssues = null;

            backlogIssues = getAllIssuesFromBoardV1(userName, password, baseURI, backlogBoardId, "", "", false, false, pageSize);
            nbacklogIssues = getAllIssuesFromBoardV1(userName, password, baseURI, nblBoardId, "", "", false, false, pageSize);
            String URI = baseURI + "/api/2/search?jql=project=" + projectKey;
            projectIssues = getIssuesUsingJQL(userName, password, URI, "", false, true, pageSize);

            boolean isExists = false;
            output = new ArrayList<>();
            for (Issue issue : projectIssues)
            {
                isExists = false;

                if (backlogIssues != null && backlogIssues.size() > 0)
                {
                    if (backlogIssues.stream().filter(x->x.getKey().equals(issue.getKey())).count() > 0)
                    {
                        isExists = true;
                    }
                }

                if (nbacklogIssues != null && nbacklogIssues.size() > 0)
                {
                    if (isExists == false)
                    {
                        if (nbacklogIssues.stream().filter(x->x.getKey().equals(issue.getKey())).count() > 0)
                        {
                            isExists = true;
                        }
                    }
                }

                if (isExists == false)
                {
                    output.add(issue);
                }
            }
            return output;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public List<Issue> getAllIssuesFromBoard (String userName, String password, String baseURI, String boardIds, String changeLogURI, String commentURI,boolean isChangeLogRequired, boolean isCommentsRequired, int pageSize)
    {
        List<Issue> output = new ArrayList<>();
        SearchIssue searchIssue = new SearchIssue();
        SearchIssue tSearchIssue = new SearchIssue();
        List<Issue> issuesToProcess = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<Issue>> backlogIssues = new ArrayList<>();

        String backlogBaseURI = baseURI + "/agile/latest/board/{BoardId}/backlog?";
        String backlogURI = "";
        String backlogURIFinal = "";
        String[] boards = (boardIds.split(","));
        int totalRecords = 0;
        int startAt = 0;
        int maxResults = 0;
        String strJSonData = "";
        int totalRecordsRetrieved = 0;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, false);

        output = null;
        try
        {
            for (String board : boards)
            {
                backlogURI = backlogBaseURI.replace("{BoardId}", board) + "";
                if (isChangeLogRequired == true)
                {
                    //change the URI if we need change log. Comments normally retrieved with the Issue
                    backlogURI = backlogBaseURI.replace("{BoardId}", board) + "expand=changelog&";
                }
                try
                {
                    backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=1";
                    strJSonData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);

                    if (!strJSonData.isEmpty())
                    {
                        searchIssue = objectMapper.readValue(strJSonData, SearchIssue.class);

                        if (searchIssue != null)
                        {
                            totalRecords = searchIssue.getTotal();
                        }

                        //Get the Pagesize and update in Max Results
                        maxResults = pageSize;
                        if (totalRecords > 0)
                        {
                            //Temp
                            //totalRecords = 200;
                            if (totalRecords  > maxResults)
                            {
                                totalRecordsRetrieved = 0;
                                //Loop and Get the data till it retrieved all the records
                                while ((totalRecords -1) > totalRecordsRetrieved)
                                {
                                    backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(maxResults);
                                    strJSonData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);

                                    if (!strJSonData.isEmpty())
                                    {
                                        searchIssue = objectMapper.readValue(strJSonData, SearchIssue.class);
                                    }
                                    else
                                    {
                                        break;
                                    }

                                    if (searchIssue != null)
                                    {
                                        if (searchIssue.getIssues().size() > 0)
                                        {
                                            issuesToProcess = searchIssue.getIssues();
                                            //issuesToProcess = ProcessBackLogIssuesForCommentAndChangeLogV1(userName, password, issuesToProcess, commentURI, changeLogURI, isCommentsRequired, isChangeLogRequired);
                                            backlogIssues.add(issuesToProcess);
                                        }
                                    }
                                    else
                                    {
                                        break;
                                    }

                                    //Run more if any records are pending to retrieve
                                    totalRecordsRetrieved = totalRecordsRetrieved + searchIssue.getIssues().size();
                                    startAt = totalRecordsRetrieved + 1;
                                } //End of While
                            }
                            else
                            {
                                backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(totalRecords);
                                strJSonData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);

                                if (!strJSonData.isEmpty())
                                {
                                    searchIssue = objectMapper.readValue(strJSonData, SearchIssue.class);
                                }

                                if (searchIssue != null)
                                {
                                    if (searchIssue.getIssues().size() > 0)
                                    {
                                        issuesToProcess = searchIssue.getIssues();
                                        //issuesToProcess = ProcessBackLogIssuesForCommentAndChangeLogV1(userName, password, issuesToProcess, commentURI, changeLogURI, isCommentsRequired, isChangeLogRequired);
                                        backlogIssues.add(issuesToProcess);
                                    }
                                }
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                }
            }

            if (backlogIssues.size() > 0)
            {
                output = new ArrayList<>();
                for (List<Issue> bissue : backlogIssues)
                {
                    for (Issue issue : bissue)
                    {
                        output.add(issue);
                    }
                }
            }

            return output;
        }
        catch (Exception exMain)
        {
            return null;
        }
    }

    public List<Issue> getIssuesUsingJQLOld (String userName, String password, String searchJQL, boolean isChangelogRequired, int pageSize)
    {
        List<Issue> output = new ArrayList<>();
        SearchIssue searchIssue = new SearchIssue();
        SearchIssue tSearchIssue = new SearchIssue();
        List<Issue> issuesToProcess = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<Issue>> backlogIssues = new ArrayList<>();

        String backlogURI = "";
        String backlogURIFinal = "";
        int totalRecords = 0;
        int startAt = 0;
        int maxResults = 0;
        String strJSonData = "";
        int totalRecordsRetrieved = 0;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, false);

        output = null;
        try
        {
            if (isChangelogRequired == true)
            {
                //change the URI if we need change log. Comments normally retrieved with the Issue
                backlogURI = searchJQL + "&expand=changelog&";
            }
            try
            {
                backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=1";
                strJSonData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);

                if (!strJSonData.isEmpty())
                {
                    searchIssue = objectMapper.readValue(strJSonData, SearchIssue.class);

                    if (searchIssue != null)
                    {
                        totalRecords = searchIssue.getTotal();
                    }

                    //Get the Pagesize and update in Max Results
                    maxResults = pageSize;
                    if (totalRecords > 0)
                    {
                        if (totalRecords  > maxResults)
                        {
                            totalRecordsRetrieved = 0;
                            //Loop and Get the data till it retrieved all the records
                            while ((totalRecords -1) > totalRecordsRetrieved)
                            {
                                backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(maxResults);
                                strJSonData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);

                                if (!strJSonData.isEmpty())
                                {
                                    searchIssue = objectMapper.readValue(strJSonData, SearchIssue.class);
                                }
                                else
                                {
                                    break;
                                }

                                if (searchIssue != null)
                                {
                                    if (searchIssue.getIssues().size() > 0)
                                    {
                                        issuesToProcess = searchIssue.getIssues();
                                        backlogIssues.add(issuesToProcess);
                                    }
                                }
                                else
                                {
                                    break;
                                }

                                //Run more if any records are pending to retrieve
                                totalRecordsRetrieved = totalRecordsRetrieved + searchIssue.getIssues().size();
                                startAt = totalRecordsRetrieved + 1;
                            } //End of While
                        }
                        else
                        {
                            backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(totalRecords);
                            strJSonData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);

                            if (!strJSonData.isEmpty())
                            {
                                searchIssue = objectMapper.readValue(strJSonData, SearchIssue.class);
                            }

                            if (searchIssue != null)
                            {
                                if (searchIssue.getIssues().size() > 0)
                                {
                                    issuesToProcess = searchIssue.getIssues();
                                    backlogIssues.add(issuesToProcess);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                String s = "";
            }

            if (backlogIssues.size() > 0)
            {
                output = new ArrayList<>();
                for (List<Issue> bissue : backlogIssues)
                {
                    for (Issue issue : bissue)
                    {
                        output.add(issue);
                    }
                }
            }

            return output;
        }
        catch (Exception exMain)
        {
            return null;
        }
    }

    public List<Issue> getAllIssuesFromBoardV1 (String userName, String password, String baseURI, String boardIds, String changeLogURI, String commentURI,boolean isChangeLogRequired, boolean isCommentsRequired, int pageSize)
    {
        List<Issue> output = new ArrayList<>();
        SearchIssue searchIssue = new SearchIssue();
        SearchIssue tSearchIssue = new SearchIssue();
        List<Issue> issuesToProcess = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<Issue>> backlogIssues = new ArrayList<>();
        //https://code.premierinc.com/issues/rest/agile/latest/board/846/issue?expand=changelog&startAt=0&maxResults=1

        String backlogBaseURI = baseURI + "/agile/latest/board/{BoardId}/issue?";
        String backlogURI = "";
        String backlogURIFinal = "";
        String[] boards = (boardIds.split(","));
        int totalRecords = 0;
        int startAt = 0;
        int maxResults = 0;
        String strJSonData = "";
        int totalRecordsRetrieved = 0;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, false);

        output = null;
        try
        {
            for (String board : boards)
            {
                backlogURI = backlogBaseURI.replace("{BoardId}", board) + "";
                if (isChangeLogRequired == true)
                {
                    //change the URI if we need change log. Comments normally retrieved with the Issue
                    backlogURI = backlogBaseURI.replace("{BoardId}", board) + "expand=changelog&";
                }
                try
                {
                    backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=1";
                    strJSonData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);

                    if (!strJSonData.isEmpty())
                    {
                        searchIssue = objectMapper.readValue(strJSonData, SearchIssue.class);

                        if (searchIssue != null)
                        {
                            totalRecords = searchIssue.getTotal();
                        }

                        //Get the Pagesize and update in Max Results
                        maxResults = pageSize;
                        if (totalRecords > 0)
                        {
                            //Temp
                            //totalRecords = 200;
                            if (totalRecords  > maxResults)
                            {
                                totalRecordsRetrieved = 0;
                                //Loop and Get the data till it retrieved all the records
                                while ((totalRecords -1) > totalRecordsRetrieved)
                                {
                                    backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(maxResults);
                                    searchIssue = restClient.exchangeWihCredential(backlogURIFinal, SearchIssue.class, userName, password);



                                    if (searchIssue != null)
                                    {
                                        if (searchIssue.getIssues().size() > 0)
                                        {
                                            issuesToProcess = searchIssue.getIssues();
                                            //issuesToProcess = ProcessBackLogIssuesForCommentAndChangeLogV1(userName, password, issuesToProcess, commentURI, changeLogURI, isCommentsRequired, isChangeLogRequired);
                                            backlogIssues.add(issuesToProcess);
                                        }
                                    }
                                    else
                                    {
                                        break;
                                    }

                                    //Run more if any records are pending to retrieve
                                    totalRecordsRetrieved = totalRecordsRetrieved + searchIssue.getIssues().size();
                                    startAt = totalRecordsRetrieved + 1;
                                } //End of While
                            }
                            else
                            {
                                backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(totalRecords);
                                searchIssue = restClient.exchangeWihCredential(backlogURIFinal, SearchIssue.class, userName, password);



                                if (searchIssue != null)
                                {
                                    if (searchIssue.getIssues().size() > 0)
                                    {
                                        issuesToProcess = searchIssue.getIssues();
                                        //issuesToProcess = ProcessBackLogIssuesForCommentAndChangeLogV1(userName, password, issuesToProcess, commentURI, changeLogURI, isCommentsRequired, isChangeLogRequired);
                                        backlogIssues.add(issuesToProcess);
                                    }
                                }
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                }
            }

            if (backlogIssues.size() > 0)
            {
                output = new ArrayList<>();
                for (List<Issue> bissue : backlogIssues)
                {
                    for (Issue issue : bissue)
                    {
                        output.add(issue);
                    }
                }
            }

            return output;
        }
        catch (Exception exMain)
        {
            return null;
        }
    }

    //////////

    private List<BacklogIssue> ProcessBackLogIssuesForCommentAndChangeLog (String userName, String password, List<BacklogIssue> issuesTobeProcessed, String commentURI, String changelogURI, boolean isCommentRequired, boolean isChangeLogRequired)
    {
        List<BacklogIssue> output = new ArrayList<>();
        try
        {
            for (BacklogIssue issue: issuesTobeProcessed)
            {
                ChangeLog changeLog = null;
                Comment comment = null;
                premier.premierslaautomate.Models.FieldForBacklogIssue field = issue.getFields();
                BacklogIssue addIssue = new BacklogIssue();

                if (isCommentRequired == true)
                {
                    comment = getAllCommentsForIssue(userName, password, commentURI, issue.getKey());
                    if (comment != null)
                    {
                        field.setComment(comment);
                    }
                }

                if (isChangeLogRequired == true)
                {
                    changeLog = getAllChangeLogForIssue(userName, password, changelogURI, issue.getKey());
                    if (changeLog != null)
                    {
                       issue.setChangelog(changeLog);
                    }
                }

                addIssue.setKey(issue.getKey());
                addIssue.setSelf(issue.getSelf());
                addIssue.setId(issue.getId());
                addIssue.setExpand(issue.getExpand());
                addIssue.setChangelog(issue.getChangelog());
                addIssue.setFields(issue.getFields());
                output.add(addIssue);
            }
        }
        catch (Exception ex)
        {

        }
        return output;
    }

    private List<IssueV1> ProcessBackLogIssuesForCommentAndChangeLogV1 (String userName, String password, List<IssueV1> issuesTobeProcessed, String commentURI, String changelogURI, boolean isCommentRequired, boolean isChangeLogRequired)
    {
        List<IssueV1> output = new ArrayList<>();
        IssueV1 addIssue = new IssueV1();
        String issueKeys = "";
        output = null;
        try
        {
            //Comments retrieval is not required here as the issues comes with commeent if we pull it from board.
            if (issuesTobeProcessed != null && issuesTobeProcessed.size() > 0)
            {
                output = new ArrayList<>();
                for (IssueV1 issue: issuesTobeProcessed)
                {
                    addIssue = new IssueV1();
                    issueKeys = issueKeys + issue.getKey() + ",";
                    addIssue.setKey(issue.getKey());
                    addIssue.setSelf(issue.getSelf());
                    addIssue.setId(issue.getId());
                    addIssue.setExpand(issue.getExpand());
                    addIssue.setFields(issue.getFields());
                    output.add(addIssue);
                }

                if (isChangeLogRequired == true)
                {
                    if (!issueKeys.isEmpty())
                    {
                        issueKeys = issueKeys.substring(0, issueKeys.length()-1);
                        List<IssueV1> changeLogIssue = getIssuesWithChangeLog(userName, password, changelogURI, issueKeys);
                        for(IssueV1 issue : output)
                        {

                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {

        }
        return output;
    }

    private List<Issue> GetCommentsForIssue (String userName, String password, List<Issue> issuesTobeProcessed, String commentURI, boolean isCommentRequired)
    {
        List<Issue> output = new ArrayList<>();
        try
        {
            for (Issue issue: issuesTobeProcessed)
            {
                ChangeLog changeLog = null;
                Comment comment = null;
                premier.premierslaautomate.Models.Field field = issue.getFields();
                Issue addIssue = new Issue();

                if (isCommentRequired == true)
                {
                    comment = getAllCommentsForIssue(userName, password, commentURI, issue.getKey());

                    if (comment != null)
                    {
                        field.setComment(comment);
                    }
                }

                addIssue.setKey(issue.getKey());
                addIssue.setSelf(issue.getSelf());
                addIssue.setId(issue.getId());
                addIssue.setExpand(issue.getExpand());
                addIssue.setChangelog(issue.getChangelog());
                addIssue.setFields(issue.getFields());
                output.add(addIssue);
            }
        }
        catch (Exception ex)
        {

        }
        return output;

    }

    public List<Issue> getAllIssuesFromBacklogOld (String userName, String password, String baseURI, String boardIds, String changeLogURI, String commentURI,boolean isChangeLogRequired, boolean isCommentsRequired)
    {
        List<Issue> issues = new ArrayList<>();
        List<Issue> output = new ArrayList<>();
        String backlogBaseURI = baseURI + "/agile/latest/board/{BoardId}/backlog?";
        String backlogURI = "";
        String backlogURIFinal = "";
        String[] boards = (boardIds.split(","));
        String issueData = "";
        int totalRecords = 0;
        int startAt = 0;
        int maxResults = 10;
        int startAtIncrement = 1;
        int maxResultsIncrement = 50;
        JSONArray issueArray = new JSONArray();
        JSONArray jsonOtherArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonFields = new JSONObject();
        JSONObject jsonOthers = new JSONObject();
        JiraField jiraField = new JiraField();
        JiraIssue jiraIssue = new JiraIssue();
        JiraPriority jiraPriority = new JiraPriority();

        String strValue = "";
        int intValue = 0;

        for (String board : boards)
        {
            backlogURI = backlogBaseURI.replace("{BoardId}", board);
            backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(maxResults);

            try
            {
                //Object obj = restClient.exchangeWihCredential(backlogURIFinal, Object.class, userName, password);
                issueData = restClient.exchangeWihCredential(backlogURIFinal, String.class, userName, password);
                jsonObject= new JSONObject(issueData);

                if (jsonObject != null)
                {
                    startAt = jsonObject.getInt("startAt");
                    totalRecords = jsonObject.getInt("total");
                    issueArray = jsonObject.getJSONArray("issues");

                    if (issueArray != null && issueArray.length() > 0)
                    {
                        for (int i = 0; i < issueArray.length(); i++)
                        {
                            jsonObject = (JSONObject) issueArray.get(i);

                            jiraIssue = new JiraIssue();
                            jiraIssue.setExpand(jsonObject.getString("expand"));
                            jiraIssue.setId(jsonObject.getInt("id"));
                            jiraIssue.setSelf(jsonObject.getString("self"));
                            jiraIssue.setKey(jsonObject.getString("key"));

                            jsonFields = jsonObject.getJSONObject("fields");

                            //Get the Fields by checking the element
                            jiraField = new JiraField();
                            if (jsonFields != null)
                            {
                                //Original Estimation
                                try
                                {
                                    strValue = jsonFields.getString("customfield_14000");
                                    jiraField.setCustomfield_14000(strValue);
                                }
                                catch (Exception ex) {}

                                //Actual Hours
                                try
                                {
                                    strValue = jsonFields.getString("customfield_14001");
                                    jiraField.setCustomfield_14001(strValue);
                                }
                                catch (Exception ex){}

                                //Fixed Version
                                try
                                {
                                    jsonOtherArray = jsonFields.getJSONArray("fixVersions");
                                    if (jsonOtherArray != null && jsonOtherArray.length() > 0)
                                    {
                                        JiraFixedVersions fixedVersions = new JiraFixedVersions();
                                        List<JiraFixedVersions> lstFixedVersion= new ArrayList<>();
                                        for (int j = 0; i < jsonOtherArray.length(); j++)
                                        {
                                            jsonOthers = (JSONObject) jsonOtherArray.get(j);

                                            if (jsonOthers != null)
                                            {
                                                fixedVersions = new JiraFixedVersions();
                                                fixedVersions.setId(jsonOthers.getString("id"));
                                                fixedVersions.setSelf(jsonOthers.getString("self"));
                                                fixedVersions.setDescription(jsonOthers.getString("description"));
                                                fixedVersions.setName(jsonOthers.getString("name"));
                                                fixedVersions.setArchived(jsonOthers.getBoolean("archived"));
                                                fixedVersions.setReleased(jsonOthers.getBoolean("released"));
                                                fixedVersions.setReleaseDate(jsonOthers.getString("releaseDate"));
                                                lstFixedVersion.add(fixedVersions);
                                            }
                                        }

                                        if (lstFixedVersion.size() > 0)
                                        {
                                            jiraField.setFixVersions(lstFixedVersion);
                                        }
                                    }
                                }
                                catch (Exception ex){}

                                //Priority
                                try
                                {
                                    jsonOthers = null;
                                    jsonOthers = jsonFields.getJSONObject("priority");

                                    if (jsonOthers != null)
                                    {
                                        jiraPriority = new JiraPriority();
                                        jiraPriority.setSelf(jsonOthers.getString("self"));
                                        jiraPriority.setId(jsonOthers.getString("id"));
                                        jiraPriority.setName(jsonOthers.getString("name"));
                                        jiraPriority.setIconUrl(jsonOthers.getString("iconUrl"));
                                        jiraField.setPriority(jiraPriority);
                                    }
                                }
                                catch (Exception ex){}

                                //Labels
                                try
                                {
                                    jsonOtherArray = jsonFields.getJSONArray("labels");
                                    if (jsonOtherArray != null && jsonOtherArray.length() > 0)
                                    {
                                        List<String> labels = new ArrayList<>();
                                        for (int j = 0; j < jsonOtherArray.length(); j++)
                                        {
                                            strValue = jsonOtherArray.getString(j);
                                            labels.add(strValue);
                                        }

                                        if (labels.size() > 0)
                                        {
                                            jiraField.setLabels(labels);
                                        }
                                    }
                                }
                                catch (Exception ex){}

                                //Issue Links
                                try
                                {
                                    jsonOtherArray = jsonFields.getJSONArray("issuelinks");
                                    if (jsonOtherArray != null && jsonOtherArray.length() > 0)
                                    {
                                        List<JiraIssueLink> issueLinks = new ArrayList<>();
                                        JiraIssueLink issueLink = new JiraIssueLink();
                                        for (int j = 0; j < jsonOtherArray.length(); j++)
                                        {
                                            jsonOthers = jsonOtherArray.getJSONObject(j);
                                            //Gson gson = new GsonBuilder().create();
                                            ObjectMapper objectMapper = new ObjectMapper();
                                            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                                            //issueLink = null;
                                            issueLink = objectMapper.readValue(jsonOthers.toString(), JiraIssueLink.class);
                                            if (issueLink != null)
                                            {
                                                issueLinks.add(issueLink);
                                            }

                                            if (issueLinks.size() > 0)
                                            {
                                                jiraField.setIssuelinks(issueLinks);
                                            }
                                            String pp = "";
                                        }
                                    }
                                }
                                catch (Exception ex){
                                    String gg = "";
                                }


                                String mm = "";



                            }




                        }
                    }
                }


                String aa = "";
            }
            catch (Exception ex)
            {
                String a = "";
            }

        }
        return output;
    }

    public Comment getAllCommentsForIssue (String userName, String password, String commentURI, String issueKey)
    {
        Comment comment = new Comment();
        Issue thisIssue = new Issue();
        comment = null;
        try
        {
            commentURI = commentURI + "/" + issueKey;
            thisIssue = restClient.exchangeWihCredential(commentURI,Issue.class, userName, password);
            if (thisIssue != null)
            {
                if (thisIssue.getFields() != null)
                {
                    if (thisIssue.getFields().getComment() != null)
                    {


                        comment = thisIssue.getFields().getComment();
                    }
                }
            }
            return comment;
        }
        catch (Exception ex)
        {
            return comment;
        }
    }

    public ChangeLog getAllChangeLogForIssue (String userName, String password, String changeLogURI, String issueKey)
    {
        ChangeLog changeLog = new ChangeLog();
        Issue thisIssue = new Issue();
        changeLog = null;
        try
        {
            changeLogURI = changeLogURI + "jql=issuekey=" + issueKey + "&expand=changelog";
            SearchIssue searchIssue = restClient.exchangeWihCredential(changeLogURI,SearchIssue.class, userName, password);

            if (searchIssue != null)
            {
                if (searchIssue.getIssues().get(0).getChangelog() != null)
                {
                    changeLog = searchIssue.getIssues().get(0).getChangelog();
                }
            }
            return changeLog;
        }
        catch (Exception ex)
        {
            return changeLog;
        }
    }

    public Comment getAllCommentsForIssueV1 (String userName, String password, String commentURI, String issueKey)
    {
        Comment comment = new Comment();
        IssueV1 thisIssue = new IssueV1();
        comment = null;
        try
        {
            commentURI = commentURI + "/" + issueKey;
            thisIssue = restClient.exchangeWihCredential(commentURI,IssueV1.class, userName, password);
            if (thisIssue != null)
            {
                if (thisIssue.getFields() != null)
                {
                    if (thisIssue.getFields().getComment() != null)
                    {


                        comment = thisIssue.getFields().getComment();
                    }
                }
            }
            return comment;
        }
        catch (Exception ex)
        {
            return comment;
        }
    }

    public ChangeLog getAllChangeLogForIssueV2 (String userName, String password, String changeLogURI, String issueKey)
    {
        ChangeLog changeLog = new ChangeLog();
        Issue thisIssue = new Issue();
        changeLog = null;
        try
        {
            changeLogURI = changeLogURI + "jql=issuekey in (" + issueKey + ")&expand=changelog";
            SearchIssue searchIssue = restClient.exchangeWihCredential(changeLogURI,SearchIssue.class, userName, password);

            if (searchIssue != null)
            {
                if (searchIssue.getIssues().get(0).getChangelog() != null)
                {
                    changeLog = searchIssue.getIssues().get(0).getChangelog();
                }
            }
            return changeLog;
        }
        catch (Exception ex)
        {
            return changeLog;
        }
    }

    public List<IssueV1> getIssuesWithChangeLog (String userName, String password, String changeLogURI, String issueKey)
    {
        List<IssueV1> output = new ArrayList<>();
        try
        {
            output = null;
            changeLogURI = changeLogURI + "jql=issuekey in (" + issueKey + ")&expand=changelog";
            SearchIssueV1 searchIssue = restClient.exchangeWihCredential(changeLogURI,SearchIssueV1.class, userName, password);

            if (searchIssue != null)
            {
                if (searchIssue.getIssues().size() > 0)
                {
                    output = searchIssue.getIssues();
                }
            }
            return output;
        }
        catch (Exception ex)
        {
            return output;
        }
    }
    //-------------------------------------------------------------------------------------------------//

    ////////////////////////////// FINAL LIST OF Methods -- START
    public List<Issue> getIssuesUsingJQL (String userName, String password, String searchJQL, String commentURI, boolean isCommentRequired, boolean isChangelogRequired, int pageSize)
    {
        List<Issue> output = new ArrayList<>();
        SearchIssue searchIssue = new SearchIssue();
        List<Issue> issuesToProcess = new ArrayList<>();
        List<List<Issue>> backlogIssues = new ArrayList<>();

        String backlogURI = "";
        String backlogURIFinal = "";
        int totalRecords = 0;
        int startAt = 0;
        int maxResults = 0;

        Map<String, List<Issue>> totalIssues = new ConcurrentHashMap<>();
        output = null;

        try
        {
            if (isChangelogRequired == true)
            {
                //change the URI if we need change log. Comments normally retrieved with the Issue
                backlogURI = searchJQL + "&expand=changelog&";
            }
            else
            {
                backlogURI = searchJQL + "&";
            }

            final String URI = backlogURI;
            try
            {
                backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=1";
                searchIssue = restClient.exchangeWihCredential(backlogURIFinal, SearchIssue.class, userName, password);

                if (searchIssue != null)
                {
                    totalRecords = searchIssue.getTotal();
                }

                //Get the Pagesize and update in Max Results
                maxResults = pageSize;
                final int totalMaxResult = maxResults;
                if (totalRecords > 0)
                {
                    if (totalRecords  > maxResults)
                    {
                        //Loop and Get the data till it retrieved all the records
                        List<Integer> listInt = new ArrayList<>();
                        int totalCalls = totalRecords/maxResults;
                        int start = 0;
                        while (totalCalls >= start)
                        {
                            listInt.add(start);
                            start++;
                        }

                        listInt.parallelStream().forEach((server) -> {
                            int startFrom = 0;
                            startFrom = (totalMaxResult * server);
                            if (startFrom > 0)
                            {
                                startFrom = startFrom + 1;
                            }

                            String URIFinal = URI + "startAt=" + String.valueOf(startFrom) + "&maxResults=" + totalMaxResult;
                            SearchIssue retrieveIssue = restClient.exchangeWihCredential(URIFinal, SearchIssue.class, userName, password);

                            if (retrieveIssue != null)
                            {
                                if (retrieveIssue.getIssues().size() > 0)
                                {
                                    totalIssues.put(URIFinal, retrieveIssue.getIssues());
                                }
                            }
                        });

                        for (List<Issue> issues : totalIssues.values())
                        {
                            if (issues != null && issues.size() > 0)
                            {
                                backlogIssues.add(issues);
                            }
                        }
                    }
                    else
                    {
                        backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(totalRecords);
                        searchIssue = restClient.exchangeWihCredential(backlogURIFinal, SearchIssue.class, userName, password);

                        if (searchIssue != null)
                        {
                            if (searchIssue.getIssues().size() > 0)
                            {
                                issuesToProcess = searchIssue.getIssues();
                                backlogIssues.add(issuesToProcess);
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                String s = "";
            }

            if (backlogIssues.size() > 0)
            {
                output = new ArrayList<>();
                for (List<Issue> bissue : backlogIssues)
                {
                    for (Issue issue : bissue)
                    {
                        output.add(issue);
                    }
                }
            }

            return output;
        }
        catch (Exception exMain)
        {
            return null;
        }
    }

    public List<Issue> getIssuesUsingBoard (String userName, String password, String baseURI, String boardId, boolean isChangeLogRequired, int pageSize)
    {
        List<Issue> output = new ArrayList<>();
        SearchIssue searchIssue = new SearchIssue();
        List<Issue> issuesToProcess = new ArrayList<>();
        List<List<Issue>> backlogIssues = new ArrayList<>();

        String backlogBaseURI = baseURI + "/agile/latest/board/" + boardId +"/issue?";

        String backlogURI = "";
        String backlogURIFinal = "";

        int totalRecords = 0;
        int startAt = 0;
        int maxResults = 0;

        Map<String, List<Issue>> totalIssues = new ConcurrentHashMap<>();
        output = null;
        try
        {
            if (isChangeLogRequired == true)
            {
                //change the URI if we need change log. Comments normally retrieved with the Issue
                backlogURI = backlogBaseURI + "expand=changelog&";
            }

            final String URI = backlogURI;
            try
            {
                backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=1";
                searchIssue = restClient.exchangeWihCredential(backlogURIFinal, SearchIssue.class, userName, password);
                if (searchIssue != null)
                {
                    totalRecords = searchIssue.getTotal();
                }

                //Get the Pagesize and update in Max Results
                maxResults = pageSize;
                final int totalMaxResult = maxResults;

                if (totalRecords > 0)
                {
                    if (totalRecords  > maxResults)
                    {
                        List<Integer> listInt = new ArrayList<>();
                        int totalCalls = totalRecords/maxResults;
                        int start = 0;
                        while (totalCalls >= start)
                        {
                            listInt.add(start);
                            start++;
                        }

                        listInt.parallelStream().forEach((server) -> {
                            String strKey = "";
                            int startFrom = 0;
                            startFrom = (totalMaxResult * server);
                            if (startFrom > 0)
                            {
                                startFrom = startFrom + 1;
                            }
                            strKey = "Key" + String.valueOf(startFrom);
                            //System.out.println("Key :" + strKey);
                            String URIFinal = URI + "startAt=" + String.valueOf(startFrom) + "&maxResults=" + totalMaxResult;
                            SearchIssue retrieveIssue = restClient.exchangeWihCredential(URIFinal, SearchIssue.class, userName, password);

                            if (retrieveIssue != null)
                            {
                                if (retrieveIssue.getIssues().size() > 0)
                                {
                                    totalIssues.put(strKey, retrieveIssue.getIssues());
                                }
                            }
                        });

                        for (List<Issue> issues : totalIssues.values())
                        {
                            if (issues != null && issues.size() > 0)
                            {
                                backlogIssues.add(issues);
                            }
                        }
                    }
                    else
                    {
                        backlogURIFinal = backlogURI + "startAt=" + String.valueOf(startAt) + "&maxResults=" + String.valueOf(totalRecords);
                        searchIssue = restClient.exchangeWihCredential(backlogURIFinal, SearchIssue.class, userName, password);
                        if (searchIssue != null)
                        {
                            if (searchIssue.getIssues().size() > 0)
                            {
                                issuesToProcess = searchIssue.getIssues();
                                backlogIssues.add(issuesToProcess);
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                String a = "";
                throw new RuntimeException("Error while retriving your data");
            }

            if (backlogIssues.size() > 0)
            {
                output = new ArrayList<>();
                for (List<Issue> bissue : backlogIssues)
                {
                    for (Issue issue : bissue)
                    {
                        output.add(issue);
                    }
                }
            }

            return output;
        }
        catch (Exception exMain)
        {
            return null;
        }
    }

    public List<Issue> getAllIssuesOnJQLV3(String userName, String password, String searchURI, String changeLogBaseURI, String commentbaseURI, boolean changeLogRequired, boolean commentsRequired,boolean closedsprints)
    {
        List<Issue> issues = new ArrayList<>();
        List<Issue> finalissue = new ArrayList<>();
        String issueURI = "";
        try
        {
            SearchIssue searchIssue = restClient.exchangeWihCredential(searchURI, SearchIssue.class, userName, password);
            issues = searchIssue.getIssues();
            //Loop till you get all the results. Update the isses object and then
            Issue thisIssue = new Issue();
            //Get All the Change Log
            for (Issue issue: issues)
            {
                Issue issueToAdd = new Issue();
                premier.premierslaautomate.Models.Field field = issue.getFields();
                ChangeLog changeLog = null;
                Comment comment = null;

                if (changeLogRequired)
                {
                    issueURI =changeLogBaseURI + "jql=issuekey=" + issue.getKey() + "&expand=changelog";
                    searchIssue = restClient.exchangeWihCredential(issueURI,SearchIssue.class, userName, password);
                    if (searchIssue != null)
                    {
                        if (searchIssue.getIssues().size() > 0)
                        {
                            if (searchIssue.getIssues().get(0).getChangelog() != null)
                            {
                                //changeLog = searchIssue.getIssues().get(0).getChangelog();
                                issue.setChangelog(searchIssue.getIssues().get(0).getChangelog());
                            }
                        }
                    }
                }


                if (commentsRequired)
                {
                    issueURI = commentbaseURI + "/" + issue.getKey();
                    comment = null;
                    thisIssue = restClient.exchangeWihCredential(issueURI,Issue.class, userName, password);
                    if (thisIssue != null)
                    {
                        if (thisIssue.getFields() != null)
                        {
                            if (thisIssue.getFields().getComment() != null)
                            {
                                //comment = searchIssue.getIssues().get(0).getFields().getComment();
                                field.setComment(thisIssue.getFields().getComment());
                            }
                        }
                    }

                }

                if (closedsprints)
                {
                    issueURI = commentbaseURI + "/" + issue.getKey();
                    comment = null;
                    thisIssue = restClient.exchangeWihCredential(issueURI,Issue.class, userName, password);
                    if (thisIssue != null)
                    {
                        if (thisIssue.getFields() != null)
                        {
                            if (thisIssue.getFields().getClosedSprints() != null)
                            {
                                //comment = searchIssue.getIssues().get(0).getFields().getComment();
                                field.setClosedSprints(thisIssue.getFields().getClosedSprints());
                            }
                        }
                    }

                }

                issueToAdd.setKey(issue.getKey());
                issueToAdd.setSelf(issue.getSelf());
                issueToAdd.setId(issue.getId());
                issueToAdd.setExpand(issue.getExpand());
                issueToAdd.setChangelog(issue.getChangelog());
                issueToAdd.setFields(issue.getFields());
                finalissue.add(issueToAdd);
            }

            return finalissue;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }
    /////////////////////////////  FINAL LIST OF Methods -- END


}

