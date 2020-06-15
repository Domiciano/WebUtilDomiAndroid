package co.domi.webutildomiandroid;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;

public class WebUtilDomi {

    private String method;
    private String url;
    private String body;
    private String response;
    private HashMap<String, String> headers;
    private ArrayList<String> hosts;
    private HTTPUtil util;
    private Activity activity;
    private IAction uiAction;
    private IAction workerAction;


    private WebUtilDomi() {
        hosts = new ArrayList<>();
        headers = new HashMap<>();
    }

    public static WebUtilDomi create() {
        return new WebUtilDomi();
    }

    //1
    public WebUtilDomi withMethod(String method) {
        this.method = method;
        return this;
    }

    //2
    public WebUtilDomi toURL(String url) {
        this.url = url;
        return this;
    }

    //3*
    public WebUtilDomi withBody(String json) {
        this.body = json;
        return this;
    }

    //4*
    public WebUtilDomi setHeader(String clave, String valor) {
        this.headers.put(clave, valor);
        return this;
    }

    //5
    public WebUtilDomi forActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    //6*
    public WebUtilDomi withWorkerEndAction(IAction workerAction) {
        this.workerAction = workerAction;
        return this;
    }

    //7*
    public WebUtilDomi withUIEndAction(IAction uiAction) {
        this.uiAction = uiAction;
        return this;
    }

    public void execute() {
        //2
        if (url.startsWith("https")) {
            util = new HTTPSWebUtilDomi();
        } else if (url.startsWith("http")) {
            util = new HTTPWebUtilDomi();
        } else {
            System.out.println("WebUtilDomi>>> Malformed URL. Use a protocol http or https");
        }

        //4*
        for (String header : headers.keySet()) {
            String value = headers.get(header);
            util.setHeader(header, value);
        }

        //2
        util.addKnownHost(url);

        new Thread(
                () -> {
                    //1
                    response = "";
                    if (method.equals("GET")) {
                        response = util.syncGETrequest(url);
                    } else if (method.equals("POST")) {
                        //3
                        if (body == null) {
                            System.out.println(">>>UtilDomi: Body is missing, if you want a message with no body, use setBody(\"\"). Operation cancelled");
                            return;
                        } else response = util.syncPOSTRequest(url, body);
                    } else if (method.equals("PUT")) {
                        //3
                        if (body == null) {
                            System.out.println(">>>UtilDomi: Body is missing, if you want a message with no body, use setBody(\"\"). Operation cancelled");
                            return;
                        } else response = util.syncPUTRequest(url, body);
                    } else if (method.equals("DELETE")) {
                        response = util.syncDELETErequest(url);
                    }

                    //6*
                    if (workerAction != null) workerAction.run(response);

                    //5, 7*
                    if (activity != null && uiAction != null) {
                        activity.runOnUiThread(
                                () -> {
                                    uiAction.run(response);
                                }
                        );
                    } else if (activity == null && uiAction != null) {
                        System.out.println(">>>UtilDomi: Context is missing. It is needed to complete UI Action");
                    } else if (activity != null && uiAction == null) {
                        System.out.println(">>>UtilDomi: UIAction is missing");
                    }
                }
        ).start();
    }
}
