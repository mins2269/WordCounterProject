package kr.sesac.wordcounter.output;

import kr.sesac.wordcounter.result.AnalysisResult;
import kr.sesac.wordcounter.result.WordCountSorter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** 결과를 화면에 출력하고 파일로 저장한다. */
public class ResultOutput {
    private static final Path OUTPUT_PATH = Path.of("out", "counts.tsv");

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printSummary(AnalysisResult r) {
        System.out.println("입력: " + r.getInputPath());
        System.out.println("파일: 시도 " + r.getAttempted() + "개 / 성공 " + r.getSuccess() + "개 / 실패 "
                + r.getFail() + "개 / 지원하지 않아 건너뜀 " + r.getSkip() + "개");
        System.out.println("전체 단어: " + r.getTotalWordCount() + "개 / 서로 다른 단어: " + r.getDistinctCount() + "개");
        System.out.println("처리 시간: " + String.format("%.2f", r.getElapsedTimeMs()) + "ms");

        if (!r.getFailMessages().isEmpty()) {
            System.out.println("[실패 상세 정보]");
            for (String msg : r.getFailMessages()) {
                System.out.println(" - " + msg);
            }
        }
        System.out.println();
    }

    public void printTopWords(AnalysisResult r, int topCount) {
        List<Map.Entry<String, Integer>> sorted = WordCountSorter.sortedEntries(r.getWordCountMap());
        int limit = Math.min(topCount, sorted.size());   // 단어 종류보다 큰 N이면 있는 만큼만
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            System.out.println((i + 1) + ". " + entry.getKey() + " : " + entry.getValue() + "회");
        }
        System.out.println();
    }

    public void printWordCount(String word, int count) {
        System.out.println(word + " : " + count + "회");
        System.out.println();
    }
}
