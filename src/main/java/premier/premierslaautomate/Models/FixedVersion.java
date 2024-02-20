package premier.premierslaautomate.Models;
import lombok.Data;

import java.io.Serializable;

@Data
public class FixedVersion implements Serializable
{
    private String self;
    private String id;
    private String name;
    private boolean archived;
    private boolean released;
    private String releaseDate;
}

