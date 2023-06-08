package Main.Selenium;

public class Keyword {
    public int oid;
    public String keyword;

    public Keyword(int oid, String keyword) {
        this.oid = oid;
        this.keyword = keyword;
    }

    public Keyword() {

    }

    public Keyword(String keyword) {
        this.keyword = keyword;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
