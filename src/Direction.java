public enum Direction {
    NORD(new Point(-1, 0)),
    EAST(new Point(0, 1)),
    SOUTH(new Point(1, 0)),
    WEST(new Point(0, -1));

    final Point point;

    Direction(Point point) {
        this.point = point;
    }
}
