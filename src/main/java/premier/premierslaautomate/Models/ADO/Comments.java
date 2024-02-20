package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;

@Data
public class Comments implements Serializable {
    private String workItemId;
    private int version;
    private String text;
    private String createdDate;
    private String modifiedDate;
}

