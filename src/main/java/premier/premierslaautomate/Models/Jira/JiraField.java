package premier.premierslaautomate.Models.Jira;

import lombok.Data;
import premier.premierslaautomate.DataServices.JiraPriority;
import premier.premierslaautomate.Models.Jira.JiraFixedVersions;
import premier.premierslaautomate.Models.Jira.JiraResolution;
import premier.premierslaautomate.Models.Resolution;
import premier.premierslaautomate.Models.Status;

import java.io.Serializable;
import java.util.List;
@Data
public class JiraField implements Serializable
{
    private List<JiraFixedVersions> fixVersions;
    private String customfield_14000;
    private String customfield_14001;
    private JiraPriority priority;
    private JiraResolution resolution;
    private String lastViewed;
    private List<String> labels;
    private List<JiraIssueLink> issuelinks;
    private Status status;
}
