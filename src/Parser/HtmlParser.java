package kr.sesac.wordcounter.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class HtmlParser implements FileParser {

    // PROGRESS.md에 명시할 상수 설정 (본문 선택자)
    public static final String SELECTOR = "#content";

    @Override
    public String parse(Path path) throws FileParseException, IOException {
        String rawHtml;
        try {
            rawHtml = Utf8Rule.readAllStrict(path);
        } catch (java.nio.charset.MalformedInputException e) {
            throw new FileParseException("UTF-8로 읽을 수 없는 파일입니다: " + path.getFileName(), e);
        }

        Document doc = Jsoup.parse(rawHtml, "");

        // 1. 본문 선택자 검색
        Elements elements = doc.select(SELECTOR);

        // 2. 선택자가 정확히 1개가 아닌 경우 예외 처리 (0개 또는 2개 이상)
        if (elements.size() != 1) {
            throw new FileParseException("선택자 '" + SELECTOR + "'에 해당하는 요소가 정확히 1개이어야 합니다. (찾은 개수: " + elements.size() + ")");
        }

        Element targetElement = elements.first();

        // 3. 본문 안쪽의 불필요 태그(script, style, nav, header, footer) 및 내용 삭제
        targetElement.select("script, style, nav, header, footer").remove();

        // 4. 순수 텍스트 반환 (Jsoup이 HTML 엔티티인 &nbsp;, &#74; 등을 자동으로 디코딩함)
        return targetElement.text();
    }
}
