import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentTests {
    ChromeDriver driver;
    WebDriverWait wait;

    By showlogInButton = By.cssSelector(".showlogin");
    By username = By.cssSelector("[name='username']");
    By password = By.cssSelector("[name='password']");
    By logInButton = By.cssSelector("[name='login']");
    By firstName = By.cssSelector("#billing_first_name");
    By lastName = By.cssSelector(("#billing_last_name"));
    By address = By.cssSelector("#billing_address_1");
    By postCode = By.cssSelector("#billing_postcode");
    By city = By.cssSelector("#billing_city");
    By phoneNumber = By.cssSelector("#billing_phone");
    By email = By.cssSelector("#billing_email");
    By passwordToNewAccount = By.cssSelector("#account_password");
    By cardNumber = By.cssSelector("input[name='cardnumber']");
    By expirationDate = By.cssSelector("[name='exp-date']");
    By cvc = By.cssSelector("[name='cvc']");
    By submitTerms = By.cssSelector("[name='terms']");
    By productPageAddToCartButton = By.cssSelector("[name='add-to-cart']");
    By productPageShowCartButton = By.cssSelector(".woocommerce-message>.wc-forward");
    By cartView = By.cssSelector(".entry-title");
    By placedOrderInfo = By.cssSelector(".woocommerce-order-details__title");
    By placedOrders = By.cssSelector(".woocommerce-orders-table__cell-order-number");
    By myAccountButton = By.cssSelector("li.my-account.menu-item-201");
    By deleteAccountButton = By.cssSelector(".delete-me");
    By placeOrderButton = By.cssSelector(".checkout-button");
    By payAndBuyButton = By.cssSelector("#place_order");
    By myAccount = By.cssSelector(".my-account");
    By myOrders = By.cssSelector(".woocommerce-MyAccount-navigation-link--orders");
    By orderNumber = By.cssSelector(".order>strong");
    By orderDate = By.cssSelector(".date>strong");
    By orderAmount = By.cssSelector(".total .amount");
    By orderPaymentMethod = By.cssSelector(".method>strong");
    By orderProductName = By.cssSelector(".product-name>a");
    By orderProductQty = By.cssSelector(".product-quantity");


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
    public void logInAndPay() {
        addProductAndGoToCart("https://fakestore.testelka.pl/product/gran-koscielcow/");
        placeOrder();
        logInWithExistingAccount();
        enterPaymentDetails();
        enterCardDetails(true);
        waitTillProcessingNewOrderEnds();
        Assertions.assertEquals("Szczegóły zamówienia", driver.findElement(placedOrderInfo).getText());
    }

    @Test
    public void createAccountAndPay() {
        addProductAndGoToCart("https://fakestore.testelka.pl/product/gran-koscielcow/");
        placeOrder();
        createAccount();
        enterCardDetails(true);
        waitTillProcessingNewOrderEnds();
        Assertions.assertEquals("Szczegóły zamówienia", driver.findElement(placedOrderInfo).getText());
        deleteAccount();
    }

    @Test
    public void payWithoutCreatingAccount() {
        addProductAndGoToCart("https://fakestore.testelka.pl/product/gran-koscielcow/");
        placeOrder();
        enterPaymentDetails();
        driver.findElement(By.cssSelector("#billing_email")).sendKeys("testemail1@gmail.com");
        enterCardDetails(true);
        payAndBuy();
        waitTillProcessingNewOrderEnds();
        Assertions.assertEquals("Szczegóły zamówienia", driver.findElement(placedOrderInfo).getText());
    }

    @Test
    public void checkOrdersList() {
        addProductAndGoToCart("https://fakestore.testelka.pl/product/gran-koscielcow/");
        placeOrder();
        createAccount();
        enterCardDetails(true);
        waitTillProcessingNewOrderEnds();
        goToMyOrders();
        Assertions.assertTrue(driver.findElements(placedOrders).size()>0);
        deleteAccount();
    }

    @Test
    public void showOrderSummary() {
        addProductAndGoToCart("https://fakestore.testelka.pl/product/gran-koscielcow/");
        placeOrder();
        logInWithExistingAccount();
        enterPaymentDetails();
        enterCardDetails(true);
        int order = Integer.parseInt(waitTillProcessingNewOrderEnds());
        String expectedDate = getCurrentDate();
        String actualDate = driver.findElement(orderDate).getText();
        String expectedAmount = "2 999,99 zł";
        String actualAmount = driver.findElement(orderAmount).getText();
        String expectedMethodOfPayment = "Karta debetowa/kredytowa (Stripe)";
        String actualMethodOfPayment = driver.findElement(orderPaymentMethod).getText();
        String expectedProductName = "Grań Kościelców";
        String actualProductName = driver.findElement(orderProductName).getText();
        String expectedQuantity = "x 1";
        String actualQuantity = driver.findElement(orderProductQty).getText();

        assertAll(
                () -> assertTrue(order>0),
                () -> assertEquals(expectedDate, actualDate),
                () -> assertEquals(expectedAmount, actualAmount),
                () -> assertEquals(expectedMethodOfPayment, actualMethodOfPayment),
                () -> assertEquals(expectedProductName, actualProductName),
                () -> assertEquals(expectedQuantity, actualQuantity)
        );
    }

    @Test
    public void showErrorMessageDuringOrdering() {
        addProductAndGoToCart("https://fakestore.testelka.pl/product/gran-koscielcow/");
        placeOrder();
        enterCardDetails(true);
        payAndBuy();
        String errorMessage = waitTillProcessingNewOrderEnds();
        assertAll(
                () -> assertTrue(errorMessage.contains("Imię płatności jest wymaganym polem.")),
                () -> assertTrue(errorMessage.contains("Nazwisko płatności jest wymaganym polem.")),
                () -> assertTrue(errorMessage.contains("Ulica płatności jest wymaganym polem.")),
                () -> assertTrue(errorMessage.contains("Miasto płatności jest wymaganym polem.")),
                () -> assertTrue(errorMessage.contains("Telefon płatności jest wymaganym polem.")),
                () -> assertTrue(errorMessage.contains("Adres email płatności jest wymaganym polem.")),
                () -> assertTrue(errorMessage.contains("Kod pocztowy płatności jest wymaganym polem.")),
                () -> assertTrue(errorMessage.contains("Proszę przeczytać i zaakceptować regulamin sklepu aby móc sfinalizować zamówienie"))
        );
        deleteAccount();
    }

    private void goToMyOrders(){
        goToMyAccount();
        driver.findElement(myOrders).click();
    }

    private void goToMyAccount() {
        driver.findElement(myAccount).click();
    }
    private void placeOrder() {
        driver.findElement(placeOrderButton).click();
    }

    private void payAndBuy() {
        driver.findElement(payAndBuyButton).click();
    }

    private void addProductAndGoToCart(String productPageURL) {
        addProduct(productPageURL);
        goToCart();
    }

    public void addProduct(String productPageURL) {
        driver.navigate().to(productPageURL);
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

    private void logInWithExistingAccount() {
        wait.until(ExpectedConditions.presenceOfElementLocated(showlogInButton)).click();
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div[class='woocommerce']"))));
        driver.findElement(username).clear();
        driver.findElement(username).sendKeys("karolina.ra.kr@gmail.com");
        driver.findElement(password).clear();
        driver.findElement(password).sendKeys("Koraliczek12!");
        driver.findElement(logInButton).click();
    }

    private void enterPaymentDetails() {
        driver.findElement(firstName).sendKeys("Karolina");
        driver.findElement(lastName).sendKeys("Mroczek");
        driver.findElement(address).sendKeys("Ołowiana 3/4");
        driver.findElement(postCode).sendKeys("02-322");
        driver.findElement(city).sendKeys("Warszawa");
        driver.findElement(phoneNumber).sendKeys("677542345");
    }

    private void enterCardDetails(boolean submitTerms) {
        driver.switchTo().frame(0);
        driver.findElement(cardNumber).clear();
        driver.findElement(cardNumber).sendKeys("4242424242424242");
        driver.switchTo().defaultContent();
        driver.switchTo().frame(1);
        driver.findElement(expirationDate).clear();
        driver.findElement(expirationDate).sendKeys("12/21");
        driver.switchTo().defaultContent();
        driver.switchTo().frame(2);
        driver.findElement(cvc).clear();
        driver.findElement(cvc).sendKeys("121");
        driver.switchTo().defaultContent();
        if (submitTerms) {
            driver.findElement(By.cssSelector("#terms")).click();
        }
    }

    private void createAccount() {
        enterPaymentDetails();
        driver.findElement(email).sendKeys("testemail11@gmail.com");
        driver.findElement(By.cssSelector("#createaccount")).click();
        wait.until(ExpectedConditions.elementToBeClickable(passwordToNewAccount));
        driver.findElement(passwordToNewAccount).sendKeys("testoweHaslo11");
    }

    private String waitTillProcessingNewOrderEnds() {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.urlContains("/zamowienie-otrzymane/"));
        return
                wait.until(ExpectedConditions.presenceOfElementLocated(orderNumber)).getText();
    }

    private void deleteAccount() {
        driver.findElement(myAccountButton).click();
        wait.until(ExpectedConditions.elementToBeClickable(deleteAccountButton)).click();
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    private String getCurrentDate() {
        Calendar date = Calendar.getInstance();
        String fullDate = date.get(Calendar.DAY_OF_MONTH) + " " + getPolishMonth(date.get(Calendar.MONTH)) + ", " + date.get(Calendar.YEAR);
        return fullDate;
    }

    private String getPolishMonth(int numberOfMonth){
        String[] monthNames = {"stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca",
                "lipca", "sierpnia", "września", "października", "listopada", "grudnia"};
        return monthNames[numberOfMonth];
    }

}

