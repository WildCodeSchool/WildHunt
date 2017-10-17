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

    EditText name_challenge, hint_challenge;
    ImageView imageViewInscriptionLogo, imageViewAvatar;
    Spinner spinner_challenge;
    Button butLoad, butUpload, butCreateChallenge;
    FirebaseDatabase ref;
    DatabaseReference childRef;
    int PICK_IMAGE_REQUEST = 111, REQUEST_IMAGE_CAPTURE = 1;
    Uri filePath;
    ProgressDialog progressDialog;
    private String mUserId, mCreatedQuestId, mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        // Pour recuperer la key d'un user (pour le lier a une quête)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUserId = sharedPreferences.getString("mUserId", mUserId);
        mCreatedQuestId = sharedPreferences.getString("mCreatedQuest", "");
        Log.d("key", mUserId);


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
        navigationView.setItemIconTintList(null);

        // Avatar
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Avatar").child(mUserId);
//        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
//        // Load the image using Glide
//        if (storageReference.getDownloadUrl().isSuccessful()){
//            Glide.with(getApplicationContext())
//                    .using(new FirebaseImageLoader())
//                    .load(storageReference)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(imageViewAvatar);
//        }
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
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

        // Lien PopUp
//        imageViewInscriptionLogo = (ImageView) findViewById(R.id.imageViewInscriptionLogo);
//        imageViewInscriptionLogo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ChallengesActivity.this, ChallengesActivity_PopUp.class);
//                intent.putExtra("mCreatedQuest", mCreatedQuestId);
//                startActivity(intent);
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

//TODO RECYCLER VIEW
//        // Pour remplir la liste des challenges avec les challenges créees!!!
//        final RecyclerView recyclerViewGamesCreated = (RecyclerView) findViewById(R.id.recyclerViewChallengeCreated);
//        recyclerViewGamesCreated.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//
//        DatabaseReference refChallenge = FirebaseDatabase.getInstance().getReference("Challenge").child(mCreatedQuestId);
//
//
//        final FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Challenge, ChallengeViewHolder>(
//                Challenge.class,
//                R.layout.challenge_recyclerview,
//                ChallengeViewHolder.class,
//                refChallenge) {
//            @Override
//            public void populateViewHolder(ChallengeViewHolder holder, Challenge challenge, int position) {
//                holder.setChallenge_name(challenge.getChallenge_name());
//                holder.setChallenge_difficulty(challenge.getChallenge_difficulty());
//            }
//        };
//
//        // Set the adapter avec les données et la ligne de separation
//        recyclerViewGamesCreated.setAdapter(mAdapter);

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

        DatabaseReference refUser =
                FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mCreatedQuestId = user.getUser_createdquestID();
                mUserName = user.getUser_name();



                //StorageReference strRef = FirebaseStorage.getInstance().getReference("Quest").child(mCreatedQuestId).child(challengeId);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //ON CLICK
        butCreateChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameContent = name_challenge.getText().toString().trim();
                String hintContent = hint_challenge.getText().toString().trim();
                String spinnerContent = spinner_challenge.getSelectedItem().toString();
                String idquest = "test";//TODO a modifier


                // Impossible to create if nothing is written
                if ((nameContent.equals("")) || (hintContent.equals(""))) {
                    Toast.makeText(getApplicationContext(), R.string.toast_challenge, Toast.LENGTH_LONG).show();
                } else {
                    // childRef.push().getKey() is used to generate the different key
                    final String challengeId = ref.getReference("Challenge").child(mCreatedQuestId).push().getKey();


                    final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                    // Upload photos on Firebase
                    if (filePath != null) {
                        progressDialog.show();

                        StorageReference childRef = storageRef.child("Quest").child(mCreatedQuestId).child(challengeId);
                        UploadTask uploadTask = childRef.putFile(filePath);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
//                                    Toast.makeText(getApplicationContext(), getString(R.string.created), Toast.LENGTH_SHORT).show();
//                                    Handler handler = new Handler();
//                                    handler.postDelayed(new Runnable() {
//                                        public void run() {
//                                            Intent intent = new Intent(ChallengesActivity.this, CreateQuestActivity.class);
//                                            startActivity(intent);
//                                        }
//                                    }, 1500);
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
                        imageViewInscriptionLogo.setDrawingCacheEnabled(true);
                        imageViewInscriptionLogo.buildDrawingCache();
                        Bitmap bitmap = imageViewInscriptionLogo.getDrawingCache();
                        ByteArrayOutputStream baas = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baas);
                        byte[] data = baas.toByteArray();
                        UploadTask uploadTask = storageRef.child("Quest").child(mCreatedQuestId).child(challengeId).putBytes(data);
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
//                                    Handler handler = new Handler();
//                                    handler.postDelayed(new Runnable() {
//                                        public void run() {
//                                            Intent intent = new Intent(ChallengesActivity.this, CreateQuestActivity.class);
//                                            startActivity(intent);
//                                        }
//                                    }, 1500);
                            }
                        });
                    }
                    // Load the image using Glide
//                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//                    StorageReference strRef = firebaseStorage.getReference("Quest");
//                    StorageReference pathReference = strRef.child(mCreatedQuestId).child(challengeId);
//                    Glide.with(getApplicationContext())
//                            .using(new FirebaseImageLoader())
//                            .load(pathReference)
//                            .error(R.drawable.camera)
//                            .skipMemoryCache(true)
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)
//                            .into(imageViewInscriptionLogo);


                    // Creation du nouveau challenge

                    Challenge challenge = new Challenge(nameContent, hintContent, spinnerContent, idquest, mUserId);
                    challenge.setChallenge_name(nameContent);
                    challenge.setHint_challenge(hintContent);
                    challenge.setChallenge_difficulty(spinnerContent);
                    childRef.child(mCreatedQuestId).child(challengeId).setValue(challenge);
                    childRef.child(mCreatedQuestId).child(challengeId).child("challenge_questId").setValue(mCreatedQuestId);
                    childRef.child(mCreatedQuestId).child(challengeId).child("challenge_creatorID").setValue(mUserId);
                    childRef.child(mCreatedQuestId).child(challengeId).child("challenge_creatorname").setValue(mUserName);
                    //TODO A MODIFIER PAR VALENTIN
                    //childRef.child(challengeId).child("challenge_nbrePoints").setValue(LA VALEUR INT DU SPINNER);
                    name_challenge.setText("");
                    hint_challenge.setText("");
                    startActivity(new Intent(getApplicationContext(), ChallengesActivity.class));


                }
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
            Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_lobby) {
            Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_create) {
            startActivity(new Intent(getApplicationContext(), CreateQuestActivity.class));
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(getApplicationContext(), ValidateQuestActivity.class);
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
        imageViewInscriptionLogo = (ImageView) findViewById(R.id.imageViewInscriptionLogo);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imageViewInscriptionLogo.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageViewInscriptionLogo.setImageBitmap(imageBitmap);
        }
    }

}
