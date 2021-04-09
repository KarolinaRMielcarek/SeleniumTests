import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class CartTests {
    ChromeDriver driver;
    WebDriverWait wait;
    By productPageAddToCartButton = By.cssSelector("[name='add-to-cart']");
    String productId = "4114";
    By categoryPageAddToCartButton = By.cssSelector(".post-"+productId+">.add_to_cart_button");
    By productPageShowCartButton = By.cssSelector(".woocommerce-message>.wc-forward");
    By cartView = By.cssSelector(".entry-title");
    String expectedProductName = "Grań Kościelców";
    By productName = By.cssSelector("tr>td[class='product-name']");
    By productQuantity = By.cssSelector("input[type='number']");
    By removeButton = By.cssSelector("a.remove");
    String[] productPages = {"/wspinaczka-via-ferraty/", "/egipt-el-gouna/", "/wspinaczka-island-peak/",
            "/fuerteventura-sotavento/", "/yoga-i-pilates-w-hiszpanii/", "/grecja-limnos/", "/windsurfing-w-karpathos/",
            "/wyspy-zielonego-przyladka-sal/", "/wakacje-z-yoga-w-kraju-kwitnacej-wisni/",
            "/wczasy-relaksacyjne-z-yoga-w-toskanii/"};


    @BeforeEach
    public void driverSetup() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.navigate().to("https://fakestore.testelka.pl/");
        driver.findElement(By.cssSelector(".woocommerce-store-notice__dismiss-link")).click();
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, 10);
    }

    @AfterEach
    public void driverQuit() {
        driver.quit();
    }

    @Test
    public void addTripToCartFromProductPage() {
        addProductAndGoToCart("https://fakestore.testelka.pl/product/gran-koscielcow/");
        Assertions.assertEquals(expectedProductName, driver.findElement(productName).getText());
        Assertions.assertEquals("1", driver.findElement(productQuantity).getAttribute("value"));
    }

    @Test
    public void addTripToCartFromCategoryPage() {
        driver.navigate().to(" https://fakestore.testelka.pl/product-category/wspinaczka/");
        driver.findElement(categoryPageAddToCartButton).click();
        By cart = By.cssSelector("a[title='Zobacz koszyk']");
        wait.until(ExpectedConditions.elementToBeClickable(cart));
        driver.findElement(cart).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(cartView));
        Assertions.assertEquals(expectedProductName, driver.findElement(productName).getText());
        Assertions.assertEquals("1", driver.findElement(productQuantity).getAttribute("value"));
    }

    @Test
    public void removeTripFromTheCart() {
        addProductAndGoToCart("https://fakestore.testelka.pl/product/gran-koscielcow/");
        driver.findElement(removeButton).click();
        By alert = By.cssSelector("p.cart-empty");
        wait.until(ExpectedConditions.presenceOfElementLocated(alert));
        Assertions.assertEquals("Twój koszyk jest pusty.", driver.findElement(alert).getText());
    }

    @Test
    public void addTenSameTrips() {
        addProductToCart("https://fakestore.testelka.pl/product/gran-koscielcow/", "10");
        WebElement numberOfTrips = driver.findElement(By.cssSelector("div>input"));
        Assertions.assertEquals("10", numberOfTrips.getAttribute("value"));
    }

    @Test
    public void addTenVariousTrips() {
        for(String productPage : productPages) {
            addProduct("https://fakestore.testelka.pl/product" + productPage);
        }
        goToCart();
        int numberOfTrips = driver.findElements(By.cssSelector(".cart_item")).size();
        Assertions.assertEquals(10, numberOfTrips);
    }

    @Test
    public void changeAmountOfTripsInTheCart() {
        addProductAndGoToCart("https://fakestore.testelka.pl/product/gran-koscielcow/");
        WebElement numberOfTrips = driver.findElement(By.cssSelector("input[type='number']"));
        numberOfTrips.clear();
        numberOfTrips.sendKeys("9");
        driver.findElement(By.cssSelector("[name='update_cart']")).click();
        Assertions.assertEquals("9", numberOfTrips.getAttribute("value"));
    }

    public void addProductAndGoToCart(String productPageURL) {
        addProduct(productPageURL);
        goToCart();
    }

    public void addProduct(String productPageURL) {
        driver.navigate().to(productPageURL);
        addProductToCart();
    }

    public void addProductToCart(String productPageURL, String productQuantity){
        driver.navigate().to(productPageURL);
        WebElement quantity = driver.findElement(By.cssSelector("input.qty"));
        quantity.clear();
        quantity.sendKeys(productQuantity);
        addProductToCart();
    }

    public void addProductToCart() {
        WebElement addToCartButton = driver.findElement(productPageAddToCartButton);
        addToCartButton.click();
    }

    public void goToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(productPageShowCartButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartView));
    }
}

