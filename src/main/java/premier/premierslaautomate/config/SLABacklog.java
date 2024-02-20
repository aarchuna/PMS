package premier.premierslaautomate.config;

import lombok.Data;

import java.util.List;

@Data
public class SLABacklog {
    private List<MeasureConfiguration> backlogconfiguration;
}
