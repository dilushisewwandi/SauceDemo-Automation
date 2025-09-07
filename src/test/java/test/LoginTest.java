package test;

import dataProviders.LoginDataProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.List;

public class LoginTest {
    WebDriver driver;
    SoftAssert softAssert = new SoftAssert();

    @BeforeMethod
    public void openLoginPage() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
    }

    private void login(String username, String password) {
        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.name("login-button")).click();
    }

    @Test(priority = 0, dataProvider = "validLoginData", dataProviderClass = LoginDataProvider.class)
    public void loginWithValidCredentials(String username, String password) {
        login(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        String currentURL = driver.getCurrentUrl();
        String expectedURL = "https://www.saucedemo.com/inventory.html";

        softAssert.assertEquals(currentURL, expectedURL, "User not redirected to inventory page after login");
        softAssert.assertAll();

    }

    @Test(priority = 1, dataProvider = "invalidPasswordData", dataProviderClass = LoginDataProvider.class)
    public void loginWithInvalidPassword(String username, String password) throws InterruptedException {
        login(username, password);

        WebElement errorMessage = driver.findElement(By.cssSelector("h3[data-test='error']"));
        softAssert.assertTrue(errorMessage.getText().contains("Epic sadface: Username and password do not match any user in this service"),
                "Error message not displayed correctly");

        String currentURL = driver.getCurrentUrl();
        String expectedURL = "https://www.saucedemo.com/";

        softAssert.assertTrue(currentURL.equals(expectedURL), "User should remain on login page");
        softAssert.assertAll();

    }

    @Test(priority = 2, dataProvider = "emptyFieldsData", dataProviderClass = LoginDataProvider.class)
    public void loginWithEmptyFields(String username, String password) {
        login(username, password);

        WebElement errorMessage = driver.findElement(By.cssSelector("h3[data-test='error']"));
        softAssert.assertTrue(errorMessage.getText().contains("Epic sadface: Username is required"), "Error message not displayed correctly");

        String currentURL = driver.getCurrentUrl();
        String expectedURL = "https://www.saucedemo.com/";

        softAssert.assertTrue(currentURL.equals(expectedURL), "User should remain on login page");
        softAssert.assertAll();

    }

    @Test(priority = 3, dataProvider = "lockedUserData", dataProviderClass = LoginDataProvider.class)
    public void lockedUserLogin(String username, String password) {
        login(username, password);

        WebElement errorMessage = driver.findElement(By.cssSelector("h3[data-test='error']"));
        softAssert.assertTrue(errorMessage.getText().contains("Epic sadface: Sorry, this user has been locked out."), "Error message not displayed correctly");

        String currentURL = driver.getCurrentUrl();
        String expectedURL = "https://www.saucedemo.com/";

        softAssert.assertTrue(currentURL.equals(expectedURL), "User should remain on login page");
        softAssert.assertAll();

    }

    @Test(priority = 4, dataProvider = "problemUserData", dataProviderClass = LoginDataProvider.class)
    public void problemUserTest(String username, String password){
        login("standard_user","secret_sauce");

        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(20));

        WebElement standardImage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='inventory_item_img']/a/img")));
        String imgSrc1 = standardImage.getAttribute("src");
        System.out.println("Standard Image src: " + imgSrc1);

        driver.navigate().back();

        wait.until(ExpectedConditions.urlContains("https://www.saucedemo.com/"));

        login(username,password);

        wait.until(ExpectedConditions.urlContains("https://www.saucedemo.com/inventory.html"));
        softAssert.assertTrue(driver.getCurrentUrl().contains("inventory.html"),"URL is incorrect after login");

        WebElement currentImage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='inventory_item_img']/a/img")));
        String imgSrc2 = currentImage.getAttribute("src");
        System.out.println("Current Image src: " + imgSrc2);

        softAssert.assertNotEquals(imgSrc1,imgSrc2,"This image can not be displayed after login problem user. Test failed");
        softAssert.assertAll();
    }

    @Test(priority = 5, dataProvider = "errorUserData", dataProviderClass = LoginDataProvider.class)
    public void errorUserLogin(String username, String password) throws InterruptedException {
        login(username,password);

        String currentURL = driver.getCurrentUrl();
        String expectedURL = "https://www.saucedemo.com/inventory.html";
        softAssert.assertEquals(currentURL, expectedURL, "User not redirected to inventory page after login");

        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(20));
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-bolt-t-shirt")));
        addToCartButton.click();

        List<WebElement> cartBadge = driver.findElements(By.xpath("//span[@class='shopping_cart_badge']"));

        if (cartBadge.isEmpty()){
            softAssert.assertTrue(true,"Cart badge not increased as expected for error user");
        }else {
            String cartBadgeCount = cartBadge.get(0).getText();
            int count  = Integer.parseInt(cartBadgeCount);
            softAssert.assertEquals(count,"Cart badge increased unexpectedly for error user");
        }
        softAssert.assertAll();
    }

    @Test(priority = 6, dataProvider = "performanceGlitchUserData", dataProviderClass = LoginDataProvider.class)
    public void performanceGlitchUserLogin(String username, String password){

        long startTime = System.currentTimeMillis();
        System.out.println(startTime);

        login(username,password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        long endTime = System.currentTimeMillis();
        System.out.println(endTime);
        long responseTime = (endTime - startTime)/1000;
        System.out.println(responseTime);

        softAssert.assertTrue(responseTime>=4 && responseTime<=7,"Login was not delayed as expected. Response time is: " + responseTime);

        String currentURL = driver.getCurrentUrl();
        String expectedURL = "https://www.saucedemo.com/inventory.html";
        softAssert.assertEquals(currentURL, expectedURL, "User not redirected to inventory page after login");
        softAssert.assertAll();
    }

    @AfterMethod
    public void closeBrowser(){
        driver.quit();
    }
}
