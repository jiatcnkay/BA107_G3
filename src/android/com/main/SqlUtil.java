package android.com.main;

import java.util.Map;
import java.util.Set;

public class SqlUtil {
	
	public static String get_aCondition_For_Oracle(String columnName, String value) {

		String aCondition = null;

		if ("mem_gender".equals(columnName) || "mem_county".equals(columnName) || "mem_emotion".equals(columnName)) // �Ω�varchar
			aCondition = columnName + " like '%" + value + "%'";
		else if ("mem_birthday".equals(columnName)){
	        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy");
	        String formatDate = df.format(new java.util.Date());
	        Integer year = Integer.parseInt(formatDate);
	        
			switch(value){
				case "20���H�U":
					aCondition = columnName + " >to_date('"+(year-20)+"/01/01','yyyy/mm/dd' )";
					break;
				case "20��30��":
					aCondition = columnName + " between to_date('"+(year-30)+"/01/01','yyyy/mm/dd' ) and"
							+ " to_date('"+(year-20)+"/01/01','yyyy/mm/dd' )";
					break;
				case "30��40��":
					aCondition = columnName + " between to_date('"+(year-40)+"/01/01','yyyy/mm/dd' ) and"
							+ " to_date('"+(year-30)+"/01/01','yyyy/mm/dd' )";
					break;
				case "40���H�W":
					aCondition = columnName + " <to_date('"+(year-40)+"/01/01','yyyy/mm/dd' )";
					break;
			}
		}                         
		return aCondition + " ";
	}

	
	public static String get_WhereCondition(Map<String, String> map) {
		Set<String> keys = map.keySet();
		StringBuffer whereCondition = new StringBuffer();
		int count = 0;
		for (String key : keys) {
			String value = map.get(key);
			if (value != null && value.trim().length() != 0) {
				count++;
				String aCondition = get_aCondition_For_Oracle(key, value.trim());

				if (count == 1)
					whereCondition.append(" where " + aCondition);
				else
					whereCondition.append(" and " + aCondition);

				System.out.println("���e�X�d�߸�ƪ�����count = " + count);
			}
		}	
		return whereCondition.toString();
	}
}

//if ("mem_gender".equals(columnName) || "sal".equals(columnName) || "comm".equals(columnName) || "deptno".equals(columnName)) // �Ω��L
//aCondition = columnName + "=" + value;
