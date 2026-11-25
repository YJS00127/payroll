async function loadPayslipData(id){
    const response = await fetch(`/api/payslip/${id}`)
    const data = await response.json();

    const payrollData = data.payroll;
    console.log(data)

    document.getElementById("yearMonth").innerText =
        `${payrollData.year}년 ${payrollData.month}월 급여명세서`

    document.getElementById("empNo").innerText =
        `${data.empId}`

    const birth = data.empBirth;

    // info-title (기본정보)
    const basicInfo = {
        payslipYearMonth: `${payrollData.year}년 ${payrollData.month<10 ? `0${payrollData.month}월` : `${payrollData.month}월`}`,
        paymentDay: payrollData.paymentDate ? `${payrollData.paymentDate.slice(8, 10)}일` : "",
        empName: `${payrollData.empName}(${data.deptName})`,
        empBirth: `${birth.slice(2).replaceAll("-", "")}-${getNumByGender(data.empBirth, data.empGender)}******`,
        deptName: data.deptName,
        empRank: data.empRank
    }

    Object.entries(basicInfo).forEach(([id, value]) => {
        document.getElementById(id).textContent = value;
    })

    // work-info (근로정보)
    const workData = {
        workDays: data.workDays || "",
        weeklyHolidayDays: data.weeklyHolidayDays || "",
        totalWorkHour: data.totalWorkHour || "",
        deductionHour: data.deductionHour || "",
        weeklyHolidayHour: data.weeklyHolidayHour || "",
    }

    renderTable(".work-info-list", [workData], [
        {name: "workDays"},
        {name: "weeklyHolidayDays"},
        {name: "totalWorkHour"},
        {name: "deductionHour"},
        {name: "weeklyHolidayHour"}
    ])

    // payment-list (지급내역)
    const paymentData = [
        createPayment("기본급", payrollData.basicSalary, "총 근로시간", "시급"),
        createPayment("주차수당", payrollData.weeklyHolidayPay, "주차시간", "시급"),
        createPayment("전월소급분", payrollData.retroactivePay),
        createPayment("공제금", payrollData.deductionAmount),
        createPayment("월차수당", payrollData.monthlyLeavePay, "미사용월차", "일급"),
        createPayment("식대", payrollData.daliyMealAllowance, "일일 식대", "근로일수"),
        createPayment("연장근로수당", payrollData.overtimePay, "연장근로시간", "시급", "1.5"),
        createPayment("미지급수당", payrollData.unpaidAllowance, payrollData.unpaidAllowanceReason),
        createPayment("기타수당", payrollData.otherAllowance),
        createPayment("휴일근로수당", payrollData.holidayWorkPay),
        createPayment("지급합계", payrollData.grossSalary)
    ]

    renderTable(".payment-list", paymentData, [
        {name: "name"},
        {name: "payment"},
        {name: "leftDes"},
        {name: "operator"},
        {name: "rightDes"},
        {name: "rate"}
    ]);



    // deduction-list (공제내역)
    const deductionData = [
        createDeduction("국민연금", payrollData.nationalPension,
            data.pensionBase===0 ? "계약직평균급여" : "기준소득월액",
            data.nationalPensionRate),
        createDeduction("급여합계", payrollData.healthInsurance,
            data.pensionBase===0 ? "급여합계" : "보수총액",
            data.healthInsuranceRate),
        createDeduction("노인장기요양", payrollData.longTermCareInsurance, "건강보험공제금", data.longTermCareInsuranceRate),
        createDeduction("고용보험", payrollData.employmentInsurance, "급여합계", data.employmentInsuranceRate),
        createDeduction("건강보험정산", payrollData.healthInsuranceAdjustment,
            data.isRetirement ? "퇴직정산" : ""),
        // 퇴직으로 인한 건강보험정산 발생 시 "퇴직정산" 출력
        createDeduction("요양보험정산", payrollData.longTermCareAdjustment,
            data.isRetirement ? "퇴직정산" : ""),
        createDeduction("퇴직정산소득세", payrollData.retirementIncomeTax),
        createDeduction("퇴직정산주민세", payrollData.retirementLocalIncomeTax, "퇴직정산소득세공제금", data.localIncomeTaxRate),
        createDeduction("소득세(사업소세)", payrollData.incomeTax),
        createDeduction("주민세(사업소세)", payrollData.localIncomeTax, "소득세공제금", data.localIncomeTaxRate),
        createDeduction("공제합계", payrollData.totalDeduction)
    ]

    renderTable(".deduction-list", deductionData, [
        {name: "name"},
        {name: "deduction"},
        {name: "leftDes"},
        {name: "operator"},
        {name: "rate"}
    ])

    document.getElementById("netSalary").innerText = payrollData.netSalary.toLocaleString("ko-KR");

    document.getElementById("paymentDate").innerText =
        `${payrollData.paymentDate.split("-")[0]}년 ${payrollData.paymentDate.split("-")[1]}월 ${payrollData.paymentDate.split("-")[2]}일`

}

function getNumByGender(birth, gender){
    if(birth.Year < 2000){
        if(gender === "남"){
            return "1"
        } else{
            return "2"
        }
    } else{
        if(gender === "남"){
            return "3"
        } else{
            return "4"
        }
    }
}

function createPayment(name, payment, leftDes, rightDes, rate){
    const checkReason = payment && name !== "미지급수당";
    const checkOvertimeOrHoliday = payment && (name === "연장근로수당" || name === "휴일근로수당")

    return {
        name,
        payment: payment ? payment.toLocaleString("ko-KR") : "",
        leftDes: payment ? leftDes : "",
        operator: checkReason && leftDes? "x" : "",
        rightDes: checkReason ? rightDes : "",
        rate: checkReason ? rate : "",
        operators: checkOvertimeOrHoliday ? "x" : "",
        rate2: checkOvertimeOrHoliday ? (name==="연장근로수당" ? "1.5" : "2") : ""
    };
}

function createDeduction(name, deduction, leftDes, rate) {
    return {
        name,
        deduction: deduction ? deduction.toLocaleString("ko-KR") : "",
        leftDes: deduction ? leftDes : "",
        operator: deduction && rate != null ? "x" : "",
        rate: deduction && rate != null ? `${rate}%` : ""
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadPayslipData(22);
})