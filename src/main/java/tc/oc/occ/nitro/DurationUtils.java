package tc.oc.occ.nitro;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationUtils {

  private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([smhdw])");

  private DurationUtils() {}

  public static Long parseDurationMillis(String input) {
    if (input == null) {
      return null;
    }
    String trimmed = input.trim().toLowerCase(Locale.ROOT);
    if (trimmed.isEmpty()) {
      return null;
    }
    if (isIndefinite(trimmed)) {
      return null;
    }
    Matcher matcher = DURATION_PATTERN.matcher(trimmed);
    long total = 0;
    int lastEnd = 0;
    while (matcher.find()) {
      if (matcher.start() != lastEnd) {
        return null;
      }
      long value = Long.parseLong(matcher.group(1));
      char unit = matcher.group(2).charAt(0);
      switch (unit) {
        case 's':
          total += value * 1000L;
          break;
        case 'm':
          total += value * 60_000L;
          break;
        case 'h':
          total += value * 3_600_000L;
          break;
        case 'd':
          total += value * 86_400_000L;
          break;
        case 'w':
          total += value * 604_800_000L;
          break;
        default:
          return null;
      }
      lastEnd = matcher.end();
    }
    if (lastEnd != trimmed.length() || total <= 0) {
      return null;
    }
    return total;
  }

  public static boolean isIndefinite(String input) {
    if (input == null) {
      return false;
    }
    String trimmed = input.trim().toLowerCase(Locale.ROOT);
    return trimmed.equals("indefinite") || trimmed.equals("permanent") || trimmed.equals("forever");
  }

  public static String formatDuration(long durationMillis) {
    long seconds = durationMillis / 1000L;
    long weeks = seconds / 604800;
    seconds %= 604800;
    long days = seconds / 86400;
    seconds %= 86400;
    long hours = seconds / 3600;
    seconds %= 3600;
    long minutes = seconds / 60;
    seconds %= 60;

    StringBuilder builder = new StringBuilder();
    appendUnit(builder, weeks, "w");
    appendUnit(builder, days, "d");
    appendUnit(builder, hours, "h");
    appendUnit(builder, minutes, "m");
    appendUnit(builder, seconds, "s");
    if (builder.length() == 0) {
      return "0s";
    }
    return builder.toString().trim();
  }

  private static void appendUnit(StringBuilder builder, long value, String suffix) {
    if (value > 0) {
      if (builder.length() > 0) {
        builder.append(' ');
      }
      builder.append(value).append(suffix);
    }
  }
}
