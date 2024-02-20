package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

    @Data
    public class IssueDateVariance implements Serializable {
        private String Key;
        private String Type;
        private String IssueStatus;
        private String Priority;
        private Date commitedDate;
        Date closedDate;
        private String commitedDateString;
        private String closedDateString;
        private Double variance;
        private Double varianceinMin;
        private Double varianceinHour;
        private int limit;
        private String status;
        private double estimatedHours;
        private double actualHours;
        private String comments;


    }




