package com.nagrahari.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    int correctAnswer,incorrectAnswer;

    ArrayList<String> celURl = new ArrayList<String>();
    ArrayList<String> celName =  new ArrayList<String>();

    ImageView iV;
    Button button1,button2,button3,button4;

    String result=null;
    Bitmap celImage;

    String[] options=new String[4];


    public void onTapped(View view)
    {
        String id= (String)view.getTag();
        System.out.println(id);

        System.out.println(correctAnswer);

        if(correctAnswer==Integer.parseInt(id))
        {

            Toast.makeText(MainActivity.this,"Correct",Toast.LENGTH_SHORT).show();
            makeLayout();
        }
        else
        {
            Toast.makeText(MainActivity.this,"Incorrect",Toast.LENGTH_SHORT).show();
            makeLayout();
        }

    }

    public class DownloadImage extends  AsyncTask<String,Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url= null;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return  myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //downloading http content and storing in result
    public  class DownloadContent extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... urls) {

            String result="";
            HttpURLConnection urlConnection;

            try {
                URL url= new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inReader= new InputStreamReader(in);

                int data=inReader.read();

                while (data!=-1){
                    char current=(char)data;
                    result=result+current;
                    data=inReader.read();
                }
                return  result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
            StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }




        DownloadContent data =new DownloadContent();

        iV = (ImageView)findViewById(R.id.imageView);

        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        button4=(Button)findViewById(R.id.button4);

        try {
            /*result=data.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult=result.split("div class=\"sidebarContainer\">");
            //System.out.println(splitResult[0]);

            Pattern p =Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find())
            {
                System.out.println(m.group(1));
                celURl.add(m.group(1));
            }

            p =Pattern.compile("alt=\"(.*?)\"");
            Matcher k=p.matcher(splitResult[0]);

            while(k.find())
            {
                System.out.println(k.group(1));
                celName.add(k.group(1));
            }
             */
            result=data.execute("https://svenskainfluencers.nu/kandisar/").get();

            String[] splitResu=result.split("<p class=\"has-text-align-center\">");

            String[] splitResult=splitResu[1].split("</div><!-- .entry-content -->");

            //finding the images url
            Pattern p =Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find())
            {
                System.out.println(m.group(1));
                celURl.add(m.group(1));
            }


            Pattern p1 =Pattern.compile("<figcaption class=\"blocks-gallery-item__caption\">(.*?)</figcaption>");
            Matcher m1 = p1.matcher(splitResult[0]);

            //finding the images name
            while(m1.find())
            {
                System.out.println(m1.group(1));
                celName.add(m1.group(1));
            }


            System.out.println(celName);
            System.out.println(celURl);
            makeLayout();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void makeLayout()
    {
        DownloadImage image = new DownloadImage();

        Random random=new Random();
        int index=random.nextInt(celURl.size());
        try {
            celImage = image.execute(celURl.get(index)).get();
            iV.setImageBitmap(celImage);

            Random random2=new Random();
            correctAnswer=random2.nextInt(4);

            //generating random number for option tab
            for(int i=0;i<4;i++)
            {
                if(i==correctAnswer)
                {

                    options[i] = celName.get(index);
                }
                else
                {
                    Random random3 =new Random();
                    incorrectAnswer=random3.nextInt(celURl.size());

                    while(incorrectAnswer==index)
                    {

                        incorrectAnswer=random3.nextInt(celURl.size());
                    }

                    options[i]=celName.get(incorrectAnswer);
                }}
                button1.setText(options[0]);
                button2.setText(options[1]);
                button3.setText(options[2]);
                button4.setText(options[3]);
        }
            catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

}}
