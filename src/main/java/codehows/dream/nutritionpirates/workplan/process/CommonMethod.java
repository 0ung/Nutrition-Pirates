package codehows.dream.nutritionpirates.workplan.process;

public class CommonMethod {
	public static String getString(double time) {
		int hours = (int)time; // 시간 부분
		int minutes = (int)Math.ceil((time - hours) * 60); // 올림 처리한 분 부분
		if (minutes == 60) {
			hours += 1;
			minutes = 0;
		}
		return hours + "시 " + minutes + "분";
	}
}
