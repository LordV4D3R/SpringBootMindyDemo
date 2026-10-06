package com.hsf.jpademo.entity;

import jakarta.persistence.*;

/**
 * =========================================================================================
 * THỰC THỂ USERPROFILE (HỒ SƠ NGƯỜI DÙNG)
 * =========================================================================================
 * - Đại diện cho bảng 'user_profiles' trong cơ sở dữ liệu.
 * - Đóng vai trò là OWNING SIDE (Bên sở hữu quan hệ 1 - 1):
 *   Bảng này giữ cột Khóa ngoại (Foreign Key) `user_id` liên kết về bảng `users`.
 * =========================================================================================
 */
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    //abc

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "bio", length = 500)
    private String bio;

    /**
     * QUAN HỆ ONE-TO-ONE VỚI USER (Owning Side):
     * - @JoinColumn(name = "user_id"): Chỉ định tên cột khóa ngoại (FK) trong bảng 'user_profiles'.
     * - unique = true: Đảm bảo tính toàn vẹn 1-1 ở mức CSDL (mỗi User chỉ có duy nhất 1 Profile).
     * - FetchType.LAZY: Tải lười, chỉ truy vấn User khi thực sự gọi getProfile().getUser().
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Constructors
    public UserProfile() {
    }

    public UserProfile(String phoneNumber, String address, String bio) {
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.bio = bio;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }
}
