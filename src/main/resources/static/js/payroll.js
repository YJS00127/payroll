let searchParams = new URLSearchParams();

async function loadPayrollData(page=0, params = searchParams){
    searchParams = new URLSearchParams(params);
    searchParams.set("page", page);

    const response = await fetch(`/api/payroll?${searchParams.toString()}`);
    const pageData = await response.json();

    const payrollData = pageData.content.map(payroll => ({
        ...payroll,
        payrollYearMonth: `${payroll.year}.${payroll.month<10 ? `0${payroll.month}` : `${payroll.month}`}`,
    }))

    renderTable(".payroll-list", payrollData, [
        {name: "id", type: "checkbox", className: "payroll-checkbox"},
        {name: "empName"},
        {name: "empCode"},
        {name: "payrollYearMonth"},
        {name: "grossSalary"},
        {name: "totalDeduction"},
        {name: "netSalary"},
        {name: "paymentDate"},
        {
            type: "button",
            buttons: [
                {label: "수정", onClick: payroll => editPayroll(payroll)},
                {label: "삭제", onClick: payroll => delPayroll(payroll.id)}
            ]
        }
    ]);

    createPagination(pageData, nextPage => loadPayrollData(nextPage));
}

function addPayroll(){
    openModal({
        title: "급여 등록",
        fields: [
            {name: "empCode", label: "사원코드"},
            {name: "year", label: "연도", type: "number", newRow: true},
            {name: "month", label: "월", type: "number"},
            {name: "retroactivePay", label: "전월소급분", type: "number"},
            {name: "unpaidAllowance", label: "미지급수당", type: "number"},
            {name: "unpaidAllowanceReason", label: "미지급수당 사유", type: "number"},
            {name: "dailyMealAllowance", label: "식대", type: "number"},
            {name: "otherAllowance", label: "기타수당", type: "number"},
            {name: "retirementIncomeTax", label: "퇴직정산소득세", type: "number", newRow: true},
            {name: "retirementLocalIncomeTax", label: "소득세", type: "number"}
        ], onSubmit : async data => {
            await savePayroll(data);
            await loadPayrollData(0, searchParams);
        }
    });
}

async function savePayroll(data){
    const response = await fetch(`/api/payroll`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 등록 실패");
    }

    alert(result.message);
}

async function editPayroll(payroll){
    const response = await fetch(`/api/payroll/${payroll.id}`);
    const payrollData = await response.json();
    console.log(payrollData)

    openModal({
        title: "급여 수정",
        fields: [
            {name: "empInfo", label: "사원정보", disabled: true},
            {name: "year", label: "연도", type: "number", newRow: true},
            {name: "month", label: "월", type: "number"},
            {name: "retroactivePay", label: "전월소급분", type: "number"},
            {name: "unpaidAllowance", label: "미지급수당", type: "number"},
            {name: "unpaidAllowanceReason", label: "미지급수당 사유", type: "number"},
            {name: "dailyMealAllowance", label: "식대", type: "number"},
            {name: "otherAllowance", label: "기타수당", type: "number", newRow: true},
            {name: "retirementIncomeTax", label: "퇴직정산소득세", type: "number"},
            {name: "retirementLocalIncomeTax", label: "소득세", type: "number"}
        ], values: {
            ...payrollData,
            empInfo: `${payrollData.empName} (${payrollData.empCode})`
        }, onSubmit: async data => {
            await updatePayroll(payrollData.id, data)
            await loadPayrollData(searchParams);
        }
    })
}

async function updatePayroll(id, data){
    const response = await fetch(`/api/payroll/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 수정 실패")
    }

    alert(result.message);
}

async function delPayroll(id){
    if(!confirm("해당 급여 정보를 삭제하시겠습니까?")) return;

    const response = await fetch(`/api/payroll/${id}`, {
        method: "DELETE"
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 삭제 실패")
    }

    alert("급여 삭제 성공")
    await loadPayrollData(0, searchParams)
}

async function delSelectedPayroll(){
    const ids = [...document.querySelectorAll(".payroll-checkbox")]
        .map(checkbox => Number(checkbox.value));

    if(ids.length !== 0){
        if(!confirm("선택된 급여 정보를 삭제하시겠습니까?")) return;
    } else{
        throw new Error("삭제할 급여 정보를 선택해주세요");
    }

    const response = await fetch(`/api/payroll`, {
        method: "DELETE",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(ids)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 삭제 실패")
    }

    alert(result.message)
    await loadPayrollData(0, searchParams);
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("selectAll").addEventListener("change", function () {
        const checkboxes = document.querySelectorAll(".payroll-checkbox");

        checkboxes.forEach(checkboxes => {
            checkboxes.checked = this.checked;
        });
    })

    document.querySelector(".payroll-list").addEventListener("change", () => {
        const checkboxes = document.querySelectorAll(".payroll-checkbox");
        const selectAll = document.getElementById("selectAll");

        selectAll.checked = checkboxes.length > 0 &&
            [...checkboxes].every(checkbox => checkbox.checked);
    })

    createSearchBox([
        {name: "keyword", placeholder: "사원명 또는 사원코드 검색"},
        {name: "year", placeholder: "연도"},
        {name: "month", placeholder: "월"}
    ], params => {
        loadPayrollData(0, params)
    })
})