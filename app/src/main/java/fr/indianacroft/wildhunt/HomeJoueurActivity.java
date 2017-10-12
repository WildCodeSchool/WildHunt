//package fr.indianacroft.wildhunt;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.annotation.NonNull;
//import android.support.design.widget.BottomNavigationView;
//import android.support.design.widget.NavigationView;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.view.ViewPager;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.firebase.ui.storage.images.FirebaseImageLoader;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//public class HomeJoueurActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//
//    private SectionsPagerAdapter mSectionsPagerAdapter;
//    private ViewPager mViewPager;
//    private String mUserId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_homejoueur);
//
//
//        // Pour recuperer la key d'un user (pour le lier a une quête)
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        mUserId = sharedPreferences.getString("mUserId", mUserId);
//        Log.d("key", mUserId);
//        /////////////////////////////////////////////////////////////////
//
//
//
//        // Toolbar
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//        // Drawer Menu
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        // Avatar
//        // POUR CHANGER L'AVATAR SUR LA PAGE AVEC CELUI CHOISI
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Avatar").child(mUserId);
//        final ImageView imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
//        // Load the image using Glide
//        if (storageReference.getDownloadUrl().isSuccessful()){
//            Glide.with(getApplicationContext())
//                    .using(new FirebaseImageLoader())
//                    .load(storageReference)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(imageViewAvatar);
//        }
//
//
//        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HomeJoueurActivity.this, ProfileActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        // Fragment Adapter
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.container);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mViewPager);
//
//    }
//
//    // Fragments
//    public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//                case 0:
//                    //onglet 1 vers Player Activity
//                    HomeJoueur_PlayerActivity playerActivity = new HomeJoueur_PlayerActivity();
//                    return playerActivity;
//                case 1:
//                    // onglet 2 vers Lobby Activity
//                    HomeJoueur_LobbyActivity lobbyActivity = new HomeJoueur_LobbyActivity();
//                    return lobbyActivity;
//                default:
//                    return null;
//            }
//        }
//        @Override
//        public int getCount() {
//            // Show 2 total pages.
//            return 2;
//        }
//        //TODO enlever hardcoding
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    // nom onglet 1
//                    return "Partie en cours";
//                case 1:
//                    // nom onglet 2
//                    return "Rejoindre Partie";
//            }
//            return null;
//        }
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        return super.onOptionsItemSelected(item);
//    }
//
//    // Drawer Menu
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//        // TODO : remplacer les toasts par des liens ET faire en sorte qu'on arrive sur les pages de fragments
//        if (id == R.id.nav_rules) {
//            Intent intent = new Intent(getApplicationContext(), RulesActivity.class);
//            startActivity(intent);
//        } else if (id == R.id.nav_play) {
//            Intent intent = new Intent(getApplicationContext(), HomeJoueurActivity.class);
//            startActivity(intent);
//        } else if (id == R.id.nav_create) {
//            startActivity(new Intent(getApplicationContext(), HomeGameMasterActivity.class));
//        } else if (id == R.id.nav_manage) {
//            Intent intent = new Intent(getApplicationContext(), HomeGameMasterActivity.class);
//            startActivity(intent);
//        } else if (id == R.id.nav_delete) {
//            startActivity(new Intent(getApplicationContext(), ConnexionActivity.class));
//        }
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_validate:
//                    return true;
//                case R.id.navigation_hint:
//                    return true;
//                case R.id.navigation_leave:
//                    return true;
//            }
//            return false;
//        }
//
//    };
//}
