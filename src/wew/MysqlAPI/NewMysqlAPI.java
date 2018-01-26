package wew.MysqlAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NewMysqlAPI {
	
	private String SqlIp = null;
	private String SqlPort = null;
	private String Sql = null;
	private  String SqlTable = null;
	private String SqlUser = null;
	private String SqlPasd = null;
	private String[] fields ;
	private Connection connect = null;
	private Statement statement = null;
	
	public NewMysqlAPI(String... field){
		fields = field;
	}
	
	/**
	 * ��½���ݿ�
	 */
	public void login(String ip, String port, String user, String password, String database, String table) {
		SqlIp = ip;
		SqlPort = port;
		Sql = database;
		SqlUser = user;
		SqlPasd = password;
		SqlTable = table;
	}
	
	/**
	 * �������ݿ�
	 */
	public void connect() {
		String url = "jdbc:mysql://" + SqlIp + ":" + SqlPort + "/" + Sql + "?useUnicode=true&characterEncoding=utf-8"; 
		String user = SqlUser; 
		String password = SqlPasd; 
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			statement = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		connect = conn;
	}
	
	/**
	 * �ر��������ݿ�
	 */
	public void close() {
		try {
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * ����½�󣬴��������
	 */
	public void createTable() {
		connect();
		try {
			PreparedStatement state = connect.prepareStatement("create table if not exists " + SqlTable + "(" + getFields() + ")");
			state.executeUpdate();
			state = connect.prepareStatement("alter table " + SqlTable + " convert to character set utf8");
			state.executeUpdate();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
	}
		 
	/**
	 * ����һ������
	 * key = ��������
	 * keydata = ����������
	 * fields = ���ݿ��е���
	 * data = ÿ���е�����
	 */
	@SuppressWarnings("resource")
	public void addNewData(String key, String keyData, List<String> fields, List<String> data) {
		connect();
		try {
			PreparedStatement state = connect.prepareStatement("select * from "+SqlTable + " where " + key + "=?");
			state.setString(1, keyData);
			ResultSet res = state.executeQuery();
			if(res.next()) {
				state=connect.prepareStatement("update "+SqlTable + " "+getSettingSentence(fields,data) + " where " + key+"=?");
				state.setString(1, keyData);
				state.executeUpdate();
				state.close();
			} else {
				state=connect.prepareStatement("insert into " + SqlTable + " values(" + getInsertSentence(data) + ")");
				state.executeUpdate();
				state.close();
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
	}
	
	/**
	 * ɾ��һ��key
	 * key = ��������
	 * keydata = ����������
	 */
	public void deleteData(String key, String keyData) {
		connect();
		try {
			PreparedStatement state = connect.prepareStatement("delete from " + SqlTable + " where " + key + "=?");
			state.setString(1, keyData);
			state.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
	}
	
	/**
	 * �������ݿ���һ���е�ֵ�����ֵ���滻Ϊ�µ�
	 * key = ��������
	 * keydata = ����������
	 * value = ��Ҫ�޸ĵ�����
	 * valuedata = �޸ĵ�ֵ
	 */
	public void onUpdate(String key, String keydata, String value, Object valuedata) {
		connect();
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			ResultSet res = stmt.executeQuery("select 1 from " + SqlTable + " where " + key + "='" + keydata + "'");
			if(!res.next()) {
				stmt.executeUpdate("insert into " + SqlTable + " values('" + keydata + "','" + valuedata + "')");
			} else {
				res = stmt.executeQuery("select " + value + " from " + SqlTable + " where " + key + "='" + keydata + "'");
				stmt.executeUpdate("update " + SqlTable + " set " + value + "='" + valuedata + "' where " + key + "='" + keydata + "'");
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
    }
	
	/**
	 * key = ��������
	 * keydata = ����������
	 * value = ��Ҫ���ҵ�ֵ
	 */
	public Object onGetData(String key, String keydata, String value) {
		connect();
		Connection con = connect;
		try {
			ResultSet rs = statement.executeQuery("select " + value + " from " + SqlTable + " where " + key + "='" + keydata + "'");
			while(rs.next()){
				 return rs.getString(1);
			 }
			 con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
		return null;
	}
		 
	/**
	 * ��ȡ���ݿ����е�key
	 */
	public List<String> getAllKey() {
		connect();
		try {
			PreparedStatement state = connect.prepareStatement("select * from " + SqlTable);
			ResultSet res = state.executeQuery();
			List<String> args = new ArrayList<>();
			while(res.next()) {
				args.add(res.getString(1));
			}
			return args;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
		return null;
	}
	
	private String getFields() {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for(String arg:fields) {
			i = i + 1;
			builder.append(arg + " TEXT" + (i == fields.length ? "":","));
		}
		return builder.toString();
	}
	 
	private String getSettingSentence(List<String> fields,List<String> data) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i<fields.size(); i++) {
			builder.append("set " + fields.get(i) + "='" + data.get(i) + "'" + (i == fields.size()-1 ? "":" , "));
		}
		return builder.toString();
	}
	private String getInsertSentence(List<String> data) {
		StringBuilder builder = new StringBuilder();
		for(String arg:data) {
			builder.append("'" + arg + "'" + (data.indexOf(arg) == data.size()-1 ? "":","));
		}
		return builder.toString();
	}

}
