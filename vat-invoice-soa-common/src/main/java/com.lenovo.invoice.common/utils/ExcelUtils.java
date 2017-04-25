package com.lenovo.invoice.common.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel工具类.
 * User: lijie
 * Date: 2015/8/16
 * Time: 11:44
 */
public abstract class ExcelUtils {

    /**
     * 写入excel
     *
     * @param workbook    HSSFWorkbook
     * @param excelHeader 标题行
     * @param datas       数据集合
     * @return 总行数
     */
    public static int write2Excel(HSSFWorkbook workbook, String[] excelHeader, List<Object[]> datas) {
        int allRow = 0;
        if (excelHeader == null || excelHeader.length <= 0) {
            return allRow;
        }
        HSSFSheet sheet = workbook.createSheet("sheet1");
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);    //设置标题为绿色
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(style);
            //     sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, 3000);
        }
        allRow++;
        if (datas == null || datas.size() <= 0) {
            return allRow;
        }
        for (int i = 0; i < datas.size(); i++) {
            Object[] data = datas.get(i);
            if (data == null || data.length <= 0) {
                continue;
            }
            row = sheet.createRow(allRow++);
            for (int j = 0; j < data.length; j++) {
                row.createCell(j).setCellValue(String.valueOf(data[j] == null ? "" : data[j]));
            }
        }
        return allRow;
    }

    /**
     * 对外提供读取excel 的方法
     * */
    public static List<List<Object>> readExcel(File file) throws IOException {
        String fileName = file.getName();
        String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName
                .substring(fileName.lastIndexOf(".") + 1);
        if ("xls".equals(extension)) {
            return read2003Excel(file);
        }
//        else if ("xlsx".equals(extension)) {
//            return read2007Excel(file);
//        }
        else {
            throw new IOException("不支持的文件类型");
        }
    }
    /**
     * 读取 office 2003 excel
     * @throws IOException
     * @throws
     */
    private static List<List<Object>> read2003Excel(File file)
            throws IOException {
        List<List<Object>> list = new LinkedList<List<Object>>();
        HSSFWorkbook hwb = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet sheet = hwb.getSheetAt(0);
        Object value = null;
        HSSFRow row = null;
        HSSFCell cell = null;
        int counter = 0;
        for (int i = sheet.getFirstRowNum(); counter < sheet
                .getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            } else {
                counter++;
            }
            List<Object> linked = new LinkedList<Object>();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                DecimalFormat df = new DecimalFormat("0");// 格式化 number String 字符
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
                switch (cell.getCellType()) {
                    case XSSFCell.CELL_TYPE_STRING:
                        System.out.println(i + "行" + j + " 列 is String type");
                        value = cell.getStringCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_NUMERIC:
                        System.out.println(i + "行" + j
                                + " 列 is Number type ; DateFormt:"
                                + cell.getCellStyle().getDataFormatString());
                        if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                            value = df.format(cell.getNumericCellValue());
                        } else if ("General".equals(cell.getCellStyle()
                                .getDataFormatString())) {
                            value = nf.format(cell.getNumericCellValue());
                        } else {
                            value = sdf.format(HSSFDateUtil.getJavaDate(cell
                                    .getNumericCellValue()));
                        }
                        break;
                    case XSSFCell.CELL_TYPE_BOOLEAN:
                        System.out.println(i + "行" + j + " 列 is Boolean type");
                        value = cell.getBooleanCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_BLANK:
                        System.out.println(i + "行" + j + " 列 is Blank type");
                        value = "";
                        break;
                    default:
                        System.out.println(i + "行" + j + " 列 is default type");
                        value = cell.toString();
                }
                if (value == null || "".equals(value)) {
                    continue;
                }
                linked.add(value);
            }
            list.add(linked);
        }
        return list;
    }

    /**
     * 大转盘读取促销码
     * @param in
     * @return
     * @throws IOException
     */
    public static List<List<Object>> read2007Excel_common(InputStream in)throws IOException {
        List<List<Object>> list = new LinkedList<List<Object>>();
        // 构造 XSSFWorkbook 对象，strPath 传入文件路径
        XSSFWorkbook xwb = new XSSFWorkbook(in);
        // 读取第一章表格内容
        XSSFSheet sheet = xwb.getSheetAt(0);
        Object value = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        int counter = 0;
        for (int i = sheet.getFirstRowNum(); counter < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            } else {
                counter++;
            }
            List<Object> linked = new LinkedList<Object>();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                DecimalFormat df = new DecimalFormat("0");// 格式化 number String 字符
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0");// 格式化数字
                switch (cell.getCellType()) {
                    case XSSFCell.CELL_TYPE_STRING:
                        value = cell.getStringCellValue();
//                        System.out.println(i + "行" + j + " 列 is String type  value="+value);
                        break;
                    case XSSFCell.CELL_TYPE_NUMERIC:
                        if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                            value = df.format(cell.getNumericCellValue());
                        } else if ("General".equals(cell.getCellStyle()
                                .getDataFormatString())) {
                            value = nf.format(cell.getNumericCellValue());
//                            value = cell.getNumericCellValue();
                        } else {
                            value = sdf.format(HSSFDateUtil.getJavaDate(cell
                                    .getNumericCellValue()));
                        }
                        break;
                    case XSSFCell.CELL_TYPE_BOOLEAN:
                        value = cell.getBooleanCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_BLANK:
                        value = "";
                        break;
                    default:
                        value = cell.toString();
                }
                if (value == null || "".equals(value)) {
                    continue;
                }
                linked.add(value);
            }
            list.add(linked);
        }
        return list;
    }


    /**
     * 对导入的用户发放优惠券
     * @param in
     * @return
     * @throws IOException
     */
    public static List<List<Object>> read2007Excel_Membercommon(InputStream in)throws IOException {
        List<List<Object>> list = new LinkedList<List<Object>>();
        // 构造 XSSFWorkbook 对象，strPath 传入文件路径
        XSSFWorkbook xwb = new XSSFWorkbook(in);
        // 读取第一章表格内容
        XSSFSheet sheet = xwb.getSheetAt(0);
        Object value = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        int counter = 0;
        for (int i = sheet.getFirstRowNum(); counter < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            } else {
                counter++;
            }
            List<Object> linked = new LinkedList<Object>();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                DecimalFormat df = new DecimalFormat("0");// 格式化 number String 字符
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0");// 格式化数字
                switch (cell.getCellType()) {
                    case XSSFCell.CELL_TYPE_STRING:
                        value = cell.getStringCellValue();
//                        System.out.println(i + "行" + j + " 列 is String type  value="+value);
                        break;
                    case XSSFCell.CELL_TYPE_NUMERIC:
                        if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                            value = df.format(cell.getNumericCellValue());
                        } else if ("General".equals(cell.getCellStyle()
                                .getDataFormatString())) {
                            value = nf.format(cell.getNumericCellValue());
//                            value = cell.getNumericCellValue();
                        } else {
                            value = sdf.format(HSSFDateUtil.getJavaDate(cell
                                    .getNumericCellValue()));
                        }
                        break;
                    case XSSFCell.CELL_TYPE_BOOLEAN:
                        value = cell.getBooleanCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_BLANK:
                        value = "";
                        break;
                    default:
                        value = cell.toString();
                }
                if (value == null || "".equals(value)) {
                    continue;
                }
                linked.add(value);
            }
            if (linked.size()==0){
                continue;
            }
            list.add(linked);
        }
        return list;
    }


    public static String getStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        String cellvalue = "";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    cellvalue = format.format(cell.getDateCellValue());
                } else {
                    DecimalFormat df = new DecimalFormat("#.##");
                    String strValue = df.format(cell.getNumericCellValue());
                    if (strValue.endsWith(".0")) {
                        cellvalue = strValue.substring(0, strValue.length() - 2);
                    } else {
                        cellvalue = strValue;
                    }
                }
                break;
            case HSSFCell.CELL_TYPE_STRING:
                cellvalue = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                cellvalue = cell.getBooleanCellValue() + "";
                break;
            default:
                cellvalue = "";
                break;
        }

        if ("null".equalsIgnoreCase(cellvalue)){
            cellvalue = "";
        }
        return cellvalue.trim();
    }

    public static void main(String[] args) {
        try {
//            System.out.println(read2007Excel(new File("D:\\promocode.xlsx")));
            InputStream inputStream = new FileInputStream(new File("D:\\11月1日码.xlsx"));
            System.out.println(read2007Excel_common(inputStream));
            // readExcel(new File("D:\\test.xls"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
