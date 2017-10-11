package fr.indianacroft.wildhunt;


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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ConnexionActivity extends AppCompatActivity {

    final String userName = "NameKey";
    final String userPassword = "PasswordKey";
    private boolean auth = false;
    private String mUserId = "UserKey";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        final EditText editTextUserName = (EditText) findViewById(R.id.connexionUserName);
        final EditText editTextUserPassword = (EditText) findViewById(R.id.connexionUserPassword);
        Button buttonSend = (Button) findViewById(R.id.buttonConnexionSend);

        // On recupere les Shared  Preferences
        final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String sharedPrefUserName = sharedpreferences.getString(userName, "");
        final String sharedPrefUserPassword = sharedpreferences.getString(userPassword, "");
        final String sharedPrefUserKey = sharedpreferences.getString(mUserId, "");

        //On rempli les editText avec les sharedPreferences si c'est pas notre premiere connexion
        if (!sharedPrefUserName.isEmpty() && !sharedPrefUserPassword.isEmpty()) {
            editTextUserName.setText(sharedPrefUserName);
            editTextUserPassword.setText(sharedPrefUserPassword);
        }


        // Au clic du bouton, c'est la que tout se passe
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //On recupere le contenu des edit text

                final String userNameContent = editTextUserName.getText().toString();
                final String userPasswordContent = editTextUserPassword.getText().toString();
                // Toast si les champs ne sont pas rempli
                if (TextUtils.isEmpty(userNameContent) || TextUtils.isEmpty(userPasswordContent)) {
                    Toast.makeText(getApplicationContext(), R.string.error_fill, Toast.LENGTH_SHORT).show();
                } else {
                    //On recupere tout les users
                    final DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("User");
                    refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int compteur = 0;

                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                User userValues = dsp.getValue(User.class);

                                //On compare le contenu des edit text avec Firebase grâce au user_name
                                if (userValues.getUser_name().equals(userNameContent)) {
                                    // On verifie le password
                                    if (userValues.getUser_password().equals(userPasswordContent)) {

                                        // La clé de l'utilisateur qu'on va utiliser partout dans l'application.
                                        mUserId = dsp.getKey();
                                        // On sauvegarde dans les sharedPreferences
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString(userName, userNameContent);
                                        editor.putString(userPassword, userPasswordContent);
                                        editor.putString("mUserId", mUserId);
                                        editor.apply();

                                        startActivity(new Intent(getApplicationContext(), HomeJoueurActivity.class));


                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.error_password, Toast.LENGTH_SHORT).show();
                                    }
                                    return;

                                }

                            }
                            // Le compte n'existe pas, on le créer !
                            String questContent = "Pas de qûete pour l'instant";
                            User user = new User(userNameContent, userPasswordContent, questContent);
                            user.setUser_name(userNameContent);
                            user.setUser_password(userPasswordContent);
                            user.setUser_quest(questContent);
                            user.setUser_indice("false");
                            String userId = refUser.push().getKey();
                            refUser.child(userId).setValue(user);

                            // La clé de l'utilisateur qu'on va utiliser partout dans l'application.
                            mUserId = userId;

                            //On enregistre dans les shared Preferences
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(userName, userNameContent);
                            editor.putString(userPassword, userPasswordContent);
                            editor.putString("mUserId", userId);
                            editor.apply();
                            Toast.makeText(getApplicationContext(), R.string.created_user, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), RulesActivity.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

}