package premier.premierslaautomate.Models.ADO;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
public class SlaResult
{
    private String slaName;
    private String slaType;
    private String expectedServiceLevel;
    private String minimumServiceLevel;
    private String numerator;
    private String denominator;
    private String actual;
    private String status;
}
