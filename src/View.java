import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class View extends Layout{

    private int position;
    TerminalHandler terminalHandler = new TerminalHandler();

    public View(int height, int width, Point leftUpperCorner) {
        super(height, width, leftUpperCorner);
    }
    public View(int height, int width, CompositeLayout parent, Point leftUpperCorner) {
        super(height, width, parent, leftUpperCorner);
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    /** This method returns the position of the FileBufferView
     * @return: int
     */
    public int getPosition() {
        return this.position;
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    abstract String[] makeShow();

    /** This method shows the content of the FileBufferView and the updated scrollbars
     * @return: void
     */
    public void show(){
        String[] toShow = makeShow();
        for(int i=0; i<toShow.length; i++){
            terminalHandler.printText(getLeftUpperCorner().getX() + i,
                    getLeftUpperCorner().getY(), toShow[i]);
        }
    }


    @Override
    public void initViewPosition(int i) {
        setPosition(i);
    }

    /** This method returns the focused view at the given index i
     * @return: FileBufferView || null
     */
    @Override
    public View getFocusedView(int i) {
        if (getPosition() == i) {
            return this;
        }
        return null;
    }

    /** This method returns the number of views
     * @return: int
     */
    @Override
    public int countViews() {
        return 1;
    }

    /** This method updates the size of the layout to the given parameters heigth, width and leftUpperCorner
     * and updates the scroll states
     * @post getHeigth() == heigth
     * @post getWidth() == width
     * @post getLeftUpperCorner() == leftUpperCorner
     * @return: void
     */
    @Override
    public void updateSize(int heigth, int width, Point leftUpperCorner) {
        setHeigth(heigth);
        setWidth(width);
        setLeftUpperCorner(leftUpperCorner);
    }
}
