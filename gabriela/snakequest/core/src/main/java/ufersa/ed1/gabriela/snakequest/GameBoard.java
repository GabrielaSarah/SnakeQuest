package ufersa.ed1.gabriela.snakequest;
/**
 * GameBoard com suporte a paredes usando matriz booleana.
 * Mantém API compatível: getCols, getRows, getCellSize, isInside, isWall, setWalls, setWallAt, clearWalls.
 */
public class GameBoard {
    private final int cols;
    private final int rows;
    private final int cellSize;
    private boolean[][] walls; // true = parede

    public GameBoard(int cols, int rows, int cellSize) {
        this.cols = cols;
        this.rows = rows;
        this.cellSize = cellSize;
        this.walls = new boolean[cols][rows];
        clearWalls();
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }
    public int getCellSize() { return cellSize; }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < cols && y >= 0 && y < rows;
    }

    /** Retorna true se a célula (x,y) for parede. Fora do tabuleiro é considerado parede. */
    public boolean isWall(int x, int y) {
        if (!isInside(x, y)) return true;
        return walls[x][y];
    }

    /** Substitui a matriz de paredes. A matriz deve ter dimensões [cols][rows]. */
    public void setWalls(boolean[][] newWalls) {
        if (newWalls == null) return;
        int maxX = Math.min(cols, newWalls.length);
        int maxY = Math.min(rows, newWalls[0].length);
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                walls[x][y] = newWalls[x][y];
            }
        }
    }

    /** Marca/desmarca uma célula como parede. Ignora se fora do tabuleiro. */
    public void setWallAt(int x, int y, boolean isWall) {
        if (!isInside(x, y)) return;
        walls[x][y] = isWall;
    }

    /** Remove todas as paredes (torna todo o tabuleiro piso). */
    public void clearWalls() {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                walls[x][y] = false;
            }
        }
    }
}
