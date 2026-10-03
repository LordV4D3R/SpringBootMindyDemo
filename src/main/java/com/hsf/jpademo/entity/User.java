package com.hsf.jpademo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * =========================================================================================
 * THỰC THỂ USER (NGƯỜI DÙNG - THỰC THỂ TRUNG TÂM)
 * =========================================================================================
 * - Đại diện cho bảng 'users' trong cơ sở dữ liệu.
 * - Đóng vai trò là INVERSE SIDE (Bên đối ứng / bị sở hữu):
 *   Bảng 'users' hoàn toàn KHÔNG giữ khóa ngoại nào của UserProfile hay Order.
 *   Nó sử dụng thuộc tính 'mappedBy = "user"' để tham chiếu ngược lại thuộc tính 'user'
 *   trong các thực thể con.
 * =========================================================================================
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    /**
     * QUAN HỆ 1 - 1 VỚI USERPROFILE:
     * - mappedBy = "user": Khai báo phía UserProfile mới là bên giữ khóa ngoại (Owning side).
     * - cascade = CascadeType.ALL: Khi lưu (PERSIST), cập nhật (MERGE) hoặc xóa (REMOVE) User,
     *   Hibernate sẽ tự động lan truyền thao tác tương ứng sang UserProfile.
     * - orphanRemoval = true: Nếu gỡ liên kết (setProfile(null)), bản ghi Profile tương ứng
     *   sẽ bị xóa hẳn khỏi database.
     * - fetch = FetchType.LAZY: Tải lười để tối ưu hiệu năng.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserProfile profile;

    /**
     * QUAN HỆ 1 - N VỚI ORDER:
     * - mappedBy = "user": Thuộc tính 'user' trong entity Order giữ khóa ngoại 'user_id'.
     * - cascade = CascadeType.ALL: Khi lưu/xóa User, toàn bộ danh sách Order đi kèm được lưu/xóa theo.
     * - orphanRemoval = true: Khi xóa một Order ra khỏi danh sách 'orders', Hibernate sẽ tự động
     *   sinh câu lệnh SQL DELETE để xóa dòng Order đó trong CSDL (Xóa phần tử mồ côi).
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    // Constructors
    public User() {
    }

    public User(String username, String email, String fullName) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    // =========================================================================================
    // CÁC HÀM TIỆN ÍCH ĐỒNG BỘ 2 CHIỀU (SYNCHRONIZATION HELPER METHODS)
    // =========================================================================================

    /**
     * Gán hồ sơ cho User:
     * Đồng bộ cả 2 chiều trong bộ nhớ RAM:
     * 1. Gán profile cho User.
     * 2. Gán ngược lại user cho Profile (để khi Hibernate lưu Profile, nó biết user_id là bao nhiêu).
     */
    public void setProfile(UserProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }

    /**
     * Thêm đơn hàng vào danh sách của User:
     * ĐÂY LÀ DÒNG CODE QUAN TRỌNG NHẤT ĐỂ TRÁNH LỖI user_id BỊ NULL:
     * - orders.add(order): Thêm vào List của User.
     * - order.setUser(this): Gán đối tượng User vào Order (Owning Side). Dòng này làm cho
     *   Hibernate điền giá trị user_id vào câu lệnh INSERT!
     */
    public void addOrder(Order order) {
        this.orders.add(order);
        order.setUser(this);
    }

    /**
     * Hủy / Gỡ đơn hàng khỏi danh sách của User:
     * Kết hợp với 'orphanRemoval = true', khi gỡ khỏi list và set user = null,
     * Hibernate sẽ tự động sinh câu lệnh DELETE xóa Order này khỏi bảng 'orders'.
     */
    public void removeOrder(Order order) {
        this.orders.remove(order);
        order.setUser(null);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
