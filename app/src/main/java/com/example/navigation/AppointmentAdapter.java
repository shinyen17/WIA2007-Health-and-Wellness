package com.example.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    ConnectionClass connectionClass;
    Connection con;

    private final List<Appointment> appointmentList;
    private final Context context;  // To access context for UI updates
    private final boolean isPastFragment;
    private Fragment fragment;

    //Constructor
    public AppointmentAdapter(Context context, List<Appointment> appointmentList, boolean isPastFragment, Fragment fragment) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.connectionClass = new ConnectionClass();
        this.isPastFragment = isPastFragment;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        Log.d("RecyclerView Binding", "Binding Appointment: ID=" + appointment.getId() +
                ", Position=" + position);
        holder.tvSpecialization.setText(appointment.getSpecialization());
        holder.tvDoctorName.setText("Dr. "+appointment.getDoctorName());
        holder.tvAppointmentDate.setText(getDayOfWeek(appointment.getDate()) + "   "+ appointment.getDate());
        holder.tvAppointmentTime.setText(appointment.getTime());

        // Set the image resource based on Doctor_ID
        int doctorImageRes = getDoctorImageResource(appointment.getdoctorId());
        holder.imageViewDoctor.setImageResource(doctorImageRes);

        if(isPastFragment){
            holder.deleteAppointment.setVisibility(View.GONE); // If it's in the PastFragment, hide the delete button
        }else{
            // If it's in the UpcomingFragment, show the delete button
            holder.deleteAppointment.setVisibility(View.VISIBLE);

            holder.deleteAppointment.setOnClickListener(v-> {
                Log.d("Delete Appointment", "Delete button clicked for appointment: " + appointment.getId());
                //Toast.makeText(v.getContext(), "Delete button pressed for appointment: " + appointment.getId(), Toast.LENGTH_SHORT).show();
                showDialog(v,"Are you sure to delete appointment? ", appointment.getId());
            });
        }

    }

    @Override
    public int getItemCount() {
        Log.d("RecyclerView Adapter", "Item Count: " + appointmentList.size());
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSpecialization, tvDoctorName, tvAppointmentDate, tvAppointmentTime;
        ImageView imageViewDoctor;
        ImageButton deleteAppointment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSpecialization = itemView.findViewById(R.id.tvSpecialization);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvAppointmentDate = itemView.findViewById(R.id.tvAppointmentDate);
            tvAppointmentTime = itemView.findViewById(R.id.tvAppointmentTime);
            imageViewDoctor = itemView.findViewById(R.id.imageViewDoctor);
            deleteAppointment = itemView.findViewById(R.id.deleteAppointment);
        }
    }

    public String getDayOfWeek(String date) {
        try {
            // Define the input date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = dateFormat.parse(date);

            // Define the output day-of-week format
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(parsedDate); // Returns "Monday", "Tuesday", etc.
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Fallback in case of an error
        }
    }
    private int getDoctorImageResource(int doctorId) {
        switch (doctorId) {
            case 1:
                return R.drawable.dr1; // Replace with your actual drawable names
            case 2:
                return R.drawable.dr2;
            case 3:
                return R.drawable.dr3;
            case 4:
                return R.drawable.dr4;
            case 5:
                return R.drawable.dr5;
            case 6:
                return R.drawable.dr6;
            default:
                return R.drawable.dr1; // Fallback image if no match
        }
    }

    private void showDialog(View view, String message, int appointmentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        // Set the title of the dialog
        builder.setTitle("Appointment Cancellation");

        builder.setMessage(message)
                .setCancelable(false)

                // Set the "Confirm" button (Positive action)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        deleteAppointment(view, appointmentId);
                        Toast.makeText(view.getContext(), "Appointment deleted successfully", Toast.LENGTH_SHORT).show();

                        // Replace the current fragment with AppointmentFragment
                        AppointmentFragment appointmentFragment = new AppointmentFragment();
                        // Use the fragment's Activity to get the FragmentManager
                        FragmentTransaction fragmentTransaction = fragment.getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, appointmentFragment); // Replace with your actual container ID
                        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack for navigation
                        fragmentTransaction.commit();

                        dialog.dismiss();
                    }
                })

                // Set the "Cancel" button (Negative action)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss(); // Dismiss the dialog
                    }
                });


        // Create the dialog and show it
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void deleteAppointment(View view, int appointmentId){
        Log.d("Delete Appointment", "deleteAppointment method called with appointmentId: " + appointmentId);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = connectionClass.CONN();
                if (con == null) {
                    Log.e("Delete Appointment", "Database connection is null. Could not establish a connection.");
                } else {
                    Log.d("Delete Appointment", "Database connection established successfully.");
                }
                if (con != null) {
                    String query = "DELETE FROM Appointment WHERE appointment_ID = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setInt(1, appointmentId);

                    // Log the query and the parameter
                    Log.d("Appointment Deletion", "Executing query: " + query + " with appointment_ID: " + appointmentId);

                    //execute the query
                    int rowAffected  = stmt.executeUpdate();

                    // Log the number of rows affected
                    Log.d("Rows Affected", "Rows affected: " + rowAffected);

                    // Run on the UI thread to show appropriate Toast messages
                    ((Activity) context).runOnUiThread(() -> {
                        if (rowAffected > 0) {
                            // Appointment deleted successfully
                            Toast.makeText(view.getContext(), "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // No appointment found with the given ID
                            Toast.makeText(view.getContext(), "No appointment found with ID: " + appointmentId, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    ((Activity) context).runOnUiThread(() -> Toast.makeText(view.getContext(), "Database connection failed.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((Activity) context).runOnUiThread(() -> Toast.makeText(view.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }finally {
                try {
                    if (con != null) {
                        con.close(); // Close the connection
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
