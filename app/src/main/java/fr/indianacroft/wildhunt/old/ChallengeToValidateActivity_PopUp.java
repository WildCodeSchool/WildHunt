package fr.indianacroft.wildhunt.old;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fr.indianacroft.wildhunt.R;

public class ChallengeToValidateActivity_PopUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_challenge_to_validate__pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout(width * 1, (int) (height * .5));


        String mCreatedQuestId = getIntent().getStringExtra("Quest");
        String challengeId = getIntent().getStringExtra("Challenge");


        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child("Quest").child(mCreatedQuestId).child(challengeId);

        ImageView imageSolution = findViewById(R.id.imageSolution);

        /*Glide.with(getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageSolution);*/
    }
}
