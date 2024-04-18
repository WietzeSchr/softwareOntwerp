import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static org.junit.jupiter.api.Assertions.*;

class StackedLayoutTest {

    @Test
    void testRotateSiblings() {
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        FileBuffer f3 = new FileBuffer(new String[]{"dit", "is", "een", "test"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[]{fbv1, fbv2, fbv3});
        sl1.initViewPosition(1);
        StackedLayout result = sl1.rotateSiblings(1, 2, 3, sl1);
        assertEquals(result.getSubLayouts()[0], fbv1);
        assertEquals(result.getSubLayouts()[1].getClass(), SideBySideLayout.class);
        SideBySideLayout sbsl1 = (SideBySideLayout) result.getSubLayouts()[1];
        assertEquals(sbsl1.getSubLayouts()[0], fbv2);
        assertEquals(sbsl1.getSubLayouts()[1], fbv3);
        StackedLayout sl2 = new StackedLayout(1, 1, new Point(1, 1), new Layout[]{fbv1, fbv2, fbv3});
        sl2.initViewPosition(1);
        StackedLayout result2 = sl2.rotateSiblings(-1, 1, 2, sl2);
        assertEquals(result2.getSubLayouts()[0].getClass(), SideBySideLayout.class);
        SideBySideLayout sbsl2 = (SideBySideLayout) result2.getSubLayouts()[0];
        assertEquals(sbsl2.getSubLayouts()[0], fbv2);
        assertEquals(sbsl2.getSubLayouts()[1], fbv1);
        assertEquals(result2.getSubLayouts()[1], fbv3);
        FileBuffer f4 = new FileBuffer(new String[]{""}, "4");
        FileBuffer f5 = new FileBuffer(new String[]{""}, "5");
        FileBufferView fbv4 = new FileBufferView(1, 1, new Point(1, 1), f4);
        FileBufferView fbv5 = new FileBufferView(1, 1, new Point(1, 1), f5);
        StackedLayout sl3 = new StackedLayout(1, 1, new Point(1, 1), new Layout[]{fbv3, fbv4, fbv5});
        SideBySideLayout sbsl3 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[]{fbv1, fbv2, sl3});
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
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        FileBuffer f3 = new FileBuffer(new String[]{"dit", "is", "een", "test"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        FileBuffer f4 = new FileBuffer(new String[]{""}, "4");
        FileBufferView fbv4 = new FileBufferView(1, 1, new Point(1, 1), f4);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[]{fbv1, fbv2});
        sl1.initViewPosition(1);
        CompositeLayout result1 = sl1.rotateSiblingsFlip(1, 1, 2, sl1);
        assertEquals(result1.getClass(), SideBySideLayout.class);
        assertEquals(result1.getSubLayouts()[0], fbv1);
        assertEquals(result1.getSubLayouts()[1], fbv2);
        StackedLayout sbsl2 = new StackedLayout(1, 1, new Point(1, 1), new Layout[]{fbv3, fbv4});
        result1.getSubLayouts()[1] = sbsl2;
        result1.initViewPosition(1);
        CompositeLayout result2 = result1.rotateSiblingsFlip(-1, 2, 3, sbsl2);
        assertEquals(result2.getClass(), SideBySideLayout.class);
        assertEquals(result2.countSubLayouts(), 3);
        assertEquals(result2.getSubLayouts()[0], fbv1);
        assertEquals(result2.getSubLayouts()[1], fbv3);
        assertEquals(result2.getSubLayouts()[2], fbv4);
        StackedLayout sub1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[]{fbv1, fbv2});
        StackedLayout sub2 = new StackedLayout(1, 1, new Point(1, 1), new Layout[]{fbv3, fbv4});
        SideBySideLayout Stacked3 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[]{sub1, sub2});
        Stacked3.initViewPosition(1);
        CompositeLayout result3 = Stacked3.rotateSiblingsFlip(1, 1, 2, sub1);
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
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        FileBuffer f3 = new FileBuffer(new String[]{"dit", "is", "een", "test"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        FileBuffer f4 = new FileBuffer(new String[]{""}, "4");
        FileBufferView fbv4 = new FileBufferView(1, 1, new Point(1, 1), f4);
        FileBuffer f5 = new FileBuffer(new String[] {}, "5");
        FileBufferView fbv5 = new FileBufferView(1, 1, new Point(1,1), f5);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv3,fbv4, fbv5});
        SideBySideLayout sbsl2 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {fbv2, sl1});
        StackedLayout sl3 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv1, sbsl2});
        sl3.initViewPosition(1);
        CompositeLayout result = sl3.rotateNonSiblings(1, 2, fbv3, sbsl2, sl1);
        assertEquals(result.getClass(), StackedLayout.class);
        assertEquals(result.getSubLayouts()[0], fbv1);
        assertEquals(result.getSubLayouts()[1].getClass(), SideBySideLayout.class);
        sbsl2 = (SideBySideLayout) result.getSubLayouts()[1];
        assertEquals(sbsl2.getSubLayouts()[0], fbv3);
        assertEquals(sbsl2.getSubLayouts()[1], fbv2);
        assertEquals(sbsl2.getSubLayouts()[2].getClass(), StackedLayout.class);
        assertEquals(sbsl2.countSubLayouts(), 3);
        sl1 = (StackedLayout) sbsl2.getSubLayouts()[2];
        assertEquals(sl1.countSubLayouts(), 2);
        assertEquals(sl1.getSubLayouts()[0], fbv4);
        assertEquals(sl1.getSubLayouts()[1], fbv5);
    }

    @Test
    void testRotateNonSiblingsPromote() {
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        FileBuffer f3 = new FileBuffer(new String[]{"dit", "is", "een", "test"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        FileBuffer f4 = new FileBuffer(new String[]{""}, "4");
        FileBufferView fbv4 = new FileBufferView(1, 1, new Point(1, 1), f4);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv1,fbv2});
        SideBySideLayout sbsl2 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {sl1, fbv3});
        StackedLayout sl3 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {sbsl2, fbv4});
        sl3.initViewPosition(1);
        CompositeLayout result1 = (CompositeLayout) sl3.rotateNonSiblingsPromote(1, 2, fbv3, sl1, sbsl2);
        assertEquals(result1.getClass(), StackedLayout.class);
        assertEquals(result1.getSubLayouts()[0].getClass(), SideBySideLayout.class);
        sbsl2 = (SideBySideLayout) result1.getSubLayouts()[0];
        assertArrayEquals(sbsl2.getSubLayouts(), new Layout[] {fbv1, fbv3, fbv2});
        assertEquals(result1.getSubLayouts()[1], fbv4);
        sl1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv3,fbv4});
        sbsl2 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {fbv2, sl1});
        sl3 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv1, sbsl2});
        sl3.initViewPosition(1);
        CompositeLayout result2 = (CompositeLayout) sl3.rotateNonSiblingsPromote(1, 2, fbv3, sbsl2, sl1);
        assertEquals(result2.getClass(), StackedLayout.class);
        assertEquals(result2.getSubLayouts()[1].getClass(), SideBySideLayout.class);
        sbsl2 = (SideBySideLayout) result2.getSubLayouts()[1];
        assertArrayEquals(sbsl2.getSubLayouts(), new Layout[] {fbv2, fbv3, fbv4});
        assertEquals(result2.getSubLayouts()[0], fbv1);
    }

    @Test
    void testInsertViews() {
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        FileBuffer f3 = new FileBuffer(new String[]{"dit", "is", "een", "test"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        StackedLayout sl1 = new StackedLayout(10, 15, new Point(1, 1), new Layout[]{fbv1, fbv2, fbv3});
        sl1.initViewPosition(1);
        FileBuffer f4 = new FileBuffer(new String[] {"4"}, "test4");
        FileBufferView fbv4 = new FileBufferView(1, 1, new Point(1, 1), f4);
        FileBuffer f5 = new FileBuffer(new String[] {}, "5");
        FileBufferView fbv5 = new FileBufferView(1, 1, new Point(1, 1), f5);
        CompositeLayout result1 = (CompositeLayout) sl1.insertViews(2, sl1, new View[] {fbv4, fbv5});
        CompositeLayout result2 = (CompositeLayout) sl1.insertViews(2, null, new View[] {fbv4});
        assertEquals(result1.getClass(), StackedLayout.class);
        assertEquals(result2.getClass(), StackedLayout.class);
        assertEquals(result1.countSubLayouts(), 3);
        assertEquals(result2.countSubLayouts(), 3);
        assertEquals(result1.getSubLayouts()[0], fbv1);
        assertEquals(result1.getSubLayouts()[1].getClass(), SideBySideLayout.class);
        assertEquals(result1.getSubLayouts()[2], fbv3);
        SideBySideLayout sbsl1 = (SideBySideLayout) result1.getSubLayouts()[1];
        assertEquals(sbsl1.countSubLayouts(), 3);
        assertEquals(sbsl1.getSubLayouts()[0], fbv2);
        assertEquals(sbsl1.getSubLayouts()[1], fbv4);
        assertEquals(sbsl1.getSubLayouts()[2], fbv5);
        assertEquals(result2.getSubLayouts()[0], fbv1);
        assertEquals(result2.getSubLayouts()[1].getClass(), SideBySideLayout.class);
        SideBySideLayout sbsl2 = (SideBySideLayout) result2.getSubLayouts()[1];
        assertEquals(sbsl2.countSubLayouts(), 2);
        assertEquals(sbsl2.getSubLayouts()[0], fbv2);
        assertEquals(sbsl2.getSubLayouts()[1], fbv4);
        assertEquals(result2.getSubLayouts()[2], fbv3);
    }

    @Test
    void testFlip() {
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        FileBuffer f3 = new FileBuffer(new String[]{"dit", "is", "een", "test"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        StackedLayout sl1 = new StackedLayout(10, 15, new Point(1, 1), new Layout[]{fbv1, fbv2, fbv3});
        SideBySideLayout result = sl1.flip();
        assertArrayEquals(result.getSubLayouts(), new Layout[] {fbv1, fbv2, fbv3});
        assertEquals(result.getHeigth(), 10);
        assertEquals(result.getWidth(), 15);
        assertEquals(result.getLeftUpperCorner(), new Point(1, 1));
    }

    @Test
    void testCalcSubSize() {
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        FileBuffer f3 = new FileBuffer(new String[]{"dit", "is", "een", "test"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        StackedLayout sl1 = new StackedLayout(15, 10, new Point(1, 1), new Layout[]{fbv1, fbv2, fbv3});
        assertEquals(sl1.calcSubSize(), new Point(5, 10));
        assertEquals(sl1.calcLeftUpCorner(0), new Point(1, 1));
        assertEquals(sl1.calcLeftUpCorner(1), new Point(6, 1));
        assertEquals(sl1.calcLeftUpCorner(2), new Point(11, 1));
    }
}