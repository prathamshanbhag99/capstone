package com.demowebshop.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for Product Page.
 */
public class ProductPage {
    WebDriver driver;

    // Locators
    @FindBy(xpath = "//a[normalize-space()='Build your own computer']")
    private WebElement buildYourOwnComputerLink;

    @FindBy(xpath = "//a[normalize-space()='Simple Computer']")
    private WebElement simpleComputerProductLink;

    @FindBy(id = "products-orderby")
    private WebElement sortByDropdown;

    @FindBy(id = "products-viewmode")
    private WebElement viewModeDropdown;

    @FindBy(xpath = "//input[@id='add-to-cart-button-75']")
    private WebElement addToCartButton;

    @FindBy(xpath = "//input[@id='add-to-wishlist-button-1']")
    private WebElement addToWishlistButton;

    // Constructor
    public ProductPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }


    public void navigateToBuildYourOwnComputer() {
        buildYourOwnComputerLink.click();
    }

    public void navigateToSimpleComputer() {
        simpleComputerProductLink.click();
    }

    public void addToCart() {

        driver.findElement(By.xpath("//input[@id='product_attribute_75_5_31_96']")).click();
        driver.findElement(By.xpath("//*[@id=\"product_attribute_75_6_32_99\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"product_attribute_75_3_33_103\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"product_attribute_75_8_35_108\"]")).click();
        addToCartButton.click();
    }

    public void addToWishlist() {
        addToWishlistButton.click();
    }


    public void sortBy(String option) {
        sortByDropdown.click();
        WebElement sortOption = driver.findElement(By.xpath("//select[@id='products-orderby']/option[text()='" + option + "']"));
        sortOption.click();
    }

    public void changeViewMode(String mode) {
        viewModeDropdown.click();
        WebElement viewOption = driver.findElement(By.xpath("//select[@id='products-viewmode']/option[text()='" + mode + "']"));
        viewOption.click();
    }


    public List<String> getProductNames() {
        List<WebElement> products = driver.findElements(By.cssSelector(".product-title a"));
        List<String> productNames = new ArrayList<>();
        for (WebElement product : products) {
            productNames.add(product.getText());
        }
        return productNames;
    }
}
