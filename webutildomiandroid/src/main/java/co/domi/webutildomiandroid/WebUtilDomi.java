package co.domi.webutildomiandroid;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;

public class WebUtilDomi {

    private String method;
    private String url;
    private String body;
    private HashMap<String, String> headers;
    private ArrayList<String> hosts;
    private HTTPUtil util;
    private Activity activity;
    private Runnable uiAction;
    private IAction workerAction;


    private WebUtilDomi(){
        hosts = new ArrayList<>();
        headers = new HashMap<>();
    }

    public static WebUtilDomi create(){
        return new WebUtilDomi();
    }

    public WebUtilDomi withMethod(String method){
        this.method = method;
        return this;
    }

    public WebUtilDomi toURL(String url){
        this.url = url;
        return this;
    }

    public WebUtilDomi withBody(String json) {
        this.body = json;
        return this;
    }

    public WebUtilDomi setHeader(String clave, String valor) {
        this.headers.put(clave, valor);
        return this;
    }

    public void execute() {
        if(url.startsWith("https")){
            util = new HTTPSWebUtilDomi();
        }else if(url.startsWith("http")){
            util = new HTTPWebUtilDomi();
        }else{
            System.out.println("WebUtilDomi>>> Malformed URL");
        }

        for(String header : headers.keySet()){
            String value = headers.get(header);
            util.setHeader(header, value);
        }

        for(String host : hosts){
            util.addKnownHost(host);
        }

        new Thread(
                ()->{
                    if(method.equals("GET")){
                        util.syncGETrequest(url);
                    }else if(method.equals("POST")){
                        if(body == null) System.out.println(">>>UtilDomi: Body is missing");
                        else util.syncPOSTRequest(url, body);
                    }else if(method.equals("PUT")){
                        if(body == null) System.out.println(">>>UtilDomi: Body is missing");
                        else util.syncPUTRequest(url, body);
                    }else if(method.equals("DELETE")){
                        util.syncDELETErequest(url);
                    }

                    workerAction.run();

                    activity.runOnUiThread(
                            this.uiAction
                    );
                }
        ).start();
    }

    public void forActivity(Activity activity){
        this.activity = activity;
    }

    public WebUtilDomi withWorkerEndAction(IAction workerAction) {
        this.workerAction = workerAction;
        return this;
    }

    public WebUtilDomi withUIEndAction(Runnable uiAction) {
        this.uiAction = uiAction;
        return this;
    }
}
