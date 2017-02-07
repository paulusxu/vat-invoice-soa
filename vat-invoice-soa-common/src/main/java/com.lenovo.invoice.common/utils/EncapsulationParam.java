package com.lenovo.invoice.common.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by xuweihua on 2016/7/26.
 */
public class EncapsulationParam {
    /**
     * @param obj
     * @param request
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws ParseException
     */

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void getObjectFromRequest(Object obj,HttpServletRequest request) throws IllegalArgumentException, IllegalAccessException, ParseException {
        Class<?> cla = obj.getClass();//获得对象类型
        Field field[] = cla.getDeclaredFields();//获得该类型中的所有属性
        for(int i=0;i<field.length;i++) {//遍历属性列表
            field[i].setAccessible(true);//禁用访问控制检查
            Class<?> fieldType = field[i].getType();//获得属性类型
            String attr = request.getParameter(field[i].getName());//获得属性值
            if(attr==null) {//如果属性值为null则不做任何处理，直接进入下一轮循环
                continue;
            }
            /**
             * 根据对象中属性类型的不同，将request对象中的字符串转换为相应的属性
             */
            if(fieldType==String.class) {
                field[i].set(obj,attr);
            }
            else if(fieldType==int.class||fieldType==Integer.class){//当转换失败时，设置0
                field[i].set(obj,Integer.parseInt(request.getParameter(field[i].getName())));
            } else if (fieldType==Date.class) {//当转换失败时，设置为null
                field[i].set(obj, sdf.parse(request.getParameter(field[i].getName())));
            } else if (fieldType==Boolean.class) {
                field[i].set(obj, Boolean.parseBoolean(request.getParameter(field[i].getName())));
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(Boolean.parseBoolean("true"));
    }
}
