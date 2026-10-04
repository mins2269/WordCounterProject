package kr.sesac.wordcounter.analyzer;

import kr.sesac.wordcounter.result.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DocumentWordAnalyzer {
    private static final List<String> SUPPORTED_EXTENSIONS = List.of(".txt", ".csv", ".tsv", ".html", ".htm");

    private final Scanner sc = new Scanner(System.in);
    private AnalysisResult currentResult = null; // 최근 분석 결과 저장
    private FileAnalyzer fileAnalyzer;

    public DocumentWordAnalyzer(FileAnalyzer fileAnalyzer){
        this.fileAnalyzer = fileAnalyzer;
    }

    public boolean numCheck(int num) {
        switch (num) {
            case 0:
                System.out.println("프로그램을 종료합니다.");
                return false;
            case 1:
                startNewAnalysis();
                return true;
            case 2:
                showTopNWords();
                return true;
            case 3:
                findWordCount();
                return true;
            case 4:
                saveTotalResult();
                return true;
            case 5:
                showRecentSummary();
                return true;
            default:
                System.out.println("정상 입력 범위는 0~5입니다.");
                return true;
        }
    }

    // TODO 1. 새 분석 시작
    private void startNewAnalysis() {
        while (true) {
            System.out.print("파일 또는 폴더 경로 > ");
            String pathStr = sc.nextLine().trim();

            if (pathStr.isEmpty()){
                System.out.println("새 분석을 취소하고 메뉴로 돌아갑니다.");
                return;
            }

            Path path = Path.of(pathStr);

            // 존재하는 지
            if (!Files.exists(path)) {
                System.out.println("경로를 찾을 수 없습니다: " + pathStr
                        + " (실제로 찾은 위치: " + path.toAbsolutePath().normalize() + ")");
                continue;
            }
            // 주어진 경로가 현재 프로그램의 권한으로 읽을 수 있는 지 검사
            if(!Files.isReadable(path)){
                System.out.println("경로를 읽을 수 없습니다." + pathStr);
                continue;
            }

            // 분석할 파일 or 폴더의 결과를 담을 객체
            AnalysisResult result = new AnalysisResult(path);
            // 입력받은 경로가 파일, 폴더인지 검사 후 상황에 맞는 검사

            long startTime = System.nanoTime();
            fileAnalyzer.startAnalyze(path, result);
            long endTime = System.nanoTime();
            result.setElapsedTimeMs((endTime - startTime) / 1_000_000.0);

            if(printSummaryAndPathInit(path, result)){
                continue;
            }
            break;
        }
    }
    // TODO 분석 후 요약 메시지 출력 그리고 경로 최신화
    private boolean printSummaryAndPathInit(Path path, AnalysisResult result){
        // 성공 개수만 검증하면 지원 확장자는 맞는데 지원하지 않는 파일이라고 출력됨
        int attemptedParsable = result.getSuccess() + result.getFail();
        if(attemptedParsable == 0){
            // 지원하지 않는 파일을 직접 입력
            if(Files.isRegularFile(path)){
                System.out.println("지원하지 않는 파일 형식입니다. 지원 확장자: " + SUPPORTED_EXTENSIONS);
            } // 폴더에 지원 파일이 하나도 없음
            else {
                System.out.println("분석할 지원 파일" + SUPPORTED_EXTENSIONS + "이 없습니다.");
            }
            return true;
        }
        // 다 실패
        if(result.getSuccess() == 0 && result.getFail() >0){
            System.out.println("성공한 파일이 없습니다.");
        }
        // 폳더에 지원 확장자 아닌게 섞임
        if(result.getSkip() > 0){
//            System.out.println("지원하지 않는 파일 " + result.getSkip() + "개를 건너뛰었습니다.");
        }

        this.currentResult = result;

        System.out.println();
        System.out.println("분석완료");
        result.printSummary();
        return false;
    }
    // TODO 2. 상위 N개 단어 보기
    private void showTopNWords () {
            if (hasQueryableResult()) {
                if (hasAnalysisResult()) {// TODO startNewAnalysis 메서드에서 currentResult를 초기화함
                    Map<String, Integer> map = currentResult.getWordCountMap();
                    while (true) {
                        System.out.print("몇 개를 볼까요? (기본 10) > ");
                        String numStr = sc.nextLine().trim();

                        int N = 10;

                        // TODO 1. 엔터, 2. 영어, 한글 String 입력 3. 1 미만 정수
                        // TODO 두 if 문은 차례대로 2, 3 filter, 둘 다 해당 안 하는 엔터 경우는 그냥 기본 초기화 값 10
                        if (!numStr.isEmpty()) {
                            try {
                                N = Integer.parseInt(numStr);
                            } catch (NumberFormatException e) {
                                System.out.println("1 이상의 정수를 입력하세요.");
                                continue;
                            }
                        }
                        if (N < 1) {
                            System.out.println("1 이상의 정수를 입력하세요.");
                            continue;
                        }
                        // map을 list에 담고 내림차순으로 정렬
                        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
                        list.sort(
                                Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                                        .thenComparing(Map.Entry::getKey)
                        );
                        // 단어 종류보다 큰 N이 입력될 경우 list.size()보다 더 확인할 필요가 없기 때문
                        int limit = Math.min(N, list.size());
                        for (int i = 0; i < limit; i++) {
                            Map.Entry<String, Integer> entry = list.get(i);
                            System.out.println((i + 1) + ". " + entry.getKey() + " : " + entry.getValue() + "회");
                        }
                        System.out.println();
                        break;
                    }
                }
            }
    }
    // TODO 3. 특정 단어 횟수 찾기
    private void findWordCount () {
            if (!hasQueryableResult()) return;
            if (!hasAnalysisResult()) {
                return;
            }
            while (true) {
                System.out.print("찾을 단어 > ");
                List<String> tokens = Tokenizer.tokenize(sc.nextLine().trim());
                if (tokens.size() != 1) {
                    System.out.println("단어 하나를 입력하세요");
                    continue;
                }
                String targetToken = tokens.get(0);
                int count = currentResult.getWordCountMap().getOrDefault(targetToken, 0);
                System.out.println(targetToken + " : " + count + "회");
                System.out.println();
                break;
            }
    }
    // TODO 4. 전체 결과 저장
    private void saveTotalResult () {
            if (hasQueryableResult()) {
                if (hasAnalysisResult()) {
                    Path outputPath = Path.of("out", "counts.tsv");
                    try {
                        // 상위 디렉토리 체크
                        // API는 파일을 만들어주긴 해도 상위 폴더까지는 자동으로 만들어주지 않는다.
                        // 그래서 사용하기 전에 폴더부터 확실하게 만들어주기 위해
                        if (outputPath.getParent() != null) {
                            // TODO 폴더를 생성하다 ioexception 터질 수 있음
                            Files.createDirectories(outputPath.getParent());
                        }
                        // 상위 N개 조회와 동일한 정렬 로직 재사용 (예: 빈도 내림차순, 동점시 사전순)
                        List<Map.Entry<String, Integer>> list = new ArrayList<>(currentResult.getWordCountMap().entrySet());
                        list.sort(
                                Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                                        .thenComparing(Map.Entry::getKey)
                        );
                        // TODO 파일을 열다가 실패하면 ioException 던진다
                        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
                            // TODO 여기서 write하는 순간 바로 기존 파일은 이번 새로운 결과로 덮어쓴다.
                            writer.write("word\tcount");
                            writer.newLine();
                            for (Map.Entry<String, Integer> entry : list) {
                                writer.write(entry.getKey() + "\t" + entry.getValue());
                                writer.newLine();
                            }
                        }
                        System.out.println("전체 결과 " + list.size() + "개 단어를 " + outputPath + "에 저장했습니다.");
                    } catch (IOException e) {
                        System.out.println("파일 저장 중 오류가 발생했습니다: " + e.getMessage());
                    }
                    System.out.println();
                }
            }

    }
    // TODO 5. 최근 분석 요약 보기
    private void showRecentSummary () {
            if (!hasAnalysisResult()) return;
            currentResult.printSummary();
    }
    // TODO 분석 결과 유무 체크 가드 함수
    private boolean hasAnalysisResult () {
            if (currentResult == null) {
                System.out.println("먼저 1번을 선택해 새 분석을 시작하세요.");
                return false;
            }
            return true;
    }
    // TODO 조회·저장 전용 가드: 성공한 파일이 1개 이상이어야 함
    private boolean hasQueryableResult () {
            if (!hasAnalysisResult()) return false;
            if (currentResult.getSuccess() == 0) {
                System.out.println("성공한 파일이 없어 조회·저장할 수 없습니다. 5번에서 실패 내용을 확인하세요.");
                return false;
            }
            return true;
        }
}
