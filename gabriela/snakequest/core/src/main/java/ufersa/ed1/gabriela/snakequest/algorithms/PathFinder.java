//package ufersa.ed1.gabriela.snakequest.algorithms;

//import java.util.*;
//import ufersa.ed1.gabriela.snakequest.Segment;
//import ufersa.ed1.gabriela.snakequest.Snake;
//import ufersa.ed1.gabriela.snakequest.GameBoard;
//
//public class PathFinder {
//    private final GameBoard board;
//
//    public PathFinder(GameBoard board) {
//        this.board = board;
//    }
//
//    private static class Node {
//        int x, y;
//        int g;
//        int f;
//        Node parent;
//        Node(int x, int y, int g, int f, Node parent) { this.x = x; this.y = y; this.g = g; this.f = f; this.parent = parent; }
//    }
//
//    private int heuristic(int x1, int y1, int x2, int y2) {
//        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
//    }
//
//    /**
//     * Retorna lista de coordenadas do caminho (inclui cabeça como primeiro elemento).
//     * Se não houver caminho, retorna lista vazia.
//     * willGrow indica se a cobra crescerá no próximo movimento (afeta se a cauda pode ser considerada livre).
//     */
//    public List<int[]> findPath(Snake snake, int targetX, int targetY, boolean willGrow) {
//        Segment head = snake.head();
//        int sx = head.x;
//        int sy = head.y;
//
//        Comparator<Node> cmp = Comparator.comparingInt(n -> n.f);
//        PriorityQueue<Node> open = new PriorityQueue<>(cmp);
//        boolean[][] closed = new boolean[board.getCols()][board.getRows()];
//        Map<String, Integer> bestG = new HashMap<>();
//
//        Node start = new Node(sx, sy, 0, heuristic(sx, sy, targetX, targetY), null);
//        open.add(start);
//        bestG.put(key(sx, sy), 0);
//
//        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
//
//        while (!open.isEmpty()) {
//            Node cur = open.poll();
//            if (cur.x == targetX && cur.y == targetY) {
//                LinkedList<int[]> path = new LinkedList<>();
//                Node n = cur;
//                while (n != null) {
//                    path.addFirst(new int[]{n.x, n.y});
//                    n = n.parent;
//                }
//                return path;
//            }
//
//            if (closed[cur.x][cur.y]) continue;
//            closed[cur.x][cur.y] = true;
//
//            for (int[] d : dirs) {
//                int nx = cur.x + d[0];
//                int ny = cur.y + d[1];
//                if (!board.isInside(nx, ny)) continue;
//                if (board.isWall(nx, ny)) continue;
//
//                // verifica ocupação pela cobra
//                boolean occupied = snake.occupies(nx, ny);
//                if (occupied) {
//                    // se não vai crescer, permitir passar pela cauda atual (ela será liberada)
//                    Segment tail = snake.get(snake.size() - 1);
//                    if (!(tail.x == nx && tail.y == ny) || willGrow) {
//                        continue;
//                    }
//                }
//
//                int ng = cur.g + 1;
//                String k = key(nx, ny);
//                if (bestG.containsKey(k) && bestG.get(k) <= ng) continue;
//                bestG.put(k, ng);
//                int f = ng + heuristic(nx, ny, targetX, targetY);
//                open.add(new Node(nx, ny, ng, f, cur));
//            }
//        }
//
//        return Collections.emptyList();
//    }
//
//    private String key(int x, int y) { return x + "," + y; }
//}
