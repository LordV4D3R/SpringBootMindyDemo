# 🚀 HSF302 — Demo Thực Hành Quan Hệ JPA (User - UserProfile - Order)
## Bài Tập Thực Chiến Từ Cấp 1 Đến Cấp 3

Dự án mẫu Spring Boot 3 chuẩn phân tầng, minh họa đầy đủ các khái niệm cốt lõi của môn học **HSF302 (JPA and Spring Framework)** theo đúng đề bài thực tế mà sinh viên đang học.

---

## 🛠️ Công Nghệ Sử Dụng
* **Java:** 21 LTS
* **Framework:** Spring Boot 3.3.4 (Spring Data JPA, Spring Web)
* **ORM:** Hibernate 6 / Jakarta Persistence API (JPA 3.1)
* **Database:** H2 in-memory Database (không cần cài đặt CSDL rời)
* **Build Tool:** Maven

---

## 🧭 Cấu Trúc Dự Án
```text
spring-boot-jpa-demo/
├── pom.xml                                   # Quản lý dependencies (Spring Boot, Data JPA, H2)
├── README.md                                 # Hướng dẫn sử dụng & giải thích kiến trúc
└── src/
    └── main/
        ├── java/com/hsf/jpademo/
        │   ├── JpaDemoApplication.java       # Hàm main() khởi chạy Spring Boot
        │   ├── entity/                       # 3 Thực thể cốt lõi
        │   │   ├── User.java                 # Entity trung tâm (Inverse side: mappedBy)
        │   │   ├── UserProfile.java          # Quan hệ 1 - 1 (Owning side: @JoinColumn user_id)
        │   │   └── Order.java                # Quan hệ Many - 1 (Owning side: @JoinColumn user_id)
        │   ├── repository/                   # Tầng truy xuất CSDL (Spring Data JPA)
        │   │   ├── UserRepository.java       # findByEmail, findUserFullDetailsById (JOIN FETCH)
        │   │   ├── OrderRepository.java      # findByStatus, calculateTotalSpent (JPQL SUM)
        │   │   └── UserProfileRepository.java
        │   ├── service/
        │   │   └── StudentLabService.java    # Xử lý logic & giải thích chi tiết 3 cấp độ
        │   └── runner/
        │       └── DataInitializerAndDemoRunner.java # Tự động chạy demo khi bật app
        └── resources/
            └── application.properties        # Cấu hình H2 database, show SQL, H2 Console
```

---

## 🚀 Cách Mở Và Chạy Trên IntelliJ IDEA

1. **Mở dự án trong IntelliJ IDEA:**
   * Mở IntelliJ IDEA $\rightarrow$ Chọn **File** $\rightarrow$ **Open...**
   * Điều hướng đến thư mục: `E:\Mindy\HSF\Code_Demo\spring-boot-jpa-demo`
   * Chọn `pom.xml` hoặc thư mục dự án và chọn **Open as Project**.
   * Chờ IntelliJ tải các thư viện Maven trong vài giây.

2. **Chạy ứng dụng:**
   * Tìm đến file: `src/main/java/com/hsf/jpademo/JpaDemoApplication.java`.
   * Nhấp chuột phải $\rightarrow$ Chọn **Run 'JpaDemoApplication'** (hoặc bấm biểu tượng tam giác xanh `▶️`).

3. **Quan sát kết quả trên Console:**
   * Toàn bộ kịch bản từ Cấp 1, Cấp 2, Cấp 3 sẽ tự động chạy tuần tự và in kết quả kèm các câu lệnh SQL tương ứng ra màn hình Console.

4. **Truy cập H2 Console trên trình duyệt:**
   * Mở trình duyệt web và truy cập: `http://localhost:8080/h2-console`
   * Cấu hình đăng nhập:
     * **JDBC URL:** `jdbc:h2:mem:hsf_db`
     * **User Name:** `sa`
     * **Password:** *(để trống)*
   * Nhấn nút **Connect** để xem trực tiếp các bảng `users`, `user_profiles`, `orders` và dữ liệu thực tế!

---

## 📋 Tóm Tắt Các Cấp Độ Trong Code

### 🟢 Cấp Độ 1: Thao tác cơ bản & Quan hệ 1 - 1
* **Bài 1.1:** Thêm User 1 (`Nguyễn Văn An`) kèm `UserProfile` bằng **CascadeType.ALL** (chỉ gọi 1 lệnh `userRepository.save(user1)`).
* **Bài 1.2:** Truy vấn User 1 theo ID qua `Optional<User>` và in ra màn hình `Họ tên - Địa chỉ`.
* **Bài 1.3:** Derived Query tự sinh mã SQL: `findByEmail(String email)`.

### 🟡 Cấp Độ 2: Quan hệ 1 - N & Cơ chế Cập nhật (Dirty Checking)
* **Bài 2.1:** Thêm 2 đơn hàng `ORD-101` và `ORD-102` cho An qua helper method `user1.addOrder(...)`. Tạo User 2 (`Lê Thị Bình`) kèm Profile và đơn hàng `ORD-201`.
* **Bài 2.2:** Derived Query trên Order: `findByStatus("COMPLETED")`.
* **Bài 2.3:** Cập nhật thông tin Profile của An (SĐT mới, Địa chỉ TP.HCM) và đổi trạng thái đơn `ORD-102` sang `COMPLETED` mà **không cần gọi hàm update()** nhờ cơ chế **Dirty Checking** của Hibernate.

### 🔴 Cấp Độ 3: Kỹ thuật nâng cao
* **Bài 3.1:** Giải quyết triệt để lỗi **N+1 Query** bằng `JOIN FETCH` trong JPQL (chỉ chạy đúng 1 câu lệnh SQL duy nhất).
* **Bài 3.2:** Thống kê tính toán tổng chi tiêu của An bằng hàm `SUM` trong JPQL: $250.000 + 1.200.000 = 1.450.000$ VNĐ.
* **Bài 3.3:** Xóa phần tử mồ côi (**Orphan Removal**): Hủy đơn `ORD-101` bằng `user1.removeOrder(...)` $\rightarrow$ Đơn này tự động biến mất khỏi bảng `orders`.
* **Bài 3.4:** Xóa kéo theo (**Cascade Delete**): Xóa User 2 (Bình) $\rightarrow$ Profile và toàn bộ đơn hàng của Bình tự động bị xóa sạch.

---
*Tài liệu nghiên cứu giảng dạy HSF302 — Lưu tại Vault Obsidian: `D:\Obsidian_Save\Mindy\HSF`*
