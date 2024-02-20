package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SearchBoard implements Serializable
{


    private int maxResults;
    private int startAt;
    private int total;
    private boolean isLast;
    private List<BoardInfo> values;
}
