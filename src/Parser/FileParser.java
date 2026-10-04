package kr.sesac.wordcounter.parser;

import java.io.IOException;
import java.nio.file.Path;

public interface FileParser {
    /**
     * 파일에서 분석할 텍스트 추출
     * @param path 읽을 파일 경로
     * @return 추출된 텍스트 (셀/요소 간은 공백 또는 줄바꿈으로 구분)
    // * @throws FileParseException 파싱 규칙 위반 또는 형식 오류 발생 시
     * @throws IOException 파일 읽기 에러 발생 시
     */
    String parse(Path path) throws FileParseException, IOException;
}
