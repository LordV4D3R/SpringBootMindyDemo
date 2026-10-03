package com.hsf.jpademo.repository;

import com.hsf.jpademo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * =========================================================================================
 * REPOSITORY QUẢN LÝ THỰC THỂ ORDER
 * =========================================================================================
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * BÀI 2.2: DERIVED QUERY TRÊN ORDER
     * - Tìm kiếm danh sách các đơn hàng theo trạng thái (status).
     * - SQL sinh ra: SELECT * FROM orders WHERE status = ?
     */
    List<Order> findByStatus(String status);

    /**
     * BÀI 3.2: THỐNG KÊ TÍNH TOÁN BẰNG HÀM SUM TRONG JPQL
     * - Tính tổng số tiền (totalAmount) của các đơn hàng thuộc về một User cụ thể
     *   và có trạng thái là 'COMPLETED'.
     * - Chú ý cú pháp JPQL:
     *   Truy vấn dựa trên thuộc tính Entity: 'o.user.id' và 'o.status'
     *   chứ không phải tên bảng trong CSDL.
     * - Trả về BigDecimal để đảm bảo độ chính xác tài chính.
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
           "WHERE o.user.id = :userId AND o.status = :status")
    BigDecimal calculateTotalSpentByUserAndStatus(@Param("userId") Long userId, 
                                                 @Param("status") String status);
}
