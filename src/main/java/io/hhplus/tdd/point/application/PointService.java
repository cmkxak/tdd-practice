package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    public PointService(PointHistoryTable pointHistoryTable, UserPointTable userPointTable) {
        this.pointHistoryTable = pointHistoryTable;
        this.userPointTable = userPointTable;
    }

    public UserPoint charge(long id, long amount) {
        UserPoint currentUserPoint = findUserPointById(id);

        long chargedPoint = currentUserPoint.point() + amount;

        UserPoint savedUserPoint = userPointTable.insertOrUpdate(id, chargedPoint);

        updatePointHistory(savedUserPoint, amount, TransactionType.CHARGE);

        return savedUserPoint;
    }

    public UserPoint findPointById(long id) {
        return findUserPointById(id);
    }

    private void updatePointHistory(UserPoint savedUserPoint, long amount, TransactionType type) {
        pointHistoryTable.insert(savedUserPoint.id(), amount, type, savedUserPoint.updateMillis());
    }

    private UserPoint findUserPointById(long id) {
        return userPointTable.selectById(id);
    }

}
