package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animator implements ApplicationListener {
	// Constant rows and columns of the sprite shee
	private static final int FRAME_COLS = 9, FRAME_ROWS = 4;

	OrthographicCamera camera;
	Animation<TextureRegion> walkAnimation;
	Animation<TextureRegion> frontWalkAnimation;
	Animation<TextureRegion> backWalkAnimation;
	Animation<TextureRegion> rightWalkAnimation;
	Animation<TextureRegion> leftWalkAnimation;
	TextureRegion currentFrame;
	TextureRegion lastFrame; //not used yet
	Texture walkSheet;
	SpriteBatch spriteBatch;
	Sprite sprite;
	//posiciones del personaje
	float posX = 100;
	float posY = 100;
	boolean walking = false;
	Texture backgroundImage;
	float scaleFactor = 1; //not used yet
	//posiciones limites para mover el presonaje
	float maxX, maxY, minX, minY;
	// A variable for tracking elapsed time for the animation
	float stateTime;

	@Override
	public void create() {
		/*camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);*/
		maxX = 600;
		maxY = 150;
		minX = -10;
		minY = 0;
		// Load the sprite sheet as a Texture
		walkSheet = new Texture(Gdx.files.internal("man_walking2.png"));
		backgroundImage = new Texture(Gdx.files.internal("background1.png"));
		// Use the split utility method to create a 2D array of TextureRegions. This is
		// possible because this sprite sheet contains frames of equal size and they are
		// all aligned.
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS,
				walkSheet.getHeight() / FRAME_ROWS);

		// Place the regions into a 1D array in the correct order, starting from the top
		// left, going across first. The Animation constructor requires a 1D array.
		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];

		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}

		//posicion inicial del personaje
		sprite = new Sprite(walkFrames[18]);
		sprite.setPosition(posX, posY);

		//front walk
		TextureRegion[] frontWalk = new TextureRegion[FRAME_COLS];
		index = 0;
		for (int i = 0; i < FRAME_COLS; i++) {
			frontWalk[index++] = tmp[0][i];
		}

		//left walk
		TextureRegion[] leftWalk = new TextureRegion[FRAME_COLS];
		index = 0;
		for (int i = 0; i < FRAME_COLS; i++) {
			leftWalk[index++] = tmp[1][i];
		}

		//back walk
		TextureRegion[] backWalk = new TextureRegion[FRAME_COLS];
		index = 0;
		for (int i = 0; i < FRAME_COLS; i++) {
			backWalk[index++] = tmp[2][i];
		}

		//left walk
		TextureRegion[] rightWalk = new TextureRegion[FRAME_COLS];
		index = 0;
		for (int i = 0; i < FRAME_COLS; i++) {
			rightWalk[index++] = tmp[3][i];
		}

		// Initialize the Animation with the frame interval and array of frames
		walkAnimation = new Animation<TextureRegion>(0.050f, walkFrames);
		frontWalkAnimation = new Animation<TextureRegion>(0.050f, frontWalk);
		backWalkAnimation = new Animation<TextureRegion>(0.050f, backWalk);
		rightWalkAnimation = new Animation<TextureRegion>(0.050f, rightWalk);
		leftWalkAnimation = new Animation<TextureRegion>(0.050f, leftWalk);

		// Instantiate a SpriteBatch for drawing and reset the elapsed animation
		// time to 0
		spriteBatch = new SpriteBatch();
		stateTime = 0f;

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
		//camera.update();
		//maxX = Gdx.graphics.getWidth()/2;
		//move control
		if(Gdx.input.isKeyPressed(Input.Keys.UP)){
			walking = true;
			if(posY < maxY){
				sprite.translateY(1f);
				posY++;
			}
			currentFrame = frontWalkAnimation.getKeyFrame(stateTime, true);
		}else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
			walking = true;
			if(posY > minY){
				sprite.translateY(-1f);
			/*scaleFactor = scaleFactor - 0.1f;
			sprite.scale(scaleFactor);*/
				posY--;
			}
			currentFrame = backWalkAnimation.getKeyFrame(stateTime, true);
		}else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			walking = true;
			if(posX < maxX){
				sprite.translateX(1f);
				posX++;
			}
			currentFrame = rightWalkAnimation.getKeyFrame(stateTime, true);
		}else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			walking = true;
			if(posX > minX){
				sprite.translateX(-1f);
				posX--;
			}
			currentFrame = leftWalkAnimation.getKeyFrame(stateTime, true);
		}else{
			walking = false;
		}

		spriteBatch.begin();
		spriteBatch.draw(backgroundImage, 0 , 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if(walking){
			spriteBatch.draw(currentFrame, posX, posY); // Draw current frame at (50, 50)
		}else{
			sprite.draw(spriteBatch);
		}
		spriteBatch.end();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() { // SpriteBatches and Textures must always be disposed
		spriteBatch.dispose();
		walkSheet.dispose();
	}
}
