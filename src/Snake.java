import java.util.ArrayList;

public class Snake extends Object{

    private Point head;

    private ArrayList<Point> body;

    private Point dir;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    public Snake(ArrayList<Point> snake, Point dir) {
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

    public Snake(Point head, ArrayList<Point> body, Point dir) {
        this.head = head;
        this.body = body;
        this.dir = dir;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    public void setHead(Point newHead) {
        this.head = newHead;
    }

    public Point getHead() {
        return head.clone();
    }

    public void setBody(ArrayList<Point> newBody) {
        this.body = newBody;
    }

    public ArrayList<Point> getBody() {
        ArrayList<Point> result = new ArrayList<>();
        for (Point point : body) {
            result.add(point.clone());
        }
        return result;
    }

    public void setDir(Point newDir) {
        if (!  getHead().add(newDir).equals(getBody().get(0))) {
            this.dir = newDir;
        }
    }

    public Point getDir() {
        return dir.clone();
    }

    /* ****************
     *   MOVE SNAKE   *
     * ****************/

    public void move() {
        Point newHead = getHead().add(getDir());
        ArrayList<Point> newBody = new ArrayList<>();
        newBody.add(getHead());
        newBody.addAll(getBody());
        setHead(newHead);
        setBody(newBody);
    }

    public void removeTail() {
        ArrayList<Point> oldBody = getBody();
        oldBody.remove(oldBody.size() - 1);
        setBody(oldBody);
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    public char charAt(Point p) {
        Point UP = new Point(-1, 0);
        Point DOWN = new Point(1, 0);
        Point LEFT = new Point(0, -1);
        Point RIGHT = new Point(0, 1);
        if (getHead().equals(p)) {
            if (getDir().equals(UP)) {
                return (char) 94;
            }
            else if (getDir().equals(DOWN)) {
                return 'v';
            }
            else if (getDir().equals(LEFT)) {
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
                    if (body.get(i - 1).minus(body.get(i)).equals(DOWN) || body.get(i - 1).minus(body.get(i)).equals(UP)) {
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

    public Point[] abstractList() {
        ArrayList<Point> body = getBody();
        Point[] result = new Point[body.size()+ 1];
        result[0] = getHead();
        for (int i = 0; i < body.size(); i++) {
            result[i + 1] = body.get(i);
        }
        return result;
    }

    public Point getOuterBox() {
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
        return new Point(down - up + 1, rigth - left + 1);
    }

    public boolean contains(Point p) {
        if (getHead().equals(p)) {
            return true;
        }
        if (bodyContains(p)) {
            return true;
        }
        return false;
    }

    public boolean bodyContains(Point p) {
        ArrayList<Point> body = getBody();
        for (int i = 0; i < body.size(); i++) {
            if (body.get(i).equals(p)) {
                return true;
            }
        }
        return false;
    }

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
