//import destination.PositionClass;
//import org.apache.commons.io.FileUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.*;
//
//public class JsoupParser {
//
//    private Document document;
//
//    public void parseHTML(String sourceDir, String destinationDir) {
//
//        Document document = null;
//        File input = new File(sourceDir);
//
//        try {
//            document = Jsoup.parse(input, "UTF-8", "http://example.com/");
//            Elements pElement = findAllClassPElements(document);
//            Elements rElement = findAllClassRElements(document);
//
//            Map<Integer, StyleData> styleMap = findAllStyles(document, pElement);
//            Map<Integer, HTMLBlockRange> pTempMap = fillDataWithPElements(pElement);
//            Map<Integer, HTMLBlockRange> rTempMap = fillDataWithRElements(rElement);
//
//            Map<Integer, FinalData> finalData = getFinalData(rTempMap, pTempMap, styleMap);
//
//            removeAllClassRelements(document, pElement);
//
//            fillDataFinalDocument(document, finalData);
//            File output = new File(destinationDir);
//            FileUtils.writeStringToFile(output, document.outerHtml(), "UTF-8");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void fillDataFinalDocument(Document document, Map<Integer, FinalData> finalData) {
//        Integer dataIterate = 1;
//        Elements classElements = document.getElementsByClass("r");
//        System.out.println(classElements.size());
//
//        for (int i = 1; i < classElements.size(); i++) {
//
//            String getClassElementData = classElements.get(i).toString();
//            String s = getClassElementData.replaceAll("<|div|class|=|\"r\"|style|\"|>|&nbsp;|/|\n", "");
//            s += "line-height: auto;";
//
//            classElements.get(i).attr("style", s);
//
//            if (finalData.get(i).getStyle() == null) {
//                String noneTextStyle = "<div class=\"p\" style=\"position: -webkit-sticky;\">\n</div>";
//                classElements.get(i).append(noneTextStyle);
//            } else {
//                StringBuilder styleStringBuilder = new StringBuilder();
//                styleStringBuilder.append("<div class=\"p\"  style=\" ")
//                        .append("font-family:"+finalData.get(i).getStyle().getFontFamily()+";")
//                        .append("font-size:"+finalData.get(i).getStyle().getFontSize()+"pt; ")
//                        .append("position: -webkit-sticky; " + finalData.get(i).getTextAlign())
//                        .append("color:" + finalData.get(i).getStyle().getColor() + ";")
//                        .append(" \">\n"+finalData.get(dataIterate).getOwnText())
//                        .append("</div>");
//                classElements.get(i).append(styleStringBuilder.toString());
//            }
//            dataIterate++;
//        }
//    }
//
//    public void removeAllClassRelements(Document document, Elements pElement) {
//        Elements elements = document.getAllElements();
//        Integer idElement = 0;
//        for (Element element : elements) {
//            String idToDelete = "p" + idElement.toString();
//            if (idElement < pElement.size()) {
//                if (element.getElementById(idToDelete) != null) {
//                    element.getElementById(idToDelete.toString()).remove();
//                    idElement++;
//                }
//            }
//        }
//    }
//
//    public Map<Integer, FinalData> getFinalData(Map<Integer, HTMLBlockRange> rTempMap, Map<Integer, HTMLBlockRange> pTempMap,
//                                                Map<Integer, StyleData> styleMap) {
//        Map<Integer, FinalData> finalData = new LinkedHashMap<>();
//
//        StringBuilder stringBuilder;
//
//        for (Map.Entry<Integer, HTMLBlockRange> entryR : rTempMap.entrySet()) {
//            stringBuilder = new StringBuilder();
//            List<Integer> leftAlignList = new ArrayList<>();
//            List<Integer> rightAlignList = new ArrayList<>();
//
//            int getYleftAlign = 0;
//            int getYrightAlign = 0;
//            int tempXposition = 0;
//
//            StyleData getRStyle = null;
//            for (Map.Entry<Integer, HTMLBlockRange> entryP : pTempMap.entrySet()) {
//
//                if (entryR.getValue().getXFirst() <= entryP.getValue().getXFirst()
//                        && entryR.getValue().getXSecond() >= entryP.getValue().getXSecond()) {
//                    if (leftAlignList.isEmpty()) {
//                        getYleftAlign = entryP.getValue().getYFirst().intValue();
//                        leftAlignList.add(entryP.getValue().getXFirst().intValue());
//                    } else if (!leftAlignList.isEmpty()) {
//                        if (entryP.getValue().getYFirst().intValue() != getYleftAlign) {
//                            leftAlignList.add(entryP.getValue().getXFirst().intValue());
//                            getYleftAlign = entryP.getValue().getYFirst().intValue();
//                        }
//                    }
//                    if (getYrightAlign == 0) {
//                        getYrightAlign = entryP.getValue().getYFirst().intValue();
//                        tempXposition = entryP.getValue().getXSecond().intValue();
//                    } else if (entryP.getValue().getYFirst().intValue() == getYrightAlign) {
//                        tempXposition = entryP.getValue().getXSecond().intValue();
//                    } else if (entryP.getValue().getYFirst().intValue() != getYrightAlign) {
//                        rightAlignList.add(tempXposition);
//                        getYrightAlign = entryP.getValue().getYFirst().intValue();
//                        tempXposition = entryP.getValue().getXSecond().intValue();
//                    }
//
//                    if (entryR.getValue().getYFirst() <= entryP.getValue().getYFirst() && entryR.getValue().getYSecond() >= entryP.getValue().getYSecond()) {
//                        getRStyle = styleMap.get(entryP.getKey());
//                        stringBuilder.append(entryP.getValue().getOwnText() + " ");
//                    }
//                }
//            }
//
//            int countLeftAlignList = 0;
//            for (int i = 0; i < leftAlignList.size(); i++) {
//                for (int j = i + 1; j < leftAlignList.size(); j++) {
//                    if (leftAlignList.get(i).equals(leftAlignList.get(j))) {
//                        countLeftAlignList++;
//                    }
//                }
//            }
//            int countRightAlignList = 0;
//            for (int i = 0; i < rightAlignList.size(); i++) {
//                for (int j = i + 1; j < rightAlignList.size(); j++) {
//                    if (rightAlignList.get(i).equals(rightAlignList.get(j))) {
//                        countRightAlignList++;
//                    }
//                }
//            }
//
//            String align = "";
//            if (countLeftAlignList > countRightAlignList) {
//                align = "text-align: left;";
//            } else if (countRightAlignList > countLeftAlignList) {
//                align = "text-align: right;";
//            } else if (countLeftAlignList == countRightAlignList) {
//                align = "text-align: center;";
//            }
//
//            finalData.put(entryR.getKey(), fillFinalData(stringBuilder.toString(), align, getRStyle));
//            stringBuilder.setLength(0);
//        }
//        return finalData;
//    }
//
//
//
//    public Map<Integer, HTMLBlockRange> fillDataWithRElements(Elements rElement) {
//        Map<Integer, HTMLBlockRange> rTempMap = new HashMap<>();
//        Integer i = 1;
//        for (Element e : rElement) {
//
//
//            String s = e.parent().attr("style");
//            PositionClass htmlPagePosition = parseHTMLPage(s);
//
//            PositionClass positionClass = parseRHTMLClassData(e.attr("style"));
//            if (!positionClass.getHeight().equals(htmlPagePosition.getHeight()) && !positionClass.getWidth().equals(htmlPagePosition.getWidth())) {
//                rTempMap.put(i, fillBlockDataRange(positionClass));
//                i++;
//            }
//
////            PositionClass positionClass = parseRHTMLClassData(e.attr("style"));
////            if (!positionClass.getTop().equals("0.0") && !positionClass.getLeft().equals("0.0")) {
////                rTempMap.put(i, fillBlockDataRange(positionClass));
////                i++;
////            }
//        }
//        return rTempMap;
//    }
//
//    public Map<Integer, HTMLBlockRange> fillDataWithPElements(Elements pElement) {
//        Map<Integer, HTMLBlockRange> pTempMap = new LinkedHashMap<>();
//        Integer i = 1;
//        for (Element e : pElement) {
//            pTempMap.put(i, fillBlockDataRange(parsePHTMLClassData(e.attr("style")), e.ownText()));
//            System.out.println(e.ownText());
//            i++;
//        }
//        return pTempMap;
//    }
//
//    public Map<Integer, StyleData> findAllStyles(Document document, Elements pElement) {
//        Map<Integer, StyleData> styleMap = new LinkedHashMap<>();
//        Integer styleIterator = 1;
//        for (Element e : pElement) {
//            styleMap.put(styleIterator, getStyle(e.attr("style")));
//            styleIterator++;
//        }
//        return styleMap;
//    }
//
//    public Elements findAllClassPElements(Document document) {
//        Elements pElement = document.select("div.p");
//        return pElement;
//    }
//
//    public Elements findAllClassRElements(Document document) {
//        Elements rElement = document.select("div.r");
//        return rElement;
//    }
//
//
//    public  FinalData fillFinalData(String ownText, String textAlign, StyleData styleData) {
//        FinalData finalData = new FinalData();
//        finalData.setOwnText(ownText);
//        finalData.setTextAlign(textAlign);
//        finalData.setStyle(styleData);
//        return finalData;
//    }
//
//    public  HTMLBlockRange fillBlockDataRange(PositionClass positionClass, String ownText) {
//        HTMLBlockRange htmlBlockRange = new HTMLBlockRange();
//
//        htmlBlockRange.setOwnText(ownText); // set own text
//
//        //x
//        htmlBlockRange.setXFirst(Double.parseDouble(positionClass.getLeft()));
//        htmlBlockRange.setXSecond(Double.parseDouble(positionClass.getLeft()) + Double.parseDouble(positionClass.getWidth()));
//
//        //y
//        htmlBlockRange.setYFirst(Double.parseDouble(positionClass.getTop()));
//        htmlBlockRange.setYSecond(Double.parseDouble(positionClass.getTop()) + Double.parseDouble(positionClass.getHeight()));
//
//        return htmlBlockRange;
//    }
//
//    public  HTMLBlockRange fillBlockDataRange(PositionClass positionClass) { // overload method
//        HTMLBlockRange htmlBlockRange = new HTMLBlockRange();
//
//        //x
//        htmlBlockRange.setXFirst(Double.parseDouble(positionClass.getLeft()));
//        htmlBlockRange.setXSecond(Double.parseDouble(positionClass.getLeft()) + Double.parseDouble(positionClass.getWidth()));
//
//        //y
//        htmlBlockRange.setYFirst(Double.parseDouble(positionClass.getTop()));
//        htmlBlockRange.setYSecond(Double.parseDouble(positionClass.getTop()) + Double.parseDouble(positionClass.getHeight()));
//
//        return htmlBlockRange;
//    }
//
//    public StyleData getStyle(String style) {
//        StyleData styleData = new StyleData();
//        String s = style.replaceAll("pt", "");
//        String[] splitedText = s.split(":|;");
//        for (int i = 0; i < splitedText.length; i++) {
//            if (splitedText[i].equals("font-family")) {
//                styleData.setFontFamily(splitedText[i + 1]);
//            } else if (splitedText[i].equals("font-size")) {
//                styleData.setFontSize(splitedText[i + 1]);
//            } else if (splitedText[i].equals("color")) {
//                styleData.setColor(splitedText[i + 1]);
//            }
//        }
//        return styleData;
//    }
//
//
//    public  PositionClass parsePHTMLClassData(String style) {
//        PositionClass positionClass = new PositionClass();
//        String s = style.replaceAll("pt", "");
//        String[] splitedText = s.split(":|;");
//        for (int i = 0; i < splitedText.length; i++) {
//            if (splitedText[i].equals("top")) {
//                positionClass.setTop(splitedText[i + 1]);
//            } else if (splitedText[i].equals("left")) {
//                positionClass.setLeft(splitedText[i + 1]);
//            } else if (splitedText[i].equals("line-height")) {
//                positionClass.setHeight(splitedText[i + 1]);
//            } else if (splitedText[i].equals("width")) {
//                positionClass.setWidth(splitedText[i + 1]);
//            }
//        }
//        return positionClass;
//    }
//
//    public  PositionClass parseRHTMLClassData(String style) {
//        PositionClass positionClass = new PositionClass();
//        String s = style.replaceAll("pt", "");
//        String[] splitedText = s.split(":|;");
//        for (int i = 0; i < splitedText.length; i++) {
//            if (splitedText[i].equals("top")) {
//                positionClass.setTop(splitedText[i + 1]);
//            } else if (splitedText[i].equals("left")) {
//                positionClass.setLeft(splitedText[i + 1]);
//            } else if (splitedText[i].equals("height")) {
//                positionClass.setHeight(splitedText[i + 1]);
//            } else if (splitedText[i].equals("width")) {
//                positionClass.setWidth(splitedText[i + 1]);
//            }
//        }
//        return positionClass;
//    }
//
//    public  PositionClass parseHTMLPage (String htmlPage) {
//        PositionClass positionClass = new PositionClass();
//        String s = htmlPage.replaceAll("pt", "");
//        String[] splitedText = s.split(":|;");
//        for (int i = 0; i < splitedText.length; i++) {
//            if (splitedText[i].equals("width")) {
//                positionClass.setWidth(splitedText[i + 1]);
//            } else if (splitedText[i].equals("height")) {
//                positionClass.setHeight(splitedText[i + 1]);
//            }
//        }
//        return positionClass;
//    }
//
//}
