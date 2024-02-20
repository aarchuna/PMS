package premier.premierslaautomate.Models.Jira;

import lombok.Data;
import premier.premierslaautomate.DataServices.JiraPriority;

import java.io.Serializable;

@Data
public class JiraIssuLinkRecordField implements Serializable
{
    private String summary;
    private JiraStatus status;
    private JiraPriority priority;
    private JiraIssueType issuetype;

}
