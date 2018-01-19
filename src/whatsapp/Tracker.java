package whatsapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Tracker {
    static Logger LOGGER = LoggerFactory.getLogger(Tracker.class);
    static final String P1;
    static final String P2;
    static final String P3;
    static final String P4;
    static final String P5;
    static final String P6;
    static final boolean executeViaJar = true;
    static boolean isTimerEnabled = false;
    static short RESETWIFITIME = 40; // MIN

    static {
        // COPERSON = "Sha1";
        P2 = "Ckumar";
        P3 = "Sambu1";
        P4 = "Ammaseey";
        P5 = "prenesh";
        P6 = "Sus1";
        P1 = "Bharathy";// case Sensitive
        // P1 = "Jio";// case Sensitive

        P1TIME = 1100;
        POTHERSTIME = 1100;
    }

    static WebDriver driver;
    static String[] trackReport = {};
    // static Date startTime = null;
    static JavascriptExecutor js = null;
    static short prevLength = 0;
    static short P1TIME;
    static short POTHERSTIME;

    static final String SEARCHINPUT = "#side div input";
    static PrintWriter out;
    static long startTime;

    public static void main(final String... args) {

        // resetWiFi();
        // System.exit(0);
        final String SCRIPTPATH;
        if (executeViaJar != false) {
            // C:/Users/logk/Ugol/git/WhatsAppTrackin
            SCRIPTPATH = "/src/main/resources/tracker_from_selenium.js"; // for
                                                                         // executable
                                                                         // jar
        } else {
            SCRIPTPATH = "src/main/resources/tracker_from_selenium.js";
        }

        LOGGER.info("Started program");
        // startTime = new Date();

        // Optional, if not specified, WebDriver will search your path for chromedriver.
        // System.setProperty("webdriver.chrome.driver", "C:/Users/logk/ugoL/Tools/ChromeDriver/chromedriver.exe");
        // System.setProperty("webdriver.gecko.driver", "C:/Users/logk/ugoL/Tools/mozilla/geckodriver.exe");

        driver = new ChromeDriver();
        // driver = new FirefoxDriver();
        // driver = new InternetExplorerDriver();
        driver.manage().window().maximize();

        LOGGER.info(Paths.get(SCRIPTPATH).toString());
        String trackerScript = null;
        try {
            if (executeViaJar == false) {
                trackerScript = new String(Files.readAllBytes(Paths.get(SCRIPTPATH)));
            } else {
                trackerScript = IOUtils.toString(Tracker.class.getResourceAsStream(SCRIPTPATH)); // for executable jar

            }
        } catch (final NoSuchFileException e) {
            LOGGER.error("NoSuchFileException excetion:" + e.getMessage());
            Runtime.getRuntime().exit(0);
        } catch (final IOException e) {
            LOGGER.error("IO excetion:" + e.getMessage());
            Runtime.getRuntime().exit(0);
        }

        LOGGER.info("Opening Whatsapp...");

        driver.navigate().to("https://web.whatsapp.com");
        final WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(SEARCHINPUT)));
        wait.withTimeout(5, TimeUnit.SECONDS);
        LOGGER.info("Logged in...");

        js = (JavascriptExecutor) driver;
        if (!(trackerScript.isEmpty() || trackerScript == null)) {
            js.executeScript(trackerScript);
        } else {
            LOGGER.error("Script Starts run..");
            Runtime.getRuntime().exit(0);
        }
        LOGGER.info("Script Starts run..");

        // String tName = js.executeScript("return trackingName='" + TRACKPERSON +
        // "';").toString();
        // LOGGER.info("Track Person set to :" + tName);

        try {
            byte profileNo = 0;
            short openProfileCount = 0;
            initializeFileWriter();
            out.println();
            out.println(
                    "___________________________________________________________________________________________");
            out.println();

            final int openProfileCountForResetWifi = RESETWIFITIME * (60 / ((P1TIME + 2000) / 1000));
            while (true) {
                if (isTimerEnabled) {
                    LOGGER.info("Timer starts for : : :" + profileNo);
                    startTime = System.currentTimeMillis();
                }

                openProfileCount++;
                // if(Calendar.getInstance().get(Calendar.MINUTE)/20==0 &&
                // Calendar.getInstance().get(Calendar.SECOND) > 40) {
                if (openProfileCount == openProfileCountForResetWifi) {
                    out.println(new Date().toString() + "  :  Reset WiFi :" + resetWiFi());
                    openProfileCount = 0;
                    if (isTimerEnabled) {
                        LOGGER.info("After reset wifi:" + (System.currentTimeMillis() - startTime));
                        startTime = System.currentTimeMillis();
                    }
                }

                if (isTimerEnabled) {
                    LOGGER.info("Before thread sleep :" + (System.currentTimeMillis() - startTime));
                    startTime = System.currentTimeMillis();
                }

                try {
                    // Thread.sleep(new Random().nextInt(3000+ 1) + 7000);
                    switch (profileNo) {
                        case 0:
                            Thread.sleep(P1TIME);
                            openProfile(wait, P1);
                            break;
                        case 1:
                            Thread.sleep(POTHERSTIME);
                            openProfile(wait, P2);
                            break;
                        case 2:
                            Thread.sleep(POTHERSTIME);
                            openProfile(wait, P3);
                            break;
                        case 3:
                            Thread.sleep(P1TIME);
                            openProfile(wait, P1);
                            break;
                        case 4:
                            Thread.sleep(POTHERSTIME);
                            openProfile(wait, P4);
                            break;
                        case 5:
                            Thread.sleep(POTHERSTIME);
                            openProfile(wait, P5);
                            profileNo = -1;
                            break;
                        case 7:
                            Thread.sleep(POTHERSTIME);
                            openProfile(wait, P1);
                            break;
                        case 6:
                            Thread.sleep(POTHERSTIME);
                            openProfile(wait, P6);
                            profileNo = -1;
                            break;
                    }
                    profileNo++;

                    if (isTimerEnabled) {
                        LOGGER.info("After open profile :" + (System.currentTimeMillis() - startTime));
                        startTime = System.currentTimeMillis();
                    }

                } catch (final InterruptedException e) {
                    LOGGER.error("Thread Sleep Intereption " + e.getMessage());
                }

                if (openProfileCount % 12 == 0) {
                    checkBrowserReport();
                    out.flush();
                    if (isTimerEnabled) {
                        LOGGER.info("After report write :" + (System.currentTimeMillis() - startTime));
                        startTime = System.currentTimeMillis();
                    }
                }

            }
        } catch (final WebDriverException e) {
            out.println("Stopped at:" + new Date().toString());
            out.close();
            e.printStackTrace();
            LOGGER.error("WebDriverException Exception in Tracker " + e.getMessage());
        } catch (final Exception e) {
            out.println("Stopped at:" + new Date().toString());
            out.close();
            LOGGER.error("Exception in Tracker " + e.getMessage());
        }

    }

    public static void initializeFileWriter() throws IOException {
        final FileWriter fw = new FileWriter("C:/Users/logk/Google Drive/WA/trackerREport.log", true);
        // try (FileWriter fw = new FileWriter("C:/Users/logk/trackerREport.log", true);
        final BufferedWriter bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
    }

    public static void checkBrowserReport() {
        @SuppressWarnings("unchecked")
        final String[] browserReport = ((ArrayList<String>) js.executeScript("return getTrackerReport();"))
                .toArray(new String[1]);
        if (browserReport.length > prevLength) {
            /*
             * if (browserReport.length - prevLength > 1) { LOGGER.warn("Array Size differs by:" + (browserReport.length
             * - prevLength)); }
             */
            // browserReport=((ArrayList<String>)
            // js.executeScript("return
            // getTrackerReport();")).toArray(new String[1]);
            // trackReport =browserReport;
            // LOGGER.info("TrackingReport
            // :"+Arrays.asList(trackReport).toString());
            for (int i = browserReport.length - prevLength; i > 0; i--) {
                out.println(new Date().toString() + "  :  " + browserReport[browserReport.length - i]);
            }
            prevLength = (short) browserReport.length;
        }
    }

    public static void openProfile(final WebDriverWait wait, final String trackPerson) throws InterruptedException {
        if (isTimerEnabled) {
            LOGGER.info("After thread wakeup :" + (System.currentTimeMillis() - startTime));
            startTime = System.currentTimeMillis();
        }

        final WebElement ele = driver.findElement(By.cssSelector(SEARCHINPUT));
        WebElement elePerson;
        WebElement eleBack;
        final String elePersonString = "#pane-side div.chat-body > div.chat-main > div.chat-title > span[title='"
                + trackPerson + "']";
        final String eleBackString = "#side > div.chatlist-panel-search > div > button";
        try {
            ele.clear();
            // js.executeScript("arguments[0].value='" + trackPerson + "';", ele);
            // ele.sendKeys("t", Keys.BACK_SPACE);
            ele.sendKeys(trackPerson);

            if (isTimerEnabled) {
                LOGGER.info("After name writes on search bar :" + (System.currentTimeMillis() - startTime));
                startTime = System.currentTimeMillis();
            }

            Thread.sleep(600);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(elePersonString)));

            if (isTimerEnabled) {
                LOGGER.info("After name writes - wait untill :" + (System.currentTimeMillis() - startTime));
                startTime = System.currentTimeMillis();
            }

            elePerson = driver.findElement(By.cssSelector(elePersonString));
            elePerson.click();
            eleBack = driver.findElement(By.cssSelector(eleBackString));
            eleBack.click();
            ele.sendKeys(Keys.BACK_SPACE);

            // js.executeScript("arguments[0].scrollIntoView()", elePerson);
            // final Actions actions = new Actions(driver);
            // actions.moveToElement(elePerson).click().perform();
            // ele.clear();
            // ele.sendKeys("$");
            // driver.findElement(By.cssSelector("#side div.list-search.active > button")).click();

        } catch (final TimeoutException e) {
            // e.addInfo("Excep.where", "Outer timeout catch");
            // e.addInfo("Excep.time", new Date().toString());
            e.printStackTrace();
            LOGGER.error(" Exception in Tracker at" + new Date() + " " + e.getMessage());
            try {
                Thread.sleep(300);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(elePersonString)));
                elePerson = driver.findElement(By.cssSelector(elePersonString));
                js.executeScript("arguments[0].scrollIntoView()", elePerson);
                final Actions actions = new Actions(driver);
                actions.moveToElement(elePerson).click().perform();
                ele.sendKeys("%");

            } catch (final TimeoutException e1) {
                // e1.addInfo("Excep.where", "Inner timeout catch");
                // e1.addInfo("Excep.time", new Date().toString());
                e1.printStackTrace();
                LOGGER.error("Nested Exception in Tracker at" + new Date() + " " + e1.getMessage());
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }
        /*
         * finally { //driver.findElement(By.cssSelector("#side div.list-search > button")).click(); //ele.clear();
         * ele.sendKeys("$"); }
         */
    }

    public static String resetWiFi() {
        try {
            String returnVal = null;
            String ss = null;
            String SSID = null;
            // LOGGER.info("reset wifi : " + SSID);
            // Process p = Runtime.getRuntime().exec("ps -ef");
            final Process p = Runtime.getRuntime().exec("cmd /c netsh wlan show interfaces");

            final BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // Process p = new ProcessBuilder("cmd"," netsh wlan show interfaces ").start();
            /*
             * if(Redirect.PIPE.file() == null && Redirect.PIPE.type() == Redirect.Type.PIPE)
             * LOGGER.info("Redirect is PIPE"); BufferedReader stdInput = new BufferedReader(new
             * InputStreamReader(p.getInputStream())); BufferedReader stdError = new BufferedReader(new
             * InputStreamReader(p.getErrorStream())); OutputStream ot= p.getOutputStream();
             */
            // p.waitFor();
            // BufferedWriter writeer = new BufferedWriter(new
            // OutputStreamWriter(p.getOutputStream()));
            // writeer.write("netsh wlan disconnect");
            // //writeer.write("netsh wlan show interfaces");
            // writeer.flush();
            //
            while ((ss = stdInput.readLine()) != null) {
                // LOGGER.info("1:" + ss);
                if (ss.indexOf("SSID") != -1 && ss.indexOf("BSSID") == -1) {
                    SSID = ss.substring(ss.indexOf(":") + 1);
                    SSID = SSID.trim();
                    // LOGGER.info("Connected wifi : " + SSID);
                    /*
                     * writeer.write("netsh wlan disconnect"); writeer.flush();
                     */

                    final Process p1 = Runtime.getRuntime().exec("cmd /c netsh wlan disconnect");
                    final BufferedReader stdInput1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                    while ((ss = stdInput1.readLine()) != null) {
                        LOGGER.info("2:" + ss);
                    }
                    stdInput1.close();
                    p1.destroy();

                    Thread.sleep(2500);
                    // System.exit(0);
                    // writeer.write("netsh wlan connect \""+"PayPalGuest"+"\"");
                    // writeer.write("netsh wlan connect \""+SSID+"\"");
                    // writeer.flush();
                    final Process p2 = Runtime.getRuntime().exec("cmd /c netsh wlan connect \"" + SSID + "\"");
                    final BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                    while ((ss = stdInput2.readLine()) != null) {
                        if (ss.contains("successfully")) {
                            returnVal = SSID + ": Success";
                            LOGGER.info(returnVal);
                        } else {
                            Thread.sleep(60000);
                            connectToWifi(SSID);
                            returnVal = SSID + ": Delayed Success";
                        }
                    }
                    stdInput2.close();
                    // stdError.close();
                    // stdInput.close();
                    p2.destroy();

                }
            }

            while ((ss = stdError.readLine()) != null) {
                LOGGER.error("Here is the standard error of the command (if any):\n");
                LOGGER.error(ss);
            }

            p.destroy();
            return returnVal;
        } catch (final IOException e) {
            LOGGER.error("FROM CATCH " + e.toString());
            return "Fail";
        } catch (final InterruptedException e) {
            LOGGER.error("Error ", e.getStackTrace());
            return "Fail";
        }
    }

    public static void connectToWifi(final String SSID) throws IOException {
        final Process p = Runtime.getRuntime().exec("cmd /c netsh wlan connect \"" + SSID + "\"");
        p.destroy();
    }

}
