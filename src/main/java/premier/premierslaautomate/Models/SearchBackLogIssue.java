package premier.premierslaautomate.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties
public class SearchBackLogIssue implements Serializable
{
    private String expand;
    private int startAt;
    private int maxResults;
    private int total;
    private List<BacklogIssue> issues;
}
