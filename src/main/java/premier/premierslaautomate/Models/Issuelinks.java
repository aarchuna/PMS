package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
@Data
public class Issuelinks  implements Serializable
{
private String id;
private String self;
private Type type;
private InwardIssue inwardIssue;
private OutwardIssue outwardIssue;

}
