package premier.premierslaautomate.Models.Jira;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JiraResolution implements Serializable
{
    private String self;
    private String id;
    private String description;
    private String name;
}
