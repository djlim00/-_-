package kuit.remetic.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name="User")
public class User {

    @Id
    @Column(name = "user_email", nullable = false)
    private String email;

    @Column(name = "profile_image_Url", nullable = false)
    private String profileImageUrl;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    // Getters and setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}