package premier.premierslaautomate.Utilities;

public class UrlUtils {

    public static String getSprintByIdUrl(String url,Integer sprintId){
        return String.format(url,sprintId);
    }
}
