package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TestPlan implements Serializable
{
    private List<Value> value;
}
