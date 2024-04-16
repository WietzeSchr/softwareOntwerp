import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SideBySideLayoutTest {
    @Test
    void sideBySideLayoutTest() {
        // Tests voor constructors
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        sbsl1.initViewPosition(1);
        assertEquals(sbsl1.getSubLayouts()[0], fbv1);
        assertEquals(sbsl1.getSubLayouts()[1], fbv2);
        assertEquals(fbv1.getPosition(), 1);
        assertEquals(fbv2.getPosition(), 2);
        // Tests voor update size (+ calcSubSize + calcLeftUpperCorner)
        sbsl1.updateSize(10,20, new Point(1,1));
        assertEquals(sbsl1.getHeigth(), 10);
        assertEquals(sbsl1.getWidth(), 20);
        assertEquals(sbsl1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv1.getHeigth(), 10);
        assertEquals(fbv1.getWidth(), 10);
        assertEquals(fbv1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv2.getHeigth(), 10);
        assertEquals(fbv2.getWidth(), 10);
        assertEquals(fbv2.getLeftUpperCorner(), new Point(1,11));
        assertEquals(sbsl1.calcSubSize(), new Point(10,10));
        // Tests voor rotateView

    }

    @Test
    void testRotateSiblings() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        FileBuffer f3 = new FileBuffer(new String[] {"dit", "is", "een", "test" }, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2, fbv3});
        sbsl1.initViewPosition(1);
        SideBySideLayout result = sbsl1.rotateSiblings(1, 2, 3, sbsl1);
        assertEquals(result.getSubLayouts()[0], fbv1);
        assertEquals(result.getSubLayouts()[1].getClass(), StackedLayout.class);
        StackedLayout sl1 = (StackedLayout) result.getSubLayouts()[1];
        assertEquals(sl1.getSubLayouts()[0], fbv2);
        assertEquals(sl1.getSubLayouts()[1], fbv3);
        SideBySideLayout sbsl2 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2, fbv3});
        sbsl2.initViewPosition(1);
        SideBySideLayout result2 = sbsl2.rotateSiblings(-1, 1, 2, sbsl2);
        assertEquals(result2.getSubLayouts()[0].getClass(), StackedLayout.class);
        StackedLayout sl2 = (StackedLayout) result2.getSubLayouts()[0];
        assertEquals(sl2.getSubLayouts()[0], fbv2);
        assertEquals(sl2.getSubLayouts()[1], fbv1);
        assertEquals(result2.getSubLayouts()[1], fbv3);
        FileBuffer f4 = new FileBuffer(new String[] {""}, "4");
        FileBuffer f5 = new FileBuffer(new String[] {""}, "5");
        FileBufferView fbv4 = new FileBufferView(1, 1, new Point(1,1), f4);
        FileBufferView fbv5 = new FileBufferView(1, 1, new Point(1,1), f5);
        SideBySideLayout sbsl3 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {fbv3, fbv4, fbv5});
        StackedLayout sl3 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv1, fbv2, sbsl3});
        sl3.initViewPosition(1);
        StackedLayout result3 = sl3.rotateSiblings(1, 3, 4, sbsl3);
        assertEquals(result3.getSubLayouts()[0], fbv1);
        assertEquals(result3.getSubLayouts()[1], fbv2);
        assertEquals(result3.getSubLayouts()[2].getClass(), SideBySideLayout.class);
        SideBySideLayout subLay = (SideBySideLayout) result3.getSubLayouts()[2];
        assertEquals(subLay.getSubLayouts()[1], fbv5);
        assertEquals(subLay.getSubLayouts()[0].getClass(), StackedLayout.class);
        StackedLayout subSubLay = (StackedLayout) subLay.getSubLayouts()[0];
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
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {fbv1, fbv2});
        sbsl1.initViewPosition(1);
        CompositeLayout result1 = sbsl1.rotateSiblingsFlip(1, 1, 2, sbsl1);
        assertEquals(result1.getClass(), StackedLayout.class);
        assertEquals(result1.getSubLayouts()[0], fbv2);
        assertEquals(result1.getSubLayouts()[1], fbv1);
        SideBySideLayout sbsl2 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {fbv3, fbv4});
        result1.getSubLayouts()[1] = sbsl2;
        result1.initViewPosition(1);
        CompositeLayout result2 = result1.rotateSiblingsFlip(-1, 2, 3, sbsl2);
        assertEquals(result2.getClass(), StackedLayout.class);
        assertEquals(result2.countSubLayouts(), 3);
        assertEquals(result2.getSubLayouts()[0], fbv2);
        assertEquals(result2.getSubLayouts()[1], fbv3);
        assertEquals(result2.getSubLayouts()[2], fbv4);
        SideBySideLayout sub1 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {fbv1, fbv2});
        SideBySideLayout sub2 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {fbv3, fbv4});
        StackedLayout sl3 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {sub1, sub2});
        sl3.initViewPosition(1);
        CompositeLayout result3 = sl3.rotateSiblingsFlip(1, 1,2, sub1);
        assertEquals(result3.countSubLayouts(), 3);
        assertEquals(result3.getSubLayouts()[0], fbv2);
        assertEquals(result3.getSubLayouts()[1], fbv1);
        assertEquals(result3.getSubLayouts()[2].getClass(), SideBySideLayout.class);
        SideBySideLayout sub = (SideBySideLayout) result3.getSubLayouts()[2];
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