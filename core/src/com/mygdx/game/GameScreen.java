package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


import java.util.Iterator;


public class GameScreen implements Screen {

    final Drop game;
    Music mp3;
    OrthographicCamera camera;
    Texture dropImage;
    Texture bucketImage;
    Rectangle bucket;
    Texture background1;
    Texture nubes;
    Vector3 touchPos;
    Array<Rectangle> raindrops;
    Array<Rectangle> nubes1;
    long lastDropTime;
    boolean gameOver = false;
    Texture over;
    int proebano;
    int dropsGatchered;



    public GameScreen (final Drop gam) {
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        touchPos = new Vector3();
            bucketImage = new Texture("bucket.png");
        dropImage = new Texture("droplet.png");
        nubes = new Texture("nubes.png");
        background1 = new Texture("background1.png");
        over = new Texture("over.png");




        mp3 = Gdx.audio.newMusic(Gdx.files.internal("mp3.mp3"));
        mp3.setLooping(true);
        mp3.setVolume(0.3f);
        mp3.play();
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        nubes1 = new Array<Rectangle>();
        raindrops = new Array<Rectangle>();
        spawnRaindrop();
        spawnnubes();

    }

    private void spawnRaindrop(){
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800-64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }
    private void spawnnubes(){
        Rectangle nubes2 = new Rectangle();
        nubes2.x = 10;
        nubes2.y = 480;
        nubes2.width = 64;
        nubes2.height = 64;
        nubes1.add(nubes2);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(background1,1,1);
        if (gameOver == false) {
            game.batch.draw(bucketImage, bucket.x, bucket.y);
        }else if (gameOver == true){
            game.batch.draw(over,130,20);
            bucket.x = -500;
            game.font.draw(game.batch, "Pess f", 400, 100);
        }
        for (Rectangle raindrop: raindrops){
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        for (Rectangle nubes2: nubes1){
            game.batch.draw(nubes,nubes2.x,nubes2.y);
        }
        game.font.draw(game.batch, "Drops Collected: " + dropsGatchered, 0, 480);
        game.font.draw(game.batch, "Proebano: " + proebano + "/5", 0, 465);

        game.batch.end();

        if(Gdx.input.isTouched()){
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = (int) (touchPos.x -64 / 2);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) bucket.x -= 400 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.D)) bucket.x += 400 * Gdx.graphics.getDeltaTime();
        if (bucket.x == 0){
            bucket.x = 1;
        }
        if (bucket.x < 0) bucket.x = 0;
        if (bucket.x > 800 - 64) bucket.x = 800 - 64;

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnnubes();

        Iterator<Rectangle> iter = raindrops.iterator();
        Iterator<Rectangle> iter1 = nubes1.iterator();
        while (iter.hasNext()){
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0){
                proebano++;
                iter.remove();
            }
            if (raindrop.overlaps(bucket)){
                dropsGatchered++;
                iter.remove();
            }
        }
        while (iter1.hasNext()){
            Rectangle nubes2 = iter1.next();
            nubes2.x += 20 * Gdx.graphics.getDeltaTime();
            if (nubes2.y + 64 < 0){
                proebano++;
                iter1.remove();
            }
            if (nubes2.overlaps(bucket)){
                dropsGatchered++;
                iter1.remove();
            }
        }
        if (proebano == 5){
            gameOver = true;
        }
        if (proebano > 5){
            proebano --;
        }
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
    }

    @Override
    public void show() {
    }
}