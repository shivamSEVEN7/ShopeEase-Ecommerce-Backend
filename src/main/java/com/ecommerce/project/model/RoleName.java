package com.ecommerce.project.model;

import java.util.Set;

public enum RoleName {
    ADMIN(Set.of(
            Permissions.USER_VIEW,
            Permissions.USER_MANAGE,
            Permissions.PRODUCT_MANAGE,
            Permissions.ORDER_VIEW,
            Permissions.CATEGORY_MANAGE
    )),

    CUSTOMER(Set.of(
            Permissions.ORDER_CREATE,
            Permissions.REVIEW_CREATE,
            Permissions.PROFILE_UPDATE
    )),

    SELLER(Set.of(
            Permissions.PRODUCT_CREATE,
            Permissions.PRODUCT_UPDATE,
            Permissions.INVENTORY_MANAGE
    )),

    GUEST(Set.of(
            Permissions.PRODUCT_VIEW,
            Permissions.CATEGORY_VIEW
    ));

    Set<Permissions> permissions;

    RoleName(Set<Permissions> Permissions) {
        this.permissions = Permissions;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }
}
