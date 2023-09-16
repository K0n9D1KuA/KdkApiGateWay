package com.qyh.gateway.core.response;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ResponseCode;
import io.netty.handler.codec.http.*;
import org.asynchttpclient.Response;
import utils.JSONUtil;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 网关消息返回对象
 * @email 3161788646@qq.com
 * @date 2023/9/2 23:01
 */


public class GatewayResponse {




    /**
     * 响应头
     */
    private HttpHeaders responseHeaders = new DefaultHttpHeaders();

    /**
     * 额外的响应结果
     */
    private final HttpHeaders extraResponseHeaders = new DefaultHttpHeaders();

    /**
     * 响应内容
     */
    private String content;

    /**
     * 异步返回对象
     */
    private Response futureResponse;

    /**
     * 响应返回码
     */
    private HttpResponseStatus httpResponseStatus;


    public GatewayResponse() {

    }

    /**
     * 设置响应头信息
     *
     * @param key
     * @param val
     */
    public void putHeader(CharSequence key, CharSequence val) {
        responseHeaders.add(key, val);
    }

    /**
     * 构建异步响应对象
     *
     * @param futureResponse
     * @return
     */
    public static GatewayResponse buildGatewayResponse(Response futureResponse) {
        GatewayResponse response = new GatewayResponse();
        response.setFutureResponse(futureResponse);
        response.setHttpResponseStatus(HttpResponseStatus.valueOf(futureResponse.getStatusCode()));
        return response;
    }

    /**
     * 处理返回json对象，失败时调用
     *
     * @param code 状态码
     * @param args 额外的参数
     * @return  GatewayResponse
     */
    public static GatewayResponse buildGatewayResponse(ResponseCode code, Object... args) {

        ObjectNode objectNode = JSONUtil.createObjectNode();
        //状态
        objectNode.put(JSONUtil.STATUS, code.getStatus().code());
        //状态码
        objectNode.put(JSONUtil.CODE, code.getCode());
        //信息
        objectNode.put(JSONUtil.MESSAGE, code.getMessage());

        GatewayResponse response = new GatewayResponse();
        response.setHttpResponseStatus(code.getStatus());
        //表示以json形式返回content
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");

        //序列化content
        response.setContent(JSONUtil.toJSONString(objectNode));

        return response;
    }

    /**
     * 处理返回json对象，成功时调用
     *
     * @param data
     * @return
     */
    public static GatewayResponse buildGatewayResponse(Object data) {


        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS, ResponseCode.SUCCESS.getStatus().code());
        objectNode.put(JSONUtil.CODE, ResponseCode.SUCCESS.getCode());
        objectNode.putPOJO(JSONUtil.DATA, data);

        GatewayResponse response = new GatewayResponse();
        response.setHttpResponseStatus(ResponseCode.SUCCESS.getStatus());
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        response.setContent(JSONUtil.toJSONString(objectNode));
        return response;
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(HttpHeaders responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public HttpHeaders getExtraResponseHeaders() {
        return extraResponseHeaders;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Response getFutureResponse() {
        return futureResponse;
    }

    public void setFutureResponse(Response futureResponse) {
        this.futureResponse = futureResponse;
    }

    public HttpResponseStatus getHttpResponseStatus() {
        return httpResponseStatus;
    }

    public void setHttpResponseStatus(HttpResponseStatus httpResponseStatus) {
        this.httpResponseStatus = httpResponseStatus;
    }
}
