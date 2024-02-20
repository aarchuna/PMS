package premier.premierslaautomate.Models.Jira;

import lombok.Data;

import java.io.Serializable;

@Data
public class JiraIssueLinkType implements Serializable
{
    private String id;
    private String name;
    private String inward;
    private String outward;
    private String self;
}
