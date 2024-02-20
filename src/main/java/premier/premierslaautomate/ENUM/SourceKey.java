package premier.premierslaautomate.ENUM;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum SourceKey {
    JIRA("Jira"),
    ADO("ADO");

    public final String value;

    private SourceKey(String value) {
        this.value = value;
    }
}
