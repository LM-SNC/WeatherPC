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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class GameScreen implements Screen {
    final Drop game;
    Music mp3;
    Sprite exitButtonSprite;
    OrthographicCamera camera;
    Texture dropImage;
    Texture bucketImage;
    Rectangle bucket;
    Texture pause;
    File score;
    Texture bg_pause;
    Sprite pauseSprite;
    Vector2 nubesx;
    Texture background1;
    int Hscore;
    PrintWriter scorewrite;
    boolean gamepause;
    BufferedReader buff;
    Vector3 touchPos;
    Texture button1;
    Texture button2;
    Texture button3;
    Sprite button1S;
    Sprite button2S;
    Sprite button3S;
    Array<Float> speedsForNubes;
    Array<Rectangle> raindrops;
    Array<Rectangle> nubesdrops;
    Texture exitButtonTexture;
    long lastDropTime;

    boolean gameOver = false;
    Texture over;
    int sp;
    int proebano;
    Stage stage;
    float volume = 1f;
    int dropsGatchered;
    Texture nubesImg;

    public GameScreen(final Drop gam) {
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        touchPos = new Vector3();
        bucketImage = new Texture("bucket.png");
        dropImage = new Texture("droplet.png");
        exitButtonTexture = new Texture("exit_button2.png");
        exitButtonSprite = new Sprite(exitButtonTexture);
        pause = new Texture(Gdx.files.internal("pause.png"));
        pauseSprite = new Sprite(pause);
        nubesImg = new Texture("nubes.png");
        background1 = new Texture("background1.png");
        exitButtonSprite.setPosition(704, 430);
        bg_pause = new Texture(Gdx.files.internal("bg_pause.png"));
        button1 = new Texture("resume.png");
        button1S = new Sprite(button1);
        pauseSprite.setPosition(-10, 200);
//        button1S.setPosition(button1.getWidth() + 50, button1.getHeight());
        over = new Texture("over.png");
        nubesx = new Vector2();

        try {
            score = new File("Score.txt");
            if (!score.exists()) {
                score.createNewFile();
            }
        } catch (IOException error) {
            System.out.println("Error:" + error);
        }

        mp3 = Gdx.audio.newMusic(Gdx.files.internal("mp3.mp3"));
        mp3.setLooping(true);
        mp3.play();
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 0;
        bucket.height = 32;

        speedsForNubes = new Array<>();
        raindrops = new Array<Rectangle>();
        spawnRaindrop();
        nubesdrops = new Array<Rectangle>();
        spawnNubes();

        try {
            score();
            scoreWrite();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage = new Stage();
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        final Slider slider = new Slider(0f, 0.9f, 20, false, skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Slider slider = (Slider) actor;
                slider.getValue();
                volume = slider.getValue();

            }
        });
        Container<Slider> container = new Container<Slider>(slider);
        container.setTransform(true);   // for enabling scaling and rotation
        container.size(100, 60);
        container.setOrigin(container.getWidth() / 2, container.getHeight() / 2);
        container.setPosition(200, 400);
        container.setScale(1);  //scale according to your requirement

        stage.addActor(container);
    }

    private void spawnRaindrop() {
        if (!gamepause) {
            Rectangle raindrop = new Rectangle();
            raindrop.x = MathUtils.random(0, 800 - 64);
            raindrop.y = 480;
            raindrop.width = 64;
            raindrop.height = 64;
            raindrops.add(raindrop);
            lastDropTime = TimeUtils.nanoTime();
        }
    }

    private void spawnNubes() {
        Rectangle nubesdrop = new Rectangle();
        nubesdrop.x = MathUtils.random(0, 500);
        nubesdrop.y = 350;
        sp++;
        speedsForNubes.add(MathUtils.random(0.8f, 1.2f));
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
                dispose();
            }
            if ((touchX >= pauseSprite.getX()) && touchX <= (pauseSprite.getX() + pauseSprite.getWidth()) && (touchY >= pauseSprite.getY()) && touchY <= (pauseSprite.getY() + pauseSprite.getHeight())) {
                gamepause = true;

            }
            if ((touchX >= button1S.getX()) && touchX <= (button1S.getX() + button1S.getWidth()) && (touchY >= button1S.getY()) && touchY <= (button1S.getY() + button1S.getHeight())) {
                gamepause = false;

            }
        }
    }

    public void inputHandler(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gamepause = !gamepause;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!gamepause) {
            for (int k = 0; k < nubesdrops.size; k++) {
                Rectangle nubesdrop = nubesdrops.get(k);
                float speed = speedsForNubes.get(k);
                nubesdrop.x += speed;
                if (nubesdrop.x > 500 || nubesdrop.x < 0) {
                    speedsForNubes.set(k, -speed);
                }
            }
        }

        mp3.setVolume(volume);
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        MathUtils.random(0, 450);
        game.batch.begin();
        game.batch.draw(background1, 1, 1);

        if (gameOver == false) {
            game.batch.draw(bucketImage, bucket.x, bucket.y);
        } else if (gameOver == true) {
            try {
                score();
                scoreWrite();
            } catch (IOException e) {
                e.printStackTrace();
            }
            game.batch.draw(over, 130, 20);
            game.font.draw(game.batch, "Pess f", 400, 100);
        }

        for (Rectangle raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        for (Rectangle Nubesdrop : nubesdrops) {
            game.batch.draw(nubesImg, Nubesdrop.x, Nubesdrop.y);
        }

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000 && !gamepause) {
            spawnRaindrop();
        }
        if (sp < MathUtils.random(1, 6)) {
            spawnNubes();
        }
        if (dropsGatchered > Hscore) {
            Hscore = dropsGatchered;
        }

        game.batch.draw(exitButtonSprite, 704, 430);
        if (!gamepause) {
            game.batch.draw(pauseSprite, 10, 200);
        }

        game.font.draw(game.batch, "Drops Collected: " + dropsGatchered, 0, 480);
        game.font.draw(game.batch, "Drops Collected: " + volume, 0, 400);
        game.font.draw(game.batch, "Proebano: " + proebano + "/5", 0, 465);
        game.font.draw(game.batch, "High score: " + Hscore, 0, 445);
        //   if(gamepause){
        //     game.batch.draw(bg_pause,200,120);
        // }

        if (gamepause) {
            stage.act();
            stage.draw();
            game.batch.draw(button1, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, button1.getWidth(), button1.getHeight());
        }
        game.batch.end();

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = (int) (touchPos.x - 64 / 2);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) && !gamepause)
            bucket.x -= 400 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.D) && !gamepause)
            bucket.x += 400 * Gdx.graphics.getDeltaTime();
        if (bucket.x == 0) {
            bucket.x = 1;
        }
        if (bucket.x < 0) {
            bucket.x = 0;
        }
        if (bucket.x > 800 - 64) {
            bucket.x = 800 - 64;
        }

        Iterator<Rectangle> iter = raindrops.iterator();
        if (!gamepause) {
            while (iter.hasNext()) {
                Rectangle raindrop = iter.next();
                raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
                if (raindrop.y + 64 < 0) {
                    proebano++;
                    iter.remove();
                }
                if (raindrop.overlaps(bucket)) {
                    if (gameOver == false) {
                        dropsGatchered++;
                    }
                    iter.remove();
                }
            }
        }
        if (proebano == 5) {
            gameOver = true;
        }
        if (proebano > 5) {
            proebano--;
        }
        handleTouch();
        if (Gdx.input.isKeyPressed(Input.Keys.F) && gameOver == true) {
            restart();
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen::resize()", "width:" + width + " height:" + height);
    }

    public void restart() {
        Gdx.app.log("GameScreen::restart()", "--");
        gameOver = false;
        raindrops.clear();
        proebano = 0;
        dropsGatchered = 0;
    }

    public void score() throws IOException {
        Gdx.app.log("GameScreen::score()", "--");
        String line;
        buff = new BufferedReader(new FileReader("score.txt"));
        while ((line = buff.readLine()) != null) {
            Hscore = Integer.parseInt(line);
        }
        buff.close();
    }

    public void scoreWrite() throws FileNotFoundException {
        Gdx.app.log("GameScreen::scoreWrite()", "--");
        if (dropsGatchered > Hscore) {
            Hscore = dropsGatchered;
        }
        scorewrite = new PrintWriter(score);
        scorewrite.print(Hscore);
        scorewrite.close();
    }

    @Override
    public void pause() {
        Gdx.app.log("GameScreen::pause()", "--");
    }

    @Override
    public void resume() {
        Gdx.app.log("GameScreen::resume()", "--");
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen::hide()", "--");
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameScreen::dispose()", "--");
        dropImage.dispose();
        bucketImage.dispose();
        mp3.dispose();
        nubesImg.dispose();
        exitButtonTexture.dispose();
        try {
            score();
            scoreWrite();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen::show()", "--");
        Gdx.input.setInputProcessor(stage);
    }
}
