package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Issuetype implements Serializable {
    private String self;
    private String id;
    private String description;
    private String iconUrl;
    private String name;
    private String subtask;
    private String avatarId;
}
