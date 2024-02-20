package premier.premierslaautomate.Models.Jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties
public class JiraStatusCategory implements Serializable
{
    private String self;
    private String id;
    private String key;
    private String colorName;
    private String name;

}
