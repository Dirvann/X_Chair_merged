package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Value;


import java.util.ArrayList;





import static com.badlogic.gdx.scenes.scene2d.actions.Actions.hide;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.show;


public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	private SpriteBatch batch;
	private Sprite sprite;
	private Texture img;
	private World world;
    private Body body;

    private Sprite s_circle1;
    private Sprite s_circle_small;

	private Sprite s_circle2;
	private Sprite s_circle_small2;

    private Texture t_circle;

	private Texture tSquareGrey;
	private Texture tSquareRed;
	private Texture tBackground;
	private Texture tLogo;
	private Texture tSteeringWheel;
	private Texture tArrowButton;
	private Texture tSetting;

	private TextureRegion trSteeringwheel;
	private TextureRegion trArrowButton;

	private Sprite sSquareLeft;
	private Sprite sSquareRight;

	public static int YLeft;   //Y position of the left stick
	public static int YRight;
	private int BindLeft;   //-1 if no pointer bindend, else value of pointer ID
	private int BindRight;
	public static int Setting;
	public static String Battery;

	public static String ValueLeft;
	public static String ValueRight;
	public static String ValueSetting;

	private int screenSidePadding; //the distance between the upper part and lower part of the screen
									//to set as max and min moving value of the controllers

	public static boolean sendData; //To check if the handler is allowed to send data in activitymain
	public static boolean sendSetting;





    private boolean showController1;
	private boolean showController2;
	private int downX1;
	private int downY1;
	private int downX2;
	private int downY2;
	private int radius = 150;

	private BitmapFont font;
	private BitmapFont fTitle;

	private int window; //1 for tilt right, 2 for tilt left
						//Left is tilt control
						//Right is slider control

	private boolean bAccelerate = false;  //checks if the acceleration button is pressed
	private boolean bReverse = false;     //check if the reverse button is pressed




	//TODO: add variable of changing controller
	public static int example_var = 100;

	public static boolean touched = false;
	@Override
	public void create () {

		t_circle = new Texture("Circle_Red.png");
		s_circle1 = new Sprite(t_circle);
		s_circle2 = new Sprite(t_circle);

        s_circle_small = new Sprite(t_circle);
		s_circle_small2 = new Sprite(t_circle);
        showController1 = false;
		showController2 = false;

		tSquareGrey = new Texture("square_grey.png");
		tSquareRed = new Texture("square_red.png");
		tBackground = new Texture("background2.jpg");
		tLogo = new Texture("logo.png");
		tSteeringWheel = new Texture("steeringwheel.png");
		tArrowButton = new Texture("arrow_button-0.png");

		String s;
		switch(Setting){
			case 0:
				s = "1.png";
				break;
			case 1:
				s = "2.png";
				break;
			case 2:
				s = "3.png";
				break;
			default:
				Setting = 0;  //TODO: change setting on message of the car, or request
				s = "1.png";
		}

		tSetting = new Texture(s);

		Battery = "0";


		trSteeringwheel = new TextureRegion(tSteeringWheel);
		trArrowButton = new TextureRegion(tArrowButton);

		sSquareLeft = new Sprite(tSquareRed);
		sSquareRight = new Sprite(tSquareRed);

		YLeft = Gdx.graphics.getHeight()/2;
		YRight = Gdx.graphics.getHeight()/2;

		BindLeft = -1;
		BindRight = -1;

		sendData = false;  //checked in the mainactivity if it is allowed to send the power data
		sendSetting = false;



		font = new BitmapFont();
		font.setColor(Color.BLUE);
		font.getData().scale(5);

		screenSidePadding = 50;

		fTitle = new BitmapFont();
		fTitle.setColor(Color.BLACK);
		fTitle.getData().scale(7);


		//Window defines which control window is shown
		//It is now initializing the window to show on open
		if(Gdx.input.getRotation() == 270){
			window = 1;
		} else if (Gdx.input.getRotation() == 90){
			window = 2;
		}

		averageAccY(Gdx.input.getAccelerometerY());



		///////////////////////////////////
		batch = new SpriteBatch();
		// We will use the default LibGdx logo for this example, but we need a
		//sprite since it's going to move
		img = new Texture("badlogic.jpg");
		sprite = new Sprite(img);

		// Center the sprite in the top/middle of the screen
		sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
				Gdx.graphics.getHeight() / 2);

		// Create a physics world, the heart of the simulation.  The Vector
		//passed in is gravity
		world = new World(new Vector2(0, -98f), true);

		// Now create a BodyDefinition.  This defines the physics objects type
		//and position in the simulation
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		// We are going to use 1 to 1 dimensions.  Meaning 1 in physics engine
		//is 1 pixel
		// Set our body to the same position as our sprite
		bodyDef.position.set(sprite.getX(), sprite.getY());


		body = world.createBody(bodyDef);

		// Now define the dimensions of the physics shape
		PolygonShape shape = new PolygonShape();
		// We are a box, so this makes sense, no?
		// Basically set the physics polygon to a box with the same dimensions
		//as our sprite
		shape.setAsBox(sprite.getWidth()/2, sprite.getHeight()/2);

		// FixtureDef is a confusing expression for physical properties
		// Basically this is where you, in addition to defining the shape of the
		//body
		// you also define it's properties like density, restitution and others
		//we will see shortly
		// If you are wondering, density and area are used to calculate over all
		//mass
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;

		Fixture fixture = body.createFixture(fixtureDef);

		shape.dispose();
		Gdx.input.setInputProcessor(this);



	}


	@Override
	public void render () {
		// Advance the world, by the amount of time that has elapsed since the
		//last frame
		// Generally in a real game, dont do this in the render loop, as you are
		//tying the physics
		// update rate to the frame rate, and vice versa
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);

		// Now update the spritee position accordingly to it's now updated
		//Physics body
		//sprite.setPosition(body.getPosition().x, body.getPosition().y);

		// You know the rest...
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		//ValueLeft = Math.round((float)(((float)((float)(screenHeight-(float)(screenSidePadding*2))/screenHeight)*YLeft))/screenHeight*128);
		//ValueRight = Math.round((Gdx.graphics.getHeight()-screenSidePadding)/Gdx.graphics.getHeight()
		///		*YRight+screenSidePadding);
		float AccY = averageAccY();    //the value of the Y accelerometer
		int maxAccY = 7;
		if(Gdx.input.getRotation() == 270){
			window = 1;
			ValueLeft = Integer.toHexString(Math.round((YLeft*127)/screenHeight));
			ValueRight = Integer.toHexString(Math.round((YRight*127)/screenHeight));
		} else if (Gdx.input.getRotation() == 90){
			window = 2;
			if (AccY > maxAccY) AccY = maxAccY;      // Limiting the max rotation of the device
			if (AccY < -maxAccY) AccY = -maxAccY;




			if(bAccelerate) {

                /* BAS IS GREAT
                if(acceleration == 0) {
                    left_motor = right_motor = 255;
                }
                if(acceleration > 0) {
                    left_motor = 255;
                    right_motor = 127.5 + 127.5*cos(acceleration/max_acceleration*PI);
                }
                if(acceleration < 0) {
                    left_motor = 127.5 + 127.5*cos(acceleration/max_acceleration*PI);
                    right_motor = 255;
                }
                */
                int l = 0;
                int r = 0;

                if(AccY >= 0){
                    l = 127;
                    r = (int)(63 + 63*Math.cos(AccY/maxAccY*Math.PI));
                }
                if(AccY < 0){
                    r = 127;
                    l = ((int)(63 + 63*Math.cos(AccY/maxAccY*Math.PI)));
                }
                //System.out.println("ValueLeft: " + l + " ValueRight: " + r);

                ValueLeft = Integer.toHexString(l);
                ValueRight = Integer.toHexString(r);



                //V1
				/*ValueLeft = Integer.toHexString(Math.round(
						((float)Math.atan((averageAccY()+maxAccY/2))/(float)1.57)*128+128));//TODO: (optional) rescale(bigger) the x of the atan function for smoother change
				ValueRight = Integer.toHexString(Math.round(
						((float)Math.atan((-averageAccY()+maxAccY/2))/(float)1.57)*128+128));*/


				/*System.out.println("ValueLeft: " +
						Math.round(((float)Math.atan((averageAccY()+maxAccY/2))/(float)1.57)*128+128) + " ValueRight: " + Math.round(
						((float)Math.atan((-averageAccY()+maxAccY/2))/(float)1.57)*128+128));
				//System.out.print("  atan: " + Math.atan(averageAccY()));*/

			} else if(bReverse){

                int l = 0;
                int r = 0;

                if(AccY >= 0){
                    l = -127;
                    r = (int)-(63 + 63*Math.cos(AccY/maxAccY*Math.PI));
                }
                if(AccY < 0){
                    r = -127;
                    l = (int)-(63 + 63*Math.cos(AccY/maxAccY*Math.PI));
                }
                //System.out.println("ValueLeft: " + l + " ValueRight: " + r);

                ValueLeft = Integer.toHexString(l);
                ValueRight = Integer.toHexString(r);

                //V1
				/*ValueLeft = Integer.toHexString(Math.round(256-
						(((float)Math.atan((averageAccY()+maxAccY/2))/(float)1.57)*128+128)));
				ValueRight = Integer.toHexString(Math.round(256-
						(((float)Math.atan((-averageAccY()+maxAccY/2))/(float)1.57)*128+128)));*/
			} else {
				ValueLeft = Integer.toHexString(63);
				ValueRight = Integer.toHexString(63);
			}
		}


		/*System.out.println(" accZ: " + Math.round(Gdx.input.getAccelerometerZ()) + " AccX: " +
				Math.round(Gdx.input.getAccelerometerX()) +
				" AccY: " + Math.round(Gdx.input.getAccelerometerY()));*/

		batch.begin();
		//batch.draw(sprite, sprite.getX(), sprite.getY());
        /*if(showController1) {
			int radiusOffset = 2;
			double radiusFraction = 0.75;
			batch.draw(s_circle1.getTexture(), s_circle1.getX()-radius/2*radiusOffset,
					s_circle1.getY()-radius/2*radiusOffset, radius*radiusOffset, radius*radiusOffset);
            batch.draw(s_circle_small.getTexture(), Math.round(s_circle_small.getX()-radius/2*3/4),
					Math.round(s_circle_small.getY()-radius/2*3/4), Math.round(radius*3/4), Math.round(radius*3/4));
        }
        if(showController2) {
			hide();
			int radiusOffset = 2;
			double radiusFraction = 0.7;
			batch.draw(s_circle2.getTexture(), s_circle2.getX()-radius/2*radiusOffset,
					s_circle2.getY()-radius/2*radiusOffset, radius*radiusOffset, radius*radiusOffset);
			batch.draw(s_circle_small2.getTexture(), Math.round(s_circle_small2.getX()-radius/2*3/4),
					Math.round(s_circle_small2.getY()-radius/2*3/4), Math.round(radius*3/4), Math.round(radius*3/4));
		}*/

        //Update the value of the motor power


		batch.draw(tBackground, 0, 0, screenWidth, screenHeight);

		//Slider controls
		if(window == 1) {
			batch.draw(tSquareGrey,
					Gdx.graphics.getWidth() * 10 / 100,    //Xpos
					Gdx.graphics.getHeight() * 4 / 100,    //Ypos

					Gdx.graphics.getWidth() * 15 / 100,    //width
					Gdx.graphics.getHeight() * 92 / 100);  //Height
			batch.draw(tSquareGrey,
					Gdx.graphics.getWidth() * 90 / 100 - Gdx.graphics.getWidth() * 15 / 100,
					Gdx.graphics.getHeight() * 4 / 100,

					Gdx.graphics.getWidth() * 15 / 100,
					Gdx.graphics.getHeight() * 92 / 100);

			batch.draw(sSquareLeft.getTexture(),
					Gdx.graphics.getWidth() * 75 / 1000,
					YLeft - Gdx.graphics.getWidth() * 18 / 100 / 2,

					Gdx.graphics.getWidth() * 20 / 100,
					Gdx.graphics.getWidth() * 18 / 100);

			batch.draw(sSquareRight.getTexture(),
					Gdx.graphics.getWidth() * 92 / 100 - Gdx.graphics.getWidth() * 193 / 1000,
					YRight - Gdx.graphics.getWidth() * 18 / 100 / 2,   //the Y pos minus the half of the height of the image

					Gdx.graphics.getWidth() * 20 / 100,
					Gdx.graphics.getWidth() * 18 / 100);
			font.draw(batch, ValueRight, Gdx.graphics.getWidth() / 2 + 10, Gdx.graphics.getHeight() / 2);
			font.draw(batch, ValueLeft, Gdx.graphics.getWidth() / 2 - 200, Gdx.graphics.getHeight() / 2);
		} else if(window == 2){
			if(Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)){


				batch.draw(trSteeringwheel,
						screenWidth/2-screenHeight*70/100/2, -screenHeight*70/100/4,     //position
						screenHeight*70/100/2, screenHeight*70/100/2, 	//origin
						screenHeight*70/100, screenHeight*70/100, 		//width height
						1, 1, 											//scale
						-averageAccY(Gdx.input.getAccelerometerY())/maxAccY*90);			//rotation

				batch.draw(trArrowButton,
						screenWidth*87/100-screenHeight*30/100/2, screenHeight*10/100,
						screenHeight*30/100/2,screenHeight*30/100/2,
						screenHeight*30/100, screenHeight*30/100,
						1, 1,
						90);
				batch.draw(trArrowButton,
						screenWidth*12/100-screenHeight*30/100/2, screenHeight*10/100,
						screenHeight*30/100/2,screenHeight*30/100/2,
						screenHeight*30/100, screenHeight*30/100,
						1, 1,
						-90);

			}else{
				//TODO: Display, you don't own an accelerometer

			}
			batch.draw(tSetting, screenWidth*80/100-screenHeight*20/100, screenHeight*70/100-screenHeight*20/100,
					screenHeight*20/100, screenHeight*20/100);
			font.draw(batch, Battery, Gdx.graphics.getWidth()*20/100, Gdx.graphics.getHeight() / 2);
		}
		//Title
		fTitle.draw(batch, "Controller", screenWidth/2-screenWidth*15/100+screenHeight*30/100/2, screenHeight*85/100);
		batch.draw(tLogo, screenWidth/2-screenHeight*30/100/2-screenWidth*15/100,
				screenHeight*80/100-screenHeight*30/100/2, screenHeight*30/100, screenHeight*30/100);

		batch.end();


	}

	private ArrayList<Float> accList = new ArrayList<Float>();
	private int smoothingFactor = 10;
	private float averageAccY(float AccData){

		accList.add(AccData);
		if(accList.size() > smoothingFactor){
			accList.remove(0);
		}
		float sum = 0;
			for(float acc : accList){
				sum += acc;
			}
			return sum/accList.size();
	}
	private float averageAccY(){
		float sum = 0;
		for(float acc : accList){
			sum += acc;
		}
		return sum/accList.size();
	}

	private void ChangeSetting(){
		if(Setting == 2){
			Setting = 0;
		}else {
			Setting += 1;
		}

		switch(Setting){
			case 0:
				tSetting = new Texture("1.png");
				break;
			case 1:
				tSetting = new Texture("2.png");
				break;
			case 2:
				tSetting = new Texture("3.png");
				break;
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		font.dispose();
		tSquareGrey.dispose();
		tSquareRed.dispose();
		tBackground.dispose();
	}

	public static void exit() {
		Gdx.app.exit();
	}

	public static void chide() {
		hide();
	}

	public static void cshow(){
		show();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		/*if(pointer == 0) {
			showController1 = true;
			s_circle1.setPosition(screenX, Gdx.graphics.getHeight() - screenY);
			downX1 = screenX;
			downY1 = Gdx.graphics.getHeight() - screenY;
		}else if(pointer == 1) {
			showController2 = true;
			s_circle2.setPosition(screenX, Gdx.graphics.getHeight() - screenY);
			downX2 = screenX;
			downY2 = Gdx.graphics.getHeight() - screenY;
		}*/
		if(window == 1) {
			if (screenX <= Gdx.graphics.getWidth() / 2 && BindLeft == -1) {
				BindLeft = pointer;
				YLeft = Gdx.graphics.getHeight() - screenY;
			} else if (screenX > Gdx.graphics.getHeight() / 2 && BindRight == -1) {
				BindRight = pointer;
				YRight = Gdx.graphics.getHeight() - screenY;
			}

			sendData = true;
		} else if(window == 2){
			if(pointer == 0) {
				if (Gdx.graphics.getHeight() - screenY < Gdx.graphics.getHeight() / 3 && screenX > Gdx.graphics.getWidth() * 2 / 3) {
					bAccelerate = true;
					sendData = true;
				} else if(Gdx.graphics.getHeight() - screenY < Gdx.graphics.getHeight() / 3 && screenX < Gdx.graphics.getWidth()/ 3){
					bReverse = true;
					sendData = true;
				}
				if(Gdx.graphics.getHeight()-screenY > Gdx.graphics.getHeight()*50/100 && screenX > Gdx.graphics.getWidth()*60/100){
					ChangeSetting();
					sendSetting = true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		/*if(pointer == 0) {
			showController1 = false;
		}else if(pointer == 1) {
			showController2 = false;
		}*/
		if(window == 1) {
			if (pointer == BindLeft) {
				BindLeft = -1;
				YLeft = Gdx.graphics.getHeight() / 2;
			} else if (pointer == BindRight) {
				BindRight = -1;
				YRight = Gdx.graphics.getHeight() / 2;
			}

			if (BindLeft == -1 && BindRight == -1) {
				sendData = false;
			}
			ValueLeft = "0";
			ValueRight = "0";
		}
		if(window == 2){
			if(pointer == 0){
				bAccelerate = false;
				bReverse = false;
				ValueLeft = "80";
				ValueRight = "80";
				sendData = false;
			}
		}


		return false;

	}

	public boolean touchDragged(int screenX, int screenY, int pointer){
		/*sprite.setPosition(screenX, Gdx.graphics.getHeight()-screenY); // for old project only
		if(pointer == 0){
			example_var = screenX;
		}

		if(pointer == 0) {
			if (Math.sqrt(Math.pow(screenX - downX1, 2) + Math.pow(Gdx.graphics.getHeight() - screenY - downY1, 2))
					< radius) {
				s_circle_small.setPosition(screenX, Gdx.graphics.getHeight() - screenY);
			} else {
				float xpos = Math.round(downX1 + ((screenX - downX1) * radius) / Math.sqrt(Math.pow(screenX - downX1, 2)
						+ Math.pow(Gdx.graphics.getHeight() - screenY - downY1, 2)));
				float ypos = Math.round(downY1 + ((Gdx.graphics.getHeight() - screenY - downY1) * radius)
						/ Math.sqrt(Math.pow(screenX - downX1, 2)
						+ Math.pow(Gdx.graphics.getHeight() - screenY - downY1, 2)));

				s_circle_small.setPosition(xpos, ypos);
			}
		} else if(pointer == 1){
			if (Math.sqrt(Math.pow(screenX - downX2, 2) + Math.pow(Gdx.graphics.getHeight() - screenY - downY2, 2))
					< radius) {
				s_circle_small2.setPosition(screenX, Gdx.graphics.getHeight() - screenY);
			} else {
				float xpos = Math.round(downX2 + ((screenX - downX2) * radius) / Math.sqrt(Math.pow(screenX - downX2, 2)
						+ Math.pow(Gdx.graphics.getHeight() - screenY - downY2, 2)));
				float ypos = Math.round(downY2 + ((Gdx.graphics.getHeight() - screenY - downY2) * radius)
						/ Math.sqrt(Math.pow(screenX - downX2, 2)
						+ Math.pow(Gdx.graphics.getHeight() - screenY - downY2, 2)));

				s_circle_small2.setPosition(xpos, ypos);
			}
		}*/
		if(window == 1) {
			if (pointer == BindLeft) {
				YLeft = Gdx.graphics.getHeight() - screenY; //-screenY because the screen coordinates of the touch
			} else if (pointer == BindRight) {              //start from the upper left corner, and the game
				YRight = Gdx.graphics.getHeight() - screenY;//coordinates form the lower left corner
			}
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void resize(int width, int height) {  //exits the application when there is rotation to portrait
		super.resize(width, height);
		if(Gdx.input.getRotation() == 0 || Gdx.input.getRotation() == 180){
			exit();
		} else if(Gdx.input.getRotation() == 270){
			window = 1;
		} else if (Gdx.input.getRotation() == 90){
			window = 2;
		}
	}
}


//www.gamefromscratch.com/post/2014/08/27/LibGDX-Tutorial-13-Physics-with-Box2D-Part-1-A-Basic-Physics-Simulations.aspx
