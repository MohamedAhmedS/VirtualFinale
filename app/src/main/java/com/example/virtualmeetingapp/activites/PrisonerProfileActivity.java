package com.example.virtualmeetingapp.activites;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.TimeSpinnerAdapter;
import com.example.virtualmeetingapp.models.AppointTimeItem;
import com.example.virtualmeetingapp.models.AppointmentModel;
import com.example.virtualmeetingapp.models.Prisoner;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.receivers.ReminderBroadcast;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.NetworkUtils;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PrisonerProfileActivity extends BaseActivity {

    private static final String TAG = "PrisonerProfileActivity";

    TextView tvVisitorProName, tvVisitorProEmail, tvVisitorProDes;
    TextView tvVisitorProApproveByAdmin, tvVisitorProPriListApproval, tvVisitorProOffListApproval;
    ImageView tvVisitorProImage1;
    Button deleteImage;
    Button btnMsg, btnRequestConWithPri;
    Button btnNotify;

    String prisonerUID;
    String userName;
    String selectedDate = "";
    ArrayAdapter<String> dataAdapter;
    ArrayList<AppointTimeItem> mTimeList, mTimeList1;
    TimeSpinnerAdapter timeSpinnerAdapter;

    String appointDate;
    String appointTime;

    User currentUser;
    FirebaseFirestore fireStoreDB;
    FirebaseStorage mStorage;
    private StorageReference mDeleteRef;

    // Recoard Video code start

    //Permissions
    private static final int SCREEN_RECORD_REQUEST_CODE = 777;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = PERMISSION_REQ_ID_RECORD_AUDIO + 1;
    private boolean hasPermissions = false;


    // Recoard Video code end


    @Override
    public void initXML() {
        tvVisitorProName = findViewById(R.id.tvVisitorProName);
        tvVisitorProEmail = findViewById(R.id.tvVisitorProEmail);
        tvVisitorProDes = findViewById(R.id.tvVisitorProDes);
        tvVisitorProApproveByAdmin = findViewById(R.id.tvVisitorProApproveByAdmin);
        tvVisitorProPriListApproval = findViewById(R.id.tvVisitorProPriListApproval);
        tvVisitorProOffListApproval = findViewById(R.id.tvVisitorProOffListApproval);
        tvVisitorProImage1 = findViewById(R.id.tvVisitorProImage1);
        deleteImage = findViewById(R.id.deleteImage);
        btnMsg = findViewById(R.id.btnMsg);
        btnRequestConWithPri = findViewById(R.id.btnRequestConWithPri);
        btnNotify = findViewById(R.id.btnNotify);
    }

    @Override
    public void initVariables() {
        fireStoreDB = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDeleteRef = mStorage.getReference();
        currentUser = (User) Global.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prisoner_profile);

        initXML();
        initVariables();
        userData();
        appointmentTimeList();

        createNotificationChannel();

        if (NetworkUtils.getInstance().isNetworkConnected(this)) {
            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_APPOINTMENTS)
                    .whereEqualTo("visitorId", visitor.getUid())
                    .whereEqualTo("prisonerId", prisonerUID)
                    .whereGreaterThan("startTimeInMillis", System.currentTimeMillis())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<AppointmentModel> appointmentModels = task.getResult().toObjects(AppointmentModel.class);
                            if (appointmentModels.size() > 0) {
                                boolean matchFound = false;
                                for (AppointmentModel item : appointmentModels) {
                                    if (item.getAppointmentStatus().equals("pending") || item.getAppointmentStatus().equals("approved")) {
                                        btnRequestConWithPri.setText("You have already appointment with this prisoner");
                                        btnRequestConWithPri.setEnabled(false);
                                        matchFound = true;
                                        break;
                                    }
                                }
                                if (!matchFound) {
                                    btnRequestConWithPri.setVisibility(View.VISIBLE);
                                    // recording code end
                                    btnRequestConWithPri.setOnClickListener(v -> {
                                        //            scheduleAlarm("04/11/2020 06:20 PM", "Test", "Test");
                                        //            ToastHelper.showToast("Your Alarm is Set...!!!");
                                        appointmentDialog();
                                    });
                                }
                            } else {
                                btnRequestConWithPri.setVisibility(View.VISIBLE);
                                // recording code end
                                btnRequestConWithPri.setOnClickListener(v -> {
                                    //            scheduleAlarm("04/11/2020 06:20 PM", "Test", "Test");
                                    //            ToastHelper.showToast("Your Alarm is Set...!!!");
                                    appointmentDialog();
                                });
                            }
                        } else {
                            btnRequestConWithPri.setVisibility(View.GONE);
                        }
                    });
        }
//        appointmentNotification();

        btnMsg.setOnClickListener(v -> {
            if (appointmentModel == null) {
                ToastHelper.showToast("Invalid Access, contact support");
                return;
            }
//                    Map<String, Object> updateMap1 = new HashMap<>();
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.INTENT_CHAT_UID, prisonerUID);
            bundle.putString(Constants.INTENT_CHAT_PROFILE_THUMB, "");
            bundle.putSerializable("appointment", appointmentModel);
            intent.putExtras(bundle);
            startActivity(intent);
        });

//        testing new notification code with time in db start

//        fireStoreDB.collection(Constants.COLLECTION_APPOINTMENTS).whereEqualTo("visitorId", currentUser.getUid())
//                .whereEqualTo("prisonerId", prisonerUID)
//                .get()
//                .addOnCompleteListener(checkDate -> {
//                    if (!checkDate.isSuccessful()) {
//                        ToastHelper.showToast(checkDate.getException().getMessage());
//                    } else {
//                        AppointmentModel appointmentModel = checkDate.getResult().toObjects(AppointmentModel.class).get(0);
//                        String[] timeSplit = appointmentModel.getAppointmentTime().split("-");
//                        appointTime = timeSplit[0].trim();
//                        appointDate = appointmentModel.getAppointmentDate();
//                        scheduleAlarm(appointDate + " " + appointTime, "Virtual Meeting App", "Your Appointment time is started.");
////                        ToastHelper.showToastLong(appointDate + " " + appointTime);
//                                    ToastHelper.showToast("We Set Alarm for you...!!!");
//                    }
//                });

        btnNotify.setOnClickListener(v -> {
            scheduleAlarm("20/11/2020 07:33 PM", "Test", "Test");
//            scheduleAlarm(appointDate + " " + appointTime, "Virtual Meeting App", "Your Appointment time is started.");
            ToastHelper.showToast("Your Alarm is Set...!!!");
        });

//        testing new notification code with time in db end

    }

    AppointmentModel appointmentModel = null;

    public void checkTodayAppointment() {
//        fireStoreDB.collection(Constants.COLLECTION_APPOINTMENTS).whereEqualTo("visitorId", currentUser.getUid())
//                .whereEqualTo("prisonerId", prisonerUID)
//                .get()
//                .addOnCompleteListener(checkDate -> {
//                    if (!checkDate.isSuccessful()) {
//                        ToastHelper.showToast(checkDate.getException().getMessage());
//                    } else {
//
//                        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
//                        String currentDate = new SimpleDateFormat("d/MM/yyyy", Locale.getDefault()).format(new Date());
//
//                        List<AppointmentModel> appointmentModels = checkDate.getResult().toObjects(AppointmentModel.class);
//                        if (!visitor.isChatApvByOfficer()) {
//                            for (int i = 0; i < appointmentModels.size(); i++) {
//                                String[] timeSplit = appointmentModels.get(i).getAppointmentTime().split("-");
//                                String appointmentDate = appointmentModels.get(i).getAppointmentDate();
//                                if (timeSplit[0].trim().equals(currentTime.trim()) && appointmentDate.equals(currentDate)) {
//                                    btnMsg.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        }
//                    }
//                });

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
        fireStoreDB.collection(Constants.COLLECTION_APPOINTMENTS)
                .whereEqualTo("prisonerId", prisonerUID)
                .whereEqualTo("visitorId", currentUser.getUid())
                .whereEqualTo("appointmentDate", currentDate)
                .whereGreaterThanOrEqualTo("endTimeInMillis", System.currentTimeMillis())
                .whereEqualTo("appointmentStatus", "approved").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    ToastHelper.showToast(task.getException().getMessage());
                } else {
                    List<AppointmentModel> appointmentModels = task.getResult().toObjects(AppointmentModel.class);
                    if (appointmentModels != null && appointmentModels.size() > 0) {
                        btnMessageRunnable = () -> {
                            if (appointmentModels.size() > 0) {
                                long currentTimeInMillis = System.currentTimeMillis();
                                if (currentTimeInMillis >= appointmentModels.get(0).getStartTimeInMillis() &&
                                        currentTimeInMillis <= appointmentModels.get(0).getEndTimeInMillis()) {
                                    appointmentModel = appointmentModels.get(0);
                                    btnMsg.setVisibility(View.VISIBLE);
                                } else {
                                    btnMsg.setVisibility(View.GONE);
                                }
                                btnMessageHandler.postDelayed(btnMessageRunnable, 1000);
                            }
                        };
                        btnMessageHandler.post(btnMessageRunnable);
                    }
                }
            }
        });
    }

    Handler btnMessageHandler = new Handler();
    Runnable btnMessageRunnable;

    @Override
    protected void onStart() {
        super.onStart();
        checkTodayAppointment();
    }

    @Override
    protected void onStop() {
        super.onStop();
        btnMessageHandler.removeCallbacksAndMessages(null);
    }

    public void userData() {
        if (getIntent() != null) {
            prisonerUID = getIntent().getStringExtra("prisonerUID");
            fireStoreDB.collection(Constants.COLLECTION_USER)
                    .whereEqualTo("uid", prisonerUID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful() || task.getResult() == null) {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        } else {
                            Prisoner prisoner = task.getResult().toObjects(Prisoner.class).get(0);
                            userName = prisoner.getUserName();
                            tvVisitorProName.setText(userName);

                            String userEmail = prisoner.getUserEmail();
                            tvVisitorProEmail.setText(userEmail);

                            String userDes = prisoner.getDescriptionPrisoners();
                            tvVisitorProDes.setText(userDes);
                        }
                    });
        }
    }

    public void appointmentTimeList() {
        mTimeList = new ArrayList<>();
        mTimeList.add(new AppointTimeItem("10:00 AM - 10:10 AM"));
        mTimeList.add(new AppointTimeItem("10:10 AM - 10:20 AM"));
        mTimeList.add(new AppointTimeItem("10:20 AM - 10:30 AM"));
        mTimeList.add(new AppointTimeItem("10:30 AM - 10:40 AM"));
        mTimeList.add(new AppointTimeItem("10:40 AM - 10:50 AM"));
        mTimeList.add(new AppointTimeItem("10:50 AM - 11:00 AM"));
        mTimeList.add(new AppointTimeItem("11:00 AM - 11:10 AM"));
        mTimeList.add(new AppointTimeItem("11:10 AM - 11:20 AM"));
        mTimeList.add(new AppointTimeItem("11:20 AM - 11:30 AM"));
        mTimeList.add(new AppointTimeItem("11:30 AM - 11:40 AM"));
        mTimeList.add(new AppointTimeItem("11:40 AM - 11:50 AM"));
        mTimeList.add(new AppointTimeItem("11:50 AM - 12:00 PM"));
        mTimeList.add(new AppointTimeItem("12:00 PM - 12:10 PM"));
        mTimeList.add(new AppointTimeItem("12:10 PM - 12:20 PM"));
        mTimeList.add(new AppointTimeItem("12:20 PM - 12:30 PM"));
        mTimeList.add(new AppointTimeItem("12:30 PM - 12:40 PM"));
        mTimeList.add(new AppointTimeItem("12:40 PM - 12:50 PM"));
        mTimeList.add(new AppointTimeItem("12:50 PM - 01:00 PM"));
        mTimeList.add(new AppointTimeItem("01:00 PM - 01:10 PM"));
        mTimeList.add(new AppointTimeItem("01:10 PM - 01:20 PM"));
        mTimeList.add(new AppointTimeItem("01:20 PM - 01:30 PM"));
        mTimeList.add(new AppointTimeItem("01:30 PM - 01:40 PM"));
        mTimeList.add(new AppointTimeItem("01:40 PM - 01:50 PM"));
        mTimeList.add(new AppointTimeItem("01:50 PM - 02:00 PM"));

        mTimeList1 = new ArrayList<>();
        mTimeList1.add(new AppointTimeItem("02:00 PM - 02:10 PM"));
        mTimeList1.add(new AppointTimeItem("02:10 PM - 02:20 PM"));
        mTimeList1.add(new AppointTimeItem("02:20 PM - 02:30 PM"));
        mTimeList1.add(new AppointTimeItem("02:30 PM - 02:40 PM"));
        mTimeList1.add(new AppointTimeItem("02:40 PM - 02:50 PM"));
        mTimeList1.add(new AppointTimeItem("02:50 PM - 03:00 PM"));
        mTimeList1.add(new AppointTimeItem("03:00 PM - 03:10 PM"));
        mTimeList1.add(new AppointTimeItem("03:10 PM - 03:20 PM"));
        mTimeList1.add(new AppointTimeItem("03:20 PM - 03:30 PM"));
        mTimeList1.add(new AppointTimeItem("03:30 PM - 03:40 PM"));
        mTimeList1.add(new AppointTimeItem("03:40 PM - 03:50 PM"));
        mTimeList1.add(new AppointTimeItem("03:50 PM - 04:00 PM"));
        mTimeList1.add(new AppointTimeItem("04:00 PM - 04:10 PM"));
        mTimeList1.add(new AppointTimeItem("04:10 PM - 04:20 PM"));
        mTimeList1.add(new AppointTimeItem("04:20 PM - 04:30 PM"));
        mTimeList1.add(new AppointTimeItem("04:30 PM - 04:40 PM"));
        mTimeList1.add(new AppointTimeItem("04:40 PM - 04:50 PM"));
        mTimeList1.add(new AppointTimeItem("04:50 PM - 05:00 PM"));
        mTimeList1.add(new AppointTimeItem("05:00 PM - 05:10 PM"));
        mTimeList1.add(new AppointTimeItem("05:10 PM - 05:20 PM"));
        mTimeList1.add(new AppointTimeItem("05:20 PM - 05:30 PM"));
        mTimeList1.add(new AppointTimeItem("05:30 PM - 05:40 PM"));
        mTimeList1.add(new AppointTimeItem("05:40 PM - 05:50 PM"));
        mTimeList1.add(new AppointTimeItem("05:50 PM - 06:00 PM"));
    }

    public void createAppointmentTimeList(Spinner spinner, RadioGroup radioGroup) {
        if (selectedDate.equals("")) {
            ToastHelper.showToast("Please Select Date!");
            return;
        }
        fireStoreDB.collection(Constants.COLLECTION_APPOINTMENTS).whereEqualTo("prisonerId", prisonerUID)
                .whereEqualTo("appointmentDate", selectedDate)
                .get()
                .addOnCompleteListener(checkDate -> {
                    if (!checkDate.isSuccessful()) {
                        ToastHelper.showToast(checkDate.getException().getMessage());
                    } else {
                        List<AppointmentModel> appointmentModels = checkDate.getResult().toObjects(AppointmentModel.class);
                        List<AppointTimeItem> mTempList = mTimeList;
                        if (radioGroup.getCheckedRadioButtonId() == R.id.afterNoon) {
                            mTempList = mTimeList1;
                        }
                        for (AppointmentModel appointmentModel : appointmentModels) {
                            for (int i = 0; i < mTempList.size(); i++) {
                                if (mTempList.get(i).getTvTime().equals(appointmentModel.getAppointmentTime())) {
                                    mTempList.get(i).setAvailable(false);
                                }
                            }
                        }

                        TimeSpinnerAdapter timeSpinnerAdapter =
                                new TimeSpinnerAdapter(PrisonerProfileActivity.this, mTempList);
                        spinner.setAdapter(timeSpinnerAdapter);
                    }
                });

    }

    public void appointmentDialog() {
        final Dialog dialog = new Dialog(PrisonerProfileActivity.this);
        dialog.setContentView(R.layout.appointment_date_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null)
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        EditText pickDate = dialog.findViewById(R.id.appointmentDatePick);
        Button btnAppoint = dialog.findViewById(R.id.btnAppoint);
        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v1 -> dialog.dismiss());

        Spinner spinner = dialog.findViewById(R.id.timeSpinner);

        final RadioGroup radio = dialog.findViewById(R.id.radioGroup);
        radio.setOnCheckedChangeListener((group, checkedId) -> {
            createAppointmentTimeList(spinner, radio);
            spinner.setVisibility(View.VISIBLE);
        });

        //Spinner adapter end
        pickDate.setOnFocusChangeListener((v1, v2) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        monthOfYear += 1;
                        selectedDate = dayOfMonth + "/" + monthOfYear + "/" + year;
                        fireStoreDB.collection(Constants.COLLECTION_APPOINTMENTS).whereEqualTo("visitorId", currentUser.getUid())
                                .whereEqualTo("appointmentDate", selectedDate).get().addOnCompleteListener(checkSelectedDate -> {
                            if (!checkSelectedDate.isSuccessful()) {
                                ToastHelper.showToast(checkSelectedDate.getException().getMessage());
                            } else {
                                List<AppointmentModel> checkCurrentAppointment = checkSelectedDate.getResult().toObjects(AppointmentModel.class);
                                if (checkCurrentAppointment.size() > 0) {
//                                   dialog.dismiss();
                                    ToastHelper.showToastLong("You already have appointment on current date. You can't select this date again");
                                } else {
                                    pickDate.setText(selectedDate);
                                    spinner.setVisibility(View.VISIBLE);
                                    createAppointmentTimeList(spinner, radio);
                                }
                            }
                        });
//                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
//                            selectedDate = sdf.format(view.getMi);
                    }, -1, -1, -1);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.DAY_OF_YEAR, 1);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            c.add(Calendar.DAY_OF_YEAR, 30);
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            datePickerDialog.show();
        });

        //new code for uploading data start

        long appointmentId = System.currentTimeMillis();
        String Uid = prisonerUID;
        String prisonerName = userName;
        String Uid1 = "";
        String officerName = "";
        String visitorUid = currentUser.getUid();
        String visitorsName = currentUser.getUserName();
        String appointmentStatus = "pending";

        btnAppoint.setOnClickListener(v12 -> {
            String date = pickDate.getText().toString();
            String time = ((AppointTimeItem) spinner.getSelectedItem()).getTvTime();
            boolean isChatApvByOfficer = false;

            AppointmentModel appointmentModel = new AppointmentModel(appointmentId, Uid, prisonerName, Uid1, officerName,
                    visitorUid, visitorsName, date, time, appointmentStatus, isChatApvByOfficer);

            String[] timeSplit = time.split(" - ");
            String startTime = timeSplit[0];
            String endTime = timeSplit[1];

            try {
                long startTimeInMillis = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US).parse(date + " " + startTime).getTime();
                long endTimeInMillis = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US).parse(date + " " + endTime).getTime();

                appointmentModel.setStartTimeInMillis(startTimeInMillis);
                appointmentModel.setEndTimeInMillis(endTimeInMillis);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            fireStoreDB.collection(Constants.COLLECTION_APPOINTMENTS)
                    .document(String.valueOf(appointmentId))
                    .set(appointmentModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            ToastHelper.showToast("Appointment Request Sent Successfully");
                        } else {
                            ToastHelper.showToast(task.getException().getLocalizedMessage());
                        }
                    });
            //new code for uploading data end
        });
        dialog.show();
    }

//    public void appointmentNotification() {
//        btnNotify.setOnClickListener(v -> {
//            Toast.makeText(PrisonerProfileActivity.this, "Reminder Set", Toast.LENGTH_SHORT).show();
//
//            Intent intent = new Intent(PrisonerProfileActivity.this, ReminderBroadcast.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(PrisonerProfileActivity.this, 0, intent, 0);
//
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//            long timeButtonClick = System.currentTimeMillis();
//            long timeSecInMillis = 1000 * 10;
//
////            String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
////                Toast.makeText(MainActivity.this,currentTime, Toast.LENGTH_SHORT).show();
////                timeButtonClick + timeSecInMillis
////            if (currentTime.equals("04:33 PM")) {
//            alarmManager.set(AlarmManager.RTC_WAKEUP, timeButtonClick + timeSecInMillis, pendingIntent);
////            } else {
////                Toast.makeText(PrisonerProfileActivity.this,"Reminder Set False", Toast.LENGTH_SHORT).show();
////            }
//
//        });
//    }

    public void createNotificationChannel() {
        CharSequence name = "MyTestChannel";
        String des = "This is testing app of alarm manager";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("notifyLemubit", name, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription(des);
        }

        NotificationManager notificationManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleAlarm(String scheduleTime, String title, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
        try {
            long mScheduleDate = sdf.parse(scheduleTime).getTime();
//            long mScheduleDate = System.currentTimeMillis();
            long mScheduleDate1 = mScheduleDate + 20000;
            long mScheduleDate2 = mScheduleDate + (20000 * 6);

            int requestCode = new Random().nextInt(900) + 1;
            ReminderBroadcast.setAlarm(this, mScheduleDate1, title, message, requestCode, mScheduleDate2);
//            ReminderBroadcast.setAlarm(this, mScheduleDate2, title, message, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
