package premier.premierslaautomate.Interfaces;

import premier.premierslaautomate.Models.*;

import java.util.List;
public interface IJiraDataService
{
    public List<Sprint> getAllSprint(String userName, String password);
    public SearchBoard getAllBoards(String userName, String password);
    public List<BoardInfo> getAllBoardsNew(String userName, String password);
    public List<BoardInfo> getAllBoards1(String userName, String password);
    public List<Issue> getAllIssuesBySprint(String requestUri,String userName, String password);
    public SlaProcessingData getTimelyBacklogDelivery(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel, String userName, String password);
    public SlaProcessingData getBacklogItemQualityDelivery(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel, String userName, String password);
    public SlaProcessingData  getDelayInReadyForProductionRelease(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel, String userName, String password);
    public SlaProcessingData  getDefectsDetectedInUserAcceptanceTesting(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel, String userName, String password);
    public SlaProcessingData  getIssuesDetectedPostProductionRelease( String numURI, int expectedServiceLevel, int minServiceLevel, String userName, String password);
    public SlaProcessingData getProductPercentTestingCompleted(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel, String userName, String password);
    public SlaProcessingData getPercentageOfTestsAutomated(String denoUri, String numURI, int expectedServiceLevel, int minServiceLevel, String userName, String password);


    //Backlog Definition
    public List<Issue> getAllIssueOnJQL(String uri, String userName, String password, String baseURI);
    public List<Issue> getAllIssueWithChangeLogOnJQL(String uri, String userName, String password, String baseURI);
    public List<Issue> getAllIssueWithComment(String uri, String userName, String password, String baseURI);
    public List<Issue> getAllIssuesOnJQLV1(String userName, String password, String searchURI, String changeLogBaseURI, String commentbaseURI, boolean changeLogRequired, boolean commentsRequired);
    public List<Issue> getAllIssuesOnJQLV2(String userName, String password, String searchURI, String commentbaseURI, boolean changeLogRequired, boolean commentsRequired, int pageSize);

    public Issue getIssueByKey(String uri, String userName, String password);
    public List<BacklogIssue> getAllIssuesFromBacklog (String userName, String password, String baseURI, String boardIds,String changeLogURI, String commentURI,boolean isChangeLogRequired, boolean isCommentsRequired, int pageSize);
    public List<Issue> getAllIssuesFromBoard (String userName, String password, String baseURI, String boardIds, String changeLogURI, String commentURI,boolean isChangeLogRequired, boolean isCommentsRequired, int pageSize);
    public List<Issue> getAllIssuesFromBoardV1 (String userName, String password, String baseURI, String boardIds, String changeLogURI, String commentURI,boolean isChangeLogRequired, boolean isCommentsRequired, int pageSize);
    public List<Issue> GetIssueDifference (String userName, String password, String baseURI, String BacklogBoardId, String nblBoardId, String ProjectKey, int pageSize);
    //Actual Function

    //Final List of Methods to be available -- Start
    public List<Issue> getIssuesUsingJQL (String userName, String password, String searchJQL, String commentURI, boolean isCommentRequired, boolean isChangelogRequired, int pageSize);
    public List<Issue> getIssuesUsingBoard (String userName, String password, String baseURI, String boardId, boolean isChangeLogRequired, int pageSize);

    public List<Issue> getAllIssuesOnJQLV3(String userName, String password, String searchURI, String changeLogBaseURI, String commentbaseURI, boolean changeLogRequired, boolean commentsRequired,boolean closedsprints);
    //Final List of Methods to be available -- End
}
