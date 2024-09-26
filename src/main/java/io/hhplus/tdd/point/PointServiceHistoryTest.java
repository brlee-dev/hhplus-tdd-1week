package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PointServiceHistoryTest {

    private PointService pointService;

    @Before
    public void setUp() {
        UserPointTable userPointTable = new UserPointTable();
        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    /** 포인트 내역 조회 테스트: 거래 내역이 있는 경우 */
    @Test
    public void testGetPointHistories_WithHistory() {
        // given
        long userId = 1L;
        pointService.chargePoint(userId, 1_000L);
        pointService.usePoint(userId, 500L);

        // when
        List<PointHistory> histories = pointService.getPointHistories(userId);

        // then
        assertEquals(2, histories.size());
        assertEquals(TransactionType.CHARGE, histories.get(0).type());
        assertEquals(TransactionType.USE, histories.get(1).type());
    }

    /** 포인트 내역 조회 테스트: 거래 내역이 없는 경우 */
    @Test
    public void testGetPointHistories_NoHistory() {
        // given
        long userId = 2L;

        // when
        List<PointHistory> histories = pointService.getPointHistories(userId);

        // then
        assertEquals(0, histories.size());
    }
}


