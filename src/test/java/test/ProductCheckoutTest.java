package test;

import dataProviders.CheckoutDataProvider;
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

public class ProductCheckoutTest {
    WebDriver driver;
    SoftAssert softAssert = new SoftAssert();


    @BeforeMethod
    public void openLoginPage(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
    }

    private void login(String username, String password) {
        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.name("login-button")).click();
    }

    private void checkoutContinue(String firstname, String lastname, String postalcode){
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(10));
        WebElement firstName = wait.until(ExpectedConditions.elementToBeClickable(By.id("first-name")));
        firstName.sendKeys(firstname);
        WebElement lastName = driver.findElement(By.id("last-name"));
        lastName.sendKeys(lastname);
        WebElement postalCode = driver.findElement(By.id("postal-code"));
        postalCode.sendKeys(postalcode);
        WebElement continueButton = driver.findElement(By.id("continue"));
        continueButton.click();
    }

    @Test(dataProvider = "checkoutData", dataProviderClass = CheckoutDataProvider.class)
    public void checkoutProduct(String username, String password, String firstname, String lastname, String postalcode) {
        login(username,password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        String currentUrl1 = driver.getCurrentUrl();
        String expectedUrl1 = "https://www.saucedemo.com/inventory.html";
        softAssert.assertEquals(currentUrl1, expectedUrl1, "User not redirected to the inventory page");

        driver.findElement(By.xpath("//div[normalize-space()='Sauce Labs Bike Light']"));
        //productItem.click();
//
//        wait.until(ExpectedConditions.urlContains("inventory-item.html?id=0"));
//        String currentUrl2 = driver.getCurrentUrl();
//        String expectedUrl2 = "https://www.saucedemo.com/inventory-item.html?id=0";
//        softAssert.assertEquals(currentUrl2, expectedUrl2, "User not redirected to the inventory item page");

        WebElement addToCartButton = driver.findElement(By.id("add-to-cart-sauce-labs-bike-light"));
        addToCartButton.click();

//        String buttonText = "Remove";
        // wait for "Remove" to appear
        WebElement removeButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("remove-sauce-labs-bike-light")));
        //String buttonText = driver.findElement(By.xpath("//button[contains(text(),'Remove')]")).getText();
        softAssert.assertEquals(removeButton.getText(), "Remove", "Button text not changed");

        WebElement cartBadge = driver.findElement(By.xpath("//div[@id='shopping_cart_container']/a/span"));
        //softAssert.assertNotNull(cartBadge.getText(),"Cart badge is not increasing when item add");
        softAssert.assertTrue(!cartBadge.getText().isEmpty(), "Cart badge is not increasing when item is added");

//        WebElement cartLink = driver.findElement(By.xpath("//div[@id='shopping_cart_container']/a"));
        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.className("shopping_cart_link")));
        cartLink.click();

        wait.until(ExpectedConditions.urlContains("cart.html"));
        String currentUrl3 = driver.getCurrentUrl();
        String expectedUrl3 = "https://www.saucedemo.com/cart.html";
        softAssert.assertEquals(currentUrl3, expectedUrl3, "User not redirected to the cart");

//        String item = new String();
//        WebElement cart_item = driver.findElement(By.xpath("(//div[@class='cart_item'])[1]"));
//        if (item.equals(cart_item)){
//            System.out.println("Added item previewed");
//        }else {
//            System.out.println("Incorrect item");
//        }
        WebElement cartItemName = driver.findElement(By.className("inventory_item_name"));
        softAssert.assertEquals(cartItemName.getText(), "Sauce Labs Bike Light", "Incorrect item in cart");

        WebElement checkoutButton = driver.findElement(By.id("checkout"));
        checkoutButton.click();

        wait.until(ExpectedConditions.urlContains("checkout-step-one.html"));
        String currentUrl4 = driver.getCurrentUrl();
        String expectedUrl4 = "https://www.saucedemo.com/checkout-step-one.html";
        softAssert.assertEquals(currentUrl4, expectedUrl4, "User not redirected to the checkout step one");

        checkoutContinue(firstname,lastname,postalcode);

        WebDriverWait wait1 = new WebDriverWait(driver,Duration.ofSeconds(15));
        wait1.until(ExpectedConditions.urlContains("checkout-step-two.html"));
        String currentUrl5 = driver.getCurrentUrl();
        String expectedUrl5 = "https://www.saucedemo.com/checkout-step-two.html";
        softAssert.assertEquals(currentUrl5, expectedUrl5, "User not redirected to the checkout step two");

        WebElement summaryTotal = driver.findElement(By.xpath("//div[@class='summary_total_label']"));
//        if (!summaryTotal.getText().contains("0.00")){
        softAssert.assertTrue(summaryTotal.getText().contains("Total"), "Summary total label missing");
        WebElement finishButton = driver.findElement(By.id("finish"));
        finishButton.click();

        wait.until(ExpectedConditions.urlContains("checkout-complete.html"));
        String currentUrl6 = driver.getCurrentUrl();
        String expectedUrl6 = "https://www.saucedemo.com/checkout-complete.html";
        softAssert.assertEquals(currentUrl6, expectedUrl6, "User not redirected to the checkout complete");

        WebElement checkoutCompleteHeader = driver.findElement(By.xpath("//h2[normalize-space()='Thank you for your order!']"));
        softAssert.assertTrue((checkoutCompleteHeader.getText().equalsIgnoreCase("Thank you for your order!")));

        System.out.println("Checkout process complete. Test passed");
        softAssert.assertAll();
    }

    @AfterMethod
    public void closeBrowser(){
        driver.quit();
    }
}
