package kr.sesac.wordcounter.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TsvParser implements FileParser {

    // PROGRESS.md에 명시할 상수 설정 (분석 대상 열 목록)
    public static final List<String> TARGET_COLUMNS = List.of("document");

    @Override
    public String parse(Path path) throws FileParseException, IOException {
        StringBuilder sb = new StringBuilder();

        // UTF-8 확인
        try (BufferedReader reader = Utf8Rule.newStrictReader(path)) {
            String headerLine = reader.readLine();

            // 1. 헤더 검증
            if (headerLine == null || headerLine.trim().isEmpty()) {
                throw new FileParseException("파일이 비어 있거나 헤더를 읽을 수 없습니다.");
            }

            // 헤더 분리 및 공백 제거 비교 준비
            String[] headers = headerLine.split("\t", -1);
            Map<String, Integer> headerIndexMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerIndexMap.put(headers[i].trim(), i);
            }

            // 2. 지정한 분석 열이 헤더에 포함되어 있는지 검증
            List<Integer> targetIndices = new ArrayList<>();
            for (String targetCol : TARGET_COLUMNS) {
                String trimmedTarget = targetCol.trim();
                if (!headerIndexMap.containsKey(trimmedTarget)) {
                    throw new FileParseException("지정한 분석 열이 헤더에 존재하지 않습니다: " + trimmedTarget);
                }
                targetIndices.add(headerIndexMap.get(trimmedTarget));
            }

            int expectedColumnCount = headers.length;
            String line;

            // 3. 데이터 레코드 읽기
            while ((line = reader.readLine()) != null) {
                String[] cells = line.split("\t", -1);

                // 셀 수가 헤더 수와 다르면 실패
                if (cells.length != expectedColumnCount) {
                    throw new FileParseException("레코드의 셀 수가 헤더 개수(" + expectedColumnCount +
                            ")와 다릅니다. 현재 행 셀 수: " + cells.length);
                }

                for (int index : targetIndices) {
                    String cellValue = cells[index];
                    if (cellValue != null && !cellValue.trim().isEmpty()) {
                        sb.append(cellValue).append("\n");
                    }
                }
            }
        }catch (java.nio.charset.MalformedInputException e){
            throw new FileParseException("UTF-8로 읽을 수 없는 파일입니다: " + path.getFileName(), e);
        }

        return sb.toString();
    }
}
