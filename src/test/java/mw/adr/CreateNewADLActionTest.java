package mw.adr;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class CreateNewADLActionTest {

    List<String> names= Arrays.asList("0001-PRJ-ESB-optymalizaccja-pamieciowa-modelu.md",
            "0002-PRJ-ESB-IDM-ybór-narzędzia-do-analizy-logów-dziennika.md",
            "0001-TCH-ESB-optymalizacja-pamięciowa-modelu-(xmlseealso).md",
            "0003-TCH-ESB-BPM-sposob-persystencji",
            "0004-TCH-tylko-to.md"
    );

    @DisplayName("names")
    @Test
    void newAdlTest() {
        Pattern patt=Pattern.compile("([0-9]{4})-((PRJ|TCH)(-[A-Z]{3})*).+");

    names.forEach(
        it -> {
          Matcher matcher = patt.matcher(it);

          if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
              if ( matcher.group(i) == null) {
                  continue;                  //
              }
              System.out.println(i+"=>" + matcher.group(i));
            }
          }
          System.out.println("");
        });
     }
}