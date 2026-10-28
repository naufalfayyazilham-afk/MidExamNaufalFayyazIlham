package com.example.midexam;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private EditText etName, etId;
    private CheckBox cbEnrolled;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<DBHelper.Student> students = new ArrayList<>();
    private DBHelper.Student selectedStudent = null; // student yang dipilih dari list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Paksa light mode supaya teks di list tetap kebaca di background lavender
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);

        etName = findViewById(R.id.etName);
        etId = findViewById(R.id.etId);
        cbEnrolled = findViewById(R.id.cbEnrolled);
        listView = findViewById(R.id.listView);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnView = findViewById(R.id.btnView);

        // List dengan mode pilih 1 item
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice,
                new ArrayList<String>());
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        btnAdd.setOnClickListener(v -> addStudent());
        btnEdit.setOnClickListener(v -> editStudent());
        btnDelete.setOnClickListener(v -> deleteStudent());
        btnView.setOnClickListener(v -> {
            viewStudents();
            toast("List refreshed (" + students.size() + " students)");
        });

        // Pilih item dari list
        listView.setOnItemClickListener((parent, view, position, id) -> {
            DBHelper.Student s = students.get(position);

            // Klik item yang sama lagi = batal pilih
            if (selectedStudent != null && selectedStudent.id.equals(s.id)) {
                clearForm();
                return;
            }

            selectedStudent = s;
            etName.setText(s.name);
            etId.setText(s.id);
            etId.setEnabled(false); // ID dikunci saat edit
            cbEnrolled.setChecked(s.enrolled);
        });

        viewStudents();
    }

    private void addStudent() {
        if (selectedStudent != null) {
            toast("Tap the selected student again to deselect before adding new data");
            return;
        }

        String name = etName.getText().toString().trim();
        String id = etId.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Student Name is required");
            return;
        }
        if (id.isEmpty()) {
            etId.setError("Student ID is required");
            return;
        }

        DBHelper.Student s = new DBHelper.Student(id, name, cbEnrolled.isChecked());
        if (db.insertStudent(s)) {
            toast("Student added");
            clearForm();
            viewStudents();
        } else {
            toast("Failed: Student ID already exists");
        }
    }

    private void editStudent() {
        if (selectedStudent == null) {
            toast("Select a student from the list first");
            return;
        }

        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Student Name is required");
            return;
        }

        DBHelper.Student s = new DBHelper.Student(
                selectedStudent.id, name, cbEnrolled.isChecked());
        if (db.updateStudent(s)) {
            toast("Student updated");
            clearForm();
            viewStudents();
        } else {
            toast("Failed to update student");
        }
    }

    private void deleteStudent() {
        if (selectedStudent == null) {
            toast("Select a student from the list first");
            return;
        }

        if (db.deleteStudent(selectedStudent.id)) {
            toast("Student deleted");
            clearForm();
            viewStudents();
        } else {
            toast("Failed to delete student");
        }
    }

    private void viewStudents() {
        students = db.getAllStudents();
        adapter.clear();
        for (DBHelper.Student s : students) {
            adapter.add(s.name + "\nID: " + s.id + "  •  "
                    + (s.enrolled ? "Enrolled" : "Not Enrolled"));
        }
        adapter.notifyDataSetChanged();
        listView.clearChoices();
        selectedStudent = null;
        etId.setEnabled(true);
    }

    private void clearForm() {
        etName.setText("");
        etId.setText("");
        etId.setEnabled(true);
        cbEnrolled.setChecked(false);
        selectedStudent = null;
        listView.clearChoices();
        adapter.notifyDataSetChanged();
        etName.requestFocus();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}