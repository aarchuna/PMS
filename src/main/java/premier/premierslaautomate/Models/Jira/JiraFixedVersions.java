package premier.premierslaautomate.Models.Jira;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class JiraFixedVersions implements Serializable
{
    private String self;
    private String id;
    private String description;
    private String name;
    private boolean archived;
    private boolean released;
    private String releaseDate;
}
