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

public class AddToCartTest {
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

//    @Test
//    public void addToCart(){
//        login("standard_user", "secret_sauce");
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        wait.until(ExpectedConditions.urlContains("inventory.html"));
//
//        List<WebElement> addtoCartbuttons = driver.findElements(By.xpath("//div[@class='pricebar']/button"));
//
//        for (WebElement buttons: addtoCartbuttons
//             ) {
//            buttons.click();
//        }
////        WebElement addtoCartButton = driver.findElement(By.xpath("//button[@id='add-to-cart-sauce-labs-backpack']"));
////        addtoCartButton.click();
//
//        String currentUrl = driver.getCurrentUrl();
//        String expectedUrl = "https://www.saucedemo.com/inventory.html";
//        softAssert.assertEquals(currentUrl,expectedUrl,"User not redirected to inventory page after login");
//
//        WebElement cartBadge = driver.findElement(By.xpath("//span[@class='shopping_cart_badge']"));
//        int badgeCount = Integer.parseInt(cartBadge.getText());
//        Assert.assertEquals(badgeCount,productCount,"Cart badge count mismatch!");
//
//        softAssert.assertAll();
//
//        List<WebElement> cartBadgeCount = driver.findElements(By.xpath("//span[@class='shopping_cart_badge']"));
//        if (cartBadgeCount.size()>0){
//            System.out.println("Add to cart function works properly. Test passed");
//        }else {
//            Assert.fail();
//        }
//    }
////    WebElement cartLink = driver.findElement(By.xpath("//a[@class='shopping_cart_link']"));
////            cartLink.click();
////    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
////            wait.until(ExpectedConditions.urlContains("inventory.html"));
//}
    @Test(priority = 0, dataProvider = "validLoginData", dataProviderClass = LoginDataProvider.class)
    public void addToCart(String username, String password) throws InterruptedException {
            login(username, password);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("inventory.html"));

//            List<WebElement> addtoCartbuttons = driver.findElements(By.xpath("//div[@class='pricebar']/button"));
//            for (WebElement buttons: addtoCartbuttons ) {
//                buttons.click();
//            }

            List<WebElement> addtoCartButtons = driver.findElements(By.xpath("//div[@class='pricebar']/button"));
            int clickedButtonCount = 0;
            for (WebElement buttons: addtoCartButtons ) {
                if (buttons.getText().equalsIgnoreCase("Add to Cart")){
                   buttons.click();
                   clickedButtonCount++;
                }
            }

            WebElement cartBadgeCount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='shopping_cart_badge']")));
            int count = Integer.parseInt(cartBadgeCount.getText());
            if (count == clickedButtonCount) {
                System.out.println("Add to cart function works properly. Test passed");
            }else {
                System.out.println("Test failed");;
            }
        }

    @Test(priority = 1, dataProvider = "validLoginData", dataProviderClass = LoginDataProvider.class)
    public void removeItemFromInventoryPage(String username, String password){
        login(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        List<WebElement> allButtons = driver.findElements(By.xpath("//div[@class='pricebar']/button"));
        for (WebElement removeButtons: allButtons ) {
            if (removeButtons.getText().equalsIgnoreCase("Remove")){
                removeButtons.click();
            }
        }

//        WebElement cartBadgeCount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='shopping_cart_badge']")));
//        int count = Integer.parseInt(cartBadgeCount.getText());
//        if (count<=0) {
//            System.out.println("Remove from cart function works properly. Test passed");
//        }else {
//            System.out.println("Test failed");;
//        }
        List<WebElement> cartBadgeCount = driver.findElements(By.xpath("//span[@class='shopping_cart_badge']"));
        if (cartBadgeCount.isEmpty()){
            System.out.println("Remove from cart function works properly. Test passed");
        }else {
            System.out.println("Test failed");;
        }
    }

    @Test(priority = 2, dataProvider = "validLoginData", dataProviderClass = LoginDataProvider.class)
    public void removeItemFromCart(String username, String password){
        login(username, password);

        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        List<WebElement> cartBadge = driver.findElements(By.xpath("//span[@class='shopping_cart_badge']"));
        if (cartBadge.isEmpty()){
           List<WebElement> allButtons = driver.findElements(By.xpath("//div[@class='pricebar']/button"));
//           for (WebElement addtoCartButtons: allButtons ) {
//               if (addtoCartButtons.getText().equalsIgnoreCase("Add to cart")){
//                   addtoCartButtons.click();
//               }
           allButtons.get(0).click();
           }

        WebElement cartLink = driver.findElement(By.xpath("//div[@id='shopping_cart_container']/a"));
        cartLink.click();

        wait.until(ExpectedConditions.urlContains("cart.html"));

        String currentUrl = driver.getCurrentUrl();
        String expectedUrl = "https://www.saucedemo.com/cart.html";
        softAssert.assertEquals(currentUrl,expectedUrl,"User not redirected to cart");

        List<WebElement> removeButtons = driver.findElements(By.xpath("//div[@class='item_pricebar']/button"));
        for (WebElement buttons:removeButtons) {
            buttons.click();
        }

        cartBadge = driver.findElements(By.xpath("//span[@class='shopping_cart_badge']"));
           if (cartBadge.isEmpty()){
               System.out.println("Item remove from cart successfully. Test passed");
           }else {
               System.out.println("Test failed");
           }
           softAssert.assertAll();
    }

    @AfterMethod
    public void closeBrowser(){
        driver.quit();
    }

}