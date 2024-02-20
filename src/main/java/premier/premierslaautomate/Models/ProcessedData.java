package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProcessedData implements Serializable
{
    private String SLAName;
    private String SLAKey;
    private String SLAType;
    private String SLAMeasurementPeriod;
    private String expectedServiceLevel;
    private String minimumServiceLevel;
    private String numCount;
    private String denCount;
    private String actual;
    private String slaStatus;
    private String AdoQuery;
}
