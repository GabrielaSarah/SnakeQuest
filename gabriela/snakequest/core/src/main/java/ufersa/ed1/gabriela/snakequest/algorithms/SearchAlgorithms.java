package ufersa.ed1.gabriela.snakequest.algorithms;

import ufersa.ed1.gabriela.snakequest.GameBoard;
import ufersa.ed1.gabriela.snakequest.Snake;
import ufersa.ed1.gabriela.snakequest.structures.Queue;
import ufersa.ed1.gabriela.snakequest.structures.LinkedList;

/**
 * Algoritmos de busca (BFS / flood-fill) usando as estruturas do projeto (Queue, LinkedList).
 * Não usa coleções da JDK para armazenar células alcançáveis ou caminhos.
 */
public final class SearchAlgorithms {
    private SearchAlgorithms() {}

    @FunctionalInterface
    public interface CellChecker {
        boolean isPassable(int x, int y);
    }

    private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

    /**
     * Retorna matriz seen[x][y] indicando células alcançáveis a partir de (sx,sy).
     * Se snake for null, a ocupação por cobra não é considerada.
     * Se extraPassable for null, nenhuma verificação extra é feita (apenas board.isInside).
     */
    public static boolean[][] floodFill(GameBoard board, Snake snake, int sx, int sy, CellChecker extraPassable) {
        int cols = board.getCols();
        int rows = board.getRows();
        boolean[][] seen = new boolean[cols][rows];
        if (!board.isInside(sx, sy)) return seen;

        Queue<int[]> q = new Queue<>();
        q.enqueue(new int[]{sx, sy});
        seen[sx][sy] = true;

        while (!q.isEmpty()) {
            int[] p = q.dequeue();
            int x = p[0], y = p[1];
            for (int[] d : DIRS) {
                int nx = x + d[0], ny = y + d[1];
                if (!board.isInside(nx, ny)) continue;
                if (seen[nx][ny]) continue;
                if (snake != null && snake.occupies(nx, ny)) continue;
                if (extraPassable != null && !extraPassable.isPassable(nx, ny)) continue;
                seen[nx][ny] = true;
                q.enqueue(new int[]{nx, ny});
            }
        }
        return seen;
    }

    /** Conta células alcançáveis (útil para validação de mapa). */
    public static int floodFillCount(GameBoard board, Snake snake, int sx, int sy, CellChecker extraPassable) {
        boolean[][] seen = floodFill(board, snake, sx, sy, extraPassable);
        int cnt = 0;
        for (int x = 0; x < seen.length; x++) {
            for (int y = 0; y < seen[0].length; y++) {
                if (seen[x][y]) cnt++;
            }
        }
        return cnt;
    }

    /** Verifica se existe caminho entre (sx,sy) e (tx,ty) usando BFS. */
    public static boolean existsPathBFS(GameBoard board, Snake snake, int sx, int sy, int tx, int ty, CellChecker extraPassable) {
        if (!board.isInside(tx, ty) || !board.isInside(sx, sy)) return false;
        if (sx == tx && sy == ty) return true;

        boolean[][] seen = new boolean[board.getCols()][board.getRows()];
        Queue<int[]> q = new Queue<>();
        q.enqueue(new int[]{sx, sy});
        seen[sx][sy] = true;

        while (!q.isEmpty()) {
            int[] p = q.dequeue();
            int x = p[0], y = p[1];
            for (int[] d : DIRS) {
                int nx = x + d[0], ny = y + d[1];
                if (!board.isInside(nx, ny)) continue;
                if (seen[nx][ny]) continue;
                if (snake != null && snake.occupies(nx, ny)) continue;
                if (extraPassable != null && !extraPassable.isPassable(nx, ny)) continue;
                if (nx == tx && ny == ty) return true;
                seen[nx][ny] = true;
                q.enqueue(new int[]{nx, ny});
            }
        }
        return false;
    }

    /**
     * Retorna o caminho mínimo (LinkedList de int[]{x,y}) de (sx,sy) até (tx,ty) usando BFS.
     * Retorna lista vazia (LinkedList vazia) se não houver caminho.
     * A lista inclui a posição inicial como primeiro elemento.
     */
    public static LinkedList<int[]> shortestPathBFS(GameBoard board, Snake snake, int sx, int sy, int tx, int ty, CellChecker extraPassable) {
        LinkedList<int[]> empty = new LinkedList<>();
        if (!board.isInside(tx, ty) || !board.isInside(sx, sy)) return empty;
        if (sx == tx && sy == ty) {
            LinkedList<int[]> single = new LinkedList<>();
            single.addLast(new int[]{sx, sy});
            return single;
        }

        int cols = board.getCols(), rows = board.getRows();
        boolean[][] seen = new boolean[cols][rows];
        int[][] px = new int[cols][rows];
        int[][] py = new int[cols][rows];
        for (int i = 0; i < cols; i++) for (int j = 0; j < rows; j++) { px[i][j] = -1; py[i][j] = -1; }

        Queue<int[]> q = new Queue<>();
        q.enqueue(new int[]{sx, sy});
        seen[sx][sy] = true;

        boolean found = false;
        while (!q.isEmpty() && !found) {
            int[] p = q.dequeue();
            int x = p[0], y = p[1];
            for (int[] d : DIRS) {
                int nx = x + d[0], ny = y + d[1];
                if (!board.isInside(nx, ny)) continue;
                if (seen[nx][ny]) continue;
                if (snake != null && snake.occupies(nx, ny)) continue;
                if (extraPassable != null && !extraPassable.isPassable(nx, ny)) continue;
                seen[nx][ny] = true;
                px[nx][ny] = x;
                py[nx][ny] = y;
                if (nx == tx && ny == ty) { found = true; break; }
                q.enqueue(new int[]{nx, ny});
            }
        }

        if (!found) return empty;

        // reconstruir caminho usando LinkedList (addFirst para ordem correta)
        LinkedList<int[]> path = new LinkedList<>();
        int cx = tx, cy = ty;
        while (!(cx == sx && cy == sy)) {
            path.addFirst(new int[]{cx, cy});
            int nx = px[cx][cy], ny = py[cx][cy];
            cx = nx; cy = ny;
        }
        path.addFirst(new int[]{sx, sy});
        return path;
    }
}
