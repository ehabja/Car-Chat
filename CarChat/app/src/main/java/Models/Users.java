package Models;

import android.view.View;

public class Users {

    private String FirstName , LastName , Password, Img, Status, ThumbImg, Online, Device;

    public Users(){}

    public Users(String firstname , String lastname, String password, String img, String status, String thumbImg, String online, String device){
        FirstName = firstname;
        LastName = lastname;
        Password = password;
        Img = img;
        Status = status;
        ThumbImg = thumbImg;
        Online = online;
        Device = device;

    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) { Img = img; }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getThumbImg(){ return ThumbImg;}

    public void setThumbImg(String thumbImg) {ThumbImg = thumbImg;}

    public String getOnline(){ return Online;}

    public void setOnline(String online) {Online = online;}

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }
}
