package CommonsLang;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Strings {

  private static String testString = "HH_Zues6M_Config_1_2019.04.14.12.02.47.108130_20191019T201507_OPD.h5";

  public static void main(String[] args) {

    /*
     * Extract a substring that matches a pattern
     */

    String[] tokens = StringUtils.splitByWholeSeparator(testString, "_");
    System.out.println("tokens=" + Arrays.toString(tokens));
    System.out.println("utc=" + tokens[4]);

    Pattern pattern = Pattern.compile("\\d{4}.\\d{2}.\\d{2}.\\d{2}.\\d{2}.\\d{2}.\\d{6}");
    Matcher matcher = pattern.matcher(testString);
    if (matcher.find()) {
      System.out.println("utc=" + matcher.group());
      tokens = StringUtils.splitByWholeSeparator(matcher.group(), ".");
      System.out.println("tokens=" + Arrays.toString(tokens));
    }
  }

}
