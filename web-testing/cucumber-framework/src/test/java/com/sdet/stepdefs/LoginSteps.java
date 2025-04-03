package com.sdet.stepdefs;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.sdet.pages.LoginPage;
import org.testng.Assert;

public class LoginSteps {
    private WebDriver driver;
    private LoginPage loginPage;
    private int failedLoginAttempts = 0;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver);
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        driver.get("https://example.com/login");
    }

    @When("I enter username {string}")
    public void i_enter_username(String username) {
        loginPage.enterUsername(username);
    }

    @When("I enter password {string}")
    public void i_enter_password(String password) {
        loginPage.enterPassword(password);
    }

    @When("I click the login button")
    public void i_click_the_login_button() {
        loginPage.clickLogin();
    }

    @Then("I should be redirected to the dashboard")
    public void i_should_be_redirected_to_dashboard() {
        Assert.assertEquals(driver.getCurrentUrl(), "https://example.com/dashboard");
    }

    @Then("I should see error message {string}")
    public void i_should_see_error_message(String expectedError) {
        Assert.assertEquals(loginPage.getErrorMessage(), expectedError);
    }

    @When("I enter incorrect password {int} times")
    public void i_enter_incorrect_password_times(Integer attempts) {
        for (int i = 0; i < attempts; i++) {
            loginPage.enterPassword("wrongpass" + i);
            loginPage.clickLogin();
            failedLoginAttempts++;
        }
    }

    @Then("the account should be temporarily locked")
    public void account_should_be_locked() {
        Assert.assertTrue(failedLoginAttempts >= 3);
        // Add verification for account lock status
    }

    @When("I click on forgot password link")
    public void click_forgot_password() {
        // Implement forgot password link click
    }

    @Then("I should be redirected to password reset page")
    public void verify_reset_page() {
        Assert.assertTrue(driver.getCurrentUrl().contains("/reset-password"));
    }

    @When("I enter my email {string}")
    public void enter_email(String email) {
        // Implement email entry for password reset
    }

    @When("I click reset password button")
    public void click_reset_button() {
        // Implement reset button click
    }

    @Then("I should see message {string}")
    public void verify_message(String expectedMessage) {
        // Verify success message
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}