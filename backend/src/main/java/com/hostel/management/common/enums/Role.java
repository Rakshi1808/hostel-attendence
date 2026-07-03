package com.hostel.management.common.enums;

public enum Role {
    STUDENT("ROLE_STUDENT"),
    WARDEN("ROLE_WARDEN"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
