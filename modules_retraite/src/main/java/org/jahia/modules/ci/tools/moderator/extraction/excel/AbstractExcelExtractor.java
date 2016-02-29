package org.jahia.modules.ci.tools.moderator.extraction.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.commons.util.CiConstants;
import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.AbstractExtractor;

public abstract class AbstractExcelExtractor extends AbstractExtractor implements IExcelExtractor, CiConstants {

	public static final String NAME = "name";
	public static final String VALUE = "value";
	protected String title;
	protected String normalizedTitle;
	protected List<Properties> properties;
	protected List<IColumn> columns;
	protected List<IRow> rows;
	protected int sortColumn;

	public AbstractExcelExtractor(String title) {
		setTitle(title);
		properties = new ArrayList<Properties>();
		columns = new ArrayList<IColumn>();
		rows = new ArrayList<IRow>();
	}

	protected String statParam;

	public void setStatParam(String statParam) {
		this.statParam = statParam;
	}

	public String getTitle() {
		return title;
	}

	public String getNormalizedTitle() {
		return normalizedTitle;
	}

	public void setTitle(String title) {
		this.title = title;
		String tmpTitle = Formatter.normalizeText(title, Formatter.FILENAME_CHARS_TO_ESCAPE);
		if (tmpTitle.length() > 35)
			this.normalizedTitle = new String(tmpTitle.substring(0, 34));
		else
			this.normalizedTitle = tmpTitle;
	}

	public void setNormalizedTitle(String normalizedTitle) {
		this.normalizedTitle = normalizedTitle;
	}

	public List<Properties> getProperties() {
		return properties;
	}

	public void setProperties(List<Properties> properties) {
		this.properties = properties;
	}

	public List<IColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<IColumn> columns) {
		this.columns = columns;
	}

	public List<IRow> getRows() {
		return rows;
	}

	public void setRows(List<IRow> rows) {
		this.rows = rows;
	}

	public int getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(int sortColumn) {
		this.sortColumn = sortColumn;
	}

}