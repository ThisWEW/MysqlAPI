package wew.MysqlAPI;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
  public void onEnable(){
	  getLogger().info("MysqlAPI�ɹ�������");
  }
  public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args){
	  if(sender.isOp()&&cmd.getName().equalsIgnoreCase("mysqlapi")){
		  sender.sendMessage("MysqlAPI���������У�  BY:WEW");
		  return true;
	  }
	  return false;
  }
}
