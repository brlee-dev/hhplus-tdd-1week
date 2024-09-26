package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }


    /**
     * 특정 유저의 포인트를 조회하는 기능
     */
    @GetMapping("{id}")
    public ResponseEntity<UserPoint> point(
            @PathVariable long id
    ) {
        log.info("포인트 조회 요청 - 유저 ID: {}", id);
        UserPoint userPoint = pointService.getPoint(id);
        return ResponseEntity.ok(userPoint);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역을 조회하는 기능
     */
    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointHistory>> history(
            @PathVariable long id
    ) {
        log.info("포인트 내역 조회 요청 - 유저 ID: {}", id);
        List<PointHistory> histories = pointService.getPointHistories(id);
        return ResponseEntity.ok(histories);
    }

    /**
     * 특정 유저의 포인트를 충전하는 기능
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<?> charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        log.info("포인트 충전 요청 - 유저 ID: {}, 금액: {}", id, amount);
        try {
            UserPoint userPoint = pointService.chargePoint(id, amount);
            return ResponseEntity.ok(userPoint);
        } catch (IllegalArgumentException e) {
            log.error("포인트 충전 실패 - 유저 ID: {}, 이유: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 특정 유저의 포인트를 사용하는 기능
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<?> use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        log.info("포인트 사용 요청 - 유저 ID: {}, 금액: {}", id, amount);
        try {
            UserPoint userPoint = pointService.usePoint(id, amount);
            return ResponseEntity.ok(userPoint);
        } catch (IllegalArgumentException e) {
            log.error("포인트 사용 실패 - 유저 ID: {}, 이유: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
