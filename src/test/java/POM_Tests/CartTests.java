package POM_Tests;

import PageObjects.CategoryPage;
import PageObjects.ProductPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CartTests extends BaseTest {

    String productURL = "https://fakestore.testelka.pl/product/gran-koscielcow/";
    String pageURL = "https://fakestore.testelka.pl/product-category/wspinaczka/";
    String[] productSites = {"/wspinaczka-via-ferraty/", "/egipt-el-gouna/", "/wspinaczka-island-peak/",
            "/fuerteventura-sotavento/", "/yoga-i-pilates-w-hiszpanii/", "/grecja-limnos/", "/windsurfing-w-karpathos/",
            "/wyspy-zielonego-przyladka-sal/", "/wakacje-z-yoga-w-kraju-kwitnacej-wisni/",
            "/wczasy-relaksacyjne-z-yoga-w-toskanii/"};

    @Test
    public void addTripToCartFromProductPageTest() {
    ProductPage productPage = new ProductPage(driver).goToProductPage(productURL);
    productPage.closeCookiesAnnotation();
    int tripsAmount = productPage
            .addToCart()
            .viewCart()
            .getAmountOfTrips();

    Assertions.assertTrue(tripsAmount==1);
    }

    @Test
    public void addTripToCartFromCategoryPageTest() {
        CategoryPage categoryPage = new CategoryPage(driver).goToCategoryPage(pageURL);
        categoryPage.closeCookiesAnnotation();
        int tripsAmount = categoryPage
            .addToCart()
            .viewCart()
            .getAmountOfTrips();

        Assertions.assertTrue(tripsAmount==1);
    }

    @Test
    public void addToCartSameTripTenTimesTest() {
        ProductPage productPage = new ProductPage(driver).goToProductPage(productURL);
        productPage.closeCookiesAnnotation();
        int tripsAmount = productPage
                .addToCart(10)
                .viewCart()
                .getAmountOfTrips();

        Assertions.assertEquals(10, tripsAmount);
    }

    @Test
    public void addToCartTenVariousTripsTest() {
        ProductPage productPage = new ProductPage(driver);
        for(String productSite : productSites) {
            productPage.goToProductPage("https://fakestore.testelka.pl/product" + productSite)
                    .addToCart();
        }
        int numberOfTrips = productPage.header.viewCart().getNumberOfTrips();
        Assertions.assertEquals(10, numberOfTrips);
    }

    @Test
    public void changeAmountOfTripsInTheCartTest() {
        ProductPage productPage = new ProductPage(driver).goToProductPage(productURL);
        productPage.closeCookiesAnnotation();
        int numberOfTrips = productPage
                .addToCart()
                .viewCart()
                .updateTripsQuantity(3)
                .updateCart()
                .getAmountOfTrips();

        Assertions.assertEquals(3, numberOfTrips);
    }

    @Test
    public void removeTripFromCartTest() {
        ProductPage productPage = new ProductPage(driver).goToProductPage(productURL);
        productPage.closeCookiesAnnotation();
        boolean isCartEmpty = productPage
                .addToCart()
                .viewCart()
                .removeProduct()
                .isCartEmpty();

        Assertions.assertTrue(isCartEmpty);
    }
}
