package kr.sesac.wordcounter.parser;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Path;

public class TxtParser implements FileParser {
    @Override
    public String parse(Path path) throws FileParseException, IOException {
        try {
            return Utf8Rule.readAllStrict(path);
        } catch (MalformedInputException e) {
            throw new FileParseException("UTF-8로 읽을 수 없는 파일입니다: " + path.getFileName(), e);
        }
    }
}
