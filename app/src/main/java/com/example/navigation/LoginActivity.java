package com.example.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
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

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Replace with your Login screen layout file name

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        TextView tvRegisterLink = findViewById(R.id.tv_login_link);

        String text = "Don't have an account? Register";
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // Remove underline
            }
        };

        spannableString.setSpan(clickableSpan, 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegisterLink.setText(spannableString);
        tvRegisterLink.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }

    public void loginUser(View view) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection connection = null;
            String[] loginStatus = new String[1]; // For thread safety
            final int[] userId = {-1};

            try {
                connection = new ConnectionClass().CONN();
                if (connection == null) {
                    loginStatus[0] = "Database connection failed";
                } else {
                    String query = "SELECT * FROM user WHERE Email = ? AND Password = ?";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    stmt.setString(1, email);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        loginStatus[0] = "Login successful";
                        userId[0] = rs.getInt("User_ID"); // Get the User_ID of the logged-in user

                        runOnUiThread(() -> {
                            //Toast.makeText(LoginActivity.this, loginStatus[0], Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            // Pass the userId as an extra in the intent
                            intent.putExtra("userId", userId[0]); // Pass the userId as an extra (using array to allow mutation)
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        loginStatus[0] = "Invalid email or password";
                    }
                    rs.close();
                    stmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                loginStatus[0] = "Login failed: " + e.getMessage();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(() -> Toast.makeText(LoginActivity.this, loginStatus[0], Toast.LENGTH_SHORT).show());
        });
    }
}
