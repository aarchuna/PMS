package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WorkItem implements Serializable

{
    private String id;
    private String rev;
    private WorkItemFields fields;
    //private Object _links;
    //private List<Revision> revisions;
    private List<RevisionValue> revisions;
    private Comment comment;
    private List<WorkItem> childLinks;
}
