package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdoUser implements Serializable {
    String displayName;
    String uniqueName;
}