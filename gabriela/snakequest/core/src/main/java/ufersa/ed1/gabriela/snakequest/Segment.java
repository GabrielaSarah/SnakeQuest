package ufersa.ed1.gabriela.snakequest;

public class Segment {
    public int x, y;
    public Segment(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Segment)) return false;
        Segment s = (Segment) o;
        return x == s.x && y == s.y;
    }
}
