package kr.sesac.wordcounter.parser;

import java.nio.file.Path;

public class ParserFactory {

    public FileParser parserGuess(Path filePath){
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
