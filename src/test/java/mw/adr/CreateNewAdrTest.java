package mw.adr;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CreateNewAdrTest {
  List<String> validNames =
      Arrays.asList(

          "PRJ-ESB Optymalizacja pamięciowa modelu",
          "PRJ-ESB-IDM Wybór narzędzia",
          "TCH-ESB optymalizacja-pamięciowa-modelu-(xmlseealso).md",
          "TCH-ESB-BPM sposob-persystencji",
          "TCH tylko-to.md");

  List<String> notValidNames =
      Arrays.asList(
          "ABC-ESB Optymalizacja pamięciowa modelu",
          "PRJ-ESB-IDM_Wybór narzędzia",
          "TCH-ESB",
          "TCHESB-BPM",
          "0004-C-ylko-to.md");

  @DisplayName("adr names test")
  @Test
  void namesTest() {
      Pattern patt=Pattern.compile("(((PRJ|TCH)(-[A-Z]{3})*)\\s(.+))");
      validNames.forEach(it -> {
          Matcher matcher = patt.matcher(it);
          Assertions.assertThat(matcher.matches()).isEqualTo(true);
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
