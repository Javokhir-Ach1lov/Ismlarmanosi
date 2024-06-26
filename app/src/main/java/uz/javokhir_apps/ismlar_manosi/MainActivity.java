package uz.javokhir_apps.ismlar_manosi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private String searchText = "";
    private List<Word> wordsList;
    ArrayAdapter<String> adapter;
    private int backButtonCount = 0;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        MobileAds.initialize(this, initializationStatus -> {});

        listView = findViewById(R.id.listView);

        wordsList = Utils.loadWordsFromJSON(this, searchText);

        List<String> wordNamesList = new ArrayList<>();
        for (Word word : wordsList) {
            wordNamesList.add(word.getWord());
        }

        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.text1, wordNamesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String word =(String) parent.getAdapter().getItem(position);
            String definition="";
            for(Word w: wordsList){
                if(w.getWord().equals(word)){
                    definition = w.getDefinition();
                }
            }

            showBottomSheetDialog(word, definition);
        });
    }

    public void onUserBackButtonPressed(View view) {
        backButtonCount = 0;
    }
    private void showMoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_more, null);

        builder.setView(dialogView)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showMoreDialogContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_contact, null);

        builder.setView(dialogView)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showMoreDialogPrivacy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_about_app, null);

        builder.setView(dialogView)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            startActivity(intent);

    }

    private void showBottomSheetDialog(String word, String definition) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(bottomSheetView);

        AdView adView = bottomSheetView.findViewById(R.id.adView2);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        TextView wordTextView = bottomSheetView.findViewById(R.id.wordTextView);
        TextView definitionTextView = bottomSheetView.findViewById(R.id.definitionTextView);
        ImageView shareButton = bottomSheetView.findViewById(R.id.shareButton);
        ImageView copyButton = bottomSheetView.findViewById(R.id.copyButton);
        ImageView closeButton = bottomSheetView.findViewById(R.id.closeButton);

        View parent = (View) bottomSheetView.getParent();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        wordTextView.setText(word);
        definitionTextView.setText(definition);

        shareButton.setOnClickListener(v -> {
            String shareContent = "Term: " + word + "\n\nDescription: " + definition + "\n\nDownload app: ";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);

            startActivity(Intent.createChooser(shareIntent, "Share via"));
                });

        dialog.show();
    }

    private void updateListView(String searchText) {
        List<String> filteredWordNamesList = new ArrayList<>();

        for (Word word : wordsList) {
            if (word.getWord().toLowerCase().contains(searchText.toLowerCase())) {
                filteredWordNamesList.add(word.getWord());
            }
        }

        ArrayAdapter<String> filteredAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.text1, filteredWordNamesList);
        listView.setAdapter(filteredAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);

                return false;
            }

        });
        MenuItem anotherMenuItem = menu.findItem(R.id.about);
        anotherMenuItem.setOnMenuItemClickListener(item -> {

            showMoreDialog();
            return true;
        });

        MenuItem privacy = menu.findItem(R.id.privacy);
        privacy.setOnMenuItemClickListener(item -> {

            showMoreDialogPrivacy();
            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }
}
