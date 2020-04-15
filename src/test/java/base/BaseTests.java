package base;

import com.google.common.io.Files;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pages.HomePage;
import utils.EventReporter;
import utils.WindowManager;

import java.io.File;
import java.io.IOException;

public class BaseTests
{
    //private WebDriver driver;
    private EventFiringWebDriver driver;
    protected HomePage homePage;

    @BeforeClass
    public void setUp()
    {
        System.setProperty("webdriver.chrome.driver","resources/chromedriver");
        driver = new EventFiringWebDriver(new ChromeDriver(getChromeOptions()));
        driver.register(new EventReporter( ));
        goHome();
        setCookie();
    }

    @BeforeMethod
    public void goHome()
    {
        driver.get("https://the-internet.herokuapp.com/");
        homePage = new HomePage(driver);
    }

    @AfterClass
    public void tearDown()
    {
        driver.quit();
    }

    @AfterMethod
    public void recordFailTests(ITestResult testResult)
    {
        if (ITestResult.FAILURE == testResult.getStatus())
        {
            var camera = (TakesScreenshot)driver;
            File screenshot = camera.getScreenshotAs(OutputType.FILE);
            try
            {
                Files.move(screenshot, new File("resources/screenshots/" + testResult.getName() + ".png"));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public WindowManager getWindowManager()
    {
        return new  WindowManager(driver);
    }

    private ChromeOptions getChromeOptions()
    {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        //options.setHeadless(true);
        return options;
    }

    private void setCookie()
    {
        //creates cookie
        Cookie cookie =  new Cookie.Builder("TAU", "123")
                .domain("the-internet.herokuapp.com")
                .build();

        //set cookie
        driver.manage().addCookie(cookie );
    }
}
