package migros;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import java.time.Duration;
import java.util.*;

public class migros02 {

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);

        // www.migros.com.tr'ye git
        driver.get("https://www.migros.com.tr/");

        // cerezleri reddet
        driver.findElement(By.xpath("//*[text()='Tümünü Reddet']")).click();

        //www.migros.com.tr sayfasinin acildigini teyit et
        WebElement pageControlElement = driver.findElement(By.xpath("//a[@id='header-sanalmarket-tab']"));

        Assert.assertTrue(pageControlElement.isDisplayed(), "Kullanici Dogru Sayfadadir. ");
        Thread.sleep(1500);

        //migros hemeni tikla
        WebElement migrosHemenTabElement = driver.findElement(By.xpath("//a[@id='header-hemen-tab']"));
        migrosHemenTabElement.click();

        //teslimat yontemini belirleye tikla
        driver.findElement(By.xpath("//*[text()='Teslimat Yöntemini Belirle']")).click();
        Thread.sleep(2000);
        //adresime gelsine tikla
        driver.findElement(By.xpath("//div[@class='subtitle-1 text-align-center']")).click();

        //arama cubuguna "Yelki, Guzelbahce/İzmir, Turkiye" yaz
        WebElement adresElementi = driver.findElement(By.xpath("//input[@id='mat-input-0']"));
        adresElementi.sendKeys("Yelki, Güzelbahçe/İzmir, Türkiye");
        Thread.sleep(2500);
        adresElementi.click();
        //cikan seceneklerden ilkini tikla
        Thread.sleep(2000);
        actions.keyDown(Keys.ARROW_DOWN).keyDown(Keys.ENTER).perform();

        //isaretledigim Konumu Ekle butonuna bas
        driver.findElement(By.xpath("//*[text()=' İşaretlediğim Konumu Ekle ']")).click();
        Thread.sleep(1500);
        // Evet, Adresim Doğru secenegini tikla
        driver.findElement(By.xpath("//*[text()=' Evet, Adresim Doğru ']")).click();
        Thread.sleep(1500);
        // Listeyi asagi kaydir
        actions.keyDown(Keys.PAGE_DOWN)
                .keyDown(Keys.PAGE_DOWN)
                .perform();

        // Kategorileri bul ve yazdir
        List<WebElement> kategorilerElementiList = driver.findElements(By.xpath("//div[@class='main-category-tabs']"));
        for (int i = 0; i < kategorilerElementiList.size(); i++) {
            System.out.println(i + 1 + "-) " + kategorilerElementiList.get(i).getText());
        }
        // et balik tikla
        WebElement etBalikElementi = driver.findElement(By.xpath("//span[@class='subtitle-2 text-color-black' and text()='Et & Tavuk & Balık']"));
        jse.executeScript("arguments[0].scrollIntoView();", etBalikElementi);
        Thread.sleep(1500);
        etBalikElementi.click();
        Thread.sleep(2500);
        actions.keyDown(Keys.END).perform();

        // urunleri bul
        List<WebElement> urunlerElementiList = driver.findElements(By.xpath("//sm-list-page-item/*"));
        Thread.sleep(1500);

        // urunleri karistir
        Collections.shuffle(urunlerElementiList);

        // Butce limiti
        double butceLimiti = 700.0;
        double toplamFiyat = 0.0;

// urunleri tikla ve sepete ekle
        for (WebElement urun : urunlerElementiList) {
            String urunAdi = urun.getText();
            try {
                // urun fiyatini al
                WebElement urunFiyatiElement = urun.findElement(By.xpath("//span[@id='new-amount']"));
                String urunFiyatiString = urunFiyatiElement.getText()
                        .replaceAll("[^0-9.,]+", "")
                        .replace(",", ".")
                        .trim();
                double urunFiyati = Double.parseDouble(urunFiyatiString);

                // Toplam fiyati guncelle
                double guncelToplamFiyat = toplamFiyat + urunFiyati;

                // Butceyi asacak mi kontrol et
                if (guncelToplamFiyat < butceLimiti) {
                    // urunu sepete ekle
                    WebElement product = urun.findElement(By.xpath(".//*[@class='ng-fa-icon add-to-cart-button ng-star-inserted']"));
                    Thread.sleep(2000);
                    jse.executeScript("arguments[0].click();", product);
                    System.out.println("Secilen urun: " + urunAdi);

                    // Toplam fiyati guncelle
                    toplamFiyat = guncelToplamFiyat;

                    System.out.println("alisveris sepeti tutari: "+guncelToplamFiyat +" TL");
                    System.out.println("-----------------------------------------------------");
                } else {
                    System.out.println("alisveris sepeti asim yapildigindaki tutar: "+guncelToplamFiyat +" TL");
                    System.out.println("Butce limiti asildi! Alısveris durduruldu.");
                    break;
                }
            } catch (NoSuchElementException e) {
                System.out.println("Hata: Element bulunamadi");
                e.printStackTrace();
            }
            Thread.sleep(2000);
        }

        // Driver'ı kapat
        driver.quit();
    }
}
