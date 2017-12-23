package com.cykiq.studyingalphaquiz.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arifu on 7/28/2017.
 */

public class Question {



    //just for informartion.... not to be altered...ask webservice to do the job
    public int id;
    public String body;
    public String[] options;
    public int correct;
    public String answer_discription;
    public int difficulty_level;
    public int[] choiceCount;
    public String img_url;

    public int choice= -1;
    public String topic="no topic";

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", options=" + Arrays.toString(options) +
                ", correct=" + correct +
                ", answer_discription='" + answer_discription + '\'' +
                ", difficulty_level=" + difficulty_level +
                ", choiceCount=" + Arrays.toString(choiceCount) +
                ", img_url='" + img_url + '\'' +
                ", choice=" + choice +
                ", topic='" + topic + '\'' +
                '}';
    }
}
