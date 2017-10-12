package fr.indianacroft.wildhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by wilder on 11/10/17.
 */

public class ChallengeViewHolder extends RecyclerView.ViewHolder {

    public TextView mChallengeName;
    public TextView mChallengeDiff;
    private String mUserId;

    public ChallengeViewHolder(final View itemView) {
        super(itemView);

        // Pour recuperer la key d'un user (pour le lier a une quête)
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        mUserId = preferences.getString("mUserId", "");
        /////////////////////////////////////////////////////////////////

        mChallengeName = (TextView) itemView.findViewById(R.id.challengeName);
        mChallengeDiff = (TextView) itemView.findViewById(R.id.challengeDiff);

    }

//    private static LobbyViewHolder.ClickListener mClickListener;
//
//    //Interface to send callbacks...
//    public interface ClickListener{
//        public void onItemClick(View view, int position);
//    }
//
//    public static void setOnClickListener(LobbyViewHolder.ClickListener clickListener){
//        mClickListener = clickListener;
//    }

///////// POUR RENTRER LES DONNEES DANS LE RECYCLER VIEW \\\\\\\\\\\\\\\
    //______________________________________________________\\

    // Pour mettre le nom de la party / quête dans le reclycer view (les parametres doivent etres exactement pareil que sur FIREBASE)
    public void setChallenge_name(String challenge_name) {
        mChallengeName.setText(challenge_name);
    }
    // pour mettre la description de la party / quête dans le recycler view
    public void setChallenge_difficulty(String challenge_difficulty) {
        mChallengeDiff.setText(challenge_difficulty);
    }
}

