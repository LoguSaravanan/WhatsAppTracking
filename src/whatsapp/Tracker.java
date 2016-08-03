package whatsapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tracker {
	static Logger LOGGER = LoggerFactory.getLogger(Tracker.class);
	static final String TRACKPERSON = "Bharathy";// case Sensitive
	static final String COPERSON="Suresh Kali";
	// static final String COPERSON="Vignesh Suresh";
	//static final String COPERSON = "Anbu";
	static final String SCRIPTPATH = "src/main/resources/tracker_from_selenium.js";
	static WebDriver driver;
	static String[] trackReport = {};

	public static void main(String... args) {

		driver = new ChromeDriver();
		driver.manage().window().maximize();

		LOGGER.info("Opening Whatsapp...");

		driver.navigate().to("https://web.whatsapp.com");
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"side\"]/div[2]/div/label/input")));

		LOGGER.info("Logged in...");

		openProfile(wait, TRACKPERSON);

		String trackerScript = null;
		try {
			trackerScript = new String(Files.readAllBytes(Paths.get(SCRIPTPATH)));
		} catch (NoSuchFileException e) {
			LOGGER.error("NoSuchFileException excetion:" + e.getMessage());
		} catch (IOException e) {
			LOGGER.error("IO excetion:" + e.getMessage());

		}

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(trackerScript);

		LOGGER.info("Script Starts run..");

		String tName = js.executeScript("return trackingName='" + TRACKPERSON + "';").toString();
		LOGGER.info("Track Person set to :" + tName);

		try (FileWriter fw = new FileWriter("trackerREport.log", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			try {
				boolean toggle = false;
				int prevLength = 1;
				while (true) {
					try {
						Thread.sleep(new Random().nextInt((14000 - 7000) + 1) + 7000);
					} catch (InterruptedException e) {
						LOGGER.error("Thread Sleep Intereption " + e.getMessage());
					}
					if (toggle) {
						openProfile(wait, COPERSON);
						toggle = !toggle;
					} else {
						openProfile(wait, TRACKPERSON);
						toggle = !toggle;
					}
					@SuppressWarnings("unchecked")
					String[] browserReport = ((ArrayList<String>) js.executeScript("return getTrackerReport();"))
							.toArray(new String[1]);
					if (browserReport.length > prevLength) {
						if (browserReport.length - prevLength > 1) {
							LOGGER.error("Array Size differs by:" + (browserReport.length - prevLength));
						}

						// browserReport=((ArrayList<String>)
						// js.executeScript("return
						// getTrackerReport();")).toArray(new String[1]);
						// trackReport =browserReport;
						// LOGGER.info("TrackingReport
						// :"+Arrays.asList(trackReport).toString());
						for (int i = browserReport.length - prevLength; i >= 1; i--)
							out.println(browserReport[browserReport.length - (i + 1)]);
						out.flush();
						prevLength = browserReport.length;
					}
				}
			} catch (WebDriverException e) {
				out.println("Stopped at:" + new Date().toString());
				LOGGER.error("WebDriverException Exception in Tracker " + e.getMessage());
			}
		} catch (IOException e) {
			LOGGER.error(" Exception in Tracker REpoert File" + e.getMessage());
		}

	}

	public static void openProfile(WebDriverWait wait, String trackPerson) {
		try {
			WebElement ele = driver.findElement(By.xpath("//*[@id=\"side\"]/div[2]/div/label/input"));
			ele.clear();
			ele.sendKeys(trackPerson);
			// case Insensitive
			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"pane-side\"]/div/div/div/div[1]/div/div/div[2]/div[1]/div/span[matches(@title,'"+TRACKPERSON+"','i')]")));
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//*[@id=\"pane-side\"]/div/div/div/div[1]/div/div/div[2]/div[1]/div/span[@title='"
							+ trackPerson + "']")));
			ele.sendKeys(Keys.ENTER);

		} catch (TimeoutException e) {
			LOGGER.error(" Exception in Tracker at" + new Date() + " " + e.getMessage());
		}
	}

}