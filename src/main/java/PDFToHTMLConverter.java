import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.fit.pdfdom.PDFDomTree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PDFToHTMLConverter {

    public void replaceHTMLCharacter(String pathToHtml){

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

    public void pdfConverter(String sourceDir, String destinationDir){
        try {

//            String sourceDir = "./src/main/java/12.pdf";
//            String destinationDir = "./src/main/java/PDFCopy/";
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

}
