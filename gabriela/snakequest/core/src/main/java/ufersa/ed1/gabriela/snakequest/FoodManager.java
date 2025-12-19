package ufersa.ed1.gabriela.snakequest;

import java.util.Random;
import ufersa.ed1.gabriela.snakequest.algorithms.SearchAlgorithms;
import ufersa.ed1.gabriela.snakequest.structures.GenericArray;

/**
 * FoodManager adaptado para usar as estruturas genéricas do projeto (GenericArray).
 * Não usa coleções da JDK para armazenar células alcançáveis.
 */
public class FoodManager {
    private final GameBoard board;
    private final Random rng = new Random();
    private int foodX, foodY;

    public FoodManager(GameBoard board) { this.board = board; }

    public void spawn(Snake snake) {
        int cols = board.getCols();
        int rows = board.getRows();

        // CellChecker que considera paredes
        SearchAlgorithms.CellChecker checker = (x, y) -> !board.isWall(x, y);

        // floodFill a partir da cabeça para obter células alcançáveis
        int sx = snake.head().x;
        int sy = snake.head().y;
        boolean[][] seen = SearchAlgorithms.floodFill(board, snake, sx, sy, checker);

        // monta lista de células livres alcançáveis usando GenericArray
        GenericArray<int[]> reachable = new GenericArray<>(Math.max(4, cols * rows / 4));
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                if (!seen[x][y]) continue;
                if (snake.occupies(x, y)) continue;
                if (board.isWall(x, y)) continue;
                reachable.add(new int[]{x, y});
            }
        }

        if (reachable.size() > 0) {
            int idx = rng.nextInt(reachable.size());
            int[] chosen = reachable.get(idx);
            foodX = chosen[0];
            foodY = chosen[1];
            return;
        }

        // Fallback determinístico: varre todo o tabuleiro e escolhe a primeira célula livre
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                if (board.isWall(x, y)) continue;
                if (!snake.occupies(x, y)) {
                    foodX = x;
                    foodY = y;
                    return;
                }
            }
        }

        // Se não encontrou nada (tabuleiro cheio), mantém coordenadas atuais
    }

    public boolean isFoodAt(int x, int y) {
        return x == foodX && y == foodY;
    }

    public int getFoodX() { return foodX; }
    public int getFoodY() { return foodY; }
}
