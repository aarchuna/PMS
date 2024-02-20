package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVariables implements Serializable
{
    private String teamProject;
    private String tier;
    private String from;
    private String to;
    private String BaselineAvg;
    private String iterationPathFormat;
    private String areaPathBL;
    private String areaPathNBL;
    private String userName;
    private String password;
    private String testPlanId;
    private String testSuitId;
}
