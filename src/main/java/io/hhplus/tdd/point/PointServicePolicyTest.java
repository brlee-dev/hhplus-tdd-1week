package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointServicePolicyTest {

    private PointService pointService;

    @Before
    public void setUp() {
        UserPointTable userPointTable = new UserPointTable();
        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    /** 최대 포인트 한도 초과 테스트 */
    @Test(expected = IllegalArgumentException.class)
    public void testChargePoint_ExceedMaxLimit() {
        // given
        long userId = 1L;
        long initialAmount = 90_000L;
        pointService.chargePoint(userId, initialAmount);

        // when
        pointService.chargePoint(userId, 20_000L);

        // then
        // 예외가 발생하므로 아래 코드는 실행되지 않습니다.
    }

    /** 잔액 부족 시 포인트 사용 실패 테스트 */
    @Test(expected = IllegalArgumentException.class)
    public void testUsePoint_InsufficientBalance() {
        // given
        long userId = 1L;
        long useAmount = 10_000L;

        // when
        pointService.usePoint(userId, useAmount);

        // then
        // 예외가 발생하므로 아래 코드는 실행되지 않습니다.
    }

    /** 정상적인 포인트 충전 및 사용 테스트 */
    @Test
    public void testChargeAndUsePoint_Success() {
        // given
        long userId = 1L;
        pointService.chargePoint(userId, 50_000L);

        // when
        pointService.usePoint(userId, 20_000L);
        UserPoint userPoint = pointService.getPoint(userId);

        // then
        assertEquals(30_000L, userPoint.amount());
    }
}


