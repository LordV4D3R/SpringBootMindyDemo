package com.hsf.jpademo.repository;

import com.hsf.jpademo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * =========================================================================================
 * REPOSITORY QUẢN LÝ THỰC THỂ USER (SPRING DATA JPA)
 * =========================================================================================
 * - Kế thừa JpaRepository<User, Long>: Có sẵn toàn bộ hàm CRUD cơ bản
 *   (save, findById, findAll, deleteById, count...).
 * - Spring Data JPA sẽ tự động tạo đối tượng triển khai lúc runtime.
 * =========================================================================================
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * BÀI 1.3: DERIVED QUERY (TRUY VẤN TỰ SINH THEO TÊN HÀM)
     * - Cú pháp findBy + Email:
     *   Spring phân tích cú pháp (AST) lúc khởi động ứng dụng và sinh câu SQL:
     *   SELECT * FROM users WHERE email = ? LIMIT 1
     * - Trả về Optional<User> để chống lỗi NullPointerException nếu không tìm thấy.
     */
    Optional<User> findByEmail(String email);

    /**
     * BÀI 3.1: GIẢI QUYẾT TRIỆT ĐỂ LỖI N+1 QUERY BẰNG 'JOIN FETCH'
     * - Thay vì bắn 1 query lấy User, rồi bắn thêm N query lấy Profile và Orders (khi LAZY load),
     *   câu lệnh JPQL này ép Hibernate sinh đúng 1 câu lệnh SQL 'LEFT OUTER JOIN'
     *   để nạp (eagerly fetch) toàn bộ dữ liệu User, Profile, Orders trong đúng 1 lần truy vấn duy nhất.
     */
    @Query("SELECT u FROM User u " +
           "LEFT JOIN FETCH u.profile " +
           "LEFT JOIN FETCH u.orders " +
           "WHERE u.id = :id")
    Optional<User> findUserFullDetailsById(@Param("id") Long id);
}
