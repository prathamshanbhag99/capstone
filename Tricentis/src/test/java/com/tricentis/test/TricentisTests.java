package com.tricentis.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class TricentisTests {
    WebDriver driver;
    String firstName;
    String lastName;
    String email;
    String password;

    @BeforeClass
    public void setup() throws IOException {

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://demowebshop.tricentis.com/");
        String projectPath = System.getProperty("user.dir");
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(projectPath + "\\tricentis.properties");
        properties.load(input);
        firstName = properties.getProperty("FirstName");
        lastName = properties.getProperty("LastName");
        email = properties.getProperty("Email");
        password = properties.getProperty("Password");
    }


    @Test(priority = 1)
    public void verifyHomePageElements() {
        Assert.assertTrue(driver.findElement(By.id("small-searchterms")).isDisplayed(), "Search Store element is missing");
        Assert.assertTrue(driver.findElement(By.xpath("//div[@class='block block-category-navigation']//div[@class='title']")).isDisplayed(), "Category menu is missing");
        Assert.assertTrue(driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[1]/div[2]/div[1]/ul/li[1]/a")).isDisplayed(), "Register tab is missing");
        Assert.assertTrue(driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[1]/div[2]/div[1]/ul/li[2]/a")).isDisplayed(), "Login tab is missing");

    }

    @Test(priority = 2)
    public void verifyNewsletterInvalidEmail() {
        try {
            driver.findElement(By.id("newsletter-email")).sendKeys("InvalidEmail");
            driver.findElement(By.id("newsletter-subscribe-button")).click();
            WebElement successMessage = driver.findElement(By.xpath("//div[@id='newsletter-result-block']"));
            Thread.sleep(3000);
            if (successMessage.isDisplayed()) {
                Assert.assertTrue(successMessage.getText().contains("Thank you for signing up! A verification email has been sent. We appreciate your interest."));
            } else {

                throw new Exception("Success message not displayed.");
            }
        } catch (Exception e) {
            Assert.fail("Test failed: " + e.getMessage());
        }
    }


    @Test(priority = 3)
    public void verifyNewsletterSignup() throws InterruptedException {
        driver.findElement(By.id("newsletter-email")).clear();
        driver.findElement(By.id("newsletter-email")).sendKeys("test@example.com");
        driver.findElement(By.id("newsletter-subscribe-button")).click();
        WebElement successMessage = driver.findElement(By.xpath("//div[@id='newsletter-result-block']"));
        Thread.sleep(3000);
        Assert.assertTrue(successMessage.getText().contains("Thank you for signing up! A verification email has been sent. We appreciate your interest."));
    }

    @Test(priority = 4)
    public void registerUser() {
        driver.findElement(By.cssSelector("a.ico-register")).click();

        driver.findElement(By.id("gender-male")).click(); // Gender: Male
        driver.findElement(By.id("FirstName")).sendKeys(firstName);
        driver.findElement(By.id("LastName")).sendKeys(lastName);
        driver.findElement(By.id("Email")).sendKeys(email);
        driver.findElement(By.id("Password")).sendKeys(password);
        driver.findElement(By.id("ConfirmPassword")).sendKeys(password);
        driver.findElement(By.id("register-button")).click();
        WebElement successMessage = driver.findElement(By.className("result"));
        Assert.assertTrue(successMessage.getText().contains("Your registration completed"));
        driver.findElement(By.cssSelector("a.ico-logout")).click();
    }

    @Test(priority = 5)
    public void validateLoginWithIncorrectCredentials() {
        driver.findElement(By.cssSelector("a.ico-login")).click();
        driver.findElement(By.id("Email")).sendKeys("wrong@example.com");
        driver.findElement(By.id("Password")).sendKeys("wrongpassword");
        driver.findElement(By.cssSelector("input.button-1.login-button")).click();
        WebElement errorMessage = driver.findElement(By.cssSelector(".validation-summary-errors"));
        Assert.assertTrue(errorMessage.isDisplayed(), "Error message not displayed for incorrect login");
    }

    @Test(priority = 6)
    public void loginUser() {
        driver.findElement(By.cssSelector("a.ico-login")).click();
        driver.findElement(By.id("Email")).sendKeys(email);
        driver.findElement(By.id("Password")).sendKeys(password);
        driver.findElement(By.cssSelector("input.button-1.login-button")).click();
        WebElement accountLink = driver.findElement(By.cssSelector("a.account"));
        Assert.assertEquals(accountLink.getText(), email);
    }

    @Test(priority = 8)
    public void verifyRecentlyViewed() {
        Assert.assertTrue(driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div[2]/div[1]/strong")).isDisplayed(), "Recently viewed section is missing");
    }

    @Test(priority = 9)
    public void searchProduct() {
        driver.findElement(By.id("small-searchterms")).sendKeys("computer");
        driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[1]/div[3]/form/input[2]")).click();
        Assert.assertTrue(driver.findElement(By.xpath("//a[normalize-space()='Build your own cheap computer']")).isDisplayed(), "Product Not Found");
    }

    @Test(priority = 10)
    public void verifySortingByName()   {
        WebElement sortingDropdown = driver.findElement(By.id("products-orderby"));
        sortingDropdown.click();
        WebElement sortByNameOption = driver.findElement(By.xpath("//select[@id='products-orderby']/option[text()='Name: Z to A']"));
        sortByNameOption.click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.textToBePresentInElementLocated(
                        By.xpath("//select[@id='products-orderby']/option[@selected='selected']"), "Name: Z to A"));
        List<WebElement> productNames = driver.findElements(By.cssSelector(".product-title a"));
        List<String> actualNames = productNames.stream().map(WebElement::getText).collect(Collectors.toList());
        List<String> expectedNames = new ArrayList<>(actualNames);
        expectedNames.sort(Comparator.reverseOrder());
        Assert.assertEquals(actualNames, expectedNames, "The products are not sorted correctly by 'Name: Z to A'.");
    }

    @Test(priority = 12)
    public void verifyViewAsList() {
        WebElement viewDropdown = driver.findElement(By.id("products-viewmode"));
        viewDropdown.click();
        WebElement listView = driver.findElement(By.xpath("//select[@id='products-viewmode']/option[text()='List']"));
        listView.click();
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("viewmode=list"), "The URL does not reflect 'List' view.");
        List<WebElement> productItems = driver.findElements(By.cssSelector(".product-list"));
        Assert.assertTrue(productItems.size() > 0, "No products are displayed in 'List' view.");

    }

    @Test(priority = 13)
    public void verifyProduct() {
        driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div/div[2]/div[3]/div[1]/div[1]/div/div[2]/h2/a")).click();
        WebElement product = driver.findElement(By.xpath("//*[@id=\"product-details-form\"]/div/div[1]/div[2]/div[1]/h1"));
        Assert.assertTrue(product.getText().contains("Simple Computer"));

    }

    @Test(priority = 14)
    public void verifyAddToWishlist() {
        try {
            WebElement wishlistButton = driver.findElement(By.id("add-to-wishlist-button"));
            if (wishlistButton.isDisplayed()) {
                System.out.println("Test Passed: 'Add to Wishlist' button is visible.");
            } else {
                throw new Exception("Add to Wishlist Button is Missing");
            }
        } catch (Exception a) {

            Assert.fail("Test Failed:" + a.getMessage());
        }
    }


    @Test(priority = 15)
    public void addToCart() {
        WebDriverWait wait = null;
        driver.findElement(By.xpath("//*[@id=\"product_attribute_75_5_31_96\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"product_attribute_75_6_32_99\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"product_attribute_75_3_33_103\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"product_attribute_75_8_35_108\"]")).click();

        driver.findElement(By.id("add-to-cart-button-75")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement cartMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p.content")));
        Assert.assertTrue(cartMessage.getText().contains("The product has been added to your shopping cart"));
        System.out.println("Item has been added to the cart!");
    }

    @Test(priority = 16)
    public void changeProductQuantity() throws InterruptedException {
        Thread.sleep(6000);
        driver.findElement(By.xpath("//span[normalize-space()='Shopping cart']")).click();

        WebElement cartValueElement = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div/div/div[2]/div/form/div[2]/div[2]/div[1]/table/tbody/tr[4]/td[2]/span/span/strong"));
        String cartValueText = cartValueElement.getText().replace(",", "").replace("₹", "").trim();
        double oldCartValue = Double.parseDouble(cartValueText);

        int newQuantity=2;

        WebElement quantity = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div/div/div[2]/div/form/table/tbody/tr/td[5]/input"));
        quantity.clear();
        quantity.sendKeys(""+newQuantity);
        driver.findElement(By.xpath("//input[@name='updatecart']")).click();

        double expectedCartValue = oldCartValue * newQuantity;
        WebElement updatedCartValueElement = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div/div/div[2]/div/form/div[2]/div[2]/div[1]/table/tbody/tr[4]/td[2]/span/span/strong"));
        String updatedCartValueText = updatedCartValueElement.getText().replace(",", "").replace("₹", "").trim();
        double updatedCartValue = Double.parseDouble(updatedCartValueText);
        Assert.assertEquals(updatedCartValue, expectedCartValue);
    }


    @Test(priority = 17)
    public void deleteProduct() {
        driver.findElement(By.xpath("//input[@name='removefromcart']")).click();
        driver.findElement(By.xpath("//input[@name='updatecart']")).click();
        WebElement cartMessage = driver.findElement(By.xpath("//div[@class='order-summary-content']"));
        Assert.assertTrue(cartMessage.getText().contains("Your Shopping Cart is empty!"));


    }

    @Test(priority = 18)
    public void verifyForgotPasswordRedirect() {
        driver.findElement(By.cssSelector("a.ico-logout")).click();
        driver.findElement(By.cssSelector("a.ico-login")).click();
        driver.findElement(By.linkText("Forgot password?")).click();
        WebElement resetPasswordPage = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div/div[1]/h1"));
        Assert.assertTrue(resetPasswordPage.getText().contains("Password recovery"), "Forgot password link did not redirect correctly");
        driver.findElement(By.xpath("//input[@id='Email']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@name='send-email']")).click();
        WebElement confirmationMessage = driver.findElement(By.xpath("//div[@class='result']"));
        Assert.assertTrue(confirmationMessage.getText().contains("Email with instructions has been sent to you."));
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
