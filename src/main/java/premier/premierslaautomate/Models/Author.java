package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
@Data
public class Author implements Serializable {
    private String displayName;
    private String emailAddress;

}