package Main.SQL;

import Main.Commands.BotCommands;
import Main.Selenium.Keyword;
import Main.Selenium.Post;
import Main.Selenium.DateHelper;
import Main.Selenium.TaskJob;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MySQL {
Boolean eligible = false;
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3086/parabolicup", "root", "6780771oO");
    public MySQL() throws SQLException {
    }


    public Queue<TaskJob> currentTasks() throws SQLException, InterruptedException {

        final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3086/parabolicup", "root", "6780771oO");
        Statement statement = connection.createStatement();
        Queue<TaskJob> tasks = new LinkedList<>();
        ResultSet resultSet = statement.executeQuery("select * from queries");
        System.out.println("Checking queries...");
        while (true) {

        while (resultSet.next()) {
            eligible =true;
                TaskJob task = new TaskJob(resultSet.getInt("id"),resultSet.getString("clientoid"),
                        resultSet.getString("phrase"), resultSet.getString("lastRun"),
                        resultSet.getString("due"),
                        resultSet.getString("mode"), resultSet.getInt("runEvery"),resultSet.getString("textChannel"));

        if (task.getModeString().equals(BotCommands.Mode.SCHEDULED_TASK.toString())) {
            DateHelper nextRunDate = new DateHelper().stringToDateHelper(task.getLastRun());
            nextRunDate = getReadyForQuery(nextRunDate,task.getFrequency());

            eligible = eligibleForTask(nextRunDate);
            if (!dayInTheFuture(new DateHelper().stringToDateHelper(task.getExpireDate()))) {
                String queryStatement = "delete from queries where id = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(queryStatement);
                deleteStatement.setInt(1, task.getId());
                deleteStatement.execute();
                System.out.println(task.getExpireDate() + " deleted an old queryStatement.");
                eligible = false;
            }
            System.out.println(eligible+" :eligibility on"+task.getPhrase());
            if (eligible)
                tasks.add(task);
        }
        else if (task.getModeString().equals(BotCommands.Mode.NORMAL_QUERY.toString())) {
            tasks.add(task);
            System.out.println("Normal query inserted on the phrase of "+task.getPhrase());
        }

            }

            connection.close();
            TimeUnit.SECONDS.sleep(1);
            return tasks;
        }
    }


    public void insertQuery(TaskJob task) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3086/parabolicup", "root", "6780771oO");
        if (task.getMode().equals(BotCommands.Mode.NORMAL_QUERY)) {
            String query = " insert into queries (phrase,mode)" + " values (?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, task.getPhrase());
            statement.setString(2,"NORMAL_QUERY");
            statement.execute();
            System.out.println("query added.");
            connection.close();
        }
        else if (task.getMode().equals(BotCommands.Mode.SCHEDULED_TASK)) {
            String query = " insert into queries (clientoid,phrase,due,lastRun,runEvery,mode)" + " values (?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,task.getEvent().getMember().getId());
            statement.setString(2,task.getPhrase());
            statement.setString(3, task.getExpireDate());
            statement.setString(4,task.getLastRun());
            statement.setInt(5,task.getFrequency());
            statement.setString(6,task.getMode().toString());
            statement.execute();
            connection.close();

        }

    }

    public ArrayList<String> getUserQueries(String clientoid) throws SQLException {
        String query = "select * from queries where clientoid = ?";
        Statement statement = connection.createStatement();
        ArrayList<String> queries = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("select * from queries where clientoid = "+clientoid);
        while (resultSet.next())
             queries.add(resultSet.getString("phrase"));
        return queries;

    }



    public void deleteQuery(String keyword) throws SQLException {

        String queryStatement = "delete from queries where phrase = ?";
        PreparedStatement deleteStatement = connection.prepareStatement(queryStatement);
        deleteStatement.setString(1, keyword);
        deleteStatement.execute();
        connection.close();
    }

    public String searchForValidTextChannel(String clientoid) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println(clientoid);
        ResultSet resultSet = statement.executeQuery("select * from queries where clientoid = "+clientoid);
        while (resultSet.next()) {
            if (!resultSet.getString("textChannel").equals("null")) {
                setTextRoomID(resultSet.getString("textChannel"),clientoid);
                return resultSet.getString("textChannel");
            }
        }
        return "null";

    }

    public void deleteQuery(String clientoid,String phrase) throws SQLException {

        String queryStatement = "delete from queries where clientoid = ? and phrase = ?";
        PreparedStatement deleteStatement = connection.prepareStatement(queryStatement);
        deleteStatement.setString(1, clientoid);
        deleteStatement.setString(2, phrase);
        deleteStatement.execute();
        connection.close();
    }


    public  void setLastRun(TaskJob taskJob) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3086/parabolicup", "root", "6780771oO");
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DATE);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);



        String formatted = mYear + "/" + mMonth + "/" + mDay + "/" + mHour + "/" + mMinute;
        // Set last run to now.
        System.out.println(formatted+" the one that should be updated to.");
        String queryStatement = "update queries set lastRun = ? where id = ? ";

        PreparedStatement updateStatement = connection.prepareStatement(queryStatement);
        updateStatement.setString(1, formatted);
        updateStatement.setInt(2, Integer.parseInt(String.valueOf(taskJob.getId())));
        updateStatement.executeUpdate();
        System.out.println("updated query on id:"+taskJob.getId());
    }

    public void setTextRoomID(String serverID,String memberID) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3086/parabolicup", "root", "6780771oO");
        String queryStatement = "update queries set textChannel = ? where clientoid = ? ";

        PreparedStatement updateStatement = connection.prepareStatement(queryStatement);
        updateStatement.setString(1,  serverID);
        updateStatement.setString(2, memberID );
        updateStatement.executeUpdate();
        System.out.println("should be updated I guess");
    }

    public String getUserRoomID(String clientoid) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3086/parabolicup", "root", "6780771oO");
        Statement statement = connection.createStatement();
        System.out.println(clientoid);
        ResultSet resultSet = statement.executeQuery("select * from queries where clientoid = "+clientoid);
        while (resultSet.next())
        return resultSet.getString("clientoid");


        return null;
    }




    public boolean dayInTheFuture(DateHelper date) {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DATE);

        int year=date.getYear()-mYear;
        int month=date.getMonth()-mMonth;
        int day=date.getDay()-mDay;

        if (month > 0 && year == 0 || year > 0 || month == 0 && year == 0 && day >= 0)
            return true;
        else
            return false;
    }


    public DateHelper getReadyForQuery(DateHelper date, int runEvery) {
        System.out.println("last run on:"+date.getHour()+":"+date.getMin());

        Calendar calendar = Calendar.getInstance();
        int lastDayOfMonth = calendar.getActualMaximum(Calendar.DATE);
        date.setMin(date.getMin()+runEvery);
        if (date.getMin()>60) {
            date.setHour(date.getHour()+(date.getMin() / 60));
            date.setMin(date.getMin()%60);
        }
        if (date.getHour()>24) {
            date.setDay(date.getDay()+(date.getHour() / 24));
            date.setHour(date.getHour()%24);
        }
        if (date.getDay()>lastDayOfMonth) {
            date.setMonth(date.getMonth()+(date.getDay()/30));
            date.setDay(date.getDay()%30);
            //date.day=date.day%lastDayOfMonth;
        }
        if (date.getMonth()>12) {
            date.setYear(date.getMonth()+(date.getMonth()/12));
            date.setMonth(date.getMonth()%12);
        }
        //System.out.println("next run on:"+date.getHour()+":"+date.getMin()+"\n with the frequency of "+runEvery);
        return date;

    }

    public boolean eligibleForTask(DateHelper date) {
        Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mYear = calendar.get(Calendar.YEAR);


        // if the date, which is when the last run scheduled to be already past, the system will detect it and send a flag to
        // specify that this query is ready.
        if (date.getYear()==mYear&&date.getMonth()==mMonth&&date.getDay()==mDay) {
            if (mHour>date.getHour()||mHour==date.getHour()&&mMinute>date.getMin()-1) // -1 to give a spare minute so the post will be already sent at his original time.
                return true;
        }
        else if (date.getYear()==mYear&&date.getMonth()==mMonth&&date.getDay()<mDay||date.getYear()==mYear&&date.getMonth()<mMonth||date.getYear()<mYear)
            return true;

        return false;
    }

    public boolean eligibleForQuery(DateHelper date) {
        Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mYear = calendar.get(Calendar.YEAR);



        if (!dayInTheFuture(date)) {
            //if (date.getHour()>mHour)
            return true;
        }
        // if the date, which is when the last run scheduled to be already past, the system will detect it and send a flag to
        // specify that this query is ready.
        if (date.getYear()==mYear&&date.getMonth()==mMonth&&date.getDay()==mDay) {
            if (mHour>date.getHour()||mHour==date.getHour()&&mMinute>date.getMin()-1) // -1 to give a spare minute so the post will be already sent at his original time.
                return true;
        }
        else if (date.getYear()==mYear&&date.getMonth()==mMonth&&date.getDay()<mDay||date.getYear()==mYear&&date.getMonth()<mMonth||date.getYear()<mYear)
            return true;

        return false;
    }




    public void setToToday(Query query) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3086/parabolicup", "root", "6780771oO");
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DATE);
        int mHour = 00;
        int mMinute = 00;



        String formatted = mDay + "/" + mMonth + "/" + mYear + "/" + mHour + "/" + mMinute;
        // Set last run to now.
        String queryStatement = "update queries set lastRun = ? where id = ? ";
        PreparedStatement updateStatement = connection.prepareStatement(queryStatement);
        updateStatement.setString(1, formatted);
        updateStatement.setInt(2, Integer.parseInt(String.valueOf(query.id)));
        updateStatement.executeUpdate();
        System.out.println("updated query.");

    }

    //region helpers for deep search.
    public LinkedList<Post> PostsFromKeyword(Keyword keyword) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3086/parabolicup", "root", "6780771oO");
        Statement statement = connection.createStatement();
        System.out.println("checking all posts on keyword: "+keyword.keyword);
        LinkedList<Post> posts = new LinkedList<>();
        ResultSet resultSet = statement.executeQuery("SELECT * from posts where keywordoid = "+keyword.oid);
        while (resultSet.next()) {
            //System.out.println("heree");
            posts.add(new Post(resultSet.getString("publisherFullName"),resultSet.getString("publisherUserName"),resultSet.getString("content"),resultSet.getString("timePosted"),resultSet.getString("postSource")));
        }
        return posts;
    }

    public List<Post> CompareAndScrape(LinkedList<List<Post>> fromScrape,List<Post> fromDatabase) {
        List<Post> returnedPosts = new ArrayList<>();

        for (int i=0;i<fromScrape.size();i++) {
            for (int j=0;j<fromScrape.get(i).size();j++) {
                returnedPosts.add(fromScrape.get(i).get(j));
            }
        }
        System.out.println(returnedPosts.size()+" <Remove duplicates from the scrapped posts");
        for (int j = 0; j < returnedPosts.size(); j++) {
            Post post = returnedPosts.get(j);
            String content = returnedPosts.get(j).content;
            returnedPosts.removeIf(s -> s.getContent().contains(content));
            returnedPosts.add(post);
        }
        System.out.println(returnedPosts.size()+" <size after");
        for (Post postFromDatabase : fromDatabase) {
            for(Post currentScrapedPosts:returnedPosts) {
                if(currentScrapedPosts.postSource.contains(postFromDatabase.postSource)) {
                    returnedPosts.remove(currentScrapedPosts);
                    break;
                }
            }
        }
        System.out.println(returnedPosts.size()+" <Removed posts whom contained in the Database");
        return returnedPosts;
    }


    public List<Post> CombineAndFilter(LinkedList<List<Post>> fromScrape) {
        List<Post> returnedPosts = new ArrayList<>();

        for (int i=0;i<fromScrape.size();i++) {
            for (int j=0;j<fromScrape.get(i).size();j++) {
                returnedPosts.add(fromScrape.get(i).get(j));
            }
        }
        System.out.println(returnedPosts.size()+" <Remove duplicates from the scrapped posts");
        for (int j = 0; j < returnedPosts.size(); j++) {
            Post post = returnedPosts.get(j);
            String content = returnedPosts.get(j).content;
            returnedPosts.removeIf(s -> s.getContent().contains(content));
            returnedPosts.add(post);
        }
        System.out.println(returnedPosts.size()+" <size after");
        return returnedPosts;
    }


    public void uploadByList(List<Post> posts,Keyword keyword) throws SQLException, InterruptedException {
        System.out.println("started uploading on keyowrd "+keyword.keyword);
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3086/parabolicup", "root", "6780771oO");
        String query = " insert into posts (keywordoid,publisherUserName,publisherFullName,content,timePosted,postSource,imageSource)" + " values (?,?,?,?,?,?,?)";
        PreparedStatement statement= connection.prepareStatement(query);;
        int i=1; for(Post post:posts) { i++;
            statement.setInt(1,keyword.oid);
            statement.setString(2, post.username);
            statement.setString(3, post.fullname);
            statement.setString(4, post.content);
            statement.setString(5, post.timePosted);
            statement.setString(6, post.postSource);
            statement.setString(7, post.imageSrc);
            statement.addBatch();
            if(i % 100 == 0) {
                statement.executeBatch();
            }
        }
        statement.executeBatch();
        System.out.println("Upload completed");
        connection.close();
    }




}


