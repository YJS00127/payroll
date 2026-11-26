let searchUsageParams = new URLSearchParams();
let searchRecordParams = new URLSearchParams();

async function loadLeaveUsageData(page= searchUsageParams.get("page") ?? 0, params = searchUsageParams){
    searchUsageParams = new URLSearchParams(params);
    searchUsageParams.set("page", page);

    const response = await fetch(`/api/leave-usage?${searchUsageParams.toString()}`)
    const data = await response.json();

    const leaveUsageData = data.content.map(leaveUsage => ({
        ...leaveUsage,
        leaveDate: `${leaveUsage.startDate} ~ ${leaveUsage.endDate}`
    }))

    renderTable(".leave-usage-list", leaveUsageData, [
        {name: "id", type: "checkbox", className: "leave-usage-checkbox"},
        {name: "empName", label: "사원명"},
        {name: "empCode", label: "사원코드"},
        {name: "leaveDate", label: "휴가 날짜"},
        {name: "usedDays", label: "사용 휴가일수"},
        {name: "leaveType", label: "휴가 종류"},
        {
            type: "button",
            buttons:[
                {label: "수정", onClick: leaveUsage => editLeaveUsage(leaveUsage)},
                {label: "삭제", onClick: leaveUsage => delLeaveUsage(leaveUsage.id)}
            ]
        }]
    );

    createPagination(data, nextPage => loadLeaveUsageData(nextPage));
}

async function addLeaveUsage(){
    openModal({
        title: "휴가 이력 등록",
        fields: [
            {name: "empCode", label: "사원코드"},
            {
                name: "leaveType",
                label: "휴가 종류",
                type: "select",
                options: [
                    {value: "휴가", label: "휴가"},
                    {value: "반차", label: "반차"},
                    {value: "반반차", label: "반반차"}
                ]
            },
            {name: "startDate", label: "휴가 시작일", type: "date"},
            {name: "endDate", label: "휴가 종료일", type: "date"}
        ],
        onSubmit: async data => {
            if(data.leaveType !== "휴가") {
                data.endDate = data.startDate;
            }
            await saveLeaveUsage(data)
            await loadLeaveUsageData(searchUsageParams)
        }
    });

    const leaveType = document.querySelector('[name="leaveType"]');
    const endDate = document.querySelector('[name="endDate"]');

    function updateDateField() {
        const isFullLeave = leaveType.value === "휴가";

        endDate.closest(".modal-field").style.display =
            isFullLeave ? "" : "none";

        endDate.disabled = !isFullLeave;
    }

    leaveType.addEventListener("change", updateDateField);
    updateDateField();
}

async function saveLeaveUsage(data){
    const response = await fetch(`/api/leave-usage`,{
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
    const result = await response.json();
    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "휴가 등록 실패");
    }
    alert(result.message);
}

async function editLeaveUsage(leaveUsage){
    const response = await fetch(`/api/leave-usage/${leaveUsage.id}`);
    const leaveUsageData = await response.json();

    openModal({
        title: "휴가 이력 수정",
        fields: [
            {name: "empInfo", label: "사원 정보", disabled: true},
            {
                name: "leaveType",
                label: "휴가 종류",
                type: "select",
                options: [
                    {label: "휴가", value: "휴가"},
                    {label: "반차", value: "반차"},
                    {label: "반반차", value: "반반차"}
                ]
            },
            {name: "startDate", label: "휴가 시작일", type: "date"},
            {name: "endDate", label: "휴가 종료일", type: "date"}
        ], values: {
            ...leaveUsageData,
            empInfo: `${leaveUsageData.empName} (${leaveUsageData.empCode})`
        },
        onSubmit: async data => {
            await updateLeaveUsage(leaveUsageData.id, data)
            await loadLeaveUsageData(searchUsageParams);
        }
    })
}

async function updateLeaveUsage(id, data){
    const response = await fetch(`/api/leave-usage/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "휴가 수정 실패")
    }

    alert(result.message);
}

async function delLeaveUsage(id){
    if(!confirm("해당 휴가를 삭제하시겠습니까?")) return;

    const response = await fetch(`/api/leave-usage/${id}`, {
        method: "DELETE"
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "휴가 삭제 실패")
    }

    alert(result.message);
    await loadLeaveUsageData(searchUsageParams);
}

async function delSelectedLeaveUsage(){
    if(!confirm("선택된 휴가를 삭제하시겠습니까?")) return;

    const ids = [...document.querySelectorAll(".leave-usage-checkbox:checked")]
        .map(checkbox => Number(checkbox.value));

    if(ids.length === 0){
        alert("삭제할 휴가를 선택해주세요")
        return
    }

    const response = await fetch(`/api/delete`, {
        method: "DELETE",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(ids)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "휴가 수정 실패")
    }

    alert(result.message);
}

async function loadLeaveRecordData(page=searchRecordParams.get("page") ?? 0, params= searchRecordParams){
    searchRecordParams = new URLSearchParams(params);
    searchRecordParams.set("page", page);

    console.log(searchUsageParams)

    const response = await fetch(`/api/leave-record?${searchRecordParams.toString()}`)
    const pageData = await response.json();

    const leaveRecordData = pageData.content.map(leaveRecord => ({
       ...leaveRecord,
       yearMonth: `${leaveRecord.year}.${leaveRecord.month<10 ? `0${leaveRecord.month}` : `${leaveRecord.month}`}`
    }))

    renderTable(".leave-record-list", leaveRecordData, [
        {name: "id", type: "checkbox", className: "leave-record-checkbox"},
        {name: "empName", label: "사원명"},
        {name: "empCode", label: "사원코드"},
        {name: "yearMonth", label: "기준연월"},
        {name: "totalLeave", label: "총 휴가"},
        {name: "accruedLeave", label: "발생 휴가"},
        {name: "usedLeave", label: "사용 휴가"},
        {name: "remainingLeave", label: "잔여 휴가"},
        {
            type: "button",
            buttons: [
                {label: "수정", onClick: leaveRecord => editLeaveRecord(leaveRecord)},
                {label: "삭제", onClick: leaveRecord => delLeaveRecord(leaveRecord.id)}
            ]
        }]
    );

    createPagination(pageData, nextPage => loadLeaveRecordData(nextPage))
}

async function addLeaveRecord(){
    openModal({
        title: "휴가 정보 등록",
        fields: [
            {name: "empCode", label: "사원코드"},
            {name: "accruedLeave", label: "발생 휴가", type: "number"},
            {name: "year", label: "연도", type: "number"},
            {name: "month", label: "월", type: "number"}
        ],
        onSubmit: async data => {
            await saveLeaveRecord(data)
            await loadLeaveRecordData()
        }
    })
}

async function saveLeaveRecord(data){
    const response = await fetch(`/api/leave-record`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "휴가 정보 등록 실패");
    }

    alert(result.message);
}

async function editLeaveRecord(leaveRecord){
    const response = await fetch(`/api/leave-record/${leaveRecord.id}`)
    const recordData = await response.json();

    openModal({
        title: "휴가 정보 수정",
        fields: [
            {name: "empInfo", label: "사원 정보", disabled: true},
            {name: "totalLeave", label: "총 휴가", type: "number"},
            {name: "accruedLeave", label: "발생 휴가", type: "number"},
            {name: "usedLeave", label: "사용 휴가", type: "number"},
            {name: "year", label: "연도", type: "number"},
            {name: "month", label: "월", type: "number"}
        ], values: {
            ...recordData,
            empInfo: `${recordData.empName} (${recordData.empCode})`
        }, onSubmit: async data => {
            await updateLeaveRecord(leaveRecord.id, {
                ...data,
                empCode: recordData.empCode
            })
            await loadLeaveRecordData()
        }
    })
}

async function updateLeaveRecord(id, data){
    const response = await fetch(`/api/leave-record/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "휴가 정보 수정 실패");
    }

    alert(result.message);
}

async function delLeaveRecord(id){
    if(!confirm("해당 휴가 정보를 삭제하시겠습니까?")) return;

    const response = await fetch(`/api/leave-record/${id}`, {
        method: "DELETE"
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "휴가 정보 삭제 실패");
    }

    alert(result.message);
    await loadLeaveRecordData();
}

async function delSelectedLeaveRecord(){
    if(!confirm("선택된 휴가 정보를 삭제하시겠습니까?")) return;

    const ids = [...document.querySelectorAll(".leave-record-checkbox:checked")]
        .map(checkbox => Number(checkbox.value))

    if(ids.length === 0){
        alert("삭제할 휴가 정보를 선택하세요");
        return
    }

    const response = await fetch(`/api/leave-record`, {
        method: "DELETE",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(ids)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "휴가 정보 삭제 실패");
    }

    alert(result.message);
    await loadLeaveRecordData();
}

function setCheckbox(tabId, checkboxClass){
    const tab = document.getElementById(tabId);
    const selectAll = tab.querySelector(".selectAll");

    selectAll.addEventListener("change", () => {
        tab.querySelectorAll(`.${checkboxClass}`).forEach(checkbox => {
            checkbox.checked = selectAll.checked;
        })
    })

    tab.addEventListener("change", e => {
        if(!e.target.classList.contains(checkboxClass)) return;

        const checkboxes = tab.querySelectorAll(`.${checkboxClass}`);

        selectAll.checked = checkboxes.length > 0 &&
            [...checkboxes].every(checkbox => checkbox.checked);
    })
}

document.addEventListener("DOMContentLoaded", () => {
    createTabs();

    setCheckbox("leave-usage", "leave-usage-checkbox");
    setCheckbox("leave-record", "leave-record-checkbox");

    createSearchBox([
        {name: "keyword", placeholder: "사원명 또는 사원코드로 검색"},
        {name: "leaveDate", type: "date", placeholder: "휴가일"}
    ], params => loadLeaveUsageData(0, params), "leaveUsageSearchBox")

    createSearchBox([
        {name: "keyword", placeholder: "사원명 또는 사원코드로 검색"},
        {name: "year", placeholder: "연도"},
        {name: "month", placeholder: "월"}
    ], params => loadLeaveRecordData(0, params), "leaveRecordSearchBox")

    document.querySelectorAll(".tab-button").forEach(button => {
        button.addEventListener("click", () => {
            if(button.dataset.tab === "leave-usage"){
                loadLeaveUsageData();
            } else{
                loadLeaveRecordData();
            }
        });
    });
})