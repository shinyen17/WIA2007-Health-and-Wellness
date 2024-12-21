package com.example.combine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity2 extends AppCompatActivity {

    ConnectionClass connectionClass;
    Connection con;
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        connectionClass = new ConnectionClass();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView seeall = findViewById(R.id.seeAll);
        seeall.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, Category.class);
            startActivity(intent);
        });

        ImageButton appoitmentSchedule = findViewById(R.id.appoitmentSchedule);
        appoitmentSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, Schedule.class);
            startActivity(intent);
        });

        //from top doctor to appointment
        ConstraintLayout DR1 = findViewById(R.id.DR1);
        DR1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, chooseSlot.class);
            intent.putExtra("doctor_name", "Dr. Sarah");
            intent.putExtra("specialization", "Clinical Psychologist");
            intent.putExtra("doctor_image", R.drawable.dr1);
            startActivity(intent);
        });

        ConstraintLayout DR3 = findViewById(R.id.DR3);
        DR3.setOnClickListener(v -> {
            // Start Activity3 and pass the doctor details
            Intent intent = new Intent(MainActivity2.this, chooseSlot.class);
            intent.putExtra("doctor_name", "Dr. Adam");
            intent.putExtra("specialization", "Dietition");
            intent.putExtra("doctor_image", R.drawable.dr3);
            startActivity(intent);
        });

        ConstraintLayout DR5 = findViewById(R.id.DR5);
        DR5.setOnClickListener( v-> {
            Intent intent = new Intent(MainActivity2.this, chooseSlot.class);
            intent.putExtra("doctor_name", "Dr. John");
            intent.putExtra("specialization", "General Practitioner (GP)");
            intent.putExtra("doctor_image", R.drawable.dr5);
            startActivity(intent);
        });

        LinearLayout mentalHealth = findViewById(R.id.mentalHealth);

        mentalHealth.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity2.this, Category.class);
            intent.putExtra("fragment_to_load", "MentalFragment");
            startActivity(intent);
        });

        LinearLayout diet = findViewById(R.id.diet);
        diet.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity2.this, Category.class);
            intent.putExtra("fragment_to_load", "DietFragment");
            startActivity(intent);
        });

        LinearLayout general = findViewById(R.id.general);
        general.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity2.this, Category.class);
            intent.putExtra("fragment_to_load", "GeneralFragment");
            startActivity(intent);
        });

        //Search doctor
        EditText dcotorInsert = findViewById(R.id.dcotorInsert);
        ImageButton BtnFind = findViewById(R.id.BtnFind);

        BtnFind.setOnClickListener(v -> {
            String doctorName = dcotorInsert.getText().toString().trim();
            if (doctorName.isEmpty()) {
                Toast.makeText(this, "Please enter a doctor's name.", Toast.LENGTH_SHORT).show();
            } else {
                searchDoctor(doctorName);
            }
        });

    }

    private void searchDoctor(String doctorName) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = connectionClass.CONN(); // Replace with your database connection logic
                if (con != null) {
                    String query = "SELECT * FROM Doctor WHERE Doctor_Name = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, doctorName);
                    ResultSet rs = stmt.executeQuery();

                    boolean found = rs.next(); // Check if there's at least one matching row
                    runOnUiThread(() -> {
                        if (found) {
                            showDialog("Doctor " + doctorName + " is available!");
                        } else {
                            showDialog("Doctor " + doctorName + " not found.");
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Database connection failed.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Method to show a pop-up dialog
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss(); // Dismiss the dialog on clicking OK
                    }
                });

        // Create the dialog and show it
        AlertDialog alert = builder.create();
        alert.show();
    }
}