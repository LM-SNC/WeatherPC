package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
    File score;
    Vector2 nubesx;
    Texture background1;
    int Hscore;
    PrintWriter scorewrite;
    ImageButton pause;
    boolean gamepause;
    BufferedReader buff;
    Vector3 touchPos;
    ImageButton setting;
    ImageButton resumeButton;
    ImageButton exit_bn;
    //    Texture button2;
//    Texture button3;
//    Sprite button2S;
//    Sprite button3S;
    Array<Float> speedsForNubes;
    Array<Rectangle> raindrops;
    Array<Rectangle> nubesdrops;
    Texture exitButtonTexture;
    long lastDropTime;
    Label dropColleted;

    boolean gameOver = false;
    Texture over;
    int sp;
    int proebano;
    Stage stage;
    //    float volume = 1f;
    int dropsGatchered;
    Texture nubesImg;

    public GameScreen(final Drop gam) {
        Gdx.app.log("GameScreen::GameScreen()", "gam:" + gam);
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        touchPos = new Vector3();
        bucketImage = new Texture("bucket.png");
        dropImage = new Texture("droplet.png");
        nubesImg = new Texture("nubes.png");
        background1 = new Texture("background1.png");
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
        //stage.setDebugAll(true);
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        dropColleted = new Label("Drops Collected: " + dropsGatchered, skin);

        Table firstTable = new Table();
        firstTable.setFillParent(true);
        pause = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("pause.png"))));
        pause.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gamepause = true;
                pause.setVisible(false);
                resumeButton.setVisible(true);
                setting.setVisible(true);
                exit_bn.setVisible(true);

            }
        });
        firstTable.add(dropColleted).expand().align(Align.topLeft).row();
        firstTable.add(pause).expand().align(Align.topLeft);
        stage.addActor(firstTable);

        Table pauseTable = new Table();
        pauseTable.setFillParent(true);

        final Slider slider = new Slider(0f, 1f, 0.01f, false, skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Slider slider = (Slider) actor;
                slider.getValue();
                mp3.setVolume(slider.getValue());
            }
        });


        resumeButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("resume.png"))));
        resumeButton.setVisible(false);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gamepause = false;
                resumeButton.setVisible(false);
                setting.setVisible(false);
                exit_bn.setVisible(false);
                pause.setVisible(true);
            }
        });
        setting = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("setting.png"))));
        setting.setVisible(false);
        setting.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                boolean swith = setting.isChecked();
                resumeButton.setVisible(!swith);
                setting.setVisible(swith || !swith);
                slider.setVisible(swith);
                exit_bn.setVisible(!swith);
                pause.setVisible(false);
                System.out.print(swith);
            }
        });
        exit_bn = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("exit_bn.png"))));
        exit_bn.setVisible(false);
        slider.setVisible(false);
        exit_bn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(new MainMenuScreen(game)); // Переход к экрану игры
                dispose();
            }
        });

        pauseTable.add(slider).row();
        pauseTable.add(resumeButton).row();
        pauseTable.add(setting).padTop(20).padBottom(20).row();
        pauseTable.add(exit_bn).padBottom(2);

//        Container<Slider> container = new Container<Slider>(slider);
//        container.setTransform(true);   // for enabling scaling and rotation
//        container.size(100, 60);
//        container.setOrigin(container.getWidth() / 2, container.getHeight() / 2);
//        container.setPosition(200, 400);
//        container.setScale(1);  //scale according to your requirement

        stage.addActor(pauseTable);
    }

    private void spawnRaindrop() {
        Gdx.app.log("GameScreen::spawnRaindrop()", "--");
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
        Gdx.app.log("GameScreen::spawnNubes()", "--");
        Rectangle nubesdrop = new Rectangle();
        nubesdrop.x = MathUtils.random(0, 500);
        nubesdrop.y = 350;
        sp++;
        speedsForNubes.add(MathUtils.random(0.8f, 1.2f));
        nubesdrops.add(nubesdrop);
    }


    public void inputHandler(float delta) {
        Gdx.app.log("GameScreen::inputHandler()", "delta:" + delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gamepause = !gamepause;
        }
    }

    @Override
    public void render(float delta) {
//        Gdx.app.log("GameScreen::render()", "delta:" + delta);
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

        if (!gamepause) {
            if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
                spawnRaindrop();
            }
            if (sp < MathUtils.random(1, 6)) {
                spawnNubes();
            }

            if (dropsGatchered > Hscore) {
                Hscore = dropsGatchered;
            }
        }

        stage.act();
        stage.draw();

        dropColleted.setText("Drops Collected: " + dropsGatchered);

        game.font.draw(game.batch, "Drops Collected: " + dropsGatchered, 0, 480);
        game.font.draw(game.batch, "Proebano: " + proebano + "/5", 0, 465);
        game.font.draw(game.batch, "High score: " + Hscore, 0, 445);
        //   if(gamepause){
        //     game.batch.draw(bg_pause,200,120);
        // }


//

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
