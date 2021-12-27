package com.whaleal.icefrog.core.lang.fruits;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Fruits<br>
 * 该类中定义了多个水果的字符串信息
 *
 * @author lhp
 */
public class Fruits {
    /**
     * 随机种子
     */
    private final Random _seed;
    /**
     * 水果集合
     */
    private final List<String> _collection;

    public Fruits() {
        this._collection = Arrays.asList("apple", "apricot", "avocado", "banana", "breadfruit", "bilberry", "blackberry", "blackcurrant", "blueberry", "boysenberry", "cantaloupe", "currant", "cherry", "cherimoya", "cloudberry", "coconut", "cranberry", "cucumber", "damson", "date", "dragonfruit", "durian", "eggplant", "elderberry", "feijoa", "fig", "goji.berry", "gooseberry", "grape", "raisin", "grapefruit", "guava", "huckleberry", "honeydew", "jackfruit", "jambul", "jujube", "kiwi.fruit", "kumquat", "lemon", "lime", "loquat", "lychee", "mango", "marion.berry", "melon", "cantaloupe", "honeydew", "watermelon", "rock.melon", "miracle.fruit", "mulberry", "nectarine", "nut", "olive", "orange", "clementine", "mandarine", "blood.orange", "tangerine", "papaya", "passionfruit", "peach", "pepper", "chili.pepper", "bell.pepper", "pear", "williams.pear.or.bartlett.pear", "persimmon", "physalis", "pineapple", "pomegranate", "pomelo", "mangosteen", "quince", "raspberry", "western.raspberry", "rambutan", "redcurrant", "salal.berry", "salmon.berry", "satsuma", "star.fruit", "strawberry", "tamarillo", "tomato", "ugli.fruit", "watermelon");
        this._seed = new Random();
        // 使用指定的随机源随机排列指定的列表
        Collections.shuffle(this._collection, this._seed);
    }

    public Fruits(Random pRnd) {
        this._collection = Arrays.asList("apple", "apricot", "avocado", "banana", "breadfruit", "bilberry", "blackberry", "blackcurrant", "blueberry", "boysenberry",
                "cantaloupe", "currant", "cherry", "cherimoya", "cloudberry", "coconut", "cranberry", "cucumber", "damson", "date",
                "dragonfruit", "durian", "eggplant", "elderberry", "feijoa", "fig", "goji.berry", "gooseberry", "grape", "raisin",
                "grapefruit", "guava", "huckleberry", "honeydew", "jackfruit", "jambul", "jujube", "kiwi.fruit", "kumquat", "lemon",
                "lime", "loquat", "lychee", "mango", "marion.berry", "melon", "cantaloupe", "honeydew", "watermelon", "rock.melon",
                "miracle.fruit", "mulberry", "nectarine", "nut", "olive", "orange", "clementine", "mandarine", "blood.orange", "tangerine",
                "papaya", "passionfruit", "peach", "pepper", "chili.pepper", "bell.pepper", "pear", "williams.pear.or.bartlett.pear", "persimmon", "physalis",
                "pineapple", "pomegranate", "pomelo", "mangosteen", "quince", "raspberry", "western.raspberry", "rambutan", "redcurrant", "salal.berry",
                "salmon.berry", "satsuma", "star.fruit", "strawberry", "tamarillo", "tomato", "ugli.fruit", "watermelon");
        this._seed = pRnd;
        // 使用指定的随机源随机排列指定的列表
        Collections.shuffle(this._collection, this._seed);
    }



    int getCollectionSize() {
        return this._collection.size();
    }


    List<String> getCollection() {
        return this._collection;
    }
}

