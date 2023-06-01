package com.demo.icso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * created by CJS on 2023/5/24 15:28
 */
public class StartActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button btn_sbc = findViewById(R.id.SBC_sample_measurement);
        Button btn_oxy = findViewById(R.id.Oxygen_Saturation_Measurement);
        btn_oxy.setOnClickListener(this);
        btn_sbc.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SBC_sample_measurement:
                Intent mainIntent = new Intent(StartActivity.this, CatchrgbActivity.class);
                startActivity(mainIntent);
                break;
            case R.id.Oxygen_Saturation_Measurement:
                Intent mainIntent1 = new Intent(StartActivity.this, Clinic_Oxy_Activity.class);
                startActivity(mainIntent1);
                break;
        }
    }
}
