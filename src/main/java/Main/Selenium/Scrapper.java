package Main.Selenium;

import Main.SQL.MySQL;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Scrapper {
    int timeOut = 30;
    int shortTimeOut = 50;
    int notThatShortTimeOut =1;
    Duration timeOutDuration = Duration.ofSeconds(timeOut);
    Duration shortTimeOutDuration = Duration.ofMillis(shortTimeOut);
    Duration notThatShortTimeOutDuration = Duration.ofSeconds(notThatShortTimeOut);
    List<Post> PostsScrapped = new ArrayList<>();
    String onlyPosts = " -filter:replies";
    List<String> postsContent = new ArrayList<String>();
    String content,timePosted, userName, fullName,postSourceXpath,userNameXpath,postSource, imageSource,formatted;
    String contentXpath,timePostedXpath,postedByXpath,fullnameXpath,postImageXpath,videoXpath;
    Boolean hasPic = false;
    Boolean foundUserName = false;
    Boolean foundFullName = false;
    Boolean foundContent = false;
    Boolean foundDaytime = false;
    Boolean postSaucefound = false;

    //Filter filter = new Filter();
    List<Post> threadPosts = new LinkedList<>();



    public List<Post> searchEvolved(String searchInput) throws InterruptedException {
        searchInput = searchInput+"-filter:replies";
        String searchInputXpath = "/div/div/div[2]/main/div/div/div/div[1]/div/div[1]/div[1]/div/div/div/div/div[2]/div[2]/div/div/div/form/div[1]/div/div/label/div[2]/div/input";
        // Driver initiallize
        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY,"true");
        WebDriverManager.chromedriver().clearResolutionCache().setup();
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        //

        driver.get("https://twitter.com/explore");
        submitSearch(driver,searchInput);
        Dimension dm = new Dimension(1080,1920);
        setToLatest(driver);
        driver.manage().window().setSize(dm);
        System.out.println("Good morning, searching for "+searchInput);
        WebDriverWait webDriverWait = new WebDriverWait(driver,shortTimeOutDuration);
        long started = System.currentTimeMillis() / 1000L;
        for(int i=0;i<5;i++) {
            for(int j=0;j<30;j++) {
                contentXpath = returnContent(driver,j);
                if(foundContent) {
                    evolved(driver,j);
                }
                TimeUnit.MILLISECONDS.sleep(100);
            }
            if(threadPosts.size()==0) {
                System.out.println("Week skipped on "+searchInput);
                return threadPosts;
            }
            js.executeScript("window.scrollBy(0,1000)", "");
        }
        // Print for check
        //for(int i=0;i<PostsScrapped.size();i++)
        //System.out.println(PostsScrapped.get(i).content);
        long finished = System.currentTimeMillis() / 1000L;
        System.out.println(finished - started + " Seconds took, "+ threadPosts.size()+" scrapped   ");
        driver.close();
        return threadPosts;
    }


    public void evolved(WebDriver driver, int j) {
        // Return result.
        Post post = combineToPost(driver,j);
        // Assert.
        if(post.getTimePosted()!=null) {
            // No dupes please.
            threadPosts.removeIf(s -> s.getContent().contains(content));
            threadPosts.add(post);
        }
    }


    public Post combineToPost(WebDriver driver,int j) {
        postImageXpath = returnPic(driver,j);
        userNameXpath = returnUserName(driver,j);
        fullnameXpath = returnFullName(driver,j);
        timePostedXpath = returnDaytime(driver,j);
        postSourceXpath = returnPostSauce(driver,j);
        //contentXpath = returnContent(driver,j); // checking at the function call.

        // Act.
        if(hasPic) {
            imageSource = driver.findElement(By.xpath(postImageXpath)).getAttribute("src").toString();
        }
        else imageSource = null;
        //
        if(foundUserName&&foundFullName&&foundContent&&foundDaytime&&postSaucefound) {
            userName = driver.findElement(By.xpath(userNameXpath)).getAttribute("innerText");
            fullName = driver.findElement(By.xpath(fullnameXpath)).getAttribute("innerText");
            content = driver.findElement(By.xpath(contentXpath)).getAttribute("innerText");
            timePosted = driver.findElement(By.xpath(timePostedXpath)).getAttribute("datetime");
            postSource = driver.findElement(By.xpath(postSourceXpath)).getAttribute("href");

        }
        // Return result.
        Post post = new Post();
        if(userName!=null&&fullName!=null&&content!=null&&timePosted!=null&&postSource!=null)
            post = new Post(userName,fullName ,content,timePosted,postSource);
        if (imageSource!=null)
            post.setImageSrc(postSource);

        return post;
    }


    List<Post> deepSearch(Keyword keyword, int runs) throws SQLException, InterruptedException {
        System.out.println("Received: "+keyword.keyword);
        LinkedList<List<Post>> AllPosts = new LinkedList<>();
        long currentDate = Calendar.getInstance().getTimeInMillis();
        long oldWeek = currentDate;
        long week = 604800000;
        List<Post> threadPosts = new LinkedList<>();
        boolean flag = false;
        for (int i=0;i<runs;i++) {
            currentDate = oldWeek;
            oldWeek = currentDate - week;
            formatted = keyword.keyword + " since:" + timeToString(oldWeek) + " until:" + timeToString(currentDate) + " ";
            System.out.println("Searching for the phrase: " + formatted);
            AllPosts.add(searchEvolved(formatted));
            //Conditions based searching, in case of 3 blanks in a row, will return null as "no posts found" flag.
            if (AllPosts.get(i).size() == 0) {
                if(flag)
                    return null;
                else if(week!=604800000&&week!=1209600000)
                    flag = true;
                else
                    week*=2;
            }
        }
        MySQL sql = new MySQL();
        int before = threadPosts.size();
        //LinkedList<Post> fromDatabase = sql.PostsFromKeyword(keyword);
        System.out.println("I shall be now, "+before+" Beforehead");
        System.out.println("should start");
        List<Post> toReturn = sql.CombineAndFilter(AllPosts);
        //When using the database.
        //List<Post> toInject = sql.CompareAndScrape(AllPosts,fromDatabase);
        //sql.uploadByList(toInject,keyword);
        //System.out.println("Upload"+toInject.size()+" posts successully ");
        //return toInject;
        return toReturn;
    }


    //region check multiple xPaths for each parameter.
    public String returnPic(WebDriver driver,int j) {
        ArrayList<String> pictureXpaths = new ArrayList<>();
        pictureXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[2]/div/section/div/div/div["+j+"]/div/div/article/div/div/div/div[2]/div[2]/div[2]/div[2]/div/div/div/div/div/a/div/div[2]/div/img");
        pictureXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[2]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[2]/div[2]/div/div/div/div/div/a/div/div[2]/div/img");
        pictureXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[2]/div/section/div/div/div["+j+"]/div/div/article/div/div/div/div[2]/div[2]/div[2]/div[2]/div/div/div/div/div/a/div/div[2]/div/img");
        pictureXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[2]/div[2]/div/div[2]/div/div[3]/div/div/div/div/a/div/div[2]/div/img");

        for (String pictureXpath:pictureXpaths) {
            try {
                String size = driver.findElement(By.xpath(pictureXpath)).getSize().toString();
                if (size != null) {
                    hasPic = true;
                    return pictureXpath;
                }
            } catch (Exception e) {
            }
        }
        hasPic = false;
        return null;
    }

    public String returnUserName(WebDriver driver, int j) {
        ArrayList<String> userNameXpaths = new ArrayList<>();
        userNameXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[3]/div/section/div/div/div["+j+"]/div/div/article/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/div[2]/div/div[1]/a");
        userNameXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[2]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div[1]/div/div[2]/div/a/div");
        userNameXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[2]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div[1]/div/div[2]/div/div/a/div/span");
        userNameXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div/div/div[2]/div/div[1]/a/div/span");
        for (String userNameXpath:userNameXpaths) {
            try {
                String size = driver.findElement(By.xpath(userNameXpath)).getSize().toString();
                if (size != null) {
                    foundUserName = true;
                    return userNameXpath;
                }
            } catch (Exception e) {
            }
        }
        System.out.println("username failed");
        foundUserName = false;
        return null;
    }


    public String returnContent(WebDriver driver, int j) {
        ArrayList<String> contentXpaths = new ArrayList<>();
        contentXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[3]/div/section/div/div/div["+j+"]/div/div/article/div/div/div[2]/div[2]/div[3]/div");
        contentXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[2]/div/section/div/div/div["+j+"]/div/div/article/div/div/div/div[2]/div[2]/div[2]/div[1]");
        contentXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[2]/div/section/div/div/div[" + j + "]/div/div/div/article/div/div/div/div[2]/div[2]/div[2]/div[1]/div");
        contentXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[2]/div/section/div/div/div[" + j + "]/div/div/div/article/div/div/div/div[2]/div[2]/div[2]/div[1]/div");
        contentXpaths.add("/html/body/div[3]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[2]/div[1]");
        contentXpaths.add("/html/body/div[3]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[2]/div[1]/div");
        contentXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[2]/div[1]");

        for (String contentXpath : contentXpaths) {
            try {
                String size = driver.findElement(By.xpath(contentXpath)).getSize().toString();
                if (size != null) {
                    foundContent = true;
                    System.out.println(contentXpath);
                    return contentXpath;
                }
            } catch (Exception e) {
            }
        }
        foundContent = false;
        return null;

    }


    public String returnFullName(WebDriver driver,int j) {
        ArrayList<String> fullNameXpaths = new ArrayList<>();
        fullNameXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[3]/div/section/div/div/div["+j+"]/div/div/article/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/div[1]");
        fullNameXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[2]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div[1]/div/div[1]/a/div/div[1]/span/span");
        fullNameXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[2]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div[1]/div/div[1]");
        fullNameXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div/div/div[1]/div/a/div/div[1]/span/span");
        fullNameXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[3]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div/div/div[1]");

        for (String fullNameXpath:fullNameXpaths) {
            try {
                String size = driver.findElement(By.xpath(fullNameXpath)).getSize().toString();
                if (size != null) {
                    foundFullName = true;
                    return fullNameXpath;
                }
            } catch (Exception e) { }
        }

        System.out.println("fullName failed");
        foundFullName = false;
        return null;
    }

    public String returnDaytime(WebDriver driver,int j) {
        ArrayList<String> timeXpaths = new ArrayList<>();
        timeXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[3]/div/section/div/div/div["+j+"]/div/div/article/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/div[2]/div/div[3]/a/time");
        timeXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[2]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div/div/div[2]/div/div[3]/a/time");
        timeXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[2]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div/div/div[2]/div/div[3]/a/time");
        timeXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[2]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/a/time");
        timeXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div/div/div[2]/div/div[3]/a/time");


        for (String timeXpath:timeXpaths) {
            try {
                String size = driver.findElement(By.xpath(timeXpath)).getSize().toString();
                if (size != null) {
                    foundDaytime = true;
                    return timeXpath;
                }
            } catch (Exception e) { }
        }

        System.out.println("dayTime  failed");
        foundDaytime = false;
        return null;
    }

    public String returnPostSauce(WebDriver driver,int j){
        ArrayList<String> postSauceXpaths = new ArrayList<>();
        postSauceXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[3]/div/section/div/div/div["+j+"]/div/div/article/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/div[2]/div/div[3]/a");
        postSauceXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[2]/div/section/div/div/div[" + j + "]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/a");
        postSauceXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[2]/div/section/div/div/div[" + j + "]/div");
        postSauceXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[2]/div/section/div/div/div" + j + "]");
        postSauceXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[2]/div/section/div/div/div[" + j + "]/div/div/div/article");
        postSauceXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[2]/div/section/div/div/div[" + j + "]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div/div/div[2]/div/div[3]/a");
        postSauceXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/section/div/div/div["+j+"]");
        postSauceXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[3]/div/section/div/div/div["+j+"]/div/div/div/article/div/div/div/div[2]/div[2]/div[1]/div/div/div[1]/div/div/div[2]/div/div[3]/a");


        for (String postSauchXpath : postSauceXpaths) {
            try {
                String size = driver.findElement(By.xpath(postSauchXpath)).getSize().toString();
                if (size != null && driver.findElement(By.xpath(postSauchXpath)).getAttribute("href") != null) {
                    postSaucefound = true;
                    return postSauchXpath;
                }
            } catch (Exception e) { }
        }
        System.out.println("post source failed." +j);
        postSaucefound = false;
        return null;
    }

    public void setToLatest(WebDriver driver) {
        ArrayList<String> lastPostsXpaths = new ArrayList<>();
        lastPostsXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[1]/div[1]/div[2]/nav/div/div[2]/div/div[2]");
        lastPostsXpaths.add("/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[1]/div[2]/nav/div/div[2]/div/div[2]/a");

        for (String xpath:lastPostsXpaths) {
            try {
                driver.findElement(By.xpath(xpath)).click();

            } catch (Exception e) { }
        }

    }

    //endregion

    public String timeToString(long currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentDate);
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH)+1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        return mYear+"-"+mMonth+"-"+mDay;
    }

    public void submitSearch(WebDriver driver, String input) throws InterruptedException {
        String url = driver.getCurrentUrl();
        while (true) {
            TimeUnit.SECONDS.sleep(1);
            driver.findElement(By.tagName("input")).sendKeys(input + Keys.ENTER);
            if (driver.getCurrentUrl()!=url) {
                TimeUnit.SECONDS.sleep(1);
                return;
            }
        }

        // Old method -
        // Check if search input is visible and ready to start the task.
        //new WebDriverWait(driver, timeOutDuration).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(searchInputXpath)));

        //for (int i = 0; i < 10; i++)
        //  driver.findElement(By.xpath(searchInputXpath)).sendKeys(Keys.BACK_SPACE);
        //driver.findElement(By.xpath(searchInputXpath)).sendKeys(searchInput);
        //driver.findElement(By.xpath(searchInputXpath)).sendKeys(Keys.ENTER);
    }

}
