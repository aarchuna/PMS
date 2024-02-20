package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
@Data
public class RevisionValue implements Serializable {
    private String id;
    private String rev;
    private RevisionFields fields;
}
