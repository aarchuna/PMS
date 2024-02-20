package premier.premierslaautomate.Models.ADO;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WorkItemTestPlan implements Serializable
{
    private Long id;
    private String name;
    private List<JsonNode> workItemFields;
}
