package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
@Data
public class WorkItemFields implements Serializable {

    private String AreaPath;
    private String TeamProject;
    private String IterationPath;
    private String WorkItemType;
    private String State;
    private String Reason;
    private String CreatedDate;
    private String ChangedDate;
    private String ResolvedDate;
    private String ClosedDate;
    private String AcceptedDate;
    private String DueDate;
    private String StateChangeDate;
    private int CommentCount;
    private String Title;
    private String BoardColumn;
    private String Severity;
    private String Priority;
    private double ActualEffortinHours;
    private double EffortinHoursRemaining;
    private double OriginalEffort;
    private String ServiceLevelType;
    private int SecurityThreatCount;
    private double StoryPoints;
    private String FixedVersions;
    private String Tags;
    private String DateInfluenced;
    private String RevisedDueDate;
    private String TestNotExecuted;
    private String AutomationStatus;
    private String TypesofTesting;
    private String RevisedDueDateInfluencedBy;
    private String ResolvedReason;
}
