package suan.chan.pzpi_17_8.entity;

public enum PriorityType {
    First(0),
    Second(1),
    Third(2);

    private final int value;
    private PriorityType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
