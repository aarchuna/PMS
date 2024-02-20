package premier.premierslaautomate.Models;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class FixedVersionIssue implements Serializable
{
    private FixedVersion fixedVersion;
    private List<Issue> issueList;
}
