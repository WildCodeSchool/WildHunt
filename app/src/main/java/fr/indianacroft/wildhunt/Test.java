package fr.indianacroft.wildhunt;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by wilder on 13/10/17.
 */

public class Test {
    ArrayList<String> arrayUser = new ArrayList<String>();
    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("User");

    public void user(){

        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                String[] userTab = new String[(int)dataSnapshot.getChildrenCount()];
                ArrayList<User> tabuser = new ArrayList<User>((int) dataSnapshot.getChildrenCount());
                for (DataSnapshot dsp : dataSnapshot.getChildren()){
                    User userId = dsp.getValue(User.class);
//                    String userkey = dsp.getKey();
//                    userTab[i] = userkey;
                    tabuser.add(userId);
                    i++;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
