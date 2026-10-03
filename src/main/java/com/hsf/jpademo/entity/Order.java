package com.hsf.jpademo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * =========================================================================================
 * THỰC THỂ ORDER (ĐƠN HÀNG)
 * =========================================================================================
 * - Đại diện cho bảng 'orders' trong cơ sở dữ liệu.
 * - Đóng vai trò là OWNING SIDE trong quan hệ Many-to-One:
 *   Bảng này giữ cột Khóa ngoại `user_id` để biết đơn hàng này thuộc về ai.
 * =========================================================================================
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", nullable = false, unique = true, length = 50)
    private String orderCode;

    /**
     * BigDecimal là kiểu dữ liệu chuẩn doanh nghiệp cho tiền tệ,
     * tránh sai số dấu phẩy động của float/double.
     */
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    /**
     * Trạng thái đơn hàng: PENDING, COMPLETED, CANCELLED...
     */
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    /**
     * QUAN HỆ MANY-TO-ONE VỚI USER (Owning Side):
     * - Nhiều Order thuộc về Một User.
     * - @JoinColumn(name = "user_id"): Tạo cột khóa ngoại user_id trong bảng 'orders'.
     * - nullable = false: Đảm bảo một đơn hàng bắt buộc phải gắn với một người dùng hợp lệ.
     * - FetchType.LAZY: Khi query Order thì KHÔNG vội query User nếu chưa dùng đến.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Order() {
        this.orderDate = LocalDateTime.now();
    }

    public Order(String orderCode, BigDecimal totalAmount, String status) {
        this.orderCode = orderCode;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderCode='" + orderCode + '\'' +
                ", totalAmount=" + totalAmount +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                '}';
    }
}
