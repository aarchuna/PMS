package premier.premierslaautomate.Models.ADO;

import lombok.Data;
import premier.premierslaautomate.Models.Comment;

import java.io.Serializable;
import java.util.List;

@Data
public class WorkItemObject implements Serializable {

    private String id;
    private String rev;
    private Object fields;
    private Object _links;
    private List<Revision> revisions;
    private Comment comment;

}
