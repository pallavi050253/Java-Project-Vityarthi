public class Member {
    private int memberId;
    private String fullName;
    private String email;
    private String password;
    private String role; // "ADMIN" or "STUDENT"
    private String phoneNumber;

    
    public Member() {
    }

    
    public Member(int memberId, String fullName, String email, String password, String role, String phoneNumber) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    
    public Member(String fullName, String email, String password, String role, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

   
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public String toString() {
        return "ID: " + memberId + " | Name: " + fullName + " | Role: " + role;
    }
}
