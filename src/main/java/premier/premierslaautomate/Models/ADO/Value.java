package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Value implements Serializable
{

    private TestCaseReference testCaseReference;
    private Result results;
    private WorkItemTestPlan workItem;
    private Long id;

}
