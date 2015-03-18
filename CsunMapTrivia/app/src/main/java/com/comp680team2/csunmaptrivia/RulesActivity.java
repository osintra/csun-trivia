package com.comp680team2.csunmaptrivia;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by nishika on 3/17/2015.
 */
public class RulesActivity extends Activity{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_activity);

        Button backButton = (Button)findViewById(R.id.ruleButton1);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                finish();
            }
        });
    }
}

