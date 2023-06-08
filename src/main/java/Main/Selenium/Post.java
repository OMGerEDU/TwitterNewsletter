package Main.Selenium;

public class Post {
    public String username,fullname,content,timePosted,imageSrc,postSource;



    public Post(String username, String fullname, String content, String timePosted, String postSource, String imageSrc) {
        this.username = username;
        this.fullname = fullname;
        this.content = content;
        this.timePosted = timePosted;
        this.postSource = postSource;
        this.imageSrc = imageSrc;
    }

    public Post(String username, String fullname, String content, String timePosted) {
        this.username = username;
        this.fullname = fullname;
        this.content = content;
        this.timePosted = timePosted;
    }

    public Post(String username, String fullname, String content, String timePosted, String postSource) {
        this.username = username;
        this.fullname = fullname;
        this.content = content;
        this.timePosted = timePosted;
        this.postSource = postSource;
    }

    public Post() {
    }


    public Post(String username, String timePosted,String content) {
        this.username = username;
        this.timePosted = timePosted;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }

    public String getPostSource() {
        return postSource;
    }

    public void setPostSource(String postSource) {
        this.postSource = postSource;
    }
}
