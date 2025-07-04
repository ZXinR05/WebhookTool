package xin.xinx.webhookTool.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TranslationTool {
    static final Pattern MC_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?s");
    public static Map<String, String> loadTranslations(InputStream inputStream) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, type);
        }
    }

    public static String toMessageFormat(String sourceString) {
        Matcher matcher = MC_PATTERN.matcher(sourceString);
        int autoIndex = 0;
        StringBuilder sb = new StringBuilder();
        while(matcher.find()) {
            String indexGroup = matcher.group(1);
            String replacement;

            if(indexGroup != null) {
                int index = Integer.parseInt(indexGroup) - 1;
                replacement = "{" + index + "}";
            } else {
                replacement = "{" + autoIndex + "}";
                autoIndex ++;
            }
            // 使用 Matcher.appendReplacement 来正确处理匹配前后的字符串以及替换
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String serialize(Component component) {
        Component translatedComponent = GlobalTranslator.render(component, Locale.SIMPLIFIED_CHINESE);
        return PlainTextComponentSerializer.plainText().serialize(translatedComponent);
    }
}

