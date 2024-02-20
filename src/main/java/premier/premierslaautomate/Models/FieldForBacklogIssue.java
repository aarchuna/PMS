package premier.premierslaautomate.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties
public class FieldForBacklogIssue implements Serializable
{
    private List<FixedVersion> fixVersions;
    private String customfield_13500;
    private Resolution resolution;
    private String customfield_11203;
    private String customfield_13501;
    private String customfield_12800;
    private String customfield_12802;
    private String customfield_14702;
    private String customfield_12801;
    private String customfield_14703;
    private String customfield_12804;
    private String customfield_12803;
    private String customfield_12806;
    private String customfield_12805;
    private String customfield_12808;
    private String customfield_12807;
    private String customfield_12809;
    private String lastViewed;
    private String customfield_14700;
    private String customfield_14701;
    private List<String> labels;
    private String customfield_13206;
    private String customfield_12911;
    private String customfield_12913;
    private Status status;
    private String customfield_14010;
    private String customfield_14011;
    private String customfield_14015;
    private String customfield_14013;
    private String customfield_14018;
    private String customfield_14016;
    private String customfield_13204;
    private String customfield_13600;
    private String customfield_14017;
    //private List<String> customfield_11414;
    //private List<String> customfield_11413;
    private String customfield_12901;
    private String customfield_14009;
    private String customfield_12900;
    private String customfield_12903;
    private String customfield_12902;
    private String customfield_10605;
    private String customfield_12905;
    private String customfield_12904;
    private String customfield_12907;
    private String customfield_12906;
    private String customfield_12909;
    private String customfield_12908;
    private String customfield_14000;//ACTUAL EST oR oRIGINAL est
    private String customfield_14400;
    private String customfield_14001;//ACTUAL EST oR oRIGINAL est
    private String customfield_14007;
    private String customfield_10200;
    private String customfield_14008;

    //private List<String> customfield_11412;  //error
    private String customfield_14006;
    private String customfield_13307;
    private Issuetype issuetype;
    private CustomField_14510 customfield_14510;
    private Project project;
    private String customfield_12730;
    private String customfield_12611;
    private String customfield_11400;

    private String customfield_12723;
    private String customfield_12722;
    private String customfield_14507;
    private String customfield_14903;
    private String customfield_12725;
    //private List<String> customfield_14504; //error
    private String customfield_14900;
    private String customfield_12724;
    private String customfield_14901;
    private String customfield_12726;

    private String customfield_14508;
    private String customfield_14904;
    private String resolutiondate;
    private String customfield_12728;
    private String customfield_14509;
    private String created;

    private String customfield_12200;
    private String customfield_14502;
    private String customfield_10300;
    private String customfield_12721;
    private String customfield_14500;
    private String customfield_12720;
    private String customfield_12712;
    //private List<String> customfield_13801;
    private String customfield_12711;
    //private List<String> customfield_13800;
    private String customfield_12714;

    private String customfield_11900;
    private String customfield_12713;
    private String customfield_12716;
    private String customfield_12715;
    private String customfield_12718;
    private String customfield_12717;
    private String customfield_12719;
    private String updated;

    private String customfield_13001;
    private String customfield_14213;
    private String description;
    private Customfield_13000 customfield_13000;
    private String customfield_14210;
    private String customfield_14211;
    private String customfield_14209;
    private String customfield_12701;
    private List<String> customfield_10005;
    private String customfield_12703;

    private String customfield_14603;
    private String customfield_14208;
    private String customfield_12705;
    private String customfield_12704;
    private String customfield_12706;
    private String customfield_12709;
    private String summary;
    private String customfield_14201;
    private String customfield_14202;
    private String customfield_14200;
    //private List<String> customfield_10000;
    private Customfield_14205 customfield_14205;
    private Customfield_14206 customfield_14206;
    private String customfield_10002;
    private String customfield_14203;

    private String customfield_12303;
    private Customfield_14204 customfield_14204;
    private String customfield_12302;
    private String customfield_10004;
    private String customfield_12811;
    private String customfield_13900;
    private String customfield_12810;
    private String customfield_12813;
    private String customfield_12812;
    private String customfield_12814;
    private String duedate;
    private Comment comment;
    private List<Issuelinks> issuelinks;
}
