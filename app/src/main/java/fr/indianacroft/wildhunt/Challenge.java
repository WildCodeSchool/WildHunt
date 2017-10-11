package fr.indianacroft.wildhunt;

/**
 * Created by apprenti on 10/4/17.
 */

public class Challenge {

    private String challenge_name;
    private String challenge_difficulty;
    private String hint_challenge;
    private String challenge_creatorID;
    private String challenge_questId;

    public Challenge(String challenge_name, String challenge_difficulty, String hint_challenge, String challenge_creatorID, String challenge_questId) {
        this.challenge_name = challenge_name;
        this.challenge_difficulty = challenge_difficulty;
        this.hint_challenge = hint_challenge;
        this.challenge_creatorID = challenge_creatorID;
        this.challenge_questId = challenge_questId;
    }

    public Challenge() {
        // Needed for Firebase
    }

    public String getChallenge_name() {
        return challenge_name;
    }

    public void setChallenge_name(String challenge_name) {
        this.challenge_name = challenge_name;
    }

    public String getChallenge_difficulty() {
        return challenge_difficulty;
    }

    public void setChallenge_difficulty(String challenge_difficulty) {
        this.challenge_difficulty = challenge_difficulty;
    }

    public String getHint_challenge() {
        return hint_challenge;
    }

    public void setHint_challenge(String hint_challenge) {
        this.hint_challenge = hint_challenge;
    }

    public String getChallenge_creatorID() {
        return challenge_creatorID;
    }

    public void setChallenge_creatorID(String challenge_creatorID) {
        this.challenge_creatorID = challenge_creatorID;
    }

    public String getChallenge_questId() {
        return challenge_questId;
    }

    public void setChallenge_questId(String challenge_questId) {
        this.challenge_questId = challenge_questId;
    }


}