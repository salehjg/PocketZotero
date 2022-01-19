package io.github.salehjg.pocketzotero.zoteroengine.types;

import java.io.Serializable;

public class Creator implements Serializable {
    protected String mFirstName, mLastName, mType;
    protected int mCreatorId, mCreatorTypeId, mOrderIndex;

    public Creator(){
        this.mFirstName = "?";
        this.mLastName = "?";
        this.mType = "?";
        this.mCreatorId = -1;
        this.mCreatorTypeId = -1;
        this.mOrderIndex = -1;
    }
    public Creator(String firstName, String lastName){
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mType = "?";
        this.mCreatorId = -1;
        this.mCreatorTypeId = -1;
        this.mOrderIndex = -1;
    }

    public Creator(String firstName, String lastName, int creatorTypeId, int orderIndex){
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mType = "?";
        this.mCreatorId = -1;
        this.mCreatorTypeId = creatorTypeId;
        this.mOrderIndex = orderIndex;
    }

    public Creator(int creatorId, int creatorTypeId, int orderIndex){
        this.mFirstName = "?";
        this.mLastName = "?";
        this.mType = "?";
        this.mCreatorId = creatorId;
        this.mCreatorTypeId = creatorTypeId;
        this.mOrderIndex = orderIndex;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String extractFullName(){
        return mFirstName+" "+mLastName;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public int getCreatorId() {
        return mCreatorId;
    }

    public void setCreatorId(int creatorId) {
        this.mCreatorId = creatorId;
    }

    public int getCreatorTypeId() {
        return mCreatorTypeId;
    }

    public void setCreatorTypeId(int creatorTypeId) {
        this.mCreatorTypeId = creatorTypeId;
    }

    public int getOrderIndex() {
        return mOrderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.mOrderIndex = orderIndex;
    }
}
