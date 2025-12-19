package ufersa.ed1.gabriela.snakequest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import ufersa.ed1.gabriela.snakequest.algorithms.*;
import ufersa.ed1.gabriela.snakequest.structures.*;

public class SnakeGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    private Texture startScreenImage;
    private Texture gameOverImage;
    private Texture backgroundTexture;
    private Texture foodTexture;
    private Texture snakeHeadTexture;
    private Texture snakeBodyTexture;

    private Music backgroundMusic;
    private Sound eatSound;
    private Sound gameOverSound;

    private GameBoard board;
    private Snake snake;
    private FoodManager food;
    private ScoreManager scoreManager;

    private boolean startScreen = true;
    private boolean typingName = true;
    private boolean showInstructions = false;
    private boolean gameOver = false;
    private boolean muted = false;

    private String playerName = "";
    private int score = 0;

    // controle de tempo do jogo
    private final float TICK = 1f / 8f;
    private float accumulator = 0f;

    // feedback visual ao comer
    private static class FloatingText {
        String text;
        float x, y;
        long startTime;
        float duration = 800; // ms
        FloatingText(String t, float x, float y) { this.text = t; this.x = x; this.y = y; this.startTime = TimeUtils.millis(); }
        boolean isAlive() { return TimeUtils.timeSinceMillis(startTime) < duration; }
        float alpha() { return 1f - (TimeUtils.timeSinceMillis(startTime) / duration); }
    }
    private Array<FloatingText> floatingTexts = new Array<>();

    @Override
    public void create() {
        batch = new SpriteBatch();
        layout = new GlyphLayout();

        // carregar imagens opcionais (start, game over, background, food)
        // se algum asset não existir, capture exceção e deixe como null
        try { startScreenImage = new Texture("start_screen.png"); } catch (Exception e) { startScreenImage = null; }
        try { gameOverImage = new Texture("game_over.png"); } catch (Exception e) { gameOverImage = null; }
        try { backgroundTexture = new Texture("background.png"); } catch (Exception e) { backgroundTexture = null; }
        try { foodTexture = new Texture("food.png"); } catch (Exception e) { foodTexture = null; }

        // carregar sons e música (opcional)
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f);
            backgroundMusic.play();

            eatSound = Gdx.audio.newSound(Gdx.files.internal("eat.wav"));
            gameOverSound = Gdx.audio.newSound(Gdx.files.internal("gameover.wav"));
        } catch (Exception e) {
            backgroundMusic = null;
            eatSound = null;
            gameOverSound = null;
        }

        // fonte padrão
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        // inicializar lógica do jogo
        board = new GameBoard(25, 20, 24); // cols, rows, cellSize (ajuste conforme sua classe)
        scoreManager = new ScoreManager(50);
        try { scoreManager.loadScores("scores.txt"); } catch (Exception ignored) { }

        // cria texturas da cobra em tempo de execução (Pixmap) - usa board.getCellSize()
        int cell = board.getCellSize();

        // corpo: bloco verde
        Pixmap pBody = new Pixmap(cell, cell, Pixmap.Format.RGBA8888);
        pBody.setColor(0f, 0.8f, 0.2f, 1f); // cor do corpo
        pBody.fill();
        snakeBodyTexture = new Texture(pBody);
        snakeBodyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pBody.dispose();

        // cabeça: bloco mais escuro com olhos brancos
        Pixmap pHead = new Pixmap(cell, cell, Pixmap.Format.RGBA8888);
        pHead.setColor(0f, 0.6f, 0.1f, 1f); // cor da cabeça
        pHead.fill();
        pHead.setColor(1f, 1f, 1f, 1f);
        int eye = Math.max(1, cell / 6);
        pHead.fillRectangle(cell/4 - eye/2, cell - cell/3 - eye/2, eye, eye);
        pHead.fillRectangle(3*cell/4 - eye/2, cell - cell/3 - eye/2, eye, eye);
        snakeHeadTexture = new Texture(pHead);
        snakeHeadTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pHead.dispose();

        // prepara jogo (cobra e comida serão inicializados ao começar)
        snake = null;
        food = null;
    }

    private void startNewGame() {
        // cria snake com posição central do tabuleiro
        int startX = board.getCols() / 2;
        int startY = board.getRows() / 2;
        snake = new Snake(16, startX, startY); // capacidade inicial 16 (ou use sua implementação)
        food = new FoodManager(board);
        food.spawn(snake);
        score = 0;
        gameOver = false;
        accumulator = 0f;
    }

    private void endGame() {
        gameOver = true;
        if (!muted && gameOverSound != null) gameOverSound.play();
        String nameToSave = playerName.trim().isEmpty() ? "Jogador" : playerName.trim();
        scoreManager.addScore(new ScoreEntry(nameToSave, score));
        try { scoreManager.saveScores("scores.txt"); } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleInput() {
        // global toggles
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            muted = !muted;
            if (backgroundMusic != null) {
                if (muted) backgroundMusic.pause(); else backgroundMusic.play();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            showInstructions = !showInstructions;
        }

        // tela inicial: digitar nome
        if (startScreen && typingName) {
            // letras A-Z
            for (int k = Input.Keys.A; k <= Input.Keys.Z; k++) {
                if (Gdx.input.isKeyJustPressed(k)) {
                    String s = Input.Keys.toString(k);
                    if (s.length() == 1 && playerName.length() < 20) playerName += s;
                }
            }
            // números
            for (int k = Input.Keys.NUM_0; k <= Input.Keys.NUM_9; k++) {
                if (Gdx.input.isKeyJustPressed(k) && playerName.length() < 20) {
                    String s = Input.Keys.toString(k).replace("NUM_", "");
                    playerName += s;
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && playerName.length() < 20) {
                playerName += " ";
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && playerName.length() > 0) {
                playerName = playerName.substring(0, playerName.length() - 1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                typingName = false;
                startScreen = false;
                if (playerName.trim().isEmpty()) playerName = "Jogador";
                startNewGame();
            }
            return;
        }

        // durante o jogo
        if (!startScreen && !gameOver) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) snake.setDirection(Direction.UP);
            else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) snake.setDirection(Direction.DOWN);
            else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) snake.setDirection(Direction.LEFT);
            else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) snake.setDirection(Direction.RIGHT);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                // voltar ao menu inicial
                startScreen = true;
                typingName = true;
                playerName = "";
                if (!muted && backgroundMusic != null) backgroundMusic.play();
            }
        }

        // após game over
        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                startNewGame();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                startScreen = true;
                typingName = true;
                playerName = "";
            }
        }

        // se estivermos na tela inicial mas já confirmamos nome (ENTER), permitir iniciar com ENTER
        if (startScreen && !typingName && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startScreen = false;
            startNewGame();
        }
    }

    private void update(float delta) {
        if (startScreen || gameOver) return;

        accumulator += delta;
        while (accumulator >= TICK && !gameOver) {
            accumulator -= TICK;
            snake.move();
            Segment head = snake.head();

            // colisão com parede
            if (!board.isInside(head.x, head.y) || snake.collidesWithSelf()) {
                endGame();
                return;
            }

            // comer comida
            if (food.isFoodAt(head.x, head.y)) {
                snake.growOnNextMove();
                score += 10;
                if (!muted && eatSound != null) eatSound.play();
                float fx = head.x * board.getCellSize() + board.getCellSize() / 2f;
                float fy = head.y * board.getCellSize() + board.getCellSize() / 2f;
                floatingTexts.add(new FloatingText("+10", fx, fy));
                food.spawn(snake);
            }
        }

        // limpar floating texts expiradas
        for (int i = floatingTexts.size - 1; i >= 0; i--) {
            if (!floatingTexts.get(i).isAlive()) floatingTexts.removeIndex(i);
        }
    }

    @Override
    public void render() {
        handleInput();
        update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        if (startScreen) {
            // desenha imagem de start (se existir)
            if (startScreenImage != null) {
                batch.draw(startScreenImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }

            // desenha campo de nome e ranking dinâmico por cima
            font.setColor(Color.WHITE);
            drawCenteredText("Digite seu nome: " + (typingName ? (playerName.isEmpty() ? "_" : playerName) : playerName),
                Gdx.graphics.getHeight() * 0.72f);

            drawCenteredText("Pressione ENTER para confirmar e começar", Gdx.graphics.getHeight() * 0.66f);

            // Top 5
            font.setColor(Color.ORANGE);
            drawCenteredText("Top 5 Jogadores:", Gdx.graphics.getHeight() * 0.60f);
            float y = Gdx.graphics.getHeight() * 0.56f;
            int max = Math.min(5, scoreManager.getScores().size());
            for (int i = 0; i < max; i++) {
                if (i == 0) font.setColor(Color.CYAN);
                else if (i == 1) font.setColor(Color.LIME);
                else if (i == 2) font.setColor(Color.MAGENTA);
                else font.setColor(Color.ROYAL);
                drawCenteredText(scoreManager.getScores().get(i).toString(), y - i * 26f);
            }

            // instruções rápidas
            font.setColor(Color.BLUE);
            drawCenteredText("M para mutar e I para instruções", Gdx.graphics.getHeight() * 0.12f);

            // instruções detalhadas (se ativadas)
            if (showInstructions) {
                font.setColor(Color.GREEN);
                float iy = Gdx.graphics.getHeight() * 0.44f;
                drawCenteredText("Instruções:", iy);
                drawCenteredText("Setas para mover, SPACE reinicia após Game Over", iy - 28f);
                drawCenteredText("Se colidir, com o corpo ou com as bordas(limites da janela) é Game Over", iy - 56f);
                drawCenteredText("ESC volta ao menu e M para mutar/desmutar", iy - 84f);
            }

        } else {
            // desenha fundo do mapa (se existir) ou limpa área do tabuleiro
            if (backgroundTexture != null) {
                batch.draw(backgroundTexture, 0, 0, board.getCols() * board.getCellSize(), board.getRows() * board.getCellSize());
            } else {

            }

            // desenha comida (se textura disponível) ou um bloco simples
            if (foodTexture != null) {
                batch.draw(foodTexture, food.getFoodX() * board.getCellSize(), food.getFoodY() * board.getCellSize(),
                    board.getCellSize(), board.getCellSize());
            } else {

            }

            // desenha cobra usando snake.get(i) e snake.size()
            for (int i = 0; i < snake.size(); i++) {
                Segment s = snake.get(i);
                Texture tex = (i == 0) ? snakeHeadTexture : snakeBodyTexture;
                batch.draw(tex, s.x * board.getCellSize(), s.y * board.getCellSize(),
                    board.getCellSize(), board.getCellSize());
            }

            // HUD: score e nome
            font.setColor(Color.WHITE);
            font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 10);
            font.draw(batch, "Player: " + playerName, 10, Gdx.graphics.getHeight() - 40);

            // floating texts (efeito +10)
            for (FloatingText ft : floatingTexts) {
                font.setColor(1f, 1f, 0.2f, ft.alpha());
                font.draw(batch, ft.text, ft.x - 10, ft.y + (TimeUtils.timeSinceMillis(ft.startTime) / 6f));
            }

            // se game over, desenha overlay de Game Over
            if (gameOver) {
                if (gameOverImage != null) batch.draw(gameOverImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                font.setColor(Color.WHITE);
                drawCenteredText("Current Score: " + score, Gdx.graphics.getHeight() * 0.66f);

                // ranking dinâmico
                float ry = Gdx.graphics.getHeight() * 0.58f;
                for (int i = 0; i < Math.min(5, scoreManager.getScores().size()); i++) {
                    if (i == 0) font.setColor(Color.CYAN);
                    else if (i == 1) font.setColor(Color.PURPLE);
                    else if (i == 2) font.setColor(Color.GREEN);
                    else font.setColor(Color.RED);
                    drawCenteredText(scoreManager.getScores().get(i).toString(), ry - i * 26f);
                }

                font.setColor(Color.WHITE);
                drawCenteredText("Press SPACE para reiniciar ou ESC para voltar ao menu", Gdx.graphics.getHeight() * 0.18f);
            }
        }

        batch.end();
    }

    private void drawCenteredText(String text, float y) {
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.draw(batch, text, x, y);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        if (startScreenImage != null) startScreenImage.dispose();
        if (gameOverImage != null) gameOverImage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (foodTexture != null) foodTexture.dispose();
        if (snakeHeadTexture != null) snakeHeadTexture.dispose();
        if (snakeBodyTexture != null) snakeBodyTexture.dispose();
        if (eatSound != null) eatSound.dispose();
        if (gameOverSound != null) gameOverSound.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();
    }
}
