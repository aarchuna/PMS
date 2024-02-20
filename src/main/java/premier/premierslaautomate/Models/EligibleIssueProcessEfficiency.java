package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class EligibleIssueProcessEfficiency implements Serializable
{
    private String Key;
    private String Type;
    private String IssueStatus;
    private Priority priority;
    private String committeddate;
    private String delivereddate;
    private double actualhourspent;
    private double totalhourstodeliver;
    private double totalDays;
    private String eligibility;

}
