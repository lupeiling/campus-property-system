package com.campus.property.controller;

import com.campus.property.entity.*;
import com.campus.property.service.CanteenService;
import com.campus.property.service.DormService;
import com.campus.property.service.MaterialService;
import com.campus.property.service.RepairService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private DormService dormService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private CanteenService canteenService;

    @GetMapping("/dorm-allocation/{format}")
    public void exportDormAllocation(@PathVariable String format, HttpServletResponse response) throws Exception {
        List<DormAllocation> list = dormService.listAllAllocations();
        String title = "宿舍入住统计表";
        String[] headers = {"ID", "用户ID", "房间ID", "床位号", "入住日期", "退宿日期", "状态"};
        if ("pdf".equalsIgnoreCase(format)) {
            exportPdf(response, title, headers, list, item -> new String[]{
                    String.valueOf(item.getId()),
                    String.valueOf(item.getUserId()),
                    String.valueOf(item.getRoomId()),
                    String.valueOf(item.getBedNo()),
                    item.getCheckInDate() != null ? item.getCheckInDate().toString() : "",
                    item.getCheckOutDate() != null ? item.getCheckOutDate().toString() : "",
                    item.getStatus() == 1 ? "在住" : "已退宿"
            });
        } else {
            exportExcel(response, title, headers, list, item -> new String[]{
                    String.valueOf(item.getId()),
                    String.valueOf(item.getUserId()),
                    String.valueOf(item.getRoomId()),
                    String.valueOf(item.getBedNo()),
                    item.getCheckInDate() != null ? item.getCheckInDate().toString() : "",
                    item.getCheckOutDate() != null ? item.getCheckOutDate().toString() : "",
                    item.getStatus() == 1 ? "在住" : "已退宿"
            });
        }
    }

    @GetMapping("/material/{format}")
    public void exportMaterial(@PathVariable String format, HttpServletResponse response) throws Exception {
        List<Material> list = materialService.listAllMaterials();
        String title = "物资库存清单";
        String[] headers = {"ID", "编号", "名称", "规格", "单位", "库存数量", "最低库存", "单价"};
        if ("pdf".equalsIgnoreCase(format)) {
            exportPdf(response, title, headers, list, item -> new String[]{
                    String.valueOf(item.getId()),
                    item.getMaterialNo() != null ? item.getMaterialNo() : "",
                    item.getMaterialName() != null ? item.getMaterialName() : "",
                    item.getSpecification() != null ? item.getSpecification() : "",
                    item.getUnit() != null ? item.getUnit() : "",
                    String.valueOf(item.getStockQuantity()),
                    String.valueOf(item.getMinQuantity()),
                    item.getUnitPrice() != null ? item.getUnitPrice().toString() : ""
            });
        } else {
            exportExcel(response, title, headers, list, item -> new String[]{
                    String.valueOf(item.getId()),
                    item.getMaterialNo() != null ? item.getMaterialNo() : "",
                    item.getMaterialName() != null ? item.getMaterialName() : "",
                    item.getSpecification() != null ? item.getSpecification() : "",
                    item.getUnit() != null ? item.getUnit() : "",
                    String.valueOf(item.getStockQuantity()),
                    String.valueOf(item.getMinQuantity()),
                    item.getUnitPrice() != null ? item.getUnitPrice().toString() : ""
            });
        }
    }

    @GetMapping("/canteen-consume/{format}")
    public void exportCanteenConsume(@PathVariable String format, HttpServletResponse response) throws Exception {
        List<CanteenConsume> list = canteenService.listAllConsumes();
        String title = "餐饮消费汇总表";
        String[] headers = {"ID", "用户ID", "食堂ID", "消费金额", "消费时间"};
        if ("pdf".equalsIgnoreCase(format)) {
            exportPdf(response, title, headers, list, item -> new String[]{
                    String.valueOf(item.getId()),
                    String.valueOf(item.getUserId()),
                    String.valueOf(item.getCanteenId()),
                    item.getAmount() != null ? item.getAmount().toString() : "",
                    item.getConsumeTime() != null ? item.getConsumeTime().toString() : ""
            });
        } else {
            exportExcel(response, title, headers, list, item -> new String[]{
                    String.valueOf(item.getId()),
                    String.valueOf(item.getUserId()),
                    String.valueOf(item.getCanteenId()),
                    item.getAmount() != null ? item.getAmount().toString() : "",
                    item.getConsumeTime() != null ? item.getConsumeTime().toString() : ""
            });
        }
    }

    private <T> void exportExcel(HttpServletResponse response, String title, String[] headers, List<T> dataList, RowMapper<T> mapper) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title + ".xlsx", "UTF-8"));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(title);

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < dataList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                String[] values = mapper.map(dataList.get(i));
                for (int j = 0; j < values.length; j++) {
                    row.createCell(j).setCellValue(values[j] != null ? values[j] : "");
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    private <T> void exportPdf(HttpServletResponse response, String title, String[] headers, List<T> dataList, RowMapper<T> mapper) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title + ".pdf", "UTF-8"));

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font titleFont = new Font(bfChinese, 18, Font.BOLD);
        Font headerFont = new Font(bfChinese, 10, Font.BOLD);
        Font cellFont = new Font(bfChinese, 9, Font.NORMAL);

        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(20);
        document.add(titlePara);

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        for (T item : dataList) {
            String[] values = mapper.map(item);
            for (String value : values) {
                PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", cellFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();
    }

    @FunctionalInterface
    interface RowMapper<T> {
        String[] map(T item);
    }
}
