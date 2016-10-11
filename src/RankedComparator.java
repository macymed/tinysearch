import java.util.*;

public class RankedComparator implements Comparator<Map.Entry<Integer, Double>>{
	public int compare(Map.Entry<Integer, Double> x, Map.Entry<Integer, Double> y){
		if(x.getValue() < y.getValue()){
			return 1;
		}else if(x.getValue() > y.getValue()){
			return -1;
		}else{
			return 0;
		}

	}
}
