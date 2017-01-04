package scouter.client.xlog;

import scouter.lang.step.ApiCallStep;
import scouter.util.HashUtil;

public class XLogFilterStatus {
	
	public String objName = "";
	public String service = "";
	public String ip = "";
	public String userAgent = "";
	public boolean onlySql;
	public boolean onlyApicall;
	public boolean onlyError;
	
	@Override
	public int hashCode() {
		int filter_hash = HashUtil.hash(objName);
		filter_hash ^= HashUtil.hash(service);
		filter_hash ^= HashUtil.hash(ip);
		filter_hash ^= HashUtil.hash(userAgent);
		filter_hash ^= HashUtil.hash(onlyError ? "onlyError" : "");
		filter_hash ^= HashUtil.hash(onlySql ? "onlySql" : "");
		filter_hash ^=  HashUtil.hash(onlyApicall ? "onlyApicall" : "");
		return filter_hash;
	}
	
	public XLogFilterStatus clone() {
		XLogFilterStatus status = new XLogFilterStatus();
		status.objName = objName;
		status.service = service;
		status.ip = ip;
		status.userAgent = userAgent;
		status.onlySql = onlySql;
		status.onlyApicall = onlyApicall;
		status.onlyError = onlyError;
		return status;
	}
}
