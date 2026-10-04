package kr.sesac.wordcounter.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Tokenizer {
    private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d+");

    private Tokenizer() {}

    public static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            String token = matcher.group().toLowerCase();
            if (!DIGIT_PATTERN.matcher(token).matches()) {
                tokens.add(token);
            }
        }
        return tokens;
    }
}
