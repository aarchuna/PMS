package premier.premierslaautomate.Interfaces;

import org.springframework.http.ResponseEntity;
import premier.premierslaautomate.Models.ADO.*;

import java.util.List;
public interface IAdoDataService
{
    public List<WorkItem> getWorkitems (String userName, String password, String searchURL, String wiqlRequest, String httpMethod, boolean isCommentRequired, boolean isHistoryRequired, int pageSize);
    public List<WorkItem> getWorkitems (String userName, String password, String searchURL, String wiqlRequest, String httpMethod, boolean isCommentRequired, boolean isHistoryRequired, boolean getOnlyTransitionHistory, int pageSize);
    public List<WorkItem> getWorkitems (String userName, String password, String searchURL, String wiqlRequest, String httpMethod, boolean isCommentRequired, boolean isHistoryRequired, boolean getOnlyTransitionHistory, boolean isLinkedChildItemsRequired, String whichWorkItemTypes, String searchLinkURI, String searchWorkItemURIToSearchLinkItem, int pageSize);
    public List<WorkItem> getWorkitemsHistory (String userName, String password, String searchURL, String wiqlRequest, String httpMethod, boolean isCommentRequired, boolean isHistoryRequired, boolean getOnlyTransitionHistory, int pageSize);
    public TestPlan getTestPlan (String username, String password, String denoQuery);
}
