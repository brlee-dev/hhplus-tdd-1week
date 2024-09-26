package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointServiceInquiryTest {

    private PointService pointService;

    @Before
    public void setUp() {
        UserPointTable userPointTable = new UserPointTable();
        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    /** 포인트 조회 테스트: 포인트가 있는 경우 */
    @Test
    public void testGetPoint_WithBalance() {
        // given
        long userId = 1L;
        pointService.chargePoint(userId, 1_000L);

        // when
        UserPoint userPoint = pointService.getPoint(userId);

        // then
        assertEquals(1_000L, userPoint.amount());
    }

    /** 포인트 조회 테스트: 포인트가 없는 경우 */
    @Test
    public void testGetPoint_NoBalance() {
        // given
        long userId = 2L;

        // when
        UserPoint userPoint = pointService.getPoint(userId);

        // then
        assertEquals(0L, userPoint.amount());
    }
}


