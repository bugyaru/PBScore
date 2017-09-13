/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.pbcore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author admin
 */
public class PBSaveLoadExcel {

    private Workbook wb;
    private String SheetName;
    private List<List<Object>> data = new ArrayList<List<Object>>();

    public PBSaveLoadExcel(File file) throws FileNotFoundException, IOException, InvalidFormatException {
        InputStream in = new FileInputStream(file);
        String[] flarr = file.getName().split("\\.");
        //System.out.println(flarr[flarr.length-1]);
        if (flarr[flarr.length - 1].equals("xls")) {
            wb = new HSSFWorkbook(in);
        } else {
            wb = new XSSFWorkbook(in);
        }
        in.close();
        Sheet element = wb.getSheetAt(0);
        SheetName = element.getSheetName();
        int rowsCount = element.getLastRowNum();
        for (int i = 1; i <= rowsCount; i++) {
            Row row = element.getRow(i);
            int colCounts = row.getLastCellNum();
            System.out.println("Total Number of Cols: " + colCounts);
            List<Object> rowdata = new ArrayList<Object>();
            for (int j = 0; j < 10; j++) {
                Cell cell = row.getCell(j);
                try {
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        //System.out.println("[" + i + "," + j + "]=" + cell.getStringCellValue());
                        rowdata.add(cell.getStringCellValue());
                    } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            rowdata.add(cell.getDateCellValue());
                        } else {
                            rowdata.add(cell.getNumericCellValue());
                        }
                        //System.out.println("[" + i + "," + j + "]=" + cell.getNumericCellValue());
                    } else if (cell.getCellTypeEnum() == CellType._NONE) {
                        //System.out.println("[" + i + "," + j + "]=");
                        rowdata.add("");
                    }
                } catch (NullPointerException e) {
                    rowdata.add(0);
                }
            }
            data.add(rowdata);
        }

    }

    public String getSheetName() {
        return SheetName;
    }

    public List<List<Object>> getData() {
        return data;
    }

}
