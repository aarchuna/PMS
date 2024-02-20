package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SearchIssueChangeLog implements Serializable
{
    private String expand;
    private int startAt;
    private int maxResults;
    private int total;
    private List<Issue> issues;

}
