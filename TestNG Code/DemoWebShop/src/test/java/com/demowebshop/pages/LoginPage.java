package com.demowebshop.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class LoginPage {
    WebDriver driver;


    @FindBy(css = "a.ico-login")
    private WebElement loginLink;

    @FindBy(id = "Email")
    private WebElement emailField;

    @FindBy(id = "Password")
    private WebElement passwordField;

    @FindBy(css = "input.button-1.login-button")
    private WebElement loginButton;

    @FindBy(css = ".validation-summary-errors")
    private WebElement errorMessage;

    @FindBy(css = "a.ico-logout")
    private WebElement logoutLink;

    @FindBy(linkText = "Forgot password?")
    private WebElement forgotPasswordLink;

    @FindBy(xpath = "//div[@id='newsletter-result-block']")
    private WebElement newsletterResultBlock;



    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void login(String email, String password) {
        loginLink.click();
        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        loginButton.click();
    }

    public void logout() {
        logoutLink.click();
    }

    public String getErrorMessage() {
        return errorMessage.getText();
    }

    public void clickForgotPassword() {
        loginLink.click();
        forgotPasswordLink.click();
    }
}
