package io.hhplus.tdd.point.application;

import io.hhplus.tdd.HHPlusAppExcetion;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointServiceTest {

    private PointService pointService;
    private PointHistoryTable pointHistoryTable;
    private UserPointTable userPointTable;

    @BeforeEach
    void setUp() {
        pointHistoryTable = new PointHistoryTable();
        userPointTable = new UserPointTable();
        pointService = new PointService(pointHistoryTable, userPointTable);

        UserPoint userPoint = new UserPoint(1, 10000, System.currentTimeMillis());
        userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
        pointHistoryTable.insert(userPoint.id(), userPoint.point(), TransactionType.CHARGE, userPoint.updateMillis());
    }

    @Test
    void charge() {
        //given
        long id = 1;
        long amount = 15000;

        //when
        UserPoint updatedUserPoint = pointService.charge(id, amount);

        //then
        assertThat(updatedUserPoint.point()).isEqualTo(25000);
    }

    @Test
    void charge_hist() {
        //given
        long id = 1;
        long amount = 15000;

        //when
        UserPoint updatedUserPoint = pointService.charge(id, amount);

        //then
        List<PointHistory> chargeHists = pointHistoryTable.selectAllByUserId(updatedUserPoint.id())
                .stream().filter(pointHistory -> pointHistory.type() == TransactionType.CHARGE)
                .toList();

        int lastIdx = chargeHists.size() - 1;
        assertThat(chargeHists.get(lastIdx).amount()).isEqualTo(15000);
    }

    @Test
    void fail_not_enough_balance() {
        //given
        long id = 1;
        long amount = 15000;

        //when
        HHPlusAppExcetion hhPlusAppExcetion = assertThrows(HHPlusAppExcetion.class, () -> pointService.use(id, amount));

        //then
        assertThat(hhPlusAppExcetion.getErrorResponse().code()).isEqualTo("ERR-100");
    }

    @Test
    void success_use() {
        //given
        long id = 1;
        long amount = 5000;

        //when
        UserPoint remainingUserPoint = pointService.use(id, amount);

        //then
        assertThat(remainingUserPoint.point()).isEqualTo(5000);
    }

    @Test
    void use_hist() {
        //given
        long id = 1;
        long amount = 5000;

        //when
        UserPoint remainingUserPoint = pointService.use(id, amount);

        //then
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

        List<PointHistory> useHists = pointHistories.stream()
                .filter(pointHistory -> pointHistory.type().equals(TransactionType.USE))
                .collect(Collectors.toList());

        int lastIdx = useHists.size() - 1;
        assertThat(useHists.get(lastIdx).amount()).isEqualTo(5000);
    }


}