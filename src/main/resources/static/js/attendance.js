let currentMode = "emp";

async function changeMode(mode) {
    currentMode = mode;

    document.querySelectorAll(".list-tabs button").forEach(button => {
        button.classList.toggle(
            "active",
            button.dataset.mode === mode
        );
    });

    const isEmp = mode === "emp";

    document.getElementById("employeeList").hidden = !isEmp;
    document.getElementById("attendanceList").hidden = isEmp;

    await loadSearchBox();

    if(isEmp){
        await loadEmpData();
    } else{
        await loadAttendanceData();
    }
}

async function loadSearchBox(){
    if(currentMode === "emp"){
        const response = await fetch(`/api/dept`);
        const deptData = await response.json();

        const deptOptions = deptData.map(dept => ({
           value: dept.name,
           label: dept.name
        }));

        createSearchBox([
            {name: "keyword", placeholder: "사원명 또는 사원코드 검색"},
            {name: "deptName", type: "select", options: deptOptions, placeholder: "부서 전체"}
        ], params => {
            loadEmpData(0, params);
        });
    } else {
        createSearchBox([
            {name: "keyword", placeholder: "사원명 또는 사원코드 검색"},
            {name: "year", placeholder: "연도"},
            {name: "month", placeholder: "월"}
        ], params => {
            loadAttendanceData(0, params);
        });
    }
}

// 사원 데이터
async function loadEmpData(page = 0, params = new URLSearchParams()){
    const searchParams = new URLSearchParams(params);
    searchParams.set("page", page);

    const empResponse = await fetch(`/api/emp?${searchParams.toString()}`);
    const pageData = await empResponse.json();

    renderTable(".emp-list", pageData.content, [
        {name: "name", label: "사원명"},
        {name: "code", label: "사원코드"},
        {name: "deptName", label: "부서명"},
        {
            type: "button",
            buttons: [
                {label: "일괄 등록", onClick: emp => addAttendances(emp)},
                {label: "등록", onClick: emp => addAttendance(emp)}
            ]
        }
    ]);

    document.getElementById("totalEmpCount").textContent =
        `총 ${pageData.totalElements}명`;

    createPagination(pageData, nextPage => loadEmpData(nextPage, params));
}

const statusOptions = [
    {value: "근무", label: "근무"},
    {value: "지각", label: "지각"},
    {value: "결근", label: "결근"},
    {value: "휴가", label: "휴가"},
    {value: "반차", label: "반차"}
]

// 근태 등록 (emp)
async function addAttendance(emp){
    openModal({
        title: "근태 등록",
        fields: [
            {name: "workDate", label: "근로일", type: "date", required: true},
            {
                name: "status",
                label: "근로 상태",
                type: "select",
                options: statusOptions,
                required: true
            },
            {name: "checkIn", label: "출근 시간", type: "time"},
            {name: "checkOut", label: "퇴근 시간", type: "time"}
        ],
        values: {
            workDate: new Date().toLocaleDateString("sv-SE"),
            status: "근무",
            checkIn: "09:00",
            checkOut: "18:00",
        },
        onSubmit: async data => {
            await saveAttendance({
                ...data,
                empCode: emp.code
            });
            await loadEmpData();
        }
    });
}

async function saveAttendance(data){
    const response = await fetch(`/api/attendance`, {
        method: "POST",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify(data)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error("근태 등록 실패")
    }

    alert(result.message);
}

// 근태 데이터
async function loadAttendanceData(page = 0, params = new URLSearchParams()) {
    const searchParams = new URLSearchParams(params);
    searchParams.set("page", page);

    const response = await fetch(`/api/attendance?${searchParams.toString()}`);
    const pageData = await response.json();

    const attendanceList = pageData.content.map(attendance => ({
        ...attendance,
        workTime: attendance.checkIn && attendance.checkOut
        ? `${attendance.checkIn.slice(0,5)} ~ ${attendance.checkOut.slice(0,5)}` : "-"
    }));

    renderTable(".attendance-list", attendanceList, [
        {name: "id", type: "checkbox", className: "attendance-checkbox"},
        {name: "workDate"},
        {name: "workTime"},
        {name: "workHour"},
        {
            type: "button",
            buttons: [
                {label: "수정", onClick : attendance => editAttendance(attendance)},
                {label: "삭제", onClick : attendance => delAttendance(attendance.id)}
            ]
        }]
    );

    document.getElementById("totalAttendanceCount").textContent =
        `총 ${pageData.totalElements}건`;

    createPagination(pageData, nextPage => loadAttendanceData(nextPage, params));
}

// 근태 수정 (attendance)
async function editAttendance(attendance){
    const response = await fetch(`/api/attendance/${attendance.id}`);
    const attendanceData = await response.json();
    openModal({
        title: "근태 수정",
        fields: [
            {name: "workDate", label: "근로일", type: "date"},
            {
                name: "status",
                label: "근로 상태",
                type: "select",
                options: statusOptions,
            },
            {name: "checkIn", label: "출근 시간", type: "time"},
            {name: "checkOut", label: "퇴근 시간", type: "time"}
        ],
        values: attendanceData,
        onSubmit: async data => {
            await updateAttendance(attendance.id, data);
        }
    })
}

async function updateAttendance(id, data){
    const res = await fetch(`/api/attendance/${id}`, {
        method: "PUT",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify(data)
    });
    const result = await res.json();

    if(!res.ok || result.result === "fail"){
        throw new Error("근태 수정 실패");
    }

    alert(result.message);
    await loadAttendanceData();
}

// 근태 삭제 (attendance)
async function delAttendance(id){
    if(!confirm("해당 근태를 삭제하시겠습니까?")) return;

    const response = await fetch(`/api/attendance/${id}`, {
        method: "DELETE"
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error("근태 삭제 실패");
    }

    alert(result.message);
    await loadAttendanceData();
}

// 근태 다중 삭제 (attendance)
async function delSelectedAttendance(){
    const ids = [...document.querySelectorAll(".attendance-checkbox:checked")]
        .map(checkbox => Number(checkbox.value));

    if(ids.length !== 0) {
        if (!confirm("선택된 근태를 삭제하시겠습니까?")) return;
    } else{
        alert("삭제할 근태를 선택해주세요");
        return;
    }

    const response = await fetch(`/api/attendances`, {
        method: "DELETE",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify(ids)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message);
    }

    alert(result.message);
    await loadAttendanceData();
}

document.addEventListener("DOMContentLoaded", async () => {
    document.getElementById("selectAll").addEventListener("change", function() {
        const checkboxes = document.querySelectorAll(".attendance-checkbox");

        checkboxes.forEach(checkbox => {
            checkbox.checked = this.checked;
        });
    });

    document.querySelector(".attendance-list").addEventListener("change",() => {
        const checkboxes = document.querySelectorAll(".attendance-checkbox");
        const selectAll = document.getElementById("selectAll");

        selectAll.checked = checkboxes.length > 0 &&
            [...checkboxes].every(checkbox => checkbox.checked);

    });

    await changeMode("emp");
});