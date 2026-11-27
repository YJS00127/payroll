let searchHourlyWageParams = new URLSearchParams()
let searchSalaryParams = new URLSearchParams()


// 시급 변경 탭
async function loadHourlyWageData(page=searchHourlyWageParams.get("page") ?? 0, params = searchHourlyWageParams){
    searchHourlyWageParams = new URLSearchParams(params);
    searchHourlyWageParams.set("page", page);

    const response = await fetch(`/api/emp?${searchHourlyWageParams.toString()}`);
    const hourlyWageData = await response.json();

    renderTable(".salary-edit-list", hourlyWageData.content, [
        {name: "name", label: "사원명"},
        {name: "code", label: "사원코드"},
        {name: "joiningDate", label: "입사일"},
        {name: "hourlyWage", label: "시급"},
        {
            type: "button",
            buttons: [{label: "수정", onClick: hourlyWage => editHourlyWage(hourlyWage)}]
        }
    ]);

    document.getElementById("totalEmpCount").textContent =
        `총 ${hourlyWageData.totalElements}명`

    createPagination(hourlyWageData, nextPage => loadHourlyWageData(nextPage));
}

async function editHourlyWage(hourlyWage){
    const response = await fetch(`/api/emp/${hourlyWage.id}`);
    const hourlyWageData = await response.json();

    openModal({
        title: "급여 변경",
        fields: [
            {name: "name", label: "사원명", disabled: true},
            {name: "empInfo", label: "사원정보", disabled: true},
            {name: "previousHourlyWage", label: "변경 전 시급", disabled: true},
            {name: "newHourlyWage", label: "변경 후 시급", type: "number"},
            {name: "effectiveDate", label: "변경일", type: "date"},
            {name: "reason", label: "변경 사유"}
        ], values: {
            ...hourlyWageData,
            previousHourlyWage: hourlyWageData.hourlyWage,
            empInfo: `${hourlyWageData.name} (${hourlyWageData.code})`
        }, onSubmit: async data => {
            await addSalaryHistory({
                ...data,
                empId: hourlyWage.id,
                previousHourlyWage: hourlyWageData.hourlyWage
            })
        }
    })
}

async function addSalaryHistory(data){
    const response = await fetch(`/api/salary`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 변경 실패")
    }
    alert(result.message)
    await loadHourlyWageData(searchHourlyWageParams);
}


// 변경 이력 탭
async function loadSalaryData(page=searchSalaryParams.get("page") ?? 0, params = searchSalaryParams){
    searchSalaryParams = new URLSearchParams(params);
    searchSalaryParams.set("page", page);

    const response = await fetch(`/api/salary?${searchSalaryParams.toString()}`);
    const salaryData = await response.json();

    renderTable(".salary-history-list", salaryData.content, [
        {name: "id", type: "checkbox", className: "salary-checkbox"},
        {name: "empName", label: "사원명"},
        {name: "empCode", label: "사원코드"},
        {name: "effectiveDate", label: "변경일"},
        {name: "previousHourlyWage", label: "변경 전 시급"},
        {name: "newHourlyWage", label: "변경 후 시급"},
        {name: "reason", label: "변경 사유"},
        {
            type: "button",
            buttons: [
                {label: "수정", onClick: salary => editSalary(salary)},
                {label: "삭제", onClick: salary => delSalaryHistory(salary.id)}
            ]
        }
    ]);

    document.getElementById("totalSalaryCount").textContent =
        `총 ${salaryData.totalElements}건`;

    createPagination(salaryData, nextPage => loadSalaryData(nextPage));
}

async function editSalary(salary){
    const response = await fetch(`/api/salary/${salary.id}`)
    const salaryData = await response.json();

    openModal({
        title: "급여 변경 이력 수정",
        fields: [
            {name: "empName", label: "사원명", disabled: true},
            {name: "empCode", label: "사원코드", disabled: true},
            {
                name: "previousHourlyWage",
                label: "변경 전 시급",
                disabled: true
            },
            {
                name: "newHourlyWage",
                label: "변경 후 시급",
                type: "number",
                min: 5000,
                max: 50000
            },
            {name: "effectiveDate", label: "변경일", type: "date"},
            {name: "reason", label: "변경 사유"}
        ], values: salaryData,
        onSubmit: async data => {
            await updateSalary(salary.id, data);
            await loadSalaryData(0, searchSalaryParams);
        }
    })
}

async function updateSalary(id, data){
    const response = await fetch(`/api/salary/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 변경 기록 수정 실패")
    }
    alert(result.message);
}

async function delSalaryHistory(id){
    const response = await fetch(`/api/salary`, {
        method: "DELETE"
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 변경 기록 삭제 실패")
    }

    alert(result.message)
    await loadSalaryData();
}

async function delSelectedSalary(){
    const ids = [...document.querySelectorAll(".salary-checkbox:checked")]
        .map(checkbox => Number(checkbox.value))

    const response = await fetch(`/api/salary`, {
        method: "DELETE",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(ids)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "급여 변경 기록 삭제 실패")
    }

    alert(result.message)
    await loadSalaryData();
}

document.addEventListener("DOMContentLoaded", () => {
    createTabs();

    createSearchBox([
        {name: "keyword", placeholder: "사원명 또는 사원코드로 검색"},
        {name: "hourlyWage", placeholder: "시급"}
    ], params => loadHourlyWageData(0, params), "salaryEditSearchBox");

    createSearchBox([
        {name: "keyword", placeholder: "사원명 또는 사원코드로 검색"},
        {name: "year", placeholder: "연도"},
        {name: "month", placeholder: "월"}
    ], params => loadSalaryData(0, params), "salaryHistorySearchBox");

    document.querySelectorAll(".tab-button").forEach(button => {
        button.addEventListener("click", () => {
            if (button.dataset.tab === "salary-edit") {
                loadHourlyWageData();
            } else {
                loadSalaryData();
            }
        });
    });
})