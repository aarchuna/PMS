package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TimeToOfferBackLogJira implements Serializable {
    String StoryId;
    String Type;
    Date receiveddate;
    Date estimateddate;
    boolean eligibleforestimation;
    Float estimationhour;
    long daystoprovideestimation;
    boolean withinlimit;
}
