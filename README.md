# MysqlAPI
用于服务器的MYSQL数据操作
使用方法：
1、把这个插件作为引用
2、创建新对象MysqlAPI，构造函数需要提供所有的字段名字
3、使用login(String ip,String port,String user,String password,String database,String table)方法载入登录信息
4、使用connect()方法连接数据库，完成时使用close()方法关闭连接
5、createTable()方法创建一个新的表
6、setData(String key ,String keyData,List<String> fields,List<String> data)方法更新数据
    key是检索用的字段名，keydata是要检索的字段内容，fields是所有字段（从左到右），data是所有字段的内容（从左到右）
7、getData(String key ,String keyData,int length)方法获取数据
   key是检索用的字段名，keydata是要检索的字段内容，length是表内的字段有多少
   返回List<String>，所有的字段内容（从左到右）
