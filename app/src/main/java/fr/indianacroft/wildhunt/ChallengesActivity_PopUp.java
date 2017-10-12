//package fr.indianacroft.wildhunt;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Handler;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.DisplayMetrics;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.io.ByteArrayOutputStream;
//
//public class ChallengesActivity_PopUp extends AppCompatActivity {
//
//    Button takePhoto, chooseImg, buttonOK;
//    ImageView imageView;
//    String mUserId;
//    FirebaseDatabase ref;
//    DatabaseReference childRef;
//    int PICK_IMAGE_REQUEST = 111;
//    int REQUEST_IMAGE_CAPTURE = 1;
//    Uri filePath;
//    ProgressDialog progressDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_challenges__pop_up);
//
//        takePhoto = (Button) findViewById(R.id.takePhoto);
//        chooseImg = (Button)findViewById(R.id.chooseImg);
//        buttonOK = (Button)findViewById(R.id.buttonOK);
//        imageView = (ImageView)findViewById(R.id.imgView);
//
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//
//        getWindow().setLayout((int)(width*.8),(int)(height*.75));
//
//        takePhoto = (Button) findViewById(R.id.takePhoto);
//        takePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dispatchTakePictureIntent();
//            }
//        });
//
//        chooseImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_PICK);
//                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
//            }
//        });
//
//        // ProgressDialog
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage(getString(R.string.progressdialog_upload));
//
//        // Link to Firebase Database
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        final StorageReference storageRef = storage.getInstance().getReference();
//        ref = FirebaseDatabase.getInstance();
//        childRef = ref.getReference("Challenge");
//
//        Intent intent = getIntent();
//        final String mCreatedQuestId = intent.getStringExtra("mCreatedQuest");
//        final String challengeId = ref.getReference("Challenge").child(mCreatedQuestId).push().getKey();
//        buttonOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                 Upload photos on Firebase
//                if(filePath != null) {
//                    progressDialog.show();
//                    StorageReference childRef = storageRef.child("Quest").child(mCreatedQuestId).child(challengeId);
//                    UploadTask uploadTask = childRef.putFile(filePath);
//                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(), getString(R.string.created), Toast.LENGTH_SHORT).show();
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                public void run() {
//                                    Intent intent = new Intent(ChallengesActivity_PopUp.this, ChallengesActivity.class);
//                                    startActivity(intent);
//                                }
//                            }, 1500);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(), getString(R.string.toast_error_upload) + e, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    progressDialog.show();
//                    imageView.setDrawingCacheEnabled(true);
//                    imageView.buildDrawingCache();
//                    Bitmap bitmap = imageView.getDrawingCache();
//                    ByteArrayOutputStream baas = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baas);
//                    byte[] data = baas.toByteArray();
//                    UploadTask uploadTask = storageRef.child("Quest").child(mCreatedQuestId).child(challengeId).putBytes(data);
//                    uploadTask.addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            progressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(), getString(R.string.toast_error_upload), Toast.LENGTH_LONG).show();
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
//                            Toast.makeText(getApplicationContext(), getString(R.string.created), Toast.LENGTH_LONG).show();
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                public void run() {
//                                    Intent intent = new Intent(ChallengesActivity_PopUp.this, ChallengesActivity.class);
//                                    startActivity(intent);
//                                }
//                            }, 1500);
//                        }
//                    });
//                }
//            }
//        });
//    }
//
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap bitmap = null;
//        // PICK IMAGE REQUEST = photo de la gallery
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            filePath = data.getData();
//            try {
//                //getting image from gallery
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//
//                //Setting image to ImageView
//                imageView.setImageBitmap(bitmap);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        // REQUEST IMAGE CAPTURE = lien vers appareil photo
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            bitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(bitmap);
//        }
//    }
//}
