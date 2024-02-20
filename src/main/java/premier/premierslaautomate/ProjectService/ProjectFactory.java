package premier.premierslaautomate.ProjectService;

import premier.premierslaautomate.ENUM.ProjectKey;
import premier.premierslaautomate.Interfaces.IProject;

public class ProjectFactory {
    public IProject CreateProject (String projectKey)
    {
        if (projectKey == null || projectKey.isEmpty())
            return null;



        return new ProcessSLA();
    }
}
