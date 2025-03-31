package com.backend.entity;

public class Code {

    // HTTP 状态码
    public static final int HTTP_OK = 200; // 请求成功
    public static final int HTTP_CREATED = 201; // 资源创建成功
    public static final int HTTP_ACCEPTED = 202; // 请求被接受
    public static final int HTTP_NO_CONTENT = 204; // 没有内容
    public static final int HTTP_BAD_REQUEST = 400; // 请求错误
    public static final int HTTP_UNAUTHORIZED = 401; // 未授权
    public static final int HTTP_FORBIDDEN = 403; // 禁止访问
    public static final int HTTP_NOT_FOUND = 404; // 资源未找到
    public static final int HTTP_METHOD_NOT_ALLOWED = 405; // 方法不允许
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500; // 服务器内部错误
    public static final int HTTP_BAD_GATEWAY = 502; // 错误网关
    public static final int HTTP_SERVICE_UNAVAILABLE = 503; // 服务不可用

    // 自定义业务代码
    public static final int CODE_SUCCESS = 0; // 成功
    public static final int CODE_FAIL = 1; // 失败
    public static final int CODE_PARAM_ERROR = 2; // 参数错误
    public static final int CODE_UNAUTHORIZED = 3; // 未授权
    public static final int CODE_FORBIDDEN = 4; // 禁止访问
    public static final int CODE_NOT_FOUND = 5; // 资源未找到
    public static final int CODE_SERVER_ERROR = 6; // 服务器错误

    // 获取状态码对应的描述信息
    public static String getDescription(int code) {
        switch (code) {
            case HTTP_OK:
                return "请求成功";
            case HTTP_CREATED:
                return "资源创建成功";
            case HTTP_ACCEPTED:
                return "请求被接受";
            case HTTP_NO_CONTENT:
                return "没有内容";
            case HTTP_BAD_REQUEST:
                return "请求错误";
            case HTTP_UNAUTHORIZED:
                return "未授权";
            case HTTP_FORBIDDEN:
                return "禁止访问";
            case HTTP_NOT_FOUND:
                return "资源未找到";
            case HTTP_METHOD_NOT_ALLOWED:
                return "方法不允许";
            case HTTP_INTERNAL_SERVER_ERROR:
                return "服务器内部错误";
            case HTTP_BAD_GATEWAY:
                return "错误网关";
            case HTTP_SERVICE_UNAVAILABLE:
                return "服务不可用";
            case CODE_SUCCESS:
                return "成功";
            case CODE_FAIL:
                return "失败";
            case CODE_PARAM_ERROR:
                return "参数错误";
            case CODE_UNAUTHORIZED:
                return "未授权";
            case CODE_FORBIDDEN:
                return "禁止访问";
            case CODE_NOT_FOUND:
                return "资源未找到";
            case CODE_SERVER_ERROR:
                return "服务器错误";
            default:
                return "未知状态码";
        }
    }
}