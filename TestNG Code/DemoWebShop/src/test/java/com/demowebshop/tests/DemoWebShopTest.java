package com.demowebshop.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.demowebshop.pages.*;
import com.demowebshop.utils.ExtentReport;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;


public class DemoWebShopTest {
    static WebDriver driver;
    static ExtentReports extent;
    static HomePage homePage;
    static RegistrationPage registrationPage;
    static LoginPage loginPage;
    static ProductPage productPage;
    static CartPage cartPage;
    static String firstName;
    static String lastName;
    static String email;
    static String password;
    ExtentTest test;

    @BeforeClass
    public static void setUp() throws IOException {

        extent = ExtentReport.getReportInstance();

        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        driver.manage().window().maximize();
        driver.get("https://demowebshop.tricentis.com/");

//        WebDriverManager.chromedriver().setup();
//        driver =new ChromeDriver();
//        driver.manage().window().maximize();
//        driver.get("https://demowebshop.tricentis.com/");

        homePage = new HomePage(driver);
        registrationPage = new RegistrationPage(driver);
        loginPage = new LoginPage(driver);
        productPage = new ProductPage(driver);
        cartPage = new CartPage(driver);

        String projectPath = System.getProperty("user.dir");
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(projectPath + "/src/main/resources/tricentis.properties");
        properties.load(input);
        firstName = properties.getProperty("FirstName");
        lastName = properties.getProperty("LastName");
        email = properties.getProperty("Email");
        password = properties.getProperty("Password");


    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
        extent.flush();
    }

    @Test(priority = 1)
    public void test1VerifyHomePageElements() {
        test = extent.createTest("Verify Home Page Elements");
        Assert.assertTrue(homePage.isSearchStoreDisplayed(), "Search Store element is missing");
        Assert.assertTrue(homePage.isCategoryMenuDisplayed(), "Category menu is missing");
        Assert.assertTrue(homePage.isRegisterTabDisplayed(), "Register tab is missing");
        Assert.assertTrue(homePage.isLoginTabDisplayed(), "Login tab is missing");
        test.pass("All Home Page elements are displayed correctly");
    }

    @Test(priority = 2)
    public void test2VerifyNewsletterInvalidEmail() throws IOException {
        test = extent.createTest("Verify Newsletter with Invalid Email");
        try {
            // Assuming newsletter signup is on the HomePage
            driver.findElement(By.id("newsletter-email")).sendKeys("InvalidEmail");
            driver.findElement(By.id("newsletter-subscribe-button")).click();
            WebElement successMessage = driver.findElement(By.xpath("//div[@id='newsletter-result-block']"));
            Thread.sleep(3000);

            if (successMessage.isDisplayed()) {
                Assert.assertTrue(successMessage.getText().contains("Thank you for signing up! A verification email has been sent. We appreciate your interest."));
                test.pass("Newsletter signup with invalid email handled correctly");
            } else {

                File ssfile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(ssfile, new File("T2.png"));
                test.fail("Enter Valid Email ID" + test.addScreenCaptureFromPath("T2.png"));
                throw new Exception("Success message not displayed.");

            }

        } catch (Exception e) {

            Assert.fail("Test failed: " + e.getMessage());
            File ssfile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(ssfile, new File("T2.png"));
            test.fail("Enter Valid Email ID" + test.addScreenCaptureFromPath("T2.png"));

        }
    }

    @Test(priority = 3)
    public void test3VerifyNewsletterSignup() {
        test = extent.createTest("Verify Newsletter Signup");
        try {
            WebElement newsletterEmail = driver.findElement(By.id("newsletter-email"));
            newsletterEmail.clear();
            newsletterEmail.sendKeys("test@example.com");
            driver.findElement(By.id("newsletter-subscribe-button")).click();
            WebElement successMessage = driver.findElement(By.xpath("//div[@id='newsletter-result-block']"));
            Thread.sleep(3000);
            Assert.assertTrue(successMessage.getText().contains("Thank you for signing up! A verification email has been sent. We appreciate your interest."));
            test.pass("Newsletter signup successful with valid email");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            Assert.fail("Test failed: " + e.getMessage());
        }
    }

    @Test(priority = 4)
    public void test4RegisterUser() throws IOException {
        test = extent.createTest("User Registration");
        registrationPage.register(firstName, lastName, email, password);
        try {
            String result = registrationPage.getRegistrationResult();
            Assert.assertTrue(result.contains("Your registration completed"), "Registration failed");
            test.pass("User registration successful");
        } catch (NoSuchElementException e) {
            File ssfile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(ssfile, new File("T4.jpg"));
            test.fail("User Already Exits" + test.addScreenCaptureFromPath("T4.jpg"));
        }
        registrationPage.logout();
    }

    @Test(priority = 5)
    public void test5ValidateLoginWithIncorrectCredentials() {
        test = extent.createTest("Validate Login with Incorrect Credentials");
        loginPage.login("wrong@example.com", "wrongpassword");
        String error = loginPage.getErrorMessage();
        Assert.assertTrue(error.contains("Login was unsuccessful. Please correct the errors and try again."), "Error message not displayed for incorrect login");
        test.pass("Incorrect login credentials handled correctly");
    }

    @Test(priority = 6)
    public void test6LoginUser() {
        test = extent.createTest("User Login");
        loginPage.login(email, password);
        WebElement accountLink = driver.findElement(By.cssSelector("a.account"));
        Assert.assertEquals(accountLink.getText(), email, "Logged in user email does not match");
        test.pass("User logged in successfully");
    }

    @Test(priority = 7)
    public void test7VerifyRecentlyViewed() throws InterruptedException, IOException {
        test = extent.createTest("Verify Recently Viewed Section");
        Thread.sleep(5000);
        try {
            WebElement recentyViewed = driver.findElement(By.xpath("//strong[normalize-space()='Recently viewed products']"));
            if (recentyViewed.isDisplayed()) {
                Assert.assertTrue(recentyViewed.isDisplayed(), "Recently viewed section is missing");
                test.pass("Recently viewed section is displayed correctly");
            }
        } catch (NoSuchElementException e) {

            File ssfile2 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(ssfile2, new File("T7.jpg"));
            test.fail("Recenty Viewed Products are not displayed." + test.addScreenCaptureFromPath("T7.jpg"));
            Assert.fail("Recenty Viewed Products are not displayed.");
        }

    }

    @Test(priority = 8)
    public void test8SearchProduct() throws IOException {

        test = extent.createTest("Search for Product");
        homePage.searchproduct();
        driver.findElement(By.xpath("//input[@type='submit' and @value='Search']")).click();
        boolean isProductDisplayed = driver.findElement(By.xpath("//a[normalize-space()='Build your own computer']")).isDisplayed();
        Assert.assertTrue(isProductDisplayed, "Product Not Found");
        test.pass("Product searched and displayed successfully");
    }

    @Test(priority = 9)
    public void test9VerifySortingByName() {
        test = extent.createTest("Verify Sorting by Name: Z to A");
        productPage.sortBy("Name: Z to A");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> actualNames = productPage.getProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);
        expectedNames.sort(Comparator.reverseOrder());
        Assert.assertEquals(actualNames, expectedNames, "The products are not sorted correctly by 'Name: Z to A'.");
        test.pass("Products sorted by Name: Z to A successfully");
    }

    @Test(priority = 10)
    public void test10VerifyViewAsList() {
        test = extent.createTest("Verify View as List");
        productPage.changeViewMode("List");
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("viewmode=list"), "The URL does not reflect 'List' view.");
        List<WebElement> productItems = driver.findElements(By.cssSelector(".product-list"));
        Assert.assertTrue(productItems.size() > 0, "No products are displayed in 'List' view.");
        test.pass("View mode changed to List successfully");
    }

    @Test(priority = 11)
    public void test11VerifyProduct() {
        test = extent.createTest("Verify Specific Product");
        productPage.navigateToSimpleComputer();
        WebElement product = driver.findElement(By.xpath("//*[@id=\"product-details-form\"]/div/div[1]/div[2]/div[1]/h1"));
        Assert.assertTrue(product.getText().contains("Simple Computer"), "Product name does not match");
        test.pass("Verified specific product successfully");
    }

    @Test(priority = 12)
    public void test12VerifyAddToWishlist() throws IOException {
        test = extent.createTest("Verify Add to Wishlist");

        try {
            productPage.addToWishlist();
            WebElement successMessage = driver.findElement(By.xpath("//div[@class='wishlist-content']"));
            if (successMessage.isDisplayed()) {
                Assert.assertTrue(successMessage.getText().contains("The product has been added to your wishlist"), "Add to Wishlist failed");
                test.pass("Add to Wishlist verified successfully");
            }
        } catch (NoSuchElementException e) {

            File ssfile1 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(ssfile1, new File("T12.jpg"));
            test.fail("Add to Wishlist button is missing." + test.addScreenCaptureFromPath("T12.jpg"));
            Assert.fail("Add to Wishlist button is missing.");

        }

    }

    @Test(priority = 13)
    public void test13AddToCart() {
        test = extent.createTest("Add Product to Cart");
        productPage.addToCart();
        WebDriverWait wait = null;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement cartMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p.content")));

        if (cartMessage.isDisplayed()) {
            Assert.assertTrue(cartMessage.getText().contains("The product has been added to your shopping cart"), "Add to Cart failed");
            test.pass("Product added to cart successfully");
        } else {
            Assert.assertTrue(cartMessage.getText().contains("The product has been added to your shopping cart"), "Add to Cart failed");
            test.pass("Product Not added to cart successfully");
        }

    }

    @Test(priority = 14)
    public void test14ChangeProductQuantity() throws InterruptedException {
        test = extent.createTest("Change Product Quantity in Cart");

        Thread.sleep(3000);
        homePage.openShoppingCart();

        WebElement cartValueElement = driver.findElement(By.xpath("//span[@class='product-subtotal']"));
        String cartValueText = cartValueElement.getText().replace(",", "").replace("₹", "").trim();
        double oldCartValue = Double.parseDouble(cartValueText);

        int newQuantity = 2;

        WebElement quantity = driver.findElement(By.xpath("//input[@class='qty-input']"));
        quantity.clear();
        quantity.sendKeys(String.valueOf(newQuantity));
        driver.findElement(By.xpath("//input[@name='updatecart']")).click();

        double expectedCartValue = oldCartValue * newQuantity;
        WebElement updatedCartValueElement = driver.findElement(By.xpath("//span[@class='product-subtotal']"));
        String updatedCartValueText = updatedCartValueElement.getText().replace(",", "").replace("₹", "").trim();
        double updatedCartValue = Double.parseDouble(updatedCartValueText);

        Assert.assertEquals(updatedCartValue, expectedCartValue, "Cart value did not update correctly after changing quantity");
        if (updatedCartValue == expectedCartValue) {
            test.pass("Product quantity changed and cart value updated successfully");
        } else {
            test.fail("Failed to Update the Cart");
        }

    }

    @Test(priority = 15)
    public void test15DeleteProduct() {
        test = extent.createTest("Delete Product from Cart");
        cartPage.removeItemFromCart();
        WebElement cartMessage = driver.findElement(By.xpath("//div[@class='order-summary-content']"));
        Assert.assertTrue(cartMessage.getText().contains("Your Shopping Cart is empty!"), "Cart is not empty after deletion");
        test.pass("Product deleted from cart successfully");
    }

    @Test(priority = 16)
    public void test16VerifyForgotPasswordRedirect() {
        test = extent.createTest("Verify Forgot Password Redirect");

        try {
            loginPage.logout();
        } catch (Exception e) {

        }
        loginPage.clickForgotPassword();
        WebElement resetPasswordPage = driver.findElement(By.xpath("//h1[text()='Password recovery']"));
        Assert.assertTrue(resetPasswordPage.getText().contains("Password recovery"), "Forgot password link did not redirect correctly");
        driver.findElement(By.id("Email")).sendKeys(email);
        driver.findElement(By.name("send-email")).click();
        WebElement confirmationMessage = driver.findElement(By.xpath("//div[@class='result']"));
        Assert.assertTrue(confirmationMessage.getText().contains("Email with instructions has been sent to you."), "Password recovery email not sent");
        test.pass("Forgot Password functionality works correctly");
    }
}
