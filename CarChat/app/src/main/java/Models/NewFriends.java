package Models;

public class NewFriends {

    private String friendID;

    public NewFriends(){}

    public NewFriends(String friendID){
        this.friendID = friendID;
    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }


}
