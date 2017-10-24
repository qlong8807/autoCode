package org.durcframework.autocode.generator.oracle;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.durcframework.autocode.generator.ColumnDefinition;
import org.durcframework.autocode.generator.ColumnSelector;
import org.durcframework.autocode.generator.DataBaseConfig;

/**
 * mysql表信息查询
 *
 */
public class OracleColumnSelector extends ColumnSelector {
	
	public OracleColumnSelector(DataBaseConfig dataBaseConfig) {
		super(dataBaseConfig);
	}

	/**
	 * SHOW FULL COLUMNS FROM 表名
	 */
	@Override
	protected String getColumnInfoSQL(String tableName) {
		tableName = tableName.toUpperCase();
		String sql = "SELECT COLUME.*," + 
				"    CASE WHEN PK.POSITION IS NULL THEN 0 ELSE PK.POSITION END AS IS_PK " + 
				"	FROM" + 
				"  (SELECT  T.TABLE_NAME,T.COLUMN_NAME,T.DATA_TYPE,C.COMMENTS  FROM USER_TAB_COLUMNS  T,USER_COL_COMMENTS  C" + 
				"   WHERE T.TABLE_NAME = C.TABLE_NAME AND T.COLUMN_NAME = C.COLUMN_NAME AND T.TABLE_NAME = '"+tableName+"') COLUME" + 
				"  LEFT JOIN " + 
				"  (SELECT CU.* " + 
				"  FROM USER_CONS_COLUMNS CU, USER_CONSTRAINTS AU " + 
				"  WHERE CU.CONSTRAINT_NAME = AU.CONSTRAINT_NAME AND AU.CONSTRAINT_TYPE = 'P' AND AU.TABLE_NAME = '"+tableName+"') PK" + 
				"  ON PK.COLUMN_NAME =COLUME.COLUMN_NAME";
		return sql;
	}
	
	/*
	 * {FIELD=username, EXTRA=, COMMENT=用户名, COLLATION=utf8_general_ci, PRIVILEGES=select,insert,update,references, KEY=PRI, NULL=NO, DEFAULT=null, TYPE=varchar(20)}
	 */
	protected ColumnDefinition buildColumnDefinition(Map<String, Object> rowMap){
		Set<String> columnSet = rowMap.keySet();
		
		for (String columnInfo : columnSet) {
			rowMap.put(columnInfo.toUpperCase(), rowMap.get(columnInfo));
		}
		
		ColumnDefinition columnDefinition = new ColumnDefinition();
		
		columnDefinition.setColumnName((String)rowMap.get("COLUMN_NAME"));
		
		//全部是非自增
		columnDefinition.setIsIdentity(false);
		
		boolean isPk = "1".equals((BigDecimal)rowMap.get("IS_PK")+"");
		columnDefinition.setIsPk(isPk);
		
		String type = (String)rowMap.get("DATA_TYPE");
		columnDefinition.setType(type);
		
		columnDefinition.setComment((String)rowMap.get("COMMENTS"));
		
		return columnDefinition;
	}
	
}
