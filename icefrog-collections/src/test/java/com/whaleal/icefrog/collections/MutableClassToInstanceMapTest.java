

package com.whaleal.icefrog.collections;



import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test of {@link MutableClassToInstanceMap}.
 *
 *
 */
public class MutableClassToInstanceMapTest extends TestCase {


  private ClassToInstanceMap<Number> map;

  @Override
  protected void setUp() throws Exception {
    map = MutableClassToInstanceMap.create();
  }

  public void testConstraint() {

    /**
     * We'll give ourselves a pass on testing all the possible ways of breaking the constraint,
     * because we know that newClassMap() is implemented using ConstrainedMap which is itself
     * well-tested. A purist would object to this, but what can I say, we're dirty cheaters.
     */
    map.put(Integer.class, new Integer(5));
    try {
      map.put(Double.class, new Long(42));
      fail();
    } catch (ClassCastException expected) {
    }
    // Won't compile: map.put(String.class, "x");
  }

  public void testPutAndGetInstance() {
    assertNull(map.putInstance(Integer.class, new Integer(5)));

    Integer oldValue = map.putInstance(Integer.class, new Integer(7));
    assertEquals(5, (int) oldValue);

    Integer newValue = map.getInstance(Integer.class);
    assertEquals(7, (int) newValue);

    // Won't compile: map.putInstance(Double.class, new Long(42));
  }

  public void testNull() {
    try {
      map.put(null, new Integer(1));
      fail();
    } catch (NullPointerException expected) {
    }
    map.putInstance(Integer.class, null);
    assertNull(map.get(Integer.class));
    assertNull(map.getInstance(Integer.class));

    map.put(Long.class, null);
    assertNull(map.get(Long.class));
    assertNull(map.getInstance(Long.class));
  }

  public void testPrimitiveAndWrapper() {
    assertNull(map.getInstance(int.class));
    assertNull(map.getInstance(Integer.class));

    assertNull(map.putInstance(int.class, 0));
    assertNull(map.putInstance(Integer.class, 1));
    assertEquals(2, map.size());

    assertEquals(0, (int) map.getInstance(int.class));
    assertEquals(1, (int) map.getInstance(Integer.class));

    assertEquals(0, (int) map.putInstance(int.class, null));
    assertEquals(1, (int) map.putInstance(Integer.class, null));

    assertNull(map.getInstance(int.class));
    assertNull(map.getInstance(Integer.class));
    assertEquals(2, map.size());
  }
}
