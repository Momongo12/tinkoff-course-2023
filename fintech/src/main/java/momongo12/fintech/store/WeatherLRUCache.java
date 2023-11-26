package momongo12.fintech.store;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import momongo12.fintech.store.entities.Weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author momongo12
 * @version 1.0
 */
@Component
@Log4j2
public class WeatherLRUCache {

    @Setter
    @Getter
    @Value("${cache.course.size:100}")
    private int maxCacheSize;

    @Setter
    @Getter
    @Value("${cache.course.expiry-time-in-seconds:900}")
    private long expireTimeInSeconds;

    private final Map<String, ListNode> cache;
    private final DoubleLinkedList linkedList;
    private final ReentrantLock lock = new ReentrantLock();
    private ListNode head;
    private ListNode tail;

    public WeatherLRUCache() {
        cache = new ConcurrentHashMap<>();
        linkedList = new DoubleLinkedList();
        head = new ListNode();
        tail = new ListNode();
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Retrieves weather information for the specified region from the cache.
     *
     * @param regionName The name of the region for which weather information is requested.
     * @return An Optional containing the weather information if present, otherwise an empty Optional.
     */
    public Optional<Weather> get(String regionName) {
        lock.lock();
        try {
            ListNode listNode = cache.get(regionName);

            if (listNode != null) {
                linkedList.moveToHead(listNode);
                return Optional.of(listNode.weather);
            }
        } finally {
            lock.unlock();
        }

        return Optional.empty();
    }

    /**
     * Puts weather information into the cache for the specified region.
     * If the region already exists in the cache, it updates the weather information and moves the region to the most recently used position.
     * If the cache is at its maximum size, it removes the least recently used weather data before adding the new data.
     *
     * @param regionName The name of the region for which weather information is stored.
     * @param weather The Weather object containing the weather information for the specified region.
     */
    public void put(String regionName, Weather weather) {
        lock.lock();

        try {
            ListNode listNode = cache.get(regionName);

            if (listNode != null) {
                listNode.weather = weather;

                linkedList.moveToHead(listNode);
            } else {
                if (cache.size() == maxCacheSize) {
                    removeLeastRecentUsedWeather();
                }

                ListNode newNode = new ListNode(regionName, weather);
                linkedList.addToFront(newNode);
                cache.put(regionName, newNode);
            }
        } finally {
            lock.unlock();
        }
    }

    private void removeLeastRecentUsedWeather() {
        ListNode tail = linkedList.getTail();
        cache.remove(tail.prev.regionName);
        linkedList.removeNode(tail.prev);
    }


    @Scheduled(fixedDelayString = "${cache.course.expiry-time-in-seconds:900}000")
    private void removeExpiredWeatherData() {
        lock.lock();
        try {
            Instant currentTime = Instant.now();

            for (ListNode currentNode = linkedList.getHead().next; currentNode != linkedList.getTail(); currentNode = currentNode.next) {
                Instant weatherMeasuringDate = currentNode.weather.getMeasuringDate();
                if (currentTime.compareTo(weatherMeasuringDate.plusSeconds(expireTimeInSeconds)) >= 0) {
                    linkedList.removeNode(currentNode);
                    cache.remove(currentNode.regionName);
                }
            }
        } finally {
            lock.unlock();
        }

    }

    @Getter
    private static class DoubleLinkedList {

        private ListNode head;
        private ListNode tail;

        public DoubleLinkedList() {
            head = new ListNode();
            tail = new ListNode();
            head.next = tail;
            tail.prev = head;
        }

        public synchronized void addToFront(ListNode listNode) {
            ListNode headNext = head.next;

            head.next = listNode;
            listNode.prev = head;
            headNext.prev = listNode;
            listNode.next = headNext;
        }

        public synchronized void removeNode(ListNode listNode) {
            ListNode prev = listNode.prev;

            listNode.next.prev = prev;
            prev.next = listNode.next;
        }

        public synchronized void moveToHead(ListNode listNode) {
            removeNode(listNode);
            addToFront(listNode);
        }
    }

    @NoArgsConstructor
    private static class ListNode {
        String regionName;
        Weather weather;
        ListNode prev;
        ListNode next;

        ListNode(String regionName, Weather weather) {
            this.regionName = regionName;
            this.weather = weather;
        }
    }
}
