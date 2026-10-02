package kr.sesac.wordcounter.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utf8Rule {
    private Utf8Rule() {}
    // 엄격한 UTF-8 디코더: 잘못된 바이트를 만나면 예외를 던짐
    public static BufferedReader newStrictReader(Path path) throws IOException {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        return new BufferedReader(new InputStreamReader(Files.newInputStream(path), decoder));
    }

    // TxtParser처럼 전체를 한 번에 문자열로 읽어야 할 때
    public static String readAllStrict(Path path) throws IOException {
        try (BufferedReader reader = newStrictReader(path)) {
            StringBuilder sb = new StringBuilder();
            int ch;
            char[] buf = new char[8192];
            while ((ch = reader.read(buf)) != -1) {
                sb.append(buf, 0, ch);
            }
            return sb.toString();
        }
    }
}
