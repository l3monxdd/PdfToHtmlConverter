import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;


public class Main {
    public static void main(String[] args) {

//        try {
//            PDDocument pdf = PDDocument.load(new File("/Users/ab/Desktop/test.pdf"));
//            Writer output = new PrintWriter("/Users/ab/Desktop/test.html", "utf-8");
//            new PDFDomTree().writeText(pdf, output);
//            output.close();
//        } catch (IOException ex) {
//            System.err.println(ex.getMessage());
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        }

        //PDF to HTML Converter
        PDFToHTMLConverter pdfToHTMLConverter = new PDFToHTMLConverter();

        pdfToHTMLConverter.pdfConverter("./src/main/java/test.pdf",
                "./src/main/java/destination/");
        //

    }
}
