package co.domi.webutildomiandroid;

public interface HTTPUtil {

    void GETrequest(int callbackID, String url);

    String syncGETrequest(String url);

    String syncPOSTRequest(String url, String json);

    void POSTrequest(int callbackID, String url, String json);

    void PUTrequest(int callbackID, String url, String json);

    String syncPUTRequest(String url, String json);

    void DELETErequest(int callbackID, String url);

    String syncDELETErequest(String url);

    void addKnownHost(String host);

    void setHeader(String header, String value);


}
