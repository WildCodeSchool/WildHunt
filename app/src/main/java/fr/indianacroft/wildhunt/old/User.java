package fr.indianacroft.wildhunt.old;

/**
 * Created by wilder on 06/10/17.
 */

public class User {


    private String user_name;
    private String user_password;
    private String user_quest;
    private String user_createdquestID;
    private String user_createdquestName;
    private String user_indice;
    private String user_challenge;
    private int score;
    private String state;

    public User() {
        // Needed for firebase
    }

    public User(String user_name, String user_password, String user_quest, String user_challenge, int score) {
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_quest = user_quest;
        this.user_challenge = user_challenge;
        this.score = score;
    }

    public User(String user_createdquestID) {
        this.user_createdquestID = user_createdquestID;
    }

    public String getUser_createdquestName() {
        return user_createdquestName;
    }

    public void setUser_createdquestName(String user_createdquestName) {
        this.user_createdquestName = user_createdquestName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUser_challenge() {
        return user_challenge;
    }

    public void setUser_challenge(String user_challenge) {
        this.user_challenge = user_challenge;
    }


    public String getUser_indice() {
        return user_indice;
    }

    public void setUser_indice(String user_indice) {
        this.user_indice = user_indice;
    }

    public String getUser_createdquestID() {
        return user_createdquestID;
    }

    public void setUser_createdquestID(String user_createdquestID) {
        this.user_createdquestID = user_createdquestID;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_quest() {
        return user_quest;
    }

    public void setUser_quest(String user_quest) {
        this.user_quest = user_quest;
    }

}
