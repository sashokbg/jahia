package org.jahia.modules.ci.tools.moderator.extraction.excel;

import java.util.List;
import java.util.Properties;

import org.jahia.modules.ci.tools.moderator.extraction.IExtractor;



public interface IExcelExtractor extends IExtractor {

	public abstract String getNormalizedTitle();

	public abstract List<Properties> getProperties();

	public abstract List<IColumn> getColumns();
	
	public abstract List<IRow> getRows();

	public abstract String getTitle();

}