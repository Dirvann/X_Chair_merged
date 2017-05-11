package team_310.x_chair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import java.util.logging.LogRecord;

import com.mygdx.game.AndroidLauncher;
import com.mygdx.game.MyGdxGame;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;


public class mainactivity extends AppCompatActivity {
    //checks if the app is sending motion messages (button pressed)
    String sendingMotion = "0";
    ////////SECRET SETTINGS////////////
    private int egg = 0;
    private int imState = 1;
    RelativeLayout rl;
    static TextView speedDisplay;
    List sequence;
    static MediaPlayer mp;

    //context of application
    static Activity act = null;

    //Used for the function to send controller data
    public static boolean landscapeMode = false;

    static final WebSocket webSocket = new WebSocket();   //Creating a new webSocket

    //Handler used to loop the controller info sending
    public static Handler mHandler;

    //test
    int b = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        act = this;

        rl = (RelativeLayout) findViewById(R.id.relativelayout);
///////////////////////////////////////////////////////////////////////
///////////////packet sending configuration////////////////////////////
//////////////////////////////////////////////////////////////////////
        //Initialize WebSocket

        webSocket.host = "3.1.0.1";    //Host to connect to
        webSocket.port = 81;
        webSocket.connected = false; //variable that checks if app is connected to websocket
        webSocket.connectWebSocket();   //connecting the websocket to arduino


        /////SECRET SETTINGS//////////
        //mainactivity.context = getApplicationContext();
        speedDisplay = (TextView) findViewById(R.id.ViewSpeed);
        mp = MediaPlayer.create(this, R.raw.jinglebells);
        sequence = new ArrayList();
        sequence.add("r");
        sequence.add("b");
        sequence.add("g");
        sequence.add("y");
        sequence.add("rain");
        sequence.add("y");
        sequence.add("r");
        /////////////////////////////



///////////////////TEMP REPEATER/////////////
        final Repeater repeater = new Repeater();
        repeater.sendingInterval = 90;

/////////////////HANDLER////////////////////////////////
        //for the controller position update
        mHandler = new Handler();
        mHandler.post(controlHandler);     //This starts the controller update handler below





/////////////////////////////////////////////////////////////////////
////////////////////Color configuration//////////////////////////////
/////////////////////////////////////////////////////////////////////
        final String pressColor = "#F7E0FA"; //color of background of buttons
        final String releaseColor = "#ECBE06";

/////////////////////////////////////////////////////////////////////
/////////////////Controller configuration////////////////////////////
//////////////////////////////////////////////////////////////////////
        /*//0 for TV, 1 for DVD
        commands p = new commands();
        p.controller = 1;*/
        //TODO: add reconnection function
/////////////////////////////////////////////////////////////////////
///////////////////MOTION BUTTONS////////////////////////////////////
////////////////////////////////////////////////////////////////////
/////------------------------FORWARD-------------------------------//////
        final Button UpButton = (Button) findViewById(R.id.Up);

        UpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "forward";
                    System.out.println("Button FORWARD down");
                    /*UpButton.setBackgroundColor(Color.parseColor(pressColor));
                    UpButton.setShadowLayer(20, 20, 20, Color.BLACK);*/
                    webSocket.sendMessage(commands.WS_FORWARD);    //uncomment for normal

                    //repeater.running = true;
                    //repeater.repeat(webSocket, commands.FORWARD);
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("forward")) {
                    System.out.println("Button FORWARD up");
                    webSocket.sendMessage(commands.BRAKE);
                    sendingMotion = "0";
                    /*UpButton.setTextColor(Color.BLACK);
                    UpButton.setBackgroundColor(Color.parseColor(releaseColor));*/

                    //repeater.running = false;

                    //webSocket.running = false;    //useless?
                    return true;
                }

                return false;
            }
        });
//////------------------------DOWN-----------------------------//////////

        final Button DownButton = (Button) findViewById(R.id.Down);

        DownButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "down";
                    System.out.println("Button BACKWARD down");
                    webSocket.sendMessage(commands.WS_BACKWARD);   //uncomment for normal
                    DownButton.setTextColor(Color.BLACK);
                    /*DownButton.setBackgroundColor(Color.parseColor(pressColor));
                    DownButton.setShadowLayer(20, 20, 20, Color.BLACK);*/

                    //repeater.running = true;                            //uncomment for UDP command mode
                    //repeater.repeat(webSocket, commands.BACKWARD);
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("down")) {
                    sendingMotion = "0";
                    System.out.println("Button FORWARD up");
                    webSocket.sendMessage(commands.BRAKE);
                    DownButton.setTextColor(Color.BLACK);
                    /*DownButton.setBackgroundColor(Color.parseColor(releaseColor));
                    DownButton.setShadowLayer(0, 0, 0, Color.BLACK);*/

                    //repeater.running = false;   //uncomment for UDP command mode
                    return true;
                }

                return false;
            }
        });
////////----------------------LEFT-------------------------------//////////
        final Button LeftButton = (Button) findViewById(R.id.Left);

        LeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "left";
                    System.out.println("Button LEFT down");
                    webSocket.sendMessage(commands.WS_LEFT);
                    /*LeftButton.setTextColor(Color.BLACK);
                    LeftButton.setBackgroundColor(Color.parseColor(pressColor));*/

                    //repeater.running = true;
                    //repeater.repeat(webSocket, commands.LEFT);
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("left")) {
                    sendingMotion = "0";
                    System.out.println("Button LEFT up");
                    webSocket.sendMessage(commands.BRAKE);
                    /*LeftButton.setTextColor(Color.BLACK);
                    LeftButton.setBackgroundColor(Color.parseColor(releaseColor));*/

                    //repeater.running = false;
                    return true;
                }

                return false;
            }
        });
////////------------------------RIGHT---------------------------///////////
        final Button RightButton = (Button) findViewById(R.id.Right);

        RightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "right";
                    System.out.println("Button RIGHT down");
                    webSocket.sendMessage(commands.WS_RIGHT); //uncomment for normal
                    /*RightButton.setTextColor(Color.BLACK);
                    RightButton.setBackgroundColor(Color.parseColor(pressColor));*/

                    //repeater.running = true;
                    //repeater.repeat(webSocket, commands.RIGHT);

                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("right")) {
                    sendingMotion = "0";
                    System.out.println("Button RIGHT up");
                    webSocket.sendMessage(commands.BRAKE);
                    /*RightButton.setTextColor(Color.BLACK);
                    RightButton.setBackgroundColor(Color.parseColor(releaseColor));*/

                    //repeater.running = false;
                    return true;
                }
                return false;
            }
        });
////////////-----------SPEED UP------------------------/////////////////////////
        final Button SuButton = (Button) findViewById(R.id.speedup);

        SuButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "su";
                    webSocket.sendMessage(commands.SPEEDUP);
                    /*SuButton.setTextColor(Color.BLACK);
                    SuButton.setBackgroundColor(Color.parseColor(pressColor));*/
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("su")) {
                    sendingMotion = "0";
                    /*SuButton.setTextColor(Color.BLACK);
                    SuButton.setBackgroundColor(Color.parseColor(releaseColor));*/
                    return true;
                }
                return false;
            }
        });
////////////////--------------SPEED DOWN--------------------////////////////////////////
        final Button SoButton = (Button) findViewById(R.id.speeddown);

        SoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "so";
                    webSocket.sendMessage(commands.SPEEDDOWN);
                    /*SoButton.setTextColor(Color.BLACK);
                    SoButton.setBackgroundColor(Color.parseColor(pressColor));*/
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("so")) {
                    sendingMotion = "0";
                    /*SoButton.setTextColor(Color.BLACK);
                    SoButton.setBackgroundColor(Color.parseColor(releaseColor));*/
                    return true;
                }
                return false;
            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////COLOR BUTTON SECTION//////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////
/////////////-----------------RED--------------------------------------------////////////////////
        final Button redButton = (Button) findViewById(R.id.red);

        redButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "red";
                    webSocket.sendMessage(commands.LIGHTS_RED);

                    backgroundChanger("r");
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("red")) {
                    sendingMotion = "0";
                    return true;
                }
                return false;
            }
        });
//////////////-------------------GREEN------------------------------//////////////////////////
        final Button greenButton = (Button) findViewById(R.id.green);

        greenButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "green";
                    webSocket.sendMessage(commands.LIGHTS_GREEN); // sending green data to address defined at beginning
                    backgroundChanger("g");
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("green")) {
                    sendingMotion = "0";
                    return true;
                }
                return false;
            }
        });
//////////////////---------------------YELLOW----------------------------///////////////////
        final Button yellowButton = (Button) findViewById(R.id.yellow);

        yellowButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "yellow";
                    webSocket.sendMessage(commands.LIGHTS_YELLOW); // sending yellow to address defined at beginning
                    backgroundChanger("y");
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("yellow")) {
                    sendingMotion = "0";
                    return true;
                }
                return false;
            }
        });
/////////////////-------------BLUE------------------------------------//////////////
        final Button blueButton = (Button) findViewById(R.id.blue);

        blueButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "blue";
                    webSocket.sendMessage(commands.LIGHTS_BLUE); // sending blue data to address defined at beginning
                    backgroundChanger("b");
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("blue")) {
                    sendingMotion = "0";
                    return true;
                }
                return false;
            }
        });
//////////////-----------------------RAINBOW--------------------------///////////////////
        final Button rainbowButton = (Button) findViewById(R.id.rainbow);

        rainbowButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "rainbow";
                    webSocket.sendMessage(commands.LIGHTS_RAINBOW); // sending rainbow data to address defined at beginning
                    backgroundChanger("rain");
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("rainbow")) {
                    sendingMotion = "0";
                    return true;
                }
                return false;
            }
        });
//////////////////------------LIGHTS ON---------------------------------////////////////////
        final Button lightsOnButton = (Button) findViewById(R.id.lights_on);

        lightsOnButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "on";
                    webSocket.sendMessage(commands.LIGHTS_ON); // sending lights on data to address defined at beginning

                    //sendMessage("0x61");
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("on")) {
                    sendingMotion = "0";
                    return true;
                }
                return false;
            }
        });
///////////---------------Lights off-----------------------------//////////////////////
        final Button lightsOffButton = (Button) findViewById(R.id.lights_off);

        lightsOffButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "off";
                    webSocket.sendMessage(commands.LIGHTS_OFF); // sending lights off to address defined at beginning
                    return true;

                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("off")) {
                    sendingMotion = "0";
                    return true;
                }
                return false;
            }
        });
///////////////////------------------LIGHTS AUTO----------------------------------////////////////
        final Button lightsAutoButton = (Button) findViewById(R.id.lights_auto);

        lightsAutoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    sendingMotion = "lights_auto";
                    webSocket.sendMessage(commands.LIGHTS_AUTO); // sending lights auto to address defined at beginning*/
                    return true;


                } else if (action == MotionEvent.ACTION_UP && sendingMotion.equals("lights_auto")) {
                    sendingMotion = "0";
                    return true;
                }
                return false;
            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////AUTO DRIVING SECTION////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
        final Switch autoSwitch = (Switch) findViewById(R.id.switch1);

        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println(isChecked);
                if (isChecked) {
                    webSocket.sendMessage(commands.CHG_STATION);
                } else {
                    webSocket.sendMessage(commands.MANUAL);
                }
            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////VALUE DATA INPUTS(ex:speed)/////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////

///////-------------------------------View Speed-------------------------------------------////////
        speedDisplay = (TextView) findViewById(R.id.ViewSpeed);
        speedDisplay.setText("0.0");
        //speedDisplay.setText(String.valueOf(i));


        /*final Thread t = new Thread(){
            public void run(){
                try {
                    for (int i = 0; i < 1000; i++) {
                        speedDisplay.setText(String.valueOf(i));
                        Thread.sleep(100);

                    }
                }catch(InterruptedException e){
                    System.err.println(e);
                }

            }
        };
        t.start();*/
///////////////------------------Connect----------------------------------////////////////
        final Button connectButton = (Button) findViewById(R.id.connect);

        connectButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN && sendingMotion.equals("0")) {
                    webSocket.connectWebSocket();
                    //connectWebSocket();
                    return true;
                }
                return false;
            }
        });


//////////-------------------------CHANGE ACTIVITY BUTTON---------------------------//////////
        final Button activityButton = (Button) findViewById(R.id.button2);

        activityButton.setOnTouchListener(new View.OnTouchListener(){


            @Override
            public boolean onTouch(View view, MotionEvent event){
                int action = event.getAction();
                startActivity(new Intent(act, AndroidLauncher.class));
                if(action == MotionEvent.ACTION_DOWN){
                    //finish();
                    //startActivity(new Intent(getApplicationContext(), controller.class));
                    Transcoder.transcode(String.valueOf(b));
                    b++;
                }

                return false;
            }
        });



    }
////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////OUTSIDE ONCREATE////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////EASTER EGG///////////////////////////////////
    /*public void displaySpeed(String data) {
        final TextView speedDisplay = (TextView) findViewById(R.id.ViewSpeed);
        speedDisplay.setText("123454");
    }*/
    void backgroundChanger(String code) {
        Log.i("egg", "number " + egg+", code: " + code);
        if (sequence.get(egg) == code) {
            egg += 1;
            //displayText(String.valueOf(egg));
            Log.i("check", "code == egg "+ egg);

            if (egg == 7) {
                if (imState == 1) {
                    rl.setBackgroundResource(R.drawable.background2);
                    imState = 2;
                    egg = 0;
                    //displayText(String.valueOf(egg));
                    mp.start();

                } else{
                    rl.setBackgroundResource(R.drawable.background);
                    imState = 1;
                    egg = 0;
                    //displayText(String.valueOf(egg));
                    mp.pause();
                    mp.seekTo(0);
                }

            }
        }else {
            egg = 0;
            //displayText(String.valueOf(egg));
        }
    }
    public static void displayText(String message){
        speedDisplay.setText(message);
    }

//////////////////////////////////////////////////////////////////////////////
///////////////////////////Screen Orientation Handler////////////////////////
///////////////////////////////////////////////////////////////////////////
    //source: https://www.youtube.com/watch?v=Y5J6BURMrHQ

    /*On every configuration change of the app(like orientation) where base variables are changed
        this function is called.
        It is currently called when the orientation of the screen has changed*/

    public void onConfigurationChanged(Configuration newConfig){ // the newConfig variable contains all the configuration information
        super.onConfigurationChanged(newConfig);                    //of the android wrapper like the screen resolution and rotation
        Log.i("config", "configuration changed!!");
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            landscapeMode = true;

            startActivity(new Intent(this.getApplicationContext(), AndroidLauncher.class));
            Log.i("orientation", "landscape");
            //TODO: create checker for control behavior sending

        } else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            landscapeMode = false;
        }
        System.out.println("orientation: " + newConfig.orientation);
    }

    int i = 0;
    boolean toBreak = false;
    private Runnable controlHandler = new Runnable() {   //this runnable is called because it is a process the has to
        public void run() {                              //continuously update in background
            //Log.i("Service", "Looping"+i);
            i++;
            if (MyGdxGame.sendData) {
                toBreak = true;
                //Log.i("Speed", "Left: " + String.valueOf(MyGdxGame.YLeft) + " Right: " + String.valueOf(MyGdxGame.YRight));
                //mainactivity.webSocket.sendMessage(String.valueOf(MyGdxGame.example_var));
                mainactivity.webSocket.sendMessage(commands.BB_LEFT_WHEEL_POWER, true); // the true parameter is to bypass the toast notification
                mainactivity.webSocket.sendMessage("0x" + MyGdxGame.ValueLeft, true);   // if not connected //TEMP
                mainactivity.webSocket.sendMessage(commands.BB_RIGHT_WHEEL_POWER, true);
                mainactivity.webSocket.sendMessage("0x" + MyGdxGame.ValueRight, true);//TODO: add conditions to send for every parameter
                System.out.println("");
                //TODO: add stop command after stopped sending
            }
            if(MyGdxGame.sendSetting){
                mainactivity.webSocket.sendMessage(commands.BB_SPEED_SETTING, true);
                mainactivity.webSocket.sendMessage("0x" + MyGdxGame.Setting, true);
                MyGdxGame.sendSetting = false;
            }
            if(toBreak){
                mainactivity.webSocket.sendMessage(commands.BRAKE, true);
                toBreak = false;
            }

            mHandler.postDelayed(this, 60);   //this recalls the handler, the second parameter is the delay in milliseconds
        }
    };

}
