package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Priority implements Serializable
{
    private String self;
    private String iconUrl;
    private String name;
    private int id;
}
