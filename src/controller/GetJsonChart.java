package controller;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.DAO;
import model.Crawler;

@WebServlet("/GetJsonChart")
public class GetJsonChart extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GetJsonChart() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json; charset=utf-8");
		String currency = request.getParameter("currency");
		Crawler crawler = new Crawler(
				"https://www.esunbank.com.tw/bank/personal/deposit/rate/forex/exchange-rate-chart?Currency=" + currency
						+ "/TWD");

		try (Writer out = response.getWriter();) {

			if (!DAO.chkData(currency)) {
				crawler.getPage();
				DAO.insertData(crawler.getExchangeRates().toJSONString(), currency);
			}
			out.write(DAO.queryData(currency).toString());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
