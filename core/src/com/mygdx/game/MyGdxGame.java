package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


import java.util.Iterator;


public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture weatherImg;
    OrthographicCamera camera;
    Texture bucketImg;
    Rectangle bucket;
    Music mp3;
    Vector3 touchPosition;
    Array<Rectangle> raindrops;
    long lastDropTime;
    private void spawnRain(){
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0,800-64);
        raindrop.y = 500;
        raindrop.height = 64;
        raindrop.width = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();

    }


    @Override
    public void create() {
        batch = new SpriteBatch();
        touchPosition = new Vector3();
        mp3 = Gdx.audio.newMusic(Gdx.files.internal("mp3.mp3"));
        mp3.setLooping(true);
        mp3.play();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        weatherImg = new Texture("droplet.png");
        bucketImg = new Texture("bucket.png");
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;
        raindrops = new Array<Rectangle>();
        spawnRain();


    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bucketImg, bucket.x, bucket.y);
        for (Rectangle raindrop: raindrops){
            batch.draw(weatherImg,raindrop.x,raindrop.y);
        }
        batch.end();
        if (Gdx.input.isTouched()) {
            touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPosition);
            bucket.x = (int) (touchPosition.x - 64 / 2);
        }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                bucket.x -= 300 * Gdx.graphics.getDeltaTime();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                bucket.x += 300 * Gdx.graphics.getDeltaTime();
            }
            if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRain();
        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()){
            Rectangle raindrop = iter.next();
            raindrop.y -= 3;
            if (raindrop.y + 64 < 0) iter.remove();
            if (raindrop.overlaps(bucket)){
                iter.remove();
            }
        }

            }


        @Override
        public void dispose () {
            super.dispose();
            weatherImg.dispose();
            bucketImg.dispose();
            batch.dispose();
        }
    }

