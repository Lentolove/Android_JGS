package com.tsp.android.jgs;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author : shengping.tian
 * time   : 2021/10/11
 * desc   :
 * version: 1.0
 */
public class Test1 {

    @Test
    public void test2(String[] args) {
        Solution s = new Solution();
        List<String> list = s.letterCombinations("23");
        System.out.println(list.size());
    }


}
class Solution {



    private Map<Integer,String> map = new HashMap<>();

    List<String> list = new ArrayList<>();

    //1.回溯算法
    public List<String> letterCombinations(String digits) {
        map.put(2,"abc");
        map.put(3,"def");
        map.put(4,"ghi");
        map.put(2,"jkl");
        map.put(2,"mno");
        map.put(2,"pqrs");
        map.put(2,"tuv");
        map.put(2,"wxyz");
        backtract(new StringBuilder(),digits.toCharArray(),0);
        return list;

    }

    public void backtract(StringBuilder sb,char[] digits,int index){
        if(index == digits.length){
            list.add(sb.toString());
            return;
        }
        for(int i = index; i < digits.length; i++){
            String item = map.get(Integer.parseInt(String.valueOf(digits[i])));
            for(char a : item.toCharArray()){
                sb.append(a);
                //回溯
                backtract(sb,digits,index + 1);
                //撤销选择
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }
}