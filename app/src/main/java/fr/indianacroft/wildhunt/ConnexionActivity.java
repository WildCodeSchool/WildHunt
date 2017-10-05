package fr.indianacroft.wildhunt;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConnexionActivity extends AppCompatActivity {

    Button buttonConnexionPlay;
    Button buttonConnexionSignIn;
    Button buttonConnexionLogin;
    EditText editTextConnexionPseudo;
    EditText editTextConnexionPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        //TOAST bouton sign in si il n'y a aucune partie en cours.

        buttonConnexionPlay = (Button) findViewById(R.id.buttonConnexionPlay);

        buttonConnexionPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnexionActivity.this, HomeJoueur.class);
                startActivity(intent);

            }
        });

        // Click du bouton sign in permettant d'acceder à la page inscription.

        buttonConnexionSignIn = (Button) findViewById(R.id.buttonConnexionSignIn);

        buttonConnexionSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConnexionActivity.this,InscriptionActivity.class);
                startActivity(intent);
            }
        });

        // Champs editText deviennent visible quand on click sur le bouton log in.

        editTextConnexionPseudo = (EditText) findViewById(R.id.editTextConnexionPseudo);
        editTextConnexionPassword = (EditText) findViewById(R.id.editTextConnexionPassword);
        buttonConnexionLogin = (Button) findViewById(R.id.buttonConnexionLogIn);

        editTextConnexionPseudo.setVisibility(View.GONE);
        editTextConnexionPassword.setVisibility(View.GONE);

        buttonConnexionLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextConnexionPassword.getVisibility() == View.VISIBLE){
                    editTextConnexionPassword.setVisibility(View.GONE);
                    editTextConnexionPseudo.setVisibility(View.GONE);
                }else {
                    editTextConnexionPseudo.setVisibility(View.VISIBLE);
                    editTextConnexionPassword.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}

