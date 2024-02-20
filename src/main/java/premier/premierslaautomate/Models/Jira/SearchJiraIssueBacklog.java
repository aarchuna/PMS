package premier.premierslaautomate.Models.Jira;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SearchJiraIssueBacklog implements Serializable
{
    private String expand;
    private int startAt;
    private int maxResults;
    private int total;
}
