package premier.premierslaautomate.Models.Jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import premier.premierslaautomate.Models.Fields;

import java.io.Serializable;

@Data
@JsonIgnoreProperties
public class JiraInOutwardIssue implements Serializable
{
    private String id;
    private String key;
    private String self;
    private JiraIssuLinkRecordField fields;

}
