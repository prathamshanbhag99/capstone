package com.demowebshop.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RegistrationPage {
    WebDriver driver;

    @FindBy(xpath = "//a[normalize-space()='Register']")
    private WebElement registrationPage;

    @FindBy(id = "gender-male")
    private WebElement genderMaleRadio;

    @FindBy(id = "FirstName")
    private WebElement firstNameField;

    @FindBy(id = "LastName")
    private WebElement lastNameField;

    @FindBy(id = "Email")
    private WebElement emailField;

    @FindBy(id = "Password")
    private WebElement passwordField;

    @FindBy(id = "ConfirmPassword")
    private WebElement confirmPasswordField;

    @FindBy(id = "register-button")
    private WebElement registerButton;

    @FindBy(className = "result")
    private WebElement registrationResult;

    @FindBy(css = "a.ico-logout")
    private WebElement logoutLink;

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void register(String firstName, String lastName, String email, String password) {
        registrationPage.click();
        genderMaleRadio.click();
        firstNameField.sendKeys(firstName);
        lastNameField.sendKeys(lastName);
        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        confirmPasswordField.sendKeys(password);
        registerButton.click();
    }

    public String getRegistrationResult() {
        return registrationResult.getText();
    }


    public void logout() {
        logoutLink.click();
    }
}
