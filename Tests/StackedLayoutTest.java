import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StackedLayoutTest {
    @Test
    void stackedLayoutTest() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        sl1.initViewPosition(1);
        assertEquals(sl1.getSubLayouts()[0], fbv1);
        assertEquals(sl1.getSubLayouts()[1], fbv2);
        assertEquals(fbv1.getPosition(), 1);
        assertEquals(fbv2.getPosition(), 2);
        // Tests voor update size (+ calcSubSize + calcLeftUpperCorner)
        sl1.updateSize(10,20, new Point(1,1));
        assertEquals(sl1.getHeigth(), 10);
        assertEquals(sl1.getWidth(), 20);
        assertEquals(sl1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv1.getHeigth(), 5);
        assertEquals(fbv1.getWidth(), 20);
        assertEquals(fbv1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv2.getHeigth(), 5);
        assertEquals(fbv2.getWidth(), 20);
        assertEquals(fbv2.getLeftUpperCorner(), new Point(6,1));
        assertEquals(sl1.calcSubSize(), new Point(5,20));
    }

    @Test
    void testRotateSiblings() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        FileBuffer f3 = new FileBuffer(new String[] {"dit", "is", "een", "test" }, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2, fbv3});
        sl1.initViewPosition(1);
        StackedLayout result = sl1.rotateSiblings(1, 2, 3, sl1);
        assertEquals(result.getSubLayouts()[0], fbv1);
        assertEquals(result.getSubLayouts()[1].getClass(), SideBySideLayout.class);
        SideBySideLayout sbsl1 = (SideBySideLayout) result.getSubLayouts()[1];
        assertEquals(sbsl1.getSubLayouts()[0], fbv2);
        assertEquals(sbsl1.getSubLayouts()[1], fbv3);
        StackedLayout sl2 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2, fbv3});
        sl2.initViewPosition(1);
        StackedLayout result2 = sl2.rotateSiblings(-1, 1, 2, sl2);
        assertEquals(result2.getSubLayouts()[0].getClass(), SideBySideLayout.class);
        SideBySideLayout sbsl2 = (SideBySideLayout) result2.getSubLayouts()[0];
        assertEquals(sbsl2.getSubLayouts()[0], fbv2);
        assertEquals(sbsl2.getSubLayouts()[1], fbv1);
        assertEquals(result2.getSubLayouts()[1], fbv3);
        FileBuffer f4 = new FileBuffer(new String[] {""}, "4");
        FileBuffer f5 = new FileBuffer(new String[] {""}, "5");
        FileBufferView fbv4 = new FileBufferView(1, 1, new Point(1,1), f4);
        FileBufferView fbv5 = new FileBufferView(1, 1, new Point(1,1), f5);
        StackedLayout sl3 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv3, fbv4, fbv5});
        SideBySideLayout sbsl3 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {fbv1, fbv2, sl3});
        sbsl3.initViewPosition(1);
        SideBySideLayout result3 = sbsl3.rotateSiblings(1, 3, 4, sl3);
        assertEquals(result3.getSubLayouts()[0], fbv1);
        assertEquals(result3.getSubLayouts()[1], fbv2);
        assertEquals(result3.getSubLayouts()[2].getClass(), StackedLayout.class);
        StackedLayout subLay = (StackedLayout) result3.getSubLayouts()[2];
        assertEquals(subLay.getSubLayouts()[1], fbv5);
        assertEquals(subLay.getSubLayouts()[0].getClass(), SideBySideLayout.class);
        SideBySideLayout subSubLay = (SideBySideLayout) subLay.getSubLayouts()[0];
        assertEquals(subSubLay.getSubLayouts()[0], fbv3);
        assertEquals(subSubLay.getSubLayouts()[1], fbv4);
    }

    @Test
    void testRotateSiblingsFlip() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        FileBuffer f3 = new FileBuffer(new String[] {"dit", "is", "een", "test" }, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        FileBuffer f4 = new FileBuffer(new String[] {""}, "4");
        FileBufferView fbv4 = new FileBufferView(1, 1, new Point(1, 1), f4);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv1, fbv2});
        sl1.initViewPosition(1);
        CompositeLayout result1 = sl1.rotateSiblingsFlip(1, 1, 2, sl1);
        assertEquals(result1.getClass(), SideBySideLayout.class);
        assertEquals(result1.getSubLayouts()[0], fbv1);
        assertEquals(result1.getSubLayouts()[1], fbv2);
        StackedLayout SideBySidel2 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv3, fbv4});
        result1.getSubLayouts()[1] = SideBySidel2;
        result1.initViewPosition(1);
        CompositeLayout result2 = result1.rotateSiblingsFlip(-1, 2, 3, SideBySidel2);
        assertEquals(result2.getClass(), SideBySideLayout.class);
        assertEquals(result2.countSubLayouts(), 3);
        assertEquals(result2.getSubLayouts()[0], fbv1);
        assertEquals(result2.getSubLayouts()[1], fbv3);
        assertEquals(result2.getSubLayouts()[2], fbv4);
        StackedLayout sub1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv1, fbv2});
        StackedLayout sub2 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv3, fbv4});
        SideBySideLayout Stacked3 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {sub1, sub2});
        Stacked3.initViewPosition(1);
        CompositeLayout result3 = Stacked3.rotateSiblingsFlip(1, 1,2, sub1);
        assertEquals(result3.countSubLayouts(), 3);
        assertEquals(result3.getSubLayouts()[0], fbv2);
        assertEquals(result3.getSubLayouts()[1], fbv1);
        assertEquals(result3.getSubLayouts()[2].getClass(), StackedLayout.class);
        StackedLayout sub = (StackedLayout) result3.getSubLayouts()[2];
        assertEquals(sub.getSubLayouts()[0], fbv3);
        assertEquals(sub.getSubLayouts()[1], fbv4);
    }

    @Test
    void testRotateNonSiblings() {

    }

    @Test
    void testRotateNonSiblingsPromote() {

    }

    @Test
    void testOpenNewGame() {

    }

    @Test
    void testCalcSubSize() {

    }

    @Test
    void testCalcLeftUpperCorner() {

    }
}