package com.idunnolol.findviewbyid;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends ActionBarActivity {

    private Random mRandom;

    private TextView mStatusTextView;
    private TextView mResultsTextView;
    private ViewGroup mContainer;

    private int mDepth = 0;
    private int mNumChildrenPerNode = 1;

    private int mId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRandom = new Random();

        setContentView(R.layout.activity_main);

        mStatusTextView = (TextView) findViewById(R.id.status_text_view);
        mResultsTextView = (TextView) findViewById(R.id.results_text_view);
        mContainer = (ViewGroup) findViewById(R.id.test_container);

        findViewById(R.id.add_depth_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDepth++;
                updateStatus();
                checkTree(mContainer, 0);
            }
        });
        findViewById(R.id.add_child_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumChildrenPerNode++;
                checkTree(mContainer, 0);
            }
        });
        findViewById(R.id.run_test_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTest(100);
            }
        });
        findViewById(R.id.reset_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDepth = 0;
                mNumChildrenPerNode = 1;
                mId = 1;
                updateStatus();
                reset(mContainer);
            }
        });

        updateStatus();
    }

    private void updateStatus() {
        mStatusTextView.setText("Depth=" + mDepth + " Children (per node)=" + mNumChildrenPerNode + " total=" + mDepth * mNumChildrenPerNode);
    }

    // Recursively checks each node to see if it follows the current params set out
    private void checkTree(ViewGroup view, int depth) {
        if (depth < mDepth) {
            // If we're not at max depth, check that this node has enough children
            while (view.getChildCount() < mNumChildrenPerNode) {
                view.addView(inflateChildLayout(view));
            }

            // For each child, check their integrity
            for (int a = 0; a < view.getChildCount(); a++) {
                checkTree((ViewGroup) view.getChildAt(a), depth + 1);
            }
        }

        // If this view group has children, depend on child to define width/height
        if (view.getChildCount() != 0) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    private void reset(ViewGroup view) {
        view.removeAllViews();
    }

    private ViewGroup inflateChildLayout(ViewGroup parent) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.child, parent, false);

        // Give it a random bg color so we can distinguish it in the UI
        int color = Color.rgb(mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        viewGroup.setBackgroundColor(color);

        // Give it an ID we can use to look it up
        viewGroup.setId(mId);
        mId++;

        return viewGroup;
    }

    private void runTest(int numTimes) {
        long start, end;
        long total = 0;
        for (int a = 0; a < numTimes; a++) {
            start = System.nanoTime();
            mContainer.findViewById(mId); // Find the FURTHEST id
            end = System.nanoTime();
            total += end - start;
        }

        long avg = total / numTimes;
        mResultsTextView.setText("Avg Time for findViewById(" + mId + "): " + avg + " ns (" + (avg / 1000000) + " ms)");
    }
}
