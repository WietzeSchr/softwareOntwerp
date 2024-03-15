import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.floor;

public class StackedLayout extends CompositeLayout {
    public StackedLayout(int height, int width, Point leftUpperCorner, int subLaysCount) {
        super(height, width, leftUpperCorner, subLaysCount);
    }

    public StackedLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) {
        super(height, width, leftUpperCorner, filepaths, newLine);
    }

    public StackedLayout(int heigth, int width, Point leftUpperCorner, Layout[] subLayouts) {
        super(heigth, width, leftUpperCorner, subLayouts);
    }

    public Point calcSubSize() {
        return new Point((int) Math.floor((float) getHeigth() / (float) countSubLayouts()), getWidth());
    }


    public Point calcLeftUpCorner(int i) {
        int subHeight = (int) floor((float) getHeigth() / (float) countSubLayouts());
        return new Point((int) (getLeftUpperCorner().getX() + i * subHeight), (int) getLeftUpperCorner().getY());
    }

    @Override
    public CompositeLayout rotateView(int dir, CompositeLayout parent, int focus, int nextFocus) {
        if (this == parent) {
            FileBufferView focused = getFocusedView(focus);
            FileBufferView next = getFocusedView(nextFocus);
            Layout[] newSubLays = new Layout[getSubLayouts().length - 1];
            Layout[] subSubLays = new Layout[2];
            if (dir == 1) {
                subSubLays[0] = focused;
                subSubLays[1] = next;
            } else if(dir == -1){
                subSubLays[0] = next;
                subSubLays[1] = focused;
            }
            SideBySideLayout sbs = new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), subSubLays);
            int i=0;
            int j=0;
            Layout[] temp = new Layout[newSubLays.length];
            while (i<getSubLayouts().length) {
                if(getSubLayouts()[i] != next) {
                    temp[j++] = getSubLayouts()[i];
                }
                i++;
            }
            setSubLayouts(temp);
            for(int k=0; k<newSubLays.length; k++) {
                if (getSubLayouts()[k] == focused) newSubLays[k] = sbs;
                else newSubLays[k] = getSubLayouts()[k];
            }
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLays);
        } else {
            Layout[] newSubLay = new Layout[getSubLayouts().length];
            for (int k=0; k<newSubLay.length; k++){
                newSubLay[k] = getSubLayouts()[k].rotateView(dir, parent ,focus, nextFocus);
            }
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLay);
        }
    }
}
