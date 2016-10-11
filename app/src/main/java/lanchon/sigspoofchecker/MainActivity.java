package lanchon.sigspoofchecker;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSpoofStatus();
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
