package kr.sesac.wordcounter.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CsvParser implements FileParser {

    // PROGRESS.md에 명시할 상수 설정 (분석 대상 열 목록)
    public static final List<String> TARGET_COLUMNS = List.of("text");
    // 예: Chatbot 데이터용으로 변경 시 -> List.of("Q", "A");

    @Override
    public String parse(Path path) throws FileParseException, IOException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = Utf8Rule.newStrictReader(path)) {
            // 헤더 자동 인식 및 공백 제거 설정
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build();

            try (CSVParser parser = format.parse(reader)) {
                // String: 헤더에 있는 각 열 이름
                // integer: 각 열 이름이 몇 번째 칸인지
                Map<String, Integer> headerMap = parser.getHeaderMap();

                // 1. 헤더 검증: 파일이 비어있거나 헤더가 없는 경우
                if (headerMap == null || headerMap.isEmpty()) {
                    throw new FileParseException("헤더를 읽을 수 없거나 파일이 비어 있습니다.");
                }

                // 2. 지정한 분석 대상 열이 헤더에 존재하는지 검증
                List<Integer> targetIndices = new ArrayList<>();
                for (String targetCol : TARGET_COLUMNS) {
                    String trimmedTarget = targetCol.trim();
                    if (!headerMap.containsKey(trimmedTarget)) {
                        throw new FileParseException("지정한 분석 열이 헤더에 없습니다: " + trimmedTarget);
                    }
                    targetIndices.add(headerMap.get(trimmedTarget));
                }

                int expectedColumnCount = headerMap.size();

                // 3. 레코드 순회 (UncheckedIOException 발생 가능 지점)
                // parser가 파일에서 레코드를 한 줄씩 꺼내준다
                // 여기서 레코드를 하나씩 꺼낼 때 따움표로 열린 필드의 끝이 따움표가 없으면 예외를 던진다.
                for (CSVRecord record : parser) {
                    // 레코드의 셀 수가 헤더의 열 수와 다르면 실패 처리
                    if (record.size() != expectedColumnCount) {
                        throw new FileParseException("레코드의 셀 수가 헤더 개수(" + expectedColumnCount +
                                ")와 다릅니다. 현재 행 셀 수: " + record.size());
                    }

                    // 대상 열의 셀 값 추출 (셀 경계가 섞이지 않게 줄바꿈으로 구분)
                    for (int index : targetIndices) {
                        String cellValue = record.get(index);
                        if (cellValue != null && !cellValue.trim().isEmpty()) {
                            sb.append(cellValue).append("\n");
                        }
                    }
                }
            } catch (UncheckedIOException e) {
                // Commons CSV가 던지는 닫히지 않은 따옴표 등의 문법 오류 예외 포획
                throw new FileParseException("CSV 형식 오류 발생 (닫히지 않은 따옴표 등): " + e.getMessage(), e);
            }
        }catch (java.nio.charset.MalformedInputException e) {
            throw new FileParseException("UTF-8로 읽을 수 없는 파일입니다: " + path.getFileName(), e);
        }

        return sb.toString();
    }
}
