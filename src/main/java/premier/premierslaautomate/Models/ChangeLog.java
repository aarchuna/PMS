package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ChangeLog implements Serializable
{
    private int startAt;
    private int maxResults;
    private int total;
    private List<History> histories;
}
