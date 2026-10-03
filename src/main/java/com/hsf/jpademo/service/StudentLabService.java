package com.hsf.jpademo.service;

import com.hsf.jpademo.entity.Order;
import com.hsf.jpademo.entity.User;
import com.hsf.jpademo.entity.UserProfile;
import com.hsf.jpademo.repository.OrderRepository;
import com.hsf.jpademo.repository.UserProfileRepository;
import com.hsf.jpademo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * =========================================================================================
 * SERVICE TRIỂN KHAI VÀ GIẢI THÍCH CHI TIẾT BÀI TẬP 3 CẤP ĐỘ
 * =========================================================================================
 * - @Service: Đánh dấu class là Spring Bean thuộc tầng Service.
 * - @Transactional: Bao bọc phương thức trong một Database Transaction:
 *   + Tự động commit khi kết thúc hàm.
 *   + Tự động rollback nếu có ngoại lệ RuntimeException.
 *   + Duy trì Persistence Context để Hibernate thực hiện Dirty Checking và Lazy Loading.
 * =========================================================================================
 */
@Service
public class StudentLabService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserProfileRepository userProfileRepository;

    // Constructor Injection (Chuẩn khuyến nghị của Spring Boot / tương đương .NET Core)
    public StudentLabService(UserRepository userRepository,
                             OrderRepository orderRepository,
                             UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.userProfileRepository = userProfileRepository;
    }

    // =========================================================================================
    // CẤP ĐỘ 1: THAO TÁC CƠ BẢN & VÒNG ĐỜI ENTITY
    // =========================================================================================

    /**
     * BÀI 1.1: THÊM MỚI QUAN HỆ 1 - 1 VỚI CascadeType.ALL
     * Quy định: Chỉ gọi duy nhất 1 lệnh userRepository.save(user1).
     * Hibernate tự động lan truyền lệnh INSERT sang bảng 'user_profiles'.
     */
    @Transactional
    public Long executeLab1_1_CreateUserWithProfile() {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> [BÀI 1.1] THÊM MỚI USER 1 (NGUYỄN VĂN AN) VÀ HỒ SƠ (CASCADE.ALL)");
        System.out.println("------------------------------------------------------------------");

        // Khởi tạo thực thể User và UserProfile (Trạng thái: Transient - chỉ nằm trong RAM)
        User user1 = new User("an.nguyen", "an.nguyen@gmail.com", "Nguyễn Văn An");
        UserProfile profile1 = new UserProfile("0901234567", "Hà Nội", "Lập trình viên");

        // Gán hồ sơ cho User (hàm này tự động đồng bộ cả 2 chiều trong bộ nhớ RAM)
        user1.setProfile(profile1);

        // GỌI DUY NHẤT 1 LỆNH SAVE TRÊN USER:
        // Nhờ CascadeType.ALL, Hibernate sẽ tự sinh 2 câu lệnh INSERT:
        // 1. INSERT INTO users ... -> lấy user_id tự tăng (vd: 1)
        // 2. INSERT INTO user_profiles (..., user_id) VALUES (..., 1)
        User savedUser = userRepository.save(user1);

        System.out.println("=> Đã lưu thành công User với ID = " + savedUser.getId());
        return savedUser.getId();
    }

    /**
     * BÀI 1.2: TRUY VẤN CƠ BẢN THEO ID VÀ IN RA THÔNG TIN
     */
    @Transactional(readOnly = true)
    public void executeLab1_2_FindUserById(Long userId) {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> [BÀI 1.2] TRUY VẤN USER THEO ID = " + userId);
        System.out.println("------------------------------------------------------------------");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với ID: " + userId));

        // In ra màn hình console theo đúng định dạng yêu cầu:
        System.out.println("=> KẾT QUẢ IN RA: Họ tên: [" + user.getFullName() + 
                           "] - Địa chỉ: [" + user.getProfile().getAddress() + "]");
    }

    /**
     * BÀI 1.3: DERIVED QUERY (TỰ ĐỘNG SINH CÚ PHÁP TÌM THEO EMAIL)
     */
    @Transactional(readOnly = true)
    public void executeLab1_3_FindByEmail(String email) {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> [BÀI 1.3] DERIVED QUERY: TÌM USER THEO EMAIL = " + email);
        System.out.println("------------------------------------------------------------------");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("=> KẾT QUẢ: Tìm thấy người dùng: " + user.getFullName() + 
                               " (Username: " + user.getUsername() + ")");
        } else {
            System.out.println("=> KẾT QUẢ: Không tìm thấy người dùng có email: " + email);
        }
    }

    // =========================================================================================
    // CẤP ĐỘ 2: QUAN HỆ 1 - N VÀ CƠ CHẾ CẬP NHẬT (DIRTY CHECKING)
    // =========================================================================================

    /**
     * BÀI 2.1: THÊM MỚI QUAN HỆ 1 - N
     * - Thêm 2 đơn hàng ORD-101 và ORD-102 cho Nguyễn Văn An qua hàm user1.addOrder(...).
     * - Tạo thêm tài khoản Lê Thị Bình kèm hồ sơ và đơn hàng ORD-201.
     */
    @Transactional
    public Long executeLab2_1_AddOrdersAndCreateUser2(Long user1Id) {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> [BÀI 2.1] THÊM ĐƠN HÀNG CHO USER 1 VÀ TẠO MỚI USER 2 (LÊ THỊ BÌNH)");
        System.out.println("------------------------------------------------------------------");

        // 1. Lấy User 1 (Nguyễn Văn An) ra từ DB
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User 1 không tồn tại"));

        // Tạo 2 đơn hàng
        Order order101 = new Order("ORD-101", new BigDecimal("250000"), "COMPLETED");
        Order order102 = new Order("ORD-102", new BigDecimal("1200000"), "PENDING");

        // Sử dụng helper method addOrder để đồng bộ cả chiều User -> Order và Order -> User
        user1.addOrder(order101);
        user1.addOrder(order102);

        // Lưu User 1 (Cascade sẽ tự động insert 2 order với user_id được điền chính xác)
        userRepository.save(user1);
        System.out.println("=> Đã thêm 2 đơn hàng ORD-101 và ORD-102 cho Nguyễn Văn An.");

        // 2. Tạo tài khoản User 2 (Lê Thị Bình)
        User user2 = new User("binh.le", "binh.le@gmail.com", "Lê Thị Bình");
        UserProfile profile2 = new UserProfile("0912345678", "Đà Nẵng", "Thiết kế đồ họa");
        user2.setProfile(profile2);

        Order order201 = new Order("ORD-201", new BigDecimal("750000"), "COMPLETED");
        user2.addOrder(order201);

        User savedUser2 = userRepository.save(user2);
        System.out.println("=> Đã tạo User 2 (Lê Thị Bình) ID = " + savedUser2.getId() + 
                           " kèm Profile và Đơn hàng ORD-201.");

        return savedUser2.getId();
    }

    /**
     * BÀI 2.2: DERIVED QUERY TRÊN ORDER THEO TRẠNG THÁI (STATUS)
     */
    @Transactional(readOnly = true)
    public void executeLab2_2_FindOrdersByStatus(String status) {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> [BÀI 2.2] DERIVED QUERY: TÌM TẤT CẢ ĐƠN HÀNG CÓ STATUS = " + status);
        System.out.println("------------------------------------------------------------------");

        List<Order> orders = orderRepository.findByStatus(status);
        System.out.println("=> Tìm thấy " + orders.size() + " đơn hàng có trạng thái " + status + ":");
        for (Order o : orders) {
            System.out.println("   * Mã đơn: " + o.getOrderCode() + " - Số tiền: " + 
                               String.format("%,.0f VNĐ", o.getTotalAmount()) + " - Thuộc User: " + o.getUser().getFullName());
        }
    }

    /**
     * BÀI 2.3: CẬP NHẬT THÔNG TIN (UPDATE / DIRTY CHECKING)
     * - An đổi SĐT: 0999888777, Địa chỉ: TP. Hồ Chí Minh.
     * - Đơn ORD-102 chuyển trạng thái thành COMPLETED.
     * MINH HỌA: Không cần gọi hàm update(), Hibernate tự động so sánh bản chụp snapshot và bắn lệnh UPDATE!
     */
    @Transactional
    public void executeLab2_3_UpdateInfo(Long user1Id) {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> [BÀI 2.3] CẬP NHẬT THÔNG TIN (MINH HỌA CƠ CHẾ DIRTY CHECKING)");
        System.out.println("------------------------------------------------------------------");

        // Lấy User 1 lên (Hibernate giữ Snapshot của đối tượng trong RAM)
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User 1 không tồn tại"));

        // Thay đổi thông tin Profile
        user1.getProfile().setPhoneNumber("0999888777");
        user1.getProfile().setAddress("TP. Hồ Chí Minh");

        // Tìm đơn ORD-102 trong danh sách của User 1 và đổi trạng thái
        for (Order o : user1.getOrders()) {
            if ("ORD-102".equals(o.getOrderCode())) {
                o.setStatus("COMPLETED");
                System.out.println("   * Đã đổi trạng thái đơn ORD-102 từ PENDING -> COMPLETED");
            }
        }

        // Khi kết thúc hàm có @Transactional (hoặc khi gọi userRepository.save),
        // Hibernate tự phát hiện các trường bị thay đổi (dirty) và sinh SQL UPDATE.
        userRepository.save(user1);
        System.out.println("=> Cập nhật thành công! Kiểm tra log SQL bên dưới để thấy câu lệnh UPDATE.");
    }

    // =========================================================================================
    // CẤP ĐỘ 3: KỸ THUẬT NÂNG CAO (JOIN FETCH, AGGREGATION, ORPHAN REMOVAL, CASCADE DELETE)
    // =========================================================================================

    /**
     * BÀI 3.1: TỐI ƯU TRUY VẤN TRÁNH LỖI N+1 QUERY BẰNG 'JOIN FETCH'
     * Mở log để chứng minh hệ thống chỉ chạy ĐÚNG 1 CÂU LỆNH SELECT (dùng LEFT JOIN)!
     */
    @Transactional(readOnly = true)
    public void executeLab3_1_TestNPlus1Optimization(Long user1Id) {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> [BÀI 3.1] TEST TRUY VẤN FULL DETAILS TRÁNH LỖI N+1 BẰNG 'JOIN FETCH'");
        System.out.println("------------------------------------------------------------------");

        // Gọi hàm có JOIN FETCH
        User user = userRepository.findUserFullDetailsById(user1Id)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        System.out.println("=> Lấy thông tin thành công qua 1 câu SELECT duy nhất:");
        System.out.println("   * Họ tên: " + user.getFullName());
        System.out.println("   * Địa chỉ Profile: " + user.getProfile().getAddress());
        System.out.println("   * Số lượng đơn hàng: " + user.getOrders().size());
        user.getOrders().forEach(o -> System.out.println("     + Đơn " + o.getOrderCode() + 
                                                         ": " + String.format("%,.0f VNĐ", o.getTotalAmount()) + 
                                                         " (" + o.getStatus() + ")"));
    }

    /**
     * BÀI 3.2: THỐNG KÊ TÍNH TOÁN BẰNG JPQL (SUM)
     * Tính tổng chi tiêu của An với các đơn COMPLETED.
     * Kết quả đúng: 250.000 (ORD-101) + 1.200.000 (ORD-102 đã chuyển sang COMPLETED) = 1.450.000 VNĐ.
     */
    @Transactional(readOnly = true)
    public void executeLab3_2_CalculateTotalSpent(Long user1Id) {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> [BÀI 3.2] THỐNG KÊ TỔNG CHI TIÊU CỦA NGUYỄN VĂN AN VỚI HÀM SUM (JPQL)");
        System.out.println("------------------------------------------------------------------");

        BigDecimal totalSpent = orderRepository.calculateTotalSpentByUserAndStatus(user1Id, "COMPLETED");

        System.out.println("=> KẾT QUẢ TÍNH TOÁN: Tổng tiền đã thanh toán (COMPLETED) của Nguyễn Văn An:");
        System.out.println("   " + String.format("%,.0f VNĐ", totalSpent));
        System.out.println("   (Khớp chính xác với lý thuyết: 250.000 + 1.200.000 = 1.450.000 VNĐ) ✅");
    }

    /**
     * BÀI 3.3: XÓA PHẦN TỬ MỒ CÔI (ORPHAN REMOVAL)
     * Hủy đơn ORD-101 bằng cách gỡ khỏi danh sách của An.
     * Chứng minh rằng đơn này tự động bị XÓA KHỎI BẢNG ORDERS dưới database!
     */
    @Transactional
    public void executeLab3_3_OrphanRemoval(Long user1Id) {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> [BÀI 3.3] XÓA PHẦN TỬ MỒ CÔI (ORPHAN REMOVAL) — HỦY ĐƠN ORD-101");
        System.out.println("------------------------------------------------------------------");

        User user1 = userRepository.findUserFullDetailsById(user1Id)
                .orElseThrow(() -> new RuntimeException("User 1 không tồn tại"));

        // Tìm đơn ORD-101
        Order orderToRemove = user1.getOrders().stream()
                .filter(o -> "ORD-101".equals(o.getOrderCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn ORD-101"));

        // Gỡ đơn khỏi danh sách của User (hàm removeOrder ngắt quan hệ 2 chiều)
        user1.removeOrder(orderToRemove);

        // Lưu User lại: Hibernate nhận diện ORD-101 bị mồ côi -> sinh lệnh DELETE FROM orders WHERE id = ...
        userRepository.save(user1);

        System.out.println("=> Đã gỡ đơn ORD-101 khỏi danh sách của User 1.");

        // Kiểm tra lại trong DB:
        boolean stillExists = orderRepository.findAll().stream()
                .anyMatch(o -> "ORD-101".equals(o.getOrderCode()));
        System.out.println("=> Kiểm tra thực tế trong bảng 'orders': Đơn ORD-101 còn tồn tại không? " + 
                           (stillExists ? "CÒN (Lỗi cấu hình)" : "ĐÃ BỊ XÓA HOÀN TOÀN ✅"));
    }

    /**
     * BÀI 3.4: XÓA KÉO THEO (CASCADE DELETE)
     * Xóa tài khoản Lê Thị Bình (User 2).
     * Chứng minh Profile và Đơn hàng ORD-201 tự động bị xóa sạch.
     */
    @Transactional
    public void executeLab3_4_CascadeDelete(Long user2Id) {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println(">>> [BÀI 3.4] XÓA TÀI KHOẢN KÉO THEO (CASCADE DELETE) — XÓA USER 2 (BÌNH)");
        System.out.println("------------------------------------------------------------------");

        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User 2 không tồn tại"));

        // Gọi lệnh delete trên User 2:
        // Hibernate tự động xóa Profile và Order ORD-201 trước, rồi xóa User 2
        userRepository.delete(user2);

        System.out.println("=> Đã gọi lệnh userRepository.delete(user2).");

        // Kiểm tra xác nhận:
        boolean userExists = userRepository.existsById(user2Id);
        long remainingOrders = orderRepository.count();
        long remainingProfiles = userProfileRepository.count();

        System.out.println("=> KẾT QUẢ KIỂM TRA SAU KHI XÓA USER 2:");
        System.out.println("   * User 2 còn tồn tại trong DB? " + (userExists ? "CÒN" : "ĐÃ BỊ XÓA ✅"));
        System.out.println("   * Số lượng Profile còn lại trong DB: " + remainingProfiles + " (Chỉ còn của An)");
        System.out.println("   * Số lượng Order còn lại trong DB: " + remainingOrders + " (Chỉ còn ORD-102 của An)");
    }
}
