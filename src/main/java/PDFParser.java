import destination.PositionClass;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PDFParser {

    private Document document;

    public PDFParser(String sourceDir) {
        File input = new File(sourceDir);
        try {
            document = Jsoup.parse(input, "UTF-8", "http://example.com/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PDFParser(File input) {
        try {
            document = Jsoup.parse(input, "UTF-8", "http://example.com/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseHTML(String destinationDir) {
        try {
            Elements pElement = findAllClassPElements();
            Elements rElement = findAllClassRElements();

            Map<Integer, StyleData> styleMap = findAllStyles(pElement);
            Map<Integer, HTMLBlockRange> pTempMap = fillDataWithPElements(pElement);
            Map<Integer, HTMLBlockRange> rTempMap = fillDataWithRElements(rElement);

            Map<Integer, RTextElement> finalData = getRTextElementsMap(rTempMap, pTempMap, styleMap);

            removeAllClassRelements(pElement);

            fillDataFinalDocument(finalData);
            File output = new File(destinationDir);
            FileUtils.writeStringToFile(output, document.outerHtml(), "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillDataFinalDocument(Map<Integer, RTextElement> rText) {
        Elements classElements = document.getElementsByClass("r");
        for (int i = 1; i < classElements.size(); i++) {
            int finalI = i;
            replaceDefaultStyle(classElements, finalI);
            final String[] noneTextStyle = {"<div class=\"p\" style=\"position: -webkit-sticky;\">\n</div>"};
            rText.forEach((k, v) -> {
                if (k == finalI) {
                    noneTextStyle[0] = new StringBuilder()
                            .append("<div class=\"p\"  style=\" ")
                            .append("font-family:" + v.getStyle().getFontFamily() + ";")
                            .append("font-size:" + v.getStyle().getFontSize() + "pt; ")
                            .append("position: relative; " + v.getStyle().getTextAlign())
                            .append("color:" + v.getStyle().getColor() + ";")
                            .append(" \">\n" + v.getText())
                            .append("</div>").toString();
                }
            });
            classElements.get(finalI).append(noneTextStyle[0]);
        }
    }

    private void replaceDefaultStyle(Elements classElements, int i) {
        String getClassElementData = classElements.get(i).toString();
        String s = getClassElementData.replaceAll("<|div|class|=|\"r\"|style|\"|>|&nbsp;|/|\n", "");
        s += "line-height: auto;";

        classElements.get(i).attr("style", s);
    }

    private void removeAllClassRelements(Elements pElement) {
        Elements elements = document.getAllElements();
        Integer idElement = 0;
        for (Element element : elements) {
            String idToDelete = "p" + idElement.toString();
            if (idElement < pElement.size()) {
                if (element.getElementById(idToDelete) != null) {
                    element.getElementById(idToDelete.toString()).remove();
                    idElement++;
                }
            }
        }
    }

    private Map<Integer, RTextElement> getRTextElementsMap(Map<Integer, HTMLBlockRange> rTempMap, Map<Integer, HTMLBlockRange> pTempMap,
                                                           Map<Integer, StyleData> styleMap) {
        Map<Integer, RTextElement> rTextElementsMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, HTMLBlockRange> entryR : rTempMap.entrySet()) {
            List<Integer> leftAlignList = new ArrayList<>();
            List<Integer> rightAlignList = new ArrayList<>();

            RTextElement rTextElement = extractRText(pTempMap, styleMap, entryR, leftAlignList, rightAlignList);
            if (rTextElement != null) {
                String align = findTextAlign(leftAlignList, rightAlignList);
                rTextElement.getStyle().setTextAlign(align);
                rTextElementsMap.put(entryR.getKey(), rTextElement);
            }
        }
        return rTextElementsMap;
    }

    private String findTextAlign(List<Integer> leftAlignList, List<Integer> rightAlignList) {
        int countLeftAlignList = 0;
        for (int i = 0; i < leftAlignList.size(); i++) {
            for (int j = i + 1; j < leftAlignList.size(); j++) {
                if (leftAlignList.get(i).equals(leftAlignList.get(j))) {
                    countLeftAlignList++;
                }
            }
        }
        int countRightAlignList = 0;
        for (int i = 0; i < rightAlignList.size(); i++) {
            for (int j = i + 1; j < rightAlignList.size(); j++) {
                if (rightAlignList.get(i).equals(rightAlignList.get(j))) {
                    countRightAlignList++;
                }
            }
        }

        return extratTextAlign(countLeftAlignList, countRightAlignList);
    }

    private String extratTextAlign(int countLeftAlignList, int countRightAlignList) {
        String align = "text-align: center;";
        if (countLeftAlignList > countRightAlignList) {
            align = "text-align: left;";
        } else if (countRightAlignList > countLeftAlignList) {
            align = "text-align: right;";
        }
        return align;
    }

    private RTextElement extractRText(Map<Integer, HTMLBlockRange> pTempMap, Map<Integer, StyleData> styleMap, Map.Entry<Integer, HTMLBlockRange> entryR, List<Integer> leftAlignList, List<Integer> rightAlignList) {
        int getYleftAlign = 0;
        int getYrightAlign = 0;
        int tempXposition = 0;
        RTextElement result = null;
        StyleData getRStyle = null;
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Integer, HTMLBlockRange> entryP : pTempMap.entrySet()) {
            if (entryR.getValue().getXFirst() <= entryP.getValue().getXFirst()
                    && entryR.getValue().getXSecond() >= entryP.getValue().getXSecond()) {
                if (leftAlignList.isEmpty()) {
                    getYleftAlign = entryP.getValue().getYFirst().intValue();
                    leftAlignList.add(entryP.getValue().getXFirst().intValue());
                } else if (!leftAlignList.isEmpty()) {
                    if (entryP.getValue().getYFirst().intValue() != getYleftAlign) {
                        leftAlignList.add(entryP.getValue().getXFirst().intValue());
                        getYleftAlign = entryP.getValue().getYFirst().intValue();
                    }
                }
                if (getYrightAlign == 0) {
                    getYrightAlign = entryP.getValue().getYFirst().intValue();
                    tempXposition = entryP.getValue().getXSecond().intValue();
                } else if (entryP.getValue().getYFirst().intValue() == getYrightAlign) {
                    tempXposition = entryP.getValue().getXSecond().intValue();
                } else if (entryP.getValue().getYFirst().intValue() != getYrightAlign) {
                    rightAlignList.add(tempXposition);
                    getYrightAlign = entryP.getValue().getYFirst().intValue();
                    tempXposition = entryP.getValue().getXSecond().intValue();
                }

                if (entryR.getValue().getYFirst() <= entryP.getValue().getYFirst() && entryR.getValue().getYSecond() >= entryP.getValue().getYSecond()) {
                    getRStyle = styleMap.get(entryP.getKey());
                    stringBuilder.append(entryP.getValue().getOwnText() + " ");
                }
            }
        }

        if (!stringBuilder.toString().isEmpty()) {
            result = RTextElement.builder().text(stringBuilder.toString()).style(getRStyle).build();
        }
        return result;
    }


    private Map<Integer, HTMLBlockRange> fillDataWithRElements(Elements rElement) {
        Map<Integer, HTMLBlockRange> rTempMap = new HashMap<>();
        Integer i = 1;
        PositionClass htmlPagePosition = null;
        for (Element e : rElement) {
            if (htmlPagePosition == null) {
                String s = e.parent().attr("style");
                htmlPagePosition = parseHTMLPage(s);
            }
            PositionClass positionClass = parseRHTMLClassData(e.attr("style"));
            if (!positionClass.getHeight().equals(htmlPagePosition.getHeight()) &&
                    !positionClass.getWidth().equals(htmlPagePosition.getWidth())) {
                rTempMap.put(i, fillBlockDataRange(positionClass));
                i++;
            }
        }
        return rTempMap;
    }

    private PositionClass parseHTMLPage(String htmlPage) {
        PositionClass positionClass = new PositionClass();
        String s = htmlPage.replaceAll("pt", "");
        String[] splitedText = s.split(":|;");
        for (int i = 0; i < splitedText.length; i++) {
            if (splitedText[i].equals("width")) {
                positionClass.setWidth(splitedText[i + 1]);
            } else if (splitedText[i].equals("height")) {
                positionClass.setHeight(splitedText[i + 1]);
            }
        }
        return positionClass;
    }

    private Map<Integer, HTMLBlockRange> fillDataWithPElements(Elements pElement) {
        Map<Integer, HTMLBlockRange> pTempMap = new LinkedHashMap<>();
        Integer i = 1;
        for (Element e : pElement) {
            pTempMap.put(i, fillBlockDataRange(parsePHTMLClassData(e.attr("style")), e.ownText()));
            i++;
        }
        return pTempMap;
    }

    private Map<Integer, StyleData> findAllStyles(Elements pElement) {
        Map<Integer, StyleData> styleMap = new LinkedHashMap<>();
        Integer styleIterator = 1;
        for (Element e : pElement) {
            styleMap.put(styleIterator, getStyle(e.attr("style")));
            styleIterator++;
        }
        return styleMap;
    }

    private Elements findAllClassPElements() {
        Elements pElement = document.select("div.p");
        return pElement;
    }

    private Elements findAllClassRElements() {
        Elements rElement = document.select("div.r");
        return rElement;
    }

    private HTMLBlockRange fillBlockDataRange(PositionClass positionClass, String ownText) {
        HTMLBlockRange htmlBlockRange = new HTMLBlockRange();

        htmlBlockRange.setOwnText(ownText); // set own text

        //x
        htmlBlockRange.setXFirst(Double.parseDouble(positionClass.getLeft()));
        htmlBlockRange.setXSecond(Double.parseDouble(positionClass.getLeft()) + Double.parseDouble(positionClass.getWidth()));

        //y
        htmlBlockRange.setYFirst(Double.parseDouble(positionClass.getTop()));
        htmlBlockRange.setYSecond(Double.parseDouble(positionClass.getTop()) + Double.parseDouble(positionClass.getHeight()));

        return htmlBlockRange;
    }

    private HTMLBlockRange fillBlockDataRange(PositionClass positionClass) { // overload method
        HTMLBlockRange htmlBlockRange = new HTMLBlockRange();

        //x
        htmlBlockRange.setXFirst(Double.parseDouble(positionClass.getLeft()));
        htmlBlockRange.setXSecond(Double.parseDouble(positionClass.getLeft()) + Double.parseDouble(positionClass.getWidth()));

        //y
        htmlBlockRange.setYFirst(Double.parseDouble(positionClass.getTop()));
        htmlBlockRange.setYSecond(Double.parseDouble(positionClass.getTop()) + Double.parseDouble(positionClass.getHeight()));

        return htmlBlockRange;
    }

    private StyleData getStyle(String style) {
        StyleData styleData = new StyleData();
        String s = style.replaceAll("pt", "");
        String[] splitedText = s.split(":|;");
        for (int i = 0; i < splitedText.length; i++) {
            if (splitedText[i].equals("font-family")) {
                styleData.setFontFamily(splitedText[i + 1]);
            } else if (splitedText[i].equals("font-size")) {
                styleData.setFontSize(splitedText[i + 1]);
            } else if (splitedText[i].equals("color")) {
                styleData.setColor(splitedText[i + 1]);
            }
        }
        return styleData;
    }


    private PositionClass parsePHTMLClassData(String style) {
        PositionClass positionClass = new PositionClass();
        String s = style.replaceAll("pt", "");
        String[] splitedText = s.split(":|;");
        for (int i = 0; i < splitedText.length; i++) {
            if (splitedText[i].equals("top")) {
                positionClass.setTop(splitedText[i + 1]);
            } else if (splitedText[i].equals("left")) {
                positionClass.setLeft(splitedText[i + 1]);
            } else if (splitedText[i].equals("line-height")) {
                positionClass.setHeight(splitedText[i + 1]);
            } else if (splitedText[i].equals("width")) {
                positionClass.setWidth(splitedText[i + 1]);
            }
        }
        return positionClass;
    }

    private PositionClass parseRHTMLClassData(String style) {
        PositionClass positionClass = new PositionClass();
        String s = style.replaceAll("pt", "");
        String[] splitedText = s.split(":|;");
        for (int i = 0; i < splitedText.length; i++) {
            if (splitedText[i].equals("top")) {
                positionClass.setTop(splitedText[i + 1]);
            } else if (splitedText[i].equals("left")) {
                positionClass.setLeft(splitedText[i + 1]);
            } else if (splitedText[i].equals("height")) {
                positionClass.setHeight(splitedText[i + 1]);
            } else if (splitedText[i].equals("width")) {
                positionClass.setWidth(splitedText[i + 1]);
            }
        }
        return positionClass;
    }

}
