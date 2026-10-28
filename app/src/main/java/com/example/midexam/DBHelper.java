package com.example.midexam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "student.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "students";

    // Class data untuk 1 student
    public static class Student {
        public String id;
        public String name;
        public boolean enrolled;

        public Student(String id, String name, boolean enrolled) {
            this.id = id;
            this.name = name;
            this.enrolled = enrolled;
        }
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "student_id TEXT PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "enrolled INTEGER NOT NULL DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // ADD
    public boolean insertStudent(Student s) {
        ContentValues cv = new ContentValues();
        cv.put("student_id", s.id);
        cv.put("name", s.name);
        cv.put("enrolled", s.enrolled ? 1 : 0);
        long result = getWritableDatabase().insert(TABLE, null, cv);
        return result != -1;
    }

    // EDIT (berdasarkan Student ID)
    public boolean updateStudent(Student s) {
        ContentValues cv = new ContentValues();
        cv.put("name", s.name);
        cv.put("enrolled", s.enrolled ? 1 : 0);
        int rows = getWritableDatabase().update(TABLE, cv, "student_id = ?", new String[]{s.id});
        return rows > 0;
    }

    // DELETE (berdasarkan Student ID)
    public boolean deleteStudent(String id) {
        int rows = getWritableDatabase().delete(TABLE, "student_id = ?", new String[]{id});
        return rows > 0;
    }

    // VIEW
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT student_id, name, enrolled FROM " + TABLE + " ORDER BY name ASC", null);
        while (c.moveToNext()) {
            list.add(new Student(c.getString(0), c.getString(1), c.getInt(2) == 1));
        }
        c.close();
        return list;
    }
}