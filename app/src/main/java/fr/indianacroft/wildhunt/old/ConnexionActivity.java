package fr.indianacroft.wildhunt.old;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import fr.indianacroft.wildhunt.R;

public class ConnexionActivity extends AppCompatActivity {

    final String userName = "NameKey";
    final String userPassword = "PasswordKey";
    // Sound
    // MediaPlayer mMediaPlayer;
    private boolean auth = false;
    private String mUserId = "UserKey";
    private String mEncrypt = "encrypt";
    int score;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_connexion);

        // Notiications en commentaires suite manque de temps
//        // Start Service
//        Intent serviceIntent = new Intent(getApplicationContext(), NotificationService.class);
//        startService(serviceIntent);
//
//        // Get the Database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        // Get the Notification Reference
//        final DatabaseReference notificationRef = database.getReference("Notification");
//        // Keep the Database sync in case of loosing connexion
//        notificationRef.keepSynced(true);

        // Musique
//        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.suspense_music);
//        mMediaPlayer.setLooping(true);
//        mMediaPlayer.setVolume(100,100);
//        mMediaPlayer.start();


        final EditText editTextUserName = findViewById(R.id.connexionUserName);
        final EditText editTextUserPassword = findViewById(R.id.connexionUserPassword);
        Button buttonSend = findViewById(R.id.buttonConnexionSend);

        // On recupere les Shared  Preferences
        final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String sharedPrefUserName = sharedpreferences.getString(userName, "");
        final String sharedPrefUserPassword = sharedpreferences.getString(userPassword, "");
        final String sharedPrefUserKey = sharedpreferences.getString(mUserId, "");
        final ProgressBar simpleProgressBar = findViewById(R.id.simpleProgressBar);

        //On rempli les editText avec les sharedPreferences si c'est pas notre premiere connexion
        if (!sharedPrefUserName.isEmpty() && !sharedPrefUserPassword.isEmpty()) {
            editTextUserName.setText(sharedPrefUserName);
            editTextUserPassword.setText(sharedPrefUserPassword);
        }

        // Au clic du bouton, c'est la que tout se passe !!!!!!!!
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final ProgressDialog progressDialog = new ProgressDialog(ConnexionActivity.this);
//                progressDialog.setMessage("Chargement..."); // Setting Message
//                progressDialog.setTitle("ProgressDialog"); // Setting Title
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
//                progressDialog.show(); // Display Progress Dialog
//                progressDialog.setCancelable(false);
//                new Thread(new Runnable() {
//                    public void run() {
//                        try {
//                            Thread.sleep(10000);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        progressDialog.dismiss();
//                    }
//                }).start();


                simpleProgressBar.setVisibility(View.VISIBLE);

                //On recupere le contenu des edit text
                final String userNameContent = editTextUserName.getText().toString();
                final String userPasswordContent = editTextUserPassword.getText().toString();

                // Toast si les champs ne sont pas remplis
                if (TextUtils.isEmpty(userNameContent) || TextUtils.isEmpty(userPasswordContent)) {
                    Toast.makeText(getApplicationContext(), R.string.error_fill, Toast.LENGTH_SHORT).show();
                    simpleProgressBar.setVisibility(view.GONE);
                } else {
                    // Sinon on recupere tous les users
                    final DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("User");
                    refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                User userValues = dsp.getValue(User.class);

                                //On compare le contenu des edit text avec Firebase grâce au user_name
                                if (userValues.getUser_name().equals(userNameContent)) {
                                    // On verifie le password
                                    if (userValues.getUser_password().equals(mEncrypt(userPasswordContent, "AES"))) {

                                        // La clé de l'utilisateur qu'on va utiliser partout dans l'application.
                                        mUserId = dsp.getKey();
                                        // On sauvegarde l'utilisateur connu dans les sharedPreferences
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString(userName, userNameContent);
                                        editor.putString(userPassword, userPasswordContent);
                                        editor.putString("mUserId", mUserId);
                                        editor.apply();

                                        // If user is known : if he has no quest => LobbyActivity; if he has => PlayerActivity
                                        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("User");
                                        DatabaseReference db2 = db1.child(mUserId).child("user_quest");
                                        db2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String questOrNot = dataSnapshot.getValue(String.class);
                                                if (questOrNot.equals("Pas de qûete pour l'instant")) {
                                                    // Direct to Lobby if user is known & does not have quest
                                                    Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    // Direct to his quest if user is known & has a quest
                                                    Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    } else {
                                        // Si le mot de passe ou le pseudo ne concordent pas
                                        Toast.makeText(getApplicationContext(), R.string.error_password, Toast.LENGTH_SHORT).show();
                                        simpleProgressBar.setVisibility(View.GONE);
                                    }
                                    return;
                                }
                            }

                            // Utilisateur nouveau : le compte n'existe pas, on le créer !
                            String questContent = "Pas de qûete pour l'instant";
                            String challengeContent = "Pas de défi pour l'instant";
                            User user = new User(userNameContent, userPasswordContent, questContent, challengeContent, score);
                            user.setUser_name(userNameContent);
                            user.setUser_password(mEncrypt(userPasswordContent, "AES"));
                            user.setUser_quest(questContent);
                            user.setUser_indice("false");
                            user.setUser_challenge(challengeContent);
                            user.setUser_createdquestID("null");
                            user.setScore(0);
                            String userId = refUser.push().getKey();
                            refUser.child(userId).setValue(user);

                            // La clé de l'utilisateur qu'on va utiliser partout dans l'application.
                            mUserId = userId;

                            // On enregistre dans les shared Preferences
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(userName, userNameContent);
                            editor.putString(userPassword, userPasswordContent);
                            editor.putString("mUserId", userId);
                            editor.apply();
                            Toast.makeText(getApplicationContext(), R.string.created_user, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), RulesActivity.class));
                        }

                        // Encryptage du mot de passe
                        public String mEncrypt(String userPassword, String key) {
                            try {
                                Key clef = new SecretKeySpec(key.getBytes("ISO-8859-2"), "Blowfish");
                                Cipher cipher = Cipher.getInstance("Blowfish");
                                cipher.init(Cipher.ENCRYPT_MODE, clef);
                                return new String(cipher.doFinal(userPassword.getBytes()));
                            } catch (Exception e) {
                                return null;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        mMediaPlayer.stop();
//        mMediaPlayer.release();
//    }
//   @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        mMediaPlayer.stop();
//        mMediaPlayer.release();
//    }
}