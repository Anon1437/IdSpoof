package com.example.idspoof;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setPadding(48, 96, 48, 48);
        tv.setTextSize(16);
        tv.setText("Android ID Spoofer\n\nEnable this module in LSPosed, select the target app(s) in its scope, then force-stop and reopen them.");
        setContentView(tv);
    }
}
