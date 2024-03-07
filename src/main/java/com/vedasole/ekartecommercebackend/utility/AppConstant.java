package com.vedasole.ekartecommercebackend.utility;

public class AppConstant {

    public enum RELATIONS {

        CATEGORY("category"),
        CATEGORIES("categories"),
        PRODUCT("product"),
        PRODUCTS("products");

        private String value;

        RELATIONS(String value) {
            this.value=value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
