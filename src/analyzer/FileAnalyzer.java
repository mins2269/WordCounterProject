package kr.sesac.wordcounter.analyzer;

import kr.sesac.wordcounter.parser.*;
import kr.sesac.wordcounter.result.AnalysisResult;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 경로를 검증하고, 파일/폴더를 분석해 AnalysisResult를 만든다. 화면 출력은 하지 않는다. */
public class FileAnalyzer {
    private static final List<String> SUPPORTED_EXTENSIONS = List.of(".txt", ".csv", ".tsv", ".html", ".htm");

    private final ParserFactory parserFactory = new ParserFactory();

    public List<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }

    /** 경로를 분석한다. 경로가 없거나 읽을 수 없거나 폴더 순회에 실패하면 IOException */
    public AnalysisResult analyze(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("경로를 찾을 수 없습니다: " + path
                    + " (실제로 찾은 위치: " + path.toAbsolutePath().normalize() + ")");
        }
        if (!Files.isReadable(path)) {
            throw new IOException("경로를 읽을 수 없습니다: " + path);
        }

        AnalysisResult result = new AnalysisResult(path);
        long startTime = System.nanoTime();

        if (Files.isRegularFile(path)) {
            processFile(path, result);
        } else if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    if (Files.isRegularFile(entry)) {
                        processFile(entry, result);
                    }
                }
            } catch (DirectoryIteratorException e) {
                throw e.getCause();
            }
        }

        result.setElapsedTimeMs((System.nanoTime() - startTime) / 1_000_000.0);
        return result;
    }

    private void processFile(Path filePath, AnalysisResult result) {
        FileParser parser = parserFactory.getParser(filePath);
        if (parser == null) {
            result.skipIncrease();   // 건너뛴 파일은 '시도'에 포함하지 않는다
            return;
        }

        result.attemptIncrease();

        try {
            String text = parser.parse(filePath);

            // 파일 단위 원자성: 임시 맵에 먼저 집계하고, 끝까지 성공했을 때만 병합
            Map<String, Integer> fileCounts = new HashMap<>();
            for (String token : Tokenizer.tokenize(text)) {
                fileCounts.merge(token, 1, Integer::sum);
            }
            result.mergeFileCounts(fileCounts);

            result.successIncrease();
        } catch (FileParseException | IOException | UncheckedIOException e) {
            String message = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
            result.failIncrease();
            result.addFailMessage(filePath.getFileName() + ": " + message);
        }
    }
}
