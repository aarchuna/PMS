package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
@Data
public class RevisionFields implements Serializable {

    private String WorkItemType;
    private String State;
    private String IterationPath;
    private String Reason;
    private String CreatedDate;
    private String ClosedDate;
    private String ChangedDate;
    private String StateChangeDate;
    private double OriginalEffort;
    private AdoUser CreatedBy;
    private String RevisedDueDate;
    private String RevisedDateInfluencedBy;
}
