package premier.premierslaautomate.Interfaces;

import premier.premierslaautomate.Models.Issue;
import premier.premierslaautomate.Models.ProcessedData;
import premier.premierslaautomate.config.MeasureConfiguration;
import premier.premierslaautomate.config.ProjectConfiguration;

import java.util.List;

public interface IProject {
    public ProcessedData Process (MeasureConfiguration sla, String userName, String password, ProjectConfiguration project, List<Issue> retrievedIssues);
}
