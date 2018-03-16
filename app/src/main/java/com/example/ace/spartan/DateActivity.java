package com.example.ace.spartan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateActivity extends AppCompatActivity {
String dt;
int dd,mm,yyyy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        final DatePicker datePicker=(DatePicker)findViewById(R.id.datePicker);
        final TextView date=(TextView)findViewById(R.id.DATE);
        Button conf=(Button)findViewById(R.id.OK);
        final Button done=(Button)findViewById(R.id.SetDate);
        done.setEnabled(false);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dd=datePicker.getDayOfMonth();
               mm=datePicker.getMonth()+1;

               yyyy=datePicker.getYear();
               for(int i=0;i<8;i++){
                   System.out.println("#################DATE1: "+dd+"/"+mm+"/"+yyyy);
               }
int day,mon,yr;
                SimpleDateFormat d = new SimpleDateFormat("dd");
                SimpleDateFormat m = new SimpleDateFormat("MM");
                SimpleDateFormat y = new SimpleDateFormat("yyyy");
                Date date1 = new Date();
               day=Integer.valueOf(d.format(date1));
               mon=Integer.valueOf(m.format(date1));
               yr=Integer.valueOf(y.format(date1));
                for(int i=0;i<8;i++){
                    System.out.println("#################DATE2: "+day+"/"+mon+"/"+yr);
                }
               if(dd>=day&&mm>=mon&&yyyy>=yr){
done.setEnabled(true);
dt=String.valueOf(mm)+"/"+String.valueOf(dd)+"/"+String.valueOf(yyyy);
date.setText(dt);
               }else{
                   Toast.makeText(DateActivity.this, "Invalid Date...Please Choose Another date!", Toast.LENGTH_SHORT).show();
               }
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                Bundle b=getIntent().getExtras();
                bundle.putString("MAP_TYPE",b.getString("MAP_TYPE"));
                bundle.putString("DATE",dt);
                session_details s=new session_details(DateActivity.this);
                String[] det=new String[3];
                det=s.getVal();
                for(int i=0;i<8;i++){
                    System.out.println("############################(1)"+det[0]+"  "+det[1]+"  "+det[2]);
                }
                Intent i=new Intent(DateActivity.this,MyGoogleMapActivity.class);
                i.putExtras(bundle);
                startActivity(i);
                finish();
            }
        });
    }
}
