package android.encdecsms;
import java.security.Key;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EncDecSMSActivity extends Activity {
    /** Called when the activity is first created. */
    EditText recNum;
    EditText secretKey;
    EditText msgContent;
    Button send;
    Button cancel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enc_dec_sms);
        recNum = (EditText) findViewById(R.id.recNum);
        secretKey = (EditText) findViewById(R.id.secretKey);
        msgContent = (EditText) findViewById(R.id.msgContent);
        send = (Button) findViewById(R.id.Send);
        cancel = (Button) findViewById(R.id.cancel);
// finish the activity when click Cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        // encrypt the message and send when click Send button
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String recNumString = recNum.getText().toString();
                String secretKeyString = secretKey.getText().toString();
                String msgContentString = msgContent.getText().toString();
                if (recNumString.length() > 0 && secretKeyString.length() > 0 && msgContentString.length() > 0
                        && secretKeyString.length() == 16) {
// encrypt the message
                    byte[] encryptedMsg = encryptSMS(secretKeyString,
                            msgContentString);
                    String msgString = byte2hex(encryptedMsg);
                    sendSMS(recNumString, msgString);
                    finish();
                } else
                    Toast.makeText(getBaseContext(),
                            "Please enter phone number, secret key and the message. Secret key must be 16 characters!",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void sendSMS(String recNumString, String encryptedMsg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(encryptedMsg);
            smsManager.sendMultipartTextMessage(recNumString, null, parts,
                    null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // utility function
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1) hs += ("0" + stmp);
            else hs += stmp;
        }
        return hs.toUpperCase();
    }
    // encryption function
    public static byte[] encryptSMS(String secretKeyString, String msgContentString) {
        try {
            byte[] returnArray;
// generate AES secret key from user input
            Key key = generateKey(secretKeyString);
// specify the cipher algorithm using AES
            Cipher c = Cipher.getInstance("AES");
// specify the encryption mode
            c.init(Cipher.ENCRYPT_MODE, key);
// encrypt
            returnArray = c.doFinal(msgContentString.getBytes());
            return returnArray;
        } catch (Exception e) {
            e.printStackTrace();
            byte[] returnArray = null;
            return returnArray;
        }
    }
    private static Key generateKey(String secretKeyString) throws Exception {
// generate secret key from string
        Key key = new SecretKeySpec(secretKeyString.getBytes(), "AES");
        return key;
    }
}
