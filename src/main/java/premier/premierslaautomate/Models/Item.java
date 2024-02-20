package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Item implements Serializable
{
    private String field;
    private String fromString;
    private String toString;

}
