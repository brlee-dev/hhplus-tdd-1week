package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class PointServiceConcurrencyTest {

    private PointService pointService;

    @Before
    public void setUp() {
        UserPointTable userPointTable = new UserPointTable();
        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    /** 동시성 통합 테스트: 여러 유저의 포인트 충전 및 사용 */
    @Test
    public void testConcurrentOperations() throws InterruptedException {
        // given
        long userId1 = 1L;
        long userId2 = 2L;
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        // when
        CountDownLatch latch = new CountDownLatch(numberOfThreads * 2);

        // 유저 1에 대한 작업
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                pointService.chargePoint(userId1, 100L);
                latch.countDown();
            });
            executorService.execute(() -> {
                pointService.usePoint(userId1, 50L);
                latch.countDown();
            });
        }

        // 유저 2에 대한 작업
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                pointService.chargePoint(userId2, 200L);
                latch.countDown();
            });
            executorService.execute(() -> {
                pointService.usePoint(userId2, 150L);
                latch.countDown();
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        UserPoint userPoint1 = pointService.getPoint(userId1);
        UserPoint userPoint2 = pointService.getPoint(userId2);

        // 유저 1의 최종 포인트는 (100 - 50) * 100 = 5,000
        assertEquals(5_000L, userPoint1.amount());

        // 유저 2의 최종 포인트는 (200 - 150) * 100 = 5,000
        assertEquals(5_000L, userPoint2.amount());
    }

    /** 동시성 통합 테스트: 잔액 부족 상황에서의 포인트 사용 */
    @Test
    public void testConcurrentUseWithInsufficientBalance() throws InterruptedException {
        // given
        long userId = 1L;
        pointService.chargePoint(userId, 10_000L);

        int numberOfThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // when
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger failedCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    pointService.usePoint(userId, 500L);
                } catch (IllegalArgumentException e) {
                    failedCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        UserPoint userPoint = pointService.getPoint(userId);
        long expectedUsed = (numberOfThreads - failedCount.get()) * 500L;
        long expectedBalance = 10_000L - expectedUsed;

        assertEquals(expectedBalance, userPoint.amount());
    }
}
