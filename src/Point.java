public class Point {

    private final int x;

    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point add(Point other) {
        return new Point(getX() + other.getX(), getY() + other.getY());
    }

    public Point minus(Point other) {
        return new Point(getX() - other.getX(), getY() - other.getY());
    }

    public Point times(int factor) {return new Point(getX()*factor, getY()*factor);}

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != getClass()) {
            return false;
        }
        else {
            if (getX() != ((Point) o).getX()) {
                return false;
            }
            if (getY() != ((Point) o).getY()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Point clone() {
        return new Point(getX(), getY());
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }
}
