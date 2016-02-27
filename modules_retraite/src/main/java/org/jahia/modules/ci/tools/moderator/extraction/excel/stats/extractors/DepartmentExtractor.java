package org.jahia.modules.ci.tools.moderator.extraction.excel.stats.extractors;

import java.util.Arrays;
import java.util.Properties;

import org.commons.util.Formatter;
import org.jahia.modules.ci.tools.moderator.extraction.excel.IColumn;
import org.jahia.modules.ci.tools.moderator.extraction.excel.stats.StatExcelExtractor;
import org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks.ZipCodeOfMembers;
import org.jahia.modules.ci.tools.moderator.extraction.stats.localities.Department;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;

public class DepartmentExtractor extends StatExcelExtractor {

	public DepartmentExtractor() {
		super("R" + Formatter._Character.EACUTE + "partition des membres par r" + Formatter._Character.EACUTE + "gion");
		this.setColumns(Arrays.asList(membersDepts));
	}

	@Override
	public Object execute(JCRSessionWrapper session, RenderContext renderContext) {
		populateWithMapCallback(session, renderContext, new ZipCodeOfMembers());
		return null;
	}
	public String HORS_FRANCE = "Hors France";
	@Override
	protected void fillObjectValueProperties(Object entry, Object value) {
		entry = Department.getFromDepartmentNumber(String.valueOf(entry));
		Properties properties = new Properties();
		//test si hors France
		if (entry != null){
			properties.put(StatColumn.MEMBERS_REGION_CODE.getPropertyName(), ((Department) entry).getDepartmentNumber());
			properties.put(NAME, ((Department) entry).getLabel());
			properties.put(VALUE, String.valueOf(value));
		}
		else{
			properties.put(StatColumn.MEMBERS_REGION_CODE.getPropertyName(), HORS_FRANCE);
			properties.put(NAME, HORS_FRANCE);
			properties.put(VALUE, String.valueOf(value));
					
		}
		getProperties().add(properties);
	}

	private static IColumn[] membersDepts = new IColumn[] { StatColumn.MEMBERS_REGION_CODE, StatColumn.MEMBERS_REGION_NAME,
			StatColumn.MEMBERS_REGION_VALUE };
}