package com.sdet.tests;

import com.sdet.pages.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;
import org.testng.Assert;

public class LoginTest {
    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode for CI/CD
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void navigateToLoginPage() {
        driver.get("https://example.com/login"); // Replace with your application URL
        loginPage = new LoginPage(driver);
    }

    @Test(description = "Verify successful login with valid credentials")
    public void testSuccessfulLogin() {
        loginPage.login("validUser", "validPassword");
        // Add assertions for successful login
        Assert.assertEquals(driver.getCurrentUrl(), "https://example.com/dashboard");
    }

    @Test(description = "Verify error message with invalid credentials")
    public void testInvalidLogin() {
        loginPage.login("invalidUser", "invalidPassword");
        Assert.assertEquals(loginPage.getErrorMessage(), "Invalid username or password");
    }

    @Test(description = "Verify empty username validation")
    public void testEmptyUsername() {
        loginPage.login("", "somePassword");
        Assert.assertEquals(loginPage.getErrorMessage(), "Username is required");
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][] {
            {"user1", "pass1", "Invalid credentials"},
            {"user2", "", "Password is required"},
            {"", "", "Username and password are required"}
        };
    }

    @Test(dataProvider = "loginData", description = "Verify multiple login scenarios")
    public void testMultipleLoginScenarios(String username, String password, String expectedError) {
        loginPage.login(username, password);
        Assert.assertEquals(loginPage.getErrorMessage(), expectedError);
    }

    @AfterMethod
    public void takeScreenshotOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            // Add screenshot capture logic here
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}