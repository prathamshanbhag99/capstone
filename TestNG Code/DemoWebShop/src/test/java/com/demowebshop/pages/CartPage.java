package com.demowebshop.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CartPage {
    WebDriver driver;

    @FindBy(xpath = "//input[@name='removefromcart']")
    private WebElement removeFromCartCheckbox;

    @FindBy(xpath = "//input[@name='updatecart']")
    private WebElement updateCartButton;

    @FindBy(xpath = "//div[@class='order-summary-content']")
    private WebElement orderSummaryContent;

    @FindBy(xpath = "//input[@id='termsofservice']")
    private WebElement termsOfServiceCheckbox;

    @FindBy(xpath = "//button[@id='checkout']")
    private WebElement checkoutButton;


    public CartPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }


    public void removeItemFromCart() {
        removeFromCartCheckbox.click();
        updateCartButton.click();
    }


    public String getOrderSummaryMessage() {
        return orderSummaryContent.getText();
    }

    public void proceedToCheckout() {
        termsOfServiceCheckbox.click();
        checkoutButton.click();
    }
}
