package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Project implements Serializable
{
    private String self;
    private String id;
    private String key;
    private String name;
    private String projectTypeKey;
    private ProjectCategory projectCategory;

}
