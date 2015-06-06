package com.pandora.helper;

public final class DBUtil {

	public static String addDate(String dbname, String column, String value){
		String sql = "";

		if (dbname.equalsIgnoreCase("MySQL")) {
			sql = sql + "ADDDATE(" + column + "," + value + ") ";
		} else if (dbname.equalsIgnoreCase("PostgreSQL")) {
			sql = sql + column + " + cast((" + value + ") || ' day' as interval) ";
		} else if (dbname.equalsIgnoreCase("sqlserver")) {
			sql = sql + "DATEADD(day, " + value + "," + column + ") ";
		} else if (dbname.startsWith("HSQL")) {
			sql = sql + "dateadd('day', " + value + "," + column + ") ";
		}
		
		return sql;
	}
	
}
