package kr.sesac.wordcounter.parser;

import java.nio.file.Path;

public class ParserFactory {

    // [수정] parserGuess -> getParser : 확장자 기준으로 '선택'하는 동작이므로
    // 지원하지 않는 확장자면 null을 반환한다.
    public FileParser getParser(Path filePath){
        String fileName = filePath.getFileName().toString().toLowerCase();

        FileParser parser = null;
        if (fileName.endsWith(".txt")) {
            parser = new TxtParser();
        } else if (fileName.endsWith(".csv")) {
            parser = new CsvParser();
        } else if (fileName.endsWith(".tsv")) {
            parser = new TsvParser();
        } else if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            parser = new HtmlParser();
        }
        return parser;
    }
}
