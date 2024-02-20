package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AvgCycleJira implements Serializable {
    private String Key;
    private String Type;
    private String IssueStatus;
    private String commitedDate;
    private String releaseDate;
    private String associatedFixedVersions;
    private String matchedFixedVersions;
    private String multipleFixedVersions;
    private String fixedVersionType;
    private double totalDuration;
    private String Status;

}