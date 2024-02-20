package premier.premierslaautomate.Models;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Data
public class SlaProcessingData implements Serializable
{
    private String SLAName;
    private String SLAMeasurementPeriod;
    private String SprintsInvolved;
    private String anyOtherFilters;
    private String expectedServiceLevel;
    private String minimumServiceLevel;
    private String numCount;
    private String denCount;
    private String actualprocessingLevel;
    private String slaStatus;
    //private String processingDate;

}
