package lanchon.sigspoof.checker;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String SPOOF_PERMISSION = "android.permission.FAKE_PACKAGE_SIGNATURE";
    private static final int SPOOF_REQUEST_CODE = 1;

    private RelativeLayout mainRelativeLayout;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSpoofStatus();
        requestSpoofRuntimePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSpoofStatus();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == SPOOF_REQUEST_CODE) {
            updateSpoofStatus();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void requestSpoofRuntimePermission() {
        if (ContextCompat.checkSelfPermission(this, SPOOF_PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{SPOOF_PERMISSION},
                    SPOOF_REQUEST_CODE);
        }
    }

    protected void updateSpoofStatus() {
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        Signature[] signatures = packageInfo.signatures;
        if (signatures.length != 1) throw new RuntimeException("Got " + signatures.length + " signatures");
        String signatureString = signatures[0].toCharsString();
        Log.i("MainActivity", "Reported signature: " + signatureString);
        setSpoofStatus(getString(R.string.fake_signature).equals(signatureString));
    }

    protected void setSpoofStatus(boolean enabled) {
        if (enabled) {
            mainRelativeLayout.setBackgroundColor(getResources().getColor(R.color.spoofStatusEnabled));
            statusTextView.setText(R.string.spoofStatusEnabled);
        } else {
            mainRelativeLayout.setBackgroundColor(getResources().getColor(R.color.spoofStatusDisabled));
            statusTextView.setText(R.string.spoofStatusDisabled);
        }
    }

}
