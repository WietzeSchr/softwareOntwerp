public class Box {
    int height;
    int width;
    Point leftUpperPoint;
    Point rightLowerPoint;
    Box(int height, int width, Point leftUpperPoint, Point rightLowerPoint){
        this.height = height;
        this.width = width;
        this.leftUpperPoint = leftUpperPoint;
        this.rightLowerPoint = rightLowerPoint;
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
    public Point getRightLowerPoint(){return rightLowerPoint.clone();}
    public void setRightLowerPoint(Point rightLowerPoint){
        this.rightLowerPoint = rightLowerPoint;
    }
}
