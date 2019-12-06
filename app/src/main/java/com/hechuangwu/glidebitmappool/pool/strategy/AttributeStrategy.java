package com.hechuangwu.glidebitmappool.pool.strategy;

import android.graphics.Bitmap;

import com.hechuangwu.glidebitmappool.pool.Util;
import com.hechuangwu.glidebitmappool.pool.inter.LruPoolStrategy;
import com.hechuangwu.glidebitmappool.pool.inter.Poolable;

/**
 * Created by cwh on 2019/12/5 0005.
 * 功能:3.0以上4.4以下的处理，宽高和像素都要一致
 */
public class AttributeStrategy implements LruPoolStrategy {
    private final KeyPool keyPool = new KeyPool();
    private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap<>();
    @Override
    public void put(Bitmap bitmap) {
        final Key key = keyPool.get(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        groupedMap.put(key, bitmap);
    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        final Key key = keyPool.get(width, height, config);
        return groupedMap.get(key);
    }

    @Override
    public Bitmap removeLast() {
        return groupedMap.removeLast();
    }

    @Override
    public String logBitmap(Bitmap bitmap) {
        return getBitmapString(bitmap);
    }

    @Override
    public String logBitmap(int width, int height, Bitmap.Config config) {
        return getBitmapString(width, height, config);
    }

    @Override
    public int getSize(Bitmap bitmap) {
        return Util.getBitmapByteSize(bitmap);
    }



    @Override
    public String toString() {
        return "AttributeStrategy:\n  " + groupedMap;
    }



    static class KeyPool extends BaseKeyPool<Key> {
        public Key get(int width, int height, Bitmap.Config config) {
            Key result = get();
            result.init(width, height, config);
            return result;
        }

        @Override
        protected Key create() {
            return new Key(this);
        }
    }

    static class Key implements Poolable {
        private final KeyPool pool;
        private int width;
        private int height;
        private Bitmap.Config config;

        public Key(KeyPool pool) {
            this.pool = pool;
        }

        public void init(int width, int height, Bitmap.Config config) {
            this.width = width;
            this.height = height;
            this.config = config;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Key) {
                Key other = (Key) o;
                return width == other.width && height == other.height && config == other.config;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = width;
            result = 31 * result + height;
            result = 31 * result + (config != null ? config.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return getBitmapString(width, height, config);
        }

        @Override
        public void offer() {
            pool.offer(this);
        }
    }


    private static String getBitmapString(Bitmap bitmap) {
        return getBitmapString(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    }

    private static String getBitmapString(int width, int height, Bitmap.Config config) {
        return "[" + width + "x" + height + "], " + config;
    }
}
