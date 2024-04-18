package com.example.groomver;

public class User {
    private String userName;
    private String password;
    private String userEmail;
    private String key;
    private String UID;


    /**
     * Constructs a new user with the specified username, password, email, and UID.
     * @param userName The username of the user.
     * @param password The password of the user.
     * @param userEmail The email address of the user.
     * @param UID The unique identifier (UID) of the user.
     */
    public User(String userName, String password, String userEmail, String UID) {
        this.userName = userName;
        this.password = password;
        this.userEmail = userEmail;
        this.UID = UID;
    }

    /**
     * Default constructor.
     */
    public User(){

    }


    /**
     * Returns the UID of the user.
     * @return The UID of the user.
     */
    public String getUID() {
        return UID;
    }

    /**
     * Sets the UID of the user.
     * @param UID The UID to set.
     */
    public void setUID(String UID) {
        this.UID = UID;
    }

    /**
     * Returns the username of the user.
     * @return The username of the user.
     */
    public String getUserName(){
        return this.userName;
    }

    /**
     * Returns the password of the user.
     * @return The password of the user.
     */
    public String getPassword(){
        return this.password;
    }

    /**
     * Returns the email address of the user.
     * @return The email address of the user.
     */
    public String getUserEmail(){
        return this.userEmail;
    }

    /**
     * Sets the username of the user.
     * @param userName The username to set.
     */
    public void setUserName(String userName){
        this.userName = userName;
    }

    /**
     * Sets the password of the user.
     * @param password The password to set.
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Sets the email address of the user.
     * @param userEmail The email address to set.
     */
    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }

    /**
     * Sets the unique key associated with the user.
     * @param key The key to set.
     */
    public void setKey(String key){
        this.key = key;
    }

    /**
     * Returns the unique key associated with the user.
     * @return The unique key associated with the user.
     */
    public String getKey(){
        return this.key;
    }
}
