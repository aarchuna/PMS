package premier.premierslaautomate.ENUM;

public enum CommonKey {
    BACKLOG("BackLog"),
    NONBACKLOG("Non-BackLog"),
    BOTH("Both");

    public final String value;

    private CommonKey(String value) {
        this.value = value;
    }
}
