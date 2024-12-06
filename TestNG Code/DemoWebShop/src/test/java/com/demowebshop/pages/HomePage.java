package com.demowebshop.pages;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.io.FileInputStream;
import java.io.IOException;


public class HomePage {
    WebDriver driver;

    @FindBy(id = "small-searchterms")
    private WebElement searchStoreElement;

    @FindBy(xpath = "//div[@class='block block-category-navigation']//div[@class='title']")
    private WebElement categoryMenu;

    @FindBy(xpath = "/html/body/div[4]/div[1]/div[1]/div[2]/div[1]/ul/li[1]/a")
    private WebElement registerTab;

    @FindBy(xpath = "/html/body/div[4]/div[1]/div[1]/div[2]/div[1]/ul/li[2]/a")
    private WebElement loginTab;

    @FindBy(xpath = "//span[normalize-space()='Shopping cart']")
    private WebElement shoppingCartLink;


    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }


    public boolean isSearchStoreDisplayed() {
        return searchStoreElement.isDisplayed();
    }


    public boolean isCategoryMenuDisplayed() {
        return categoryMenu.isDisplayed();
    }

    public boolean isRegisterTabDisplayed() {
        return registerTab.isDisplayed();
    }

    public boolean isLoginTabDisplayed() {
        return loginTab.isDisplayed();
    }


    public void openShoppingCart() {
        shoppingCartLink.click();
    }

    public void searchproduct() throws IOException {
        FileInputStream input = new FileInputStream("C:\\Users\\pratham.shanbhag\\Documents\\API\\DemoWebShop\\src\\main\\resources\\tricentis_data.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(input);
        XSSFSheet sheet = workbook.getSheet("product");
        int noofrows = sheet.getPhysicalNumberOfRows();
        System.out.println("rows: " + noofrows);
        for (int i = 0; i < noofrows; i++) {
            String product = sheet.getRow(i).getCell(0).getStringCellValue();
            driver.findElement(By.id("small-searchterms")).sendKeys(product);
        }
    }
}
