package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
@Data
public class OutwardIssue implements Serializable {
    private String id;
    private String key;
    private Fields fields;

}
