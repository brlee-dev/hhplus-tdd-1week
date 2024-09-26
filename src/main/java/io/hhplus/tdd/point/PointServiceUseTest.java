package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointServiceUseTest {

    private PointService pointService;

    @Before
    public void setUp() {
        UserPointTable userPointTable = new UserPointTable();
        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(userPointTable, pointHistoryTable);

        // 초기 포인트 충전
        pointService.chargePoint(1L, 1_000L);
    }

    /** 포인트 사용 성공 테스트 */
    @Test
    public void testUsePoint_Success() {
        // given
        long userId = 1L;
        long amount = 500L;

        // when
        UserPoint userPoint = pointService.usePoint(userId, amount);

        // then
        assertEquals(500L, userPoint.amount());
    }

    /** 잔고 부족 시 포인트 사용 실패 테스트 */
    @Test(expected = IllegalArgumentException.class)
    public void testUsePoint_InsufficientBalance() {
        // given
        long userId = 1L;
        long amount = 2_000L;

        // when
        pointService.usePoint(userId, amount);
    }
}


