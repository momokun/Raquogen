package gs.momokun.raquogen;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String url = "http://api.forismatic.com/api/1.0/?";
    String quoteText,quoteAuthor,senderName,senderLink,quoteLink;
    TextView quoteTextView, quoteAuthorView, quoteLinkView;
    Button getNewQuote;
    public static String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quoteTextView = (TextView) findViewById(R.id.quoteText);
        quoteAuthorView = (TextView) findViewById(R.id.quoteAuthor);
        quoteLinkView = (TextView) findViewById(R.id.quoteLink);
        getNewQuote = (Button) findViewById(R.id.button);

        new sendPost().execute(Integer.toString(77777));
        getNewQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                int  n = rand.nextInt(50000) + 10000;
                new sendPost().execute(Integer.toString(n));
            }
        });


    }

    private class sendPost extends AsyncTask<Object, Object, String> {


        @Override
        protected String doInBackground(Object... strings) {

            String append = url + "method=getQuote&key="+strings[0]+"&format=json&lang=en";
            Log.v("TAG","URL: "+append);
            try {
                URL urlTarget = new URL(append);
                final HttpURLConnection conn = (HttpURLConnection) urlTarget.openConnection();
                conn.setRequestMethod("POST");

                byte[] buffer = append.getBytes();
                OutputStream output = conn.getOutputStream();
                output.write(buffer);

                int HttpResult = conn.getResponseCode();
                if(HttpResult == HttpURLConnection.HTTP_OK){
                    Log.v("TAG","Status OK");
                    BufferedReader br = new BufferedReader((new InputStreamReader(conn.getInputStream(), "UTF-8")));
                    String receivedLine = null;
                    result="";
                    while((receivedLine = br.readLine()) != null){
                        result += receivedLine + "\n";
                    }
                    br.close();

                }else{
                    result = conn.getResponseMessage();
                }


                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(result);
                    quoteText = jsonObj.getString("quoteText");
                    quoteAuthor = jsonObj.getString("quoteAuthor");
                    quoteLink = jsonObj.getString("quoteLink");
                    if(quoteAuthor==null){
                        quoteAuthor="-Anonymous";
                    }else{
                        quoteAuthor="-"+quoteAuthor;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                Log.v("TAG","Result: "+result);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            quoteTextView.setText(quoteText);
            quoteAuthorView.setText(quoteAuthor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                quoteLinkView.setText(Html.fromHtml("<a href="+quoteLink+"> Forismatic",Html.FROM_HTML_MODE_LEGACY));
            }else{
                quoteLinkView.setText(Html.fromHtml("<a href="+quoteLink+"> Forismatic"));
            }
            quoteLinkView.setMovementMethod(LinkMovementMethod.getInstance());
        }




    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

}
