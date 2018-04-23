package fr.indianacroft.wildhunt.old;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import fr.indianacroft.wildhunt.R;

public class PlayerActivity_PopUp extends AppCompatActivity {
    int PICK_IMAGE_REQUEST = 111;
    int REQUEST_IMAGE_CAPTURE = 1;
    Uri filePath;
    private String mUserId;
    private String mUserQuest;
    private String mChallengeId;
    private String mCreatorId;
    private String mQuestId;
    private String mUser_indice;

    ////////////////////////////////////////////////////////////////////////////////////
//    private final String TAG = "MainActivity";
//    EditText editTextTitle, editTextContent, editTextSubText;
//
//    // The Keys
//    private String TITLE = "Title";
//    private String CONTENT = "Content";
//    private String SUBTEXT = "SubText";
    ////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_player_popup);

        final ImageView imageViewSendPhoto = findViewById(R.id.imageViewSendPhoto);

        // Pour recuperer la key d'un user (pour le lier a une quête)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUserId = sharedPreferences.getString("mUserId", mUserId);
        Log.d("key", mUserId);

        mChallengeId = getIntent().getStringExtra("mChallengeKey");
        mCreatorId = getIntent().getStringExtra("mCreatorId");
        mQuestId = getIntent().getStringExtra("mQuestId");
        mUser_indice = getIntent().getStringExtra("mUser_indice");


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .75));

        // Load & Take photo
        Button butLoad = findViewById(R.id.butLoad);
        butLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        Button butUpload = findViewById(R.id.butUpload);
        butUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        // ProgressDialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progressdialog_upload));

        // Link to Firebase Database
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Database
        Button butSend = findViewById(R.id.butSend);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        // On recupere la quete crée par l'user actuel pour link challenge a la quête
        final DatabaseReference refUser =
                FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUserQuest = user.getUser_quest();

                // Reference to an image file in Firebase Storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("User").child(mUserId).child("QuestToBeValidated").child(mUserQuest).child(mChallengeId);
                // ImageView in your Activity

                // Load the image using Glide
//                if (storageReference.getDownloadUrl().isSuccessful()){
                    /*Glide.with(getApplicationContext())
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imageViewSendPhoto);*/
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            // Modifier le champ dans le user Player pour mettre le challenge en done !
           refUser.child("challenge_done").child(mChallengeId).child("state").setValue("true");

                ////////////////////////////////////////////////////////////////////////////////////
//                // TEST DE NOTIFS
//                // Start Service
//                Intent serviceIntent = new Intent(getApplicationContext(), NotificationService.class);
//                startService(serviceIntent);
//
//                // Get the Database
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                // Get the Notification Reference
//                final DatabaseReference notificationRef = database.getReference("Notification");
//                // Keep the Database sync in case of loosing connexion
//                notificationRef.keepSynced(true);
//
//                // Send Notification on Send Click
//                // Get Edit Text
////                editTextTitle = (EditText) findViewById(R.id.editTextTitle);
////                editTextContent = (EditText) findViewById(R.id.editTextContent);
////                editTextSubText = (EditText) findViewById(R.id.editTextSubText);
//
//                // Get Text
////                String title = editTextTitle.getText().toString();
////                String content = editTextContent.getText().toString();
////                String subtext = editTextSubText.getText().toString();
//                // Store in a map
//                HashMap<String, String> notification = new HashMap<String, String>();
//                notification.put(TITLE, mUserId);
//                notification.put(CONTENT, mQuestId);
//                notification.put(SUBTEXT, mChallengeId);
//                // Send the map
//                notificationRef.setValue(notification);
                ////////////////////////////////////////////////////////////////////////////////////

            // Upload photos on Firebase
            if (filePath != null) {
                progressDialog.show();
                StorageReference childRef = storageRef.child("User").child(mUserId).child("QuestToBeValidated").child(mUserQuest).child(mChallengeId);
                UploadTask uploadTask = childRef.putFile(filePath);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_upload_success), Toast.LENGTH_LONG).show();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("User").child(mCreatorId).child("aValider").child(mQuestId).child(mChallengeId).child("User").child(mUserId).setValue(false);
                        ref.child("User").child(mCreatorId).child("aValider").child(mQuestId).child(mChallengeId).child("Indice").child(mUserId).setValue(mUser_indice);
                        ref.child("User").child(mUserId).child("user_indice").setValue("false");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(PlayerActivity_PopUp.this, PlayerActivity.class);
                                startActivity(intent);
                            }
                        }, 1500);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_error_upload) + e, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                progressDialog.show();
                imageViewSendPhoto.setDrawingCacheEnabled(true);
                imageViewSendPhoto.buildDrawingCache();
                Bitmap bitmap = imageViewSendPhoto.getDrawingCache();
                ByteArrayOutputStream baas = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baas);
                byte[] data = baas.toByteArray();
                UploadTask uploadTask = storageRef.child("User").child(mUserId).child("QuestToBeValidated").child(mUserQuest).child(mChallengeId).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_error_upload), Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_upload_success), Toast.LENGTH_LONG).show();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("User").child(mCreatorId).child("aValider").child(mQuestId).child(mChallengeId).child("User").child(mUserId).setValue(false);
                        ref.child("User").child(mCreatorId).child("aValider").child(mQuestId).child(mChallengeId).child("Indice").child(mUserId).setValue(mUser_indice);
                        ref.child("User").child(mUserId).child("user_indice").setValue("false");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(PlayerActivity_PopUp.this, PlayerActivity.class);
                                startActivity(intent);
                            }
                        }, 1500);
                    }
                });
            }
            }
        });
    }

    // Send photos to ImageView
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageViewSendPhoto = findViewById(R.id.imageViewSendPhoto);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imageViewSendPhoto.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageViewSendPhoto.setImageBitmap(imageBitmap);
        }
    }
}