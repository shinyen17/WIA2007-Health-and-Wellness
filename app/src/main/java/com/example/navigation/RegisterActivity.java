package com.example.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Replace with your Register screen layout file name

        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        TextView tvLoginLink = findViewById(R.id.tv_login_link);

        String text = "Already have an account? Login";
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan, 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLoginLink.setText(spannableString);
        tvLoginLink.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }

    public void registerUser(View view) {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(RegisterActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection connection = null;
            String[] registrationStatus = new String[1]; // For thread safety

            try {
                connection = new ConnectionClass().CONN();
                if (connection == null) {
                    registrationStatus[0] = "Database connection failed";
                } else {
                    String query = "SELECT * FROM user WHERE Email = ?";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    stmt.setString(1, email);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        registrationStatus[0] = "The email address is already registered";
                    } else {
                        String insertQuery = "INSERT INTO mad.user (Username, Email, Password) VALUES (?, ?, ?)";
                        PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, email);
                        insertStmt.setString(3, password);
                        insertStmt.executeUpdate();

                        registrationStatus[0] = "Registration successful";

                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, registrationStatus[0], Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }
                    rs.close();
                    stmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                registrationStatus[0] = "Registration failed: " + e.getMessage();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, registrationStatus[0], Toast.LENGTH_SHORT).show());
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

