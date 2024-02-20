package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Resolution implements Serializable
{
    private String self;
    private String id;
    private String description;
    private String name;
}


