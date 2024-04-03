package com.example.groomver;


public class User {
    private String userName;

    private String password;

    private String userEmail;

    private String key;

    public User(String userName, String password, String userEmail) {
        this.userName = userName;
        this.password = password;
        this.userEmail = userEmail;
    }

    public User(){

    }

    public String getUserName(){
        return this.userName;
    }

    public String getPassword(){
        return this.password;
    }

    public String getUserEmail(){
        return this.userEmail;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }

    public void setKey(String key){
        this.key = key;
    }

    public String getKey(){
        return this.key;
    }
}
