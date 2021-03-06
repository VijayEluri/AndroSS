package net.tedstein.AndroSS;

import net.tedstein.AndroSS.AndroSSService.CompressionType;
import net.tedstein.AndroSS.AndroSSService.DeviceType;
import net.tedstein.AndroSS.util.Prefs;
import net.tedstein.AndroSS.util.RootUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

public class ConfigurationActivity extends Activity {
    static {
        System.loadLibrary("AndroSS_nbridge");
    }

    private static final String TAG = "AndroSS";
    private DeviceType mDeviceType = DeviceType.UNKNOWN;
    private boolean started_other_activity = false;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.config, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.config_more_settings:
            this.started_other_activity = true;
            Intent i = new Intent(this, MoreSettings.class);
            startActivity(i);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Bring back the default theme. If the GLDetector runs, this won't take
        // effect until it finishes, so there's no animation weirdness.
        setTheme(android.R.style.Theme);

        super.onCreate(savedInstanceState);
        // If vendor is unknown, we need to check that. We'll do the root check
        // in onActivityResult() if this isn't a Tegra device.
        if (AndroSSService.getOpenGLVendor(this).equals("unknown")) {
            this.started_other_activity = true;
            Intent i = new Intent(this, GLDetector.class);
            startActivityForResult(i, 0);
            overridePendingTransition(0, 0);
        }

        setContentView(R.layout.config);

        final Context c = this;
        CheckBox enabled = (CheckBox)findViewById(R.id.ServiceStatusCheckBox);
        CheckBox persistent = (CheckBox)findViewById(R.id.PersistenceCheckBox);

        Spinner compression = (Spinner)findViewById(R.id.CompressionSpinner);
        ArrayAdapter<CharSequence> types = ArrayAdapter.createFromResource(this,
                R.array.compression_types, android.R.layout.simple_spinner_item);
        types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        compression.setAdapter(types);

        CheckBox useCamera = (CheckBox)findViewById(R.id.CameraButtonCheckBox);
        CheckBox useShake = (CheckBox)findViewById(R.id.ShakeCheckBox);

        CheckBox notifyToast = (CheckBox)findViewById(R.id.ToastNotifyCheckBox);
        CheckBox notifyAudio = (CheckBox)findViewById(R.id.AudioNotifyCheckBox);
        CheckBox notifyVibe = (CheckBox)findViewById(R.id.VibeNotifyCheckBox);

        enabled.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                final SharedPreferences sp = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);

                Intent i = new Intent(c, AndroSSService.class);
                if (isChecked) {
                    if (mDeviceType == DeviceType.GENERIC &&
                            sp.getBoolean(Prefs.HAVE_ROOT_KEY, false) == false) {
                        Log.d(TAG, "Activity: Not setting Enabled to true because we lack root.");
                        RootUtils.showRootTestFailedMessage(c);
                        buttonView.setChecked(false);
                    } else {
                        startService(i);
                    }
                } else {
                    stopService(i);
                }
            }
        });

        persistent.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final SharedPreferences sp = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor spe = sp.edit();

                spe.putBoolean(Prefs.PERSISTENT_KEY, isChecked);
                spe.commit();
            }
        });

        compression.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {
                final SharedPreferences sp = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor spe = sp.edit();

                CompressionType ct = CompressionType.values()[pos];

                spe.putString(Prefs.COMPRESSION_KEY, ct.name());
                spe.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        useCamera.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final SharedPreferences sp = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor spe = sp.edit();

                spe.putBoolean(Prefs.CAMERA_TRIGGER_KEY, isChecked);
                spe.commit();
            }
        });

        useShake.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final SharedPreferences sp = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor spe = sp.edit();

                spe.putBoolean(Prefs.SHAKE_TRIGGER_KEY, isChecked);
                spe.commit();
            }
        });

        notifyToast.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final SharedPreferences sp = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor spe = sp.edit();

                spe.putBoolean(Prefs.TOAST_FEEDBACK_KEY, isChecked);
                spe.commit();
            }
        });

        notifyAudio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final SharedPreferences sp = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor spe = sp.edit();

                spe.putBoolean(Prefs.AUDIO_FEEDBACK_KEY, isChecked);
                spe.commit();
            }
        });

        notifyVibe.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final SharedPreferences sp = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor spe = sp.edit();

                spe.putBoolean(Prefs.VIBRATE_FEEDBACK_KEY, isChecked);
                spe.commit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences sp = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);

        CheckBox enabled = (CheckBox)findViewById(R.id.ServiceStatusCheckBox);
        CheckBox persistent = (CheckBox)findViewById(R.id.PersistenceCheckBox);

        Spinner compression = (Spinner)findViewById(R.id.CompressionSpinner);
        ArrayAdapter<CharSequence> types = ArrayAdapter.createFromResource(this,
                R.array.compression_types, android.R.layout.simple_spinner_item);
        types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        compression.setAdapter(types);

        CheckBox useCamera = (CheckBox)findViewById(R.id.CameraButtonCheckBox);
        CheckBox useShake = (CheckBox)findViewById(R.id.ShakeCheckBox);

        CheckBox notifyToast = (CheckBox)findViewById(R.id.ToastNotifyCheckBox);
        CheckBox notifyAudio = (CheckBox)findViewById(R.id.AudioNotifyCheckBox);
        CheckBox notifyVibe = (CheckBox)findViewById(R.id.VibeNotifyCheckBox);

        enabled.setChecked(sp.getBoolean(Prefs.ENABLED_KEY, false));
        persistent.setChecked(sp.getBoolean(Prefs.PERSISTENT_KEY, false));

        compression.setSelection(
                CompressionType.valueOf(
                        sp.getString(Prefs.COMPRESSION_KEY,
                                CompressionType.PNG.name()))
                        .ordinal());

        useShake.setChecked(sp.getBoolean(Prefs.SHAKE_TRIGGER_KEY, false));
        useCamera.setChecked(sp.getBoolean(Prefs.CAMERA_TRIGGER_KEY, false));

        notifyToast.setChecked(sp.getBoolean(Prefs.TOAST_FEEDBACK_KEY, false));
        notifyAudio.setChecked(sp.getBoolean(Prefs.AUDIO_FEEDBACK_KEY, false));
        notifyVibe.setChecked(sp.getBoolean(Prefs.VIBRATE_FEEDBACK_KEY, false));
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (this.started_other_activity == false) {
            finish();
        }
    }


    @Override
    protected void onRestart() {
        super.onResume();

        this.started_other_activity = false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(android.R.anim.fade_in, 0);
        // It would seem like this isn't necessary because this is set to false
        // in onRestart(), but, for reasons I can't confidently discern, only
        // setting it there makes it possible to get two ConfigurationActivities
        // in the stack if the root dialog gets shown. Doing it here as well
        // fixes that.
        this.started_other_activity = false;

        final Context c = this;
        SharedPreferences sp = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);
        AndroSSService.setOpenGLVendor(data.getStringExtra(GLDetector.VENDOR_EXTRA));

        mDeviceType = AndroSSService.getDeviceType(this);
        if (mDeviceType == DeviceType.GENERIC &&
            sp.getBoolean(Prefs.HAVE_TESTED_ROOT_KEY, false) == false) {
            Log.d(TAG, "Activity: Don't know if we have root; showing dialog.");
            RootUtils.showRootTestMessage(c);
        }
    }
}
