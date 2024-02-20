package premier.premierslaautomate.DataServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import premier.premierslaautomate.Interfaces.IAdoDataService;

import premier.premierslaautomate.Models.ADO.*;
import premier.premierslaautomate.Utilities.CommonUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdoDataService implements IAdoDataService
{
    @Autowired
    private RestTemplate restTemplate1;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private SlaRestClientService restClientService;

    private CommonUtil util = new CommonUtil();

    private HttpHeaders SetHeader (String userName, String password)
    {
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(userName, password);
            headers.setContentType(MediaType.APPLICATION_JSON);
            return headers;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public List<WorkItem> getWorkitems (String userName, String password, String searchURL, String wiqlRequest, String httpMethod, boolean isCommentRequired, boolean isHistoryRequired, int pageSize)
    {
        List<WorkItem> workItems = new ArrayList<>();
        workItems = null;
        try
        {
            SearchWorkitemByQuery searchWorkitemByQuery = new SearchWorkitemByQuery();
            searchWorkitemByQuery = null;
            HttpHeaders headers = SetHeader(userName, password);
            if (headers != null)
            {
                HttpEntity<String> entity = null;
                if (!wiqlRequest.isEmpty())
                {
                    entity = new HttpEntity <> (wiqlRequest,headers);
                    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(searchURL);
                    String qURL = builder.build().encode().toUriString();
                    searchWorkitemByQuery = restTemplate.exchange(qURL, HttpMethod.POST, entity, SearchWorkitemByQuery.class).getBody();

                    if (searchWorkitemByQuery != null)
                    {
                        if (searchWorkitemByQuery.getWorkItems() != null && searchWorkitemByQuery.getWorkItems().size() > 0)
                        {
                            workItems = new ArrayList<>();
                            for (WorkItemsHeader workItemHeader: searchWorkitemByQuery.getWorkItems())
                            {
                                WorkItemObject workItem = null;
                                WorkItem workItemToAdd = null;
                                Comment comment = null;
                                Revision revision = null;

                                //Get the workitem
                                entity = new HttpEntity <> (headers);
                                builder = UriComponentsBuilder.fromUriString(workItemHeader.getUrl());
                                qURL = builder.build().encode().toUriString();
                                workItem = restTemplate.exchange(qURL, HttpMethod.GET, entity, WorkItemObject.class).getBody();

                                if (workItem != null)
                                {
                                    workItemToAdd = new WorkItem();
                                    workItemToAdd.setId(workItem.getId());
                                    workItemToAdd.setRev(workItem.getRev());
                                    Map<String,Object> mapFields = (Map<String, Object>) workItem.getFields();
                                    WorkItemFields fields = null;
                                    fields = util.setupWorkItemFields(mapFields);
                                    workItemToAdd.setFields(fields);

                                    //Get the Links
                                    String commentLink = "";
                                    String revisionLink = "";
                                    Map<String,Object> mapLinks = (Map<String, Object>) workItem.get_links();
                                    if (mapLinks != null)
                                    {
                                        for (Map.Entry<String, Object> mapurl : mapLinks.entrySet())
                                        {
                                            if (mapurl.getKey().equals("workItemComments"))
                                            {
                                                Map<String, Object> commentMap = (Map<String, Object>) mapurl.getValue();
                                                if (commentMap != null)
                                                {
                                                    commentLink = commentMap.get("href").toString();
                                                }
                                            }

                                            if (mapurl.getKey().equals("workItemRevisions"))
                                            {
                                                Map<String, Object> commentRev = (Map<String, Object>) mapurl.getValue();
                                                if (commentRev != null)
                                                {
                                                    revisionLink = commentRev.get("href").toString();
                                                }
                                            }
                                        }
                                    }

                                    if (isCommentRequired == true && !commentLink.isEmpty())
                                    {
                                        //commentLink = "https://dev.azure.com/premierinc/32912845-d34e-48fb-83c6-8f28f005b1c3/_apis/wit/workItems/27498/comments";

                                        entity = new HttpEntity <> (headers);
                                        builder = UriComponentsBuilder.fromUriString(commentLink);
                                        qURL = builder.build().encode().toUriString();
                                        comment = restTemplate.exchange(qURL, HttpMethod.GET, entity, Comment.class).getBody();
                                        workItemToAdd.setComment(comment);
                                    }

                                    if (isHistoryRequired == true && !revisionLink.isEmpty())
                                    {
                                        entity = new HttpEntity <> (headers);
                                        builder = UriComponentsBuilder.fromUriString(revisionLink);
                                        qURL = builder.build().encode().toUriString();
                                        revision = restTemplate.exchange(qURL, HttpMethod.GET, entity, Revision.class).getBody();

                                        if (revision != null)
                                        {
                                            if (revision.getValue() != null && revision.getValue().size()>0)
                                            {
                                                List<RevisionValue> lstRevisions = new ArrayList<>();
                                                for (RevisionValueObject rev: revision.getValue())
                                                {
                                                    if (rev != null)
                                                    {
                                                        RevisionValue thisRevision = new RevisionValue();
                                                        thisRevision.setId(rev.getId());
                                                        thisRevision.setRev(rev.getRev());

                                                        RevisionFields rField = null;
                                                        Map<String,Object> mapRevision = (Map<String, Object>) rev.getFields();
                                                        if (mapRevision != null)
                                                        {
                                                            rField = new RevisionFields();
                                                            if (mapRevision.get("System.WorkItemType") != null)
                                                            {
                                                                rField.setWorkItemType(String.valueOf(mapRevision.get("System.WorkItemType")));
                                                            }
                                                            if (mapRevision.get("System.State") != null)
                                                            {
                                                                rField.setState(String.valueOf(mapRevision.get("System.State")));
                                                            }
                                                            if (mapRevision.get("System.Reason") != null)
                                                            {
                                                                rField.setReason(String.valueOf(mapRevision.get("System.Reason")));
                                                            }
                                                            if (mapRevision.get("System.CreatedDate") != null)
                                                            {
                                                                rField.setCreatedDate(String.valueOf(mapRevision.get("System.CreatedDate")));
                                                            }
                                                            if (mapRevision.get("System.ChangedDate") != null)
                                                            {
                                                                rField.setChangedDate(String.valueOf(mapRevision.get("System.ChangedDate")));
                                                            }
                                                            if (mapRevision.get("System.IterationPath") != null)
                                                            {
                                                                rField.setIterationPath(String.valueOf(mapRevision.get("System.IterationPath")));
                                                            }
                                                            if (mapRevision.get("Custom.RevisedDueDate") != null)
                                                            {
                                                                rField.setRevisedDueDate(String.valueOf(mapRevision.get("Custom.RevisedDueDate")));
                                                            }

                                                            if (mapRevision.get("Microsoft.VSTS.Common.StateChangeDate") != null)
                                                            {
                                                                rField.setStateChangeDate(String.valueOf(mapRevision.get("Microsoft.VSTS.Common.StateChangeDate")));
                                                            }

                                                            rField.setOriginalEffort(-1); //Setting this as -1 if data is not available
                                                            if (mapRevision.get("Microsoft.VSTS.Scheduling.OriginalEstimate") != null)
                                                            {
                                                                try
                                                                {
                                                                    rField.setOriginalEffort(Double.parseDouble(String.valueOf(mapRevision.get("Microsoft.VSTS.Scheduling.OriginalEstimate"))));
                                                                }
                                                                catch (Exception ex)
                                                                {
                                                                }
                                                            }

                                                            thisRevision.setFields(rField);
                                                        }

                                                        lstRevisions.add(thisRevision);
                                                    }
                                                }
                                                workItemToAdd.setRevisions(lstRevisions);
                                            }
                                        }
                                    }

                                    workItems.add(workItemToAdd);
                                }
                            }
                        }
                    }
                }
                else
                {
                    entity = new HttpEntity <> (headers);
                }
            }

            return workItems;
        }
        catch (Exception ex)
        {
            String b = "";
        }
        return workItems;
    }

    public List<WorkItem> getWorkitems (String userName, String password, String searchURL, String wiqlRequest, String httpMethod, boolean isCommentRequired, boolean isHistoryRequired, boolean getOnlyTransitionHistory, int pageSize)
    {
        List<WorkItem> workItems = new ArrayList<>();
        workItems = null;
        try
        {
            SearchWorkitemByQuery searchWorkitemByQuery = new SearchWorkitemByQuery();
            searchWorkitemByQuery = null;
            HttpHeaders headers = SetHeader(userName, password);
            if (headers != null)
            {
                HttpEntity<String> entity = null;
                if (!wiqlRequest.isEmpty())
                {
                    entity = new HttpEntity <> (wiqlRequest,headers);
                    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(searchURL);
                    String qURL = builder.build().encode().toUriString();
                    searchWorkitemByQuery = restTemplate.exchange(qURL, HttpMethod.POST, entity, SearchWorkitemByQuery.class).getBody();

                    if (searchWorkitemByQuery != null)
                    {
                        if (searchWorkitemByQuery.getWorkItems() != null && searchWorkitemByQuery.getWorkItems().size() > 0)
                        {
                            workItems = new ArrayList<>();
                            for (WorkItemsHeader workItemHeader: searchWorkitemByQuery.getWorkItems())
                            {
                                WorkItemObject workItem = null;
                                WorkItem workItemToAdd = null;
                                Comment comment = null;
                                Revision revision = null;

                                //Get the workitem
                                entity = new HttpEntity <> (headers);
                                builder = UriComponentsBuilder.fromUriString(workItemHeader.getUrl());
                                qURL = builder.build().encode().toUriString();
                                workItem = restTemplate.exchange(qURL, HttpMethod.GET, entity, WorkItemObject.class).getBody();

                                if (workItem != null)
                                {
                                    workItemToAdd = new WorkItem();
                                    workItemToAdd.setId(workItem.getId());
                                    workItemToAdd.setRev(workItem.getRev());
                                    Map<String,Object> mapFields = (Map<String, Object>) workItem.getFields();
                                    WorkItemFields fields = null;
                                    fields = util.setupWorkItemFields(mapFields);
                                    workItemToAdd.setFields(fields);

                                    //Get the Links
                                    String commentLink = "";
                                    String revisionLink = "";
                                    Map<String,Object> mapLinks = (Map<String, Object>) workItem.get_links();
                                    if (mapLinks != null)
                                    {
                                        for (Map.Entry<String, Object> mapurl : mapLinks.entrySet())
                                        {
                                            if (mapurl.getKey().equals("workItemComments"))
                                            {
                                                Map<String, Object> commentMap = (Map<String, Object>) mapurl.getValue();
                                                if (commentMap != null)
                                                {
                                                    commentLink = commentMap.get("href").toString();
                                                }
                                            }

                                            if (mapurl.getKey().equals("workItemRevisions"))
                                            {
                                                Map<String, Object> commentRev = (Map<String, Object>) mapurl.getValue();
                                                if (commentRev != null)
                                                {
                                                    revisionLink = commentRev.get("href").toString();
                                                }
                                            }
                                        }
                                    }

                                    if (isCommentRequired == true && !commentLink.isEmpty())
                                    {
                                        //commentLink = "https://dev.azure.com/premierinc/32912845-d34e-48fb-83c6-8f28f005b1c3/_apis/wit/workItems/27498/comments";

                                        entity = new HttpEntity <> (headers);
                                        builder = UriComponentsBuilder.fromUriString(commentLink);
                                        qURL = builder.build().encode().toUriString();
                                        comment = restTemplate.exchange(qURL, HttpMethod.GET, entity, Comment.class).getBody();
                                        workItemToAdd.setComment(comment);
                                    }

                                    if (isHistoryRequired == true && !revisionLink.isEmpty())
                                    {
                                        entity = new HttpEntity <> (headers);
                                        builder = UriComponentsBuilder.fromUriString(revisionLink);
                                        qURL = builder.build().encode().toUriString();
                                        revision = restTemplate.exchange(qURL, HttpMethod.GET, entity, Revision.class).getBody();

                                        boolean isStateChanged = false;
                                        String workItemStateprev = "";
                                        String workItemStatecurrent = "";

                                        if (revision != null)
                                        {
                                            if (revision.getValue() != null && revision.getValue().size()>0)
                                            {
                                                List<RevisionValue> lstRevisions = new ArrayList<>();

                                                for (RevisionValueObject rev: revision.getValue())
                                                {
                                                    if (rev != null)
                                                    {
                                                        RevisionValue thisRevision = new RevisionValue();
                                                        //if user want only only the transition then take only the state change data

                                                        thisRevision.setId(rev.getId());
                                                        thisRevision.setRev(rev.getRev());

                                                        RevisionFields rField = null;
                                                        Map<String,Object> mapRevision = (Map<String, Object>) rev.getFields();

                                                        if (mapRevision != null)
                                                        {

                                                            rField = new RevisionFields();
                                                            isStateChanged = false;

                                                            if (mapRevision.get("System.State") != null)
                                                            {
                                                                workItemStatecurrent = String.valueOf(mapRevision.get("System.State"));
                                                            }

                                                            if (!workItemStateprev.equals(workItemStatecurrent))
                                                            {
                                                                //There is a state change done.
                                                                isStateChanged = true;
                                                                workItemStateprev = String.valueOf(mapRevision.get("System.State"));
                                                            }

                                                            if (mapRevision.get("System.WorkItemType") != null)
                                                            {
                                                                rField.setWorkItemType(String.valueOf(mapRevision.get("System.WorkItemType")));
                                                            }
                                                            if (mapRevision.get("System.State") != null)
                                                            {
                                                                rField.setState(String.valueOf(mapRevision.get("System.State")));
                                                            }
                                                            if (mapRevision.get("System.Reason") != null)
                                                            {
                                                                rField.setReason(String.valueOf(mapRevision.get("System.Reason")));
                                                            }
                                                            if (mapRevision.get("Custom.RevisedDueDate") != null)
                                                            {
                                                                rField.setRevisedDueDate(String.valueOf(mapRevision.get("Custom.RevisedDueDate")));
                                                            }
                                                            if (mapRevision.get("Custom.RevisedDueDateInfluencedBy") != null)
                                                            {
                                                                rField.setRevisedDateInfluencedBy(String.valueOf(mapRevision.get("Custom.RevisedDueDateInfluencedBy")));
                                                            }
                                                            if (mapRevision.get("System.CreatedDate") != null)
                                                            {
                                                                rField.setCreatedDate(String.valueOf(mapRevision.get("System.CreatedDate")));
                                                            }
                                                            if (mapRevision.get("System.ChangedDate") != null)
                                                            {
                                                                rField.setChangedDate(String.valueOf(mapRevision.get("System.ChangedDate")));
                                                            }
                                                            if (mapRevision.get("Microsoft.VSTS.Common.StateChangeDate") != null)
                                                            {
                                                                rField.setStateChangeDate(String.valueOf(mapRevision.get("Microsoft.VSTS.Common.StateChangeDate")));
                                                            }

                                                            rField.setOriginalEffort(-1); //Setting this as -1 if data is not available
                                                            if (mapRevision.get("Microsoft.VSTS.Scheduling.OriginalEstimate") != null)
                                                            {
                                                                try
                                                                {
                                                                    rField.setOriginalEffort(Double.parseDouble(String.valueOf(mapRevision.get("Microsoft.VSTS.Scheduling.OriginalEstimate"))));
                                                                }
                                                                catch (Exception ex)
                                                                {
                                                                }
                                                            }

                                                            if (getOnlyTransitionHistory == true)
                                                            {
                                                                if (isStateChanged == true)
                                                                {
                                                                    thisRevision.setFields(rField);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                //Take the all the data
                                                                thisRevision.setFields(rField);
                                                            }
                                                        }

                                                        if (thisRevision.getFields() != null)
                                                        {
                                                            lstRevisions.add(thisRevision);
                                                        }
                                                    }
                                                }

                                                workItemToAdd.setRevisions(lstRevisions);
                                            }
                                        }
                                    }

                                    workItems.add(workItemToAdd);
                                }
                            }
                        }
                    }
                }
                else
                {
                    entity = new HttpEntity <> (headers);
                }
            }

            return workItems;
        }
        catch (Exception ex)
        {
            String b = "";
        }
        return workItems;
    }

    public List<WorkItem> getWorkitemsHistory (String userName, String password, String searchURL, String wiqlRequest, String httpMethod, boolean isCommentRequired, boolean isHistoryRequired, boolean getOnlyTransitionHistory, int pageSize)
    {
        List<WorkItem> workItems = new ArrayList<>();
        workItems = null;
        try
        {
            SearchWorkitemByQuery searchWorkitemByQuery = new SearchWorkitemByQuery();
            searchWorkitemByQuery = null;
            HttpHeaders headers = SetHeader(userName, password);
            if (headers != null)
            {
                HttpEntity<String> entity = null;
                if (!wiqlRequest.isEmpty())
                {
                    entity = new HttpEntity <> (wiqlRequest,headers);
                    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(searchURL);
                    String qURL = builder.build().encode().toUriString();
                    searchWorkitemByQuery = restTemplate.exchange(qURL, HttpMethod.POST, entity, SearchWorkitemByQuery.class).getBody();

                    if (searchWorkitemByQuery != null)
                    {
                        if (searchWorkitemByQuery.getWorkItems() != null && searchWorkitemByQuery.getWorkItems().size() > 0)
                        {
                            workItems = new ArrayList<>();
                            for (WorkItemsHeader workItemHeader: searchWorkitemByQuery.getWorkItems())
                            {
                                WorkItemObject workItem = null;
                                WorkItem workItemToAdd = null;
                                Comment comment = null;
                                Revision revision = null;

                                //Get the workitem
                                entity = new HttpEntity <> (headers);
                                builder = UriComponentsBuilder.fromUriString(workItemHeader.getUrl());
                                qURL = builder.build().encode().toUriString();
                                workItem = restTemplate.exchange(qURL, HttpMethod.GET, entity, WorkItemObject.class).getBody();

                                if (workItem != null)
                                {
                                    workItemToAdd = new WorkItem();
                                    workItemToAdd.setId(workItem.getId());
                                    workItemToAdd.setRev(workItem.getRev());
                                    Map<String,Object> mapFields = (Map<String, Object>) workItem.getFields();
                                    WorkItemFields fields = null;
                                    fields = util.setupWorkItemFields(mapFields);
                                    workItemToAdd.setFields(fields);

                                    //Get the Links
                                    String commentLink = "";
                                    String revisionLink = "";
                                    Map<String,Object> mapLinks = (Map<String, Object>) workItem.get_links();
                                    if (mapLinks != null)
                                    {
                                        for (Map.Entry<String, Object> mapurl : mapLinks.entrySet())
                                        {
                                            if (mapurl.getKey().equals("workItemComments"))
                                            {
                                                Map<String, Object> commentMap = (Map<String, Object>) mapurl.getValue();
                                                if (commentMap != null)
                                                {
                                                    commentLink = commentMap.get("href").toString();
                                                }
                                            }

                                            if (mapurl.getKey().equals("workItemRevisions"))
                                            {
                                                Map<String, Object> commentRev = (Map<String, Object>) mapurl.getValue();
                                                if (commentRev != null)
                                                {
                                                    revisionLink = commentRev.get("href").toString();
                                                }
                                            }
                                        }
                                    }

                                    if (isCommentRequired == true && !commentLink.isEmpty())
                                    {
                                        //commentLink = "https://dev.azure.com/premierinc/32912845-d34e-48fb-83c6-8f28f005b1c3/_apis/wit/workItems/27498/comments";

                                        entity = new HttpEntity <> (headers);
                                        builder = UriComponentsBuilder.fromUriString(commentLink);
                                        qURL = builder.build().encode().toUriString();
                                        comment = restTemplate.exchange(qURL, HttpMethod.GET, entity, Comment.class).getBody();
                                        workItemToAdd.setComment(comment);
                                    }

                                    if (isHistoryRequired == true && !revisionLink.isEmpty())
                                    {
                                        entity = new HttpEntity <> (headers);
                                        builder = UriComponentsBuilder.fromUriString(revisionLink);
                                        qURL = builder.build().encode().toUriString();
                                        revision = restTemplate.exchange(qURL, HttpMethod.GET, entity, Revision.class).getBody();

                                        boolean isStateChanged = false;
                                        String workItemStateprev = "";
                                        String workItemStatecurrent = "";

                                        if (revision != null)
                                        {
                                            if (revision.getValue() != null && revision.getValue().size()>0)
                                            {
                                                List<RevisionValue> lstRevisions = new ArrayList<>();

                                                for (RevisionValueObject rev: revision.getValue())
                                                {
                                                    if (rev != null)
                                                    {
                                                        RevisionValue thisRevision = new RevisionValue();
                                                        //if user want only only the transition then take only the state change data

                                                        thisRevision.setId(rev.getId());
                                                        thisRevision.setRev(rev.getRev());

                                                        RevisionFields rField = null;
                                                        Map<String,Object> mapRevision = (Map<String, Object>) rev.getFields();

                                                        if (mapRevision != null)
                                                        {

                                                            rField = new RevisionFields();
                                                            isStateChanged = false;

                                                            if (mapRevision.get("System.State") != null)
                                                            {
                                                                workItemStatecurrent = String.valueOf(mapRevision.get("System.State"));
                                                            }

                                                            if (!workItemStateprev.equals(workItemStatecurrent))
                                                            {
                                                                //There is a state change done.
                                                                isStateChanged = true;
                                                                workItemStateprev = String.valueOf(mapRevision.get("System.State"));
                                                            }

                                                            if (mapRevision.get("System.WorkItemType") != null)
                                                            {
                                                                rField.setWorkItemType(String.valueOf(mapRevision.get("System.WorkItemType")));
                                                            }
                                                            if (mapRevision.get("System.State") != null)
                                                            {
                                                                rField.setState(String.valueOf(mapRevision.get("System.State")));
                                                            }
                                                            if (mapRevision.get("System.Reason") != null)
                                                            {
                                                                rField.setReason(String.valueOf(mapRevision.get("System.Reason")));
                                                            }
                                                            if (mapRevision.get("Custom.RevisedDueDate") != null)
                                                            {
                                                                rField.setRevisedDueDate(String.valueOf(mapRevision.get("Custom.RevisedDueDate")));
                                                            }
                                                            if (mapRevision.get("Custom.RevisedDueDateInfluencedBy") != null)
                                                            {
                                                                rField.setRevisedDateInfluencedBy(String.valueOf(mapRevision.get("Custom.RevisedDueDateInfluencedBy")));
                                                            }
                                                            if (mapRevision.get("System.CreatedDate") != null)
                                                            {
                                                                rField.setCreatedDate(String.valueOf(mapRevision.get("System.CreatedDate")));
                                                            }
                                                            if (mapRevision.get("System.ChangedDate") != null)
                                                            {
                                                                rField.setChangedDate(String.valueOf(mapRevision.get("System.ChangedDate")));
                                                            }
                                                            if (mapRevision.get("Microsoft.VSTS.Common.StateChangeDate") != null)
                                                            {
                                                                rField.setStateChangeDate(String.valueOf(mapRevision.get("Microsoft.VSTS.Common.StateChangeDate")));
                                                            }

                                                            rField.setOriginalEffort(-1); //Setting this as -1 if data is not available
                                                            if (mapRevision.get("Microsoft.VSTS.Scheduling.OriginalEstimate") != null)
                                                            {
                                                                try
                                                                {
                                                                    rField.setOriginalEffort(Double.parseDouble(String.valueOf(mapRevision.get("Microsoft.VSTS.Scheduling.OriginalEstimate"))));
                                                                }
                                                                catch (Exception ex)
                                                                {
                                                                }
                                                            }

                                                            if (getOnlyTransitionHistory == true)
                                                            {
                                                                if (isStateChanged == true)
                                                                {
                                                                    thisRevision.setFields(rField);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                //Take the all the data
                                                                thisRevision.setFields(rField);
                                                            }
                                                        }


                                                        lstRevisions.add(thisRevision);

                                                    }
                                                }

                                                workItemToAdd.setRevisions(lstRevisions);
                                            }
                                        }
                                    }

                                    workItems.add(workItemToAdd);
                                }
                            }
                        }
                    }
                }
                else
                {
                    entity = new HttpEntity <> (headers);
                }
            }

            return workItems;
        }
        catch (Exception ex)
        {
            String b = "";
        }
        return workItems;
    }

    public List<WorkItem> getWorkitems (String userName, String password, String searchURL, String wiqlRequest, String httpMethod, boolean isCommentRequired, boolean isHistoryRequired, boolean getOnlyTransitionHistory, boolean isLinkedChildItemsRequired, String whichWorkItemTypes, String searchLinkURI, String searchWorkItemURIToSearchLinkItem, int pageSize)
    {
        List<WorkItem> workItems = new ArrayList<>();

        //searchLinkURI = "https://analytics.dev.azure.com/premierinc/SDC16ServiceLevelAutomation/_odata/v2.0/WorkItems?$select=WorkItemId&$expand=Children($filter=WorkItemType in ('Defect'); $select=WorkItemId)&$filter=WorkItemId eq WORKITEMIDVALUE";
        searchLinkURI = searchLinkURI.replace("DEFECTTYPEVALUE", whichWorkItemTypes);

        //searchWorkItemURIToSearchLinkItem = "https://dev.azure.com/premierinc/_apis/wit/workItems/WORKITEMIDVALUE";
        //password = "et4qwx2wn63t5355k2g3ovgaflzshfd6sdx4ni6hkukzuqto3qra";
        workItems = null;

        try
        {
            SearchWorkitemByQuery searchWorkitemByQuery = new SearchWorkitemByQuery();
            searchWorkitemByQuery = null;
            HttpHeaders headers = SetHeader(userName, password);
            if (headers != null)
            {
                HttpEntity<String> entity = null;
                if (!wiqlRequest.isEmpty())
                {
                    entity = new HttpEntity <> (wiqlRequest,headers);
                    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(searchURL);
                    String qURL = builder.build().encode().toUriString();
                    searchWorkitemByQuery = restTemplate.exchange(qURL, HttpMethod.POST, entity, SearchWorkitemByQuery.class).getBody();

                    if (searchWorkitemByQuery != null)
                    {
                        if (searchWorkitemByQuery.getWorkItems() != null && searchWorkitemByQuery.getWorkItems().size() > 0)
                        {
                            workItems = new ArrayList<>();
                            for (WorkItemsHeader workItemHeader: searchWorkitemByQuery.getWorkItems())
                            {
                                WorkItemObject workItem = null;
                                WorkItem workItemToAdd = null;
                                Comment comment = null;
                                Revision revision = null;
                                List<WorkItem> linkWorkItems = new ArrayList<>();
                                List<WorkItem> linkWorkItemss= new ArrayList<>();

                                //Get the workitem
                                entity = new HttpEntity <> (headers);
                                builder = UriComponentsBuilder.fromUriString(workItemHeader.getUrl());
                                qURL = builder.build().encode().toUriString();
                                workItem = restTemplate.exchange(qURL, HttpMethod.GET, entity, WorkItemObject.class).getBody();

                                if (workItem != null)
                                {
                                    linkWorkItems = null;
                                    workItemToAdd = new WorkItem();
                                    workItemToAdd.setId(workItem.getId());
                                    workItemToAdd.setRev(workItem.getRev());
                                    Map<String,Object> mapFields = (Map<String, Object>) workItem.getFields();
                                    WorkItemFields fields = null;
                                    fields = util.setupWorkItemFields(mapFields);
                                    workItemToAdd.setFields(fields);

                                    //Get the Links
                                    String commentLink = "";
                                    String revisionLink = "";
                                    Map<String,Object> mapLinks = (Map<String, Object>) workItem.get_links();
                                    if (mapLinks != null)
                                    {
                                        for (Map.Entry<String, Object> mapurl : mapLinks.entrySet())
                                        {
                                            if (mapurl.getKey().equals("workItemComments"))
                                            {
                                                Map<String, Object> commentMap = (Map<String, Object>) mapurl.getValue();
                                                if (commentMap != null)
                                                {
                                                    commentLink = commentMap.get("href").toString();
                                                }
                                            }

                                            if (mapurl.getKey().equals("workItemRevisions"))
                                            {
                                                Map<String, Object> commentRev = (Map<String, Object>) mapurl.getValue();
                                                if (commentRev != null)
                                                {
                                                    revisionLink = commentRev.get("href").toString();
                                                }
                                            }
                                        }
                                    }

                                    if (isCommentRequired == true && !commentLink.isEmpty())
                                    {
                                        //commentLink = "https://dev.azure.com/premierinc/32912845-d34e-48fb-83c6-8f28f005b1c3/_apis/wit/workItems/27498/comments";

                                        entity = new HttpEntity <> (headers);
                                        builder = UriComponentsBuilder.fromUriString(commentLink);
                                        qURL = builder.build().encode().toUriString();
                                        comment = restTemplate.exchange(qURL, HttpMethod.GET, entity, Comment.class).getBody();
                                        workItemToAdd.setComment(comment);
                                    }

                                    if (isHistoryRequired == true && !revisionLink.isEmpty())
                                    {
                                        entity = new HttpEntity <> (headers);
                                        builder = UriComponentsBuilder.fromUriString(revisionLink);
                                        qURL = builder.build().encode().toUriString();
                                        revision = restTemplate.exchange(qURL, HttpMethod.GET, entity, Revision.class).getBody();

                                        boolean isStateChanged = false;
                                        String workItemStateprev = "";
                                        String workItemStatecurrent = "";

                                        if (revision != null)
                                        {
                                            if (revision.getValue() != null && revision.getValue().size()>0)
                                            {
                                                List<RevisionValue> lstRevisions = new ArrayList<>();

                                                for (RevisionValueObject rev: revision.getValue())
                                                {
                                                    if (rev != null)
                                                    {
                                                        RevisionValue thisRevision = new RevisionValue();
                                                        //if user want only only the transition then take only the state change data

                                                        thisRevision.setId(rev.getId());
                                                        thisRevision.setRev(rev.getRev());

                                                        AdoUser adoUser = null;
                                                        RevisionFields rField = null;
                                                        Map<String,Object> mapRevision = (Map<String, Object>) rev.getFields();

                                                        if (mapRevision != null)
                                                        {

                                                            rField = new RevisionFields();
                                                            isStateChanged = false;

                                                            if (mapRevision.get("System.State") != null)
                                                            {
                                                                workItemStatecurrent = String.valueOf(mapRevision.get("System.State"));
                                                            }

                                                            if (!workItemStateprev.equals(workItemStatecurrent))
                                                            {
                                                                //There is a state change done.
                                                                isStateChanged = true;
                                                                workItemStateprev = String.valueOf(mapRevision.get("System.State"));
                                                            }

                                                            if (mapRevision.get("System.WorkItemType") != null)
                                                            {
                                                                rField.setWorkItemType(String.valueOf(mapRevision.get("System.WorkItemType")));
                                                            }
                                                            if (mapRevision.get("System.State") != null)
                                                            {
                                                                rField.setState(String.valueOf(mapRevision.get("System.State")));
                                                            }
                                                            if (mapRevision.get("System.Reason") != null)
                                                            {
                                                                rField.setReason(String.valueOf(mapRevision.get("System.Reason")));
                                                            }
                                                            if (mapRevision.get("System.CreatedDate") != null)
                                                            {
                                                                rField.setCreatedDate(String.valueOf(mapRevision.get("System.CreatedDate")));
                                                            }
                                                            if (mapRevision.get("System.ChangedDate") != null)
                                                            {
                                                                rField.setChangedDate(String.valueOf(mapRevision.get("System.ChangedDate")));
                                                            }
                                                            if (mapRevision.get("System.Tags") != null)
                                                            {
                                                                rField.setChangedDate(String.valueOf(mapRevision.get("System.Tags")));
                                                            }
                                                            if (mapRevision.get("Microsoft.VSTS.Common.StateChangeDate") != null)
                                                            {
                                                                rField.setStateChangeDate(String.valueOf(mapRevision.get("Microsoft.VSTS.Common.StateChangeDate")));
                                                            }

                                                            rField.setOriginalEffort(-1); //Setting this as -1 if data is not available
                                                            if (mapRevision.get("Microsoft.VSTS.Scheduling.OriginalEstimate") != null)
                                                            {
                                                                try
                                                                {
                                                                    rField.setOriginalEffort(Double.parseDouble(String.valueOf(mapRevision.get("Microsoft.VSTS.Scheduling.OriginalEstimate"))));
                                                                }
                                                                catch (Exception ex)
                                                                {
                                                                }
                                                            }

                                                            //Code Change By Ajinkya
                                                            adoUser = new AdoUser();
                                                            Map<String, Object> aUser = (Map<String, Object>) mapRevision.get("System.CreatedBy");
                                                            if (mapRevision.get("System.CreatedBy") != null)
                                                            {
                                                                if (aUser.get("displayName") != null) {
                                                                    adoUser.setDisplayName(String.valueOf(aUser.get("displayName")));
                                                                }
                                                                if (aUser.get("uniqueName") != null) {
                                                                    adoUser.setUniqueName(String.valueOf(aUser.get("uniqueName")));
                                                                }
                                                                rField.setCreatedBy(adoUser);
                                                            }




                                                            if (getOnlyTransitionHistory == true)
                                                            {
                                                                if (isStateChanged == true)
                                                                {
                                                                    thisRevision.setFields(rField);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                //Take the all the data
                                                                thisRevision.setFields(rField);
                                                            }
                                                        }

                                                        if (thisRevision.getFields() != null)
                                                        {
                                                            lstRevisions.add(thisRevision);
                                                        }
                                                    }
                                                }

                                                workItemToAdd.setRevisions(lstRevisions);
                                            }
                                        }
                                    }

                                    try
                                    {
                                        if (isLinkedChildItemsRequired == true)
                                        {
                                            if (!searchLinkURI.isEmpty())
                                            {
                                                String linkURI = searchLinkURI.replace("WORKITEMIDVALUE", String.valueOf(workItem.getId()));
                                                entity = new HttpEntity <> (headers);
                                                builder = UriComponentsBuilder.fromUriString(linkURI);
                                                LinkedHashMap searchLinkItem = restTemplate.exchange(linkURI, HttpMethod.GET, entity, LinkedHashMap.class).getBody();
                                                LinkedHashMap searchLinkItems = restTemplate.exchange(linkURI, HttpMethod.GET, entity, LinkedHashMap.class).getBody();
                                                LinkedWorkItems searchLinkItem1 = restTemplate.exchange(linkURI, HttpMethod.GET, entity, LinkedWorkItems.class).getBody();

                                                if (searchLinkItem != null)
                                                {
                                                    Object linkedParentChild = null;
                                                    ArrayList arrParentChild = null;
                                                    if (searchLinkItem.get("value") != null)
                                                    {
                                                        arrParentChild = (ArrayList) searchLinkItem.get("value");
                                                    }

                                                    if (arrParentChild != null)
                                                    {
                                                        //linkedParentChild = arrParentChild.get(0);
                                                        LinkedHashMap lhParentChild = (LinkedHashMap) arrParentChild.get(0);
                                                        //LinkedHashMap lhParentChild = (LinkedHashMap)linkedParentChild;
                                                        if (lhParentChild.get("Links") != null)
                                                        {
                                                            Object child = lhParentChild.get("Links");
                                                            if (child != null)
                                                            {
                                                                ArrayList arrchild = (ArrayList) child;
                                                                if (arrchild != null && arrchild.size() > 0)
                                                                {
                                                                    linkWorkItems = new ArrayList<>();
                                                                    for (Object objch : arrchild)
                                                                    {
                                                                        if (objch != null)
                                                                        {
                                                                            LinkedHashMap lhChild = (LinkedHashMap) objch;
                                                                            String strWorkItemId = "";
                                                                            if (lhChild != null)
                                                                            {
                                                                                if (lhChild.get("TargetWorkItemId") != null)
                                                                                {
                                                                                    strWorkItemId = String.valueOf(lhChild.get("TargetWorkItemId"));
                                                                                    if (!strWorkItemId.isEmpty())
                                                                                    {
                                                                                        String searchWorkItemURI = searchWorkItemURIToSearchLinkItem.replace("WORKITEMIDVALUE", strWorkItemId);
                                                                                        if (!searchWorkItemURI.isEmpty())
                                                                                        {
                                                                                            builder = UriComponentsBuilder.fromUriString(searchWorkItemURI);
                                                                                            qURL = builder.build().encode().toUriString();
                                                                                            WorkItemObject workItemLinked = restTemplate.exchange(qURL, HttpMethod.GET, entity, WorkItemObject.class).getBody();
                                                                                            if (workItemLinked != null)
                                                                                            {
                                                                                                WorkItem lnkWorkItemToAdd = new WorkItem();
                                                                                                lnkWorkItemToAdd = new WorkItem();
                                                                                                lnkWorkItemToAdd.setId(workItemLinked.getId());
                                                                                                lnkWorkItemToAdd.setRev(workItemLinked.getRev());

                                                                                                Map<String,Object> mapFieldslnk = (Map<String, Object>) workItemLinked.getFields();
                                                                                                WorkItemFields fieldslnk = null;
                                                                                                fieldslnk = util.setupWorkItemFields(mapFieldslnk);

                                                                                                //if Revision is required then get the revision and pull the revisions
                                                                                                String commentLinkChild = "";
                                                                                                String revisionLinkChild = "";
                                                                                                Map<String,Object> mapLinksChild = (Map<String, Object>) workItemLinked.get_links();

                                                                                                if (mapLinksChild != null)
                                                                                                {
                                                                                                    for (Map.Entry<String, Object> mapurl : mapLinksChild.entrySet())
                                                                                                    {
                                                                                                        if (mapurl.getKey().equals("workItemComments"))
                                                                                                        {
                                                                                                            Map<String, Object> commentMapchild = (Map<String, Object>) mapurl.getValue();
                                                                                                            if (commentMapchild != null)
                                                                                                            {
                                                                                                                commentLinkChild = commentMapchild.get("href").toString();
                                                                                                            }
                                                                                                        }

                                                                                                        if (mapurl.getKey().equals("workItemRevisions"))
                                                                                                        {
                                                                                                            Map<String, Object> revisionMapChild = (Map<String, Object>) mapurl.getValue();
                                                                                                            if (revisionMapChild != null)
                                                                                                            {
                                                                                                                revisionLinkChild = revisionMapChild.get("href").toString();
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }

                                                                                                if (isHistoryRequired == true && !revisionLinkChild.isEmpty())
                                                                                                {
                                                                                                    entity = new HttpEntity <> (headers);
                                                                                                    builder = UriComponentsBuilder.fromUriString(revisionLinkChild);
                                                                                                    qURL = builder.build().encode().toUriString();

                                                                                                    Revision revisionChild = restTemplate.exchange(qURL, HttpMethod.GET, entity, Revision.class).getBody();

                                                                                                    boolean isStateChanged = false;
                                                                                                    String workItemStateprev = "";
                                                                                                    String workItemStatecurrent = "";

                                                                                                    if (revisionChild != null)
                                                                                                    {
                                                                                                        if (revisionChild.getValue() != null && revisionChild.getValue().size()>0)
                                                                                                        {
                                                                                                            List<RevisionValue> lstRevisionsChild = new ArrayList<>();

                                                                                                            for (RevisionValueObject rev: revisionChild.getValue())
                                                                                                            {
                                                                                                                if (rev != null)
                                                                                                                {
                                                                                                                    RevisionValue thisRevisionChild = new RevisionValue();
                                                                                                                    //if user want only only the transition then take only the state change data

                                                                                                                    thisRevisionChild.setId(rev.getId());
                                                                                                                    thisRevisionChild.setRev(rev.getRev());

                                                                                                                    RevisionFields rFieldChild = null;
                                                                                                                    Map<String,Object> mapRevisionChild = (Map<String, Object>) rev.getFields();

                                                                                                                    if (mapRevisionChild != null)
                                                                                                                    {

                                                                                                                        rFieldChild = new RevisionFields();
                                                                                                                        isStateChanged = false;

                                                                                                                        if (mapRevisionChild.get("System.State") != null)
                                                                                                                        {
                                                                                                                            workItemStatecurrent = String.valueOf(mapRevisionChild.get("System.State"));
                                                                                                                        }

                                                                                                                        if (!workItemStateprev.equals(workItemStatecurrent))
                                                                                                                        {
                                                                                                                            //There is a state change done.
                                                                                                                            isStateChanged = true;
                                                                                                                            workItemStateprev = String.valueOf(mapRevisionChild.get("System.State"));
                                                                                                                        }

                                                                                                                        if (mapRevisionChild.get("System.WorkItemType") != null)
                                                                                                                        {
                                                                                                                            rFieldChild.setWorkItemType(String.valueOf(mapRevisionChild.get("System.WorkItemType")));
                                                                                                                        }
                                                                                                                        if (mapRevisionChild.get("System.State") != null)
                                                                                                                        {
                                                                                                                            rFieldChild.setState(String.valueOf(mapRevisionChild.get("System.State")));
                                                                                                                        }
                                                                                                                        if (mapRevisionChild.get("System.Reason") != null)
                                                                                                                        {
                                                                                                                            rFieldChild.setReason(String.valueOf(mapRevisionChild.get("System.Reason")));
                                                                                                                        }
                                                                                                                        if (mapRevisionChild.get("System.CreatedDate") != null)
                                                                                                                        {
                                                                                                                            rFieldChild.setCreatedDate(String.valueOf(mapRevisionChild.get("System.CreatedDate")));
                                                                                                                        }
                                                                                                                        if (mapRevisionChild.get("System.ChangedDate") != null)
                                                                                                                        {
                                                                                                                            rFieldChild.setChangedDate(String.valueOf(mapRevisionChild.get("System.ChangedDate")));
                                                                                                                        }

                                                                                                                        if (mapRevisionChild.get("Microsoft.VSTS.Common.StateChangeDate") != null)
                                                                                                                        {
                                                                                                                            rFieldChild.setStateChangeDate(String.valueOf(mapRevisionChild.get("Microsoft.VSTS.Common.StateChangeDate")));
                                                                                                                        }

                                                                                                                        rFieldChild.setOriginalEffort(-1); //Setting this as -1 if data is not available
                                                                                                                        if (mapRevisionChild.get("Microsoft.VSTS.Scheduling.OriginalEstimate") != null)
                                                                                                                        {
                                                                                                                            try
                                                                                                                            {
                                                                                                                                rFieldChild.setOriginalEffort(Double.parseDouble(String.valueOf(mapRevisionChild.get("Microsoft.VSTS.Scheduling.OriginalEstimate"))));
                                                                                                                            }
                                                                                                                            catch (Exception ex)
                                                                                                                            {
                                                                                                                            }
                                                                                                                        }

                                                                                                                        if (getOnlyTransitionHistory == true)
                                                                                                                        {
                                                                                                                            if (isStateChanged == true)
                                                                                                                            {
                                                                                                                                thisRevisionChild.setFields(rFieldChild);
                                                                                                                            }
                                                                                                                        }
                                                                                                                        else
                                                                                                                        {
                                                                                                                            //Take the all the data
                                                                                                                            thisRevisionChild.setFields(rFieldChild);
                                                                                                                        }
                                                                                                                    }

                                                                                                                    if (thisRevisionChild.getFields() != null)
                                                                                                                    {
                                                                                                                        lstRevisionsChild.add(thisRevisionChild);
                                                                                                                    }
                                                                                                                }
                                                                                                            }

                                                                                                            lnkWorkItemToAdd.setRevisions(lstRevisionsChild);
                                                                                                        }
                                                                                                    }
                                                                                                }


                                                                                                lnkWorkItemToAdd.setFields(fieldslnk);
                                                                                                if (searchLinkItems != null)
                                                                                                {
                                                                                                    Object linkedParentChilds = null;
                                                                                                    ArrayList arrParentChilds = null;
                                                                                                    if (searchLinkItems.get("value") != null)
                                                                                                    {
                                                                                                        arrParentChilds = (ArrayList) searchLinkItems.get("value");
                                                                                                    }

                                                                                                    if (arrParentChild != null)
                                                                                                    {
                                                                                                        //linkedParentChild = arrParentChild.get(0);
                                                                                                        LinkedHashMap lhParentChilds = (LinkedHashMap) arrParentChilds.get(0);
                                                                                                        //LinkedHashMap lhParentChild = (LinkedHashMap)linkedParentChild;
                                                                                                        if (lhParentChilds.get("Links") != null)
                                                                                                        {
                                                                                                            Object childss = lhParentChild.get("Links");
                                                                                                            if (childss != null)
                                                                                                            {
                                                                                                                ArrayList arrchilds = (ArrayList) child;
                                                                                                                if (arrchilds != null && arrchilds.size() > 0)
                                                                                                                {
                                                                                                                    linkWorkItemss = new ArrayList<>();
                                                                                                                    for (Object objchss : arrchild)
                                                                                                                    {
                                                                                                                        if (objchss != null)
                                                                                                                        {
                                                                                                                            LinkedHashMap lhChilds = (LinkedHashMap) objchss;
                                                                                                                            String strWorkItemIds = "";
                                                                                                                            if (lhChilds != null)
                                                                                                                            {
                                                                                                                                if (lhChilds.get("TargetWorkItemId") != null)
                                                                                                                                {
                                                                                                                                    strWorkItemIds = String.valueOf(lhChild.get("TargetWorkItemId"));
                                                                                                                                    if (!strWorkItemIds.isEmpty())
                                                                                                                                    {
                                                                                                                                        String searchWorkItemURIs = searchWorkItemURIToSearchLinkItem.replace("WORKITEMIDVALUE", strWorkItemId);
                                                                                                                                        if (!searchWorkItemURI.isEmpty())
                                                                                                                                        {
                                                                                                                                            builder = UriComponentsBuilder.fromUriString(searchWorkItemURI);
                                                                                                                                            qURL = builder.build().encode().toUriString();
                                                                                                                                            WorkItemObject workItemLinkeds = restTemplate.exchange(qURL, HttpMethod.GET, entity, WorkItemObject.class).getBody();
                                                                                                                                            if (workItemLinkeds != null)
                                                                                                                                            {
                                                                                                                                                WorkItem lnkWorkItemToAdds = new WorkItem();
                                                                                                                                                lnkWorkItemToAdds = new WorkItem();
                                                                                                                                                lnkWorkItemToAdds.setId(workItemLinked.getId());
                                                                                                                                                lnkWorkItemToAdds.setRev(workItemLinked.getRev());

                                                                                                                                                Map<String,Object> mapFieldslnks = (Map<String, Object>) workItemLinkeds.getFields();
                                                                                                                                                WorkItemFields fieldslnks = null;
                                                                                                                                                fieldslnks = util.setupWorkItemFields(mapFieldslnks);

                                                                                                                                                //if Revision is required then get the revision and pull the revisions
                                                                                                                                                String commentLinkChilds = "";
                                                                                                                                                String revisionLinkChilds = "";
                                                                                                                                                Map<String,Object> mapLinksChilds = (Map<String, Object>) workItemLinkeds.get_links();

                                                                                                                                                if (mapLinksChilds != null)
                                                                                                                                                {
                                                                                                                                                    for (Map.Entry<String, Object> mapurls : mapLinksChilds.entrySet())
                                                                                                                                                    {
                                                                                                                                                        if (mapurls.getKey().equals("workItemComments"))
                                                                                                                                                        {
                                                                                                                                                            Map<String, Object> commentMapchilds = (Map<String, Object>) mapurls.getValue();
                                                                                                                                                            if (commentMapchilds != null)
                                                                                                                                                            {
                                                                                                                                                                commentLinkChilds = commentMapchilds.get("href").toString();
                                                                                                                                                            }
                                                                                                                                                        }

                                                                                                                                                        if (mapurls.getKey().equals("workItemRevisions"))
                                                                                                                                                        {
                                                                                                                                                            Map<String, Object> revisionMapChilds = (Map<String, Object>) mapurls.getValue();
                                                                                                                                                            if (revisionMapChilds != null)
                                                                                                                                                            {
                                                                                                                                                                revisionLinkChilds = revisionMapChilds.get("href").toString();
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }

                                                                                                                                                if (isHistoryRequired == true && !revisionLinkChilds.isEmpty())
                                                                                                                                                {
                                                                                                                                                    entity = new HttpEntity <> (headers);
                                                                                                                                                    builder = UriComponentsBuilder.fromUriString(revisionLinkChilds);
                                                                                                                                                    qURL = builder.build().encode().toUriString();

                                                                                                                                                    Revision revisionChilds = restTemplate.exchange(qURL, HttpMethod.GET, entity, Revision.class).getBody();

                                                                                                                                                    boolean isStateChanged = false;
                                                                                                                                                    String workItemStateprevs = "";
                                                                                                                                                    String workItemStatecurrents = "";

                                                                                                                                                    if (revisionChilds != null)
                                                                                                                                                    {
                                                                                                                                                        if (revisionChilds.getValue() != null && revisionChilds.getValue().size()>0)
                                                                                                                                                        {
                                                                                                                                                            List<RevisionValue> lstRevisionsChilds = new ArrayList<>();

                                                                                                                                                            for (RevisionValueObject revs: revisionChilds.getValue())
                                                                                                                                                            {
                                                                                                                                                                if (revs != null)
                                                                                                                                                                {
                                                                                                                                                                    RevisionValue thisRevisionChilds = new RevisionValue();
                                                                                                                                                                    //if user want only only the transition then take only the state change data

                                                                                                                                                                    thisRevisionChilds.setId(revs.getId());
                                                                                                                                                                    thisRevisionChilds.setRev(revs.getRev());

                                                                                                                                                                    RevisionFields rFieldChilds = null;
                                                                                                                                                                    Map<String,Object> mapRevisionChilds = (Map<String, Object>) revs.getFields();

                                                                                                                                                                    if (mapRevisionChilds != null)
                                                                                                                                                                    {

                                                                                                                                                                        rFieldChilds= new RevisionFields();
                                                                                                                                                                        isStateChanged = false;

                                                                                                                                                                        if (mapRevisionChilds.get("System.State") != null)
                                                                                                                                                                        {
                                                                                                                                                                            workItemStatecurrents = String.valueOf(mapRevisionChilds.get("System.State"));
                                                                                                                                                                        }

                                                                                                                                                                        if (!workItemStateprevs.equals(workItemStatecurrents))
                                                                                                                                                                        {
                                                                                                                                                                            //There is a state change done.
                                                                                                                                                                            isStateChanged = true;
                                                                                                                                                                            workItemStateprevs = String.valueOf(mapRevisionChilds.get("System.State"));
                                                                                                                                                                        }

                                                                                                                                                                        if (mapRevisionChilds.get("System.WorkItemType") != null)
                                                                                                                                                                        {
                                                                                                                                                                            rFieldChilds.setWorkItemType(String.valueOf(mapRevisionChilds.get("System.WorkItemType")));
                                                                                                                                                                        }
                                                                                                                                                                        if (mapRevisionChilds.get("System.State") != null)
                                                                                                                                                                        {
                                                                                                                                                                            rFieldChilds.setState(String.valueOf(mapRevisionChilds.get("System.State")));
                                                                                                                                                                        }
                                                                                                                                                                        if (mapRevisionChilds.get("System.Reason") != null)
                                                                                                                                                                        {
                                                                                                                                                                            rFieldChilds.setReason(String.valueOf(mapRevisionChilds.get("System.Reason")));
                                                                                                                                                                        }
                                                                                                                                                                        if (mapRevisionChilds.get("System.CreatedDate") != null)
                                                                                                                                                                        {
                                                                                                                                                                            rFieldChilds.setCreatedDate(String.valueOf(mapRevisionChilds.get("System.CreatedDate")));
                                                                                                                                                                        }
                                                                                                                                                                        if (mapRevisionChilds.get("System.ChangedDate") != null)
                                                                                                                                                                        {
                                                                                                                                                                            rFieldChilds.setChangedDate(String.valueOf(mapRevisionChilds.get("System.ChangedDate")));
                                                                                                                                                                        }

                                                                                                                                                                        if (mapRevisionChilds.get("Microsoft.VSTS.Common.StateChangeDate") != null)
                                                                                                                                                                        {
                                                                                                                                                                            rFieldChilds.setStateChangeDate(String.valueOf(mapRevisionChilds.get("Microsoft.VSTS.Common.StateChangeDate")));
                                                                                                                                                                        }

                                                                                                                                                                        rFieldChilds.setOriginalEffort(-1); //Setting this as -1 if data is not available
                                                                                                                                                                        if (mapRevisionChilds.get("Microsoft.VSTS.Scheduling.OriginalEstimate") != null)
                                                                                                                                                                        {
                                                                                                                                                                            try
                                                                                                                                                                            {
                                                                                                                                                                                rFieldChilds.setOriginalEffort(Double.parseDouble(String.valueOf(mapRevisionChilds.get("Microsoft.VSTS.Scheduling.OriginalEstimate"))));
                                                                                                                                                                            }
                                                                                                                                                                            catch (Exception ex)
                                                                                                                                                                            {
                                                                                                                                                                            }
                                                                                                                                                                        }

                                                                                                                                                                        if (getOnlyTransitionHistory == true)
                                                                                                                                                                        {
                                                                                                                                                                            if (isStateChanged == true)
                                                                                                                                                                            {
                                                                                                                                                                                thisRevisionChilds.setFields(rFieldChilds);
                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                        else
                                                                                                                                                                        {
                                                                                                                                                                            //Take the all the data
                                                                                                                                                                            thisRevisionChilds.setFields(rFieldChilds);
                                                                                                                                                                        }
                                                                                                                                                                    }

                                                                                                                                                                    if (thisRevisionChilds.getFields() != null)
                                                                                                                                                                    {
                                                                                                                                                                        lstRevisionsChilds.add(thisRevisionChilds);
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            }

                                                                                                                                                            lnkWorkItemToAdds.setRevisions(lstRevisionsChilds);
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }


                                                                                                                                                lnkWorkItemToAdds.setFields(fieldslnks);

                                                                                                                                                linkWorkItemss.add(lnkWorkItemToAdds);
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }

                                                                                                                    workItemToAdd.setChildLinks(linkWorkItems);
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                    lnkWorkItemToAdd.setChildLinks(linkWorkItemss);
                                                                                                }


                                                                                                linkWorkItems.add(lnkWorkItemToAdd);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                    workItemToAdd.setChildLinks(linkWorkItems);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    catch (Exception exLinked)
                                    {
                                        String a = "";
                                    }

                                    workItems.add(workItemToAdd);
                                }
                            }
                        }
                    }
                }
                else
                {
                    entity = new HttpEntity <> (headers);
                }
            }

            return workItems;
        }
        catch (Exception ex)
        {
            String b = "";
        }
        return workItems;
    }

    public TestPlan getTestPlan (String username, String password, String denoQuery)
    {
        HttpHeaders headers=new HttpHeaders();
        headers.setBasicAuth(username,password);
        HttpEntity<HttpHeaders> httpEntity =new HttpEntity(headers);
        try {
            ResponseEntity<TestPlan> response = restTemplate.exchange(denoQuery, HttpMethod.GET, httpEntity, TestPlan.class);
            return response.getBody();
        }catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
    }


}
