package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class BoardInfo implements Serializable
{
    private int id;
    private String self;
    private String name;
    private String type;
}
