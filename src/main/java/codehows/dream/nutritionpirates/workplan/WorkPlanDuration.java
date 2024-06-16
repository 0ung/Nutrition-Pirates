package codehows.dream.nutritionpirates.workplan;

import codehows.dream.nutritionpirates.constants.Routing;
import org.springframework.stereotype.Component;

@Component
//소요시간 계산 로직
public class WorkPlanDuration {

    // 소요 시간 계산
    private double timeCal(int input, int kg, double time) {
        if (input < kg)
            return time;
        int quotient = input / kg;
        int remain = input % kg;
        if (remain > 0) {
            return time * quotient + time;
        }
        return time * quotient;
    }

    private double noCycle(int input, int ea, double unitTime, double time) {
        int quotient = input / ea;
        int remain = input % ea;

        double remainTime = Math.floor((remain * unitTime) / 60 * 100) / 100.0; // 남은 수량에 대한 시간 계산 (분 단위)

        if (input < ea) {
            return Math.floor((input * unitTime) / 60 * 100) / 100.0; // 기준 수량보다 작을 경우 시간 계산 (분 단위)
        } else if (remain > 0) {
            return time * quotient + remainTime; // 남은 수량이 있는 경우 전체 시간 계산
        }

        return time * quotient; // 남은 수량이 없는 경우 전체 시간 계산
    }

    //세척
    public double washingDuration(int input) {
        int kg = Routing.WASHING_ROUTING_KG;
        double time = Routing.WASHING_ROUTING_TIME;
        double wash1kg = Routing.WASHING_1KG_DURATION;

        int quotient = input / kg;
        int remain = input % kg;

        double remainTime = Math.floor((remain * wash1kg) * 100) / 100.0 / 100.0; // 남은 무게에 대한 시간 계산 (분 단위)

        if (input < kg) {
            return Math.floor((input * wash1kg) * 100 / 60) / 100.0; // 기준 무게보다 작을 경우 시간 계산 (분 단위)
        } else if (remain > 0) {
            return Math.floor((time * quotient + remainTime) * 100) / 100.0; // 남은 무게가 있는 경우 전체 시간 계산 (분 단위)
        }

        return Math.floor((time * quotient) * 100) / 100.0; // 남은 무게가 없는 경우 전체 시간 계산 (분 단위)
    }


    //추출
    public double extractionDuration(int input) {
        int kg = Routing.EXTRACTION_ROUTING;
        double time = Routing.EXTRACTION_ROUTING_TIME;
        return timeCal(input, kg, time);
    }

    //여과
    public double filterDuration(int input) {
        int kg = Routing.FILTER_ROUTING;
        double time = Routing.FILTER_ROUTING_TIME;
        return timeCal(input, kg, time);
    }

    //살균
    public double sterilizationDuration(int input) {
        int kg = Routing.STERILIZATION_ROUTING;
        double time = Routing.STERILIZATION_ROUTING_TIME;
        return timeCal(input, kg, time);
    }

    //검사
    public double inspectionDuration(int input) {
        int ea = Routing.INSPECTION_ROUTING;
        double time = Routing.INSPECTION_ROUTING_TIME;
        double inspect1ea = Routing.INSPECTION_1EA_DURATION;
        return noCycle(input, ea, inspect1ea, time);

    }

    //포장
    public double boxPackingDuration(int input) {
        int ea = Routing.BOX_PACKING_ROUTING;
        double time = Routing.BOX_PACKING_ROUTING_TIME;
        double box1ea = Routing.BOX_PACKING_1EA_DURATION;
        return noCycle(input, ea, box1ea, time);
    }

    public double juicePackingDuration(int input) {
        int ea = Routing.JUICE_PACKING_ROUTING;
        double time = Routing.JUICE_PACKING_ROUTING_TIME;
        double juice1ea = Routing.JUICE_1EA_DURATION;
        return noCycle(input, ea, juice1ea, time);
    }

    public double stickPackingDuration(int input) {
        int ea = Routing.STICK_PACKING_ROUTING;
        double time = Routing.STICK_PACKING_ROUTING_TIME;
        double stick1ea = Routing.STICK_1EA_DURATION;
        return noCycle(input, ea, stick1ea, time);
    }

    public double freezeDuration() {
        return Routing.FREEZE_TIME;
    }
    //-------------------------------------------

    // 대기 시간 계산
    private double waitingCal(int input, int max, double waitingTime) {
        int quotient = input / max;
        double remain = input % max;
        if (remain > 0) {
            return waitingTime * quotient + waitingTime;
        }
        return waitingTime * quotient;

    }

    public double filterWaiting(int input) {
        int max = Routing.FILTER_ROUTING;
        double waitingTime = Routing.FILTER_WAITING_TIME;
        return waitingCal(input, max, waitingTime);
    }

    public double sterilizationWaiting(int input) {
        int max = Routing.STERILIZATION_ROUTING;
        double waitingTime = Routing.STERILIZATION_WAITING_TIME;
        return waitingCal(input, max, waitingTime);
    }

    public double inspectionWaiting(int input) {
        int max = Routing.INSPECTION_ROUTING;
        double waitingTime = Routing.INSPECTION_WAITING_TIME;
        return waitingCal(input, max, waitingTime);
    }

    public double boxPackingWaiting(int input) {
        int max = Routing.BOX_PACKING_ROUTING;
        double waitingTime = Routing.BOX_PACKING_WAITING_TIME;
        return waitingCal(input, max, waitingTime);
    }

    public double juicePackingWaiting(int input) {
        int max = Routing.JUICE_PACKING_ROUTING;
        double waitingTime = Routing.JUICE_WAITING_TIME;
        return waitingCal(input, max, waitingTime);
    }

    public double stickPackingWaiting(int input) {
        int max = Routing.STICK_PACKING_ROUTING;
        double waitingTime = Routing.STICK_WAITING_TIME;
        return waitingCal(input, max, waitingTime);
    }

    public double extractionWaiting(int input) {
        int max = Routing.EXTRACTION_ROUTING;
        double waitingTime = Routing.EXTRACTION_WAITING_TIME;
        return waitingCal(input, max, waitingTime);
    }


    public static void main(String[] args) {
    }


}
