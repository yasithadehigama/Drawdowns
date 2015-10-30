package lsf.drawdowns.dbCon;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class IndexSrvlt
 */
@WebServlet(description = "this servelet will be the startup servlet and it "
		+ "may allow to open DB connection", urlPatterns = { "/IndexSrvlt" })
public class IndexSrvlt extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IndexSrvlt() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Gson gson = new Gson();
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		System.out.println("Drawdown system on live...!!!!!!1");
	}

	/**
	 * @see Servlet#getServletInfo()
	 */
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		System.out.println("Doget method running now........");

		String userPath = request.getServletPath();
		db_connections dbconnection = new db_connections();

		if (userPath.equals("/dataGet")) {
			try {
				// ResultSet set =
				// dbconnection.selectData("SELECT permno,date_withyear FROM capm_v2_table WHERE date='"+request.getParameter("Q")+"'");
				// ResultSet set =
				// dbconnection.selectData("SELECT caff_drawdown_end.permno,caff_drawdown_end.one_d FROM caff_drawdown_end INNER JOIN caff_drawdowns ON caff_drawdown_end.permno=caff_drawdowns.permno AND caff_drawdown_end.yrmo=caff_drawdowns.yrmo WHERE caff_drawdown_end.yrmo='200410'");

				System.out.println(request.getParameter("M"));
				;

				ResultSet set = dbconnection
						.selectData("SELECT PERMNO,CAPM_resid_D FROM view_capm where YRMO = '"
								+ request.getParameter("Q")
								+ request.getParameter("M") + "'");
				JSONArray jsonarray = new JSONArray();
				while (set.next()) {
					JSONObject jsonobj = new JSONObject();
					int permno = set.getInt("PERMNO");
					String year_date = set.getString("CAPM_resid_D");
					if (year_date == null) {

					} else {
						try {
							jsonobj.put("permno", permno);
							jsonobj.put("capm_date", year_date);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						jsonarray.put(jsonobj);
					}
				}
				PrintWriter pwr = response.getWriter();
				pwr.print(jsonarray);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else if (userPath.equals("/summaryData")) {
			try {
				String sql = null;
				if (request.getParameter("D").equals("caff")) {
					sql = "SELECT YEAR(one_d) AS date,COUNT(YEAR(one_d)) AS count FROM caff_drawdown_end GROUP BY YEAR(one_d)";
				} else {
					sql = "select date AS date,COUNT(date) AS count from capm_v2_table group by date";
				}
				ResultSet set = dbconnection.selectData(sql);

				ArrayList<Integer> aryCount = new ArrayList<Integer>();
				ArrayList<Integer> aryYear = new ArrayList<Integer>();

				while (set.next()) {
					if (set.getInt("count") == 0) {

					} else {
						aryCount.add(set.getInt("count"));
						aryYear.add(set.getInt("date"));
					}
				}
				JSONObject obj = new JSONObject();
				obj.put("Total", aryCount);
				obj.put("year", aryYear);
				PrintWriter pwr = response.getWriter();
				pwr.print(obj);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (userPath.equals("/indexData")) {
			System.out.println("indexData method");
			try {
				// ResultSet set =
				// dbconnection.selectData("select Index_dates,Index_values from indexDrawdown where Year='"+
				// request.getParameter("Q") + "'");
				ResultSet set = dbconnection
						.selectData("SELECT mergedata.one AS Index_values,mergedata.one_d AS Index_dates FROM mergedata WHERE mergedata.permno = 0 AND mergedata.one_d LIKE '%"
								+ request.getParameter("Q") + "%'");

				ArrayList<Float> aryValue = new ArrayList<Float>();
				ArrayList<String> aryDate = new ArrayList<String>();
				while (set.next()) {
					aryValue.add(set.getFloat("Index_values"));
					aryDate.add(set.getString("Index_dates"));
				}
				JSONObject obj = new JSONObject();
				obj.put("value", aryValue);
				obj.put("date", aryDate);
				PrintWriter pwr = response.getWriter();
				pwr.print(obj);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (userPath.equals("/index")) {

			try {
				
				ResultSet set = dbconnection
						.selectData("select one_d from v_index_drawdown_dates where one_d like '%"+request.getParameter("Q")+"%'");
				JSONArray jsonarray = new JSONArray();
				while (set.next()) {
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("value", set.getString("one_d"));
					jsonarray.put(set.getString("one_d"));
				}
				PrintWriter pwr = response.getWriter();
				pwr.print(jsonarray);
				System.out.println(jsonarray);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}
}
