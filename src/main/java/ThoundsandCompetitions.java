import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ThoundsandCompetitions {
    static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.get("https://10fastfingers.com/login");

        WebElement email = findElement("UserEmail", BY.ID);
        WebElement password = findElement("UserPassword", BY.ID);
        WebElement loginSubmitBtn = findElement("login-form-submit", BY.ID);

        try (InputStream input = ThoundsandCompetitions.class.getClassLoader().getResourceAsStream("credentials.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            email.sendKeys(prop.getProperty("email"));
            password.sendKeys(prop.getProperty("password"));
            loginSubmitBtn.click();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        driver.get("https://10fastfingers.com/competitions");

        List<String> competitionLinks = driver
                .findElements(By.cssSelector("#join-competition-table tbody tr td .btn-default"))
                .stream()
                .map(link -> link.getAttribute("href"))
                .collect(Collectors.toList());

        int count = competitionLinks.size();

        findElement("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll", BY.ID).click();

        for (String link : competitionLinks) {
            System.out.printf("%s links left%n", count);
            driver.get(link);

            while (true) {
                try {
                    WebElement timer = findElement("timer", BY.ID);

                    if (timer.getText().equals("0:00")) {
                        Thread.sleep(1000);
                        count--;
                        break;
                    } else {
                        WebElement highlight = findElement("highlight", BY.CLASS);
                        WebElement input = findElement("inputfield", BY.ID);

                        input.sendKeys(highlight.getText());
                        input.sendKeys(" ");
                    }
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        driver.quit();
    }

    private static WebElement findElement(String text, BY type) throws InterruptedException {
        WebElement element = null;
        int attempt = 0;
        while (attempt < 3) {
            try {
                if (type == BY.ID) {
                    return driver.findElement(By.id(text));
                } else if (type == BY.CLASS) {
                    return driver.findElement(By.className(text));
                }
            } catch (Exception e) {
                System.out.printf("Error trying to get element %s attempt %s", text, attempt);
                attempt++;
            }
        }

        return element;
    }

    enum BY {
        ID, CLASS
    }
}
