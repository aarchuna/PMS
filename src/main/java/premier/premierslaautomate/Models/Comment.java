package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Comment implements Serializable {
    private  List<Comments> comments;
}