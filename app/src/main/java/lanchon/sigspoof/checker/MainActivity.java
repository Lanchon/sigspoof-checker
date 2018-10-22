package lanchon.sigspoof.checker;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String SPOOF_PERMISSION_NAME = "android.permission.FAKE_PACKAGE_SIGNATURE";
    private static final int SPOOF_PERMISSION_REQUEST_CODE = 1;

    private RelativeLayout mainRelativeLayout;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainRelativeLayout = findViewById(R.id.mainRelativeLayout);
        statusTextView = findViewById(R.id.statusTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSpoofStatus();
        requestSpoofPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSpoofStatus();
    }

    // Request Signature Spoofing Runtime Permission

    protected void requestSpoofPermission() {
        if (ContextCompat.checkSelfPermission(this, SPOOF_PERMISSION_NAME) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{SPOOF_PERMISSION_NAME}, SPOOF_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SPOOF_PERMISSION_REQUEST_CODE: {
                updateSpoofStatus();
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Report Signature Spoofing Functional Status

    protected void updateSpoofStatus() {
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        Signature[] signatures = packageInfo.signatures;
        if (signatures.length != 1) {
            throw new RuntimeException("Got " + signatures.length + " signatures");
        }
        String signatureString = signatures[0].toCharsString();
        Log.i(TAG, "Reported signature: " + signatureString);
        setSpoofStatus(getString(R.string.fake_signature).equals(signatureString));
    }

    protected void setSpoofStatus(boolean enabled) {
        if (enabled) {
            mainRelativeLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.spoofStatusEnabled));
            statusTextView.setText(R.string.spoofStatusEnabled);
        } else {
            mainRelativeLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.spoofStatusDisabled));
            statusTextView.setText(R.string.spoofStatusDisabled);
        }
    }

}
