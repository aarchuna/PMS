package premier.premierslaautomate.Models.ADO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Comment implements Serializable {
    private String totalCount;
    private String count;
    private List<Comments> comments;
}
