package Models;

public class NewFriendsRequest {

    private String Date;

    public NewFriendsRequest(){}

    public NewFriendsRequest(String Date){
        this.Date = Date;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String friendID) {
        this.Date = Date;
    }

}
