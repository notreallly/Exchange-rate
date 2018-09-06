package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAO {
	private DAO() {
	}

	private static Connection getConnection() throws SQLException {
		String url = "jdbc:sqlserver://localhost:1433;databaseName=MyDB";
		String user = "sa";
		String pass = "1234";

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(url, user, pass);
	}

	public static boolean insertData(String json, String currencyName) {
		String query = "create table " + currencyName
				+ " (DATE date primary key,BUY nvarchar(10) not null,SELL nvarchar(10) not null) ;"
				+ "DECLARE @json NVARCHAR(MAX) = N'" + json + "' INSERT INTO " + currencyName
				+ " (DATE, BUY, SELL) SELECT DATE, BUY, SELL FROM OPENJSON(@json) "
				+ "WITH (DATE date,BUY nvarchar(10),SELL nvarchar(10))";
		boolean flag = false;

		try (PreparedStatement stmt = getConnection().prepareStatement(query);) {
			if (stmt.executeUpdate() != 0)
				flag = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return flag;
	}

	public static CharSequence queryData(String currencyName) {
		String query = "select * from " + currencyName + " for json auto";
		StringBuffer output = new StringBuffer();

		try (PreparedStatement stmt = getConnection().prepareStatement(query);) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
				output.append(rs.getString(1));

			return output;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean chkData(String currencyName) throws SQLException {
		String query = "SELECT * FROM sys.tables WHERE name =?";
		boolean flag = false;

		try (PreparedStatement stmt = getConnection().prepareStatement(query);) {
			stmt.setString(1, currencyName);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				flag = true;
		}
		return flag;
	}
}
