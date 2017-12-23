package com.cykiq.studyingalphaquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cykiq.studyingalphaquiz.Data.Question;

/**
 * Created by arifu on 7/29/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "quiz_database";
    private static final String TAG = "DatabaseHandler";




    private final Context myContext;
    private static DatabaseHandler mInstance;
    private static SQLiteDatabase db;


    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext=context;
        db = this.getWritableDatabase();


    }
    public static DatabaseHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHandler(context);
        }
        return mInstance;
    }
    public SQLiteDatabase getMyWritableDatabase() {
        if ((db == null) || (!db.isOpen())) {
            db = this.getWritableDatabase();
        }

        return db;
    }

    @Override
    public void close() {
        super.close();
        if (db != null) {
            db.close();
            db = null;
        }
        Log.d(TAG,"Database closed");
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TableQuizQuestions.NAME+" ( '_id' INTEGER PRIMARY KEY AUTOINCREMENT, '"
                + TableQuizQuestions.Columns.BODY+"' VARCHAR(255) , '"
                + TableQuizQuestions.Columns.OPTIONS+"' VARCHAR(255) ,'"
                + TableQuizQuestions.Columns.CORRECT_ANS+"' INTEGER ,'"
                + TableQuizQuestions.Columns.ANSWER_DISCRIPTION+"' VARCHAR(255))");
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void insertQuestion(Question quizQuestion){

        SQLiteDatabase db = this.getMyWritableDatabase();
        db.delete(TableQuizQuestions.NAME,columnNameEqualsValue(TableQuizQuestions.Columns.QUID,quizQuestion.id+""),null);
        //db.delete("user_details", "name='user_data'", null);

        ContentValues values = new ContentValues();
        values.put(TableQuizQuestions.Columns.QUID, quizQuestion.id);

        values.put(TableQuizQuestions.Columns.BODY, quizQuestion.body);
        values.put(TableQuizQuestions.Columns.TOPIC, quizQuestion.topic);
        String temp="";
        for(int i=0; i<quizQuestion.options.length;i++){
            if(i==0)temp+=quizQuestion.options[i];
            else temp+=","+quizQuestion.options[i];
        }
        values.put(TableQuizQuestions.Columns.OPTIONS, temp);

        values.put(TableQuizQuestions.Columns.CORRECT_ANS, quizQuestion.correct);
        values.put(TableQuizQuestions.Columns.ANSWER_DISCRIPTION, quizQuestion.answer_discription);
        values.put(TableQuizQuestions.Columns.DIFFICULTYLEVEL, quizQuestion.difficulty_level);

        temp="";
        for(int i=0; i<quizQuestion.choiceCount.length;i++){
            if(i==0)temp+=quizQuestion.choiceCount[i];
            else temp+=","+quizQuestion.choiceCount[i];
        }
        values.put(TableQuizQuestions.Columns.CHOICE_COUNT, temp);



//    public QuizQuestion(int quid, String body, List<String> options, int correctAnswer, String answerDiscription, String topic, String difficultyLevel, List<Integer> choiceCount) {

        db.insert(TableQuizQuestions.NAME, null, values);
        db.close();
        Log.d(TAG,"question saved to db successfully");
    }



    public static class TableQuizQuestions{
        public static final String NAME = "table_quiz_question";
        public static final class Columns{
            public static final String QUID ="quid";
            public static final String TOPIC ="topic";
            public static final String BODY ="question_body";
            public static final String OPTIONS ="options";
            public static final String DIFFICULTYLEVEL = "defficulty_level";
            public static final String CHOICE_COUNT = "choice_count";
            public static final String CORRECT_ANS = "correct_ans";
            public static final String ANSWER_DISCRIPTION = "answer_dicription";
        }
    }


    String columnNameEqualsValue(String columnName,String value){
        return columnName+" = "+"'"+value+"'";
    }




    String selectFrom(String selection, String from){
        return "SELECT "+selection+" FROM "+from;
    }


}
