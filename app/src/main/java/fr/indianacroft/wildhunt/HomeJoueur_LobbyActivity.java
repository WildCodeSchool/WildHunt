//package fr.indianacroft.wildhunt;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
///**
// * Created by pierre on 9/26/17.
// */
//
//public class HomeJoueur_LobbyActivity extends Fragment {
//    private static final String TAG = HomeJoueur_LobbyActivity.class.getSimpleName();
//    private FirebaseDatabase ref;
//    private DatabaseReference childRef;
//    private String mUserId;
//
//
//    // Methode utilisée pour afficher une ligne en dessous de chaque item du recycler view
//    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
//        private Drawable mDivider;
//
//        public SimpleDividerItemDecoration(HomeJoueur_LobbyActivity context) {
//            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
//        }
//
//        @Override
//        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
//            int left = parent.getPaddingLeft();
//            int right = parent.getWidth() - parent.getPaddingRight();
//
//            int childCount = parent.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                View child = parent.getChildAt(i);
//
//                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//
//                int top = child.getBottom() + params.bottomMargin;
//                int bottom = top + mDivider.getIntrinsicHeight();
//
//                mDivider.setBounds(left, top, right, bottom);
//                mDivider.draw(c);
//            }
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.homejoueur_lobbyactivity, container, false);
//
//        // Pour recuperer la key d'un user (pour le lier a une quête)
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//        mUserId = preferences.getString("mUserId", "");
//        // On recupere la qûete dans laquelle il est
//
//        // je recupere la KEY de la quête choisi grâce a son nom
//        DatabaseReference refUserQuest =
//                FirebaseDatabase.getInstance().getReference().child("User").child(mUserId).child("user_quest");
//        refUserQuest.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    String questKey = child.getKey(); // ID de la quête
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//
//
//
//        return view;
//    }
//}
