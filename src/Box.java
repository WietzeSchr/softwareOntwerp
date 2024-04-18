public class Box {
    int height;
    int width;
    Point leftUpperPoint;

    /**
     * @pre leftUpperPoint.getX() < rightLowerPoint.getX()
     * @pre leftUpperPoint.getY() < rightLowerPoint.getY()
     */
    Box(int height, int width, Point leftUpperPoint){
        this.height = height;
        this.width = width;
        this.leftUpperPoint = leftUpperPoint;
    }

    public int getHeight(){return height;}
    public void setHeight(int height){
        this.height = height;
    }
    public int getWidth(){return width;}
    public void setWidth(int width){
        this.width = width;
    }
    public Point getLeftUpperPoint(){return leftUpperPoint.clone();}
    public void setLeftUpperPoint(Point leftUpperPoint){
        this.leftUpperPoint = leftUpperPoint;
    }
    public Point getRightLowerPoint(){return leftUpperPoint.add(new Point(height - 1, width - 1));}
}
