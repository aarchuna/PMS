package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Revision implements Serializable {

    private String count;
    private List<RevisionValueObject> value;
}
