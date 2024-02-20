package premier.premierslaautomate.Models;

import lombok.Data;

import java.util.List;

@Data
public class History
{
    private String id ;
    private String created;
    private List<Item> items;
    private Author author;
}
