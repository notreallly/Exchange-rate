import DAO.DAO;
import model.Crawler;

public class Main {

	public static void main(String[] args) {
		Crawler crawler = new Crawler(
				"https://www.esunbank.com.tw/bank/personal/deposit/rate/forex/exchange-rate-chart?Currency=JPY/TWD");
		
		try {
			crawler.getPage();
			DAO.insertData(crawler.getExchangeRates().toJSONString(), "JPY");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
