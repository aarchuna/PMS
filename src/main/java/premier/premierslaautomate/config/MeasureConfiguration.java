package premier.premierslaautomate.config;

import lombok.Data;

@Data
public class MeasureConfiguration {
    private String slaname;
    private String slakey;
    private String slatype;
    private String from;
    private String to;
    private String expectedsla;
    private String minimumsla;
    private String numjql;
    private String denojql;
    private String limit;
    private String config1;
    private String config2;
    private String config3;
    private String config4;
    private String config5;
    private String input1;
    private String input2;
    private String input3;
    private String input4;
    private String input5;
    private String testPlanId;
    private String testSuiteId;
}
