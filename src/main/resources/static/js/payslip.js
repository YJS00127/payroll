let searchParams = new URLSearchParams();

let deptOptions = [];
const empRankOptions = [
    {value: "부장", label: "부장"},
    {value: "차장", label: "차장"},
    {value: "과장", label: "과장"},
    {value: "대리", label: "대리"},
    {value: "사원", label: "사원"}
]

async function loadPayslipData(page = searchParams.get("page") ?? 0, params = searchParams){
    searchParams = new URLSearchParams(params);
    searchParams.set("page", page);

    const response = await fetch(`/api/payslip?${searchParams.toString()}`)
    const pageData = await response.json();

    const paySlipData = pageData.content.map(payslip => ({
        ...payslip,
        yearMonth: `${payslip.year}.${payslip.month}`,
        payrollStatus: `${payslip.paymentDate ? `지급 완료` : `${payslip.confirmedAt ? `확정` : `미확정`}`}`,
        paymentDate: `${payslip.paymentDate || "-"}`
    }))

    renderTable(".payslip-list", paySlipData, [
        {name: "id", type: "checkbox", className: "payslip-checkbox"},
        {name: "yearMonth"},
        {name: "empName"},
        {name: "empCode"},
        {name: "paymentDate"},
        {name: "payrollStatus"},
        {
            type: "button",
            buttons: [
                {label: "미리보기", onClick: payslip => previewPayslip(payslip.id)}
            ]
        }]
    )

    document.getElementById("totalCount").innerText = `총 ${pageData.totalElements}건`;

    createPagination(pageData, nextPage => loadPayslipData(nextPage));
}

// 급여 확정
async function confirmedPayroll(){
    const ids = [...document.querySelectorAll(".payslip-checkbox:checked")]
        .map(checkbox => Number(checkbox.value));

    if(ids.length === 0){
        throw new Error("선택된 급여내역이 없습니다")
    }

    const confirmedAt = new Date().toLocaleDateString("sv-SE");

    const requestData = {
        ids: ids,
        date: confirmedAt
    }

    const response = await fetch(`/api/payslip/confirm`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(requestData)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 확정 실패")
    }

    alert(result.message);
    await loadPayslipData()
}

// 급여 확정 취소
async function cancelConfirmed(){
    const ids = [...document.querySelectorAll(".payslip-checkbox:checked")]
        .map(checkbox => Number(checkbox.value));

    if(ids.length === 0){
        throw new Error("선택된 급여내역이 없습니다")
    }

    const response = await fetch(`/api/payslip/cancel`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(ids)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 확정 취소 실패")
    }

    alert(result.message);
    await loadPayslipData();
}

// 급여 지급 확정
function confirmedPayment(){
    const ids = [...document.querySelectorAll(".payslip-checkbox:checked")]
        .map(checkbox => Number(checkbox.value));

    if(ids.length === 0){
        throw new Error("선택된 급여내역이 없습니다")
    }

    openModal({
        title: "급여 지급 확정",
        fields: [
            {name: "paymentDate", label: "급여지급일", type: "date"}
        ], values: {
            paymentDate: new Date().toLocaleDateString("sv-SE")
        }, onSubmit: async data => {
            await updatePaymentDate(data, ids);
            await loadPayslipData();
    }}
    )
}

async function updatePaymentDate(data, ids){
    const requestData = {
        ids: ids,
        date: data.paymentDate
    }

    const response = await fetch(`/api/payslip/payment`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(requestData)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 지급 확정 실패")
    }

    alert(result.message);
}

function previewPayslip(id){
    window.open(`/payslip-pdf/${id}`, "_self");
}

document.addEventListener("DOMContentLoaded", async () => {
    const response = await fetch(`/api/dept`);
    const deptData = await response.json();

    deptOptions = deptData.map(dept => ({
        value: dept.name,
        label: dept.name
    }))

    document.getElementById("selectAll").addEventListener("change", function () {
        const checkboxes = document.querySelectorAll(".payslip-checkbox")

        checkboxes.forEach(checkbox => {
            checkbox.checked = this.checked;
        })
    })

    document.querySelector(".payslip-list").addEventListener("change", () => {
        const checkboxes = document.querySelectorAll(".payslip-checkbox");
        const selectAll = document.getElementById("selectAll")

        selectAll.checked = checkboxes.length > 0 &&
            [...checkboxes].every(checkbox => checkbox.checked);
    });


    createSearchBox([
        {name: "keyword", placeholder: "사원명 또는 사원코드 검색"},
        {
            name: "deptName",
            type: "select",
            placeholder: "부서 전체",
            options: deptOptions
        },
        {
            name: "empRank",
            type: "select",
            placeholder: "직급 전체",
            options: empRankOptions
        },
        {name: "year", placeholder: "연도"},
        {name: "month", placeholder: "월"}
    ], params => {
        loadPayslipData(0, params)
    })
})


