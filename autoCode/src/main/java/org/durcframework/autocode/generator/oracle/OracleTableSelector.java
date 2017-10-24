package org.durcframework.autocode.generator.oracle;

import java.util.Map;

import org.durcframework.autocode.generator.ColumnSelector;
import org.durcframework.autocode.generator.DataBaseConfig;
import org.durcframework.autocode.generator.TableDefinition;
import org.durcframework.autocode.generator.TableSelector;

/**
 * 查询ORACLE数据库表
 */
public class OracleTableSelector extends TableSelector {

	public OracleTableSelector(ColumnSelector columnSelector,
			DataBaseConfig dataBaseConfig) {
		super(columnSelector, dataBaseConfig);
	}

	@Override
	protected String getShowTablesSQL(String dbName) {
		//String sql = "SHOW TABLE STATUS FROM " + dbName;
		String sql = "SELECT A.TABLE_NAME AS TNAME,B.COMMENTS AS TCOMMENT " + 
				"FROM USER_TABLES A,USER_TAB_COMMENTS B " + 
				"WHERE A.TABLE_NAME=B.TABLE_NAME";
		if(this.getSchTableNames() != null && this.getSchTableNames().size() > 0) {
			StringBuilder tables = new StringBuilder();
			for (String table : this.getSchTableNames()) {
				tables.append(",'").append(table).append("'");
			}
			sql += " AND A.TABLE_NAME IN (" + tables.substring(1) + ")";
		}
		return sql;
	}

	@Override
	protected TableDefinition buildTableDefinition(Map<String, Object> tableMap) {
		TableDefinition tableDefinition = new TableDefinition();
		tableDefinition.setTableName((String)tableMap.get("TNAME"));
		tableDefinition.setComment((String)tableMap.get("TCOMMENT"));
		return tableDefinition;
	}

}
