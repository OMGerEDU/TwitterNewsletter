package Main.Selenium;


public class Filter {
    String keyword;
    boolean noLinksBool;
    boolean noRepliesBool;
    boolean exactPhrase;
    int minFaves;
    int minReplies;
    int minRetweets;



    public Filter(boolean noLinksBool, boolean noRepliesBool, int minFaves, int minReplies) {
        this.noLinksBool = noLinksBool;
        this.noRepliesBool = noRepliesBool;
        this.minFaves = minFaves;
        this.minReplies = minReplies;
    }

    public Filter(boolean noLinksBool, boolean noRepliesBool, int minFaves, int minReplies, int minRetweets) {
        this.noLinksBool = noLinksBool;
        this.noRepliesBool = noRepliesBool;
        this.minFaves = minFaves;
        this.minReplies = minReplies;
        this.minRetweets = minRetweets;
    }

    public Filter() {
    }

    public Filter(boolean noLinksBool, boolean noRepliesBool) {
        this.noLinksBool = noLinksBool;
        this.noRepliesBool = noRepliesBool;
    }



    String noRepliesString = " -filter:replies";
    String noLinks = " -filter:links";
    String replies = "min_replies:";
    String faves = "min_faves:";
    String reTweets = "min_retweets:";


    public String to(String input) {
        String formatted ="(to:"+input+")";
        return formatted;
    }

    public String from(String input) {
        String formatted = " (@"+input+")";
        return formatted;
    }

    public String exact(String input) {
        String formatted = "\""+input+"\"";
        return formatted;
    }



    public String ConditionsToString(String keyword, Filter filter) {
        if (filter.isExactPhrase()) {
            keyword=exact(keyword);
        }
        if(filter.isNoLinksBool())
            keyword+=" "+noLinks;
        if(filter.isNoRepliesBool())
            keyword+=" "+noRepliesString;
        if(filter.getMinFaves()>0)
            keyword+=" "+faves+ filter.getMinFaves()+" ";
        if(filter.getMinReplies()>0)
            keyword+=" "+replies+ filter.getMinReplies()+" ";
        if(filter.getMinRetweets()>0)
            keyword+=" "+reTweets+ filter.getMinRetweets()+" ";

        return keyword;

    }

    public String noReplies() {
        return noRepliesString;
    }

    public boolean isNoLinksBool() {
        return noLinksBool;
    }

    public void setNoLinksBool(boolean noLinksBool) {
        this.noLinksBool = noLinksBool;
    }

    public boolean isNoRepliesBool() {
        return noRepliesBool;
    }

    public void setNoRepliesBool(boolean noRepliesBool) {
        this.noRepliesBool = noRepliesBool;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean isExactPhrase() {
        return exactPhrase;
    }

    public void setExactPhrase(boolean exactPhrase) {
        this.exactPhrase = exactPhrase;
    }

    public int getMinFaves() {
        return minFaves;
    }

    public void setMinFaves(int minFaves) {
        this.minFaves = minFaves;
    }

    public int getMinReplies() {
        return minReplies;
    }

    public void setMinReplies(int minReplies) {
        this.minReplies = minReplies;
    }

    public int getMinRetweets() {
        return minRetweets;
    }

    public void setMinRetweets(int minRetweets) {
        this.minRetweets = minRetweets;
    }

    public String getNoRepliesString() {
        return noRepliesString;
    }

    public void setNoRepliesString(String noRepliesString) {
        this.noRepliesString = noRepliesString;
    }

    public String getNoLinks() {
        return noLinks;
    }

    public void setNoLinks(String noLinks) {
        this.noLinks = noLinks;
    }

    public String getReplies() {
        return replies;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }

    public String getFaves() {
        return faves;
    }

    public void setFaves(String faves) {
        this.faves = faves;
    }

    public String getReTweets() {
        return reTweets;
    }

    public void setReTweets(String reTweets) {
        this.reTweets = reTweets;
    }
}

