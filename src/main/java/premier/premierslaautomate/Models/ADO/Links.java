package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
@Data
public class Links implements Serializable {
    private String workItemComments;
    private String workItemRevisions;
    private String workItemType;
}
