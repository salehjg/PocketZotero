package io.github.salehjg.pocketzotero.zoteroengine.types;

public class Creator {
    private String firstName, lastName, type;
    private int creatorId, creatorTypeId, orderIndex;

    public Creator(){
        this.firstName = "?";
        this.lastName = "?";
        this.type = "?";
        this.creatorId = -1;
        this.creatorTypeId = -1;
        this.orderIndex = -1;
    }
    public Creator(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = "?";
        this.creatorId = -1;
        this.creatorTypeId = -1;
        this.orderIndex = -1;
    }

    public Creator(String firstName, String lastName, int creatorTypeId, int orderIndex){
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = "?";
        this.creatorId = -1;
        this.creatorTypeId = creatorTypeId;
        this.orderIndex = orderIndex;
    }

    public Creator(int creatorId, int creatorTypeId, int orderIndex){
        this.firstName = "?";
        this.lastName = "?";
        this.type = "?";
        this.creatorId = creatorId;
        this.creatorTypeId = creatorTypeId;
        this.orderIndex = orderIndex;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getCreatorTypeId() {
        return creatorTypeId;
    }

    public void setCreatorTypeId(int creatorTypeId) {
        this.creatorTypeId = creatorTypeId;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
