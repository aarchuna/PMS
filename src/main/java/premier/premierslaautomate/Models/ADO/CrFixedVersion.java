package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CrFixedVersion implements Serializable {
    private String workItemId;
    private String crTitle;
    private List<WorkItem> fixedVersions; //Associated Fixed Versions with Linked items
    private List<fixedVersionProcessed> processedData;
    private String crStatus;
}
