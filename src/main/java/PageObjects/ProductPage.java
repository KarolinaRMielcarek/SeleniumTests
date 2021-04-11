package PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductPage extends BasePage {
    public HeaderPage header;
    private WebDriverWait wait;

    public ProductPage(WebDriver driver) {
        super(driver);
        //sluzy do wywolania konstruktora klasy nadrzednej
        wait = new WebDriverWait(driver, 10);
        header = new HeaderPage(driver);
    }

    private By addToCartButton = By.cssSelector("[name='add-to-cart']");
    private By showCartButton = By.cssSelector(".woocommerce-message>.wc-forward");
    private By productQuantityLocator = By.cssSelector("input[type='number']");

    public ProductPage goToProductPage(String productURL) {
        driver.navigate().to(productURL);
        return new ProductPage(driver);
    }

    public ProductPage addToCart() {
        WebElement addButton = driver.findElement(addToCartButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addButton);
        addButton.click();
        return new ProductPage(driver);
    }

    public CartPage viewCart() {
        wait.until(ExpectedConditions.elementToBeClickable(showCartButton)).click();
        return new CartPage(driver);
    }

    public ProductPage addToCart(int quantity) {
        WebElement tripsQuantity = driver.findElement(productQuantityLocator);
        tripsQuantity.clear();
        tripsQuantity.sendKeys(String.valueOf(quantity));
        return addToCart();
    }

}
