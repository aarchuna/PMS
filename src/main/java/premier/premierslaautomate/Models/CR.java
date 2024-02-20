package premier.premierslaautomate.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CR implements Serializable {

   private String  CrNumber ;
   private List<String> fixedversions;
   private boolean isDelayed;

}
