package ufersa.ed1.gabriela.snakequest.algorithms;
import ufersa.ed1.gabriela.snakequest.structures.GenericArray;
import java.io.*;

public class ScoreManager {
    private GenericArray<ScoreEntry> scores;

    public ScoreManager(int capacity) {
        scores = new GenericArray<>(capacity);
    }

    // Adiciona novo score e ordena
    public void addScore(ScoreEntry entry) {
        // encontra posição de inserção (maior score primeiro)
        int i = 0;
        while (i < scores.size() && scores.get(i).score >= entry.score) {
            i++;
        }

        // adiciona no final para aumentar o tamanho interno
        scores.add(entry);

        // desloca elementos para a direita até abrir espaço em i
        for (int j = scores.size() - 1; j > i; j--) {
            scores.set(j, scores.get(j - 1));
        }

        // coloca a nova entrada na posição correta
        scores.set(i, entry);
    }


    // Busca linear por nome
    public ScoreEntry findByName(String name) {
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).name.equalsIgnoreCase(name)) {
                return scores.get(i);
            }
        }
        return null;
    }

    // Salva em arquivo
    public void saveScores(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < scores.size(); i++) {
                ScoreEntry e = scores.get(i);
                writer.write(e.name + ";" + e.score + ";" + e.time);
                writer.newLine();
            }
        }
    }

    // Carrega de arquivo
    public void loadScores(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    scores.add(new ScoreEntry(name, score));
                }
            }
            InsertionSort.sort(scores);
        }
    }

    public GenericArray<ScoreEntry> getScores() {
        return scores;
    }
}
