//package ufersa.ed1.gabriela.snakequest.algorithms;
//
//import java.util.Random;
//
//public class MazeGenerator {
//    private final int cols;
//    private final int rows;
//    private final boolean[][] walls; // true = parede
//
//    public MazeGenerator(int cols, int rows) {
//        this.cols = cols;
//        this.rows = rows;
//        this.walls = new boolean[cols][rows];
//    }
//
//    /**
//     * Gera um labirinto simples usando randomized DFS.
//     * Recomenda-se usar cols/rows ímpares para corredores de 1 célula.
//     */
//    public boolean[][] generate(long seed) {
//        Random rng = new Random(seed);
//        // inicializa todas como parede
//        for (int x = 0; x < cols; x++) for (int y = 0; y < rows; y++) walls[x][y] = true;
//
//        // ponto inicial (garanta que esteja dentro)
//        int sx = 1;
//        int sy = 1;
//        if (sx >= cols) sx = 0;
//        if (sy >= rows) sy = 0;
//
//        dfsCarve(sx, sy, rng);
//        return walls;
//    }
//
//    private void dfsCarve(int cx, int cy, Random rng) {
//        walls[cx][cy] = false; // chão
//        int[][] dirs = {{2,0},{-2,0},{0,2},{0,-2}};
//        // embaralha direções
//        for (int i = 0; i < dirs.length; i++) {
//            int j = rng.nextInt(dirs.length);
//            int[] tmp = dirs[i]; dirs[i] = dirs[j]; dirs[j] = tmp;
//        }
//        for (int[] d : dirs) {
//            int nx = cx + d[0];
//            int ny = cy + d[1];
//            if (nx >= 0 && nx < cols && ny >= 0 && ny < rows && walls[nx][ny]) {
//                // remove parede entre as células
//                int mx = cx + d[0]/2;
//                int my = cy + d[1]/2;
//                if (mx >= 0 && mx < cols && my >= 0 && my < rows) walls[mx][my] = false;
//                dfsCarve(nx, ny, rng);
//            }
//        }
//    }
//}
