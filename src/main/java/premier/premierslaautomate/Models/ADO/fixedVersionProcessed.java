package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class fixedVersionProcessed implements Serializable {
    private String fixedVersionId;
    private String fixedVersionTitle;
    private boolean released;
    private String releaseDate;
    List<workItemProcess> linkWorkITems;


}
