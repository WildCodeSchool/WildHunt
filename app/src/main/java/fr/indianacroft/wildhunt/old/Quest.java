package fr.indianacroft.wildhunt.old;

/**
 * Created by apprenti on 10/4/17.
 */

public class Quest {

    private String quest_name;
    private String quest_description;
//    private String life_duration;
    private String quest_creatorId;

    //
    public Quest() {
        // Needed for firebase
    }

    // Contructor
    public Quest(String quest_name, String quest_description) {
        this.quest_name = quest_name;
        this.quest_description = quest_description;
//        this.life_duration = life_duration;
    }

    public String getQuest_creatorId() {
        return quest_creatorId;
    }

    public void setQuest_creatorId(String quest_creatorId) {
        this.quest_creatorId = quest_creatorId;
    }

    // Attributs
    public String getQuest_name() {
        return quest_name;
    }

    public void setQuest_name(String quest_name) {
        this.quest_name = quest_name;
    }

    public String getQuest_description() {
        return quest_description;
    }

    public void setQuest_description(String quest_description) {
        this.quest_description = quest_description;
    }

//    public String getLife_duration() {
//        return life_duration;
//    }
//
//    public void setLife_duration(String life_duration) {
//        this.life_duration = life_duration;
//    }


}
