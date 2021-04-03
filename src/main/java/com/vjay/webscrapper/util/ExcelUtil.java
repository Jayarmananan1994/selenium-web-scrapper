package com.vjay.webscrapper.util;

import com.vjay.webscrapper.model.BuildingDetail;
import com.vjay.webscrapper.model.Query;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {

    public static void writeToExcel(List<BuildingDetail> buildings, String fileName) throws IOException {
        String[] COLUMNs = {"Building Name", "Info", "Price", "Building Type"};
        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Customers");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.BLUE.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Row for Header
        Row headerRow = sheet.createRow(0);

        // Header
        for (int col = 0; col < COLUMNs.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(COLUMNs[col]);
            cell.setCellStyle(headerCellStyle);
        }

        // CellStyle for Age
        CellStyle ageCellStyle = workbook.createCellStyle();
        ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));

        int rowIdx = 1;
        for (BuildingDetail buildingDetail : buildings) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(buildingDetail.getPropertyTitle());
            row.createCell(1).setCellValue(buildingDetail.getHeading());
            row.createCell(2).setCellValue(buildingDetail.getPrice());
            row.createCell(3).setCellValue(buildingDetail.getBuildingType());

        }

        FileOutputStream fileOut = new FileOutputStream(fileName);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    public static List<Query> readExcel(String filePath) {
        try {
            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);

            Sheet sheet = workbook.getSheet("Customers");
            Iterator<Row> rows = sheet.iterator();

            List lstCustomers = new ArrayList();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if(rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Query query = new Query();

                int cellIndex = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    if(cellIndex==0) { // ID
                        query.setBudget(currentCell.getStringCellValue());
                    } else if(cellIndex==1) { // Name
                        query.setArea(currentCell.getStringCellValue());
                    } else if(cellIndex==2) { // Address
                        query.setType(currentCell.getStringCellValue());
                    }

                    cellIndex++;
                }

                lstCustomers.add(query);
            }

            // Close WorkBook
            workbook.close();

            return lstCustomers;
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }
}
