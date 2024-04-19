public enum Direction {
    NORD(new Point(-1, 0)),
    EAST(new Point(0, 1)),
    SOUTH(new Point(1, 0)),
    WEST(new Point(0, -1));

    final Point point;

    /**
     * This constructor creates a new Direction with a given Point
     * @param point the Point to be used for the Directions
     */
    Direction(Point point) {
        this.point = point;
    }
}
