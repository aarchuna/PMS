package premier.premierslaautomate.Models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties
public class FieldV1 implements Serializable
{
    private List<FixedVersion> fixVersions;
    private Resolution resolution;
    private String lastViewed;
    private Priority priority;
    private List<String> labels;
    private Status status;
    private String customfield_14000;//ORIGINAL ESTIMATION
    private String customfield_14001; //ACTUAL ESTIMATION
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
}
