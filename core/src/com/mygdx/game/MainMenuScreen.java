package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen implements Screen {

    final Drop game;
    SpriteBatch batch;
    OrthographicCamera camera;
    Texture startButtonTexture;
    Texture exitButtonTexture;
    Texture ric;
    Texture backGroundTexture;
    Sprite startButtonSprite;
  //  Sprite exitButtonSprite;
    Sprite backGroundSprite;
    private static float BUTTON_RESIZE_FACTOR = 800f; // задаём относительный размер
    private static float START_VERT_POSITION_FACTOR = 2.7f; // задаём позицию конпки start
    private static float EXIT_VERT_POSITION_FACTOR = 4.2f; // задаём позицию кнопки exit



    public MainMenuScreen(final Drop gam) {
        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        // получаем размеры экрана устройства пользователя и записываем их в переменнные высоты и ширины
        float height= Gdx.graphics.getHeight();
        float width = Gdx.graphics.getWidth();
        // устанавливаем переменные высоты и ширины в качестве области просмотра нашей игры
        camera = new OrthographicCamera(width,height);
        // этим методом мы центруем камеру на половину высоты и половину ширины
        ric = new Texture("ric.png");
        camera.setToOrtho(false);// временный вектор для "захвата" входных координат
        batch = new SpriteBatch();
        // инициализируем текстуры и спрайты
        backGroundTexture = new Texture(Gdx.files.internal("menubackground.jpg"));
        backGroundSprite = new Sprite(backGroundTexture);
        startButtonTexture = new Texture(Gdx.files.internal("start_button.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("exit_button.png"));
        startButtonSprite = new Sprite(startButtonTexture);
       // exitButtonSprite = new Sprite(exitButtonTexture);
        // устанавливаем размер и позиции
        startButtonSprite.setSize(startButtonSprite.getWidth() *(width/BUTTON_RESIZE_FACTOR), startButtonSprite.getHeight()*(width/BUTTON_RESIZE_FACTOR));
        //exitButtonSprite.setSize(exitButtonSprite.getWidth() *(width/BUTTON_RESIZE_FACTOR), exitButtonSprite.getHeight()*(width/BUTTON_RESIZE_FACTOR));
        backGroundSprite.setSize(5,5);
        startButtonSprite.setPosition(1 , 1);
        //exitButtonSprite.setPosition((width/2f -exitButtonSprite.getWidth()/2) , width/EXIT_VERT_POSITION_FACTOR);
        // устанавливаем прозрачность заднего фон
        backGroundSprite.setAlpha(0.1f);

    }

    @Override
    public void show() {

    }
    void handleTouch(){
        Vector3 temp = new Vector3();
        // Проверяем были ли касание по экрану?
        if(Gdx.input.justTouched()) {
            // Получаем координаты касания и устанавливаем эти значения в временный вектор
            temp.set(Gdx.input.getX(),Gdx.input.getY(), 0);
            // получаем координаты касания относительно области просмотра нашей камеры
            camera.unproject(temp);
            float touchX = temp.x;
            float touchY= temp.y;
            // обработка касания по кнопке Stare
            if((touchX>=startButtonSprite.getX()) && touchX<= (startButtonSprite.getX()+startButtonSprite.getWidth()) && (touchY>=startButtonSprite.getY()) && touchY<=(startButtonSprite.getY()+startButtonSprite.getHeight()) ){
                game.setScreen(new GameScreen(game)); // Переход к экрану игры
            }
        }
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(backGroundSprite,1,1);
        game.batch.draw(ric,200,0);
        game.font.draw(game.batch, "Welcome", 100, 150);
        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        game.batch.draw(startButtonSprite, 1, 1);
        handleTouch();
        game.batch.end();

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

    }
}