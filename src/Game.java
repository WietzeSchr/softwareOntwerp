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

    private int delay;

    private int score;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /**
     * This constructor creates a new Game with the given parameters heigth and width
     * @post  | getGrid().length == heigth
     * @post  | getGrid()[0].length == width
     * @post  | score == 0
     * @param heigth | The heigth of the Game
     * @param width  | The width of the Game
     */
    public Game(int heigth, int width) {
        this.grid = new int[heigth][width];
        this.delay = 1000;
        ArrayList<Point> snake = new ArrayList<>();
        int i = (int) Math.floor((float) heigth / 2);
        int j = (int) Math.floor((float) width / 2);
        Direction dir;
        if (i % 2 == 0) {
            dir = Direction.WEST;
        }
        else {
            dir = Direction.EAST;
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

    /**
     * This method sets the snake of the Game to the given parameter newSnake
     * @post | getSnake() == newSnake
     * @param newSnake | The new snake of the Game
     * @return  | void
     */
    public void setSnake(Snake newSnake) {
        this.snake= newSnake;
    }

    /**
     * This method returns the snake of the Game
     * @return  | Snake, the snake of the Game
     */
    public Snake getSnake() {
        return snake;
    }

    /**
     * This method sets the score of the Game to the given parameter newScore
     * @post | getScore() == newScore
     * @param newScore  | The new score of the Game
     * @return  | void
     */
    public void setScore(int newScore) {
        this.score = newScore;
    }

    /**
     * This method returns the score of the Game
     * @return  | int, the score of the Game
     */
    public int getScore() {
        return score;
    }

    /**
     * This method sets the delay of the Game to the given parameter newTick
     * @param newDelay  | The new delay of the Game
     * @post    | getDelay() == newDelay
     * @return  | void
     */
    public void setDelay(int newDelay) {
        this.delay = newDelay;
    }

    /**
     * This method returns the delay of the Game
     * @return  | int, the delay of the Game
     */
    public int getDelay() {
        return delay;
    }

    /**
     * This method sets the grid of the Game to the given parameter newGrid
     * @param newGrid  | The new grid of the Game
     * @post    | getGrid() == newGrid
     * @return  | void
     */
    public void setGrid(int[][] newGrid) {
        this.grid = newGrid;
    }

    /**
     * This method returns the grid of the Game
     * @return  | int[][], the grid of the Game
     */
    public int[][] getGrid() {
        int[][] result = new int[grid.length][grid[0].length];
        for (int i = 0; i < result.length; i++) {
            int[] row = new int[result[0].length];
            System.arraycopy(grid[i], 0, row, 0, result[0].length);
            result[i] = row;
        }
        return result;
    }

    /**
     * This method returns the value of the grid at the given parameter point
     * @param point |The point at the grid of the Game
     * @return      | int, the value of the grid at the given parameter point
     */
    public int getGridAt(Point point) {
        return getGrid()[point.getX() - 1][point.getY() - 1];
    }

    /**
     * This method sets the value of the grid at the given parameter point to the given parameter newValue
     * @param newValue | The new value of the grid at the given parameter point
     * @param point    | The point at the grid of the Game
     * @post    | getGridAt(point) == newValue
     * @return  | void
     */
    public void setGridAt(int newValue, Point point) {
        int[][] grid = getGrid();
        grid[point.getX() - 1][point.getY() - 1] = newValue;
        setGrid(grid);
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    /**
     * This method returns a list of all free points in the grid
     * @return  | Point[], a list of all free points in the grid
     */
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

    /**
     * This method ticks the Game by moving the snake and updating the score if the snake is not null 
     * @return  | void
     */
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

    /**
     * This method updates the score of the Game by adding 1 to the current score
     * @return  | void
     */
    private void updateScore() {
        setScore(getScore() + 1);
    }

    /**
     * This method moves the snake of the Game and checks if the snake is still valid
     * If the snake is not valid the game is lost
     * If the snake eats no apple, the tail of the snake gets smaller by 1
     * @return  | void
     */
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

    /**
     * This method spawns an apple at a random free point in the grid
     * @return  | void
     */
    public void spawnApple() {
        Point newApple = randomFreePoint();
        setGridAt(1, newApple);
    }

    /**
     * This method checks if the snake eats an apple and updates the score and the tick of the Game 
     * @return  | boolean, true if the snake eats an apple, false otherwise
     */
    public boolean eatApple() {
        if (getGridAt(getSnake().getHead()) == 1) {
            setGridAt(0, getSnake().getHead());
            spawnApple();
            setDelay(getDelay() * (100 - P) / 100);
            setScore(getScore() + K);
            return true;
        }
        return false;
    }

    /**
     * This method changes the direction of the snake in the direction of the given parameter newDir
     * @param newDir | The new direction of the snake
     * @post   | getSnake().getDir() == newDir
     * @return | void
     */
    public void changeDir(Direction newDir) {
        getSnake().setDir(newDir);
    }

    /**
     * This method sets the snake of the Game to null because the game is lost
     * @return  | void
     */
    public void loseGame() {
        setSnake(null);
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/
    /**
     * This method returns the abstract grid of the Game with the snake and the apples in it 
     * @return  | char[][], the abstract grid of the Game
     */
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

    /**
     * This method returns a random free point in the grid
     * @return  | Point, a random free point in the grid
     */
    public Point randomFreePoint() {
        Point[] freeSpace = getFreeSpace();
        return freeSpace[(int) Math.floor(Math.random() * freeSpace.length)];
    }

    /**
     * This method updates the size of the grid and the snake
     * If the snake is too big for the new grid the game is lost
     * @param heigth | The new heigth of the grid
     * @param width  | The new width of the grid
     * @return       | void
     */
    public void updateSize(int heigth, int width) {
        Box snakeBox = getSnake().getOuterBox();
        if (snakeBox.getHeight() < heigth && snakeBox.getWidth() < width) {
            updateGrid(heigth, width);
            findBestFit(heigth, width, snakeBox);
        }
        else {
            loseGame();
        }
    }

    /**
     * This method returns the number of apples in the grid
     * @return  | int, the number of apples in the grid
     * Visibile for testing
     */
     int countApples() {
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
    
    /**
     * This metod updates the grid of the game to the given heigth and width
     * @param heigth | The new heigth of the grid
     * @param width  | The new width of the grid
     * @return       | void
     */
    private void updateGrid(int heigth, int width) {
        int[][] newGrid = new int[heigth][width];
        int[][] oldGrid = getGrid();
        for (int i = 0; i < Math.min(heigth, oldGrid.length); i++) {
            System.arraycopy(oldGrid[i], 0, newGrid[i], 0, Math.min(width - 1, oldGrid[0].length));
        }
        setGrid(newGrid);
    }

    /**
     * This method finds the best fit for the snake in the new grid
     * @param heigth   | The heigth of the new grid
     * @param width    | The width of the new grid
     * @param snakeBox | The box around the snake to fit in the new grid
     * @return  | void
     */
    private void findBestFit(int heigth, int width, Box snakeBox) {
        if(!rightPointValid(heigth, width, snakeBox) || !leftPointValid(snakeBox)){
            Point snakeHeadToMid = (new Point(heigth/2, width/2)).minus(getSnake().getHead());
            translateSnake(snakeHeadToMid);
            snakeBox = getSnake().getOuterBox();
            Point translateVector = new Point(0,0);
            if(snakeBox.getLeftUpperPoint().getX()<0) {
                translateVector.minus(new Point(snakeBox.getLeftUpperPoint().getX(), 0));
            }
            if(snakeBox.getLeftUpperPoint().getY()<0) {
                translateVector.minus(new Point(0, snakeBox.getLeftUpperPoint().getY()));
            }
            if(snakeBox.getRightLowerPoint().getX()>heigth) {
                translateVector.minus(new Point(width-snakeBox.getRightLowerPoint().getX(), 0));
            }
            if(snakeBox.getRightLowerPoint().getY()>width) {
                translateVector.minus(new Point(0, heigth-snakeBox.getRightLowerPoint().getY()));
            }
            translateSnake(translateVector.add(new Point(1,1)));
        }
    }

    //translates the snake over the given vector
    /**
     * This method adds the given vector to the snake to translate it over the grid
     * @param vector | The vector to translate the snake over
     * @return       | void
     */
    private void translateSnake(Point vector){
        Snake oldSnake = getSnake();
        Snake newSnake = getSnake();
        newSnake.setHead(oldSnake.getHead().add(vector));
        ArrayList<Point> newBody = new ArrayList<Point>();
        for(Point bodyPart: oldSnake.getBody()){
            newBody.add(bodyPart.add(vector));
        }
        newSnake.setBody(newBody);
        setSnake(newSnake);
    }

    //checks if given box is fully in the grid
    /**
     * This method checks if the given box is fully in the grid, by looking at the left upper point 
     * @param box  | The box to check if it is fully in the grid
     * @return     | boolean, true if the box is valid with the left upper point, false otherwise
     */
    private boolean leftPointValid(Box box){
        return box.getLeftUpperPoint().getX()>0 && box.getLeftUpperPoint().getY()>0;
    }
     /**
     * This method checks if the given box is fully in the grid, by looking at the right lower point 
     * @param box | the box to check if it is fully in the grid
     * @return    | boolean, true if the box valid with the right lower point, false otherwise
     */
    private boolean rightPointValid(int heigth, int width, Box box){
        return box.getRightLowerPoint().getX()<heigth && box.getRightLowerPoint().getY()<width;
    }

    /**
     * This method checks if the given point is valid in the grid
     * @param point | The point to check if it is valid in the grid
     * @return      | boolean, true if the point is valid in the grid, false otherwise
     */
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
