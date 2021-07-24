import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.Select;

public class Selenium4Test {

  WebDriver driver;

  @BeforeEach
  public void setupWebDriverManager() {
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    driver.manage().window().maximize();
  }

  @AfterEach
  public void quitWebDriver() {
    driver.quit();
  }

  @Test
  public void takeScreenShotSingleElement() throws IOException {
    driver.get("http://bdd.atlasid.tech/");
    WebElement inputQuote = driver.findElement(By.id("inputQuote"));
    inputQuote.sendKeys("There is a will there is a way");
    byte[] dataBytes = inputQuote.getScreenshotAs(OutputType.BYTES);
    //Java NIO Library
    Path path = Paths.get("build/singleElement.jpg");
    Files.write(path, dataBytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    driver.quit();
  }

  @Test
  public void takeScreenShotMultipleElement() throws IOException {
    driver.get("http://bdd.atlasid.tech/");
    WebElement container = driver.findElement(By.xpath("(//div[@class='row'])[2]"));
    byte[] dataBytes = container.getScreenshotAs(OutputType.BYTES);
    //Java NIO Library
    Path path = Paths.get("build/multiElement.jpg");
    Files.write(path, dataBytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  @Test
  public void openNewTab() {
    driver.get("http://bdd.atlasid.tech/");
    String windowHandle = driver.getWindowHandle();
    System.out.println("title: " + driver.getTitle());
    driver.switchTo().newWindow(WindowType.TAB);
    driver.get("https://www.atlasid.tech/");
    System.out.println("title: " + driver.getTitle());
    waitAbit(5);
    driver.close();
    driver.switchTo().window(windowHandle);
    System.out.println("title: " + driver.getTitle());
    waitAbit(5);
  }

  @Test
  public void openNewWindow() {
    driver.get("http://bdd.atlasid.tech/");
    String windowHandle = driver.getWindowHandle();
    System.out.println("title: " + driver.getTitle());
    driver.switchTo().newWindow(WindowType.WINDOW);
    driver.get("https://www.atlasid.tech/");
    System.out.println("title: " + driver.getTitle());
    waitAbit(5);
    driver.close();
    driver.switchTo().window(windowHandle);
    System.out.println("title: " + driver.getTitle());
    waitAbit(5);
  }

  @Test
  public void getRectFunction() {
    driver.get("http://bdd.atlasid.tech/");
    WebElement inputQuote = driver.findElement(By.xpath("(//div[@class='row'])[2]"));
    Rectangle rectangle = inputQuote.getRect();
    System.out.println(rectangle.getWidth());
    System.out.println(rectangle.getHeight());
    System.out.println(rectangle.getX());
    System.out.println(rectangle.getY());
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

    //relative locators
    WebElement quoteRow = driver
        .findElement(By.xpath("//td[contains(text(), 'There is a will there is a way')]"));
    //ngebug lol
    //WebElement colorData = driver.findElement(RelativeLocator.with(By.name("tableColumnColor")).toRightOf(quoteRow));
    WebElement colorData = driver
        .findElement(RelativeLocator.with(By.name("tableColumnColor")).near(quoteRow));
    System.out.println(colorData.getText());

  }


  public void waitAbit(int seconds) {
    try {
      Thread.sleep(Duration.ofSeconds(seconds).toMillis());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


}
