package model;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Crawler {
	private String url;
	private JSONArray exchangeRates;

	public Crawler(String url) {
		this.url = url;
		exchangeRates = new JSONArray();
	}

	public JSONArray getExchangeRates() {
		return exchangeRates;
	}

	public void getPage() throws IOException {
		try (WebClient webClient = new WebClient(BrowserVersion.CHROME);) {
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
			java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);

			HtmlPage page = webClient.getPage(url);
			webClient.waitForBackgroundJavaScript(10000);
			int totalPage = getTotalPage(page.getFirstByXPath("//div[@id='pagelist']/div[4]/a").toString());
			for (int i = 1; i <= totalPage; i++) {
				System.out.println("page: " + i);
				if (i != 1) {
					HtmlElement btn = (HtmlElement) page.getFirstByXPath("//div[@id='pagelist']/div[3]/a");
					page = (HtmlPage) btn.click();
					webClient.waitForBackgroundJavaScript(10000);
				}
				getJson(page.asXml());
			}
		}
	}

	private void getJson(String page) {
		Document doc = Jsoup.parse(page);
		Elements tr = doc.select("#inteTable tbody").get(0).children();
		JSONObject exchangeRate;

		for (Element element : tr) {
			Elements td = element.children();
			exchangeRate = new JSONObject(new LinkedHashMap());

			if (isCurrencyName(td.get(0).html())) {
				continue;
			} else if (!isTitle(td.get(0).html())) {
				exchangeRate.put("DATE", td.get(0).html());
				exchangeRate.put("BUY", td.get(1).html());
				exchangeRate.put("SELL", td.get(2).html());
			} else {
				continue;
			}
			exchangeRates.add(exchangeRate);
		}
	}

	private boolean isCurrencyName(String text) {
		String pattern = "^.+\\(.+\\)$";
		return text.matches(pattern);
	}

	private boolean isTitle(String text) {
		return text.equals("日期");
	}

	private int getTotalPage(String str) {
		Pattern pattern = Pattern.compile(".*onclick=\"GotoPage\\(([0-9]+)\\).*");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return Integer.parseInt(matcher.group(1));
		}
		return -1;
	}
}
