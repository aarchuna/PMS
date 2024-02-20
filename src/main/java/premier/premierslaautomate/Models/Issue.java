package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Issue implements Serializable
{
    private String expand;
    private String id;
    private String self;
    private String key;
    private Field fields;
    private ChangeLog changelog;

}
