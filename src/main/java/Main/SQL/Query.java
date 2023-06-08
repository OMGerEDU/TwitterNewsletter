package Main.SQL;

import Main.Commands.BotCommands;

public class Query {

    public int id,clientoid,runEvery;
    public String due,phrase,lastRun,email,timesRange;
    public String[] dueArr,lastRunArr;
    public BotCommands.Mode mode;

    public Query(String phrase) {
        this.phrase = phrase;
    }

    public Query(int id, String phrase) {
        this.id = id;
        this.phrase = phrase;
    }

    public Query(int id, int clientoid, String due, String phrase, String lastRun, int runEvery) {
        this.id = id;
        this.clientoid = clientoid;
        this.due = due;
        this.phrase = phrase;
        this.lastRun = lastRun;
        this.runEvery = runEvery;
    }

    public Query(int clientoid, String due, String phrase, String lastRun, String email) {
        this.clientoid = clientoid;
        this.due = due;
        this.phrase = phrase;
        this.lastRun = lastRun;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientoid() {
        return clientoid;
    }

    public void setClientoid(int clientoid) {
        this.clientoid = clientoid;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getLastRun() {
        return lastRun;
    }

    public void setLastRun(String lastRun) {
        this.lastRun = lastRun;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getDueArr() {
        return dueArr;
    }

    public void setDueArr(String[] dueArr) {
        this.dueArr = dueArr;
    }

    public String[] getLastRunArr() {
        return lastRunArr;
    }

    public void setLastRunArr(String[] lastRunArr) {
        this.lastRunArr = lastRunArr;
    }

    public String getTimesRange() {
        return timesRange;
    }

    public void setTimesRange(String timesRange) {
        this.timesRange = timesRange;
    }

    public int getRunEvery() {
        return runEvery;
    }

    public void setRunEvery(int runEvery) {
        this.runEvery = runEvery;
    }

    public Query() {
    }

    public Query(int id, int clientoid, String due, String phrase, String lastRun) {
        this.id = id;
        this.clientoid = clientoid;
        this.due = due;
        this.phrase = phrase;
        this.lastRun = lastRun;
    }



    public Query(int id, int clientoid, String due, String phrase) {
        this.id = id;
        this.clientoid = clientoid;
        this.due = due;
        this.phrase = phrase;
    }

    public Query(int id, int clientoid, String phrase, String[] dueArr, String[] lastRunArr) {
        this.id = id;
        this.clientoid = clientoid;
        this.phrase = phrase;
        this.dueArr = dueArr;
        this.lastRunArr = lastRunArr;
    }
}
