import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.fit.pdfdom.PDFDomTree;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PDFToHTMLConverter {


    public void pdfConverter(String sourceDir, String destinationDir) {
        try {
            File oldFile = new File(sourceDir);
            String fileName = oldFile.getName().replace(".pdf", "");
            if (oldFile.exists()) {
                File newFile = new File(destinationDir);
                if (!newFile.exists()) {
                    newFile.mkdir();
                }
                int pageNumber = 1;
                PDDocument document = PDDocument.load(new File(sourceDir));
                PDPageTree allPages = document.getDocumentCatalog().getPages();
                for (int i = 0; i < document.getNumberOfPages(); i++) {
                    PDPage page = allPages.get(i);

                    PDDocument newDocument = new PDDocument();
                    newDocument.addPage(page);

                    newFile = new File(destinationDir + fileName + "_" + pageNumber + ".pdf");
                    newFile.createNewFile();

                    newDocument.save(newFile);
                    newDocument.close();

                    PDDocument pdf = PDDocument.load(newFile);
                    Writer output = new PrintWriter(destinationDir + fileName + "_" + pageNumber + ".html", "utf-8");
                    new PDFDomTree().writeText(pdf, output);
                    replaceHTMLCharacter(destinationDir + fileName + "_" + pageNumber + ".html");
                    //
                    String parserInput = destinationDir + fileName + "_" + pageNumber + ".html";
                    String parserOutput = destinationDir + fileName + "_" + pageNumber + "_" + ".html";

                    PDFParser pdfParser = new PDFParser(parserInput);
                    pdfParser.parseHTML(parserOutput);
                    //delete file
                    deleteOldHtmlFile(parserInput);
                    output.close();

                    pdf.close();
                    newFile.delete();
                    pageNumber++;
                }

            } else {
                System.err.println(fileName + " File not exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





//    public void pdfConverter(String sourceDir, String destinationDir) {
//        try {
//            JsoupParser parser = new JsoupParser();
//            File oldFile = new File(sourceDir);
//            String fileName = oldFile.getName().replace(".pdf", "");
//            if (oldFile.exists()) {
//                File newFile = new File(destinationDir);
//                if (!newFile.exists()) {
//                    newFile.mkdir();
//                }
//                int pageNumber = 1;
//                PDDocument document = PDDocument.load(new File(sourceDir));
//                PDPageTree allPages = document.getDocumentCatalog().getPages();
//                for (int i = 0; i < document.getNumberOfPages(); i++) {
//                    PDPage page = allPages.get(i);
//
//                    PDDocument newDocument = new PDDocument();
//                    newDocument.addPage(page);
//
//                    newFile = new File(destinationDir + fileName + "_" + pageNumber + ".pdf");
//                    newFile.createNewFile();
//
//                    newDocument.save(newFile);
//                    newDocument.close();
//
//                    PDDocument pdf = PDDocument.load(newFile);
//                    Writer output = new PrintWriter(destinationDir + fileName + "_" + pageNumber + ".html", "utf-8");
//                    new PDFDomTree().writeText(pdf, output);
//                    replaceHTMLCharacter(destinationDir + fileName + "_" + pageNumber + ".html");
//                    //
//                    String parserInput = destinationDir + fileName + "_" + pageNumber + ".html";
//                    String parserOutput = destinationDir + fileName + "_" + pageNumber + "_" + ".html";
//                    parser.parseHTML(parserInput, parserOutput);
//                    //delete file
////                    deleteOldHtmlFile(parserInput);
//                    output.close();
//
//                    pdf.close();
//                    newFile.delete();
//                    pageNumber++;
//                }
//
//            } else {
//                System.err.println(fileName + " File not exists");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    public void deleteOldHtmlFile(String sourceDir) {
        File file = new File(sourceDir);
        file.delete();
    }

    public static void parsePdf(String pdf, String txt) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        PrintWriter out = new PrintWriter(new FileOutputStream(txt));
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i,
                    new SimpleTextExtractionStrategy());
            out.println(strategy.getResultantText());
        }
        out.flush();
        out.close();
    }

    public void replaceHTMLCharacter(String pathToHtml) {

        Path path = Paths.get(pathToHtml);
        Charset charset = StandardCharsets.UTF_8;

        try {
            String content = new String(Files.readAllBytes(path), charset);
            content = content.replace(".p,.r{", ".p,.r{background-color:unset!important;");
            Files.write(path, content.getBytes(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
