package com.tsp.learn.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.tsp.learn.R;

public class RecyclerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        Button linearBtn = (Button)findViewById(R.id.linear_activity_btn);
        Button gridBtn = (Button)findViewById(R.id.grid_activity_btn);
        Button staggerBtn = (Button)findViewById(R.id.stagger_activity_btn);
        Button customBtn = (Button)findViewById(R.id.custom_activity_btn);


        linearBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RecyclerActivity.this,LinearActivity.class);
            startActivity(intent);
        });

        gridBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RecyclerActivity.this,GridActivity.class);
            startActivity(intent);
        });

        staggerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RecyclerActivity.this,StaggeredActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.diff_item_activity_btn).setOnClickListener(v -> {
            Intent intent = new Intent(RecyclerActivity.this,DiffItemActivity.class);
            startActivity(intent);
        });

        customBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RecyclerActivity.this,CustomLayoutActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.recycled_activity_btn).setOnClickListener(v -> {
            Intent intent = new Intent(RecyclerActivity.this,CustomRecycledLayoutActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.first_remould_recycled_activity_btn).setOnClickListener(v -> {
            Intent intent = new Intent(RecyclerActivity.this,RecycledFirstRemouldActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.second_remould_recycled_activity_btn).setOnClickListener(v -> {
            Intent intent = new Intent(RecyclerActivity.this,RecycledSecondRemouldActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cover_flow).setOnClickListener(v -> {
            Intent intent = new Intent(RecyclerActivity.this,CoverFlowActivity.class);
            startActivity(intent);
        });


    }
}
