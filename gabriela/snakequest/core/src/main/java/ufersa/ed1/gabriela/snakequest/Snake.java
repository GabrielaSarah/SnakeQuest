package ufersa.ed1.gabriela.snakequest;

public class Snake {
    private Segment[] buffer;
    private int headIndex;   // índice da cabeça no buffer
    private int count;       // número de segmentos
    private Direction direction;
    private boolean growNextMove;

    private static final int DEFAULT_CAPACITY = 16;

    public Snake(int startX, int startY) {
        this(DEFAULT_CAPACITY, startX, startY);
    }

    public Snake(int initialCapacity, int startX, int startY) {
        int cap = Math.max(4, initialCapacity);
        buffer = new Segment[cap];
        // inicializa com 3 segmentos (cabeça + 2)
        buffer[0] = new Segment(startX, startY);
        buffer[1] = new Segment(startX - 1, startY);
        buffer[2] = new Segment(startX - 2, startY);
        headIndex = 0;      // cabeça em buffer[0]
        count = 3;
        direction = Direction.RIGHT;
        growNextMove = false;
    }

    /** Retorna a cabeça (Segment) */
    public Segment head() {
        return buffer[headIndex];
    }

    /** Retorna número de segmentos */
    public int size() {
        return count;
    }

    /** Retorna o i-ésimo segmento (0 = cabeça, 1 = próximo, ...) */
    public Segment get(int i) {
        if (i < 0 || i >= count) throw new IndexOutOfBoundsException();
        int idx = (headIndex + i) % buffer.length;
        return buffer[idx];
    }

    /** Define direção, evitando reversão direta */
    public void setDirection(Direction dir) {
        if (dir == null) return;
        if ((this.direction == Direction.LEFT && dir == Direction.RIGHT) ||
            (this.direction == Direction.RIGHT && dir == Direction.LEFT) ||
            (this.direction == Direction.UP && dir == Direction.DOWN) ||
            (this.direction == Direction.DOWN && dir == Direction.UP)) {
            return;
        }
        this.direction = dir;
    }

    /** Marca para crescer no próximo movimento */
    public void growOnNextMove() {
        this.growNextMove = true;
    }

    /** Move a cobra: insere nova cabeça e remove cauda se necessário */
    public void move() {
        Segment oldHead = head();
        int nx = oldHead.x;
        int ny = oldHead.y;
        switch (direction) {
            case UP:    ny += 1; break;
            case DOWN:  ny -= 1; break;
            case LEFT:  nx -= 1; break;
            case RIGHT: nx += 1; break;
        }

        // se buffer cheio, redimensiona (dobrar)
        if (count == buffer.length) resize(buffer.length * 2);

        // nova cabeça será escrita em posição anterior ao headIndex (circular)
        headIndex = (headIndex - 1 + buffer.length) % buffer.length;
        buffer[headIndex] = new Segment(nx, ny);
        count++;

        if (growNextMove) {
            growNextMove = false; // mantemos a cauda, apenas crescemos
        } else {
            // remover a cauda: simplesmente decrementar count
            // (a "cauda" fica implicitamente fora da janela válida)
            count--;
            int tailPos = (headIndex + count) % buffer.length;
            buffer[tailPos] = null;
        }
    }

    /**
     * Verifica se algum segmento da cobra ocupa a posição (x, y).
     * Complexidade O(n) onde n é o número de segmentos.
     */
    public boolean occupies(int x, int y) {
        for (int i = 0; i < this.size(); i++) {
            Segment s = this.get(i);
            if (s.x == x && s.y == y) return true;
        }
        return false;
    }

    /** Redimensiona o buffer mantendo a ordem (cabeça em índice 0 após resize) */
    private void resize(int newCapacity) {
        Segment[] newBuf = new Segment[newCapacity];
        // copiar elementos na ordem: cabeça -> ... -> cauda
        for (int i = 0; i < count; i++) {
            newBuf[i] = get(i);
        }
        buffer = newBuf;
        headIndex = 0;
    }

    /** Verifica colisão da cabeça com o próprio corpo (exclui índice 0) */
    public boolean collidesWithSelf() {
        Segment h = head();
        for (int i = 1; i < count; i++) {
            Segment s = get(i);
            if (s.x == h.x && s.y == h.y) return true;
        }
        return false;
    }
}
