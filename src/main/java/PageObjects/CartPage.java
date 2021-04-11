package PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage extends BasePage {

    WebDriverWait wait;

    public CartPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, 7);
    }

    private By tripQuantityLocator = By.cssSelector("input[type='number']");
    private By cartLocator = By.cssSelector(".cart_item");
    private By removeButtonLocator = By.cssSelector("a.remove");
    private By loaderLocator = By.cssSelector(".blockOverlay");
    private By cartListLocator = By.cssSelector("form>.shop_table");
    private By updateButtonLocator = By.cssSelector("[name='update_cart']");


    public int getAmountOfTrips() {
        String quantity = driver.findElement(tripQuantityLocator).getAttribute("value");
        return Integer.parseInt(quantity);
    }

    public int getNumberOfTrips() {
        return driver.findElements(cartLocator).size();
    }

    public CartPage removeProduct() {
        driver.findElement(removeButtonLocator).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));
        return new CartPage(driver);
    }

    public boolean isCartEmpty() {
        int numberOfTripsInCart = driver.findElements(cartListLocator).size();
        if (numberOfTripsInCart == 1) {
            return false;
        } else if(numberOfTripsInCart == 0) {
            return true;
        } else {
            throw new IllegalArgumentException("Wrong number of trips");
        }
    }

    public CartPage updateTripsQuantity(int quantity) {
        WebElement tripQuantity = driver.findElement(tripQuantityLocator);
        tripQuantity.clear();
        tripQuantity.sendKeys(Integer.toString(quantity));
        return new CartPage(driver);
    }

    public CartPage updateCart() {
        WebElement update = driver.findElement(updateButtonLocator);
        wait.until(ExpectedConditions.elementToBeClickable(update));
        update.click();
        return new CartPage(driver);
    }
}
