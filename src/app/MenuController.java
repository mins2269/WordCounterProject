package kr.sesac.wordcounter.app;

import kr.sesac.wordcounter.analyzer.FileAnalyzer;
import kr.sesac.wordcounter.output.ResultOutput;
import kr.sesac.wordcounter.result.AnalysisResult;
import kr.sesac.wordcounter.storage.ResultWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * 메뉴를 보여주고, 선택에 맞는 기능을 연결한다.
 * 입력은 ConsoleInput, 분석은 FileAnalyzer, 출력/저장은 ResultOutput에 맡긴다.
 */
public class MenuController {
    private final FileAnalyzer analyzer;
    private final ConsoleInput input;
    private final ResultOutput output;
    private final ResultWriter resultWriter = new ResultWriter();


    private AnalysisResult currentResult = null;   // 최근 분석 결과

    public MenuController(FileAnalyzer analyzer, ConsoleInput input, ResultOutput output) {
        this.analyzer = analyzer;
        this.input = input;
        this.output = output;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            running = handleMenuChoice(input.readMenuChoice());
        }
    }

    private void printMenu() {
        output.printMessage("문서 단어 분석기");
        output.printMessage("1. 새 분석 시작");
        output.printMessage("2. 상위 N개 단어 보기");
        output.printMessage("3. 특정 단어 횟수 찾기");
        output.printMessage("4. 전체 결과 저장");
        output.printMessage("5. 최근 분석 요약 보기");
        output.printMessage("0. 종료");
    }

    /** 메뉴 번호에 맞는 기능을 실행한다. 계속 실행하면 true, 종료하면 false */
    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 0:
                output.printMessage("프로그램을 종료합니다.");
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
                output.printMessage("정상 입력 범위는 0~5입니다.");
                return true;
        }
    }

    // 1. 새 분석 시작
    private void startNewAnalysis() {
        while (true) {
            Optional<Path> pathInput = input.readPath();
            if (pathInput.isEmpty()) {
                output.printMessage("새 분석을 취소하고 메뉴로 돌아갑니다.");
                return;
            }
            Path path = pathInput.get();

            AnalysisResult result;
            try {
                result = analyzer.analyze(path);
            } catch (IOException e) {   // 경로 없음/읽기 불가/폴더 순회 실패를 한 곳에서 처리
                output.printMessage(e.getMessage());
                continue;
            }

            // 분석할 파일이 없어서 다시 입력받아야 하면 true
            if (handleAnalysisResult(path, result)) {
                continue;
            }
            return;
        }
    }

    /**
     * 분석 결과를 검사한다.
     * 분석 가능한 파일이 없으면 안내 후 true(재입력 필요),
     * 있으면 currentResult로 저장하고 요약을 출력한 뒤 false를 반환한다.
     */
    private boolean handleAnalysisResult(Path path, AnalysisResult result) {
        if (result.getAttempted() == 0) {
            if (Files.isRegularFile(path)) {
                output.printMessage("지원하지 않는 파일 형식입니다. 지원 확장자: " + analyzer.getSupportedExtensions());
            } else {
                output.printMessage("분석할 지원 파일" + analyzer.getSupportedExtensions() + "이 없습니다.");
            }
            return true;
        }
        if (result.getSuccess() == 0) {
            output.printMessage("성공한 파일이 없습니다.");
        }

        this.currentResult = result;
        output.printMessage("");
        output.printMessage("분석완료");
        output.printSummary(result);
        return false;
    }

    // 2. 상위 N개 단어 보기
    private void showTopNWords() {
        if (!hasQueryableResult()) return;
        int topCount = input.readTopCount();
        output.printTopWords(currentResult, topCount);
    }

    // 3. 특정 단어 횟수 찾기
    private void findWordCount() {
        if (!hasQueryableResult()) return;
        String word = input.readWord();
        int count = currentResult.getWordCountMap().getOrDefault(word, 0);
        output.printWordCount(word, count);
    }

    // 4. 전체 결과 저장
    private void saveTotalResult() {
        if (!hasQueryableResult()) return;
        try {
            Path saved = resultWriter.save(currentResult);
            output.printMessage("전체 결과 " + currentResult.getDistinctCount() + "개 단어를 " + saved + "에 저장했습니다.");
        } catch (IOException e) {
            output.printMessage("파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
        output.printMessage("");
    }

    // 5. 최근 분석 요약 보기
    private void showRecentSummary() {
        if (!hasAnalysisResult()) return;
        output.printSummary(currentResult);
    }

    private boolean hasAnalysisResult() {
        if (currentResult == null) {
            output.printMessage("먼저 1번을 선택해 새 분석을 시작하세요.");
            return false;
        }
        return true;
    }

    /** 조회·저장 전용 가드: 분석 결과가 있고 성공한 파일이 1개 이상이어야 한다. */
    private boolean hasQueryableResult() {
        if (!hasAnalysisResult()) return false;
        if (currentResult.getSuccess() == 0) {
            output.printMessage("성공한 파일이 없어 조회·저장할 수 없습니다. 5번에서 실패 내용을 확인하세요.");
            return false;
        }
        return true;
    }
}
