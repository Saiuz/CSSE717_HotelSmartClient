package li.gogo1;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {

    private Switch IntroductionSwitch;
    private boolean On = false;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        On = sharedPreferences.getBoolean("check", false);

        IntroductionSwitch = (Switch)findViewById(R.id.IntroductionSwitch_SA);
        IntroductionSwitch.setChecked(On);
        IntroductionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("debug", "onCheckedChanged, isChecked : " + b);
                editor.putBoolean("check", b);
                editor.apply();
            }
        });
    }
}
