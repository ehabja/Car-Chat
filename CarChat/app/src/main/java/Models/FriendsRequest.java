package Models;

public class FriendsRequest {

    private String firstUserID , secondUserID;

    public FriendsRequest(){}

    public FriendsRequest(String firstUserID , String secondUserID){
        this.firstUserID = firstUserID;
        this.secondUserID = secondUserID;
    }

    public String getFirstUserID() {
        return firstUserID;
    }

    public void setFirstUserID(String firstUserID) {
        this.firstUserID = firstUserID;
    }

    public String getSecondUserID() {
        return secondUserID;
    }

    public void setSecondUserID(String secondUserID) {
        this.secondUserID = secondUserID;
    }
}
