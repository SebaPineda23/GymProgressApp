package Application.GymProgress.Enum;

public enum Role {
    ADMIN, USER;  // ← Deben ser exactamente estos nombres

    public String getRoleName() {
        return this.name();  // "ADMIN" o "USER"
    }
}
