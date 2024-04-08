public class Snake extends Object{

    private Point head;

    private Point[] body;

    private Point dir;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    public Snake(Point[] snake, Point dir) {
        this.head = snake[0];
        Point[] body = new Point[snake.length - 1];
        for (int i = 1; i < snake.length; i++) {
            body[i - 1] = snake[i];
        }
        this.body = body;
        this.dir = dir;
    }

    public Snake(Point head, Point[] body, Point dir) {
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
        return head.copy();
    }

    public void setBody(Point[] newBody) {
        this.body = newBody;
    }

    public Point[] getBody() {
        Point[] result = new Point[body.length];
        for (int i = 0; i < body.length; i++) {
            result[i] = body[i].copy();
        }
        return result;
    }

    public void setDir(Point newDir) {
        if (!  getHead().add(newDir).equals(getBody()[0])) {
            this.dir = newDir;
        }
    }

    public Point getDir() {
        return dir.copy();
    }

    /* ****************
     *   MOVE SNAKE   *
     * ****************/

    public Point moveHead() {
        return getHead().add(getDir());
    }

    public Point[] moveBody() {
        Point[] result = new Point[getBody().length];
        result[0] = getHead();
        for (int i = 0; i < getBody().length - 1; i++) {
            result[i + 1] = getBody()[i];
        }
        return result;
    }

    public Point[] extendBody() {
        Point[] result = new Point[getBody().length + 1];
        result[0] = getHead();
        for (int i = 0; i < getBody().length; i++) {
            result[i + 1] = getBody()[i];
        }
        return result;
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
        for (int i = 0; i < getBody().length; i++) {
            if (getBody()[i].equals(p)) {
                if (i != getBody().length - 1) {
                    return 'o';
                }
                else {
                    if (getBody()[i - 1].minus(getBody()[i]).equals(DOWN) || getBody()[i - 1].minus(getBody()[i]).equals(UP)) {
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
        Point[] result = new Point[getBody().length + 1];
        result[0] = getHead();
        for (int i = 0; i < getBody().length; i++) {
            result[i + 1] = getBody()[i];
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
        for (int i = 0; i < getBody().length; i++) {
            if (getBody()[i].equals(p)) {
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
        if (! getHead().equals(((Snake) o).getHead())) {
            return false;
        }
        if (getBody().length != ((Snake) o).getBody().length) {
            return false;
        }
        for (int i = 0; i < getBody().length; i++) {
            if (! getBody()[i].equals(((Snake) o).getBody()[i])) {
                return false;
            }
        }
        if (! getDir().equals(((Snake) o).getDir())) {
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
