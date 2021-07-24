import io.github.bonigarcia.wdm.WebDriverManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.Select;

public class SeleniumGridTest {

  WebDriver driver;

  @BeforeEach
  public void setupWebDriverManager() throws MalformedURLException {
    WebDriverManager.chromedriver().setup();
    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setCapability("browserName", "chrome");
    driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps);
    driver.manage().window().maximize();
  }

  @AfterEach
  public void quitWebDriver() {
    driver.quit();
  }

  @Test
  public void relativeLocators() {
    driver.get("http://bdd.atlasid.tech/");
    //adding a quote then go to table view
    WebElement inputQuote = driver.findElement(By.id("inputQuote"));
    inputQuote.sendKeys("There is a will there is a way");
    WebElement selectColor = driver.findElement(By.id("colorSelect"));
    Select select = new Select(selectColor);
    select.selectByVisibleText("Magenta");
    WebElement buttonAddQuote = driver.findElement(By.id("buttonAddQuote"));
    buttonAddQuote.click();
    WebElement tableViewTab = driver.findElement(By.id("tableView"));
    tableViewTab.click();
    Actions actions = new Actions(driver);
    WebElement buttonShowTable = driver.findElement(By.id("buttonShowTable"));
    actions.moveToElement(buttonShowTable).pause(Duration.of(2, ChronoUnit.SECONDS)).build()
        .perform();
    waitAbit(5);
//    //relative locators
//    WebElement quoteRow = driver
//        .findElement(By.xpath("//td[contains(text(), 'There is a will there is a way')]"));
//    //ngebug lol
//    //WebElement colorData = driver.findElement(RelativeLocator.with(By.name("tableColumnColor")).toRightOf(quoteRow));
//    WebElement colorData = driver
//        .findElement(RelativeLocator.with(By.name("tableColumnColor")).near(quoteRow));
//    System.out.println(colorData.getText());

  }

  public void waitAbit(int seconds) {
    try {
      Thread.sleep(Duration.ofSeconds(seconds).toMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
