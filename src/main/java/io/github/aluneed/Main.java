package io.github.aluneed;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;

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
            bufferedReader.lines()
                    .toList();

            PdfReader pdfReader = new PdfReader(pdfInputPath);
            List<HashMap<String, Object>> bookmarkList = SimpleBookmark.getBookmark(pdfReader);
            PdfDictionary catalog = pdfReader.getCatalog();
            PdfObject obj = PdfReader.getPdfObjectRelease(catalog.get(PdfName.OUTLINES));

            if (obj != null && obj.isDictionary()) {
                PdfDictionary outlines = (PdfDictionary)obj;
            }

            FileOutputStream pdfOutputStream = new FileOutputStream(pdfOutputPath);
            Document document = new Document();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}