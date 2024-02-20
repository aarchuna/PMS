package premier.premierslaautomate.DataServices;

import lombok.Data;

import java.io.Serializable;

@Data
public class JiraPriority implements Serializable
{
    private String self;
    private String iconUrl;
    private String name;
    private String id;
}
