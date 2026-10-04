package kr.sesac.wordcounter.result;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.file.Path;
import java.util.List;

public class AnalysisResult {
    // TODO 단일 분석 작업에 대한 통계
    //  시도, 성공, 실패, 건너뜀 개수, 전체 단어 수, 단어별 카운트 Map, 소요시간
    private final Path inputPath;

    private int attempt = 0;
    private int success = 0;
    private int fail = 0;
    private int skip = 0;
    private long totalWordCount = 0;
    private double elapsedTimeMs = 0.0;

    private final Map<String, Integer> wordCountMap = new LinkedHashMap<>();
    private final List<String> failMessages = new ArrayList<>();
    // 생성자
    public AnalysisResult(Path inputPath){ this.inputPath = inputPath;}
    public void attemptIncrease(){attempt++;}
    public void successIncrease(){success++;}
    public void failIncrease(){fail++;}
    public void skipIncrease(){skip++;}

    public void addFailMessage(String message) {
        this.failMessages.add(message);
    }
    // getter
    public int getSuccess(){return success;}
    public int getSkip() { return skip; }
    public int getFail() {return fail;}
    public Map<String, Integer> getWordCountMap() {return wordCountMap;}
    // 단어 횟수 더하기
    public void addWordCount(String word, int count){
        this.wordCountMap.put(word,this.wordCountMap.getOrDefault(word,0) + count);
        this.totalWordCount += count;
    }
    public void setElapsedTimeMs(double elapsedTimeMs) {this.elapsedTimeMs = elapsedTimeMs;
    }
    // 출력
    public void printSummary(){
        System.out.println("입력: " + inputPath);
        System.out.println("파일: 시도 " + attempt + "개 / 성공 " + success + "개 / 실패 " + fail + "개 / 지원하지 않아 건너뜀 " + skip + "개");
        System.out.println("전체 단어:  " + totalWordCount + "개 / 서로 다른 단어: " + wordCountMap.size() + "개");
        System.out.println("처리 시간: " + String.format("%.2f", elapsedTimeMs) + "ms");

        if (!failMessages.isEmpty()) {
            System.out.println("[실패 상세 정보]");
            for (String msg : failMessages) {
                System.out.println(" - " + msg);
            }
        }
        System.out.println();
    }
}
