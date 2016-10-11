import java.util.*;

public class IValComparator implements Comparator<Map.Entry<String, Double>>{
	public int compare(Map.Entry<String, Double> x, Map.Entry<String, Double> y){
		if(x.getValue() < y.getValue()){
			return 1;
		}else if(x.getValue() > y.getValue()){
			return -1;
		}else{
			return 0;
		}

	}
}
