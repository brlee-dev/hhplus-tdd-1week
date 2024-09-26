package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final Lock lock = new ReentrantLock();

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    /** 포인트 충전 */
    public UserPoint chargePoint(long userId, long amount) {
        lock.lock();
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
            }

            UserPoint userPoint = userPointTable.selectById(userId);
            long newAmount = userPoint.amount() + amount;

            userPoint = userPointTable.insertOrUpdate(userId, newAmount);
            pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis());

            return userPoint;
        } finally {
            lock.unlock();
        }
    }
}
