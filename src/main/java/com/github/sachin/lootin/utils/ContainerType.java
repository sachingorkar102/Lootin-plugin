package com.github.sachin.lootin.utils;



public enum ContainerType {
    

    CHEST(27,LConstants.TITLE_CHEST) ,DOUBLE_CHEST(54,LConstants.TITLE_DOUBLE_CHEST),MINECART(27,LConstants.TITLE_MINECART),BARREL(27,null);

    private ContainerType(int slots,String title){
        this.slots = slots;
        this.title = title;
    }

    private int slots;
    private String title;

    public String getTitle() {
        return title;
    }

    public int getSlots() {
        return slots;
    }
}
