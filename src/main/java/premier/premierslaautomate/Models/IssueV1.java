package premier.premierslaautomate.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties
public class IssueV1 implements Serializable
{
    private String expand;
    private String id;
    private String self;
    private String key;
    private FieldV1 fields;
    private ChangeLog changelog;

}
