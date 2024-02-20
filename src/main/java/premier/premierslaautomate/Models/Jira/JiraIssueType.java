package premier.premierslaautomate.Models.Jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties
public class JiraIssueType implements Serializable
{
    private String self;
    private String id;
    private String description;
    private String iconUrl;
    private String name;
    private String subtask;


}
