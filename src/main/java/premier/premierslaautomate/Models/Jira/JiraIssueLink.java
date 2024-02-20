package premier.premierslaautomate.Models.Jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties
public class JiraIssueLink implements Serializable
{
    private String id;
    private String self;
    private JiraIssueLinkType type;
    private JiraInOutwardIssue outwardIssue;
    private JiraInOutwardIssue inwardIssue;
}
