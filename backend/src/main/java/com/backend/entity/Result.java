package com.backend.entity;

import java.io.Serializable;
import com.backend.entity.Code;
/**
 * 后端返回给前端数据的统一格式类
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code; // 状态码
    private String message; // 消息
    private T data; // 数据

    // 无参构造方法
    public Result() {
    }

    // 带参构造方法
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 快速创建成功返回结果的静态方法
     *
     * @param data 返回的数据
     * @param <T>  数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(Code.HTTP_OK, "操作成功", data);
    }

    /**
     * 快速创建成功返回结果的静态方法（无数据）
     *
     * @return Result对象
     */
    public static Result<?> success() {
        return new Result<>(Code.HTTP_OK, "操作成功", null);
    }

    /**
     * 快速创建失败返回结果的静态方法
     *
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result对象
     */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 快速创建失败返回结果的静态方法（带数据）
     *
     * @param code    状态码
     * @param message 错误消息
     * @param data    返回的数据
     * @param <T>     数据类型
     * @return Result对象
     */
    public static <T> Result<T> fail(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}