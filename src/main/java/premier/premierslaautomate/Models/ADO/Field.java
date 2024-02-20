package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Field implements Serializable {

    private String queryResultType;
    private String asOf;
    private List<WorkItemsHeader> workItemHeaders;
}
