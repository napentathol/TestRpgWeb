package us.sodiumlabs.rpg.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.Collections;

public class Die {
    private String username;

    private int num;

    private int val;

    private int add;

    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    private int roll(final int val) {
        return (int)(Math.random() * val) + 1;
    }

    @JsonIgnore
    private int[] rollArray(final int num, final int val) {
        int[] retArray = new int[num];

        for(int i = 0; i < num; i++) {
            retArray[i] = roll(val);
        }

        return retArray;
    }

    @JsonIgnore
    public String rollString() {
        if(num < 1) return "ERROR: number of dice less than 1!";
        if(val < 1) return "ERROR: die value less than 1!";

        String rollString = "Rolled: " + num + "d" + val + "+" + add + "; (";

        final int[] rollArr = rollArray(num, val);

        for (int i = 0; i < rollArr.length; i++) {
            rollString += rollArr[i] + (i != rollArr.length - 1 ? " + " : ") + " + add + " = ");
        }

        rollString += Arrays.stream(rollArr).sum() + add;

        return rollString;
    }
}
