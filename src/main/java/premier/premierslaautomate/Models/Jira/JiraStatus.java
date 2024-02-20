package premier.premierslaautomate.Models.Jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import premier.premierslaautomate.Models.StatusCategory;

import java.io.Serializable;

@Data
@JsonIgnoreProperties
public class JiraStatus implements Serializable
{
    private String self;
    private String description;
    private String iconUrl;
    private String name;
    private String id;
    private StatusCategory statusCategory;

}
