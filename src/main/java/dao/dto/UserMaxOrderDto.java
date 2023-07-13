package dao.dto;

public class UserMaxOrderDto {
    long userId;
    long orderId;
    String name;
    String address;

    private UserMaxOrderDto() {
    }

    public long getUserId() {
        return userId;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public static class Builder {
        private UserMaxOrderDto dto;

        public Builder() {
            dto = new UserMaxOrderDto();
        }

        public Builder userId(long userId) {
            dto.userId = userId;
            return this;
        }

        public Builder orderId(long orderId) {
            dto.orderId = orderId;
            return this;
        }

        public Builder name(String name) {
            dto.name = name;
            return this;
        }

        public Builder address(String address) {
            dto.address = address;
            return this;
        }

        public UserMaxOrderDto build() {
            return dto;
        }
    }
}
