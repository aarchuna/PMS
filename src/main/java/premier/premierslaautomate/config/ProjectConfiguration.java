package premier.premierslaautomate.config;

import lombok.Data;

import java.util.List;

@Data
public class ProjectConfiguration {
    private String projectKey;
    private String projectsource;
    private String projecturl;
    private String backlogBoardIds;
    private String nonbacklogBoardIds;
    private String outputFolder;
    private String datafileRequired;
    private String detailedLogRequired;
    private String addToFileName;
    private String logFile;
    private String outputPath;
    private String jsonPath;
    private String tabKey;
    private String twoSpace;
    private String newLine;
    private String detailedLogFile;
    private String dateFormat;
    private String releaseDateFormat;
    private String holidays;
    private String sourceDateFormat;
    private String pageSize;
    private String automationData;
    private String linkItemUrl;
    private String itemUrl;
    private String  authType;
    private String testPlanUrl;
    private String testPlanId;
    private String testSuiteId;
    private int [] tier0;
    private int [] tier1;
    private int [] tier2;
    private int [] tier3;
    private int [] problemRT;
}
