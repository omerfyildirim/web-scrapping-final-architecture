package org.vol3scrappingweb;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String baseUrl = "https://homele.com/tr/properties?page=";
        int pageCount = 2;

        WebDriver driver = new SafariDriver();
        driver.manage().window().maximize();

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/postgres", "postgres", "123456");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(50))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(StaleElementReferenceException.class);

        for (int page = 1; page <= pageCount; page++) {
            String url = baseUrl + page;
            driver.get(url);

            // Sayfanın tamamen yüklenmesini bekleyin
            wait.until(WebDriver -> (Boolean) ((JavascriptExecutor) driver).executeScript("return document.readyState === 'complete';"));

            WebElement listingsContainer = driver.findElement(By.className("utf-listings-container-area"));
            List<WebElement> listingLinks = listingsContainer.findElements(By.className("card-divider"));

            for (WebElement listingLink : listingLinks) {
                // Sayfanın tamamen yüklenmesini bekleyin
                wait.until(WebDriver -> (Boolean) ((JavascriptExecutor) driver).executeScript("return document.readyState === 'complete';"));

                int retryCount = 3;
                for (int i = 0; i < retryCount; i++) {
                    try {
                        // Sayfayı aşağı kaydır
                        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 270);");

                        WebElement textTruncateElement = listingLink.findElement(By.className("property-image"));
                        wait.until(ExpectedConditions.elementToBeClickable(textTruncateElement)).click();

                        // Önceki işlemler
                        break; // Eğer buraya kadar hatasız bir şekilde geldiyse döngüden çık
                    } catch (StaleElementReferenceException e) {
                        // Hata durumunda bir şey yapmanıza gerek yok, sadece döngünün devam etmesine izin verin
                    }
                }

                // Öğe etkileşimli hale gelene kadar bekleyin
                //Başlık-Elements
                WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByCssSelector("h2[class^='title']")));
                WebElement priceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByCssSelector("h2[class^='price']")));
                WebElement addressElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByCssSelector("div[class='address']")));

                //İletişim Bilgileri
                WebElement contactInformationElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//h5[@class='agent-name']")));

                //Detaylar-Elements
                WebElement releaseDateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-details\"]/div/div/ul/li[1]/span[2]")));
                WebElement adTypeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-details\"]/div/div/ul/li[2]/span[2]")));
                WebElement adNumberElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-details\"]/div/div/ul/li[3]/span[2]")));
                WebElement purposeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-details\"]/div/div/ul/li[5]/span[2]")));
                WebElement squareMetersElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-details\"]/div/div/ul/li[6]/span[2]")));

                //İmkanlar-Oda-Elements
                // Aşağıdaki yorum satırları her ilanda değişiklik gösterdiği için yukarıda tek parçada alındı.
                WebElement roomQuantityElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-amenities\"]/div/div[1]/div/div[2]/div")));
                //WebElement roomQuantityElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-details\"]/div/div/ul/li[7]/span[2]")));
                //WebElement bathroomQuantityElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-details\"]/div/div/ul/li[8]/span[2]")));
                //WebElement kitchenQuantityElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-amenities\"]/div/div[1]/div/div[2]/div/div[3]/div/span[2]")));

                //Temel Özellikler-Elements
                WebElement basicFeaturesElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-overview\"]/div")));

                //Genel Bakış
                WebElement overviewElement = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByXPath("//*[@id=\"section-overview\"]/div")));

                //Başlık-getText
                String agentId = driver.findElement(By.name("agent_id")).getAttribute("value");
                String title = titleElement.getText();
                String price = priceElement.getText();
                String address = addressElement.getText();

                //İletişim Bilgileri-getText
                String contactInformation = contactInformationElement.getText();

                //DETAYLAR-getText
                String adReleaseDate = releaseDateElement.getText();
                String adType = adTypeElement.getText();
                String adNumber = adNumberElement.getText();
                String adPurpose = purposeElement.getText();
                String adSquareMeters = squareMetersElement.getText();

                //İmkanlar-getText
                String adRoomQuantity = roomQuantityElement.getText();

                //Temel Özellikler-getText
                String adBasicFeatures = basicFeaturesElement.getText();

                //Genel Bakış-getText
                String overview = overviewElement.getText();




                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO listing (title, price, address, name, release, type, no, purpose, square, room, features, overview) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    preparedStatement.setString(1, title);
                    preparedStatement.setString(2, price);
                    preparedStatement.setString(3, address);
                    preparedStatement.setString(4, contactInformation);
                    preparedStatement.setString(5, adReleaseDate);
                    preparedStatement.setString(6, adType);
                    preparedStatement.setString(7, adNumber);
                    preparedStatement.setString(8, adPurpose);
                    preparedStatement.setString(9, adSquareMeters);
                    preparedStatement.setString(10, adRoomQuantity);
                    preparedStatement.setString(11, adBasicFeatures);
                    preparedStatement.setString(12, overview);
                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Page " + page);
                //System.out.println("ID         : " + id);
                System.out.println("Title      : " + title);
                System.out.println("Price      : " + price);
                System.out.println("Address    : " + address);
                System.out.println("Contact Information    : " + contactInformation);
                System.out.println("Release Date    : " + adReleaseDate);
                System.out.println("Ad Type    : " + adType);
                System.out.println("Ad Number    : " + adNumber);
                System.out.println("Purpose    : " + adPurpose);
                System.out.println("Square Meters    : " + adSquareMeters);
                System.out.println("Room Quantity    : " + adRoomQuantity);
                System.out.println("Basic Features    : " + adBasicFeatures);
                System.out.println("Overview    : " + overview);
                System.out.println("------------------------------");

                File ilanKlasoru = new File("/Users/karinriverside/Desktop/CrawlerImageFile/" + agentId);
                ilanKlasoru.mkdir();

                ilanKlasoru = new File("/Users/karinriverside/Desktop/CrawlerImageFile/" + agentId + "/" + title);
                ilanKlasoru.mkdir();

                int imageIndex = 1;
                List<WebElement> imageElements = driver.findElements(By.className("img-fluid"));
                for (WebElement imageElement : imageElements) {
                    String imageUrl = imageElement.getAttribute("src");
                    try {
                        BufferedImage image = ImageIO.read(new URL(imageUrl));
                        if (image != null) {
                            File outputfile = new File(ilanKlasoru, "image" + imageIndex + ".png");
                            // Belirli bir alanın dışındaki kısmın kesilmesi
                            BufferedImage croppedImage = cropOutsideTopArea(image, 1021, 90);
                            ImageIO.write(croppedImage, "png", outputfile);
                            imageIndex++;
                        } else {
                            System.out.println("Failed to download image from: " + imageUrl);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                driver.navigate().back();
            }
        }

        try {
            if (connection != null) {
                connection.close();
            }
            driver.quit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage cropOutsideTopArea(BufferedImage source, int cropWidth, int cropHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Kesecek bölgenin koordinatları
        int x = 0;
        int y = Math.min(sourceHeight, cropHeight);

        // Kesecek bölgenin boyutları
        int width = sourceWidth;
        int height = Math.max(0, sourceHeight - cropHeight);

        // Kırpma işlemi
        return source.getSubimage(x, y, width, height);
    }

}
