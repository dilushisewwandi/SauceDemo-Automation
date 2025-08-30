package test;

import dataProviders.LoginDataProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.ArrayList;
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

        System.out.println("Login Successful!");
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

        System.out.println("Invalid password test is passed");
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

        System.out.println("Empty fields test is passed");
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

        System.out.println("Locked out user test is passed");
    }

    @Test(priority = 4, dataProvider = "problemUserData", dataProviderClass = LoginDataProvider.class)
    public void problemUserLogin(String username, String password) throws InterruptedException {
        login("standard_user", "secret_sauce");

        List<WebElement> standardImages = driver.findElements(By.xpath("//div[@class='inventory_item_img']/a/img"));
        List<String> expectedImages = new ArrayList<>();
        for (WebElement images: standardImages
             ) {
            expectedImages.add(images.getAttribute("src"));
        }
        Thread.sleep(3000);

        driver.get("https://www.saucedemo.com/");
        login(username, password);


        String currentURL = driver.getCurrentUrl();
        String expectedURL = "https://www.saucedemo.com/inventory.html";
        softAssert.assertEquals(currentURL, expectedURL, "User not redirected to inventory page after login");

        List<WebElement> productImages = driver.findElements(By.xpath("//div[@class='inventory_item_img']/a/img"));
        List<String> actualImages = new ArrayList<>();

        for (WebElement images : productImages
        ) {
            actualImages.add(images.getAttribute("src"));
        }

        if (actualImages.equals(expectedImages)) {
            System.out.println("Problem user test failed");
        } else {
            System.out.println("Problem user test passed");
        }

        softAssert.assertAll();
    }

    @Test(priority = 5, dataProvider = "errorUserData", dataProviderClass = LoginDataProvider.class)
    public void errorUserLogin(String username, String password) throws InterruptedException {
        login(username,password);

        String currentURL = driver.getCurrentUrl();
        String expectedURL = "https://www.saucedemo.com/inventory.html";
        softAssert.assertEquals(currentURL, expectedURL, "User not redirected to inventory page after login");

        Thread.sleep(3000);
        WebElement addtoCartButtton = driver.findElement(By.id("add-to-cart-sauce-labs-bolt-t-shirt"));
        addtoCartButtton.click();

        List<WebElement> cartBadgeCount = driver.findElements(By.xpath("//span[@class='shopping_cart_badge']"));

        if (cartBadgeCount.size()>0){
            System.out.println("Add to cart buttons work properly");
        }
        else {
            System.out.println("Add to cart buttons not work properly");
        }
    }

    @Test(priority = 6, dataProvider = "performanceGlitchUserData", dataProviderClass = LoginDataProvider.class)
    public void performanceGlitchUserLogin(String username, String password){

        long startTime = System.currentTimeMillis();

        login(username,password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        long endTime = System.currentTimeMillis();
        long responseTime = (endTime - startTime)/1000;

        if (responseTime<3){
            Assert.fail("Response time is " + responseTime + " Login was too fast. Test Failed");
        } else if (responseTime>=3 && responseTime<=6) {
            System.out.println("Response time is " + responseTime + " Login delayed as expected. Test Passed");
        }else {
            Assert.fail("Response time is " + responseTime + " Login was too slow. Test Failed");
        }

        String currentURL = driver.getCurrentUrl();
        String expectedURL = "https://www.saucedemo.com/inventory.html";
        softAssert.assertEquals(currentURL, expectedURL, "User not redirected to inventory page after login");
        softAssert.assertAll();

    }

    @Test(priority = 7, dataProvider = "visualUserData", dataProviderClass = LoginDataProvider.class)
    public void visualUserLogin(String username, String password){
        login(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        String currentURL = driver.getCurrentUrl();
        String expectedURL = "https://www.saucedemo.com/inventory.html";
        softAssert.assertEquals(currentURL, expectedURL, "User not redirected to inventory page after login");
//
//        WebElement pricebar = driver.findElement(By.xpath("//div[@class='pricebar']"));
//        pricebar.getSize();
//        WebElement itemPrice = driver.findElement(By.xpath("//div[@class='inventory_item_price']"));
//        itemPrice.getSize();
//        WebElement addToCartButton = driver.findElement(By.xpath("//button[@id='add-to-cart-sauce-labs-onesie']"));
//        addToCartButton.getSize();
//
//        long spaceBetween = pricebar.getSize() - ((itemPrice.getSize())+(addToCartButton.getSize()));
//        System.out.println("Space between is " + spaceBetween);
//
//        if (spaceBetween>3){
//            System.out.println("Elements are overlapped. Visual user login test passed");
//        }else {
//            Assert.fail();
//        }
//
//        softAssert.assertAll();
        WebElement itemPrice = driver.findElement(By.xpath("//body[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[6]/div[2]/div[2]/div[1]"));
        WebElement addToCartButton = driver.findElement(By.xpath("//button[@id='add-to-cart-test.allthethings()-t-shirt-(red)']"));

        int itemPriceX = itemPrice.getLocation().getX();
        int addToCartButtonX = addToCartButton.getLocation().getX();

        int spaceBetween = addToCartButtonX-itemPriceX;

        System.out.println("Space between Item Price and Add to Cart Button is: " + spaceBetween + "px");

        // Validate layout consistency
        if (spaceBetween > 512) { // threshold, adjust if needed
            Assert.fail("FAIL: Space is too large (" + spaceBetween + "px). Button may overlap.");
        } else {
            System.out.println("PASS: Layout is consistent. No overlap detected.");
        }

        softAssert.assertAll();
    }

    @AfterMethod
    public void closeBrowser(){
        driver.quit();
    }
}
