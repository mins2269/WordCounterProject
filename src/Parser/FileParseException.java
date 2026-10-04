package kr.sesac.wordcounter.parser;

public class FileParseException extends Exception {
    public FileParseException(String message) {
        super(message);
    }

    public FileParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
