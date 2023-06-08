package Main.Selenium;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
//
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Excel {
    public static void CreateSheet(List<Post> posts, String fileName) {
        HSSFWorkbook workBook = new HSSFWorkbook();
        CreationHelper createHelper = workBook.getCreationHelper();
        // Custom colors.
        short[] evenColor = {19,79,92};
        short[] oddColor = {90,110,135};
        
        short indexEvenColor = getOrAddColorIndex(evenColor,workBook);
        short indexOddColor = getOrAddColorIndex(oddColor,workBook);
        // Create titles.
        List<String> columns = new ArrayList<>();
        columns.add("Username:");
        columns.add("Full name:");
        columns.add("Content:");
        columns.add("Time posted:");
        columns.add("Image source:");
        columns.add("Post Source:");
        Sheet sheet = workBook.createSheet("Contacts");
        sheet.setDefaultColumnWidth(30);
        Font headFont = workBook.createFont();
        headFont.setBold(true);
        headFont.setFontHeightInPoints((short) 12);
        headFont.setColor(IndexedColors.BLUE_GREY.getIndex());
        //
        CellStyle headerCellStyle = workBook.createCellStyle();
        headerCellStyle.setFont(headFont);
        headerCellStyle.setShrinkToFit(true);
        headerCellStyle.setWrapText(true);
        //
        CellStyle contentStyle = workBook.createCellStyle();
        contentStyle.setWrapText(true);
        //

        // Hyper font
        HSSFFont hlinkfont = workBook.createFont();
        hlinkfont.setUnderline(XSSFFont.U_SINGLE);
        hlinkfont.setColor(IndexedColors.WHITE1.getIndex());
        //Even
        CellStyle hyperEven = workBook.createCellStyle();
        hyperEven.setFont(hlinkfont);
        hyperEven.setFillForegroundColor(indexEvenColor);
        hyperEven.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //Odd
        CellStyle hyperOdd = workBook.createCellStyle();
        hyperOdd.setFont(hlinkfont);
        hyperOdd.setFillForegroundColor(indexOddColor);
        hyperOdd.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(50);
        // Create headers
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns.get(i));
            cell.setCellStyle(headerCellStyle);
        }


        Font defaultFont = workBook.createFont();
        defaultFont.setColor(IndexedColors.WHITE.index);


        CellStyle evenRow = workBook.createCellStyle();
        evenRow.setFont(defaultFont);
        evenRow.setFillForegroundColor(indexEvenColor);
        evenRow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        evenRow.setWrapText(true);



        CellStyle oddRow = workBook.createCellStyle();
        oddRow.setFillForegroundColor(indexOddColor);
        oddRow.setFont(defaultFont);
        oddRow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        oddRow.setWrapText(true);

        for (int i=1;i<posts.size();i++) {
            Row row = sheet.createRow(i);
            Cell userNameCell = row.createCell(0);
            Cell fullNameCell = row.createCell(1);
            Cell contentCell = row.createCell(2);
            Cell timePostedCell = row.createCell(3);
            Cell imageSrcCell = row.createCell(4);
            Cell postSourceCell = row.createCell(5);

            if(i%2==0) {
                userNameCell.setCellStyle(evenRow);
                fullNameCell.setCellStyle(evenRow);
                contentCell.setCellStyle(evenRow);
                timePostedCell.setCellStyle(evenRow);
                imageSrcCell.setCellStyle(evenRow);
                postSourceCell.setCellStyle(evenRow);

                HSSFHyperlink link = (HSSFHyperlink) createHelper.createHyperlink(HyperlinkType.URL);
                link.setAddress(posts.get(i).postSource);
                postSourceCell.setHyperlink(link);
                postSourceCell.setCellStyle(hyperEven);
            }

            else {
                userNameCell.setCellStyle(oddRow);
                fullNameCell.setCellStyle(oddRow);
                contentCell.setCellStyle(oddRow);
                timePostedCell.setCellStyle(oddRow);
                imageSrcCell.setCellStyle(oddRow);
                //postSourceCell.setCellStyle(oddRow);
                HSSFHyperlink link = (HSSFHyperlink) createHelper.createHyperlink(HyperlinkType.URL);
                link.setAddress(posts.get(i).postSource);
                postSourceCell.setHyperlink(link);
                postSourceCell.setCellStyle(hyperOdd);
            }


            userNameCell.setCellValue(posts.get(i).username);
            fullNameCell.setCellValue(posts.get(i).fullname);
            contentCell.setCellValue(posts.get(i).content);
            timePostedCell.setCellValue(posts.get(i).timePosted);
            if(posts.get(i).getImageSrc()!=null)
                imageSrcCell.setCellValue(posts.get(i).imageSrc);
            postSourceCell.setCellValue("OPEN IN THE APP.");
        }

        try {
            fileName = fileName.replaceAll("[^a-zA-Z0-9]", "").trim();
            System.out.println(fileName);
            File file = new File(fileName);
            String path = file.getAbsolutePath()+".xls";
            //String myFile = "C:\\Users\\omerd\\Desktop\\SKUL\\excelShit\\"+fileName+".xls";;
            FileOutputStream fileOutput = new FileOutputStream(path);
            workBook.write(fileOutput);
            fileOutput.close();
            System.out.println("file created successfully.");
        } catch (Exception e) {
            System.out.println("Fuck my life");
        }
        //Row row =ws.createRow(1);
    }



    private static short getOrAddColorIndex(short[] rgb, HSSFWorkbook wb) {
        HSSFPalette palette = wb.getCustomPalette();
        HSSFColor color = palette.findColor((byte) rgb[0], (byte) rgb[1], (byte) rgb[2]);
        if (color == null) {
            try {
                color = palette.addColor((byte) rgb[0], (byte) rgb[1], (byte) rgb[2]);
            } catch (RuntimeException e) {
                // Could not find free color index
                color = palette.findSimilarColor(rgb[0], rgb[1], rgb[2]);
            }
        }
        return color.getIndex();
    }


}

