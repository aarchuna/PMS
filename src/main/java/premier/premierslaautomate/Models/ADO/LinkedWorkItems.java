package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import javax.naming.Name;
import java.io.Serializable;
import java.util.List;

@Data
public class LinkedWorkItems implements Serializable {
    private String queryResultType;
    private String asOf;
    private List<LinkedWorkItem> value;
}



