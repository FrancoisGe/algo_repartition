import org.json.JSONArray;
import org.json.JSONObject;

public class SolutionAPIHopper {

    private JSONObject solution;
    private JSONObject route;
    private JSONArray activities;

    private JSONObject deb;
    private JSONObject fin;

    private Position[]positions;

    public SolutionAPIHopper(JSONObject sol){
        this.solution=sol;



        route= (JSONObject) solution.getJSONObject("solution").getJSONArray("routes").get(0);
        activities=route.getJSONArray("activities");

        deb=activities.getJSONObject(0);
        fin=activities.getJSONObject(activities.length()-1);

        positions=new Position[activities.length()];

        for (int i = 0; i < activities.length(); i++) {
            positions[i]=new Position(activities.getJSONObject(i).getJSONObject("address").getDouble("lon"),activities.getJSONObject(i).getJSONObject("address").getDouble("lat"));
            System.out.println(positions[i].toString());
        }



    }

    public Position positionDeb(){
        Position position=new Position(deb.getJSONObject("address").getDouble("lon"),deb.getJSONObject("address").getDouble("lat"));

        return position;
    }

    /*
    Renvoie la list des positions à parcourir triées
     */

    public Position[] listPositionSolution(){


        return positions;
    }


}
