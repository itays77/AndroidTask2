package com.example.task1;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.task1.Fragments.ListFragment;
import com.example.task1.Fragments.MapFragment;
import com.example.task1.Interfaces.Callback_ListItemClicked;
import com.google.android.material.button.MaterialButton;

public class RecordActivity extends AppCompatActivity implements Callback_ListItemClicked {

    private ListFragment listFragment;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        MaterialButton btnReturn = findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> returnToMainActivity());

        initFragments();
    }

    private void initFragments() {
        listFragment = new ListFragment();
        mapFragment = new MapFragment();

        listFragment.setCallbackListItemClicked(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_FRAME_list, listFragment)
                .add(R.id.main_FRAME_map, mapFragment)
                .commit();
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void listItemClicked(double lat, double lon) {
        mapFragment.zoom(lat, lon);
    }
}