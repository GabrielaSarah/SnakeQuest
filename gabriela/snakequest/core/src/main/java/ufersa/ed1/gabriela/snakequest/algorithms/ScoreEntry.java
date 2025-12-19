package ufersa.ed1.gabriela.snakequest.algorithms;

public class ScoreEntry implements Comparable<ScoreEntry> {
    public String name;
    public int score;
    public long time;

    public ScoreEntry(String name, int score) {
        this.name = name;
        this.score = score;
        this.time = System.currentTimeMillis();
    }

    @Override
    public int compareTo(ScoreEntry other) {
        // Ordena por score (decrescente)
        return Integer.compare(this.score, other.score);
    }

    @Override
    public String toString() {
        return name + " - " + score;
    }
}
