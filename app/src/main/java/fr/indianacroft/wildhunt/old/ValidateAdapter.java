package fr.indianacroft.wildhunt.old;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fr.indianacroft.wildhunt.R;


/**
 * Created by wilder on 16/10/17.
 */
public class ValidateAdapter extends BaseAdapter {

    private String mUserId;
    private String mQuestId;

    private Context context;
    private ArrayList<Pair> map;


    public ValidateAdapter(Context context, ArrayList<Pair>map) {
        this.context = context;
        this.map = map;
    }

    public int getCount() {
        return map.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        // Pour recuperer la key d'un user (pour le lier a une quête)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mUserId = sharedPreferences.getString("mUserId", mUserId);

        Log.d("key", mUserId);
        /////////////////////////////////////////////////////////////////

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.validatechallenge_recyclerview,
                    null);
        }

        final String challengeKey = map.get(position).first.toString();
        final String userKey = map.get(position).second.toString();

        DatabaseReference refUser =
                FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);
        final View finalConvertView = convertView;

        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // on recupere la qûete créee par un user
                User user = dataSnapshot.getValue(User.class);
                final String mQuestId = user.getUser_createdquestID();

                // On recupere le nom du challenge
                DatabaseReference refChallenge = FirebaseDatabase.getInstance().getReference("Challenge").child(mQuestId).child(challengeKey);
                refChallenge.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Challenge challenge = dataSnapshot.getValue(Challenge.class);
                        String challName = challenge.getChallenge_name();

                        TextView nameChall = finalConvertView.findViewById(R.id.challengeToValidate);
                        nameChall.setText(challName);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // On recupere le user a valider
                DatabaseReference refUserToValidate = FirebaseDatabase.getInstance().getReference("User").child(userKey);
                refUserToValidate.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String userNom = user.getUser_name();

                        TextView nameUser = finalConvertView.findViewById(R.id.userToValidate);
                        nameUser .setText(userNom);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                Button see = finalConvertView.findViewById(R.id.see);

                see.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ChallengeToValidateActivity.class);
                        intent.putExtra("ToValidate", challengeKey);
                        intent.putExtra("UserToValidate", userKey);
                        intent.putExtra("CreatedQuestId", mQuestId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return convertView;
    }
}