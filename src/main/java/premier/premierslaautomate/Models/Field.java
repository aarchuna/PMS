package premier.premierslaautomate.Models;
import lombok.Data;
import premier.premierslaautomate.Models.Jira.ClosedSprints;

import java.io.Serializable;
import java.util.List;

@Data
public class Field implements Serializable {
    private List<FixedVersion> fixVersions;
    private Resolution resolution;
    private String lastViewed;
    private Priority priority;
    private List<String> labels;
    private Status status;
    private String customfield_14000;//ORIGINAL ESTIMATION
    private String customfield_14001; //ACTUAL ESTIMATION
    private String customfield_14502; //CUSTOM FIELD for Release Date for ERPMM
    private String customfield_10002; //Custom field for Story Points
    private Issuetype issuetype;
    private Project project;
    private String resolutiondate;
    private String created;
    private String updated;
    private String description;
    private String summary;
    private String duedate;
    private Comment comment;
    private List<Issuelinks> issuelinks;
    private  List<ClosedSprints>closedSprints;
}






