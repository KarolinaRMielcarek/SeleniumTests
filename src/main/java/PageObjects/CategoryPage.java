package PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CategoryPage extends BasePage {

    public CategoryPage(WebDriver driver) {
        super(driver);
    }

    private String productId = "4114";
    private By addToCartButton = By.cssSelector(".post-"+productId+">.add_to_cart_button");
    private By showCartButton = By.cssSelector("a[title='Zobacz koszyk']");

    public CategoryPage goToCategoryPage(String pageURL) {
        driver.navigate().to(pageURL);
        return new CategoryPage(driver);
    }

    public CategoryPage addToCart() {
        WebElement addButton = driver.findElement(addToCartButton);
        addButton.click();
        return new CategoryPage(driver);
    }

    public CartPage viewCart() {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.elementToBeClickable(showCartButton)).click();
        return new CartPage(driver);
    }
}
