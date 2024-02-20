package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestCaseReference implements Serializable
{
    private int id;
    private String name;
    private String state;
}
