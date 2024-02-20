package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Comments implements Serializable {
    private CommentAuthor author;
    private String body;
    private CommentAuthor updateAuthor;
    private String created;
    private String updated;
}
