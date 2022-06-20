

package com.whaleal.icefrog.collections;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for {@link SetUtil#union}, {@link SetUtil#intersection} and {@link SetUtil#difference}.
 *
 *
 */

public class SetOperationsTest extends TestCase {
  @Test
  public void test(){
  }
   // suite


  public static class MoreTests extends TestCase {
    Set<String> friends;
    Set<String> enemies;

    @Override
    public void setUp() {
      friends = SetUtil.newHashSet("Tom", "Joe", "Dave");
      enemies = SetUtil.newHashSet("Dick", "Harry", "Tom");
    }

    public void testUnion() {
      Set<String> all = SetUtil.union(friends, enemies);
      assertEquals(5, all.size());

      ImmutableSet<String> immut = SetUtil.union(friends, enemies).immutableCopy();
      HashSet<String> mut = SetUtil.union(friends, enemies).copyInto(new HashSet<String>());

      enemies.add("Buck");
      assertEquals(6, all.size());
      assertEquals(5, immut.size());
      assertEquals(5, mut.size());
    }

    public void testIntersection() {
      Set<String> friends = SetUtil.newHashSet("Tom", "Joe", "Dave");
      Set<String> enemies = SetUtil.newHashSet("Dick", "Harry", "Tom");

      Set<String> frenemies = SetUtil.intersection(friends, enemies);
      assertEquals(1, frenemies.size());

      ImmutableSet<String> immut = SetUtil.intersection(friends, enemies).immutableCopy();
      HashSet<String> mut = SetUtil.intersection(friends, enemies).copyInto(new HashSet<String>());

      enemies.add("Joe");
      assertEquals(2, frenemies.size());
      assertEquals(1, immut.size());
      assertEquals(1, mut.size());
    }

    public void testDifference() {
      Set<String> friends = SetUtil.newHashSet("Tom", "Joe", "Dave");
      Set<String> enemies = SetUtil.newHashSet("Dick", "Harry", "Tom");

      Set<String> goodFriends = SetUtil.difference(friends, enemies);
      assertEquals(2, goodFriends.size());

      ImmutableSet<String> immut = SetUtil.difference(friends, enemies).immutableCopy();
      HashSet<String> mut = SetUtil.difference(friends, enemies).copyInto(new HashSet<String>());

      enemies.add("Dave");
      assertEquals(1, goodFriends.size());
      assertEquals(2, immut.size());
      assertEquals(2, mut.size());
    }

    public void testSymmetricDifference() {
      Set<String> friends = SetUtil.newHashSet("Tom", "Joe", "Dave");
      Set<String> enemies = SetUtil.newHashSet("Dick", "Harry", "Tom");

      Set<String> symmetricDifferenceFriendsFirst = SetUtil.symmetricDifference(friends, enemies);
      assertEquals(4, symmetricDifferenceFriendsFirst.size());

      Set<String> symmetricDifferenceEnemiesFirst = SetUtil.symmetricDifference(enemies, friends);
      assertEquals(4, symmetricDifferenceEnemiesFirst.size());

      assertEquals(symmetricDifferenceFriendsFirst, symmetricDifferenceEnemiesFirst);

      ImmutableSet<String> immut = SetUtil.symmetricDifference(friends, enemies).immutableCopy();
      HashSet<String> mut =
          SetUtil.symmetricDifference(friends, enemies).copyInto(new HashSet<String>());

      enemies.add("Dave");
      assertEquals(3, symmetricDifferenceFriendsFirst.size());
      assertEquals(4, immut.size());
      assertEquals(4, mut.size());

      immut = SetUtil.symmetricDifference(enemies, friends).immutableCopy();
      mut = SetUtil.symmetricDifference(enemies, friends).copyInto(new HashSet<String>());
      friends.add("Harry");
      assertEquals(2, symmetricDifferenceEnemiesFirst.size());
      assertEquals(3, immut.size());
      assertEquals(3, mut.size());
    }
  }
}
