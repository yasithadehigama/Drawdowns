package lsf.drawdowns.dbCon;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.*;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.transform.Transformers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.Test;


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
				System.out.println(request.getParameter("M"));

				String xx = "SELECT x.PERMNO_date AS PERMNO,x.CAPM_resid_date AS CAPM_resid_D FROM (SELECT PERMNO_date,YRMO_date,CAPM_resid_date FROM capm_drawdowns_date WHERE capm_drawdowns_date.HORIZON=1 AND YRMO_date='"
						+ request.getParameter("Q")
						+ request.getParameter("M")
						+ "') AS x , (SELECT PERMNO,YRMO,CAPM_resid FROM capm_drawdowns_results WHERE capm_drawdowns_results.HORIZON=1 AND YRMO='"
						+ request.getParameter("Q")
						+ request.getParameter("M")
						+ "') AS y WHERE x.PERMNO_date = y.PERMNO AND x.YRMO_date=y.YRMO ORDER BY y.CAPM_resid";
				ResultSet set = dbconnection.selectData(xx);

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
							e.printStackTrace();
						}
						jsonarray.put(jsonobj);
					}
				}
				System.out.println();
				System.out.println(jsonarray);
				PrintWriter pwr = response.getWriter();
				pwr.print(jsonarray);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// dbconnection.con.close();
			}
		} else if (userPath.equals("/rangeData")) {

			try {
				
			//	String sql ="SELECT * FROM(SELECT v1.*, @counter := @counter +1 AS counter FROM (select @counter:=0) AS initvar, v1) AS X where counter <= (10/100 * @counter) AND YRMO BETWEEN 200401 AND 200412";
				
				String xx = "SELECT x.PERMNO_date AS PERMNO,x.CAPM_resid_date AS CAPM_resid_D FROM (SELECT PERMNO_date,YRMO_date,CAPM_resid_date,@counter := @counter +1 AS counter FROM (select @counter:=0) AS initvar,capm_drawdowns_date WHERE capm_drawdowns_date.HORIZON=1 AND YRMO_date='"
						+ request.getParameter("Q")
						+ request.getParameter("M")
						+ "') AS x , (SELECT PERMNO,YRMO,CAPM_resid FROM capm_drawdowns_results WHERE capm_drawdowns_results.HORIZON=1 AND YRMO='"
						+ request.getParameter("Q")
						+ request.getParameter("M")
						+ "') AS y WHERE counter <= (10/100 * @counter) AND  x.PERMNO_date = y.PERMNO AND x.YRMO_date=y.YRMO ORDER BY y.CAPM_resid";
				ResultSet set = dbconnection.selectData(xx);

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
							e.printStackTrace();
						}
						jsonarray.put(jsonobj);
					}
				}
				System.out.println();
				System.out.println(jsonarray);
				PrintWriter pwr = response.getWriter();
				pwr.print(jsonarray);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// dbconnection.con.close();
			}

		} else if (userPath.equals("/summaryData")) {
			try {
				String sql = null;
				if (request.getParameter("D").equals("caff")) {
					sql = "SELECT YEAR(date_withyear) AS date,COUNT(YEAR(date_withyear)) AS count FROM caaf_drawdownend GROUP BY YEAR(date_withyear)";
				} else {
					sql = "SELECT YEAR(CAPM_resid_date) AS date,COUNT(YEAR(CAPM_resid_date)) AS count FROM capm_drawdowns_date GROUP BY YEAR(CAPM_resid_date)";
				}
				ResultSet set = dbconnection.selectData(sql);

				ArrayList<Integer> aryCount = new ArrayList<Integer>();
				ArrayList<Integer> aryYear = new ArrayList<Integer>();

				while (set.next()) {
					if (set.getInt("date") == 0) {

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
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (userPath.equals("/indexData")) {
			System.out.println("indexData method");
			try {
				// ResultSet set =
				// dbconnection.selectData("select Index_dates,Index_values from indexDrawdown where Year='"+
				// request.getParameter("Q") + "'");

				// ResultSet set =
				// dbconnection.selectData("SELECT mergedata.one AS Index_values,mergedata.one_d AS Index_dates FROM mergedata WHERE mergedata.permno = 0 AND mergedata.one_d LIKE '%"+
				// request.getParameter("Q") + "%'");
				ResultSet set = dbconnection
						.selectData("SELECT B.date_withyear AS Index_dates,A.value1 AS Index_values FROM ( SELECT  permno, value1,yrmo FROM caaf_drawdowns WHERE  permno=0 AND yrmo LIKE '"
								+ request.getParameter("Q")
								+ "%') AS  A  JOIN (SELECT  permno_end,date_withyear,yrmo_end FROM  caaf_drawdownend WHERE permno_end=0 AND yrmo_end LIKE '"
								+ request.getParameter("Q")
								+ "%') AS  B ON A.permno=B.permno_end AND A.yrmo=B.yrmo_end");
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
				System.out.println(obj);
			} catch (SQLException | JSONException e) {
				e.printStackTrace();
			}
		} else if (userPath.equals("/index")) {
			/*
			 * try { ResultSet set = dbconnection.selectData(
			 * "SELECT B.date_withyear AS Index_dates FROM ( SELECT  permno, value1,yrmo FROM caaf_drawdowns WHERE  permno=0 AND yrmo LIKE '"
			 * + request.getParameter("Q") +
			 * "%') AS  A  JOIN (SELECT  permno_end,date_withyear,yrmo_end FROM  caaf_drawdownend WHERE permno_end=0 AND yrmo_end LIKE '"
			 * + request.getParameter("Q") +
			 * "%') AS  B ON A.permno=B.permno_end AND A.yrmo=B.yrmo_end");
			 * ArrayList<String> aryDate = new ArrayList<String>(); JSONArray
			 * jsonarray = new JSONArray(); while(set.next()){ JSONObject obj =
			 * new JSONObject();
			 * 
			 * obj.put("value", set.getString("Index_dates"));
			 * jsonarray.put(set.getString("Index_dates"));
			 * //jsonarray.put(obj); } PrintWriter pwr = response.getWriter();
			 * pwr.print(jsonarray); } catch (Exception e) { // TODO: handle
			 * exception }
			 */
			/*
			 * try {
			 * 
			 * ResultSet set = dbconnection .selectData(
			 * "select one_d from v_index_drawdown_dates where one_d like '%" +
			 * request.getParameter("Q") + "%'"); JSONArray jsonarray = new
			 * JSONArray(); while (set.next()) { JSONObject jsonobj = new
			 * JSONObject(); jsonobj.put("value", set.getString("one_d"));
			 * jsonarray.put(set.getString("one_d")); } PrintWriter pwr =
			 * response.getWriter(); pwr.print(jsonarray);
			 * System.out.println(jsonarray); } catch (SQLException e) {
			 * e.printStackTrace(); }
			 */
		} else if (userPath.equals("/test_getSet")) {
			PrintWriter pwr = response.getWriter();
			JSONObject obj = new JSONObject();
					
			SessionFactory SFact = new Configuration().configure().buildSessionFactory();
			Session session = SFact.openSession();
			session.beginTransaction();
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");			
			String all_drawdown ="SELECT x.PERMNO AS permno,x.YRMO AS yrmo,x.CAPM_resid AS drawdownValue,y.CAPM_resid_date AS drawdownDate,x.value1 AS marketCapitalization,y.returnValue FROM ( SELECT A.PERMNO, A.YRMO, A.CAPM_resid, B.value1 FROM ( SELECT * FROM capm_drawdowns_results WHERE YRMO LIKE '2004%' AND HORIZON = 1) AS A INNER JOIN ( SELECT permno, yrmo, value1 FROM caaf_marketcapitalization WHERE yrmo LIKE '2004%') AS B ON A.PERMNO = B.permno ) AS x INNER JOIN (SELECT K.PERMNO_date,K.YRMO_date,K.CAPM_resid_date,L.value1 AS returnValue FROM (SELECT PERMNO_date,YRMO_date,CAPM_resid_date FROM capm_drawdowns_date WHERE YRMO_date LIKE '2004%' AND HORIZON = 1) AS K INNER JOIN (SELECT permno,yrmo,value1 FROM caaf_returns WHERE yrmo LIKE '2004%') AS L ON K.PERMNO_date=L.permno AND K.YRMO_date=L.yrmo) AS y ON y.PERMNO_date = x.PERMNO AND y.YRMO_date = x.yrmo ORDER by y.CAPM_resid_date";
			SQLQuery q = session.createSQLQuery(all_drawdown);
			q.setResultTransformer(Transformers.aliasToBean(Drawdown.class));			
			List<Drawdown> result=q.list();
			
			/** red bar */
			String empDate[] = getEndOfMonth(result);
			double empValue[] = getEndOfMonthLossMC(result,empDate);
			/** end of red bar*/
			
			
			
			
			session.getTransaction().commit();

		}
	}

	

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}
	String[] getEndOfMonth(List<Drawdown> result){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String empDate[] = new String[12];
		for (Iterator iterator = result.iterator(); iterator.hasNext();) {
			Drawdown drawdown = (Drawdown) iterator.next();
			try {
				if (drawdown.getDrawdownDate().equals("")) {					
				}else{
					Date date1 = df.parse(drawdown.getDrawdownDate());
					Calendar cal = Calendar.getInstance();
					cal.setTime(date1);
					int month = cal.get(Calendar.MONTH) + 1;
					switch(month){
					case 1: empDate[0]=df.format(date1);
					case 2: empDate[1]=df.format(date1);
					case 3: empDate[2]=df.format(date1);
					case 4: empDate[3]=df.format(date1);
					case 5: empDate[4]=df.format(date1);
					case 6: empDate[5]=df.format(date1);
					case 7: empDate[6]=df.format(date1);
					case 8: empDate[7]=df.format(date1);
					case 9: empDate[8]=df.format(date1);
					case 10: empDate[9]=df.format(date1);
					case 11: empDate[10]=df.format(date1);
					case 12: empDate[11]=df.format(date1);
					
					}						
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return empDate;
	}
	private double[] getEndOfMonthLossMC(List<Drawdown> result, String[] empDate) {
		double empValue[] = new double[12];
		for (Iterator iterator = result.iterator(); iterator.hasNext();) {
			Drawdown drawdown = (Drawdown) iterator.next();
			String check = drawdown.getDrawdownDate();
			if (drawdown.getDrawdownDate().equals(empDate[0])) {
				empValue[0]=empValue[0]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[1])) {
				empValue[1]=empValue[1]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[2])) {
				empValue[2]=empValue[2]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[3])) {
				empValue[3]=empValue[3]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[4])) {
				empValue[4]=empValue[4]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[5])) {
				empValue[5]=empValue[5]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[6])) {
				empValue[6]=empValue[6]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[7])) {
				empValue[7]=empValue[7]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[8])) {
				empValue[8]=empValue[8]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[9])) {
				empValue[9]=empValue[9]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[10])) {
				empValue[10]=empValue[10]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			} else if (drawdown.getDrawdownDate().equals(empDate[11])) {
				empValue[11]=empValue[11]+(drawdown.getMarketCapitalization()*(1-drawdown.getReturnValue().doubleValue()));
			}
		}
		return empValue;
		
	}
}