package io.github.aluneed;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.util.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int indentSpaces = Integer.parseInt(args[0]);
        String indexInputPath = args[1];
        String pdfInputPath = args[2];
        String pdfOutputPath = args[3] == null ? "." : args[3];

        try (FileReader fileReader = new FileReader(indexInputPath)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> indexTextLines = bufferedReader.lines().toList();
            if (indexTextLines.isEmpty()) {
                throw new RuntimeException("invalid index");
            }
            Integer pageStart = Integer.parseInt(
                    indexTextLines.get(0)
                            .replaceAll("pageStart", "")
                            .trim()
            );
            int difference = pageStart - 1;

            List<Bookmark> parsedBookmarks = indexTextLines.stream().skip(1)
                    .map(line -> {
                        Integer spaceNum = line.indexOf("-");
                        if (spaceNum < 0) {
                            return null;
                        }
                        String simpleLine = line.trim().replace("- ", "");
                        int lastSpaceIndex = simpleLine.lastIndexOf(" ");
                        String pageNumberString = simpleLine.substring(lastSpaceIndex + 1);
                        Integer pageNumber = pageNumberString.startsWith("[") ?
                                Integer.parseInt(pageNumberString.replace("[", "").replace("]", "")) :
                                (Integer.parseInt(pageNumberString) + difference);
                        Bookmark bookmark = new Bookmark();
                        bookmark.name = simpleLine.substring(0, lastSpaceIndex);
                        bookmark.pdfPageNumber = pageNumber;
                        bookmark.depth = spaceNum / indentSpaces;
                        return bookmark;
                    })
                    .filter(Objects::nonNull)
                    .toList();

            List<Bookmark> bookmarkTree = Bookmark.toTree(parsedBookmarks);

            PdfReader pdfReader = new PdfReader(pdfInputPath);

            FileOutputStream pdfOutputStream = new FileOutputStream(pdfOutputPath);
            Document document = new Document();
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
//            rootOutline.setOpen(false);

            writeBookmark(pdfCopy, bookmarkTree, rootOutline);

            document.close();

        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeBookmark(final PdfCopy pdfCopy, List<Bookmark> bookmarkTree, PdfOutline outlineNode) {
        //https://api.itextpdf.com/iText5/java/5.5.13/com/itextpdf/text/pdf/PdfDestination.html
        PdfDestination pdfDestination = new PdfDestination(PdfDestination.FIT);

        for (Bookmark bookmark : bookmarkTree) {
            PdfAction pdfAction = PdfAction.gotoLocalPage(bookmark.pdfPageNumber, pdfDestination, pdfCopy);
            PdfOutline subNode = new PdfOutline(outlineNode, pdfAction, bookmark.name, false);
//            pdfOutline.setDestinationPage();

            if (bookmark.kids != null) {
                writeBookmark(pdfCopy, bookmark.kids, subNode);
            }
        }
    }

}

class Bookmark {
    String name;
    int pdfPageNumber;
    int depth;
    List<Bookmark> kids;

    public static List<Bookmark> toTree(List<Bookmark> flatOrederedList) {
        Bookmark root = new Bookmark();
        root.kids = new ArrayList<>();
        root.depth = -1;
        root.name = "root";
        addNode(root, null, flatOrederedList, 0);
        return root.kids;
    }

    public static Integer addNode(Bookmark currentRoot, String namePrefix, List<Bookmark> list, Integer index) {
        Integer prefixCount = 0;
        while (index < list.size()) {
            Bookmark node = list.get(index);
            String prefix = namePrefix == null ? prefixCount.toString() : namePrefix + "." + prefixCount;

            if (currentRoot.depth == node.depth - 1) {  //simple direct child node
                if (currentRoot.kids == null) {
                    currentRoot.kids = new ArrayList<>();
                }
                currentRoot.kids.add(node);
                if (node.depth > 0) {
                    prefixCount++;
                    prefix = namePrefix == null ? prefixCount.toString() : namePrefix + "." + prefixCount;
                    node.name = prefix + " " + node.name;
                }
                if (node.depth == 0 && list.get(Math.min(index + 1, list.size() - 1)).depth == 1) {
                    prefixCount++;
                }
                index++;
            } else if (currentRoot.depth < node.depth - 1) {  //dive into next child root
                Bookmark nextRoot = currentRoot.kids.getLast();
                index = addNode(nextRoot, prefix, list, index);
            } else if (currentRoot.depth >= node.depth) {  //escape from nested subtree
                return index;
            }
        }
        return index;
    }

    @Override
    public String toString() {
        return this.name;
    }

}