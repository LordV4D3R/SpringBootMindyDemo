package com.hsf.jpademo.runner;

import com.hsf.jpademo.service.StudentLabService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * =========================================================================================
 * RUNNER TỰ ĐỘNG CHẠY TOÀN BỘ KỊCH BẢN DEMO KHI ỨNG DỤNG KHỞI ĐỘNG
 * =========================================================================================
 * - CommandLineRunner: Giao diện của Spring Boot. Bất kỳ bean nào triển khai interface này
 *   sẽ có hàm run() tự động chạy ngay sau khi Spring ApplicationContext được khởi tạo xong.
 * - Giúp bạn và sinh viên chỉ cần bấm nút "Run" trong IntelliJ IDEA là thấy ngay toàn bộ
 *   kết quả và câu lệnh SQL tương ứng chạy tuần tự từ Cấp 1 đến Cấp 3 trên Console!
 * =========================================================================================
 */
@Component
public class DataInitializerAndDemoRunner implements CommandLineRunner {

    private final StudentLabService labService;

    public DataInitializerAndDemoRunner(StudentLabService labService) {
        this.labService = labService;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n");
        System.out.println("===============================================================================");
        System.out.println("   KHỞI ĐỘNG CHƯƠNG TRÌNH DEMO JPA & SPRING BOOT 3 (HSF302)");
        System.out.println("===============================================================================");

        try {
            // ---------------------------------------------------------
            // CẤP ĐỘ 1: THAO TÁC CƠ BẢN VÀ QUAN HỆ 1 - 1
            // ---------------------------------------------------------
            // Bài 1.1: Tạo User 1 (An) + Profile (Lưu bằng Cascade)
            Long user1Id = labService.executeLab1_1_CreateUserWithProfile();

            // Bài 1.2: Truy vấn User 1 theo ID và in thông tin
            labService.executeLab1_2_FindUserById(user1Id);

            // Bài 1.3: Derived query tìm theo email
            labService.executeLab1_3_FindByEmail("an.nguyen@gmail.com");

            // ---------------------------------------------------------
            // CẤP ĐỘ 2: QUAN HỆ 1 - N VÀ CƠ CHẾ UPDATE (DIRTY CHECKING)
            // ---------------------------------------------------------
            // Bài 2.1: Thêm 2 đơn hàng cho An và tạo mới User 2 (Bình)
            Long user2Id = labService.executeLab2_1_AddOrdersAndCreateUser2(user1Id);

            // Bài 2.2: Derived query tìm đơn hàng theo status 'COMPLETED'
            labService.executeLab2_2_FindOrdersByStatus("COMPLETED");

            // Bài 2.3: Cập nhật thông tin Profile của An và đổi trạng thái đơn ORD-102
            labService.executeLab2_3_UpdateInfo(user1Id);

            // ---------------------------------------------------------
            // CẤP ĐỘ 3: KỸ THUẬT NÂNG CAO (JOIN FETCH, SUM, ORPHAN, CASCADE DELETE)
            // ---------------------------------------------------------
            // Bài 3.1: Kiểm tra tối ưu hóa tránh lỗi N+1 Query bằng JOIN FETCH
            labService.executeLab3_1_TestNPlus1Optimization(user1Id);

            // Bài 3.2: Thống kê tính tổng số tiền An đã chi tiêu (status = COMPLETED)
            labService.executeLab3_2_CalculateTotalSpent(user1Id);

            // Bài 3.3: Hủy đơn ORD-101 và chứng minh cơ chế Orphan Removal
            labService.executeLab3_3_OrphanRemoval(user1Id);

            // Bài 3.4: Xóa User 2 (Bình) và chứng minh cơ chế Cascade Delete
            labService.executeLab3_4_CascadeDelete(user2Id);

            System.out.println("\n===============================================================================");
            System.out.println("   CHÚC MỪNG: BẠN ĐÃ CHẠY HOÀN TẤT THÀNH CÔNG TOÀN BỘ 3 CẤP ĐỘ BÀI TẬP JPA!");
            System.out.println("   Mở trình duyệt: http://localhost:8080/h2-console để xem bảng CSDL trực tiếp.");
            System.out.println("   JDBC URL: jdbc:h2:mem:hsf_db | User: sa | Password: (để trống)");
            System.out.println("===============================================================================\n");

        } catch (Exception e) {
            System.err.println(">>> ĐÃ CÓ LỖI XẢY RA TRONG QUÁ TRÌNH THỰC THI:");
            e.printStackTrace();
        }
    }
}
