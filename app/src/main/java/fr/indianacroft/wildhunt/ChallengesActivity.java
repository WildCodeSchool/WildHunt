package fr.indianacroft.wildhunt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class ChallengesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText editTextChallengeName;
    EditText editTextHint;
    String editTextChallengeNameContent;
    String editTextHintContent;
    Button butCreateChallenge;
    ImageView imageViewInscriptionLogo;
    Button butLoad;
    Button butUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

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
        ImageView imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChallengesActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_challenge);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.challenge_difficulty, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Button Actions / Change ImageView / Text in buttons
        // TODO : changer les challenges en list view
        editTextChallengeName = (EditText) findViewById(R.id.name_challenge);
        editTextHint = (EditText) findViewById(R.id.hint_challenge);
        butCreateChallenge = (Button) findViewById(R.id.butCreateChallenge);
        /*imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
        imageViewCancel2 = (ImageView) findViewById(R.id.imageViewCancel2);*/
        butCreateChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextChallengeNameContent = editTextChallengeName.getText().toString();
                editTextHintContent = editTextHint.getText().toString();
                if ((!editTextChallengeNameContent.equals("")) && (!editTextHintContent.equals(""))) {
                    /*imageViewCancel.setImageResource(R.drawable.checked);
                    imageViewCancel2.setImageResource(R.drawable.checked);
                    butNewChallenge.setText("Challenge 1 created");
                    butNewChallenge2.setText("Challenge 2 created");*/
                    Intent intent = new Intent(ChallengesActivity.this, HomeGameMaster.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ChallengesActivity.this, R.string.toast_challenge, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Upload & Take photo
        butLoad = (Button) findViewById(R.id.butLoad);
        butLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        butUpload = (Button) findViewById(R.id.butUpload);
        butUpload.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }});
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
        // TODO : remplacer les toasts par des liens
        if (id == R.id.nav_home) {
            Intent intent = new Intent(ChallengesActivity.this, HomeGameMaster.class);
            startActivity(intent);
        } else if (id == R.id.nav_rules) {
            Intent intent = new Intent(ChallengesActivity.this, Rules.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(ChallengesActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_camera) {
            Toast.makeText(ChallengesActivity.this, "Lien page Photo", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_quests) {
            Intent intent = new Intent(ChallengesActivity.this, HomeGameMaster.class);
            startActivity(intent);
        } else if (id == R.id.nav_switch) {
            Intent intent = new Intent(ChallengesActivity.this, HomeJoueur.class);
            startActivity(intent);
        } else if (id == R.id.nav_delete) {
            Toast.makeText(ChallengesActivity.this, "Déco joueur", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Send photos to ImageView
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageViewInscriptionLogo = (ImageView) findViewById(R.id.imageViewInscriptionLogo);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
                //&& data != null && data.getData() != null
            ) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageViewInscriptionLogo.setImageBitmap(imageBitmap);
        }
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                imageViewInscriptionLogo.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
