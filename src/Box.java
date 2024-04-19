public class Box {
    int height;
    int width;
    Point leftUpperPoint;

    /**
     * This constructor creates a new Box with the given parameters height, width and leftUpperPoint
     * @param height the height of the Box
     * @param width the width of the Box
     * @param leftUpperPoint the left upper point of the Box
     * @pre leftUpperPoint.getX() < rightLowerPoint.getX()
     * @pre leftUpperPoint.getY() < rightLowerPoint.getY()
     * @post getHeight() == height
     * @post getWidth() == width
     * @post getLeftUpperPoint() == leftUpperPoint
     */
    Box(int height, int width, Point leftUpperPoint){
        this.height = height;
        this.width = width;
        this.leftUpperPoint = leftUpperPoint;
    }

    /**
     * This method returns the height of the Box
     * @return: int, the height of the Box
     */
    public int getHeight(){
        return height;
    }

    /**
     * This method sets the height of the Box to the given parameter height
     * @param height the new height of the Box
     * @post getHeight() == height
     * @return: void
     */
    public void setHeight(int height){
        this.height = height;
    }

    /**
     * This method returns the width of the Box
     * @return: int, the width of the Box
     */
    public int getWidth(){
        return width;
    }

    /**
     * This method sets the width of the Box to the given parameter height
     * @param width the new width of the Box
     * @post getWidth() == width
     * @return: void
     */
    public void setWidth(int width){
        this.width = width;
    }

    /**
     * This method returns the left upper point of the Box
     * @return: Point, the left upper point of the Box
     */
    public Point getLeftUpperPoint(){
        return leftUpperPoint.clone();
    }
    /**
     * This method sets the left upper point of the Box to the given parameter leftUpperPoint
     * @param leftUpperPoint the new left upper point of the Box
     * @post getLeftUpperPoint() == leftUpperPoint
     * @return: void
     */
    public void setLeftUpperPoint(Point leftUpperPoint){
        this.leftUpperPoint = leftUpperPoint;
    }

    /**
     * This method returns the right lower point of the Box
     * @return: Point, the right lower point of the Box
     */
    public Point getRightLowerPoint(){
        return leftUpperPoint.add(new Point(height - 1, width - 1));
    }
}
