package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class workItemProcess implements Serializable
{
    private String workitemId;
    private String workitemType;
    private String wotkItemStatus;
    private String workItemTags;
    private String closedDate;
    private String status;
}
