package io.github.aluneed;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        String indexInputPath = "/Users/aluneed/projects/pdf-index-editor/java并发编程实战目录.md";
        String pdfInputPath = "/Users/aluneed/projects/pdf-index-editor/java并发编程实战.pdf";
        String pdfOutputPath = "/Users/aluneed/projects/pdf-index-editor/java并发编程实战-indexed.pdf";
        int spaces = 2;

        try (FileReader fileReader = new FileReader(indexInputPath)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> indexTextLines = bufferedReader.lines().toList();
            List<Bookmark> bookmarks = indexTextLines.stream()
                    .


            PdfReader pdfReader = new PdfReader(pdfInputPath);


            FileOutputStream pdfOutputStream = new FileOutputStream(pdfOutputPath);
            Document document = new Document();
//            PdfWriter pdfWriter  = PdfWriter.getInstance(document, pdfOutputStream);
            //https://api.itextpdf.com/iText5/java/5.5.13/com/itextpdf/text/pdf/PdfCopy.html
            PdfCopy pdfCopy = new PdfCopy(document, pdfOutputStream);
            pdfCopy.setViewerPreferences(PdfWriter.PageModeUseOutlines);

            document.open();

            int maxPageNumber = pdfReader.getNumberOfPages();
            for (int pageNumber = 1; pageNumber <= maxPageNumber; pageNumber++) {
                PdfImportedPage page = pdfCopy.getImportedPage(pdfReader, pageNumber);
//                PdfCopy.PageStamp stamp = pdfCopy.createPageStamp(page);
                pdfCopy.addPage(page);
            }

            pdfCopy.freeReader(pdfReader);

            //https://api.itextpdf.com/iText5/java/5.5.13/com/itextpdf/text/pdf/PdfOutline.html
            PdfOutline rootOutline = pdfCopy.getRootOutline();
            rootOutline.setOpen(false);

            //https://api.itextpdf.com/iText5/java/5.5.13/com/itextpdf/text/pdf/PdfDestination.html
            PdfDestination pdfDestination = new PdfDestination(PdfDestination.FIT);
//            PdfDestination pdfDestination = new PdfDestination(PdfDestination.XYZ, 0, 728, 0);
            PdfAction pdfAction = PdfAction.gotoLocalPage(10, pdfDestination, pdfCopy);
            PdfOutline pdfOutline = new PdfOutline(rootOutline, pdfAction, "test", false);

//            var list = SimpleBookmark.getBookmark(pdfReader);
//            HashMap<String, Object> bookmark = new HashMap<>();
//            bookmark.put("Action", "GoTo");
//            bookmark.put("Title", "test");
//            bookmark.put("Page", "10");
//            pdfCopy.setOutlines(list);

            document.close();

        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeToPdf(int depth, String textLine, PdfCopy pdfCopy, PdfReader pdfReader, PdfOutline pdfOutline) {
        textLine.toString();

        PdfAction pdfAction = PdfAction.gotoLocalPage(pageNum, new PdfDestination(PdfDestination.FIT), pdfCopy);

        String


    }


}

class Bookmark {
    List<Bookmark> kids;
    String name;
    int pdfPageNumber;
    int depth;
}