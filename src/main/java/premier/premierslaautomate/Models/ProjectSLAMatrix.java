package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProjectSLAMatrix implements Serializable
{
    private String ProjectKey;
    private String ProcessingDate;
    private List<SlaProcessingData> slas;
}
