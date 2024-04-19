import java.util.ArrayList;

public class Snake extends Object{

    private Point head;

    private ArrayList<Point> body;

    private Direction dir;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /**
     * This constructor creates a new Snake with the given parameters snake and dir
     * @param snake the snake of the Snake
     * @param dir the direction of the Snake
     * @post getHead() == snake.get(0)
     * @post getBody() == snake.get(1, snake.size())
     * @post getDir() == dir
     */
    public Snake(ArrayList<Point> snake, Direction dir) {
        if (snake.size() >= 1) {
            this.head = snake.get(0);
            ArrayList<Point> body = new ArrayList<>();
            for (int i = 1; i < snake.size(); i++) {
                body.add(snake.get(i));
            }
            this.body = body;
            this.dir = dir;
        }
        else {
            throw new RuntimeException("Illegal snake");
        }
    }

    /** 
     * This constructor creates a new Snake with the given parameters head, body and dir
     * @param head the head of the Snake
     * @param body the body of the Snake
     * @param dir the direction of the Snake
     * @post getHead() == head
     * @post getBody() == body
     * @post getDir() == dir
     */
    public Snake(Point head, ArrayList<Point> body, Direction dir) {
        this.head = head;
        this.body = body;
        this.dir = dir;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    /**
     * This method sets the head of the Snake to the given parameter newHead
     * @param newHead the new head of the Snake
     * @post getHead() == newHead
     * @return: void
     */
    public void setHead(Point newHead) {
        this.head = newHead;
    }

    /**
     * This method returns the head of the Snake
     * @return: Point, the head of the Snake
     */
    public Point getHead() {
        return head.clone();
    }

    /**
     * This method sets the body of the Snake to the given parameter newBody
     * @param newBody the new body of the Snake
     * @post getBody() == newBody
     * @return: void
     */
    public void setBody(ArrayList<Point> newBody) {
        this.body = newBody;
    }

    /**
     * This method returns the body of the Snake
     * @return: ArrayList<Point>, the body of the Snake
     */
    public ArrayList<Point> getBody() {
        ArrayList<Point> result = new ArrayList<>();
        for (Point point : body) {
            result.add(point.clone());
        }
        return result;
    }

    /**
     * This method sets the direction of the Snake to the given parameter newDir
     * @param newDir the new direction of the Snake
     * @post getDir() == newDir
     * @return: void
     */
    public void setDir(Direction newDir) {
        if (!  getHead().add(newDir.point).equals(getBody().get(0))) {
            this.dir = newDir;
        }
    }

    /**
     * This method returns the direction of the Snake
     * @return: Point, the direction of the Snake
     */
    public Point getDir() {
        return dir.point;
    }

    /* ****************
     *   MOVE SNAKE   *
     * ****************/

    /**
     * This method moves the snake in the direction of the Snake
     * @return: void
     */
    public void move() {
        Point newHead = getHead().add(getDir());
        ArrayList<Point> newBody = new ArrayList<>();
        newBody.add(getHead());
        newBody.addAll(getBody());
        setHead(newHead);
        setBody(newBody);
    }

    /**
     * This method removes the last element of the body of the Snake
     * @return: void
     */
    public void removeTail() {
        ArrayList<Point> oldBody = getBody();
        oldBody.remove(oldBody.size() - 1);
        setBody(oldBody);
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /**
     * This method returns the char representation of the Snake at the given point p
     * @param p the point to get the char representation of the Snake
     * @return: char, the char representation of the Snake at the given point p
     */
    public char charAt(Point p) {
        if (getHead().equals(p)) {
            if (getDir().equals(Direction.NORD.point)) {
                return (char) 94;
            }
            else if (getDir().equals(Direction.SOUTH.point)) {
                return 'v';
            }
            else if (getDir().equals(Direction.WEST.point)) {
                return '<';
            }
            else {
                return '>';
            }
        }
        ArrayList<Point> body = getBody();
        for (int i = 0; i < body.size(); i++) {
            if (body.get(i).equals(p)) {
                if (i != body.size() - 1) {
                    return 'o';
                }
                else {
                    if (body.get(i - 1).minus(body.get(i)).equals(Direction.SOUTH.point) || body.get(i - 1).minus(body.get(i)).equals(Direction.NORD.point)) {
                        return '|';
                    }
                    else {
                        return '-';
                    }
                }
            }
        }
        return ' ';
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /** 
     * This method returns the abstract list of the Snake
     * @return: Point[], the abstract list of the Snake
     */
    public Point[] abstractList() {
        ArrayList<Point> body = getBody();
        Point[] result = new Point[body.size()+ 1];
        result[0] = getHead();
        for (int i = 0; i < body.size(); i++) {
            result[i + 1] = body.get(i);
        }
        return result;
    }

    /**
     * This method returns the outer box of the Snake, which is the smallest box that contains the Snake 
     * @return: Box, the outer box of the Snake
     */
    public Box getOuterBox() {
        Point[] snake = abstractList();
        int up = getHead().getX();
        int down = getHead().getX();
        int rigth = getHead().getY();
        int left = getHead().getY();
        for (int i = 0; i < snake.length; i++) {
            if (snake[i].getX() < up) up = snake[i].getX();
            else if (snake[i].getX() > down) down = snake[i].getX();
            if (snake[i].getY() < left) left = snake[i].getY();
            else if (snake[i].getY() > rigth) rigth = snake[i].getY();
        }
        return new Box(down - up + 1, rigth - left + 1, new Point(up, left));
    }

    /**
     * This method returns if the Snake contains the given point p
     * @param p the point to check if the Snake contains
     * @return: boolean, true if the Snake contains the point p, false otherwise
     */
    public boolean contains(Point p) {
        if (getHead().equals(p)) {
            return true;
        }
        if (bodyContains(p)) {
            return true;
        }
        return false;
    }

    /**
     * This method returns if the body of the Snake contains the given point p
     * @param p the point to check if the body of the Snake contains
     * @return: boolean, true if the body of the Snake contains the point p, false otherwise
     */
    public boolean bodyContains(Point p) {
        ArrayList<Point> body = getBody();
        for (int i = 0; i < body.size(); i++) {
            if (body.get(i).equals(p)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns if the body of the Snake contains the head of the Snake
     * @return: boolean, true if the body of the Snake contains the head of the Snake, false otherwise
     */
    public boolean bodyContainsHead() {
        return bodyContains(getHead());
    }
    
    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) {
            return false;
        }
        Snake other = (Snake) o;
        if (! getHead().equals(other.getHead())) {
            return false;
        }
        ArrayList<Point> body = getBody();
        ArrayList<Point> otherBody = other.getBody();
        if (body.size() != otherBody.size()) {
            return false;
        }
        for (int i = 0; i < body.size(); i++) {
            if (! body.get(i).equals(otherBody.get(i))) {
                return false;
            }
        }
        if (! getDir().equals(other.getDir())) {
            return false;
        }
        return true;
    }

    /**
     * This method returns the string representation of the Snake
     * @return: String, the string representation of the Snake
     */
    @Override
    public String toString() {
        String result = "";
        Point[] snake = abstractList();
        for (int i = 0; i < snake.length; i++) {
            result += snake[i].toString();
            result += " ";
        }
        return result;
    }
}
