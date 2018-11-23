import java.util.ArrayList;
import java.util.List;

/**
 * Some static methods used in this project
 */
public class Util {

    public static List<Integer> copyList(List<Integer> list){
        List<Integer> temp = new ArrayList<Integer>();

        for(Integer i: list)
            temp.add(new Integer(i));

        return temp;
    }
}
