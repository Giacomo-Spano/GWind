package wind.newwindalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Giacomo Spanò on 29/07/2016.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!sendLogToMail()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }


        /**/
    }

    boolean sendLogToMail() {

        String line, trace = "";

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(SplashActivity.this
                            .openFileInput("stack.trace"))); // se non esiste Filenotfoundexception
            while((line = reader.readLine()) != null) {
                trace += line+"\n";
            }

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            String subject = "Error report";
            String body =
                    "Giacomo, l'applicazione si è bloccata in questo punto: \n" +
                            "Mail this to appdeveloper@gmail.com: "+
                            "\n"+
                            trace+
                            "\n";

            sendIntent.putExtra(Intent.EXTRA_EMAIL,
                    new String[] {"giaggi70@gmail.com"});
            sendIntent.putExtra(Intent.EXTRA_TEXT, body);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.setType("message/rfc822");

            SplashActivity.this.startActivity(
                    Intent.createChooser(sendIntent, "Title:"));

            SplashActivity.this.deleteFile("stack.trace");



        } catch(FileNotFoundException fnfe) {

            // se il file non esiste non fare nulla
            return false;
// ...
        } catch(IOException ioe) {
// ...
        }

        return true;
    }
}