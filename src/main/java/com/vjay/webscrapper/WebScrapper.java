package com.vjay.webscrapper;

import com.vjay.webscrapper.model.BuildingDetail;
import com.vjay.webscrapper.model.Query;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.vjay.webscrapper.util.ExcelUtil.readExcel;
import static com.vjay.webscrapper.util.ExcelUtil.writeToExcel;

public class WebScrapper {
    public static WebDriver webDriver;
    private static final String minBudgetOptionId = "buy_minprice";
    private static final String maxBudgetOptionId = "buy_maxprice";
    private static Map<String, String> bhkOptionMap = Map.ofEntries(
            Map.entry("Any", "0"),
            Map.entry("1RK/1BHK", "1"),
            Map.entry("2 BHK", "2"),
            Map.entry("3 BHK", "3"),
            Map.entry("4 BHK", "4"),
            Map.entry("5 BHK", "5"),
            Map.entry("6 BHK", "6"),
            Map.entry("7 BHK", "7"),
            Map.entry("8 BHK", "8"),
            Map.entry("9 BHK", "9"),
            Map.entry("9+ BHK", "10")
    );

    public static void main(String[] args) throws IOException {
        List<Query> queries = readExcel("query.xlsx");

        System.setProperty("webdriver.chrome.driver", "/opt/WebDriver/bin/chromedriver");
        webDriver = new ChromeDriver();
        for (int count = 0; count < queries.size(); count++) {
            extractBuildingDetails(queries.get(count), count );
        }

        webDriver.quit();
    }

    private static void extractBuildingDetails(Query query, int count) throws IOException {

        webDriver.get("https://www.99acres.com/");
        WebDriverWait waitForHome = new WebDriverWait(webDriver, 10);
        WebElement searchBox = waitForHome.until(ExpectedConditions.visibilityOfElementLocated(By.id("keyword")));
        //WebElement searchBox = webDriver.findElement(By.id("keyword"));
        searchBox.click();

        WebElement budgetDropDown = webDriver.findElement(By.id("budget_sub_wrap"));
        budgetDropDown.click();
        String[] budget = query.getBudget().split("-");
        selectMinBudget(budget[0]);
        selectMaxBudget(budget[1]);
        selectBedroomOption(query.getType());
        searchBox.sendKeys(query.getArea());
        searchBox.sendKeys(Keys.RETURN);
        List<BuildingDetail> buildings = extractBuildingDetailsFromResult();

        writeToExcel(buildings, "building_result" + count + ".xlsx");
    }

    private static List<BuildingDetail> extractBuildingDetailsFromResult() {
        try{
            WebDriverWait wait1 = new WebDriverWait(webDriver, 10);
            WebElement result = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[data-label='SEARCH']")));

            List<WebElement> resultDivs = result.findElements(By.cssSelector("div[class='pageComponent srpTuple__srpTupleBox srp']"));
            List<BuildingDetail> buildings = new ArrayList<>();
            for (WebElement resultItem : resultDivs) {
                var table = resultItem.findElement(By.className("srpTuple__tupleTable"));
                var title = table.findElement(By.id("srp_tuple_property_title")).getAttribute("textContent");
                var heading = table.findElement(By.id("srp_tuple_society_heading")).getAttribute("textContent");
                var price = table.findElement(By.id("srp_tuple_price")).getAttribute("textContent");
                var buildingType = table.findElement(By.id("srp_tuple_bedroom")).getAttribute("textContent");
                buildings.add(new BuildingDetail(title, heading, price, buildingType));
            }
            return buildings;
        }catch (Exception exception) {
            System.out.println("Could not extract result.. Skipping");
        }
        return null;
    }


    private static void selectBedroomOption(String option) {
        var bedroomOption = webDriver.findElement(By.id("bedroom_num_wrap"));
        bedroomOption.click();
        List<WebElement> bhkOption = webDriver.findElements(By.xpath("//div[@id = 's_bedroom_num']//a"));
        System.out.println("BHK Options :" + bhkOption.size());
        String desiredVal = bhkOptionMap.get(option);
        System.out.println("Desired Option:" + desiredVal);
        var desiredBhkOption = bhkOption.stream().filter(e -> desiredVal.equals(e.getAttribute("val"))).findFirst();
        if (desiredBhkOption.isPresent()) {
            WebDriverWait wait1 = new WebDriverWait(webDriver, 10);
            WebElement ele = wait1.until(ExpectedConditions.elementToBeClickable(desiredBhkOption.get()));
            ele.click();
        }
    }

    private static void selectMinBudget(String option) {
        List<WebElement> minBudgetOptions = webDriver.findElements(By.xpath("//div[@id = 'buy_minprice']//a"));
        selectOption(minBudgetOptions, option);
    }

    private static void selectMaxBudget(String option) {
        List<WebElement> minBudgetOptions = webDriver.findElements(By.xpath("//div[@id = 'buy_maxprice']//a"));
        selectOption(minBudgetOptions, option);
    }

    private static void selectOption(List<WebElement> options, String option) {
        if (!options.isEmpty()) {
            //System.out.println("Element Size:"+options.size());
            for (WebElement e : options) {
                System.out.println(e.getText());
            }

            var desiredOption = options.stream().filter(ele -> ele.getText().equals(option)).findFirst();
            if (desiredOption.isPresent()) {
                desiredOption.get().click();
            } else {
                System.out.println("Option could not be found");
            }

        }
    }
}
