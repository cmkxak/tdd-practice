package io.hhplus.tdd.point.application;

import io.hhplus.tdd.HHPlusAppExcetion;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.ErrorCode;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    public PointService(PointHistoryTable pointHistoryTable, UserPointTable userPointTable) {
        this.pointHistoryTable = pointHistoryTable;
        this.userPointTable = userPointTable;
    }


    public UserPoint findPointById(long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> findPointHistoriesById(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    public UserPoint charge(long id, long amount) {
        UserPoint currentUserPoint = findUserPointById(id);

        long chargedPoint = currentUserPoint.point() + amount;

        UserPoint savedUserPoint = userPointTable.insertOrUpdate(id, chargedPoint);

        updatePointHistory(savedUserPoint, amount, TransactionType.CHARGE);

        return savedUserPoint;
    }

    public UserPoint use(long id, long amount) {
        UserPoint currentUserPoint = findUserPointById(id);

        validateEnoughBalance(currentUserPoint.point(), amount);

        long remainingPoint = currentUserPoint.point() - amount;

        UserPoint savedUserPoint = userPointTable.insertOrUpdate(id, remainingPoint);

        updatePointHistory(savedUserPoint, amount, TransactionType.USE);

        return savedUserPoint;
    }

    private void updatePointHistory(UserPoint savedUserPoint, long amount, TransactionType type) {
        pointHistoryTable.insert(savedUserPoint.id(), amount, type, savedUserPoint.updateMillis());
    }

    private UserPoint findUserPointById(long id) {
        return userPointTable.selectById(id);
    }

    private void validateEnoughBalance(long memberBalance, long amount) {
        if (memberBalance < amount) {
            throw new HHPlusAppExcetion(ErrorCode.INVALID_BALANCE.getErrorResponse());
        }
    }
}
