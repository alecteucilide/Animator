package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NightWalk extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    private WelcomeScreen welcomeScreen;
    private GameScreen gameScreen;

    public void create() {
        batch = new SpriteBatch();
        //Use LibGDX's default Arial font.
        font = new BitmapFont();
        this.setScreen(new WelcomeScreen(this));
    }

    public void render() {
        super.render(); //important!
    }

    public void setGameScreen(GameScreen gs)
    {
        this.gameScreen = gs;
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        if (gameScreen != null)
        {
            gameScreen.dispose();
        }
    }
}
