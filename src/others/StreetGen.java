package others;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.Statement;

import config.Config;

public class StreetGen {
	private Driver dbDriver = null;
	
	private String url;
	
	public void init() {

		if (url == null) {
			url = Config.get("dburl");
		}

		try {
			if (dbDriver == null) {
				dbDriver = (Driver) Class.forName(
						"com.microsoft.sqlserver.jdbc.SQLServerDriver")
						.newInstance();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void getStreets() {
		init();

		Connection c = null;
		Statement stmt = null;

		try {
			FileWriter write = new FileWriter("./newstreets.txt");
			c = dbDriver.connect(url, null);
			c.setAutoCommit(false);
			stmt = c.createStatement();

			stmt.execute("use mis");

			String query = "SELECT HotelID, Name, Address"
					+ " FROM D_Hotel";

			System.out.println("start query... \n" + query);

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				write.write(getStreetFromAdd(rs.getString(3)) + "\n");
			}
			System.out.println("...query end");
			rs.close();
			stmt.close();
			c.close();
			write.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getStreetFromAdd(String add) {
		if (add.indexOf("路") >= 0) {
			int index = add.indexOf("路");
			return add.substring(0, index+1);
		}
		if (add.indexOf("街") >= 0) {
			int index = add.indexOf("街");
			return add.substring(0, index+1);
		}
		if (add.indexOf("道") >= 0) {
			int index = add.indexOf("道");
			return add.substring(0, index+1);
		}
		if (add.indexOf("大街") >= 0) {
			int index = add.indexOf("大街");
			return add.substring(0, index+2);
		}
		if (add.indexOf("大道") >= 0) {
			int index = add.indexOf("大道");
			return add.substring(0, index+2);
		}
		return add;
	}
	
	public void getAlias() {
		init();

		Connection c = null;
		Statement stmt = null;

		try {
			FileWriter write = new FileWriter("./alias.txt");
			c = dbDriver.connect(url, null);
			c.setAutoCommit(false);
			stmt = c.createStatement();

			stmt.execute("use mis");

			String query = "SELECT HotelID, Name"
					+ " FROM D_Hotel";

			System.out.println("start query... \n" + query);

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				write.write(rs.getString(1) + "\t" + rs.getString(2) + "\t" + getAliasFromName(rs.getString(2)) + "\n");
			}
			System.out.println("...query end");
			rs.close();
			stmt.close();
			c.close();
			write.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getAliasFromName(String name) {
		String ret = "";
		if (name.indexOf("大酒店") >= 0) {
			int index = name.indexOf("大酒店");
			//ret += name.substring(0, index) + ";";
			ret += name.substring(0, index) + "酒店;";
			ret += name.substring(0, index) + "宾馆;";
		}
		if (name.indexOf("宾馆") >= 0) {
			int index = name.indexOf("宾馆");
			ret += name.substring(0, index) + "酒店;";
			ret += name.substring(0, index) + "大酒店;";
		}
		if (name.indexOf("酒店") >= 0 && !name.contains("大酒店")) {
			int index = name.indexOf("酒店");
			ret += name.substring(0, index) + "宾馆;";
			ret += name.substring(0, index) + "大酒店;";
		}
		if (name.startsWith("上海") || name.startsWith("北京") || name.startsWith("广州") 
				|| name.startsWith("深圳")) {
			ret += name.substring(2, name.length()) + ";";
			String _name = name.substring(2, name.length());
			if (_name.indexOf("大酒店") >= 0) {
				int index = _name.indexOf("大酒店");
				//ret += name.substring(0, index) + ";";
				ret += _name.substring(0, index) + "酒店;";
				ret += _name.substring(0, index) + "宾馆;";
			}
			if (_name.indexOf("宾馆") >= 0) {
				int index = _name.indexOf("宾馆");
				ret += _name.substring(0, index) + "酒店;";
				ret += _name.substring(0, index) + "大酒店;";
			}
			if (_name.indexOf("酒店") >= 0 && !name.contains("大酒店")) {
				int index = _name.indexOf("酒店");
				ret += _name.substring(0, index) + "宾馆;";
				ret += _name.substring(0, index) + "大酒店;";
			}
		}
		
		if (name.contains("锦江之星")) {
			ret = "锦江之星;";
		} else if (name.contains("如家快捷")) {
			ret = "如家;";
		} else if (name.contains("汉庭酒店")) {
			ret = "汉庭";
		}
		
		if (ret.length() > 0) {
			ret = ret.substring(0, ret.length() - 1);
		}
		return ret;
	}
	
	public static void main(String args[]) {
		StreetGen sg = new StreetGen();
		sg.getStreets();
	}
}
