package migros;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;
import java.util.*;

public class MigrosTest {

    WebDriver driver;
    JavascriptExecutor jse;
    Actions actions;
    Random random = new Random();

    @Before
    public void setUp() {
        // WebDriver'ı başlat
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        jse = (JavascriptExecutor) driver;
        actions = new Actions(driver);
    }

    @Test
    public void migrosTest() throws InterruptedException {
        // Butce limiti
        double butceLimiti = 1000.0;
        double toplamFiyat = 0.0;

        // www.migros.com.tr'ye git
        driver.get("https://www.migros.com.tr/");

        // Pop-up'u kapat
        driver.findElement(By.xpath("//*[text()='Tümünü Reddet']")).click();

        // Migros Hemen sekmesini seç
        driver.findElement(By.id("header-hemen-tab")).click();
        Thread.sleep(2000);

        // Teslimat yöntemini belirle
        driver.findElement(By.xpath("//*[text()='Teslimat Yöntemini Belirle']")).click();
        Thread.sleep(2000);
        // Adresime Gelsin seçeneğini seç
        driver.findElement(By.xpath("//div[text()='Adresime Gelsin']")).click();

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


        // Et & Tavuk & Balık kategorisini seç
        WebElement etBalikElementi = driver.findElement(By.xpath("//span[@class='subtitle-2 text-color-black' and text()='Et & Tavuk & Balık']"));
        //jse.executeScript("arguments[0].scrollIntoView();", etBalikElementi);
        Thread.sleep(2500);
        etBalikElementi.click();
        Thread.sleep(2500);
        actions.keyDown(Keys.END).perform();

        // Ürünleri bul
        List<WebElement> urunlerElementiList = driver.findElements(By.xpath("//sm-list-page-item/*"));
        Thread.sleep(2000);
        // urunleri karistir
        Collections.shuffle(urunlerElementiList);

        // Ürünleri sepete ekle
        for (int i = 0; i < urunlerElementiList.size(); i++) {
            // Rastgele bir ürün seç
            WebElement urun = urunlerElementiList.get(random.nextInt(urunlerElementiList.size()));

            try {
                // Ürün fiyatını al
                WebElement urunFiyatiElement = urun.findElement(By.xpath(".//span[@id='new-amount']"));
                String urunFiyatiString = urunFiyatiElement.getText()
                        .replaceAll("[^0-9.,]+", "")
                        .replace(",", ".")
                        .trim();
                double urunFiyati = Double.parseDouble(urunFiyatiString);

                // Toplam fiyatı güncelle
                double guncelToplamFiyat = toplamFiyat + urunFiyati;

                // Butçeyi aşacak mı kontrol et
                if (guncelToplamFiyat < butceLimiti) {
                    // urunu sepete ekle
                    WebElement product = urun.findElement(By.xpath(".//*[@class='ng-fa-icon add-to-cart-button ng-star-inserted']"));
                    Thread.sleep(2000);
                    jse.executeScript("arguments[0].click();", product);
                    System.out.println("Secilen urun: " + urun.getText());

                    // Toplam fiyati guncelle
                    toplamFiyat = guncelToplamFiyat;

                    System.out.println("alisveris sepeti tutari: " + guncelToplamFiyat + " TL");
                    System.out.println("-----------------------------------------------------");
                } else {
                    System.out.println("alisveris sepeti asim yapildigindaki tutar: " + guncelToplamFiyat + " TL");
                    System.out.println("Butce limiti asildi! Alısveris durduruldu.");
                    break;
                }
            } catch (NoSuchElementException e) {
                System.out.println("Hata: Element bulunamadi");
                e.printStackTrace();
            }
            Thread.sleep(2000);
        }
    }

    @After
    public void tearDown() {
        // WebDriver'ı kapat
        driver.quit();
    }
}
