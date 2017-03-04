package com.rohitdeveloper.connectinsight;

/**
 * Created by Administrator on 3/4/2017.
 */

public class NetworkModel {
    private Float  similarity_score;
    private Person person_profile;
    private Integer status_code;

    public NetworkModel(){

    }

    public NetworkModel(Float similarity_score, Person person_profile, Integer status_code) {
        this.similarity_score = similarity_score;
        this.person_profile = person_profile;
        this.status_code = status_code;
    }

    public Float getSimilarity_score() {
        return similarity_score;
    }

    public void setSimilarity_score(Float similarity_score) {
        this.similarity_score = similarity_score;
    }

    public Person getPerson_profile() {
        return person_profile;
    }

    public void setPerson_profile(Person person_profile) {
        this.person_profile = person_profile;
    }

    public Integer getStatus_code() {
        return status_code;
    }

    public void setStatus_code(Integer status_code) {
        this.status_code = status_code;
    }
}
