package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


import java.util.Iterator;


public class GameScreen implements Screen {

    final Drop game;
    Music mp3;
    Sprite exitButtonSprite;
    OrthographicCamera camera;
    Texture dropImage;
    Texture bucketImage;
    Rectangle nubesdrop;
    Rectangle bucket;
    Texture background1;
    Vector3 touchPos;
    Array<Rectangle> raindrops;
    Array<Rectangle> nubesdrops;
    Texture exitButtonTexture;
    long lastDropTime;
    boolean gameOver = false;
    Texture over;
    int sp;
    int proebano;
    int speedNube = 2;
    int dropsGatchered;

    Texture nubesImg;


    public GameScreen(final Drop gam) {
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        touchPos = new Vector3();
        bucketImage = new Texture("bucket.png");
        dropImage = new Texture("droplet.png");
        exitButtonTexture = new Texture(Gdx.files.internal("exit_button.png"));
        exitButtonSprite = new Sprite(exitButtonTexture);
        nubesImg = new Texture("nubes.png");
        background1 = new Texture("background1.png");
        nubesdrop = new Rectangle();
        exitButtonSprite.setPosition(-10 , 250);
        over = new Texture("over.png");


        mp3 = Gdx.audio.newMusic(Gdx.files.internal("mp3.mp3"));
        mp3.setLooping(true);
        mp3.setVolume(0.3f);
        mp3.play();
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 0;
        bucket.height = 32;

        raindrops = new Array<Rectangle>();
        spawnRaindrop();
        nubesdrops = new Array<Rectangle>();
        spawnNubes();

    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    private void spawnNubes() {
        nubesdrop.x = MathUtils.random(0, 450);
        nubesdrop.y = 350;
        sp++;
        nubesdrops.add(nubesdrop);
    }
    void handleTouch() {
        Vector3 temp = new Vector3();
        // Проверяем были ли касание по экрану?
        if (Gdx.input.justTouched()) {
            // Получаем координаты касания и устанавливаем эти значения в временный вектор
            temp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            // получаем координаты касания относительно области просмотра нашей камеры
            camera.unproject(temp);
            float touchX = temp.x;
            float touchY = temp.y;
            // обработка касания по кнопке Stare
            if ((touchX >= exitButtonSprite.getX()) && touchX <= (exitButtonSprite.getX() + exitButtonSprite.getWidth()) && (touchY >= exitButtonSprite.getY()) && touchY <= (exitButtonSprite.getY() + exitButtonSprite.getHeight())) {
                game.setScreen(new MainMenuScreen(game)); // Переход к экрану игры
                dropImage.dispose();
                bucketImage.dispose();
                mp3.dispose();
                nubesImg.dispose();
            }
        }
    }




    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
       MathUtils.random(0, 450);
        game.batch.begin();

        game.batch.draw(background1, 1, 1);

        if (gameOver == false) {
            game.batch.draw(bucketImage, bucket.x, bucket.y);
        } else if (gameOver == true) {
            game.batch.draw(over, 130, 20);
            bucket.x = -500;
            game.font.draw(game.batch, "Pess f", 400, 100);
        }


        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
        if (sp < 3) spawnNubes();

        game.batch.draw(exitButtonSprite, -10, 250);
        for (Rectangle raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        for (Rectangle nubesdrop : nubesdrops) {
            game.batch.draw(nubesImg, nubesdrop.x, nubesdrop.y);
        }
        game.font.draw(game.batch, "Drops Collected: " + dropsGatchered, 0, 480);
        game.font.draw(game.batch, "Proebano: " + proebano + "/5", 0, 465);

        game.batch.end();

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = (int) (touchPos.x - 64 / 2);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) bucket.x -= 400 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.D)) bucket.x += 400 * Gdx.graphics.getDeltaTime();
        if (bucket.x == 0) {
            bucket.x = 1;
        }
        if (bucket.x < 0) bucket.x = 0;
        if (bucket.x > 800 - 64) bucket.x = 800 - 64;


        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0) {
                proebano++;
                iter.remove();
            }
            if (raindrop.overlaps(bucket)) {
                dropsGatchered++;
                iter.remove();
            }
        }
        Iterator<Rectangle> iter1 = nubesdrops.iterator();
        while (iter1.hasNext()) {
            Rectangle nubesdrop = iter1.next();
        }
        nubesdrop.x += speedNube;
        //if(nubesdrop.x > 500 || nubesdrop.x < 0){
        //    speedNube = -speedNube;
       // }
        if (proebano == 5) {
            gameOver = true;
        }
        if (proebano > 5) {
            proebano--;
        }
        handleTouch();
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        mp3.dispose();
        nubesImg.dispose();
    }

    @Override
    public void show() {
    }
}