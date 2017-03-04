package com.rohitdeveloper.connectinsight;

import java.io.Serializable;

/**
 * Created by Administrator on 3/2/2017.
 */

public class Person  implements Serializable {
    private String person_id;
    private String person_name;
    private String person_screen_name;
    private String person_location;
    private String person_description;
    private String person_profile_image_url;
    private Float person_similarity_score;

    public Person(){
    }

    public Person(String person_id, String person_name, String person_screen_name, String person_location, String person_description, String person_profile_image_url,Float person_similarity_score) {
        this.person_id = person_id;
        this.person_name = person_name;
        this.person_screen_name = person_screen_name;
        this.person_location = person_location;
        this.person_description = person_description;
        this.person_profile_image_url = person_profile_image_url;
        this.person_similarity_score=person_similarity_score;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPerson_screen_name() {
        return person_screen_name;
    }

    public void setPerson_screen_name(String person_screen_name) {
        this.person_screen_name = person_screen_name;
    }

    public String getPerson_location() {
        return person_location;
    }

    public void setPerson_location(String person_location) {
        this.person_location = person_location;
    }

    public String getPerson_description() {
        return person_description;
    }

    public void setPerson_description(String person_description) {
        this.person_description = person_description;
    }

    public String getPerson_profile_image_url() {
        return person_profile_image_url;
    }

    public void setPerson_profile_image_url(String person_profile_image_url) {
        this.person_profile_image_url = person_profile_image_url;
    }

    public Float getPerson_similarity_score() {return person_similarity_score;}

    public void setPerson_similarity_score(Float person_similarity_score) {
        this.person_similarity_score = person_similarity_score;
    }

}
