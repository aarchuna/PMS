package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
@Data
public class InwardIssue implements Serializable {
    private String id;
    private String key;
    private String self;
    private Fields fields;
}
