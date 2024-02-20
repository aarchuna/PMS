package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class IssueActivityDate implements Serializable
{
    private String IssueKey;
    private String RequestedStatus;
    private Date RequestedDate;
    private String RequestedDateString;
    private Date SecondRequestedDate;
    private String SecondRequestedDateString;
    private int occurance;
}
