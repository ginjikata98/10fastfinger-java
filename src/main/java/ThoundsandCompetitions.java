import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ThoundsandCompetitions {
    static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();

        driver.get("https://10fastfingers.com/login");

        Thread.sleep(3000);
        WebElement cookies = findElement("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll", BY.ID);
        cookies.click();

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

        Thread.sleep(3000);

        List<String> competitionLinks = driver
            .findElements(By.cssSelector("#join-competition-table tbody tr td .btn-default"))
            .stream()
            .map(link -> link.getAttribute("href"))
            .collect(Collectors.toList());

        int count = competitionLinks.size();

        for (String link : competitionLinks) {
            System.out.printf("%s links left", count);
            driver.get(link);

            Thread.sleep(3000);

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
                Thread.sleep(1000);
            }
        }

        return element;
    }

    enum BY {
        ID, CLASS
    }
}
