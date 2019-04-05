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
//import java.util.stream.Collectors;
//
//public class JSoupTest {
//
//    public static void main(String[] args) {
//        JSoupTest jSoupTest = new JSoupTest();
//        jSoupTest.parseHTML("/Users/ab/Documents/Idea/TRANNING/pdf_to_html/src/main/java/destination/stick_1.html","/Users/ab/Documents/Idea/TRANNING/pdf_to_html/src/main/java/destination/output.html");
//    }
//
//    public void parseHTML(String sourceDir, String destinationDir){
//
//        Document document = null;
//        File input = new File(sourceDir);
//
//        try {
//            document = Jsoup.parse(input, "UTF-8", "http://example.com/");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Elements pElement = findAllClassPElements(document);
//        Elements rElement = findAllClassRElements(document);
//
//        Map<Integer, StyleData> styleMap = findAllStyles(document, pElement);
//        Map<Integer, HTMLBlockRange> pTempMap = fillDataWithPElements(pElement);
//        Map<Integer, HTMLBlockRange> rTempMap = fillDataWithRElements(rElement);
//
//        Map<Integer, FinalData> finalData  = getFinalData(rTempMap, pTempMap, styleMap);
//
//        removeAllClassRelements(document, pElement);
//
//        fillDataFinalDocument(document,finalData);
//
//        File output = new File(destinationDir);
//        try {
//            FileUtils.writeStringToFile(output, document.outerHtml(), "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void fillDataFinalDocument(Document document, Map<Integer, FinalData> finalData){
//        Integer dataIterate = 1;
//        Elements classElements = document.getElementsByClass("r");
//        for (int i = 1; i < classElements.size(); i++) {
//
//            String getClassElementData = classElements.get(i).toString();
//            String s = getClassElementData.replaceAll("<|div|class|=|\"r\"|style|\"|>|&nbsp;|/|\n", "");
//            s += "line-height: auto;";
//
//            classElements.get(i).attr("style", s);
//
//            String ifStyleNull = "<div class=\"p\"  style=\" " + "font-family:" + "; " + "font-size:" + "pt; " + "position: -webkit-sticky;"  + "color:"  + ";" + " \">\n" +
//                    "</div>";
//
//            if(finalData.get(i).getStyle() == null){
//                classElements.get(i).append(ifStyleNull);
//            }else {
//                String styleStrings = "<div class=\"p\"  style=\" " + "font-family:" + finalData.get(i).getStyle().getFontFamily()+ "; " + "font-size:" + finalData.get(i).getStyle().getFontSize() + "pt; " + "position: -webkit-sticky;" + finalData.get(i).getTextAlign() + "color:" + finalData.get(i).getStyle().getColor() + ";" + " \">\n" +
//                        finalData.get(dataIterate).getOwnText() +
//                        "</div>";
//                classElements.get(i).append(styleStrings);
//            }
//            dataIterate++;
//        }
//    }
//
//    public void removeAllClassRelements(Document document, Elements pElement){
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
//                                                 Map<Integer, StyleData> styleMap){
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
//                if (entryR.getValue().getxFirst() <= entryP.getValue().getxFirst()
//                        && entryR.getValue().getxSecond() >= entryP.getValue().getxSecond()) {
//
//                    if (leftAlignList.isEmpty()) {
//                        getYleftAlign = entryP.getValue().getyFirst().intValue();
//                        leftAlignList.add(entryP.getValue().getxFirst().intValue());
//                    } else if (!leftAlignList.isEmpty()) {
//                        if (entryP.getValue().getyFirst().intValue() != getYleftAlign) {
//                            leftAlignList.add(entryP.getValue().getxFirst().intValue());
//                            getYleftAlign = entryP.getValue().getyFirst().intValue();
//                        }
//                    }
//                    if (getYrightAlign == 0) {
//                        getYrightAlign = entryP.getValue().getyFirst().intValue();
//                        tempXposition = entryP.getValue().getxSecond().intValue();
//                    } else if (entryP.getValue().getyFirst().intValue() == getYrightAlign) {
//                        tempXposition = entryP.getValue().getxSecond().intValue();
//                    } else if (entryP.getValue().getyFirst().intValue() != getYrightAlign) {
//                        rightAlignList.add(tempXposition);
//                        getYrightAlign = entryP.getValue().getyFirst().intValue();
//                        tempXposition = entryP.getValue().getxSecond().intValue();
//                    }
//
//                    if (entryR.getValue().getyFirst() <= entryP.getValue().getyFirst() && entryR.getValue().getySecond() >= entryP.getValue().getySecond()) {
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
//    public Map<Integer, HTMLBlockRange> fillDataWithRElements(Elements rElement){
//        Map<Integer, HTMLBlockRange> rTempMap = new HashMap<>();
//        Integer i = 1;
//        for (Element e : rElement) {
//            PositionClass positionClass = parseRHTMLClassData(e.attr("style"));
//            if (!positionClass.getTop().equals("0.0")) {
//                rTempMap.put(i, fillBlockDataRange(positionClass));
//                i++;
//            }
//        }
//        return rTempMap;
//    }
//
//    public Map<Integer, HTMLBlockRange> fillDataWithPElements(Elements pElement){
//        Map<Integer, HTMLBlockRange> pTempMap = new LinkedHashMap<>();
//        Integer i = 1;
//        for (Element e : pElement) {
//            pTempMap.put(i, fillBlockDataRange(parsePHTMLClassData(e.attr("style")), e.ownText()));
//            i++;
//        }
//        return pTempMap;
//    }
//
//    public Map<Integer, StyleData> findAllStyles(Document document, Elements pElement){
//        Map<Integer, StyleData> styleMap = new LinkedHashMap<>();
//        Integer styleIterator = 1;
//        for (Element e : pElement) {
//            styleMap.put(styleIterator, getStyle(e.attr("style")));
//            styleIterator++;
//        }
//        return styleMap;
//    }
//
//    public Elements findAllClassPElements(Document document){
//        Elements pElement = document.select("div.p");
//        return pElement;
//    }
//
//    public Elements findAllClassRElements(Document document){
//        Elements rElement = document.select("div.r");
//        return rElement;
//    }
//
//
//
//    public static void main(String[] args) {
//
//        Document document = null;
//
//        File input = new File("/Users/ab/Documents/Idea/TRANNING/pdf_to_html/src/main/java/destination/stick_2.html");
//
//        try {
//            document = Jsoup.parse(input, "UTF-8", "http://example.com/");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String title = document.title();
//        System.out.println(title);
//
//
//        Map<Integer, StyleData> styleMap = new LinkedHashMap<>(); // getAll styles
//        Map<Integer, HTMLBlockRange> pTempMap = new LinkedHashMap<>(); // new map
//        Map<Integer, HTMLBlockRange> rTempMap = new HashMap<>();
//
//        Integer styleIterator = 1;
//        Integer keyForRHTMLClass = 1;
//
//        Elements pElement = document.select("div.p"); // на даному етапі приходять всі елементи
//        Elements rElement = document.select("div.r");
//
//        System.out.println(pElement);
//
//
//        for (Element e : pElement) {
//            pTempMap.put(styleIterator, fillBlockDataRange(parsePHTMLClassData(e.attr("style")), e.ownText()));
//            styleMap.put(styleIterator, getStyle(e.attr("style")));
//            styleIterator++;
//        }
//
//
//
//        // show all styles
//        styleMap.forEach((k, v) -> System.out.println(k + " k " + v + " v "));
//        System.out.println("/////////////////////////////////");
//
//
//        for (Element e : rElement) {
//            PositionClass positionClass = parseRHTMLClassData(e.attr("style"));
//            if (!positionClass.getTop().equals("0.0")) {
//                rTempMap.put(keyForRHTMLClass, fillBlockDataRange(positionClass));
//                keyForRHTMLClass++;
//            }
//        }
//
//
//        pTempMap.forEach((k, v) -> System.out.println(k + " k " + v + " v "));
//        System.out.println("/////////////////////////////////");
//
//
//        //new data
////        Integer entryRKey = 1;
//        StringBuilder stringBuilder;
//
////        Map<Integer, String> finalData = new LinkedHashMap<>();
//        Map<Integer, FinalData> finalData = new LinkedHashMap<>();
//
//
////        int tempXposition = 0;
//
////        int entryRTemp = 0;
//
//
//
//
//        for (Map.Entry<Integer, HTMLBlockRange> entryR : rTempMap.entrySet()) {
//            stringBuilder = new StringBuilder(); // create sb
//            List<Integer> leftAlignList = new ArrayList<>(); // create list
//            List<Integer> rightAlignList = new ArrayList<>(); // create list
//
//            int getYleftAlign = 0;
//            int getYrightAlign = 0;
//            int tempXposition = 0;
//
//
//            StyleData getRStyle = null;
//
//            for (Map.Entry<Integer, HTMLBlockRange> entryP : pTempMap.entrySet()) { // new entryMap
//
//
//
//
//                if (entryR.getValue().getxFirst() <= entryP.getValue().getxFirst()
//                        && entryR.getValue().getxSecond() >= entryP.getValue().getxSecond()) { // перевірка по x
//                    // якщо Х в цих межах тоді перевіряжмо умови У
//                    // дізнаємось х координати кожного внутрішнього блоку і сетаєм в ліст
//
//
//
//
//                    if (leftAlignList.isEmpty()) {
//                        getYleftAlign = entryP.getValue().getyFirst().intValue(); // беремо y першого елемента
//                        leftAlignList.add(entryP.getValue().getxFirst().intValue());
//                    } else if (!leftAlignList.isEmpty()) { // якщо наш ліст не пустий тоді виконати наступне
//                        if (entryP.getValue().getyFirst().intValue() != getYleftAlign) { // значення y однакові,  якщо не однакові (!=) тоді ->
//                            leftAlignList.add(entryP.getValue().getxFirst().intValue());
//                            getYleftAlign = entryP.getValue().getyFirst().intValue();
//                        }
//                    }
//
//
//                    if (getYrightAlign == 0) {
////                        styleMap.get()
//
//                        getYrightAlign = entryP.getValue().getyFirst().intValue(); // сетим значення y при першому входженні
//                        tempXposition = entryP.getValue().getxSecond().intValue();
//                    } else if (entryP.getValue().getyFirst().intValue() == getYrightAlign) { // якщо y однакові тоді
//                        // записати в тимчасову змінну даний y
//                        tempXposition = entryP.getValue().getxSecond().intValue(); // записати в тимчасову змінну х
//                    } else if (entryP.getValue().getyFirst().intValue() != getYrightAlign) { // якщо у змінився тоді
//                        rightAlignList.add(tempXposition); // записати значення попереднього
//                        getYrightAlign = entryP.getValue().getyFirst().intValue();
//                        tempXposition = entryP.getValue().getxSecond().intValue();
//                    }
//
//
//                    if (entryR.getValue().getyFirst() <= entryP.getValue().getyFirst() && entryR.getValue().getySecond() >= entryP.getValue().getySecond()) {
//                        getRStyle = styleMap.get(entryP.getKey());
//                        stringBuilder.append(entryP.getValue().getOwnText() + " ");  // new stringBuffer
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
//            System.out.println("countLeftAlignList " + countLeftAlignList);
//            System.out.println("countRightAlignList " + countRightAlignList);
//
//            System.out.println();
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
////            entryRKey++;
//
//        }
//
//        System.out.println("/////////////////////////////////");
//        finalData.forEach((k, v) -> System.out.println(k + " " + v.getOwnText() + " " + v.getTextAlign() +" "+v.getStyle()));
//        System.out.println("/////////////////////////////////");
//
//        /////////////// remove 1-14 p element
//        Elements elements = document.getAllElements();
////        Integer idElement = 1;  // remove 1-14 p element
//        Integer idElement = 0; // remove all elements
//        for (Element element : elements) {
//            String idToDelete = "p" + idElement.toString();
//            if (idElement < pElement.size()) {
//                if (element.getElementById(idToDelete) != null) {
//                    element.getElementById(idToDelete.toString()).remove();
//                    idElement++;
//                }
//            }
//        }
//
//        //get element by class
//        // add data for element
//        Integer dataIterate = 1;
//        Elements classElements = document.getElementsByClass("r");
//        for (int i = 1; i < classElements.size(); i++) {
//
//            String getClassElementData = classElements.get(i).toString();
//            String s = getClassElementData.replaceAll("<|div|class|=|\"r\"|style|\"|>|&nbsp;|/|\n", "");
//            s += "line-height: auto;";
//
//            classElements.get(i).attr("style", s);
//
////            System.out.println(classElements.get(i));
//
//
//            String ifStyleNull = "<div class=\"p\"  style=\" " + "font-family:" + "; " + "font-size:" + "pt; " + "position: -webkit-sticky;"  + "color:"  + ";" + " \">\n" +
//                    "</div>";
//
//
//
//            if(finalData.get(i).getStyle() == null){
//                classElements.get(i).append(ifStyleNull);
//            }else {
//                String styleStrings = "<div class=\"p\"  style=\" " + "font-family:" + finalData.get(i).getStyle().getFontFamily()+ "; " + "font-size:" + finalData.get(i).getStyle().getFontSize() + "pt; " + "position: -webkit-sticky;" + finalData.get(i).getTextAlign() + "color:" + finalData.get(i).getStyle().getColor() + ";" + " \">\n" +
//                        finalData.get(dataIterate).getOwnText() +
//                        "</div>";
//                classElements.get(i).append(styleStrings);
//            }
//            dataIterate++;
//        }
//
////         output file
//        File output = new File("/Users/ab/Documents/Idea/TRANNING/pdf_to_html/src/main/java/destination/output.html");
//        try {
//            FileUtils.writeStringToFile(output, document.outerHtml(), "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public static FinalData fillFinalData(String ownText, String textAlign, StyleData getStyle) {
//        FinalData finalData = new FinalData();
//        finalData.setOwnText(ownText);
//        finalData.setTextAlign(textAlign);
//        finalData.setStyle(getStyle);
//        return finalData;
//    }
//
//    public static HTMLBlockRange fillBlockDataRange(PositionClass positionClass, String ownText) {
//        HTMLBlockRange htmlBlockRange = new HTMLBlockRange();
//
//        htmlBlockRange.setOwnText(ownText); // set own text
//
//        //x
//        htmlBlockRange.setxFirst(Double.parseDouble(positionClass.getLeft()));
//        htmlBlockRange.setxSecond(Double.parseDouble(positionClass.getLeft()) + Double.parseDouble(positionClass.getWidth()));
//
//        //y
//        htmlBlockRange.setyFirst(Double.parseDouble(positionClass.getTop()));
//        htmlBlockRange.setySecond(Double.parseDouble(positionClass.getTop()) + Double.parseDouble(positionClass.getHeight()));
//
//        return htmlBlockRange;
//    }
//
//    public static HTMLBlockRange fillBlockDataRange(PositionClass positionClass) { // overload method
//        HTMLBlockRange htmlBlockRange = new HTMLBlockRange();
//
//        //x
//        htmlBlockRange.setxFirst(Double.parseDouble(positionClass.getLeft()));
//        htmlBlockRange.setxSecond(Double.parseDouble(positionClass.getLeft()) + Double.parseDouble(positionClass.getWidth()));
//
//        //y
//        htmlBlockRange.setyFirst(Double.parseDouble(positionClass.getTop()));
//        htmlBlockRange.setySecond(Double.parseDouble(positionClass.getTop()) + Double.parseDouble(positionClass.getHeight()));
//
//        return htmlBlockRange;
//    }
//
//    public static StyleData getStyle(String style) {
//        StyleData getStyle = new StyleData();
//        String s = style.replaceAll("pt", "");
//        String[] splitedText = s.split(":|;");
//        for (int i = 0; i < splitedText.length; i++) {
//            if (splitedText[i].equals("font-family")) {
//                getStyle.setFontFamily(splitedText[i + 1]);
//            } else if (splitedText[i].equals("font-size")) {
//                getStyle.setFontSize(splitedText[i + 1]);
//            } else if (splitedText[i].equals("color")) {
//                getStyle.setColor(splitedText[i + 1]);
//            }
//        }
//        return getStyle;
//    }
//
//
//    public static PositionClass parsePHTMLClassData(String style) {
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
//    public static PositionClass parseRHTMLClassData(String style) {
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
//}
