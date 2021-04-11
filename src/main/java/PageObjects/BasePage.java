package PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class BasePage {
    protected WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    private By closeAnnotationLocator = By.cssSelector(".woocommerce-store-notice__dismiss-link");
    public void closeCookiesAnnotation() {
        driver.findElement(closeAnnotationLocator).click();
    }
}
