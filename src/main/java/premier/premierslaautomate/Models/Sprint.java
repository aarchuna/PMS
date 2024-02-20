package premier.premierslaautomate.Models;


import lombok.Data;

@Data
public class Sprint
{
    private Long id;
    private String State;
    private String name;
    private String  StartDate;
    private  String EndDate;
}
