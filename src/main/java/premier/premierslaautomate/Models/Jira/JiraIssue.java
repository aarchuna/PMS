package premier.premierslaautomate.Models.Jira;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class JiraIssue implements Serializable
{
    private String expand;
    private int id;
    private String self;
    private String key;
    private JiraField fields;

}
