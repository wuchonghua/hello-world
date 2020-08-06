package com.test;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 为了链接oracle数据库 生成表对应的javabean 
 * @author Administrator
 *
 */
public class GenBean implements Serializable{
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private static Connection con = null;
    private CallableStatement cst = null;
    static class Ora{
        static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
        static final String DATABASE_URL = "jdbc:oracle:thin:@113.140.80.174:15216:orcl";
        static final String DATABASE_USER = "SCOTT";
        static final String DATABASE_PASSWORD = "xinghui";
        static final String DATABASE_TABLE = "fact_user_power";  //需要生成的表名
    }
 
    static class MySql{
        static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
        static final String DATABASE_URL = "jdbc:mysql://localhost/plusoft_test?useUnicode=true&characterEncoding=GBK";
        static final String DATABASE_USER = "root";
        static final String DATABASE_PASSWORD = "1234";
    }
    public static Connection getOracleConnection() {
        try {
            Class.forName(Ora.DRIVER_CLASS);
            con=DriverManager.getConnection(Ora.DATABASE_URL,Ora.DATABASE_USER,Ora.DATABASE_PASSWORD);
            return con;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return con;
    }
 
    public static Connection getMySqlConnection() {
        try {
            Class.forName(MySql.DRIVER_CLASS);
            con=DriverManager.getConnection(MySql.DATABASE_URL,MySql.DATABASE_USER,MySql.DATABASE_PASSWORD);
            return con;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return con;
    }
    
    public static List<Map> getOracleTable(String Table) throws SQLException{
        getOracleConnection();
        List<Map> list = new ArrayList<Map>();
        try {
            DatabaseMetaData  m_DBMetaData = con.getMetaData(); 
            //getColumns(java.lang.String catalog,  java.lang.String schema,java.lang.String table, java.lang.String col)
            ResultSet colrs = m_DBMetaData.getColumns(null,Ora.DATABASE_USER.toUpperCase(), Table.toUpperCase(),"%"); 
            while(colrs.next()) { 
                Map map = new HashMap();
                String columnName = colrs.getString("COLUMN_NAME"); 
                String columnType = colrs.getString("TYPE_NAME"); 
                int datasize = colrs.getInt("COLUMN_SIZE"); 
                int digits = colrs.getInt("DECIMAL_DIGITS"); 
                int nullable = colrs.getInt("NULLABLE"); 
                String remarks = colrs.getString("REMARKS"); 
                //System.out.println(columnName+" "+columnType+" "+datasize+" "+digits+" "+ nullable); 
                map.put("columnName", columnName);
                map.put("columnType", columnType);
                map.put("datasize", datasize);
                map.put("remarks", remarks);
                list.add(map);
//                System.out.println("TABLE_CAT" + "===" + colrs.getString("TABLE_CAT"));  
//                System.out.println("TABLE_SCHEM" + "===" + colrs.getString("TABLE_SCHEM"));  
//                System.out.println("TABLE_NAME" + "===" + colrs.getString("TABLE_NAME"));  
//                System.out.println("COLUMN_NAME" + "===" + colrs.getString("COLUMN_NAME"));  
//                System.out.println("DATA_TYPE" + "===" + colrs.getString("DATA_TYPE"));  
//                System.out.println("TYPE_NAME" + "===" + colrs.getString("TYPE_NAME"));  
//                System.out.println("COLUMN_SIZE" + "===" + colrs.getString("COLUMN_SIZE"));  
//                System.out.println("BUFFER_LENGTH" + "===" + colrs.getString("BUFFER_LENGTH"));  
//                System.out.println("DECIMAL_DIGITS" + "===" + colrs.getString("DECIMAL_DIGITS"));  
//                System.out.println("NUM_PREC_RADIX" + "===" + colrs.getString("NUM_PREC_RADIX"));  
//                System.out.println("NULLABLE" + "===" + colrs.getString("NULLABLE"));  
//                System.out.println("REMARKS" + "===" + colrs.getString("REMARKS"));  
//                System.out.println("COLUMN_DEF" + "===" + colrs.getString("COLUMN_DEF"));  
//                System.out.println("SQL_DATA_TYPE" + "===" + colrs.getString("SQL_DATA_TYPE"));  
//                System.out.println("SQL_DATETIME_SUB" + "===" + colrs.getString("SQL_DATETIME_SUB"));  
//                System.out.println("CHAR_OCTET_LENGTH" + "===" + colrs.getString("CHAR_OCTET_LENGTH"));  
//                System.out.println("ORDINAL_POSITION" + "===" + colrs.getString("ORDINAL_POSITION"));  
//                System.out.println("IS_NULLABLE" + "===" + colrs.getString("IS_NULLABLE"));  
            }
//            while(colRet.next()){
//                System.out.print("列名："+colRet.getString("COLUMN_NAME"));
//                System.out.print("  数据类型是："+colRet.getString("DATA_TYPE"));
//                System.out.print("  类型名称是："+colRet.getString("TYPE_NAME"));
//                System.out.print("  列大小是："+colRet.getString("COLUMN_SIZE"));
//                System.out.println("  注释是："+colRet.getString("REMARKS"));
//            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            con.close();
        }
        return list;
    }
    /**
     *  把Oracle字段类型 转化为 java类型
     * @param sqlType  字段类型
     * @param size  字段大小
     * @param scale  默认=0
     * @return
     */
    public static String oracleSqlType2JavaType(String sqlType,int size,int scale){
        if (sqlType.equals("integer")) {
            return "Integer";
        } else if (sqlType.equals("long")) {
            return "Long";
        } else if (sqlType.equals("float")
                || sqlType.equals("float precision")
                || sqlType.equals("double")
                || sqlType.equals("double precision")
                ) {
            return "BigDecimal";
        }else if (sqlType.equals("number")
                ||sqlType.equals("decimal")
                || sqlType.equals("numeric")
                || sqlType.equals("real")) {
            return scale==0? (size<10? "Integer" : "Long") : "BigDecimal";
        }else if (sqlType.equals("varchar")
                || sqlType.equals("varchar2")
                || sqlType.equals("char")
                || sqlType.equals("nvarchar")
                || sqlType.equals("nchar")) {
            return "String";
        } else if (sqlType.equals("datetime")
                || sqlType.equals("date")
                || sqlType.equals("timestamp")) {
            return "Date";
        }
        return "String";
    }
    
    public static String getItems(List<Map> map,String tablename){
        //记得转化成小写
        StringBuffer sb = new  StringBuffer();
        sb.append("package com.databi.bean;");
        sb.append("\r\n");
        sb.append("import java.util.Date;\r\n");
        sb.append("/** \r\n "
            +" *  \r\n"
            +" * @author lsp  \r\n"
            +" *\r\n"
            + "*/\r\n"  );
        sb.append("\r\n");
        sb.append("public class "+ getUpperOne(tablename.toLowerCase()) + "  implements java.io.Serializable  {\r\n");
        //得到私有属性
        for (Map map0 : map) {
            String columnname = map0.get("columnName").toString();
            String columntype = map0.get("columnType").toString();
            String columnsize = map0.get("datasize").toString();
            String remarks = map0.get("remarks")==null?"":map0.get("remarks").toString();
            String javaType = oracleSqlType2JavaType(columntype.toLowerCase(),Integer.parseInt(columnsize),0);
            String temp = "\tprivate "+javaType+" "+columnname.toLowerCase()+"; //"+remarks+"\r\n";
            sb.append(temp);
        }
        //得到getter和setter 
        for (Map map0 : map) {
            String columnname = map0.get("columnName").toString();
            String columntype = map0.get("columnType").toString();
            String columnsize = map0.get("datasize").toString();
            String javaType = oracleSqlType2JavaType(columntype.toLowerCase(),Integer.parseInt(columnsize),0);
            String temp = "\tpublic "+javaType+" "+"get"+getUpperOne(columnname.toLowerCase())+"(){\r\n";
            String temp1 = "\t\treturn "+columnname.toLowerCase()+";\r\n";
            String temp2 = "\t}\r\n";
            sb.append(temp+temp1+temp2);
            temp = "\tpublic void "+"set"+getUpperOne(columnname.toLowerCase())+"("+javaType+" "+columnname.toLowerCase()+"){\r\n";
            temp1 = "\t\tthis."+columnname.toLowerCase()+" = "+columnname.toLowerCase()+";\r\n";
            temp2 = "\t}\r\n";
            sb.append(temp+temp1+temp2);
        }
        sb.append("}");
        return sb.toString();
        
    }
    
    /**
     * 把输入字符串的首字母改成大写
     * @param str
     * @return
     */
    public static String getUpperOne(String str){
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
    
    public static void main(String[] args) throws IOException {
        
        //JavaBeanUtils.sysoutOracleTCloumns("pexam_items_title", "his_yhkf");
        try {
            String tables  = "fact_user_power";
            String[] arr = tables.split(",");
            for (String string : arr) {
                String name = getUpperOne(string.toLowerCase());
                List<Map> map = getOracleTable(string);
                String a = getItems(map,string);
                File file = new File("D:\\bbb\\"+name+".java");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file,true);//true表示在文件末尾追加  
                fos.write(a.getBytes());  
                fos.close();//流要及时关闭  
            }
            System.out.println("生成java完成");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}