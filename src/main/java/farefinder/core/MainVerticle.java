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
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainVerticle extends AbstractVerticle {
	
	private static final String ADDRESS = "ping-address";
	private static final String HTTP_SEERVEER = "marketing.spirit.com";
	private static final String HTTP_PAGE_CONTEXT = "/traveldeals/traveldeals.php";
	private static final int HTTP_PORT = 80;

	public static void main(String[] args) {
		VertxOptions options = new VertxOptions();
		Vertx vertx = Vertx.vertx(options);
		vertx.deployVerticle(new MainVerticle());
	}

	@Override
	public void start() throws Exception {
		super.start();
		System.out.println("FareFinder Main Verticle is on the house!!!");
        
        //WEB CLIENT
        
        WebClient client = WebClient.create(vertx);
        Single<HttpResponse<String>> request = client.get(HTTP_PORT, HTTP_SEERVEER, HTTP_PAGE_CONTEXT)
          .as(BodyCodec.string())
          .rxSend();

        // Fire the request
        //request.subscribe(resp -> System.out.println("Server content " + resp.body()));

        // Again
        request.subscribe(resp -> parseBody(resp.body() )
		);

        // And again
//        request.subscribe(resp -> System.out.println("Server content " + resp.body()));
        
	}

	private void parseBody(String body) {
		
		Document document = Jsoup.parse(body);
		
		document.select("a").stream().forEach(aelement -> {
			String href = aelement.attr("href");
			if(href.contains("air.php")){
				anilizeAirFare(href);
			}
		});
		
	}

	private void anilizeAirFare(String href) {
		WebClient client = WebClient.create(vertx);
		
		URL url;
		try {
			url = new URL(href);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		
        Single<HttpResponse<String>> request = client.get(HTTP_PORT, HTTP_SEERVEER, url.getFile())
          .as(BodyCodec.string())
          .rxSend();
        request.subscribe(resp -> analizeAirBody(resp.body() )
		);
	}

	private void analizeAirBody(String body) {
		System.out.println("ANALIZING OFFER BODY!!!!!");
		System.out.println(body);
	}
	
	
}
