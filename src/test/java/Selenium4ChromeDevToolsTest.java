import com.google.common.collect.ImmutableList;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v91.emulation.Emulation;
import org.openqa.selenium.devtools.v91.network.Network;
import org.openqa.selenium.devtools.v91.network.Network.GetResponseBodyResponse;
import org.openqa.selenium.devtools.v91.network.model.ConnectionType;
import org.openqa.selenium.devtools.v91.security.Security;
import org.openqa.selenium.remote.http.HttpResponse;

public class Selenium4ChromeDevToolsTest {

  WebDriver driver;
  //https://chromedevtools.github.io/devtools-protocol/tot/Network/#method-setBlockedURLs

  @BeforeEach
  public void setupWebDriverManager() {
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    driver.manage().window().maximize();
  }

  public void waitAbit(int seconds) {
    try {
      Thread.sleep(Duration.ofSeconds(seconds).toMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @AfterEach
  public void quitWebDriver() {
    driver.quit();
  }

  @Test
  public void allowExpiredCertificates() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    devtools.send(Security.setIgnoreCertificateErrors(Boolean.TRUE));
    driver.get("https://expired.badssl.com/");
    WebElement expiredLabel = driver.findElement(By.tagName("h1"));
    System.out.println(expiredLabel.getText());
    waitAbit(5);
  }

  @Test
  public void listenToConsole() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    devtools.getDomains().log().enable();
    devtools.getDomains().events()
        .addConsoleListener(consoleEvent -> System.out.println(consoleEvent.toString()));
    driver.get("http://bdd.atlasid.tech/");
    waitAbit(10);
  }

  @Test
  public void blockingRequest() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    devtools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    devtools.send(Network.setBlockedURLs(ImmutableList
        .of("https://api.mygostore.com/*",
            "*.css", "*.jpg", "*.jpeg")));
    driver.get("https://japfabest.mygostore.com/");
    waitAbit(5);
  }

  @Test
  public void setGeoLocationTest() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    devtools.send(Emulation
        .setGeolocationOverride(Optional.of(53.46471543617865), Optional.of(-2.291327904828742),
            Optional.of(100)));
    driver.get("http://maps.google.com/");
    waitAbit(4);
    WebElement buttonYourLocation = driver
        .findElement(By.xpath("//button[contains(@id,'mylocation')]"));
    buttonYourLocation.click();
    waitAbit(10);
  }

  @Test
  public void setTimezoneTest() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    devtools.send(Emulation.setTimezoneOverride("Europe/Amsterdam"));
    driver.get("https://webbrowsertools.com/timezone/");
    waitAbit(10);
  }


  @Test
  public void emulate2GNetwork() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    devtools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    //set 40kilobyte/s for upload/download, latency 5000 m/s
    devtools.send(Network.emulateNetworkConditions(Boolean.FALSE, 5000, 40000, 40000, Optional.of(
        ConnectionType.CELLULAR2G)));
    driver.get("https://www.tokopedia.com/");
    waitAbit(20);
  }

  @Test
  public void emulateOfflineNetwork() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    devtools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    //set 10megabyte/s for upload/download, latency 10 m/s
    driver.get("https://www.google.com/");
    devtools
        .send(Network.emulateNetworkConditions(Boolean.TRUE, 10, 10000000, 10000000, Optional.of(
            ConnectionType.CELLULAR4G)));
    driver.navigate().refresh();
    waitAbit(10);
  }

  @Test
  public void clearBrowsingCache() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    driver.get("https://refreshyourcache.com/en/cache-test/");
    devtools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    devtools.send(Network.clearBrowserCache());
    devtools.send(Network.setCacheDisabled(Boolean.TRUE));
    driver.navigate().refresh();
    waitAbit(10);
  }


  @Test
  public void clearCookies() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    devtools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    driver.get("http://bdd.atlasid.tech/");
    driver.manage().addCookie(new Cookie("test", "test"));
    devtools.send(Network.clearBrowserCookies());
    waitAbit(20);
  }

  @Test
  public void interceptNetworkAndChangeResponse() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    devtools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    driver.get("https://japfabest.mygostore.com/");
    devtools.getDomains().network().addRequestHandler(
        httpRequest -> httpRequest.getUri().contains("api.mygostore.com/catalog/v1/outlet"),
        httpRequest -> {
          HttpResponse response = new HttpResponse();
          response.setStatus(200);
          response
              .setContent(() -> new ByteArrayInputStream("{\"total\":0,\"content\":[]}".getBytes(
                  StandardCharsets.UTF_8)));
          return response;
        });
    driver.navigate().refresh();
    waitAbit(20);
  }

  @Test
  public void listenNetwork() {
    //initiate devtools
    DevTools devtools = ((ChromeDriver) driver).getDevTools();
    devtools.createSession();
    devtools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    devtools.addListener(Network.requestWillBeSent(), entry -> {
      if (entry.getRequest().getUrl().contains("api.mygostore.com/catalog/v1/outlet")) {
        System.out.println("Request (id) URL      : (" + entry.getRequestId() + ") "
            + entry.getRequest().getUrl()
            + " (" + entry.getRequest().getMethod() + ")");
      }
    });

    devtools.addListener(Network.responseReceived(), entry -> {
      if (entry.getResponse().getUrl().contains("api.mygostore.com/catalog/v1/outlet")) {
        System.out.println("Response (Req id) URL : (" + entry.getRequestId() + ") "
            + entry.getResponse().getUrl()
            + " (" + entry.getResponse().getStatus() + ")");
        GetResponseBodyResponse body = devtools
            .send(Network.getResponseBody(entry.getRequestId()));
        System.out.println(body.getBody());
      }
    });

    driver.get("https://japfabest.mygostore.com/");
    //need further research since this the function deprecated.
    driver.navigate().refresh();
    waitAbit(20);
  }
}
