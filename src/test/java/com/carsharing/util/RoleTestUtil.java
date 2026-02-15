package com.carsharing.util;

import com.carsharing.model.Role;
import com.carsharing.model.enums.RoleName;

public class RoleTestUtil {
    public static Role createCustomerRole() {
        Role role = new Role();
        role.setId(2L);
        role.setName(RoleName.USER);
        return role;
    }

    public static Role createAdminRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ADMIN);
        return role;
    }
}
