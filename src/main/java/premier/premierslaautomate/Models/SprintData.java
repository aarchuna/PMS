package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SprintData implements Serializable
{
  private String expand;
  private String startAt;
  private String maxResults;
  private String total;
  private List<Issuelinks> issuelinks;
}
