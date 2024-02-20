package premier.premierslaautomate.ENUM;

public enum JiraTypes {
    EPIC("Epic"),
    STORY("Story");

    public final String value;

    private JiraTypes(String value) {
        this.value = value;
    }
}
