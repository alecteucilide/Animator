package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen {

    final int FRAME_COLS = 9, FRAME_ROWS = 4;
    final NightWalk game;
    OrthographicCamera camera;
    Animation<TextureRegion> frontWalkAnimation;
    Animation<TextureRegion> backWalkAnimation;
    Animation<TextureRegion> rightWalkAnimation;
    Animation<TextureRegion> leftWalkAnimation;
    TextureRegion currentFrame;
    Texture walkSheet;
    Texture arrowUpImage;
    Texture arrowDownImage;
    Texture arrowRightImage;
    Texture arrowLeftImage;
    Texture jumpImage;
    Texture stopImage;
    Texture backgroundImage;
    Sprite spriteStandingStill;
    Sprite spriteJumping;
    //posiciones del personaje
    float posX = 100;
    float posY = 100;
    float width = 60;
    float height = 60;
    //Varaiacion del tamaño del personaje en funcion de su movimiento en el eje y
    float factorScale = 0.2f;
    boolean walking = false;
    boolean jumping = false;
    boolean goingUp = false;
    boolean goingDown = false;
    //acceleracion
    float acc = 0f;
    //moficador de la accelaracion ver jumpingCycle
    float accModulator = 0.25f;
    //posiciones limites para mover el presonaje
    float maxX, maxY, minX, minY;
    // A variable for tracking elapsed time for the animation
    float stateTime;
    Vector3 touchPoint;
    //Activadores de moviemiento cuando el jugador hace click en las imagenes respesctivas
    boolean arrowUpActivated = false;
    boolean arrowDownActivated = false;
    boolean arrowRightActivated = false;
    boolean arrowLeftActivated = false;
    boolean jumpActivated = false;

    public GameScreen(final NightWalk game) {
        this.game = game;
        game.setGameScreen(this);
        touchPoint=new Vector3();
        maxX = 800;
        maxY = 150;
        minX = -10;
        minY = 10;

        walkSheet = new Texture(Gdx.files.internal("man_walking2.png"));
        backgroundImage = new Texture(Gdx.files.internal("background1.png"));
        arrowUpImage = new Texture(Gdx.files.internal("arrow_up.png"));
        arrowDownImage = new Texture(Gdx.files.internal("arrow_down.png"));
        arrowRightImage = new Texture(Gdx.files.internal("arrow_right.png"));
        arrowLeftImage = new Texture(Gdx.files.internal("arrow_left.png"));
        jumpImage = new Texture(Gdx.files.internal("jump.png"));
        stopImage = new Texture(Gdx.files.internal("stop.png"));


        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
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
        //personjae parado
        spriteStandingStill = new Sprite(walkFrames[18]);
        spriteStandingStill.setPosition(posX, posY);
        //perosnaje saltando
        spriteJumping = new Sprite(walkFrames[13]);

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

        //right walk
        TextureRegion[] rightWalk = new TextureRegion[FRAME_COLS];
        index = 0;
        for (int i = 0; i < FRAME_COLS; i++) {
            rightWalk[index++] = tmp[3][i];
        }

        //Creacion de la animaciones
        //walkAnimation = new Animation<TextureRegion>(0.050f, walkFrames);
        frontWalkAnimation = new Animation<TextureRegion>(0.050f, frontWalk);
        backWalkAnimation = new Animation<TextureRegion>(0.050f, backWalk);
        rightWalkAnimation = new Animation<TextureRegion>(0.050f, rightWalk);
        leftWalkAnimation = new Animation<TextureRegion>(0.050f, leftWalk);

        // Instantiate a SpriteBatch for drawing and reset the elapsed animation
        // time to 0
        stateTime = 0f;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

        //lanzamos las intrucciones adecuadas de movimiento en funcion de donde hace click el jugador en la pantalla
        if(Gdx.input.isTouched() && jumping == false){
            camera.unproject(touchPoint.set(Gdx.input.getX(),Gdx.input.getY(),0));

            if(touchPoint.x > 50 && touchPoint.x < 100 && touchPoint.y > 100 && touchPoint.y < 150){//flecha arriba
                arrowUpActivated = true;
                arrowDownActivated = false;
                arrowRightActivated = false;
                arrowLeftActivated = false;
            }else if(touchPoint.x > 50 && touchPoint.x < 100 && touchPoint.y > 0 && touchPoint.y < 50){//flecha abajo
                arrowUpActivated = false;
                arrowDownActivated = true;
                arrowRightActivated = false;
                arrowLeftActivated = false;
            }else if(touchPoint.x > 100 && touchPoint.x < 150 && touchPoint.y > 50 && touchPoint.y < 150){//flecha derecha
                arrowUpActivated = false;
                arrowDownActivated = false;
                arrowRightActivated = true;
                arrowLeftActivated = false;
            }else if(touchPoint.x > 0 && touchPoint.x < 50 && touchPoint.y > 50 && touchPoint.y < 100){//flecha izquierda
                arrowUpActivated = false;
                arrowDownActivated = false;
                arrowRightActivated = false;
                arrowLeftActivated = true;
            }else if(touchPoint.x > 0 && touchPoint.x < 50 && touchPoint.y > 100 && touchPoint.y < 150){//boton saltar
                arrowUpActivated = false;
                arrowDownActivated = false;
                arrowRightActivated = false;
                arrowLeftActivated = false;
                jumpActivated = true;
            }else if(touchPoint.x > 50 && touchPoint.x < 100 && touchPoint.y > 50 && touchPoint.y < 100){//boton parar
                arrowUpActivated = false;
                arrowDownActivated = false;
                arrowRightActivated = false;
                arrowLeftActivated = false;
            }
        }

        if(jumping == false){
            if(Gdx.input.isKeyPressed(Input.Keys.UP) || arrowUpActivated == true){
                walking = true;
                if(posY < maxY){
                    spriteStandingStill.translateY(1f);
                    posY++;
                    width = width - factorScale;
                    height = height - factorScale;
                }
                currentFrame = frontWalkAnimation.getKeyFrame(stateTime, true);
            }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || arrowDownActivated == true){
                walking = true;
                if(posY > minY){
                    spriteStandingStill.translateY(-1f);
                    posY--;
                    width = width + factorScale;
                    height = height + factorScale;
                }
                currentFrame = backWalkAnimation.getKeyFrame(stateTime, true);
            }else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || arrowRightActivated == true){
                walking = true;
                if(posX < maxX){
                    spriteStandingStill.translateX(1f);
                    posX++;
                }
                currentFrame = rightWalkAnimation.getKeyFrame(stateTime, true);
            }else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || arrowLeftActivated == true){
                walking = true;
                if(posX > minX){
                    spriteStandingStill.translateX(-1f);
                    posX--;
                }
                currentFrame = leftWalkAnimation.getKeyFrame(stateTime, true);
            }else if(Gdx.input.isKeyPressed(Input.Keys.J) || jumpActivated == true){
                jumpActivated = false;
                jumping = true;
            }else{
                walking = false;
            }
        }else {
            jumpingCycle();
        }

        game.batch.begin();
        game.batch.draw(backgroundImage, 0 , 0, 800, 480);
        game.batch.draw(arrowUpImage, 50, 100, 50, 50);
        game.batch.draw(arrowDownImage, 50, 0, 50, 50);
        game.batch.draw(arrowRightImage, 100, 50, 50, 50);
        game.batch.draw(arrowLeftImage, 0, 50, 50, 50);
        game.batch.draw(stopImage, 50, 50, 50, 50);
        game.batch.draw(jumpImage, 0, 100, 50, 50);
        if(jumping == false){
            if(walking){
                game.batch.draw(currentFrame, posX, posY, width, height);
            }else{
                game.batch.draw(spriteStandingStill, posX, posY, width, height);
            }
        }else {
            game.batch.draw(spriteJumping, posX, posY, width, height);
        }
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    /*Emulamos un salto con la funcion 1/x siendo el resultado de esta la acceleracion del personaje en el aire (forma de la courba 1/x
    da una sensacion de parar en el aire ya que la acceleracion se reduce y aproxima de cero al aumentar x, tomamos
    de forma arbitraria 8 como maximo de x para tener esa ilusion , 1/8 esta cerca de 0). En el codigo x es accModulator.
    Al caer la evolucion de la acceleracion es inversa que al subir -> se recorre la courba de 1/x en el sentido contrario*/
    public void jumpingCycle(){

        if(accModulator <= 8 && goingUp == false && goingDown == false){
            goingUp = true;
        }else if(accModulator >= 8 && goingUp){
            goingUp = false;
            goingDown = true;
        }else if(accModulator < 0.5f && goingDown){
            goingDown = false;
            accModulator = 0.5f;
            jumping = false;
        }

        if(goingUp){
            acc = 1 / accModulator;
            accModulator = accModulator + 1f;
            posY = posY + (10*acc);
        }else if(goingDown){
            acc = 1 / accModulator;
            accModulator = accModulator - 1f;
            posY = posY - (10*acc);
        }
    }

    @Override
    public void dispose() {
        walkSheet.dispose();
        backgroundImage.dispose();
    }
}
