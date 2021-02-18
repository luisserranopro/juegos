package pro.luisserrano.gotas;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;

public class Gotas extends ApplicationAdapter {
	
        private Texture imagenGota;
        private Texture imagenCubo;
        private Sound sonidoGota;
        private Music musicaFondo;
        private SpriteBatch spriteBatch;
        private OrthographicCamera camara;
        private Rectangle cubo;
        private Array<Rectangle> gotasLluvia;
        private long tiempoCaidaUltimaGota;
	
	@Override
	public void create () {

            //cargamos en memoria las imágenes del cubo y las gotas
            imagenGota = new Texture(Gdx.files.internal("droplet.png"));
            imagenCubo = new Texture(Gdx.files.internal("bucket.png"));
            
            //cargamos en memoria los sonidos y la música de fondo
            sonidoGota = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
            musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
            
            //hacemos que la música de fondo comience a sonar y se repita en bucle
            musicaFondo.setLooping(true);
            musicaFondo.play();
            
            //creamos la cámara ortográfica y el spritebatch
            camara = new OrthographicCamera();
            camara.setToOrtho(false, 800, 400);
            spriteBatch = new SpriteBatch();
            
            //creamos un objeto de la clase Rectangle para representar al cubo
            cubo = new Rectangle();
            cubo.x = 800/2 - 64/2;
            cubo.y = 20;
            cubo.width = 64;
            cubo.height = 64;
            
            //creamos un vector de gotas y creamos la primera
            gotasLluvia = new Array<Rectangle>();
            creaGotaLluvia();
	}
        
        private void creaGotaLluvia() {
            
            Rectangle gotaLluvia = new Rectangle();
            gotaLluvia.x = MathUtils.random(0, 800 - 64);
            gotaLluvia.y = 480;
            gotaLluvia.width = 64;
            gotaLluvia.height = 64;
            gotasLluvia.add(gotaLluvia);
            tiempoCaidaUltimaGota = TimeUtils.nanoTime();
        }

	@Override
	public void render () {
            //pintamos el fondo de azul oscuro
            Gdx.gl.glClearColor(0,0,0.2f,1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            
            camara.update();
            spriteBatch.setProjectionMatrix(camara.combined);
            
            //dibujamos los sprites en la pantalla
            spriteBatch.begin();
            spriteBatch.draw(imagenCubo, cubo.x, cubo.y);
            for (Rectangle gotaLluvia: gotasLluvia) {
                spriteBatch.draw(imagenGota, gotaLluvia.x, gotaLluvia.y);
            }
            spriteBatch.end();
            
            //leemos las entradas
            if (Gdx.input.isTouched()) {
                Vector3 posicionTocada = new Vector3();
                posicionTocada.set(Gdx.input.getX(),Gdx.input.getY(),0);
                camara.unproject(posicionTocada);
                cubo.x = posicionTocada.x - 64/2;
            }
            if (Gdx.input.isKeyPressed(Keys.LEFT))
                cubo.x -= 200 * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Keys.RIGHT))
                cubo.x += 200 * Gdx.graphics.getDeltaTime();
            
            //comprobamos que el cubo no se salga de la pantalla
            if (cubo.x < 0)
                cubo.x = 0;
            if (cubo.x > 800)
                cubo.x = 800 -64;
            
            //comprobamos si ha pasado un segundo desde la última gota y si es así, creamos una nueva
            if (TimeUtils.nanoTime() - tiempoCaidaUltimaGota > 1000000000)
                creaGotaLluvia();
            
            //comprobamos si alguna gota ha llegado el suelo o si ha tocado el cubo
            Iterator<Rectangle> iterator = gotasLluvia.iterator();
            while (iterator.hasNext()) {
                Rectangle gotaLluvia = iterator.next();
                gotaLluvia.y -= 200 * Gdx.graphics.getDeltaTime();
                if (gotaLluvia.y + 64 < 0)
                    iterator.remove();
                if (gotaLluvia.overlaps(cubo)) {
                    sonidoGota.play();
                    iterator.remove();
                }
            }
	}
	
	@Override
	public void dispose () {

                //liberamos los recursos
               imagenGota.dispose();
               imagenCubo.dispose();
               sonidoGota.dispose();
               musicaFondo.dispose();
               spriteBatch.dispose();
	}
}
