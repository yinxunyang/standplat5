package com.bestvike.standplat.support;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelUtil {
    public static void deleteFileDirectory(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                deleteFileDirectory(children[i].getPath());
            }
        }
        dir.delete();
    }
    public static String NullToEmpty(String change) {
        if (StringUtils.isEmpty(change)) {
            return "";
        } else {
            return change;
        }
    }
    public static void exportTravelmyDataToExcel(String[] header, List<String[]> datas, String path, String fileName) {
        File file = new File(path);
        //如果文件夹不存在
        if(!file.exists()) {
            //创建文件夹
            file.mkdirs();
        }
        file = new File(path+"/"+fileName);
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(100);
        Sheet sheet = sxssfWorkbook.createSheet();

        sheet.setColumnWidth(0, 256*18+184);
        sheet.setColumnWidth(1, 256*18+184);
        sheet.setColumnWidth(2, 256*18+184);
        sheet.setColumnWidth(3, 256*18+184);
        sheet.setColumnWidth(4, 256*18+184);
        sheet.setColumnWidth(5, 256*18+184);
        sheet.setColumnWidth(6, 256*18+184);
        sheet.setColumnWidth(7, 256*18+184);
        sheet.setColumnWidth(8, 256*18+184);
        sheet.setColumnWidth(9, 256*18+184);
        sheet.setColumnWidth(10, 256*18+184);
        sheet.setColumnWidth(11, 256*18+184);
        sheet.setColumnWidth(12, 256*18+184);
        sheet.setColumnWidth(13, 256*18+184);
        sheet.setColumnWidth(14, 256*18+184);
        sheet.setColumnWidth(15, 256*18+184);
        sheet.setColumnWidth(16, 256*18+184);

        //创建head行
        Row headRow = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            Cell cell = headRow.createCell(i);
            cell.setCellValue(header[i]);
        }
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                //从第二行开始遍历创建行
                Row row = sheet.createRow(i + 1);
                String[] d = datas.get(i);
                for (int j = 0; j < d.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(d[j]);
                }
            }
        }
        try {
            OutputStream outputStream = new FileOutputStream(file);
            sxssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sxssfWorkbook.dispose();
        }
    }
    public static void exportTravelAllEmpDataToExcel(String[] header, List<String[]> datas, String path, String fileName) {
        File file = new File(path);
        //如果文件夹不存在
        if(!file.exists()) {
            //创建文件夹
            file.mkdirs();
        }
        file = new File(path+"/"+fileName+".xls");
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(100);
        Sheet sheet = sxssfWorkbook.createSheet();
        CellStyle cellStyle = sxssfWorkbook.createCellStyle();
        sheet.setColumnWidth(0, 256*5+184);
        sheet.setColumnWidth(1, 256*12+184);
        sheet.setColumnWidth(2, 256*18+184);
        sheet.setColumnWidth(3, 256*10+184);
        sheet.setColumnWidth(4, 256*10+184);
        sheet.setColumnWidth(5, 256*10+184);
        sheet.setColumnWidth(6, 256*8+184);
        sheet.setColumnWidth(7, 256*18+184);
        sheet.setColumnWidth(8, 256*18+184);
        sheet.setColumnWidth(9, 256*18+184);
        sheet.setColumnWidth(10, 256*18+184);
        sheet.setColumnWidth(11, 256*18+184);
        sheet.setColumnWidth(12, 256*18+184);
        sheet.setColumnWidth(13, 256*18+184);
        sheet.setColumnWidth(14, 256*18+184);
        sheet.setColumnWidth(15, 256*18+184);
        sheet.setColumnWidth(16, 256*18+184);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        //创建head行
        Row headRow = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            Cell cell = headRow.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(cellStyle);
        }
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                //从第二行开始遍历创建行
                Row row = sheet.createRow(i + 1);
                String[] d = datas.get(i);
                for (int j = 0; j < d.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(d[j]);
                    cell.setCellStyle(cellStyle);
                }
            }
        }
        try {
            OutputStream outputStream = new FileOutputStream(file);
            sxssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sxssfWorkbook.dispose();
        }
    }
    public static void exportTravelDataToExcel(String[] header, List<String[]> datas, String path, String fileName) {
        String val = fileName;
        fileName = fileName +".xls";
        File file = new File(path);
        //如果文件夹不存在
        if(!file.exists()) {
            //创建文件夹
            file.mkdirs();
        }
        file = new File(path+"/"+fileName);
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(100);
        Sheet sheet = sxssfWorkbook.createSheet();

        sheet.setColumnWidth(0, 256*18+184);
        sheet.setColumnWidth(1, 256*18+184);
        sheet.setColumnWidth(2, 256*18+184);
        sheet.setColumnWidth(3, 256*18+184);
        sheet.setColumnWidth(4, 256*18+184);
        sheet.setColumnWidth(5, 256*18+184);
        sheet.setColumnWidth(6, 256*18+184);
        sheet.setColumnWidth(7, 256*18+184);
        sheet.setColumnWidth(8, 256*18+184);
        sheet.setColumnWidth(9, 256*18+184);
        sheet.setColumnWidth(10, 256*18+184);
        sheet.setColumnWidth(11, 256*18+184);
        sheet.setColumnWidth(12, 256*18+184);
        sheet.setColumnWidth(13, 256*18+184);
        sheet.setColumnWidth(14, 256*18+184);
        sheet.setColumnWidth(15, 256*18+184);
        sheet.setColumnWidth(16, 256*18+184);
        Row maxRow = sheet.createRow(0);
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0,1,0,datas.size());
        sheet.addMergedRegion(cellRangeAddress);
        maxRow.setHeight(Short.valueOf("500"));
        Cell cell1 = maxRow.createCell(0);
        cell1.setCellValue(val);
        //创建一个字体
        Font font=sxssfWorkbook.createFont();
        font.setFontHeightInPoints((short) 19);
        font.setFontName("宋体");
        font.setColor(HSSFColor.BLACK.index);
        font.setBoldweight((short) 700);
        CellStyle cellStyle = sxssfWorkbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cell1.setCellStyle(cellStyle);
        //创建head行
        CellStyle cellStyles = sxssfWorkbook.createCellStyle();
        cellStyles.setBorderBottom((short) 1);
        cellStyles.setBorderTop((short) 1);
        cellStyles.setBorderLeft((short) 1);
        cellStyles.setBorderRight((short) 1);
        Font fonts=sxssfWorkbook.createFont();
        fonts.setFontHeightInPoints((short) 16);
        fonts.setFontName("宋体");
        fonts.setFontHeightInPoints((short) 14);
        fonts.setColor(HSSFColor.BLACK.index);
        Row headRow02 = sheet.createRow(2);
        headRow02.setHeight((short) 450);
        Cell row02cell01 = headRow02.createCell(0);
        row02cell01.setCellValue("部门名称");

        cellStyles.setFont(fonts);
        row02cell01.setCellStyle(cellStyles);
        Row headRow03 = sheet.createRow(3);
        headRow03.setHeight((short) 450);
        Cell row03cell01 = headRow03.createCell(0);
        row03cell01.setCellValue("最终得分");
        row03cell01.setCellStyle(cellStyles);
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                String[] d = datas.get(i);
                Cell cell = headRow02.createCell(i+1);
                cell.setCellStyle(cellStyles);
                cell.setCellValue(d[0]);
                cell = headRow03.createCell(i+1);
                cell.setCellStyle(cellStyles);
                cell.setCellValue(d[1]);
            }
        }
        try {
            OutputStream outputStream = new FileOutputStream(file);
            sxssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sxssfWorkbook.dispose();
        }
    }
    public static void exportTravelEmpDataToExcel(String[] header, List<String[]> datas, String path, String fileName) {
        String val = fileName;
        fileName = fileName +".xls";
        File file = new File(path);
        //如果文件夹不存在
        if(!file.exists()) {
            //创建文件夹
            file.mkdirs();
        }
        file = new File(path+"/"+fileName);
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(100);
        Sheet sheet = sxssfWorkbook.createSheet();

        sheet.setColumnWidth(0, 256*6+184);
        sheet.setColumnWidth(1, 256*12+184);
        sheet.setColumnWidth(2, 256*18+184);
        sheet.setColumnWidth(3, 256*12+184);
        sheet.setColumnWidth(4, 256*10+184);
        sheet.setColumnWidth(5, 256*23+184);
        sheet.setColumnWidth(6, 256*18+184);
        sheet.setColumnWidth(7, 256*18+184);
        sheet.setColumnWidth(8, 256*18+184);
        sheet.setColumnWidth(9, 256*18+184);
        sheet.setColumnWidth(10, 256*18+184);
        sheet.setColumnWidth(11, 256*18+184);
        sheet.setColumnWidth(12, 256*18+184);
        sheet.setColumnWidth(13, 256*18+184);
        sheet.setColumnWidth(14, 256*18+184);
        sheet.setColumnWidth(15, 256*18+184);
        sheet.setColumnWidth(16, 256*18+184);
        Row maxRow = sheet.createRow(0);
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0,0,0,header.length-1);
        sheet.addMergedRegion(cellRangeAddress);
        maxRow.setHeight((short) 660);
        Cell cell1 = maxRow.createCell(0);
        //创建一个字体
        Font font=sxssfWorkbook.createFont();
        font.setFontHeightInPoints((short) 21);
        font.setFontName("宋体");
        font.setColor(HSSFColor.BLACK.index);
        font.setBoldweight((short)700);
        CellStyle cellStyle = sxssfWorkbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cell1.setCellStyle(cellStyle);
        cell1.setCellValue(val);
        //创建head行
        CellStyle cellStyless = sxssfWorkbook.createCellStyle();
        cellStyless.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyless.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyless.setBorderBottom((short) 1);
        cellStyless.setBorderTop((short) 1);
        cellStyless.setBorderLeft((short) 1);
        cellStyless.setBorderRight((short) 1);
        Font fonts =sxssfWorkbook.createFont();
        Row headRow02 = sheet.createRow(1);
        headRow02.setHeight((short) 660);
        fonts.setFontName("宋体");
        fonts.setFontHeightInPoints((short) 11);
        fonts.setBoldweight((short)700);
        fonts.setColor(HSSFColor.BLACK.index);
        cellStyless.setFont(fonts);
        for(int i=0;i<header.length;i++){
            Cell row02cell0x = headRow02.createCell(i);
            row02cell0x.setCellValue(header[i]);
            row02cell0x.setCellStyle(cellStyless);
        }
        Font fontsss =sxssfWorkbook.createFont();
        CellStyle cellStylesss = sxssfWorkbook.createCellStyle();
        fontsss.setFontName("宋体");
        fontsss.setFontHeightInPoints((short) 14);
        fontsss.setColor(HSSFColor.BLACK.index);
        cellStylesss.setBorderBottom((short) 1);
        cellStylesss.setBorderTop((short) 1);
        cellStylesss.setBorderLeft((short) 1);
        cellStylesss.setBorderRight((short) 1);
        fontsss.setFontHeightInPoints((short) 11);
        cellStylesss.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStylesss.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStylesss.setFont(fontsss);
        if (datas != null && datas.size() > 0) {
            for (int i=0;i<datas.size();i++) {
                String[] strings = datas.get(i);
                Row headRow0x = sheet.createRow(i + 2);
                headRow0x.setHeight((short)660);
                for (int j = 0; j < strings.length; j++) {
                    Cell row03cell01 = headRow0x.createCell(j);
                    if(j == strings.length-1){
                        CellStyle cellStylessss = sxssfWorkbook.createCellStyle();
                        Font fontssss =sxssfWorkbook.createFont();
                        cellStylessss.setBorderBottom((short) 1);
                        cellStylessss.setBorderTop((short) 1);
                        cellStylessss.setBorderLeft((short) 1);
                        cellStylessss.setBorderRight((short) 1);
                        fontssss.setFontHeightInPoints((short) 8);
                        fontssss.setFontName("宋体");
                        cellStylessss.setWrapText(true);
                        fontssss.setColor(HSSFColor.BLACK.index);
                        cellStylessss.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                        cellStylessss.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                        cellStylessss.setFont(fontssss);
                        row03cell01.setCellStyle(cellStylessss);
                    }else{
                        row03cell01.setCellStyle(cellStylesss);
                    }
                    row03cell01.setCellValue(strings[j]);
                }
            }
        }
        Font fontss=sxssfWorkbook.createFont();
        fontss.setFontHeightInPoints((short)14);
        fontss.setFontName("宋体");
        fontss.setBoldweight((short) 0.8);
        Row lastRow = sheet.createRow(datas.size()+2);
        CellRangeAddress cellRangeAddresss = new CellRangeAddress(datas.size()+2,datas.size()+2,0,header.length-1);
        sheet.addMergedRegion(cellRangeAddresss);
        CellStyle cellStyles = sxssfWorkbook.createCellStyle();
        cellStyles.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyles.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        cellStyles.setFont(fontss);
        cellStyles.setWrapText(true);
        lastRow.setHeight((short)2720);
        String remark = "注：1.部门内全部员工（不含休产假等员工）的平均分不得高于部门绩效评价最终得分；\n" +
                "2.部门内员工评分以0.5分为级差，不得完全相同，部门内员工的最高得分不得超过部门最终得分3分；\n" +
                "3.公司绩效考评得分直接体现于员工最终评分；\n" +
                "4.请在备注中添加得分理由。";
        Cell cellx = lastRow.createCell(0);
        cellx.setCellValue(remark);
        cellx.setCellStyle(cellStyles);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            sxssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sxssfWorkbook.dispose();
        }
    }
    public static void 搜索(){

    }
    /**
     * 导出Excel
     * @param sheetName sheet名称
     * @param title 标题
     * @param values 内容
     * @param wb HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName,String []title,String [][]values, HSSFWorkbook wb){
 
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if(wb == null){
            wb = new HSSFWorkbook();
        }
 
        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
 
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);
 
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
 
        //声明列对象
        HSSFCell cell = null;
 
        //创建标题
        for(int i=0;i<title.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
 
        //创建内容
        for(int i=0;i<values.length;i++){
            row = sheet.createRow(i + 1);
            for(int j=0;j<values[i].length;j++){
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }
}