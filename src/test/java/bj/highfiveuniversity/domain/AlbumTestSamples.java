package bj.highfiveuniversity.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AlbumTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Album getAlbumSample1() {
        return new Album().id(1L).ref("ref1").author("author1").title("title1").description("description1").duration(1);
    }

    public static Album getAlbumSample2() {
        return new Album().id(2L).ref("ref2").author("author2").title("title2").description("description2").duration(2);
    }

    public static Album getAlbumRandomSampleGenerator() {
        return new Album()
            .id(longCount.incrementAndGet())
            .ref(UUID.randomUUID().toString())
            .author(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .duration(intCount.incrementAndGet());
    }
}
