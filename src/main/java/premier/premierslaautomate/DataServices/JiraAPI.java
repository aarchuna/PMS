package premier.premierslaautomate.DataServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import premier.premierslaautomate.Models.SearchBoard;
import premier.premierslaautomate.Models.SearchIssue;
import premier.premierslaautomate.Models.SearchIssueChangeLog;

@Component
public class JiraAPI {

    @Autowired
    private SlaRestClientService restClientService;

    public SearchBoard GetBoardsPagewise (String userName, String password, String requestURI)
    {
        return restClientService.exchangeWihCredential(requestURI, new ParameterizedTypeReference<SearchBoard>() {}, userName, password);
    }

    public SearchIssueChangeLog GetIssuesBySprintWithChangeLog(String userName, String password, String requestURI)
    {
        SearchIssueChangeLog searchIssueChangeLog = restClientService.exchangeWihCredential(requestURI, SearchIssueChangeLog.class, userName, password);
        return searchIssueChangeLog;
    }

    public SearchIssue Searchissues(String userName, String password, String requestURI)
    {
        SearchIssue data = restClientService.exchangeWihCredential(requestURI, SearchIssue.class, userName, password);
        return data;
    }


}
