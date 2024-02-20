package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
@Data
public class StatusCategory implements Serializable {
    private String id;
    private String key;
    private String colorName;
    private String name;
}
