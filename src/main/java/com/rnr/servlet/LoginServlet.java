package com.rnr.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author rnr
 *
 *LoginServlet inherits all the features of a servlet by extending HttpServlet.
 *By default the doGet method will be called by the webContainer.
 *If valid credentials are given the user is redirected to success page, which is dynamically built in the below doGet method.
 *If invalid credentials are entered then the login page is displayed back to the user.  
 */
public class LoginServlet extends HttpServlet{
	
	/**
	 * This is auto generated
	 */
	private static final long serialVersionUID = -8145663309844069243L;

	Connection conn = null;
	String driver;
	String url;
	String userName;
	String password;
	
	/**
	 * The request comes to this method when the login button is clicked
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = null;
		out = response.getWriter();
	
		try {
			//get the user entered input values from the "HttpServletRequest" object, i.e request 
			String userNameStr =  request.getParameter("usernameTB");
			String passwordStr =  request.getParameter("passwordTB");
			
			System.out.println("username : " + userNameStr);
			System.out.println("password : " + passwordStr);
			
			System.out.println("ServletConfig : " + getServletConfig().getInitParameter("project"));
			System.out.println("ServletContext : " + getServletContext().getInitParameter("instructor"));
			
			boolean isValid = isValidPassword(userNameStr, passwordStr);
			
			if(isValid){
				System.out.println("Valid login cridentials entered");
				out.println("<html>");
				out.println("<body>");
				out.println("<h1>");	
				out.println("Hello welcome guest to the website");
				out.println("</h1>");
				out.println("</body>");
				out.println("</html>");			
			}else{
				System.out.println("invalid login cidentials");
				response.sendRedirect("login.html");
			}		
		}catch(Exception ex){
			System.out.println("Exception while processing request.");
			response.sendRedirect("login.html");
		}
	}
	
	/**
	 * Method for implementing the business logic, like comparing the passwords
	 * @param userName
	 * @param password
	 * @return
	 */
	public boolean isValidPassword(String userName, String password) {
		
		String pwdFromDB = getUserPassword(userName);
		if (null != pwdFromDB) {
			if (pwdFromDB.equals(password)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method which interacts with data base and fetches password for the user who is trying to login
	 * @param userName
	 * @return
	 */
	public String getUserPassword(String userName) {

		Connection conn;
		PreparedStatement stmt;
		String passwordFromDB = null;

		try {
			conn = getDBConnection(); //get the data base connection object
			String sql = "SELECT user_password FROM  USER where user_name = ? ";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userName);

			ResultSet res = stmt.executeQuery();
			while (res.next()) {
				passwordFromDB = res.getString("USER_PASSWORD");
			}
			conn.close(); //close the database connection
		} catch (Exception e) {
			e.printStackTrace();
		}
		return passwordFromDB;
	}

	public Connection getDBConnection() throws SQLException {
		try {
			loadDbProperties();
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, password);
			//give back the database connection object to the caller
			return conn;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * Read the DB configuration parameters from the .properties file in the class path using ResourceBundle class
	 */
	public void loadDbProperties() {
		//Resource bundle looks for a file named dbParameters.properties in the class path
		ResourceBundle labels = ResourceBundle.getBundle("dbParameters");
		driver = labels.getString("DB_DRIVER");
		url = labels.getString("DB_URL");
		userName = labels.getString("DB_USER");
		password = labels.getString("DB_PASSWORD");
	}
	
}
