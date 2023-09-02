package request;

import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import constants.BasicConst;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import utils.TimeUtil;


import java.nio.charset.Charset;
import java.util.*;

/**
 * @PROJECT_NAME: api-gateway
 * @DESCRIPTION: 网关请求类
 * @USER: WuYang
 * @DATE: 2022/12/29 21:01
 */
@Slf4j
public class GatewayRequest implements IGatewayRequest {

    /**
     * 服务ID
     */

    private final String uniqueId;

    /**
     * 请求进入网关时间
     */

    private final long beginTime;

    /**
     * 字符集不会变的
     */

    private final Charset charset;

    /**
     * 客户端的IP，主要用于做流控、黑白名单
     */

    private final String clientIp;

    /**
     * 请求的地址：IP：port
     */

    private final String host;

    /**
     * 请求的路径   /XXX/XXX/XX
     */

    private final String path;

    /**
     * URI：统一资源标识符，/XXX/XXX/XXX?attr1=value&attr2=value2
     * URL：统一资源定位符，它只是URI的子集一个实现
     */

    private final String uri;

    /**
     * 请求方法 post/put/GET
     */

    private final HttpMethod method;

    /**
     * 请求的格式
     */

    private final String contentType;

    /**
     * 请求头信息
     */

    private final HttpHeaders headers;

    /**
     * 参数解析器
     */

    private final QueryStringDecoder queryStringDecoder;

    /**
     * FullHttpRequest
     */

    private final FullHttpRequest fullHttpRequest;

    /**
     * 请求体
     */

    private String body;


    private long userId;

    /**
     * 请求Cookie
     */

    private Map<String, io.netty.handler.codec.http.cookie.Cookie> cookieMap;

    /**
     * post请求定义的参数结合
     */

    private Map<String, List<String>> postParameters;


    /******可修改的请求变量***************************************/
    /**
     * 可修改的Scheme，默认是http://
     */
    private String modifyScheme;

    private String modifyHost;

    private String modifyPath;

    /**
     * 构建下游请求是的http请求构建器
     */
    private final RequestBuilder requestBuilder;

    /**
     * 构造器
     *
     * @param uniqueId        后端服务唯一标识
     * @param charset         字符集
     * @param clientIp        客户端IP
     * @param host            目标域名
     * @param uri             目标uri
     * @param method          请求方式
     * @param contentType     请求内容
     * @param headers         请求头
     * @param fullHttpRequest 经过netty 处理后的request
     */
    public GatewayRequest(String uniqueId, Charset charset, String clientIp, String host, String uri, HttpMethod method, String contentType, HttpHeaders headers, FullHttpRequest fullHttpRequest) {
        this.uniqueId = uniqueId;

        //请求进入网关的时间
        this.beginTime = TimeUtil.currentTimeMillis();
        this.charset = charset;
        this.clientIp = clientIp;
        this.host = host;
        this.uri = uri;
        this.method = method;
        this.contentType = contentType;
        this.headers = headers;
        this.fullHttpRequest = fullHttpRequest;
        //QueryStringDecoder是一个用于解析URL查询字符串的工具类。它接受两个参数：uri和charset。
        //uri是一个包含查询字符串的URL或URI，例如：http://example.com/path?param1=value1&param2=value2。QueryStringDecoder将解析这个uri，并提取出其中的参数和对应的值。
        //charset是指定解码查询字符串时使用的字符集。查询字符串通常使用UTF-8编码，所以常见的charset参数值是"UTF-8"。
        //通过使用QueryStringDecoder，您可以方便地将URL查询字符串解析为参数和值的键值对，以便在代码中进行进一步的处理和使用。
        this.queryStringDecoder = new QueryStringDecoder(uri, charset);
        this.path = queryStringDecoder.path();
        this.modifyHost = host;
        this.modifyPath = path;

        //http:// 默认前缀就是 http://
        this.modifyScheme = BasicConst.HTTP_PREFIX_SEPARATOR;


        //构建 请求构建对象
        this.requestBuilder = new RequestBuilder();
        this.requestBuilder.setMethod(getMethod().name());
        this.requestBuilder.setHeaders(getHeaders());
        this.requestBuilder.setQueryParams(queryStringDecoder.parameters());

        ByteBuf contentBuffer = fullHttpRequest.content();
        if (Objects.nonNull(contentBuffer)) {
            this.requestBuilder.setBody(contentBuffer.nioBuffer());
        }
    }

    /**
     * 获取请求体
     *
     * @return
     */
    public String getBody() {
        if (StringUtils.isEmpty(body)) {
            body = fullHttpRequest.content().toString(charset);
        }
        return body;
    }

    /**
     * 获取Cookie
     *
     * @param name cookie's name
     * @return cookie
     */
    public Cookie getCookie(String name) {
        if (cookieMap == null) {
            cookieMap = new HashMap<>();

            //从请求头里面拿到cookie
            String cookieStr = getHeaders().get(HttpHeaderNames.COOKIE);

            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);

            for (Cookie cookie : cookies) {
                cookieMap.put(name, cookie);
            }
        }
        return cookieMap.get(name);
    }

    /**
     * 获取指定名词参数值
     *
     * @param name 指定名称
     * @return 指定名称所对应的参数值
     */
    public List<String> getQueryParametersMultiple(String name) {
        return queryStringDecoder.parameters().get(name);
    }


    //代码解析：
    //
    //    首先，从请求体中获取POST请求的内容，通过调用getBody()方法。
    //    接下来，判断请求是否是表单提交（isFormPost()方法）。
    //        如果是表单提交，则继续执行以下步骤：
    //            检查postParameters是否为空，如果为空，则将请求体解析为参数键值对，存储在postParameters中。
    //            如果postParameters仍为空或者为空集合，表示没有参数，返回null。
    //            否则，返回指定名称参数的值列表，通过调用postParameters.get(name)方法。
    //        如果是JSON提交（isJsonPost()方法），则执行以下步骤：
    //            尝试使用JsonPath解析请求体中的JSON数据，获取指定名称参数的值。如果解析失败，会记录错误日志。
    //            返回解析得到的指定名称参数值的列表，通过调用Lists.newArrayList(JsonPath.read(body, name).toString())方法。
    //    如果以上条件都不满足，则返回null。
    //
    //总结：这段代码是一个根据请求类型（表单提交或JSON提交）从POST请求体中获取指定名称参数值的方法。如果是表单提交，直接解析请求体中的参数键值对；如果是JSON提交，使用JsonPath解析JSON数据获取参数值。

    /**
     * post请求体里面获取指定名词参数值
     *
     * @param name 指定名称
     * @return
     */
    public List<String> getPostParametersMultiples(String name) {
        String body = getBody();
        if (isFormPost()) {
            if (postParameters == null) {
                QueryStringDecoder paramDecoder = new QueryStringDecoder(body, false);
                postParameters = paramDecoder.parameters();
            }
            if (postParameters == null || postParameters.isEmpty()) {
                return null;
            } else {
                return postParameters.get(name);
            }
        } else if (isJsonPost()) {
            try {
                return Lists.newArrayList(JsonPath.read(body, name).toString());
            } catch (Exception e) {
                log.error("JsonPath解析失败，JsonPath:{},Body:{},", name, body, e);
            }
        }
        return null;
    }


    @Override
    public void setModifyHost(String modifyHost) {
        this.modifyHost = modifyHost;
    }

    @Override
    public String getModifyHost() {
        return modifyHost;
    }

    @Override
    public void setModifyPath(String modifyPath) {
        this.modifyPath = modifyPath;
    }

    @Override
    public String getModifyPath() {
        return modifyPath;
    }

    @Override
    public void addHeader(CharSequence name, String value) {
        requestBuilder.addHeader(name, value);
    }

    @Override
    public void setHeader(CharSequence name, String value) {
        requestBuilder.setHeader(name, value);
    }

    @Override
    public void addQueryParam(String name, String value) {
        requestBuilder.addQueryParam(name, value);
    }

    @Override
    public void addFormParam(String name, String value) {
        if (isFormPost()) {
            requestBuilder.addFormParam(name, value);
        }
    }

    @Override
    public void addOrReplaceCookie(org.asynchttpclient.cookie.Cookie cookie) {
        requestBuilder.addOrReplaceCookie(cookie);
    }

    @Override
    public void setRequestTimeout(int requestTimeout) {
        requestBuilder.setRequestTimeout(requestTimeout);
    }

    @Override
    public String getFinalUrl() {
        return modifyScheme + modifyHost + modifyPath;
    }

    @Override
    public Request build() {
        requestBuilder.setUrl(getFinalUrl());
        requestBuilder.addHeader("userId", String.valueOf(userId));
        return requestBuilder.build();
    }

    public boolean isFormPost() {
        // 请求方式是 post 并且 contentType相关
        return HttpMethod.POST.equals(method) &&
                (contentType.startsWith(HttpHeaderValues.FORM_DATA.toString()) ||
                        contentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString()));
    }

    public boolean isJsonPost() {
        // 请求方式是 post 并且 contentType相关
        return HttpMethod.POST.equals(method) &&
                contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString());
    }


    public String getUniqueId() {
        return uniqueId;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public Charset getCharset() {
        return charset;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getContentType() {
        return contentType;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public QueryStringDecoder getQueryStringDecoder() {
        return queryStringDecoder;
    }

    public FullHttpRequest getFullHttpRequest() {
        return fullHttpRequest;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Map<String, Cookie> getCookieMap() {
        return cookieMap;
    }

    public void setCookieMap(Map<String, Cookie> cookieMap) {
        this.cookieMap = cookieMap;
    }

    public Map<String, List<String>> getPostParameters() {
        return postParameters;
    }

    public void setPostParameters(Map<String, List<String>> postParameters) {
        this.postParameters = postParameters;
    }

    public String getModifyScheme() {
        return modifyScheme;
    }

    public void setModifyScheme(String modifyScheme) {
        this.modifyScheme = modifyScheme;
    }

    public RequestBuilder getRequestBuilder() {
        return requestBuilder;
    }


}
