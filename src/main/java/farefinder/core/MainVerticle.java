package farefinder.core;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import rx.Single;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainVerticle extends AbstractVerticle {
	
	private static final String ADDRESS = "ping-address";
	private static final String HTTP_SEERVEER = "marketing.spirit.com";
	private static final String HTTP_PAGE_CONTEXT = "/traveldeals/traveldeals.php";
	private static final int HTTP_PORT = 80;
	private static final String MARKETING_URL_PREFIX = "http://marketing.spirit.com/traveldeals/air.php";

	public static void main(String[] args) {
		VertxOptions options = new VertxOptions();
		Vertx vertx = Vertx.vertx(options);
		vertx.deployVerticle(new MainVerticle());
	}

	@Override
	public void start() throws Exception {
		super.start();
		System.out.println("FareFinder Main Verticle is in the house!!!");
        //WEB CLIENT
        WebClient client = WebClient.create(vertx);
        Single<HttpResponse<String>> request = client.get(HTTP_PORT, HTTP_SEERVEER, HTTP_PAGE_CONTEXT)
          .as(BodyCodec.string())
          .rxSend(); 
        request.subscribe(resp -> parseBody(resp.body()));
	}

	private void parseBody(String body) {
		Document document = Jsoup.parse(body);
		document.select("a").stream()
		.map(aelement ->  aelement.attr("href") )
		.filter(url -> url.startsWith(MARKETING_URL_PREFIX))
		.map(unsanitizedUrl -> unsanitizedUrl.split("#")[0])
		.map(MainVerticle::stringToURL)
		.filter(url -> url!= null)
		.distinct()
		.forEach(this::anilizeAirFare);
	}

	public static URL stringToURL(String stringURL){
		try {
			return new URL(stringURL);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	private void anilizeAirFare(URL url) {
		System.out.println("Analizing...  " + url.toString());
//		WebClient client = WebClient.create(vertx);
//        Single<HttpResponse<String>> request = client.get(HTTP_PORT, HTTP_SEERVEER, url.getFile())
//          .as(BodyCodec.string())
//          .rxSend();
//        request.subscribe(resp -> analizeAirBody(resp.body())
//		);
	}

	private static void analizeAirBody(String body) {
		System.out.println("ANALIZING OFFER BODY!!!!!");
		
		Document document = Jsoup.parse(body);
		
		Pattern pattern = Pattern.compile("*PM");
		Matcher matcher = pattern.matcher(document.body().text());
		
		
		
		System.out.println(document.body().text());
		
		System.out.println(matcher.group(2));
	}

}
