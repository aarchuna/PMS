package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class KeyValue implements Serializable
{
    private String Key;
    private String Type;
    private String Status;
    private String Value;
}
