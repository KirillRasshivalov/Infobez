package kirillR.util;

import org.springframework.web.util.HtmlUtils;

public final class XssSanitizer {

    private XssSanitizer() {
    }

    public static String sanitize(String value) {
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(value);
    }
}
