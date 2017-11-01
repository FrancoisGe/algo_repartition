import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectAPIGraphHopper {

    private String URL_Hopper_POST ="https://graphhopper.com/api/1/vrp/optimize";
    private String key_Hopper="?key=d4e97f09-f4b7-49bb-a4e5-0b3dceda14a2";

    private String URL_Hopper_GET="https://graphhopper.com/api/1/vrp/solution/";

    private String job_id="";

    public ConnectAPIGraphHopper(){

    }

    public static void main (String[] args) throws IOException {
        ConnectAPIGraphHopper con = new ConnectAPIGraphHopper();
        con.sendOpti();

    }

    public void sendOpti() throws IOException {

        String url = URL_Hopper_POST+this.key_Hopper;
        HttpURLConnection connection=this.createCon(url,"POST");

        JSONObject start_address = this.createAdressJSON("0",13.406,52.537);
        System.out.println(start_address.toString());

        //créeaton véhicule
        JSONObject vehicle=new JSONObject();
        vehicle.put("vehicle_id","veh1");
        vehicle.put("start_address",start_address);
        vehicle.put("type_id","default");
        vehicle.put("return_to_depot",true);

        //creation liste des vehicule
        JSONArray vehicles = new JSONArray();
        vehicles.put(vehicle);

        JSONObject type_id =new JSONObject();
        type_id.put("type_id","default");
        type_id.put("profile","car");

        JSONArray vehicle_types =new JSONArray();
        vehicle_types.put(type_id);


        System.out.println(vehicle_types);

        JSONArray services =new JSONArray();
        services.put(createServiceJSON("1", 9.999,53.552));
        services.put(createServiceJSON("2", 11.57,48.145));


        //Création message JSON a envoyé à l API
        JSONObject message= new JSONObject();
        message.put("vehicles",vehicles);
        message.put("vehicle_types",vehicle_types);
        message.put("services",services);

        System.out.println(message);



        //Envoie du JSON
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(message.toString());
        wr.flush();

        JSONObject rep= new JSONObject(showBackMessage(connection));
        this.job_id=rep.getString("job_id").toString();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        url=URL_Hopper_GET+job_id+this.key_Hopper;
        System.out.println(url);

        HttpURLConnection connectionSolution=this.createCon(url,"GET");





        JSONObject repGET=new JSONObject(showBackMessage(connectionSolution));
        System.out.println("test");
        JSONObject x= (JSONObject) repGET.getJSONObject("solution").getJSONArray("routes").get(0);
        System.out.println(x.getJSONArray("activities").length());

        SolutionAPIHopper solutionAPIHopper =new SolutionAPIHopper(repGET);
        System.out.println(solutionAPIHopper.positionDeb().getLat());
        solutionAPIHopper.listPositionSolution();


    }

    private HttpURLConnection createCon(String url,String method) throws IOException {


        URL object=new URL(url);

        //Création de la connection à l'API
        HttpURLConnection con = (HttpURLConnection) object.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod(method);

        return con;
    }

    public JSONObject createAdressJSON(String id_loc,double lon,double lat){

        //Création adressJSON
        JSONObject start_address = new JSONObject();
        start_address.put("location_id",id_loc);
        start_address.put("lon",lon);
        start_address.put("lat",lat);

        return start_address;
    }

    public JSONObject createServiceJSON(String id,double lon,double lat){
        JSONObject service =new JSONObject();
        service.put("id",id);
        service.put("name","no-name");
        JSONObject adress=this.createAdressJSON(id,lon,lat);
        service.put("address",adress);

        System.out.println(service);

        return service;
    }

    public String showBackMessage(HttpURLConnection con) throws IOException {
        //display what returns the POST request

        StringBuilder sb = new StringBuilder();
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            System.out.println("" + sb.toString());
            return sb.toString();
        } else {
            System.out.println(con.getResponseMessage());
            return con.getResponseMessage();
        }
    }

}
