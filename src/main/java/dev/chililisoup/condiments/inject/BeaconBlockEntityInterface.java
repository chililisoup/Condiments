package dev.chililisoup.condiments.inject;

public interface BeaconBlockEntityInterface {
    default int condiments$getTopY() {
        throw new AssertionError();
    }

    default void condiments$setTopY(int topY) {
        throw new AssertionError();
    }
}
