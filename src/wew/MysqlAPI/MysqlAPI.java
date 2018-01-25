package wew.MysqlAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MysqlAPI {
		private String SqlIp = null;
		private String SqlPort = null;
		private String Sql = null;
		private  String SqlTable = null;
		private String SqlUser = null;
		private String SqlPasd = null;
		private String[] fields ;
		private Connection connect = null;
		public MysqlAPI(String... field){
			fields = field;
		}
		public void login(String ip,String port,String user,String password,String database,String table){
			SqlIp = ip;
			SqlPort = port;
			Sql = database;
			SqlUser = user;
			SqlPasd = password;
			SqlTable = table;
		}
		public void connect(){
				  String url="jdbc:mysql://"+SqlIp+":"+SqlPort+"/"+Sql+"?useUnicode=true&characterEncoding=utf-8"; 
				  String user=SqlUser; 
				  String password=SqlPasd; 
					Connection conn = null;
					try {
						conn = DriverManager.getConnection(url,user,password);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					connect = conn;
			}
		public void close(){
				try {
					connect.close();
				} catch (SQLException e) {e.printStackTrace();}
			}
			 public void createTable(){
					try {
						PreparedStatement state = connect.prepareStatement("create table if not exists "+SqlTable+"("+getFields()+")");
						state.executeUpdate();
						state = connect.prepareStatement("alter table "+SqlTable+" convert to character set utf8");
						state.executeUpdate();
						state.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			 }
			 public void setData(String key ,String keyData,List<String> fields,List<String> data){
				 try {
					PreparedStatement state = connect.prepareStatement("select * from "+SqlTable+" where "+key+"=?");
					state.setString(1, keyData);
					ResultSet res = state.executeQuery();
					if(res.next()){
						state=connect.prepareStatement("update "+SqlTable+" "+getSettingSentence(fields,data)+" where "+key+"=?");
						state.setString(1, keyData);
						state.executeUpdate();
						state.close();
					}else{
						state=connect.prepareStatement("insert into "+SqlTable+" values("+getInsertSentence(data)+")");
						state.executeUpdate();
						state.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			 }
			 public List<String> getData(String key ,String keyData,int length){
				 try {
					PreparedStatement state = connect.prepareStatement("select * from "+SqlTable+" where "+key+"=?");
					state.setString(1, keyData);
					ResultSet res = state.executeQuery();
					List<String> args = new ArrayList<>();
					if(res.next()){
						for(int i = 1;i<=length;i++){
							args.add(res.getString(i));
						}
					}
					return args;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			 }
			 public void deleteData(String key,String keyData){
				 try {
						PreparedStatement state = connect.prepareStatement("delete from "+SqlTable+" where "+key+"=?");
						state.setString(1, keyData);
						state.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			 }
			 public List<String> getAllKey(){
				 try {
						PreparedStatement state = connect.prepareStatement("select * from "+SqlTable);
						ResultSet res = state.executeQuery();
						List<String> args = new ArrayList<>();
						while(res.next()){
						    args.add(res.getString(1));
						}
						return args;
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return null;
			 }
		private String getFields(){
			StringBuilder builder = new StringBuilder();
			int i = 0;
			for(String arg:fields){
				i = i+1;
				builder.append(arg+" TEXT"+(i==fields.length?"":","));
			}
			return builder.toString();
		}
		private String getSettingSentence(List<String> fields,List<String> data){
			StringBuilder builder = new StringBuilder();
			for(int i = 0;i<fields.size();i++){
				builder.append("set "+fields.get(i)+"='"+data.get(i)+"'"+(i == fields.size()-1?"":" , "));
			}
			return builder.toString();
		}
		private String getInsertSentence(List<String> data){
			StringBuilder builder = new StringBuilder();
			for(String arg:data){
				builder.append("'"+arg+"'"+(data.indexOf(arg) == data.size()-1?"":","));
			}
			return builder.toString();
		}
}
