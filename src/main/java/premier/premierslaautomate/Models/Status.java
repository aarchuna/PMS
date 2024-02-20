package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
@Data
public class Status implements Serializable {
    private String self;
    private String description;
    private String iconUrl;
    private String id;
    private String name;
    private StatusCategory statusCategory;
}
