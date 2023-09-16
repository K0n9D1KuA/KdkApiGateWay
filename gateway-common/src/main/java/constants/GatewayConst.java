package constants;

public interface GatewayConst {

	String UNIQUE_ID = "uniqueId";

	String DEFAULT_VERSION = "1.0.0";

	String PROTOCOL_KEY = "protocol";

	int DEFAULT_WEIGHT = 100;

	String META_DATA_KEY = "meta";

	String BUFFER_TYPE_PARALLEL ="parallel" ;


	String FLOW_CTL_FILTER_ID = "flow_ctl_filter";
	String FLOW_CTL_FILTER_NAME = "flow_ctl_filter";
	int FLOW_CTL_FILTER_ORDER = 50;

	String FLOW_CTL_TYPE_PATH = "path";
	String FLOW_CTL_TYPE_SERVICE = "service";

	String FLOW_CTL_LIMIT_DURATION = "duration"; //以秒为单位
	String FLOW_CTL_LIMIT_PERMITS = "permits"; //允许请求的次数


}
