package premier.premierslaautomate.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties
public class BacklogIssue implements Serializable
{
    private String expand;
    private String id;
    private String self;
    private String key;
    private FieldForBacklogIssue fields;
    private ChangeLog changelog;

}
