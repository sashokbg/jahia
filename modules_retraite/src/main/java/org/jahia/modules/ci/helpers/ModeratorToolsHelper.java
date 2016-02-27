package org.jahia.modules.ci.helpers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.commons.util.CiConstants;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IRow;
import org.jahia.services.render.RenderContext;

public class ModeratorToolsHelper implements CiConstants {

	private static final Log LOG = LogFactory.getLog(ModeratorToolsHelper.class);

	/**
	 * Make an excel worbook file from IExcelExtractor and properties contained
	 * in.
	 * 
	 * @param excelExtractor
	 *            the IExcelExtractor that contains all information to build
	 *            Excel Workbook
	 * @return an excel worbook
	 */
	public static Workbook buildExtractionFile(IExcelExtractor excelExtractor) {
		HSSFWorkbook workbook = new HSSFWorkbook();

		// steelsheet name
		HSSFSheet sheet = workbook.createSheet(excelExtractor.getTitle());

		// first row or column style definition
		HSSFCellStyle titleStyle = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		titleStyle.setFont(font);

		// default cell definition
		HSSFCellStyle defaultStyle = workbook.createCellStyle();
		defaultStyle.setWrapText(true);

		// create first row
		sheet.createRow(0);

		// insert titles on left or/and top
		int rowOffset = 0;
		for (IColumn column : excelExtractor.getColumns()) {
			if (StringUtils.isNotEmpty(column.getLabel())) {
				rowOffset = 1;
				int rowNum = excelExtractor.getColumns().indexOf(column);
				sheet.getRow(0).createCell(rowNum).setCellValue(new HSSFRichTextString(column.getLabel()));
				sheet.getRow(0).getCell(rowNum).setCellStyle(titleStyle);
			}
			if (LOG.isDebugEnabled())
				LOG.debug(" | " + column.getLabel());
		}

		int columnOffset = 0;
		for (IRow irow : excelExtractor.getRows()) {
			if (StringUtils.isNotEmpty(irow.getLabel())) {
				columnOffset = 1;
				int rowNum = excelExtractor.getRows().indexOf(irow);
				Row row = null;
				if (sheet.getRow(rowNum) == null)
					row = sheet.createRow(rowNum);
				else
					row = sheet.getRow(rowNum);
				row.createCell(0).setCellValue(new HSSFRichTextString(irow.getLabel()));
				row.getCell(0).setCellStyle(titleStyle);
			}
		}

		// inserting tuples

		/*
		 * vertical representation of data : name on the top (title) and values
		 * to the below.
		 */
		for (Properties properties : excelExtractor.getProperties()) {
			int rowNum = excelExtractor.getProperties().indexOf(properties) + rowOffset;
			Row row = null;
			if (sheet.getRow(rowNum) == null)
				row = sheet.createRow(rowNum);
			else
				row = sheet.getRow(rowNum);
			for (IColumn column : excelExtractor.getColumns()) {
				int columnNum = excelExtractor.getColumns().indexOf(column);
				String property = properties.getProperty(column.getPropertyName());
				if (property != null) {
					property = properties.getProperty(column.getPropertyName(), column.getDefaultValue());
				}
				setCellProperty(row.createCell(columnNum + columnOffset), property);
				row.getCell(columnNum + columnOffset).setCellStyle(defaultStyle);
			}
			if (LOG.isDebugEnabled()) {
				StringBuilder stringBuilder = new StringBuilder();
				Iterator<Cell> cellIterator = sheet.getRow(rowNum).cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();
					stringBuilder.append(" | " + cell);
				}
				LOG.debug(stringBuilder.toString());
			}
		}

		/*
		 * horizontal representation of data : name on the left and value to the
		 * right.
		 */
		for (Properties properties : excelExtractor.getProperties()) {
			for (IRow irow : excelExtractor.getRows()) {
				int rowNum = excelExtractor.getRows().indexOf(irow) + rowOffset;
				Row row = null;

				if (sheet.getRow(rowNum) == null)
					row = sheet.createRow(rowNum);
				else
					row = sheet.getRow(rowNum);

				int colNum = excelExtractor.getProperties().indexOf(properties);

				String property = properties.getProperty(irow.getPropertyName());
				if (property != null) {
					property = properties.getProperty(irow.getPropertyName(), irow.getDefaultValue());
				}
				setCellProperty(row.createCell(colNum + columnOffset), property);
				row.getCell(colNum + columnOffset).setCellStyle(defaultStyle);
				if (LOG.isDebugEnabled()) {
					StringBuilder stringBuilder = new StringBuilder();
					Iterator<Cell> cellIterator = sheet.getRow(rowNum).cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = (Cell) cellIterator.next();
						stringBuilder.append(" | " + cell);
					}
					LOG.debug(stringBuilder.toString());
				}
			}
		}

		// formatting column size
		for (IColumn column : excelExtractor.getColumns()) {
			int rowNum = excelExtractor.getColumns().indexOf(column);
			sheet.autoSizeColumn(rowNum);
		}
		// formatting row size
		for (IRow row : excelExtractor.getRows()) {
			int rowNum = excelExtractor.getRows().indexOf(row);
			sheet.autoSizeColumn(rowNum);
		}

		// add filter to the top if title is present
		if (rowOffset > 0) {
			int lastColNum = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
			String lastColLetter = CellReference.convertNumToColString(lastColNum - 1);
			sheet.setAutoFilter(CellRangeAddress.valueOf("A1:" + lastColLetter + sheet.getLastRowNum()));
		}

		return workbook;
	}

	private static void setCellProperty(Cell cell, String property) {
		if (NumberUtils.isNumber(property))
			cell.setCellValue(Double.parseDouble(property));
		else
			cell.setCellValue(property);
	}

	/**
	 * @param renderContext
	 * @param date
	 * @param excelExtractor
	 * @throws IOException
	 */
	public static void writeWorkbook(RenderContext renderContext, Date date, IExcelExtractor excelExtractor) throws IOException {
		// build hssf workbook
		HSSFWorkbook workbook = (HSSFWorkbook) ModeratorToolsHelper.buildExtractionFile(excelExtractor);

		// sending hssf workbook in response
		HttpServletResponse response = renderContext.getResponse();
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
		String dateFormatted = dateFormat.format(date);

		response.setHeader("Content-Disposition", "attachment; filename=" + excelExtractor.getNormalizedTitle() + "_" + dateFormatted
				+ ".xls");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", -1);
		response.setContentType("application/vnd.ms-excel");

		workbook.write(response.getOutputStream());
	}
}
