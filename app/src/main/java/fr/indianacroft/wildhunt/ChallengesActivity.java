package fr.indianacroft.wildhunt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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

public class ChallengesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText name_challenge;
    EditText hint_challenge;
    String name_challenge_content;
    String hint_challenge_content;
    Spinner spinner_challenge;
    ImageView imageViewPhotoChallenge;
    Button butLoad;
    Button butUpload;
    Button butCreateChallenge;
    FirebaseDatabase ref;
    DatabaseReference childRef;
    private String mUserId;
    private String mUserQuest;
    int PICK_IMAGE_REQUEST = 111;
    int REQUEST_IMAGE_CAPTURE = 1;
    Uri filePath;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        imageViewPhotoChallenge = (ImageView) findViewById(R.id.imageViewInscriptionLogo);

        // Pour recuperer la key d'un user (pour le lier a une quête)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUserId = sharedPreferences.getString("mUserId", mUserId);
        Log.d("key", mUserId);
        /////////////////////////////////////////////////////////////////

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Drawer Menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Avatar
        // POUR CHANGER L'AVATAR SUR LA PAGE AVEC CELUI CHOISI
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Avatar").child(mUserId);
        final ImageView imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        // Load the image using Glide
        if (storageReference.getDownloadUrl().isSuccessful()){
            Glide.with(getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageViewAvatar);
        }

        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChallengesActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Spinner
        spinner_challenge = (Spinner) findViewById(R.id.spinner_challenge);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.challenge_difficulty, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_challenge.setAdapter(adapter);

        // Button Actions / Change ImageView / Text in buttons
        // TODO : SUPPRIMER LES BOUTONS ET REMPLACER PAR CLICK SUR PHOTO
        name_challenge = (EditText) findViewById(R.id.challenge_name);
        hint_challenge = (EditText) findViewById(R.id.hint_challenge);
        butCreateChallenge = (Button) findViewById(R.id.butCreateChallenge);
        /*imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
        imageViewCancel2 = (ImageView) findViewById(R.id.imageViewCancel2);*/
        butCreateChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_challenge_content = name_challenge.getText().toString();
                hint_challenge_content = hint_challenge.getText().toString();
                if ((!name_challenge_content.equals("")) && (!hint_challenge_content.equals(""))) {
                    /*imageViewCancel.setImageResource(R.drawable.checked);
                    imageViewCancel2.setImageResource(R.drawable.checked);
                    butNewChallenge.setText("Challenge 1 created");
                    butNewChallenge2.setText("Challenge 2 created");*/
                    Intent intent = new Intent(ChallengesActivity.this, HomeGameMasterActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ChallengesActivity.this, R.string.toast_challenge, Toast.LENGTH_SHORT).show();
                }
            }
        });

//        // Upload & Take photo
//        butLoad = (Button) findViewById(R.id.butLoad);
//        butLoad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dispatchTakePictureIntent();
//            }
//        });
//        butUpload = (Button) findViewById(R.id.butUpload);
//        butUpload.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View arg0) {
//                Intent intent = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 0);
//            }
//        });

        // Load & Take photo
        butLoad = (Button) findViewById(R.id.butLoad);
        butLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        butUpload = (Button) findViewById(R.id.butUpload);
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progressdialog_upload));

        // Link to Firebase Database
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getInstance().getReference();


        // Database
        name_challenge = (EditText) findViewById(R.id.challenge_name);
        hint_challenge = (EditText) findViewById(R.id.hint_challenge);
        butCreateChallenge = (Button) findViewById(R.id.butCreateChallenge);

        ref = FirebaseDatabase.getInstance();
        childRef = ref.getReference("Challenge");

        butCreateChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameContent = name_challenge.getText().toString();
                String hintContent = hint_challenge.getText().toString();
                String spinnerContent = spinner_challenge.getSelectedItem().toString();
                String idquest = "test";//TODO a modifier

                // childRef.push().getKey() is used to generate the different key
                final String challengeId = ref.getReference("Challenge").push().getKey();

                // On recupere la quete crée par l'user actuel pour link challenge a la quête
                DatabaseReference refUser =
                        FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);
                refUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        mUserQuest = user.getUser_createdquestID();
                        String userName = user.getUser_name();

                        childRef.child(challengeId).child("challenge_questId").setValue(mUserQuest);
                        childRef.child(challengeId).child("challenge_creatorID").setValue(mUserId);
                        childRef.child(challengeId).child("challenge_creatorname").setValue(userName);

                        // Upload photos on Firebase
                        if(filePath != null) {
                            progressDialog.show();
                            StorageReference childRef = storageRef.child("Quest").child(mUserQuest).child(challengeId);
                            UploadTask uploadTask = childRef.putFile(filePath);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), getString(R.string.created), Toast.LENGTH_SHORT).show();
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
                            imageViewPhotoChallenge.setDrawingCacheEnabled(true);
                            imageViewPhotoChallenge.buildDrawingCache();
                            Bitmap bitmap = imageViewPhotoChallenge.getDrawingCache();
                            ByteArrayOutputStream baas = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baas);
                            byte[] data = baas.toByteArray();
                            UploadTask uploadTask = storageRef.child("Quest").child(mUserQuest).child(challengeId).putBytes(data);
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
                                    Toast.makeText(getApplicationContext(), getString(R.string.created), Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                // Creation du nouveau challenge
                Challenge challenge = new Challenge(nameContent, hintContent, spinnerContent, idquest, mUserId);

                challenge.setChallenge_name(nameContent);
                challenge.setHint_challenge(hintContent);
                challenge.setChallenge_difficulty(spinnerContent);

                childRef.child(challengeId).setValue(challenge);



                name_challenge.setText("");
                hint_challenge.setText("");

                startActivity(new Intent(getApplicationContext(), ChallengesActivity.class));

            }
        });
    }

    // Drawer Menu
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // TODO : remplacer les toasts par des liens ET faire en sorte qu'on arrive sur les pages de fragments
        if (id == R.id.nav_rules) {
            Intent intent = new Intent(getApplicationContext(), RulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_play) {
            Intent intent = new Intent(getApplicationContext(), HomeJoueurActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_create) {
            startActivity(new Intent(getApplicationContext(), HomeGameMasterActivity.class));
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(getApplicationContext(), HomeGameMasterActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_delete) {
            startActivity(new Intent(getApplicationContext(), ConnexionActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        imageViewPhotoChallenge = (ImageView) findViewById(R.id.imageViewInscriptionLogo);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imageViewPhotoChallenge.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageViewPhotoChallenge.setImageBitmap(imageBitmap);
        }
    }
}
