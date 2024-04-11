import java.util.ArrayList;
import java.util.List;

public class Game {

    /*
     *  Points added to score when apple is consumed
     */
    private final int K = 10;


    private final int N = 1;

    /*
     *  Percent decrease in move delay when apple is consumed
     */
    private final int P = 1;

    private int[][] grid;

    private Snake snake;

    private long tick;

    private int score;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    public Game(int heigth, int width) {
        this.grid = new int[heigth - 1][width - 1];
        for (int i = 0; i < heigth - 1; i++) {
            for (int j = 0; j < width - 1; j++) {
                this.grid[i][j] = 0;
            }
        }
        this.tick = 1000;
        ArrayList<Point> snake = new ArrayList<>();
        int i = (int) Math.floor((float) (heigth - 1) / 2);
        int j = (int) Math.floor((float) (width - 1) / 2);
        Point dir;
        if (i % 2 == 0) {
            dir = new Point(0, -1);
        }
        else {
            dir = new Point(0, 1);
        }
        for (int k = 0; k < 6; k++) {
            snake.add(new Point(i, j));
            if (j == 1) {
                i -= 1;
            }
            else if (i % 2 == 0) {
                j += 1;
            }
            else {
                j -= 1;
            }
        }
        this.snake = new Snake(snake, dir);
        this.score = 0;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    public void setSnake(Snake newSnake) {
        this.snake= newSnake;
    }

    public Snake getSnake() {
        return snake;
    }

    public void setScore(int newScore) {
        this.score = newScore;
    }

    public int getScore() {
        return score;
    }

    public void setTick(long newTick) {
        this.tick = newTick;
    }

    public long getTick() {
        return tick;
    }

    public void setGrid(int[][] newGrid) {
        this.grid = newGrid;
    }

    public int[][] getGrid() {
        int[][] result = new int[grid.length][grid[0].length];
        for (int i = 0; i < result.length; i++) {
            int[] row = new int[result[0].length];
            for (int j = 0; j < result[0].length; j++) {
                row[j] = grid[i][j];
            }
            result[i] = row;
        }
        return result;
    }

    public int getGridAt(Point point) {
        return getGrid()[point.getX() - 1][point.getY() - 1];
    }

    public void setGridAt(int newValue, Point point) {
        int[][] grid = getGrid();
        grid[point.getX() - 1][point.getY() - 1] = newValue;
        setGrid(grid);
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    public Point[] getFreeSpace() {
        List<Point> result = new ArrayList<Point>();
        for (int i = 0; i < getGrid().length; i++) {
            for (int j = 0; j < getGrid()[0].length; j++) {
                if (! getSnake().contains(new Point(i + 1, j + 1))) {
                    if (getGridAt(new Point(i + 1, j + 1)) == 0) {
                        result.add(new Point(i + 1, j + 1));
                    }
                }
            }
        }
        return result.toArray(new Point[0]);
    }

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    public void tick() {
        if (getSnake() != null) {
            moveSnake();
            if (getSnake() != null) {
                double c = Math.random();
                if (c < 0.1) {
                    spawnApple();
                }
                updateScore();
            }
        }
    }

    private void updateScore() {
        setScore(getScore() + 1);
    }

    public void moveSnake() {
        getSnake().move();
        if (isValid(getSnake().getHead())) {
            if (! eatApple()) {
                getSnake().removeTail();
            }
        }
        else {
            loseGame();
        }
    }

    public void spawnApple() {
        Point newApple = randomFreePoint();
        setGridAt(1, newApple);
    }

    public boolean eatApple() {
        if (getGridAt(getSnake().getHead()) == 1) {
            setGridAt(0, getSnake().getHead());
            spawnApple();
            setTick(getTick() * (100 - P) / 100);
            setScore(getScore() + K);
            return true;
        }
        return false;
    }

    public void changeDir(Point newDir) {
        getSnake().setDir(newDir);
    }

    public void loseGame() {
        setSnake(null);
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    public char[][] getAbstractGrid() {
        char[][] result = new char[getGrid().length][getGrid()[0].length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                if (getSnake().contains(new Point(i+1, j+1))) {
                    result[i][j] = getSnake().charAt(new Point(i+1, j+1));
                }
                else if (getGridAt(new Point(i+1, j+1)) == 1) {
                    result[i][j] = '*';
                }
                else {
                    result[i][j] = ' ';
                }
            }
        }
        return result;
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    public Point randomFreePoint() {
        Point[] freeSpace = getFreeSpace();
        return freeSpace[(int) Math.floor(Math.random() * freeSpace.length)];
    }

    public void updateSize(int heigth, int width) {
        Point snakeBox = getSnake().getOuterBox();
        if (snakeBox.getX() < heigth && snakeBox.getY() < width) {
            findBestFit(heigth, width, snakeBox);
            int appleCount = countApples();
            updateGrid(heigth, width);
            for (int i = 0; i < appleCount; i++) {
                spawnApple();
            }
        }
        else {
            loseGame();
        }
    }

    private int countApples() {
        int counter = 0;
        for (int i = 0; i < getGrid().length; i++) {
            for (int j = 0; j < getGrid()[0].length; j++) {
                if (getGridAt(new Point(i + 1, j + 1)) == 1) {
                    counter += 1;
                }
            }
        }
        return counter;
    }

    private void updateGrid(int heigth, int width) {
        return;
    }

    public void findBestFit(int heigth, int width, Point snakeBox) {
        return;
    }

    public boolean isValid(Point point) {
        if (point.getX() < 1 || point.getY() < 1) {
            return false;
        }
        if (point.getX() > getGrid().length) {
            return false;
        }
        if (point.getY() > getGrid()[0].length) {
            return false;
        }
        if (getSnake().bodyContains(getSnake().getHead())) {
            return false;
        }
        return true;
    }
}
