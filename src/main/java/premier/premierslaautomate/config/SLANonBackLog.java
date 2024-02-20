package premier.premierslaautomate.config;

import lombok.Data;

import java.util.List;

@Data
public class SLANonBackLog {
    private List<MeasureConfiguration> nonbacklogconfiguration;
}
