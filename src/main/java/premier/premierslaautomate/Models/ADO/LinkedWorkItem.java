package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LinkedWorkItem implements Serializable {
    private String WorkItemId;
    private List<child> Children;
}



